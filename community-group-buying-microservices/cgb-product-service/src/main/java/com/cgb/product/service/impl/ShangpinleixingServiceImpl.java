package com.cgb.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cgb.common.utils.*;
import com.cgb.product.dao.ShangpinleixingDao;
import com.cgb.product.entity.ShangpinleixingEntity;
import com.cgb.product.service.ShangpinleixingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class ShangpinleixingServiceImpl implements ShangpinleixingService {

    private final ShangpinleixingDao dao;

    @Override
    public void save(ShangpinleixingEntity entity) {
        dao.insert(entity);
    }

    @Override
    public void update(ShangpinleixingEntity entity) {
        dao.updateById(entity);
    }

    @Override
    public void delete(Long id) {
        dao.deleteById(id);
    }

    @Override
    public IPage<ShangpinleixingEntity> queryPage(ShangpinleixingEntity params) {
        IPage<ShangpinleixingEntity> page = new Query<ShangpinleixingEntity>().getPage(
                CommonUtil.convert(params, Map.class));
        return dao.selectPage(page, new LambdaQueryWrapper<ShangpinleixingEntity>()
                .like(params.getShangpinleixing() != null, ShangpinleixingEntity::getShangpinleixing, params.getShangpinleixing())
                .orderByDesc(ShangpinleixingEntity::getId));
    }
}
