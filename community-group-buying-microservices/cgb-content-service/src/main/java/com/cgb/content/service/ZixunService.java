package com.cgb.content.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.cgb.content.entity.ZixunEntity;

public interface ZixunService {
    void save(ZixunEntity entity);
    void update(ZixunEntity entity);
    void delete(Long id);
    ZixunEntity getById(Long id);
    IPage<ZixunEntity> queryPage(ZixunEntity params);
}