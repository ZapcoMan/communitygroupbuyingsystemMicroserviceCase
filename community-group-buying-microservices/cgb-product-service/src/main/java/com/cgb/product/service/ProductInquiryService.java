package com.cgb.product.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.cgb.product.entity.ProductInquiryEntity;

public interface ProductInquiryService {
    void save(ProductInquiryEntity entity);
    void delete(Long id);
    IPage<ProductInquiryEntity> queryPage(ProductInquiryEntity params);
}