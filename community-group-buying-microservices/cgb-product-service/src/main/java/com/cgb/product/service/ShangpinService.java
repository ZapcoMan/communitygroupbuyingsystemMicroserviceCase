package com.cgb.product.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.cgb.product.entity.ShangpinEntity;

public interface ShangpinService {
    void save(ShangpinEntity entity);
    void update(ShangpinEntity entity);
    void delete(Long id);
    ShangpinEntity getById(Long id);
    IPage<ShangpinEntity> queryPage(ShangpinEntity params);
    void decreaseStock(Long id, Integer quantity);
    void increaseStock(Long id, Integer quantity);
}