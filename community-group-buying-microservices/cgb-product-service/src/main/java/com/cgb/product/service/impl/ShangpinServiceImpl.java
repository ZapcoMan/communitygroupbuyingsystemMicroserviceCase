package com.cgb.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.cgb.common.EIException;
import com.cgb.common.ErrorCode;
import com.cgb.common.utils.*;
import com.cgb.product.dao.ShangpinDao;
import com.cgb.product.entity.ShangpinEntity;
import com.cgb.product.service.ShangpinService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShangpinServiceImpl implements ShangpinService {

    private final ShangpinDao shangpinDao;
    private final RedisTemplate<String, Object> redisTemplate;

    private static final String STOCK_KEY_PREFIX = "cgb:stock:";

    @Override
    public void save(ShangpinEntity entity) {
        shangpinDao.insert(entity);
    }

    @Override
    public void update(ShangpinEntity entity) {
        if (entity.getId() == null) throw new EIException("商品ID不能为空");
        shangpinDao.updateById(entity);
    }

    @Override
    public void delete(Long id) {
        shangpinDao.deleteById(id);
    }

    @Override
    public ShangpinEntity getById(Long id) {
        ShangpinEntity entity = shangpinDao.selectById(id);
        if (entity == null) throw new EIException(ErrorCode.RESOURCE_NOT_FOUND);
        return entity;
    }

    @Override
    public IPage<ShangpinEntity> queryPage(ShangpinEntity params) {
        IPage<ShangpinEntity> page = new Query<ShangpinEntity>().getPage(
                CommonUtil.convert(params, Map.class));
        LambdaQueryWrapper<ShangpinEntity> wrapper = new LambdaQueryWrapper<>();
        if (CommonUtil.isNotEmpty(params.getMingcheng())) {
            wrapper.like(ShangpinEntity::getMingcheng, params.getMingcheng());
        }
        if (CommonUtil.isNotEmpty(params.getLeixing())) {
            wrapper.eq(ShangpinEntity::getLeixing, params.getLeixing());
        }
        wrapper.orderByDesc(ShangpinEntity::getId);
        return shangpinDao.selectPage(page, wrapper);
    }

    @Override
    public void decreaseStock(Long id, Integer quantity) {
        String key = STOCK_KEY_PREFIX + id;
        Long remain = redisTemplate.opsForValue().decrement(key, quantity);
        if (remain != null && remain < 0) {
            // 库存不足，回补 Redis
            redisTemplate.opsForValue().increment(key, quantity);
            throw new EIException("库存不足");
        }
        // 异步更新数据库
        shangpinDao.update(null,
                new com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper<ShangpinEntity>()
                        .eq(ShangpinEntity::getId, id)
                        .ge(ShangpinEntity::getKucun, quantity)
                        .setSql("kucun = kucun - " + quantity));
    }

    @Override
    public void increaseStock(Long id, Integer quantity) {
        String key = STOCK_KEY_PREFIX + id;
        redisTemplate.opsForValue().increment(key, quantity);
        shangpinDao.update(null,
                new com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper<ShangpinEntity>()
                        .eq(ShangpinEntity::getId, id)
                        .setSql("kucun = kucun + " + quantity));
    }
}