package com.cgb.user.mq;

import com.cgb.common.mq.MQTopics;
import com.cgb.common.mq.OrderStatusMessage;
import com.cgb.user.dao.MemberDao;
import com.cgb.user.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

/**
 * 用户服务 - 消费订单状态消息
 * 订单支付成功 → 异步增加用户积分
 * 积分规则：消费金额 * 1 = 积分（1元1积分）
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

    private final MemberService yonghuService;

    @Override
    public void onMessage(OrderStatusMessage message) {
        log.info("用户服务收到订单支付消息: orderId={}, userId={}, totalPrice={}",
                message.getOrderId(), message.getUserId(), message.getTotalPrice());

        if (message.getStatus() == 1 && message.getTotalPrice() != null) {
            // 积分 = 订单金额的整数部分
            Double points = message.getTotalPrice().doubleValue();
            try {
                yonghuService.addPoints(message.getUserId(), points);
                log.info("积分增加成功: userId={}, points={}", message.getUserId(), points);
            } catch (Exception e) {
                log.error("积分增加失败，需人工补偿: userId={}, points={}", message.getUserId(), points, e);
            }
        }
    }
}
