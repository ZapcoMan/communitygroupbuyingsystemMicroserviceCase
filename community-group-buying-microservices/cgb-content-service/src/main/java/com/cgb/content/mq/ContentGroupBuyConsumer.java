package com.cgb.content.mq;

import com.cgb.common.mq.GroupBuyMessage;
import com.cgb.common.mq.MQTopics;
import com.cgb.common.mq.OrderStatusMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

/**
 * 内容服务 - 消费业务消息，生成社区动态/通知
 * 团购成团 → 社区公告
 * 订单支付 → 社区动态（可选）
 */
@Slf4j
@Component
@RocketMQMessageListener(
        topic = MQTopics.GROUPBUY_STATUS_CHANGE,
        selectorExpression = MQTopics.TAG_GROUPBUY_COMPLETED,
        consumerGroup = "cgb-content-groupbuy-consumer-group"
)
@RequiredArgsConstructor
public class ContentGroupBuyConsumer implements RocketMQListener<GroupBuyMessage> {

    @Override
    public void onMessage(GroupBuyMessage message) {
        log.info("内容服务收到团购成团消息: groupBuyId={}, 生成社区公告", message.getGroupBuyId());

        // 团购成团 → 自动生成社区公告（可扩展写入 news 表）
        // 这里只做日志记录，实际可注入 NewsService 写入公告
        log.info("【社区公告】团购成团啦！团购ID={}，共{}人参团成功！",
                message.getGroupBuyId(), message.getCurrentCount());
    }
}
