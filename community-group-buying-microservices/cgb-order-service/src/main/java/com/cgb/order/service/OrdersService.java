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
}