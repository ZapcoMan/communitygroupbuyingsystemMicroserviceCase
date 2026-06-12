package com.cgb.groupbuy.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.cgb.groupbuy.entity.TuanweiEntity;

public interface TuanweiService {
    void save(TuanweiEntity entity);
    void update(TuanweiEntity entity);
    void delete(Long id);
    TuanweiEntity getById(Long id);
    IPage<TuanweiEntity> queryPage(TuanweiEntity params);

    /** 参团（分布式事务：+1人 + 扣库存 + 发MQ） */
    void joinGroupBuy(Long groupBuyId, Long userId, Integer quantity);

    /** 检查并完成成团 */
    void checkAndCompleteGroupBuy(Long groupBuyId);

    /** 批量过期团购扫描 */
    int expireGroupBuys();
}
