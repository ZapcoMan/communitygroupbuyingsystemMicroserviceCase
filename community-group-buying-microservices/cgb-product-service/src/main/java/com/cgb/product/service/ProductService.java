package com.cgb.product.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.cgb.common.R;
import com.cgb.product.entity.ProductEntity;

public interface ProductService {
    void save(ProductEntity entity);
    void update(ProductEntity entity);
    void delete(Long id);
    ProductEntity getById(Long id);
    IPage<ProductEntity> queryPage(ProductEntity params);
    R<?> decreaseStock(Long id, Integer quantity);
    R<?> increaseStock(Long id, Integer quantity);
}
