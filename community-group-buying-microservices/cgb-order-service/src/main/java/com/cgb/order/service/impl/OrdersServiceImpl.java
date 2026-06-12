package com.cgb.order.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.cgb.common.EIException;
import com.cgb.common.ErrorCode;
import com.cgb.common.feign.FeignProductService;
import com.cgb.common.feign.FeignUserService;
import com.cgb.common.mq.MQTopics;
import com.cgb.common.mq.OrderStatusMessage;
import com.cgb.common.utils.*;
import com.cgb.order.dao.OrdersDao;
import com.cgb.order.entity.OrdersEntity;
import com.cgb.order.entity.dto.CreateOrderDTO;
import com.cgb.order.entity.vo.OrderVO;
import com.cgb.order.service.OrdersService;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrdersServiceImpl implements OrdersService {

    private final OrdersDao ordersDao;
    private final FeignProductService feignProductService;
    private final FeignUserService feignUserService;
    private final RocketMQTemplate rocketMQTemplate;
    private final RedisTemplate<String, Object> redisTemplate;

    private static final String ORDER_CACHE_PREFIX = "cgb:order:";

    @Override
    public void save(OrdersEntity entity) {
        if (entity.getOrderNo() == null || "".equals(entity.getOrderNo())) {
            entity.setOrderNo(CommonUtil.generateOrderId());
        }
        if (entity.getStatus() == null) entity.setStatus(0);
        ordersDao.insert(entity);
    }

    @Override
    public void update(OrdersEntity entity) {
        if (entity.getId() == null) throw new EIException("订单ID不能为空");
        ordersDao.updateById(entity);
        evictOrderCache(entity.getId());
    }

    @Override
    public void delete(Long id) {
        ordersDao.deleteById(id);
        evictOrderCache(id);
    }

    @Override
    public OrdersEntity getById(Long id) {
        OrdersEntity entity = ordersDao.selectById(id);
        if (entity == null) throw new EIException(ErrorCode.RESOURCE_NOT_FOUND);
        return entity;
    }

    @Override
    public OrdersEntity getByOrderId(String orderId) {
        OrdersEntity entity = ordersDao.selectOne(new LambdaQueryWrapper<OrdersEntity>()
                .eq(OrdersEntity::getOrderNo, orderId));
        if (entity == null) throw new EIException(ErrorCode.RESOURCE_NOT_FOUND);
        return entity;
    }

    @Override
    public IPage<OrdersEntity> queryPage(OrdersEntity params) {
        IPage<OrdersEntity> page = new Query<OrdersEntity>().getPage(
                CommonUtil.convert(params, Map.class));
        LambdaQueryWrapper<OrdersEntity> wrapper = new LambdaQueryWrapper<>();
        if (params.getUserId() != null) wrapper.eq(OrdersEntity::getUserId, params.getUserId());
        if (params.getStatus() != null) wrapper.eq(OrdersEntity::getStatus, params.getStatus());
        if (CommonUtil.isNotEmpty(params.getOrderNo())) wrapper.like(OrdersEntity::getOrderNo, params.getOrderNo());
        wrapper.orderByDesc(OrdersEntity::getId);
        return ordersDao.selectPage(page, wrapper);
    }

    @Override
    @GlobalTransactional(name = "cgb-cancel-order", rollbackFor = Exception.class)
    public void cancel(String orderId, Long userId) {
        OrdersEntity order = ordersDao.selectOne(new LambdaQueryWrapper<OrdersEntity>()
                .eq(OrdersEntity::getOrderNo, orderId)
                .eq(OrdersEntity::getUserId, userId));
        if (order == null) throw new EIException(ErrorCode.RESOURCE_NOT_FOUND);
        if (order.getStatus() != 0) throw new EIException("只能取消待支付订�?);
        order.setStatus(2);
        ordersDao.updateById(order);

        // 取消订单 �?回补库存
        try {
            feignProductService.increaseStock(order.getProductId(), order.getQuantity());
            log.info("取消订单，库存回补成�? productId={}, quantity={}", order.getProductId(), order.getQuantity());
        } catch (Exception e) {
            log.error("取消订单库存回补失败，需人工补偿: productId={}", order.getProductId(), e);
        }

        evictOrderCache(order.getId());

        // 发送订单取消消�?        sendOrderStatusMessage(order, MQTopics.TAG_ORDER_CANCELLED);
    }

    @Override
    public void pay(String orderId) {
        OrdersEntity order = ordersDao.selectOne(new LambdaQueryWrapper<OrdersEntity>()
                .eq(OrdersEntity::getOrderNo, orderId));
        if (order == null) throw new EIException(ErrorCode.RESOURCE_NOT_FOUND);
        if (order.getStatus() != 0) throw new EIException("订单状态不允许支付");
        order.setStatus(1);
        ordersDao.updateById(order);

        evictOrderCache(order.getId());

        // 支付成功 �?�?RocketMQ 消息（积分由 MQ 消费者异步增加）
        sendOrderStatusMessage(order, MQTopics.TAG_ORDER_PAID);
    }

    /**
     * 发货（管理员操作�?     */
    @Override
    public void ship(String orderId) {
        OrdersEntity order = ordersDao.selectOne(new LambdaQueryWrapper<OrdersEntity>()
                .eq(OrdersEntity::getOrderNo, orderId));
        if (order == null) throw new EIException(ErrorCode.RESOURCE_NOT_FOUND);
        if (order.getStatus() != 1) throw new EIException("只能对已支付订单发货");
        order.setStatus(3);
        ordersDao.updateById(order);
        evictOrderCache(order.getId());
        log.info("订单发货成功: orderId={}", orderId);
    }

    /**
     * 确认收货（用户操作）
     */
    @Override
    public void confirmReceive(String orderId, Long userId) {
        OrdersEntity order = ordersDao.selectOne(new LambdaQueryWrapper<OrdersEntity>()
                .eq(OrdersEntity::getOrderNo, orderId)
                .eq(OrdersEntity::getUserId, userId));
        if (order == null) throw new EIException(ErrorCode.RESOURCE_NOT_FOUND);
        if (order.getStatus() != 3) throw new EIException("只能确认已发货的订单");
        order.setStatus(4);
        ordersDao.updateById(order);
        evictOrderCache(order.getId());
        log.info("确认收货成功: orderId={}, userId={}", orderId, userId);
    }

    /**
     * 创建订单（Seata 分布式事务）
     * TM 端：下单 + 远程调商品服务扣库存，任一失败全局回滚
     */
    @Override
    @GlobalTransactional(name = "cgb-create-order", rollbackFor = Exception.class)
    public void createOrder(OrdersEntity entity) {
        // 1. 生成订单编号
        if (entity.getOrderNo() == null || "".equals(entity.getOrderNo())) {
            entity.setOrderNo(CommonUtil.generateOrderId());
        }
        if (entity.getStatus() == null) entity.setStatus(0);

        // 2. 远程调用商品服务扣减库存（RM 端）
        log.info("分布式事务开�?�?扣减库存: productId={}, quantity={}", entity.getProductId(), entity.getQuantity());
        var stockResult = feignProductService.decreaseStock(entity.getProductId(), entity.getQuantity());
        if (stockResult.getCode() != 0) {
            throw new EIException("库存扣减失败: " + stockResult.getMsg());
        }

        // 3. 计算总价
        if (entity.getTotalPrice() == null && entity.getUnitPrice() != null && entity.getQuantity() != null) {
            entity.setTotalPrice(entity.getUnitPrice().multiply(BigDecimal.valueOf(entity.getQuantity())));
        }

        // 4. 保存订单
        ordersDao.insert(entity);
        log.info("分布式事务完�?�?订单创建成功: orderId={}", entity.getOrderNo());

        // 5. 发送订单创建消息（RocketMQ�?        sendOrderStatusMessage(entity, MQTopics.TAG_ORDER_CREATED);
    }

    /**
     * 发送订单状态变更消息到 RocketMQ
     */
    private void sendOrderStatusMessage(OrdersEntity order, String tag) {
        try {
            OrderStatusMessage msg = new OrderStatusMessage();
            msg.setOrderId(order.getOrderNo());
            msg.setUserId(order.getUserId());
            msg.setProductId(order.getProductId());
            msg.setQuantity(order.getQuantity());
            msg.setTotalPrice(order.getTotalPrice());
            msg.setStatus(order.getStatus());

            String destination = MQTopics.ORDER_STATUS_CHANGE + ":" + tag;
            rocketMQTemplate.syncSend(destination, MessageBuilder.withPayload(msg).build());
            log.info("订单状态消息发送成�? orderId={}, tag={}", order.getOrderNo(), tag);
        } catch (Exception e) {
            log.error("订单状态消息发送失�? orderId={}, tag={}", order.getOrderNo(), tag, e);
        }
    }

    @Override
    public OrderVO createOrderFromDTO(CreateOrderDTO dto, Long userId) {
        // 远程获取商品信息填充订单
        Object productData = feignProductService.getProductDetail(dto.getProductId()).getData();
        @SuppressWarnings("unchecked")
        Map<String, Object> productMap = productData instanceof Map ? (Map<String, Object>) productData : new HashMap<>();

        OrdersEntity entity = new OrdersEntity();
        entity.setUserId(userId);
        entity.setProductId(dto.getProductId());
        entity.setProductName(productMap.get("productName") != null ? productMap.get("productName").toString() : "");
        entity.setProductImage(productMap.get("picture") != null ? productMap.get("picture").toString() : "");
        entity.setQuantity(dto.getQuantity());
        entity.setUnitPrice(dto.getQuantity() != null && productMap.get("price") != null
                ? new BigDecimal(productMap.get("price").toString()) : null);
        entity.setContactPhone(dto.getContactPhone());
        entity.setShippingAddress(dto.getShippingAddress());
        entity.setPaymentMethod(dto.getPaymentMethod());
        entity.setRemark(dto.getRemark());
        entity.setGroupBuyId(dto.getGroupBuyId());

        createOrder(entity);
        return toVO(entity);
    }

    @Override
    public OrderVO toVO(OrdersEntity e) {
        if (e == null) return null;
        OrderVO vo = new OrderVO();
        vo.setId(e.getId());
        vo.setOrderId(e.getOrderNo());
        vo.setUserId(e.getUserId());
        vo.setProductId(e.getProductId());
        vo.setProductName(e.getProductName());
        vo.setProductImage(e.getProductImage());
        vo.setQuantity(e.getQuantity());
        vo.setUnitPrice(e.getUnitPrice());
        vo.setTotalPrice(e.getTotalPrice());
        vo.setContactPhone(e.getContactPhone());
        vo.setShippingAddress(e.getShippingAddress());
        vo.setStatus(e.getStatus());
        vo.setPaymentMethod(e.getPaymentMethod());
        vo.setRemark(e.getRemark());
        vo.setGroupBuyId(e.getGroupBuyId());
        vo.setCreateTime(e.getAddTime());
        vo.setUpdateTime(e.getUpdateTime());
        return vo;
    }

    private void evictOrderCache(Long id) {
        try {
            redisTemplate.delete(ORDER_CACHE_PREFIX + id);
        } catch (Exception e) {
            log.warn("订单缓存清除失败: id={}", id, e);
        }
    }
}
