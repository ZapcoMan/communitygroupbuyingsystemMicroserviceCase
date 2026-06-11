package com.cgb.product.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.cgb.product.entity.ShangpinCommentEntity;

public interface ShangpinCommentService {
    void save(ShangpinCommentEntity entity);
    void update(ShangpinCommentEntity entity);
    void delete(Long id);
    IPage<ShangpinCommentEntity> queryPage(ShangpinCommentEntity params);
    Double getAverageScore(Long productId);
}