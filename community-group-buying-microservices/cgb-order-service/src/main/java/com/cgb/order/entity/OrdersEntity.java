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
    private String orderid;      // 订单编号
    private Long userid;         // 用户ID
    private Long shangpinid;     // 商品ID
    private String shangpinming; // 商品名称
    private String shangpintupian; // 商品图片
    private Integer shuliang;   // 数量
    private BigDecimal jiage;   // 单价
    private BigDecimal zongjia;  // 总价
    private String lianxidianhua; // 联系电话
    private String shouhuodizhi;  // 收货地址
    private Integer zhuangtai;  // 状态 0待支付 1已支付 2已取消 3已发货 4已完成
    private Integer fukuanfangshi; // 付款方式
    private String beizhu;      // 备注
    private Long tuanduiid;     // 团购ID（可选）

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime addtime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatetime;

    @TableLogic
    private Integer isdelete;
}