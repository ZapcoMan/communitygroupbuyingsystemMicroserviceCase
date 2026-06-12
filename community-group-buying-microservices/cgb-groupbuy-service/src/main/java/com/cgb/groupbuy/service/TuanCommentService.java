package com.cgb.groupbuy.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.cgb.groupbuy.entity.TuanCommentEntity;

public interface TuanCommentService {
    void save(TuanCommentEntity entity);
    void update(TuanCommentEntity entity);
    void delete(Long id);
    IPage<TuanCommentEntity> queryPage(TuanCommentEntity params);
}
