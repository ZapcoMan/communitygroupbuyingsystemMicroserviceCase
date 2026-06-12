package com.cgb.groupbuy.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.cgb.common.EIException;
import com.cgb.common.ErrorCode;
import com.cgb.common.feign.FeignProductService;
import com.cgb.common.mq.GroupBuyMessage;
import com.cgb.common.mq.MQTopics;
import com.cgb.common.utils.*;
import com.cgb.groupbuy.dao.TuanweiDao;
import com.cgb.groupbuy.entity.TuanweiEntity;
import com.cgb.groupbuy.service.TuanweiService;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class TuanweiServiceImpl implements TuanweiService {

    private final TuanweiDao tuanweiDao;
    private final FeignProductService feignProductService;
    private final RocketMQTemplate rocketMQTemplate;

    @Override
    public void save(TuanweiEntity entity) {
        if (entity.getZhuangtai() == null) entity.setZhuangtai(0);
        if (entity.getXianxiarenshu() == null) entity.setXianxiarenshu(1);
        // 发起团购时扣减团长购买的库存
        if (entity.getShangpinid() != null) {
            try {
                feignProductService.decreaseStock(entity.getShangpinid(), 1);
                log.info("发起团购，团长库存扣减成功: productId={}", entity.getShangpinid());
            } catch (Exception e) {
                log.error("发起团购，库存扣减失败: productId={}", entity.getShangpinid(), e);
                throw new EIException("库存不足，无法发起团购");
            }
        }
        tuanweiDao.insert(entity);
        // 发送团购创建消息
        sendGroupBuyMessage(entity, MQTopics.TAG_GROUPBUY_JOINED);
    }

    @Override
    public void update(TuanweiEntity entity) {
        if (entity.getId() == null) throw new EIException("团购ID不能为空");
        tuanweiDao.updateById(entity);
    }

    @Override
    public void delete(Long id) { tuanweiDao.deleteById(id); }

    @Override
    public TuanweiEntity getById(Long id) {
        TuanweiEntity entity = tuanweiDao.selectById(id);
        if (entity == null) throw new EIException(ErrorCode.RESOURCE_NOT_FOUND);
        return entity;
    }

    @Override
    public IPage<TuanweiEntity> queryPage(TuanweiEntity params) {
        IPage<TuanweiEntity> page = new Query<TuanweiEntity>().getPage(
                CommonUtil.convert(params, Map.class));
        LambdaQueryWrapper<TuanweiEntity> wrapper = new LambdaQueryWrapper<>();
        if (CommonUtil.isNotEmpty(params.getMingcheng())) {
            wrapper.like(TuanweiEntity::getMingcheng, params.getMingcheng());
        }
        if (params.getZhuangtai() != null) {
            wrapper.eq(TuanweiEntity::getZhuangtai, params.getZhuangtai());
        }
        wrapper.orderByDesc(TuanweiEntity::getId);
        return tuanweiDao.selectPage(page, wrapper);
    }

    /**
     * 参团（Seata分布式事务：人数+1 + 扣库存 + 发MQ）
     */
    @Override
    @GlobalTransactional(name = "cgb-join-groupbuy", rollbackFor = Exception.class)
    public void joinGroupBuy(Long groupBuyId, Long userId, Integer quantity) {
        // 1. 查询团购信息
        TuanweiEntity groupBuy = tuanweiDao.selectById(groupBuyId);
        if (groupBuy == null) throw new EIException("团购不存在");
        if (groupBuy.getZhuangtai() != 0) throw new EIException("团购已结束");
        if (groupBuy.getJieshushijian() != null && groupBuy.getJieshushijian().isBefore(LocalDateTime.now())) {
            throw new EIException("团购已过期");
        }
        if (groupBuy.getXianxiarenshu() >= groupBuy.getLirenjia()) {
            throw new EIException("团购人数已满");
        }

        // 2. 原子增加参团人数
        int rows = tuanweiDao.increaseMember(groupBuyId, 1);
        if (rows == 0) throw new EIException("参团失败，团购已满或已结束");

        // 3. 远程调用商品服务扣减库存
        log.info("参团分布式事务 → 扣减库存: productId={}, quantity={}", groupBuy.getShangpinid(), quantity);
        var stockResult = feignProductService.decreaseStock(groupBuy.getShangpinid(), quantity);
        if (stockResult.getCode() != 0) {
            throw new EIException("库存扣减失败: " + stockResult.getMsg());
        }

        // 4. 发送参团消息（RocketMQ）
        sendGroupBuyMessage(groupBuy, MQTopics.TAG_GROUPBUY_JOINED);

        // 5. 检查是否成团
        checkAndCompleteGroupBuy(groupBuyId);

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
            TuanweiEntity entity = tuanweiDao.selectById(groupBuyId);
            sendGroupBuyMessage(entity, MQTopics.TAG_GROUPBUY_COMPLETED);
        }
    }

    /**
     * 发送团购状态变更消息到 RocketMQ
     */
    private void sendGroupBuyMessage(TuanweiEntity entity, String tag) {
        try {
            GroupBuyMessage msg = new GroupBuyMessage();
            msg.setGroupBuyId(entity.getId());
            msg.setLeaderUserId(entity.getUserid());
            msg.setProductId(entity.getShangpinid());
            msg.setGroupPrice(entity.getTejia());
            msg.setTargetCount(entity.getLirenjia());
            msg.setCurrentCount(entity.getXianxiarenshu());
            if (tag.equals(MQTopics.TAG_GROUPBUY_COMPLETED)) msg.setStatus(1);
            else if (tag.equals(MQTopics.TAG_GROUPBUY_EXPIRED)) msg.setStatus(2);
            else msg.setStatus(0);

            String destination = MQTopics.GROUPBUY_STATUS_CHANGE + ":" + tag;
            rocketMQTemplate.syncSend(destination, MessageBuilder.withPayload(msg).build());
            log.info("团购状态消息发送成功: groupBuyId={}, tag={}", entity.getId(), tag);
        } catch (Exception e) {
            log.error("团购状态消息发送失败: groupBuyId={}, tag={}", entity.getId(), tag, e);
        }
    }
}
