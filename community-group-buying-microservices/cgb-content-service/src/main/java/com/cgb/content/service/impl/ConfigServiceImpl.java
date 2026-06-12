package com.cgb.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.cgb.common.utils.*;
import com.cgb.content.dao.ConfigDao;
import com.cgb.content.entity.ConfigEntity;
import com.cgb.content.service.ConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class ConfigServiceImpl implements ConfigService {

    private final ConfigDao dao;

    @Override
    public void save(ConfigEntity entity) {
        dao.insert(entity);
    }

    @Override
    public void update(ConfigEntity entity) {
        dao.updateById(entity);
    }

    @Override
    public void delete(Long id) {
        dao.deleteById(id);
    }

    @Override
    public IPage<ConfigEntity> queryPage(ConfigEntity params) {
        IPage<ConfigEntity> page = new Query<ConfigEntity>().getPage(
                CommonUtil.convert(params, Map.class));
        return dao.selectPage(page, new LambdaQueryWrapper<ConfigEntity>()
                .like(params.getName() != null, ConfigEntity::getName, params.getName())
                .orderByDesc(ConfigEntity::getId));
    }
}
