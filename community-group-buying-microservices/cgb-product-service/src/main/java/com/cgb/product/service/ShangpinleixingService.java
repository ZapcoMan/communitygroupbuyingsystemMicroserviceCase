package com.cgb.product.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.cgb.product.entity.ShangpinleixingEntity;

public interface ShangpinleixingService {
    void save(ShangpinleixingEntity entity);
    void update(ShangpinleixingEntity entity);
    void delete(Long id);
    IPage<ShangpinleixingEntity> queryPage(ShangpinleixingEntity params);
}
