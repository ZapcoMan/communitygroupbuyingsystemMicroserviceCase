package com.cgb.order.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.cgb.order.entity.CartEntity;

public interface CartService {
    void save(CartEntity entity);
    void update(CartEntity entity);
    void delete(Long id);
    IPage<CartEntity> queryPage(CartEntity params);
    void clear(Long userId);
}