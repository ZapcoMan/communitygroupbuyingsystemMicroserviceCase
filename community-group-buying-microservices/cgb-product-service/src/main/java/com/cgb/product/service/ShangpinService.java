package com.cgb.product.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.cgb.common.R;
import com.cgb.product.entity.ShangpinEntity;

public interface ShangpinService {
    void save(ShangpinEntity entity);
    void update(ShangpinEntity entity);
    void delete(Long id);
    ShangpinEntity getById(Long id);
    IPage<ShangpinEntity> queryPage(ShangpinEntity params);
    R<?> decreaseStock(Long id, Integer quantity);
    R<?> increaseStock(Long id, Integer quantity);
}
