package com.cgb.content.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.cgb.content.entity.ConfigEntity;

public interface ConfigService {
    void save(ConfigEntity entity);
    void update(ConfigEntity entity);
    void delete(Long id);
    IPage<ConfigEntity> queryPage(ConfigEntity params);
}
