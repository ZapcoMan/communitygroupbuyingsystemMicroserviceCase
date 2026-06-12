package com.cgb.groupbuy.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.cgb.common.utils.*;
import com.cgb.groupbuy.dao.GroupBuyCommentDao;
import com.cgb.groupbuy.entity.GroupBuyCommentEntity;
import com.cgb.groupbuy.service.GroupBuyCommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class GroupBuyCommentServiceImpl implements GroupBuyCommentService {

    private final GroupBuyCommentDao dao;

    @Override
    public void save(GroupBuyCommentEntity entity) {
        dao.insert(entity);
    }

    @Override
    public void update(GroupBuyCommentEntity entity) {
        dao.updateById(entity);
    }

    @Override
    public void delete(Long id) {
        dao.deleteById(id);
    }

    @Override
    public IPage<GroupBuyCommentEntity> queryPage(GroupBuyCommentEntity params) {
        IPage<GroupBuyCommentEntity> page = new Query<GroupBuyCommentEntity>().getPage(
                CommonUtil.convert(params, Map.class));
        return dao.selectPage(page, new LambdaQueryWrapper<GroupBuyCommentEntity>()
                .eq(params.getTuanweiid() != null, GroupBuyCommentEntity::getTuanweiid, params.getTuanweiid())
                .like(params.getContent() != null, GroupBuyCommentEntity::getContent, params.getContent())
                .orderByDesc(GroupBuyCommentEntity::getId));
    }
}
