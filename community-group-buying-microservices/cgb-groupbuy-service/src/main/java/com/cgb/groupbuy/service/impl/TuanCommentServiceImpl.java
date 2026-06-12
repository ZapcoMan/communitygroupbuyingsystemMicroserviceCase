package com.cgb.groupbuy.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.cgb.common.utils.*;
import com.cgb.groupbuy.dao.TuanCommentDao;
import com.cgb.groupbuy.entity.TuanCommentEntity;
import com.cgb.groupbuy.service.TuanCommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class TuanCommentServiceImpl implements TuanCommentService {

    private final TuanCommentDao dao;

    @Override
    public void save(TuanCommentEntity entity) {
        dao.insert(entity);
    }

    @Override
    public void update(TuanCommentEntity entity) {
        dao.updateById(entity);
    }

    @Override
    public void delete(Long id) {
        dao.deleteById(id);
    }

    @Override
    public IPage<TuanCommentEntity> queryPage(TuanCommentEntity params) {
        IPage<TuanCommentEntity> page = new Query<TuanCommentEntity>().getPage(
                CommonUtil.convert(params, Map.class));
        return dao.selectPage(page, new LambdaQueryWrapper<TuanCommentEntity>()
                .eq(params.getTuanweiid() != null, TuanCommentEntity::getTuanweiid, params.getTuanweiid())
                .like(params.getContent() != null, TuanCommentEntity::getContent, params.getContent())
                .orderByDesc(TuanCommentEntity::getId));
    }
}
