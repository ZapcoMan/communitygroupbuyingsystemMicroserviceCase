package com.cgb.user.mq;

import com.cgb.common.mq.MQTopics;
import com.cgb.common.mq.OrderStatusMessage;
import com.cgb.user.service.YonghuService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

/**
 * 用户服务 - 消费订单支付消息，异步增加积分
 */
@Slf4j
@Component
@RocketMQMessageListener(
        topic = MQTopics.ORDER_STATUS_CHANGE,
        selectorExpression = MQTopics.TAG_ORDER_PAID,
        consumerGroup = "cgb-user-order-consumer-group"
)
@RequiredArgsConstructor
public class UserOrderMessageConsumer implements RocketMQListener<OrderStatusMessage> {

    private final YonghuService yonghuService;

    @Override
    public void onMessage(OrderStatusMessage message) {
        log.info("用户服务收到订单支付消息: orderId={}, userId={}, totalPrice={}",
                message.getOrderId(), message.getUserId(), message.getTotalPrice());

        try {
            // 积分规则：每消费1元得1积分（与同步调用互为补充，此处可做幂等校验）
            Double points = message.getTotalPrice() != null ? message.getTotalPrice().doubleValue() : 0.0;
            if (points > 0) {
                yonghuService.addPoints(message.getUserId(), points);
                log.info("异步积分增加成功: userId={}, points={}", message.getUserId(), points);
            }
        } catch (Exception e) {
            log.error("异步积分增加失败: userId={}", message.getUserId(), e);
            // 抛出异常触发 RocketMQ 重试
            throw new RuntimeException("积分增加失败，触发消息重试", e);
        }
    }
}
