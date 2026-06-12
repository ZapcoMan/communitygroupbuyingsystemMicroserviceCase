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
@TableName("group_slot")
public class GroupSlotEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("mingcheng")
    private String groupName;           // 团购名称

    @TableField("tupian")
    private String coverImage;          // 团购图片

    @TableField("jieshao")
    private String description;         // 团购介绍

    @TableField("shangpinid")
    private Long productId;             // 关联商品ID

    @TableField("zhuangtai")
    private Integer status;             // 状态 0进行中 1已成团 2已过期

    @TableField("lirenjia")
    private Integer targetMemberCount;  // 成团人数

    @TableField("xianxiarenshu")
    private Integer currentMemberCount; // 当前人数

    @TableField("yuanjia")
    private BigDecimal originalPrice;   // 原价

    @TableField("tejia")
    private BigDecimal groupPrice;      // 团购价

    @TableField("jieshushijian")
    private LocalDateTime endTime;      // 截止时间

    @TableField("userid")
    private Long leaderUserId;          // 团长用户ID

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime addTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer isDelete;
}
