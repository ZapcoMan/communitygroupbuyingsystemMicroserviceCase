package com.cgb.order.entity.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 创建订单请求 DTO（英文字段，用户友好）
 */
@Data
@Schema(description = "创建订单请求")
public class CreateOrderDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    @Schema(description = "商品ID", example = "1")
    @NotNull(message = "商品ID不能为空")
    private Long productId;

    @Schema(description = "购买数量", example = "2")
    @NotNull(message = "购买数量不能为空")
    @Min(value = 1, message = "购买数量至少为1")
    private Integer quantity;

    @Schema(description = "联系电话")
    private String contactPhone;

    @Schema(description = "收货地址")
    private String shippingAddress;

    @Schema(description = "付款方式: 0微信 1支付宝", example = "0")
    private Integer paymentMethod;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "团购ID（可选）")
    private Long groupBuyId;
}
