package com.cgb.product.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.cgb.product.entity.ProductCommentEntity;

public interface ProductCommentService {
    void save(ProductCommentEntity entity);
    void update(ProductCommentEntity entity);
    void delete(Long id);
    IPage<ProductCommentEntity> queryPage(ProductCommentEntity params);
    Double getAverageScore(Long productId);
}