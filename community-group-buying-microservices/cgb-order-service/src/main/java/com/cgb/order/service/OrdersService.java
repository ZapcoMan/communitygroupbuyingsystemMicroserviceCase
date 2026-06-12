package com.cgb.order.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.cgb.order.entity.OrdersEntity;

public interface OrdersService {
    void save(OrdersEntity entity);
    void update(OrdersEntity entity);
    void delete(Long id);
    OrdersEntity getById(Long id);
    OrdersEntity getByOrderId(String orderId);
    IPage<OrdersEntity> queryPage(OrdersEntity params);
    void cancel(String orderId, Long userId);
    void pay(String orderId);

    /**
     * 创建订单（Seata 分布式事务：下单 + 扣库存）
     * @param entity 订单实体
     */
    void createOrder(OrdersEntity entity);
}
