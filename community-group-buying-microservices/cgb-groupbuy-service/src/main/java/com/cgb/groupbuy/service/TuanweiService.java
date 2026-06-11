package com.cgb.groupbuy.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.cgb.groupbuy.entity.TuanweiEntity;

public interface TuanweiService {
    void save(TuanweiEntity entity);
    void update(TuanweiEntity entity);
    void delete(Long id);
    TuanweiEntity getById(Long id);
    IPage<TuanweiEntity> queryPage(TuanweiEntity params);
}