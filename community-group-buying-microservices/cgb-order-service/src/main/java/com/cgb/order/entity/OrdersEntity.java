package com.cgb.order.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单实体
 */
@Data
@TableName("orders")
public class OrdersEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("orderid")
    private String orderNo;             // 订单编号

    @TableField("userid")
    private Long userId;                // 用户ID

    @TableField("shangpinid")
    private Long productId;             // 商品ID

    @TableField("shangpinming")
    private String productName;         // 商品名称

    @TableField("shangpintupian")
    private String productImage;        // 商品图片

    @TableField("shuliang")
    private Integer quantity;           // 数量

    @TableField("jiage")
    private BigDecimal unitPrice;       // 单价

    @TableField("zongjia")
    private BigDecimal totalPrice;      // 总价

    @TableField("lianxidianhua")
    private String contactPhone;        // 联系电话

    @TableField("shouhuodizhi")
    private String shippingAddress;     // 收货地址

    @TableField("zhuangtai")
    private Integer status;             // 状态 0待支付 1已支付 2已取消 3已发货 4已完成

    @TableField("fukuanfangshi")
    private Integer paymentMethod;      // 付款方式

    @TableField("beizhu")
    private String remark;              // 备注

    @TableField("tuanduiid")
    private Long groupBuyId;            // 团购ID（可选）

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime addTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer isDelete;
}
