package com.cgb.product.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.cgb.product.entity.ProductCategoryEntity;

public interface ProductCategoryService {
    void save(ProductCategoryEntity entity);
    void update(ProductCategoryEntity entity);
    void delete(Long id);
    IPage<ProductCategoryEntity> queryPage(ProductCategoryEntity params);
}
