package com.cgb.common.mq;

import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 订单状态变更消息体
 */
@Data
public class OrderStatusMessage implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 订单编号 */
    private String orderId;
    /** 用户ID */
    private Long userId;
    /** 商品ID */
    private Long productId;
    /** 购买数量 */
    private Integer quantity;
    /** 订单总价 */
    private BigDecimal totalPrice;
    /** 订单状态：0待支付 1已支付 2已取消 */
    private Integer status;
    /** 变更时间戳 */
    private Long timestamp;

    public OrderStatusMessage() {
        this.timestamp = System.currentTimeMillis();
    }
}
