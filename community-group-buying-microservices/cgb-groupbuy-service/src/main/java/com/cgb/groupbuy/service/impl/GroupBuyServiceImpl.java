package com.cgb.groupbuy.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.cgb.common.EIException;
import com.cgb.common.utils.*;
import com.cgb.groupbuy.dao.GroupBuyDao;
import com.cgb.groupbuy.entity.GroupSlotEntity;
import com.cgb.groupbuy.entity.GroupBuyEntity;
import com.cgb.groupbuy.service.GroupSlotService;
import com.cgb.groupbuy.service.GroupBuyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class GroupBuyServiceImpl implements GroupBuyService {

    private final GroupBuyDao tuanxinxiDao;
    private final GroupSlotService tuanweiService;

    @Override
    public void save(GroupBuyEntity entity) {
        if (entity.getStatus() == null) entity.setStatus(0);
        tuanxinxiDao.insert(entity);
    }

    @Override
    public void update(GroupBuyEntity entity) { tuanxinxiDao.updateById(entity); }

    @Override
    public void delete(Long id) { tuanxinxiDao.deleteById(id); }

    @Override
    public GroupBuyEntity getById(Long id) { return tuanxinxiDao.selectById(id); }

    @Override
    public IPage<GroupBuyEntity> queryPage(GroupBuyEntity params) {
        IPage<GroupBuyEntity> page = new Query<GroupBuyEntity>().getPage(
                CommonUtil.convert(params, Map.class));
        return tuanxinxiDao.selectPage(page, new LambdaQueryWrapper<GroupBuyEntity>()
                .eq(params.getGroupBuyId() != null, GroupBuyEntity::getGroupBuyId, params.getGroupBuyId())
                .eq(params.getUserId() != null, GroupBuyEntity::getUserId, params.getUserId())
                .orderByDesc(GroupBuyEntity::getId));
    }

    @Override
    public int countByTuanId(Long groupBuyId) {
        return tuanxinxiDao.selectCount(new LambdaQueryWrapper<GroupBuyEntity>()
                .eq(GroupBuyEntity::getGroupBuyId, groupBuyId)
                .eq(GroupBuyEntity::getStatus, 1)).intValue();
    }

    /**
     * 参团（委托给 GroupSlotService.joinGroupBuy 的分布式事务方法）
     * 注意：不再在此方法声明 @GlobalTransactional，避免嵌套事务
     * 事务边界在 GroupSlotService.joinGroupBuy 中统一管理
     */
    @Override
    public void joinGroupBuy(Long groupBuyId, Long userId, Integer quantity) {
        // 1. 调用团购服务执行核心参团逻辑（+1人 + 扣库存 + 发MQ + 成团判定）
        tuanweiService.joinGroupBuy(groupBuyId, userId, quantity);

        // 2. 写入参团记录
        GroupSlotEntity groupBuy = tuanweiService.getById(groupBuyId);
        GroupBuyEntity record = new GroupBuyEntity();
        record.setGroupBuyId(groupBuyId);
        record.setUserId(userId);
        record.setProductId(groupBuy.getProductId());
        record.setQuantity(quantity);
        record.setPrice(groupBuy.getGroupPrice());
        record.setStatus(0); // 待支付
        tuanxinxiDao.insert(record);

        log.info("参团记录写入成功: groupBuyId={}, userId={}", groupBuyId, userId);
    }
}
