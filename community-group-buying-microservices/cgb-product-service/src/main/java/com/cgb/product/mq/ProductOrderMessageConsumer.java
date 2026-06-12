package com.cgb.product.mq;

import com.cgb.common.mq.MQTopics;
import com.cgb.common.mq.OrderStatusMessage;
import com.cgb.product.dao.ProductDao;
import com.cgb.product.entity.ProductEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

/**
 * 商品服务 - 消费订单状态变更消息
 * 订单支付成功 → 更新商品销量统计
 * 订单取消 → 库存已由Seata回滚，此处仅做日志/统计
 */
@Slf4j
@Component
@RocketMQMessageListener(
        topic = MQTopics.ORDER_STATUS_CHANGE,
        selectorExpression = "*",
        consumerGroup = "cgb-product-order-consumer-group"
)
@RequiredArgsConstructor
public class ProductOrderMessageConsumer implements RocketMQListener<OrderStatusMessage> {

    private final ProductDao shangpinDao;

    @Override
    public void onMessage(OrderStatusMessage message) {
        log.info("商品服务收到订单状态消息: orderId={}, status={}, productId={}",
                message.getOrderId(), message.getStatus(), message.getProductId());

        switch (message.getStatus()) {
            case 1: // 已支付
                log.info("订单支付成功，商品销量统计: productId={}", message.getProductId());
                // 可扩展：更新商品销量字段
                break;
            case 2: // 已取消
                log.info("订单取消，库存已由Seata回滚: productId={}", message.getProductId());
                break;
            default:
                log.debug("订单状态变更: orderId={}, status={}", message.getOrderId(), message.getStatus());
        }
    }
}
