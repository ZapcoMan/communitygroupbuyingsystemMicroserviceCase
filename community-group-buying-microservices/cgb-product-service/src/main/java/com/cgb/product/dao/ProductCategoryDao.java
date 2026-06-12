package com.cgb.product.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cgb.product.entity.ProductCategoryEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ProductCategoryDao extends BaseMapper<ProductCategoryEntity> {
}
