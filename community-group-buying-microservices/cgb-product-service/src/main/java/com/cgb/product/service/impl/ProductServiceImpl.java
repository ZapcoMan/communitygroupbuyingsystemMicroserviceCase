package com.cgb.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.cgb.common.EIException;
import com.cgb.common.ErrorCode;
import com.cgb.common.R;
import com.cgb.common.utils.*;
import com.cgb.product.dao.ProductDao;
import com.cgb.product.entity.ProductEntity;
import com.cgb.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductDao shangpinDao;
    private final RedisTemplate<String, Object> redisTemplate;

    private static final String STOCK_KEY_PREFIX = "cgb:stock:";

    @Override
    public void save(ProductEntity entity) {
        shangpinDao.insert(entity);
        // 初始化库存缓存
        if (entity.getStock() != null) {
            initStockCache(entity.getId(), entity.getStock());
        }
    }

    @Override
    public void update(ProductEntity entity) {
        if (entity.getId() == null) throw new EIException("商品ID不能为空");
        shangpinDao.updateById(entity);
    }

    @Override
    public void delete(Long id) {
        shangpinDao.deleteById(id);
        redisTemplate.delete(STOCK_KEY_PREFIX + id);
    }

    @Override
    public ProductEntity getById(Long id) {
        ProductEntity entity = shangpinDao.selectById(id);
        if (entity == null) throw new EIException(ErrorCode.RESOURCE_NOT_FOUND);
        return entity;
    }

    @Override
    public IPage<ProductEntity> queryPage(ProductEntity params) {
        IPage<ProductEntity> page = new Query<ProductEntity>().getPage(
                CommonUtil.convert(params, Map.class));
        LambdaQueryWrapper<ProductEntity> wrapper = new LambdaQueryWrapper<>();
        if (CommonUtil.isNotEmpty(params.getProductName())) {
            wrapper.like(ProductEntity::getProductName, params.getProductName());
        }
        if (CommonUtil.isNotEmpty(params.getCategory())) {
            wrapper.eq(ProductEntity::getCategory, params.getCategory());
        }
        if (params.getStatus() != null) {
            wrapper.eq(ProductEntity::getStatus, params.getStatus());
        }
        wrapper.orderByDesc(ProductEntity::getId);
        return shangpinDao.selectPage(page, wrapper);
    }

    @Override
    public R<?> decreaseStock(Long id, Integer quantity) {
        String key = STOCK_KEY_PREFIX + id;
        Long remain = redisTemplate.opsForValue().decrement(key, quantity);
        if (remain != null && remain < 0) {
            redisTemplate.opsForValue().increment(key, quantity);
            return R.fail("库存不足");
        }
        int rows = shangpinDao.decreaseStock(id, quantity);
        if (rows == 0) {
            redisTemplate.opsForValue().increment(key, quantity);
            return R.fail("库存不足");
        }
        log.info("库存扣减成功: productId={}, quantity={}, remain={}", id, quantity, remain);
        return R.ok();
    }

    @Override
    public R<?> increaseStock(Long id, Integer quantity) {
        String key = STOCK_KEY_PREFIX + id;
        redisTemplate.opsForValue().increment(key, quantity);
        shangpinDao.increaseStock(id, quantity);
        log.info("库存回补成功: productId={}, quantity={}", id, quantity);
        return R.ok();
    }

    private void initStockCache(Long productId, Integer stock) {
        try {
            redisTemplate.opsForValue().set(STOCK_KEY_PREFIX + productId, stock, 24, TimeUnit.HOURS);
        } catch (Exception e) {
            log.warn("库存缓存初始化失败: productId={}", productId, e);
        }
    }
}
