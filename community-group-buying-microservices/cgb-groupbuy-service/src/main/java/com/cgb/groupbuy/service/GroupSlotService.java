package com.cgb.groupbuy.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.cgb.groupbuy.entity.GroupSlotEntity;

public interface GroupSlotService {
    void save(GroupSlotEntity entity);
    void update(GroupSlotEntity entity);
    void delete(Long id);
    GroupSlotEntity getById(Long id);
    IPage<GroupSlotEntity> queryPage(GroupSlotEntity params);

    /** 参团（分布式事务：+1人 + 扣库存 + 发MQ） */
    void joinGroupBuy(Long groupBuyId, Long userId, Integer quantity);

    /** 检查并完成成团 */
    void checkAndCompleteGroupBuy(Long groupBuyId);

    /** 批量过期团购扫描 */
    int expireGroupBuys();
}
