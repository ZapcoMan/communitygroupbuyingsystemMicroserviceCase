package com.cgb.content.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.cgb.content.entity.InformationEntity;

public interface InformationService {
    void save(InformationEntity entity);
    void update(InformationEntity entity);
    void delete(Long id);
    InformationEntity getById(Long id);
    IPage<InformationEntity> queryPage(InformationEntity params);
}