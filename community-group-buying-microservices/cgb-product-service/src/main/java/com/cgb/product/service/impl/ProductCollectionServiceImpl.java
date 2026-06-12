package com.cgb.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.cgb.common.utils.*;
import com.cgb.product.dao.ProductCollectionDao;
import com.cgb.product.entity.ProductCollectionEntity;
import com.cgb.product.service.ProductCollectionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductCollectionServiceImpl implements ProductCollectionService {

    private final ProductCollectionDao collectionDao;

    @Override
    public void save(ProductCollectionEntity entity) { collectionDao.insert(entity); }

    @Override
    public void delete(Long id) { collectionDao.deleteById(id); }

    @Override
    public ProductCollectionEntity getByUserAndProduct(Long userId, Long productId) {
        return collectionDao.selectOne(new LambdaQueryWrapper<ProductCollectionEntity>()
                .eq(ProductCollectionEntity::getUserId, userId)
                .eq(ProductCollectionEntity::getProductId, productId));
    }

    @Override
    public IPage<ProductCollectionEntity> queryPage(ProductCollectionEntity params) {
        IPage<ProductCollectionEntity> page = new Query<ProductCollectionEntity>().getPage(
                CommonUtil.convert(params, Map.class));
        return collectionDao.selectPage(page, new LambdaQueryWrapper<ProductCollectionEntity>()
                .eq(params.getUserId() != null, ProductCollectionEntity::getUserId, params.getUserId())
                .eq(params.getProductId() != null, ProductCollectionEntity::getProductId, params.getProductId())
                .orderByDesc(ProductCollectionEntity::getId));
    }

    @Override
    public boolean isCollected(Long userId, Long productId) {
        return getByUserAndProduct(userId, productId) != null;
    }
}
