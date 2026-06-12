package com.cgb.user.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cgb.user.entity.YonghuEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface YonghuDao extends BaseMapper<YonghuEntity> {

    /**
     * 原子增加积分（并发安全）
     */
    @Update("UPDATE yonghu SET jifen = jifen + #{points} WHERE id = #{id}")
    int addPoints(@Param("id") Long id, @Param("points") Double points);
}