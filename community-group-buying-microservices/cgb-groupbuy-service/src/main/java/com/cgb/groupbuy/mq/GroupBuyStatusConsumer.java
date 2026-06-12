package com.cgb.groupbuy.mq;

import com.cgb.common.mq.GroupBuyMessage;
import com.cgb.common.mq.MQTopics;
import com.cgb.groupbuy.dao.TuanweiDao;
import com.cgb.groupbuy.entity.TuanweiEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

/**
 * 团购服务 - 消费团购状态消息
 * 处理：成团后自动创建订单、过期团购标记等
 */
@Slf4j
@Component
@RocketMQMessageListener(
        topic = MQTopics.GROUPBUY_STATUS_CHANGE,
        selectorExpression = "*",
        consumerGroup = "cgb-groupbuy-status-consumer-group"
)
@RequiredArgsConstructor
public class GroupBuyStatusConsumer implements RocketMQListener<GroupBuyMessage> {

    private final TuanweiDao tuanweiDao;

    @Override
    public void onMessage(GroupBuyMessage message) {
        log.info("团购服务收到团购状态消息: groupBuyId={}, status={}, currentCount={}/{}",
                message.getGroupBuyId(), message.getStatus(),
                message.getCurrentCount(), message.getTargetCount());

        switch (message.getStatus()) {
            case 1: // 成团
                log.info("团购成团! groupBuyId={}, 共{}人参团", message.getGroupBuyId(), message.getCurrentCount());
                // 成团后可触发：通知所有参团用户、创建批量订单等
                break;
            case 2: // 过期
                log.info("团购过期: groupBuyId={}", message.getGroupBuyId());
                // 过期后触发：回补库存、通知用户等
                break;
            case 0: // 参团
                log.info("新用户参团: groupBuyId={}, 当前{}/{}人",
                        message.getGroupBuyId(), message.getCurrentCount(), message.getTargetCount());
                break;
            default:
                log.warn("未知团购状态: {}", message.getStatus());
        }
    }
}
