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
@TableName("tuanxinxi")
public class TuanxinxiEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long tuanduiid;      // 团购ID（关联tuanwei）
    private Long userid;         // 参团用户
    private Long shangpinid;     // 商品ID
    private Integer shuliang;    // 购买数量
    private BigDecimal jiage;    // 购买价格
    private Integer zhuangtai;   // 状态 0待支付 1已支付 2已取消

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime addtime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatetime;

    @TableLogic
    private Integer isdelete;
}