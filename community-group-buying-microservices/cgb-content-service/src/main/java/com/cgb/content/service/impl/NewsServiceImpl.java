package com.cgb.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.cgb.common.utils.*;
import com.cgb.content.dao.NewsDao;
import com.cgb.content.entity.NewsEntity;
import com.cgb.content.service.NewsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class NewsServiceImpl implements NewsService {

    private final NewsDao newsDao;

    @Override
    public void save(NewsEntity entity) { newsDao.insert(entity); }

    @Override
    public void update(NewsEntity entity) { newsDao.updateById(entity); }

    @Override
    public void delete(Long id) { newsDao.deleteById(id); }

    @Override
    public NewsEntity getById(Long id) { return newsDao.selectById(id); }

    @Override
    public IPage<NewsEntity> queryPage(NewsEntity params) {
        IPage<NewsEntity> page = new Query<NewsEntity>().getPage(
                CommonUtil.convert(params, Map.class));
        LambdaQueryWrapper<NewsEntity> wrapper = new LambdaQueryWrapper<>();
        if (CommonUtil.isNotEmpty(params.getTitle())) {
            wrapper.like(NewsEntity::getTitle, params.getTitle());
        }
        if (CommonUtil.isNotEmpty(params.getType())) {
            wrapper.eq(NewsEntity::getType, params.getType());
        }
        wrapper.orderByDesc(NewsEntity::getId);
        return newsDao.selectPage(page, wrapper);
    }
}