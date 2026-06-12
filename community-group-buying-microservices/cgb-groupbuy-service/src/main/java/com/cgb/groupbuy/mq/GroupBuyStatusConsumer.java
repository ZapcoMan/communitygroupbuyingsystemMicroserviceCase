package com.cgb.groupbuy.mq;

import com.cgb.common.feign.FeignProductService;
import com.cgb.common.mq.GroupBuyMessage;
import com.cgb.common.mq.MQTopics;
import com.cgb.groupbuy.dao.GroupSlotDao;
import com.cgb.groupbuy.entity.GroupSlotEntity;
import com.cgb.groupbuy.service.GroupSlotService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

/**
 * 团购服务 - 消费团购状态消�? * 处理：成团后通知、过期团购库存回�? */
@Slf4j
@Component
@RocketMQMessageListener(
        topic = MQTopics.GROUPBUY_STATUS_CHANGE,
        selectorExpression = "*",
        consumerGroup = "cgb-groupbuy-status-consumer-group"
)
@RequiredArgsConstructor
public class GroupBuyStatusConsumer implements RocketMQListener<GroupBuyMessage> {

    private final GroupSlotService tuanweiService;
    private final FeignProductService feignProductService;

    @Override
    public void onMessage(GroupBuyMessage message) {
        log.info("团购服务收到团购状态消�? groupBuyId={}, status={}, currentCount={}/{}",
                message.getGroupBuyId(), message.getStatus(),
                message.getCurrentMemberCount(), message.getTargetMemberCount());

        switch (message.getStatus()) {
            case 1: // 成团
                log.info("团购成团! groupBuyId={}, 共{}人参团", message.getGroupBuyId(), message.getCurrentMemberCount());
                break;
            case 2: // 过期
                log.info("团购过期: groupBuyId={}", message.getGroupBuyId());
                // 过期团购 �?回补库存
                if (message.getProductId() != null && message.getQuantity() != null) {
                    try {
                        feignProductService.increaseStock(message.getProductId(), message.getQuantity());
                        log.info("过期团购库存回补成功: productId={}, quantity={}",
                                message.getProductId(), message.getQuantity());
                    } catch (Exception e) {
                        log.error("过期团购库存回补失败，需人工补偿: productId={}", message.getProductId(), e);
                    }
                }
                break;
            case 0: // 参团
                log.info("新用户参团 groupBuyId={}, 当前{}/{}", message.getGroupBuyId(), message.getCurrentMemberCount(), message.getTargetMemberCount());
                break;
            default:
                log.warn("未知团购状态 {}", message.getStatus());
        }
    }
}
