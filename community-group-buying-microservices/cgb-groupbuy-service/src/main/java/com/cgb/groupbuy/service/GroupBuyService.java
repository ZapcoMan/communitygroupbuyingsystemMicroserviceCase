package com.cgb.groupbuy.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.cgb.groupbuy.entity.GroupBuyEntity;

public interface GroupBuyService {
    void save(GroupBuyEntity entity);
    void update(GroupBuyEntity entity);
    void delete(Long id);
    GroupBuyEntity getById(Long id);
    IPage<GroupBuyEntity> queryPage(GroupBuyEntity params);
    int countByTuanId(Long tuanduiid);

    /** 参团（含分布式事务） */
    void joinGroupBuy(Long groupBuyId, Long userId, Integer quantity);
}
