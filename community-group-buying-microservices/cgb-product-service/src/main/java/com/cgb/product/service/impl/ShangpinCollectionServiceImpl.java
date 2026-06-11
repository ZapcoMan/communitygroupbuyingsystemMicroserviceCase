package com.cgb.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.cgb.common.utils.*;
import com.cgb.product.dao.ShangpinCollectionDao;
import com.cgb.product.entity.ShangpinCollectionEntity;
import com.cgb.product.service.ShangpinCollectionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShangpinCollectionServiceImpl implements ShangpinCollectionService {

    private final ShangpinCollectionDao collectionDao;

    @Override
    public void save(ShangpinCollectionEntity entity) {
        collectionDao.insert(entity);
    }

    @Override
    public void delete(Long id) {
        collectionDao.deleteById(id);
    }

    @Override
    public ShangpinCollectionEntity getByUserAndProduct(Long userId, Long productId) {
        return collectionDao.selectOne(new LambdaQueryWrapper<ShangpinCollectionEntity>()
                .eq(ShangpinCollectionEntity::getUserid, userId)
                .eq(ShangpinCollectionEntity::getShangpinid, productId));
    }

    @Override
    public IPage<ShangpinCollectionEntity> queryPage(ShangpinCollectionEntity params) {
        IPage<ShangpinCollectionEntity> page = new Query<ShangpinCollectionEntity>().getPage(
                CommonUtil.convert(params, Map.class));
        return collectionDao.selectPage(page, new LambdaQueryWrapper<ShangpinCollectionEntity>()
                .eq(params.getUserid() != null, ShangpinCollectionEntity::getUserid, params.getUserid())
                .eq(params.getShangpinid() != null, ShangpinCollectionEntity::getShangpinid, params.getShangpinid())
                .orderByDesc(ShangpinCollectionEntity::getId));
    }

    @Override
    public boolean isCollected(Long userId, Long productId) {
        return getByUserAndProduct(userId, productId) != null;
    }
}