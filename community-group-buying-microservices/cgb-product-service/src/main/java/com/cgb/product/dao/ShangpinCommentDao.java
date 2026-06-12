package com.cgb.product.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cgb.product.entity.ProductCommentEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ProductCommentDao extends BaseMapper<ProductCommentEntity> {
}