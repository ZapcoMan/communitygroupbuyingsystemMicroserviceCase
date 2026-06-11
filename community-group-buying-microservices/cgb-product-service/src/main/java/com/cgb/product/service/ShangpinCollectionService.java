package com.cgb.product.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.cgb.product.entity.ShangpinCollectionEntity;

public interface ShangpinCollectionService {
    void save(ShangpinCollectionEntity entity);
    void delete(Long id);
    ShangpinCollectionEntity getByUserAndProduct(Long userId, Long productId);
    IPage<ShangpinCollectionEntity> queryPage(ShangpinCollectionEntity params);
    boolean isCollected(Long userId, Long productId);
}