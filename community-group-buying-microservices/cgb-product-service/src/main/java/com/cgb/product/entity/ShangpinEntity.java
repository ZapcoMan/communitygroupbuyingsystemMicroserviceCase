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

    private String mingcheng;        // 商品名称
    private String leixing;         // 商品类型
    private String tupian;          // 商品图片
    private String jieshao;         // 商品介绍
    private String tihuofangshi;   // 提货方式
    private Integer kucun;          // 库存
    private BigDecimal jiage;       // 价格
    private BigDecimal yuanjia;     // 原价
    private Integer status;         // 状态 0上架 1下架
    private Long userid;            // 所属商家用户ID

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime addtime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatetime;

    @TableLogic
    private Integer isdelete;
}