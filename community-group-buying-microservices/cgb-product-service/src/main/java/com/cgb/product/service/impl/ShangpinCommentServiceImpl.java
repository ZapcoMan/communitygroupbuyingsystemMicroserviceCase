package com.cgb.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.cgb.common.utils.*;
import com.cgb.product.dao.ProductCommentDao;
import com.cgb.product.entity.ProductCommentEntity;
import com.cgb.product.service.ProductCommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductCommentServiceImpl implements ProductCommentService {

    private final ProductCommentDao commentDao;

    @Override
    public void save(ProductCommentEntity entity) { commentDao.insert(entity); }

    @Override
    public void update(ProductCommentEntity entity) { commentDao.updateById(entity); }

    @Override
    public void delete(Long id) { commentDao.deleteById(id); }

    @Override
    public IPage<ProductCommentEntity> queryPage(ProductCommentEntity params) {
        IPage<ProductCommentEntity> page = new Query<ProductCommentEntity>().getPage(
                CommonUtil.convert(params, Map.class));
        return commentDao.selectPage(page, new LambdaQueryWrapper<ProductCommentEntity>()
                .eq(params.getProductId() != null, ProductCommentEntity::getProductId, params.getProductId())
                .eq(params.getUserId() != null, ProductCommentEntity::getUserId, params.getUserId())
                .orderByDesc(ProductCommentEntity::getId));
    }

    @Override
    public Double getAverageScore(Long productId) {
        LambdaQueryWrapper<ProductCommentEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProductCommentEntity::getProductId, productId);
        Long count = commentDao.selectCount(wrapper);
        if (count == null || count == 0) return 0.0;
        Double sum = commentDao.selectList(wrapper).stream()
                .mapToDouble(ProductCommentEntity::getRating)
                .sum();
        return sum / count;
    }
}
