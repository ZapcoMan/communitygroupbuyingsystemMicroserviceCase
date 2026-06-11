package com.cgb.order.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cgb.order.entity.CartEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CartDao extends BaseMapper<CartEntity> {
}