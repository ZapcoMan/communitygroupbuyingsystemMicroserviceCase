package com.cgb.user.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.cgb.user.entity.YonghuEntity;

public interface YonghuService {
    void save(YonghuEntity entity);
    void update(YonghuEntity entity);
    void delete(Long id);
    YonghuEntity getById(Long id);
    YonghuEntity getByZhanghao(String zhanghao);
    IPage<YonghuEntity> queryPage(YonghuEntity params);
}