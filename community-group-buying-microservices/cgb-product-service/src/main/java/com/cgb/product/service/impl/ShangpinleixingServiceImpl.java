package com.cgb.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.cgb.common.utils.*;
import com.cgb.product.dao.ProductCategoryDao;
import com.cgb.product.entity.ProductCategoryEntity;
import com.cgb.product.service.ProductCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class ProductCategoryServiceImpl implements ProductCategoryService {

    private final ProductCategoryDao dao;

    @Override
    public void save(ProductCategoryEntity entity) { dao.insert(entity); }

    @Override
    public void update(ProductCategoryEntity entity) { dao.updateById(entity); }

    @Override
    public void delete(Long id) { dao.deleteById(id); }

    @Override
    public IPage<ProductCategoryEntity> queryPage(ProductCategoryEntity params) {
        IPage<ProductCategoryEntity> page = new Query<ProductCategoryEntity>().getPage(
                CommonUtil.convert(params, Map.class));
        return dao.selectPage(page, new LambdaQueryWrapper<ProductCategoryEntity>()
                .like(params.getCategoryName() != null, ProductCategoryEntity::getCategoryName, params.getCategoryName())
                .orderByDesc(ProductCategoryEntity::getId));
    }
}
