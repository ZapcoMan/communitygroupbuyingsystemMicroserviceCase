package com.cgb.groupbuy.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.cgb.common.EIException;
import com.cgb.common.utils.*;
import com.cgb.groupbuy.dao.TuanweiDao;
import com.cgb.groupbuy.dao.TuanxinxiDao;
import com.cgb.groupbuy.entity.TuanweiEntity;
import com.cgb.groupbuy.entity.TuanxinxiEntity;
import com.cgb.groupbuy.service.TuanweiService;
import com.cgb.groupbuy.service.TuanxinxiService;
import io.seata.spring.annotation.GlobalTransactional;
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
        if (entity.getZhuangtai() == null) entity.setZhuangtai(0);
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
                .eq(params.getTuanduiid() != null, TuanxinxiEntity::getTuanduiid, params.getTuanduiid())
                .eq(params.getUserid() != null, TuanxinxiEntity::getUserid, params.getUserid())
                .orderByDesc(TuanxinxiEntity::getId));
    }

    @Override
    public int countByTuanId(Long tuanduiid) {
        return tuanxinxiDao.selectCount(new LambdaQueryWrapper<TuanxinxiEntity>()
                .eq(TuanxinxiEntity::getTuanduiid, tuanduiid)
                .eq(TuanxinxiEntity::getZhuangtai, 1)).intValue();
    }

    /**
     * 参团（委托给 TuanweiService 的分布式事务方法）
     */
    @Override
    @GlobalTransactional(name = "cgb-join-groupbuy-record", rollbackFor = Exception.class)
    public void joinGroupBuy(Long groupBuyId, Long userId, Integer quantity) {
        // 1. 调用团购服务执行核心参团逻辑（+1人 + 扣库存 + 发MQ + 成团判定）
        tuanweiService.joinGroupBuy(groupBuyId, userId, quantity);

        // 2. 写入参团记录
        TuanweiEntity groupBuy = tuanweiService.getById(groupBuyId);
        TuanxinxiEntity record = new TuanxinxiEntity();
        record.setTuanduiid(groupBuyId);
        record.setUserid(userId);
        record.setShangpinid(groupBuy.getShangpinid());
        record.setShuliang(quantity);
        record.setJiage(groupBuy.getTejia());
        record.setZhuangtai(0); // 待支付
        tuanxinxiDao.insert(record);

        log.info("参团记录写入成功: groupBuyId={}, userId={}", groupBuyId, userId);
    }
}
