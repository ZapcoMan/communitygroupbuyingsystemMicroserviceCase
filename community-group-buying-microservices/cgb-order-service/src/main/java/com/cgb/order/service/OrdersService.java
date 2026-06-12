package com.cgb.order.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.cgb.order.entity.OrdersEntity;
import com.cgb.order.entity.dto.CreateOrderDTO;
import com.cgb.order.entity.vo.OrderVO;

public interface OrdersService {
    void save(OrdersEntity entity);
    void update(OrdersEntity entity);
    void delete(Long id);
    OrdersEntity getById(Long id);
    OrdersEntity getByOrderId(String orderId);
    IPage<OrdersEntity> queryPage(OrdersEntity params);
    void cancel(String orderId, Long userId);
    void pay(String orderId);
    void ship(String orderId);
    void confirmReceive(String orderId, Long userId);

    /**
     * 创建订单（Seata 分布式事务：下单 + 扣库存）
     * @param entity 订单实体
     */
    void createOrder(OrdersEntity entity);

    /**
     * 创建订单（DTO 入口，含远程商品信息查询）
     */
    OrderVO createOrderFromDTO(CreateOrderDTO dto, Long userId);

    /**
     * Entity → VO 转换
     */
    OrderVO toVO(OrdersEntity entity);
}
