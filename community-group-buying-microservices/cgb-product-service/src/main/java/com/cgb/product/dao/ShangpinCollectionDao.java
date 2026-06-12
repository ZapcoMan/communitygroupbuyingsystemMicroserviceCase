package com.cgb.product.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cgb.product.entity.ProductCollectionEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ProductCollectionDao extends BaseMapper<ProductCollectionEntity> {
}