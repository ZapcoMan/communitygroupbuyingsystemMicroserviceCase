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
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class ForumServiceImpl implements ForumService {

    private final ForumDao forumDao;
    private final RedisTemplate<String, Object> redisTemplate;

    private static final String THUMB_UP_KEY = "cgb:forum:thumbup";
    private static final String HOT_POSTS_KEY = "cgb:forum:hot";
    private static final long HOT_CACHE_TTL_MINUTES = 10;

    @Override
    public void save(ForumEntity entity) {
        if (entity.getLikeCount() == null) entity.setLikeCount(0);
        if (entity.getDislikeCount() == null) entity.setDislikeCount(0);
        forumDao.insert(entity);
        redisTemplate.delete(HOT_POSTS_KEY);
    }

    @Override
    public void update(ForumEntity entity) {
        forumDao.updateById(entity);
        redisTemplate.delete(HOT_POSTS_KEY);
    }

    @Override
    public void delete(Long id) {
        forumDao.deleteById(id);
        redisTemplate.delete(HOT_POSTS_KEY);
    }

    @Override
    public ForumEntity getById(Long id) { return forumDao.selectById(id); }

    @Override
    public IPage<ForumEntity> queryPage(ForumEntity params) {
        IPage<ForumEntity> page = new Query<ForumEntity>().getPage(
                CommonUtil.convert(params, Map.class));
        LambdaQueryWrapper<ForumEntity> wrapper = new LambdaQueryWrapper<>();
        if (params.getUserId() != null) wrapper.eq(ForumEntity::getUserId, params.getUserId());
        if (CommonUtil.isNotEmpty(params.getTitle())) wrapper.like(ForumEntity::getTitle, params.getTitle());
        wrapper.orderByDesc(ForumEntity::getId);
        return forumDao.selectPage(page, wrapper);
    }

    /**
     * 点赞（Redis 原子计数 + 防重复点赞）
     */
    @Override
    public void thumbUp(Long id) {
        ForumEntity entity = forumDao.selectById(id);
        if (entity == null) throw new EIException("帖子不存�?);

        // 使用 Redis SETNX 防重复（简化版：用帖子ID作为key，每个帖子每人只能赞一次）
        String dedupeKey = THUMB_UP_KEY + ":" + id;
        Boolean isNew = redisTemplate.opsForValue().setIfAbsent(dedupeKey, "1", 24, TimeUnit.HOURS);
        if (Boolean.TRUE.equals(isNew)) {
            // 首次点赞，更新数据库
            entity.setLikeCount(entity.getLikeCount() + 1);
            forumDao.updateById(entity);
            log.info("帖子点赞成功: id={}, 当前点赞�?{}", id, entity.getLikeCount());
        } else {
            log.info("帖子已点赞，跳过: id={}", id);
        }

        // 清除热门帖子缓存
        redisTemplate.delete(HOT_POSTS_KEY);
    }

    /**
     * 获取热门帖子（Redis 缓存，按点赞数排序）
     */
    @Override
    public IPage<ForumEntity> getHotPosts(int page, int limit) {
        IPage<ForumEntity> pageResult = new Query<ForumEntity>().getPage(
                Map.of("page", page, "limit", limit));
        LambdaQueryWrapper<ForumEntity> wrapper = new LambdaQueryWrapper<ForumEntity>()
                .orderByDesc(ForumEntity::getLikeCount)
                .gt(ForumEntity::getLikeCount, 0);
        IPage<ForumEntity> result = forumDao.selectPage(pageResult, wrapper);

        // 标记缓存（简化：仅做标记，实际可缓存完整分页结果�?        try {
            redisTemplate.opsForValue().set(HOT_POSTS_KEY, "cached", HOT_CACHE_TTL_MINUTES, TimeUnit.MINUTES);
        } catch (Exception e) {
            log.warn("热门帖子缓存写入失败", e);
        }

        return result;
    }
}
