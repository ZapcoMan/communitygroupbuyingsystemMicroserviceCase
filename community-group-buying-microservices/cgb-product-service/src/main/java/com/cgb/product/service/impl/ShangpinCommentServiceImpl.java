package com.cgb.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cgb.common.utils.*;
import com.cgb.product.dao.ShangpinCommentDao;
import com.cgb.product.entity.ShangpinCommentEntity;
import com.cgb.product.service.ShangpinCommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShangpinCommentServiceImpl implements ShangpinCommentService {

    private final ShangpinCommentDao commentDao;

    @Override
    public void save(ShangpinCommentEntity entity) {
        commentDao.insert(entity);
    }

    @Override
    public void update(ShangpinCommentEntity entity) {
        commentDao.updateById(entity);
    }

    @Override
    public void delete(Long id) {
        commentDao.deleteById(id);
    }

    @Override
    public IPage<ShangpinCommentEntity> queryPage(ShangpinCommentEntity params) {
        IPage<ShangpinCommentEntity> page = new Query<ShangpinCommentEntity>().getPage(
                CommonUtil.convert(params, Map.class));
        return commentDao.selectPage(page, new LambdaQueryWrapper<ShangpinCommentEntity>()
                .eq(params.getShangpinid() != null, ShangpinCommentEntity::getShangpinid, params.getShangpinid())
                .eq(params.getUserid() != null, ShangpinCommentEntity::getUserid, params.getUserid())
                .orderByDesc(ShangpinCommentEntity::getId));
    }

    @Override
    public Double getAverageScore(Long productId) {
        LambdaQueryWrapper<ShangpinCommentEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ShangpinCommentEntity::getShangpinid, productId);
        Long count = commentDao.selectCount(wrapper);
        if (count == null || count == 0) return 0.0;
        Double sum = commentDao.selectList(wrapper).stream()
                .mapToDouble(ShangpinCommentEntity::getPingfen)
                .sum();
        return sum / count;
    }
}