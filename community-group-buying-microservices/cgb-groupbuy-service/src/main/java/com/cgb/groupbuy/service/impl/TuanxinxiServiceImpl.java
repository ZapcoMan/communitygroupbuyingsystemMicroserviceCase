package com.cgb.groupbuy.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.cgb.common.utils.*;
import com.cgb.groupbuy.dao.TuanxinxiDao;
import com.cgb.groupbuy.entity.TuanxinxiEntity;
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
}