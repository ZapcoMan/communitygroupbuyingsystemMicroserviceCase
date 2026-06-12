package com.cgb.product.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.cgb.product.entity.ProductCollectionEntity;

public interface ProductCollectionService {
    void save(ProductCollectionEntity entity);
    void delete(Long id);
    ProductCollectionEntity getByUserAndProduct(Long userId, Long productId);
    IPage<ProductCollectionEntity> queryPage(ProductCollectionEntity params);
    boolean isCollected(Long userId, Long productId);
}