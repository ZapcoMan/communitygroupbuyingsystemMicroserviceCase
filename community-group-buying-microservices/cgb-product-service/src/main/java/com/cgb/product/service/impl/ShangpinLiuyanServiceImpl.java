package com.cgb.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.cgb.common.utils.*;
import com.cgb.product.dao.ShangpinLiuyanDao;
import com.cgb.product.entity.ShangpinLiuyanEntity;
import com.cgb.product.service.ShangpinLiuyanService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShangpinLiuyanServiceImpl implements ShangpinLiuyanService {

    private final ShangpinLiuyanDao liuyanDao;

    @Override
    public void save(ShangpinLiuyanEntity entity) { liuyanDao.insert(entity); }

    @Override
    public void delete(Long id) { liuyanDao.deleteById(id); }

    @Override
    public IPage<ShangpinLiuyanEntity> queryPage(ShangpinLiuyanEntity params) {
        IPage<ShangpinLiuyanEntity> page = new Query<ShangpinLiuyanEntity>().getPage(
                CommonUtil.convert(params, Map.class));
        return liuyanDao.selectPage(page, new LambdaQueryWrapper<ShangpinLiuyanEntity>()
                .eq(params.getProductId() != null, ShangpinLiuyanEntity::getProductId, params.getProductId())
                .eq(params.getUserId() != null, ShangpinLiuyanEntity::getUserId, params.getUserId())
                .orderByAsc(ShangpinLiuyanEntity::getId));
    }
}
