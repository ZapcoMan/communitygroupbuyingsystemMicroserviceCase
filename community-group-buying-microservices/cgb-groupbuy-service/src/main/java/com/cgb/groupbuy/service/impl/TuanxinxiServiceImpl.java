package com.cgb.groupbuy.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.cgb.common.EIException;
import com.cgb.common.utils.*;
import com.cgb.groupbuy.dao.TuanxinxiDao;
import com.cgb.groupbuy.entity.TuanweiEntity;
import com.cgb.groupbuy.entity.TuanxinxiEntity;
import com.cgb.groupbuy.service.TuanweiService;
import com.cgb.groupbuy.service.TuanxinxiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class TuanxinxiServiceImpl implements TuanxinxiService {

    private final TuanxinxiDao tuanxinxiDao;
    private final TuanweiService tuanweiService;

    @Override
    public void save(TuanxinxiEntity entity) {
        if (entity.getStatus() == null) entity.setStatus(0);
        tuanxinxiDao.insert(entity);
    }

    @Override
    public void update(TuanxinxiEntity entity) { tuanxinxiDao.updateById(entity); }

    @Override
    public void delete(Long id) { tuanxinxiDao.deleteById(id); }

    @Override
    public TuanxinxiEntity getById(Long id) { return tuanxinxiDao.selectById(id); }

    @Override
    public IPage<TuanxinxiEntity> queryPage(TuanxinxiEntity params) {
        IPage<TuanxinxiEntity> page = new Query<TuanxinxiEntity>().getPage(
                CommonUtil.convert(params, Map.class));
        return tuanxinxiDao.selectPage(page, new LambdaQueryWrapper<TuanxinxiEntity>()
                .eq(params.getGroupBuyId() != null, TuanxinxiEntity::getGroupBuyId, params.getGroupBuyId())
                .eq(params.getUserId() != null, TuanxinxiEntity::getUserId, params.getUserId())
                .orderByDesc(TuanxinxiEntity::getId));
    }

    @Override
    public int countByTuanId(Long groupBuyId) {
        return tuanxinxiDao.selectCount(new LambdaQueryWrapper<TuanxinxiEntity>()
                .eq(TuanxinxiEntity::getGroupBuyId, groupBuyId)
                .eq(TuanxinxiEntity::getStatus, 1)).intValue();
    }

    /**
     * 参团（委托给 TuanweiService.joinGroupBuy 的分布式事务方法）
     * 注意：不再在此方法声明 @GlobalTransactional，避免嵌套事务
     * 事务边界在 TuanweiService.joinGroupBuy 中统一管理
     */
    @Override
    public void joinGroupBuy(Long groupBuyId, Long userId, Integer quantity) {
        // 1. 调用团购服务执行核心参团逻辑（+1人 + 扣库存 + 发MQ + 成团判定）
        tuanweiService.joinGroupBuy(groupBuyId, userId, quantity);

        // 2. 写入参团记录
        TuanweiEntity groupBuy = tuanweiService.getById(groupBuyId);
        TuanxinxiEntity record = new TuanxinxiEntity();
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
