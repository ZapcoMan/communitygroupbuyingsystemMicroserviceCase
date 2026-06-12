package com.cgb.content.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.cgb.content.entity.ForumEntity;

public interface ForumService {
    void save(ForumEntity entity);
    void update(ForumEntity entity);
    void delete(Long id);
    ForumEntity getById(Long id);
    IPage<ForumEntity> queryPage(ForumEntity params);
    void thumbUp(Long id);

    /** 获取热门帖子（Redis 缓存） */
    IPage<ForumEntity> getHotPosts(int page, int limit);
}
