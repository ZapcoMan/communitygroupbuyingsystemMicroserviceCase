package com.cgb.product.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cgb.product.entity.ShangpinEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface ShangpinDao extends BaseMapper<ShangpinEntity> {

    /** 原子扣减库存（并发安全） */
    @Update("UPDATE shangpin SET kucun = kucun - #{quantity} WHERE id = #{id} AND kucun >= #{quantity}")
    int decreaseStock(@Param("id") Long id, @Param("quantity") Integer quantity);

    /** 原子回补库存（并发安全） */
    @Update("UPDATE shangpin SET kucun = kucun + #{quantity} WHERE id = #{id}")
    int increaseStock(@Param("id") Long id, @Param("quantity") Integer quantity);
}
