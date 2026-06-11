package com.cgb.order.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.cgb.order.entity.AddressEntity;

public interface AddressService {
    void save(AddressEntity entity);
    void update(AddressEntity entity);
    void delete(Long id);
    AddressEntity getById(Long id);
    IPage<AddressEntity> queryPage(AddressEntity params);
    void setDefault(Long id, Long userId);
}