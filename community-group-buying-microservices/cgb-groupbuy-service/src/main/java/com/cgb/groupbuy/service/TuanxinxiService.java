package com.cgb.groupbuy.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.cgb.groupbuy.entity.TuanxinxiEntity;

public interface TuanxinxiService {
    void save(TuanxinxiEntity entity);
    void update(TuanxinxiEntity entity);
    void delete(Long id);
    TuanxinxiEntity getById(Long id);
    IPage<TuanxinxiEntity> queryPage(TuanxinxiEntity params);
    int countByTuanId(Long tuanduiid);

    /** 参团（含分布式事务） */
    void joinGroupBuy(Long groupBuyId, Long userId, Integer quantity);
}
