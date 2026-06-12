package com.cgb.order.entity.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单响应 VO（英文字段，对前端暴露）
 */
@Data
@Schema(description = "订单响应")
public class OrderVO implements Serializable {
    private static final long serialVersionUID = 1L;

    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "订单编号")
    private String orderId;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "商品ID")
    private Long productId;

    @Schema(description = "商品名称")
    private String productName;

    @Schema(description = "商品图片")
    private String productImage;

    @Schema(description = "购买数量")
    private Integer quantity;

    @Schema(description = "单价")
    private BigDecimal unitPrice;

    @Schema(description = "总价")
    private BigDecimal totalPrice;

    @Schema(description = "联系电话")
    private String contactPhone;

    @Schema(description = "收货地址")
    private String shippingAddress;

    @Schema(description = "状态: 0待支付 1已支付 2已取消 3已发货 4已完成")
    private Integer status;

    @Schema(description = "付款方式: 0微信 1支付宝")
    private Integer paymentMethod;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "团购ID")
    private Long groupBuyId;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}
