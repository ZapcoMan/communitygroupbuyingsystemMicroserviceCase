package com.cgb.content.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cgb.content.entity.MessagesEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MessagesDao extends BaseMapper<MessagesEntity> {
}