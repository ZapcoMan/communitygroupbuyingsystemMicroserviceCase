package com.cgb.common.mq;

/**
 * RocketMQ 消息主题常量
 */
public interface MQTopics {

    /** 订单状态变更主题 */
    String ORDER_STATUS_CHANGE = "ORDER_STATUS_CHANGE";

    /** 订单状态变更 Tags */
    String TAG_ORDER_CREATED = "ORDER_CREATED";
    String TAG_ORDER_PAID = "ORDER_PAID";
    String TAG_ORDER_CANCELLED = "ORDER_CANCELLED";
}
