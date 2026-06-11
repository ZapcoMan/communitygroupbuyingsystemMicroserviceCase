package com.cgb.groupbuy.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.cgb.groupbuy.entity.TuanxinxiEntity;

public interface TuanxinxiService {
    void save(TuanxinxiEntity entity);
    void update(TuanxinxiEntity entity);
    void delete(Long id);
    IPage<TuanxinxiEntity> queryPage(TuanxinxiEntity params);
    int countByTuanId(Long tuanduiid);
}