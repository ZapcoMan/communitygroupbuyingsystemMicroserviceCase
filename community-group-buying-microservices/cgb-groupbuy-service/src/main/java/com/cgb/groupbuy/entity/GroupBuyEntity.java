package com.cgb.groupbuy.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 团购信息表（参团记录）
 */
@Data
@TableName("group_info")
public class GroupBuyEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("tuanduiid")
    private Long groupBuyId;            // 团购ID（关联tuanwei）

    @TableField("userid")
    private Long userId;                // 参团用户

    @TableField("shangpinid")
    private Long productId;             // 商品ID

    @TableField("shuliang")
    private Integer quantity;           // 购买数量

    @TableField("jiage")
    private BigDecimal price;           // 购买价格

    @TableField("zhuangtai")
    private Integer status;             // 状态 0待支付 1已支付 2已取消

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime addTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer isDelete;
}
