package com.cgb.product.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.cgb.product.entity.ShangpinLiuyanEntity;

public interface ShangpinLiuyanService {
    void save(ShangpinLiuyanEntity entity);
    void delete(Long id);
    IPage<ShangpinLiuyanEntity> queryPage(ShangpinLiuyanEntity params);
}