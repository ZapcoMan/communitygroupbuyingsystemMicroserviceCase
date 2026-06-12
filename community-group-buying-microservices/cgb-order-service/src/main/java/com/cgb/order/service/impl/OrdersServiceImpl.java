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
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrdersServiceImpl implements OrdersService {

    private final OrdersDao ordersDao;
    private final FeignProductService feignProductService;
    private final FeignUserService feignUserService;
    private final RocketMQTemplate rocketMQTemplate;

    @Override
    public void save(OrdersEntity entity) {
        if (entity.getOrderid() == null || "".equals(entity.getOrderid())) {
            entity.setOrderid(CommonUtil.generateOrderId());
        }
        if (entity.getZhuangtai() == null) entity.setZhuangtai(0);
        ordersDao.insert(entity);
    }

    @Override
    public void update(OrdersEntity entity) {
        if (entity.getId() == null) throw new EIException("订单ID不能为空");
        ordersDao.updateById(entity);
    }

    @Override
    public void delete(Long id) { ordersDao.deleteById(id); }

    @Override
    public OrdersEntity getById(Long id) {
        OrdersEntity entity = ordersDao.selectById(id);
        if (entity == null) throw new EIException(ErrorCode.RESOURCE_NOT_FOUND);
        return entity;
    }

    @Override
    public OrdersEntity getByOrderId(String orderId) {
        OrdersEntity entity = ordersDao.selectOne(new LambdaQueryWrapper<OrdersEntity>()
                .eq(OrdersEntity::getOrderid, orderId));
        if (entity == null) throw new EIException(ErrorCode.RESOURCE_NOT_FOUND);
        return entity;
    }

    @Override
    public IPage<OrdersEntity> queryPage(OrdersEntity params) {
        IPage<OrdersEntity> page = new Query<OrdersEntity>().getPage(
                CommonUtil.convert(params, Map.class));
        LambdaQueryWrapper<OrdersEntity> wrapper = new LambdaQueryWrapper<>();
        if (params.getUserid() != null) wrapper.eq(OrdersEntity::getUserid, params.getUserid());
        if (params.getZhuangtai() != null) wrapper.eq(OrdersEntity::getZhuangtai, params.getZhuangtai());
        if (CommonUtil.isNotEmpty(params.getOrderid())) wrapper.like(OrdersEntity::getOrderid, params.getOrderid());
        wrapper.orderByDesc(OrdersEntity::getId);
        return ordersDao.selectPage(page, wrapper);
    }

    @Override
    @GlobalTransactional(name = "cgb-cancel-order", rollbackFor = Exception.class)
    public void cancel(String orderId, Long userId) {
        OrdersEntity order = ordersDao.selectOne(new LambdaQueryWrapper<OrdersEntity>()
                .eq(OrdersEntity::getOrderid, orderId)
                .eq(OrdersEntity::getUserid, userId));
        if (order == null) throw new EIException(ErrorCode.RESOURCE_NOT_FOUND);
        if (order.getZhuangtai() != 0) throw new EIException("只能取消待支付订单");
        order.setZhuangtai(2);
        ordersDao.updateById(order);

        // 取消订单 → 回补库存 + 发 RocketMQ 消息
        try {
            feignProductService.increaseStock(order.getShangpinid(), order.getShuliang());
            log.info("取消订单，库存回补成功: productId={}, quantity={}", order.getShangpinid(), order.getShuliang());
        } catch (Exception e) {
            log.error("取消订单库存回补失败，需人工补偿: productId={}", order.getShangpinid(), e);
        }

        // 发送订单取消消息
        sendOrderStatusMessage(order, MQTopics.TAG_ORDER_CANCELLED);
    }

    @Override
    public void pay(String orderId) {
        OrdersEntity order = ordersDao.selectOne(new LambdaQueryWrapper<OrdersEntity>()
                .eq(OrdersEntity::getOrderid, orderId));
        if (order == null) throw new EIException(ErrorCode.RESOURCE_NOT_FOUND);
        if (order.getZhuangtai() != 0) throw new EIException("订单状态不允许支付");
        order.setZhuangtai(1);
        ordersDao.updateById(order);

        // 支付成功 → 发 RocketMQ 消息（积分由 MQ 消费者异步增加，避免重复）
        sendOrderStatusMessage(order, MQTopics.TAG_ORDER_PAID);
    }

    /**
     * 创建订单（Seata 分布式事务）
     * TM 端：下单 + 远程调商品服务扣库存，任一失败全局回滚
     */
    @Override
    @GlobalTransactional(name = "cgb-create-order", rollbackFor = Exception.class)
    public void createOrder(OrdersEntity entity) {
        // 1. 生成订单编号
        if (entity.getOrderid() == null || "".equals(entity.getOrderid())) {
            entity.setOrderid(CommonUtil.generateOrderId());
        }
        if (entity.getZhuangtai() == null) entity.setZhuangtai(0);

        // 2. 远程调用商品服务扣减库存（RM 端）
        log.info("分布式事务开始 → 扣减库存: productId={}, quantity={}", entity.getShangpinid(), entity.getShuliang());
        var stockResult = feignProductService.decreaseStock(entity.getShangpinid(), entity.getShuliang());
        if (stockResult.getCode() != 0) {
            throw new EIException("库存扣减失败: " + stockResult.getMsg());
        }

        // 3. 计算总价
        if (entity.getZongjia() == null && entity.getJiage() != null && entity.getShuliang() != null) {
            entity.setZongjia(entity.getJiage().multiply(BigDecimal.valueOf(entity.getShuliang())));
        }

        // 4. 保存订单
        ordersDao.insert(entity);
        log.info("分布式事务完成 → 订单创建成功: orderId={}", entity.getOrderid());

        // 5. 发送订单创建消息（RocketMQ）
        sendOrderStatusMessage(entity, MQTopics.TAG_ORDER_CREATED);
    }

    /**
     * 发送订单状态变更消息到 RocketMQ
     */
    private void sendOrderStatusMessage(OrdersEntity order, String tag) {
        try {
            OrderStatusMessage msg = new OrderStatusMessage();
            msg.setOrderId(order.getOrderid());
            msg.setUserId(order.getUserid());
            msg.setProductId(order.getShangpinid());
            msg.setQuantity(order.getShuliang());
            msg.setTotalPrice(order.getZongjia());
            msg.setStatus(order.getZhuangtai());

            String destination = MQTopics.ORDER_STATUS_CHANGE + ":" + tag;
            rocketMQTemplate.syncSend(destination, MessageBuilder.withPayload(msg).build());
            log.info("订单状态消息发送成功: orderId={}, tag={}", order.getOrderid(), tag);
        } catch (Exception e) {
            // 消息发送失败不影响主业务，仅记录日志
            log.error("订单状态消息发送失败: orderId={}, tag={}", order.getOrderid(), tag, e);
        }
    }

    @Override
    public OrderVO createOrderFromDTO(CreateOrderDTO dto, Long userId) {
        // 远程获取商品信息填充订单
        Object productData = feignProductService.getProductDetail(dto.getProductId()).getData();
        @SuppressWarnings("unchecked")
        Map<String, Object> productMap = productData instanceof Map ? (Map<String, Object>) productData : new HashMap<>();

        OrdersEntity entity = new OrdersEntity();
        entity.setUserid(userId);
        entity.setShangpinid(dto.getProductId());
        entity.setShangpinming(productMap.get("mingcheng") != null ? productMap.get("mingcheng").toString() : "");
        entity.setShangpintupian(productMap.get("tupian") != null ? productMap.get("tupian").toString() : "");
        entity.setShuliang(dto.getQuantity());
        entity.setJiage(dto.getQuantity() != null && productMap.get("jiage") != null
                ? new BigDecimal(productMap.get("jiage").toString()) : null);
        entity.setLianxidianhua(dto.getContactPhone());
        entity.setShouhuodizhi(dto.getShippingAddress());
        entity.setFukuanfangshi(dto.getPaymentMethod());
        entity.setBeizhu(dto.getRemark());
        entity.setTuanduiid(dto.getGroupBuyId());

        createOrder(entity);
        return toVO(entity);
    }

    @Override
    public OrderVO toVO(OrdersEntity e) {
        if (e == null) return null;
        OrderVO vo = new OrderVO();
        vo.setId(e.getId());
        vo.setOrderId(e.getOrderid());
        vo.setUserId(e.getUserid());
        vo.setProductId(e.getShangpinid());
        vo.setProductName(e.getShangpinming());
        vo.setProductImage(e.getShangpintupian());
        vo.setQuantity(e.getShuliang());
        vo.setUnitPrice(e.getJiage());
        vo.setTotalPrice(e.getZongjia());
        vo.setContactPhone(e.getLianxidianhua());
        vo.setShippingAddress(e.getShouhuodizhi());
        vo.setStatus(e.getZhuangtai());
        vo.setPaymentMethod(e.getFukuanfangshi());
        vo.setRemark(e.getBeizhu());
        vo.setGroupBuyId(e.getTuanduiid());
        vo.setCreateTime(e.getAddtime());
        vo.setUpdateTime(e.getUpdatetime());
        return vo;
    }
}
