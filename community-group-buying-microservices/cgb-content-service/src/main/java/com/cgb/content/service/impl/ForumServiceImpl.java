package com.cgb.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.cgb.common.EIException;
import com.cgb.common.utils.*;
import com.cgb.content.dao.ForumDao;
import com.cgb.content.entity.ForumEntity;
import com.cgb.content.service.ForumService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ForumServiceImpl implements ForumService {

    private final ForumDao forumDao;

    @Override
    public void save(ForumEntity entity) {
        if (entity.getThumbsupnum() == null) entity.setThumbsupnum(0);
        if (entity.getCainixihao() == null) entity.setCainixihao(0);
        forumDao.insert(entity);
    }

    @Override
    public void update(ForumEntity entity) { forumDao.updateById(entity); }

    @Override
    public void delete(Long id) { forumDao.deleteById(id); }

    @Override
    public ForumEntity getById(Long id) { return forumDao.selectById(id); }

    @Override
    public IPage<ForumEntity> queryPage(ForumEntity params) {
        IPage<ForumEntity> page = new Query<ForumEntity>().getPage(
                CommonUtil.convert(params, Map.class));
        LambdaQueryWrapper<ForumEntity> wrapper = new LambdaQueryWrapper<>();
        if (params.getUserid() != null) wrapper.eq(ForumEntity::getUserid, params.getUserid());
        if (CommonUtil.isNotEmpty(params.getTitle())) {
            wrapper.like(ForumEntity::getTitle, params.getTitle());
        }
        wrapper.orderByDesc(ForumEntity::getId);
        return forumDao.selectPage(page, wrapper);
    }

    @Override
    public void thumbUp(Long id) {
        ForumEntity entity = forumDao.selectById(id);
        if (entity == null) throw new EIException("帖子不存在");
        entity.setThumbsupnum(entity.getThumbsupnum() + 1);
        forumDao.updateById(entity);
    }
}