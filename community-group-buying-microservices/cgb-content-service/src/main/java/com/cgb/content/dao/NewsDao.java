package com.cgb.content.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cgb.content.entity.NewsEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface NewsDao extends BaseMapper<NewsEntity> {
}