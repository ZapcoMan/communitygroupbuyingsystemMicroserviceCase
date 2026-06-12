package com.cgb.content.mq;

import com.cgb.common.mq.GroupBuyMessage;
import com.cgb.common.mq.MQTopics;
import com.cgb.content.entity.NewsEntity;
import com.cgb.content.service.NewsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

/**
 * 内容服务 - 消费团购成团消息，自动生成社区公告
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

    private final NewsService newsService;

    @Override
    public void onMessage(GroupBuyMessage message) {
        log.info("内容服务收到团购成团消息: groupBuyId={}", message.getGroupBuyId());

        try {
            // 团购成团 → 自动生成社区公告
            NewsEntity news = new NewsEntity();
            news.setTitle("🎉 团购成团通知");
            news.setContent(String.format(
                    "团购成团啦！团购ID=%d，共%d人参团成功，成团人数目标%d人。快来参与更多优惠团购吧！",
                    message.getGroupBuyId(), message.getCurrentMemberCount(), message.getTargetMemberCount()));
            news.setType("groupbuy");
            newsService.save(news);
            log.info("社区公告生成成功: groupBuyId={}", message.getGroupBuyId());
        } catch (Exception e) {
            log.error("社区公告生成失败: groupBuyId={}", message.getGroupBuyId(), e);
            // 不抛异常，避免消息重试
        }
    }
}
