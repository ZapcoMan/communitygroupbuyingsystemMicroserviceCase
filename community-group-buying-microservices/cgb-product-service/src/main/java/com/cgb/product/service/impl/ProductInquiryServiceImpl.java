package com.cgb.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.cgb.common.utils.*;
import com.cgb.product.dao.ProductInquiryDao;
import com.cgb.product.entity.ProductInquiryEntity;
import com.cgb.product.service.ProductInquiryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductInquiryServiceImpl implements ProductInquiryService {

    private final ProductInquiryDao liuyanDao;

    @Override
    public void save(ProductInquiryEntity entity) { liuyanDao.insert(entity); }

    @Override
    public void delete(Long id) { liuyanDao.deleteById(id); }

    @Override
    public IPage<ProductInquiryEntity> queryPage(ProductInquiryEntity params) {
        IPage<ProductInquiryEntity> page = new Query<ProductInquiryEntity>().getPage(
                CommonUtil.convert(params, Map.class));
        return liuyanDao.selectPage(page, new LambdaQueryWrapper<ProductInquiryEntity>()
                .eq(params.getProductId() != null, ProductInquiryEntity::getProductId, params.getProductId())
                .eq(params.getUserId() != null, ProductInquiryEntity::getUserId, params.getUserId())
                .orderByAsc(ProductInquiryEntity::getId));
    }
}
