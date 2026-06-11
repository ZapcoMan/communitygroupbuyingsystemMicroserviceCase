package com.cgb.content.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.cgb.content.entity.NewsEntity;

public interface NewsService {
    void save(NewsEntity entity);
    void update(NewsEntity entity);
    void delete(Long id);
    NewsEntity getById(Long id);
    IPage<NewsEntity> queryPage(NewsEntity params);
}