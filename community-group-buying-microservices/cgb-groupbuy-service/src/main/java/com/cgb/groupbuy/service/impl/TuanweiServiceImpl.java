package com.cgb.groupbuy.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.cgb.common.EIException;
import com.cgb.common.ErrorCode;
import com.cgb.common.utils.*;
import com.cgb.groupbuy.dao.TuanweiDao;
import com.cgb.groupbuy.entity.TuanweiEntity;
import com.cgb.groupbuy.service.TuanweiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class TuanweiServiceImpl implements TuanweiService {

    private final TuanweiDao tuanweiDao;

    @Override
    public void save(TuanweiEntity entity) {
        if (entity.getZhuangtai() == null) entity.setZhuangtai(0);
        if (entity.getXianxiarenshu() == null) entity.setXianxiarenshu(1);
        tuanweiDao.insert(entity);
    }

    @Override
    public void update(TuanweiEntity entity) {
        if (entity.getId() == null) throw new EIException("团购ID不能为空");
        tuanweiDao.updateById(entity);
    }

    @Override
    public void delete(Long id) { tuanweiDao.deleteById(id); }

    @Override
    public TuanweiEntity getById(Long id) {
        TuanweiEntity entity = tuanweiDao.selectById(id);
        if (entity == null) throw new EIException(ErrorCode.RESOURCE_NOT_FOUND);
        return entity;
    }

    @Override
    public IPage<TuanweiEntity> queryPage(TuanweiEntity params) {
        IPage<TuanweiEntity> page = new Query<TuanweiEntity>().getPage(
                CommonUtil.convert(params, Map.class));
        LambdaQueryWrapper<TuanweiEntity> wrapper = new LambdaQueryWrapper<>();
        if (CommonUtil.isNotEmpty(params.getMingcheng())) {
            wrapper.like(TuanweiEntity::getMingcheng, params.getMingcheng());
        }
        if (params.getZhuangtai() != null) {
            wrapper.eq(TuanweiEntity::getZhuangtai, params.getZhuangtai());
        }
        wrapper.orderByDesc(TuanweiEntity::getId);
        return tuanweiDao.selectPage(page, wrapper);
    }
}