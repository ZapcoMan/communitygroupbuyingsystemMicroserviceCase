package com.cgb.groupbuy.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 团长表（发起团购的用户）
 */
@Data
@TableName("tuanwei")
public class TuanweiEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("mingcheng")
    private String groupName;           // 团购名称

    @TableField("tupian")
    private String picture;             // 团购图片

    @TableField("jieshao")
    private String description;         // 团购介绍

    @TableField("shangpinid")
    private Long productId;             // 关联商品ID

    @TableField("zhuangtai")
    private Integer status;             // 状态 0进行中 1已成团 2已过期

    @TableField("lirenjia")
    private Integer targetCount;        // 成团人数

    @TableField("xianxiarenshu")
    private Integer currentCount;       // 当前人数

    @TableField("yuanjia")
    private BigDecimal originalPrice;   // 原价

    @TableField("tejia")
    private BigDecimal groupPrice;      // 团购价

    @TableField("jieshushijian")
    private LocalDateTime endTime;      // 截止时间

    @TableField("userid")
    private Long userId;                // 团长用户ID

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime addtime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatetime;

    @TableLogic
    private Integer isdelete;
}
