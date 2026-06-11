package com.cgb.order.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 购物车实体
 */
@Data
@TableName("cart")
public class CartEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userid;     // 用户ID
    private Long shangpinid; // 商品ID
    private Integer shuliang; // 数量

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime addtime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatetime;

    @TableLogic
    private Integer isdelete;
}