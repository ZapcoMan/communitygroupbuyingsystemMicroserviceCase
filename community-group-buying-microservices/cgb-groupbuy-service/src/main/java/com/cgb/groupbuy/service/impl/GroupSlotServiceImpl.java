package com.cgb.groupbuy.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.cgb.common.EIException;
import com.cgb.common.ErrorCode;
import com.cgb.common.feign.FeignProductService;
import com.cgb.common.mq.GroupBuyMessage;
import com.cgb.common.mq.MQTopics;
import com.cgb.common.utils.*;
import com.cgb.groupbuy.dao.GroupSlotDao;
import com.cgb.groupbuy.entity.GroupSlotEntity;
import com.cgb.groupbuy.service.GroupSlotService;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class GroupSlotServiceImpl implements GroupSlotService {

    private final GroupSlotDao tuanweiDao;
    private final FeignProductService feignProductService;
    private final RocketMQTemplate rocketMQTemplate;
    private final RedisTemplate<String, Object> redisTemplate;

    private static final String GROUP_BUY_CACHE_PREFIX = "cgb:groupbuy:";

    @Override
    public void save(GroupSlotEntity entity) {
        if (entity.getStatus() == null) entity.setStatus(0);
        if (entity.getCurrentMemberCount() == null) entity.setCurrentMemberCount(1);
        if (entity.getEndTime() == null) {
            entity.setEndTime(LocalDateTime.now().plusDays(7)); // 默认7�?        }
        // 发起团购时扣减团长购买的库存
        if (entity.getProductId() != null) {
            try {
                feignProductService.decreaseStock(entity.getProductId(), 1);
                log.info("发起团购，团长库存扣减成�? productId={}", entity.getProductId());
            } catch (Exception e) {
                log.error("发起团购，库存扣减失�? productId={}", entity.getProductId(), e);
                throw new EIException("库存不足，无法发起团�?);
            }
        }
        tuanweiDao.insert(entity);
        // 缓存团购信息
        cacheGroupBuy(entity);
        // 发送团购创建消�?        sendGroupBuyMessage(entity, MQTopics.TAG_GROUPBUY_JOINED);
    }

    @Override
    public void update(GroupSlotEntity entity) {
        if (entity.getId() == null) throw new EIException("团购ID不能为空");
        tuanweiDao.updateById(entity);
        // 更新缓存
        evictGroupBuyCache(entity.getId());
    }

    @Override
    public void delete(Long id) {
        tuanweiDao.deleteById(id);
        evictGroupBuyCache(id);
    }

    @Override
    public GroupSlotEntity getById(Long id) {
        // 先查缓存
        String key = GROUP_BUY_CACHE_PREFIX + id;
        try {
            Object cached = redisTemplate.opsForValue().get(key);
            if (cached instanceof GroupSlotEntity) {
                return (GroupSlotEntity) cached;
            }
        } catch (Exception e) {
            log.warn("团购缓存读取失败: id={}", id, e);
        }
        GroupSlotEntity entity = tuanweiDao.selectById(id);
        if (entity == null) throw new EIException(ErrorCode.RESOURCE_NOT_FOUND);
        cacheGroupBuy(entity);
        return entity;
    }

    @Override
    public IPage<GroupSlotEntity> queryPage(GroupSlotEntity params) {
        IPage<GroupSlotEntity> page = new Query<GroupSlotEntity>().getPage(
                CommonUtil.convert(params, Map.class));
        LambdaQueryWrapper<GroupSlotEntity> wrapper = new LambdaQueryWrapper<>();
        if (CommonUtil.isNotEmpty(params.getGroupName())) {
            wrapper.like(GroupSlotEntity::getGroupName, params.getGroupName());
        }
        if (params.getStatus() != null) {
            wrapper.eq(GroupSlotEntity::getStatus, params.getStatus());
        }
        wrapper.orderByDesc(GroupSlotEntity::getId);
        return tuanweiDao.selectPage(page, wrapper);
    }

    /**
     * 参团（Seata分布式事务：人数+1 + 扣库�?+ 发MQ�?     * 注意：仅在此方法声明 @GlobalTransactional，内部调用的方法不再声明
     */
    @Override
    @GlobalTransactional(name = "cgb-join-groupbuy", rollbackFor = Exception.class)
    public void joinGroupBuy(Long groupBuyId, Long userId, Integer quantity) {
        // 1. 查询团购信息
        GroupSlotEntity groupBuy = tuanweiDao.selectById(groupBuyId);
        if (groupBuy == null) throw new EIException("团购不存�?);
        if (groupBuy.getStatus() != 0) throw new EIException("团购已结�?);
        if (groupBuy.getEndTime() != null && groupBuy.getEndTime().isBefore(LocalDateTime.now())) {
            throw new EIException("团购已过�?);
        }
        if (groupBuy.getCurrentMemberCount() >= groupBuy.getTargetMemberCount()) {
            throw new EIException("团购人数已满");
        }

        // 2. 原子增加参团人数
        int rows = tuanweiDao.increaseMember(groupBuyId, 1);
        if (rows == 0) throw new EIException("参团失败，团购已满或已结�?);

        // 3. 远程调用商品服务扣减库存
        log.info("参团分布式事�?�?扣减库存: productId={}, quantity={}", groupBuy.getProductId(), quantity);
        var stockResult = feignProductService.decreaseStock(groupBuy.getProductId(), quantity);
        if (stockResult.getCode() != 0) {
            throw new EIException("库存扣减失败: " + stockResult.getMsg());
        }

        // 4. 发送参团消息（RocketMQ�?        sendGroupBuyMessage(groupBuy, MQTopics.TAG_GROUPBUY_JOINED);

        // 5. 检查是否成�?        checkAndCompleteGroupBuy(groupBuyId);

        // 6. 清除缓存
        evictGroupBuyCache(groupBuyId);

        log.info("参团成功: groupBuyId={}, userId={}, quantity={}", groupBuyId, userId, quantity);
    }

    /**
     * 检查并完成成团（原子操作）
     */
    @Override
    public void checkAndCompleteGroupBuy(Long groupBuyId) {
        int rows = tuanweiDao.completeGroupBuy(groupBuyId);
        if (rows > 0) {
            log.info("团购成团成功: groupBuyId={}", groupBuyId);
            GroupSlotEntity entity = tuanweiDao.selectById(groupBuyId);
            sendGroupBuyMessage(entity, MQTopics.TAG_GROUPBUY_COMPLETED);
            evictGroupBuyCache(groupBuyId);
        }
    }

    /**
     * 过期团购扫描（可由定时任务调用）
     */
    @Override
    public int expireGroupBuys() {
        // 批量扫描过期团购
        LambdaQueryWrapper<GroupSlotEntity> wrapper = new LambdaQueryWrapper<GroupSlotEntity>()
                .eq(GroupSlotEntity::getStatus, 0)
                .lt(GroupSlotEntity::getEndTime, LocalDateTime.now());
        var expiredList = tuanweiDao.selectList(wrapper);
        int count = 0;
        for (GroupSlotEntity entity : expiredList) {
            int rows = tuanweiDao.expireGroupBuy(entity.getId());
            if (rows > 0) {
                count++;
                sendGroupBuyMessage(entity, MQTopics.TAG_GROUPBUY_EXPIRED);
                evictGroupBuyCache(entity.getId());
                log.info("团购已过�? groupBuyId={}", entity.getId());
            }
        }
        return count;
    }

    /**
     * 发送团购状态变更消息到 RocketMQ
     */
    private void sendGroupBuyMessage(GroupSlotEntity entity, String tag) {
        try {
            GroupBuyMessage msg = new GroupBuyMessage();
            msg.setGroupBuyId(entity.getId());
            msg.setLeaderUserId(entity.getLeaderUserId());
            msg.setProductId(entity.getProductId());
            msg.setGroupPrice(entity.getGroupPrice());
            msg.setTargetMemberCount(entity.getTargetMemberCount());
            msg.setCurrentMemberCount(entity.getCurrentMemberCount());
            if (tag.equals(MQTopics.TAG_GROUPBUY_COMPLETED)) msg.setStatus(1);
            else if (tag.equals(MQTopics.TAG_GROUPBUY_EXPIRED)) msg.setStatus(2);
            else msg.setStatus(0);

            String destination = MQTopics.GROUPBUY_STATUS_CHANGE + ":" + tag;
            rocketMQTemplate.syncSend(destination, MessageBuilder.withPayload(msg).build());
            log.info("团购状态消息发送成�? groupBuyId={}, tag={}", entity.getId(), tag);
        } catch (Exception e) {
            log.error("团购状态消息发送失�? groupBuyId={}, tag={}", entity.getId(), tag, e);
        }
    }

    private void cacheGroupBuy(GroupSlotEntity entity) {
        try {
            redisTemplate.opsForValue().set(GROUP_BUY_CACHE_PREFIX + entity.getId(), entity, 30, TimeUnit.MINUTES);
        } catch (Exception e) {
            log.warn("团购缓存写入失败: id={}", entity.getId(), e);
        }
    }

    private void evictGroupBuyCache(Long id) {
        try {
            redisTemplate.delete(GROUP_BUY_CACHE_PREFIX + id);
        } catch (Exception e) {
            log.warn("团购缓存清除失败: id={}", id, e);
        }
    }
}
