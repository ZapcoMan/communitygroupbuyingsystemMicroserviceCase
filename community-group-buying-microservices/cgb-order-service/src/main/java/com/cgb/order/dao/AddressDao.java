package com.cgb.order.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cgb.order.entity.AddressEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AddressDao extends BaseMapper<AddressEntity> {
}