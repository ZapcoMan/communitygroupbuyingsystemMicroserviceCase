package com.cgb.product.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 商品实体
 */
@Data
@TableName("shangpin")
public class ShangpinEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("mingcheng")
    private String productName;         // 商品名称

    @TableField("leixing")
    private String category;            // 商品类型

    @TableField("tupian")
    private String picture;             // 商品图片

    @TableField("jieshao")
    private String description;         // 商品介绍

    @TableField("tihuofangshi")
    private String pickupMethod;        // 提货方式

    @TableField("kucun")
    private Integer stock;              // 库存

    @TableField("jiage")
    private BigDecimal price;           // 价格

    @TableField("yuanjia")
    private BigDecimal originalPrice;   // 原价

    private Integer status;             // 状态 0上架 1下架

    @TableField("userid")
    private Long merchantId;            // 所属商家用户ID

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime addtime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatetime;

    @TableLogic
    private Integer isdelete;
}
