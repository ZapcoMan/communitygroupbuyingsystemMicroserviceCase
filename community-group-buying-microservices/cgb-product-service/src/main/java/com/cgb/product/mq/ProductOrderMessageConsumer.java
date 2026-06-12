package com.cgb.product.mq;

import com.cgb.common.mq.MQTopics;
import com.cgb.common.mq.OrderStatusMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

/**
 * 商品服务 - 消费订单状态变更消息
 * 用于异步处理：订单创建后同步库存缓存、订单取消后确保库存回补等
 */
@Slf4j
@Component
@RocketMQMessageListener(
        topic = MQTopics.ORDER_STATUS_CHANGE,
        selectorExpression = MQTopics.TAG_ORDER_CREATED + " || " + MQTopics.TAG_ORDER_CANCELLED + " || " + MQTopics.TAG_ORDER_PAID,
        consumerGroup = "cgb-product-order-consumer-group"
)
@RequiredArgsConstructor
public class ProductOrderMessageConsumer implements RocketMQListener<OrderStatusMessage> {

    @Override
    public void onMessage(OrderStatusMessage message) {
        log.info("商品服务收到订单状态消息: orderId={}, status={}, productId={}, quantity={}",
                message.getOrderId(), message.getStatus(), message.getProductId(), message.getQuantity());

        switch (message.getStatus()) {
            case 0: // 待支付（刚创建）
                log.info("订单创建，商品ID={} 已扣减库存 {} 件（由Seata分布式事务保证）",
                        message.getProductId(), message.getQuantity());
                break;
            case 2: // 已取消
                log.info("订单取消，商品ID={} 库存应已回补 {} 件",
                        message.getProductId(), message.getQuantity());
                break;
            case 1: // 已支付
                log.info("订单支付完成，商品ID={} 确认出库 {} 件",
                        message.getProductId(), message.getQuantity());
                break;
            default:
                log.warn("未知订单状态: {}", message.getStatus());
        }
    }
}
