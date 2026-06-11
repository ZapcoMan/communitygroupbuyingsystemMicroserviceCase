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
    private String mingcheng;      // 团购名称
    private String tupian;         // 团购图片
    private String jieshao;        // 团购介绍
    private Long shangpinid;       // 关联商品ID
    private Integer zhuangtai;     // 状态 0进行中 1已成团 2已过期
    private Integer lirenjia;     // 成团人数
    private Integer xianxiarenshu; // 当前人数
    private BigDecimal yuanjia;   // 原价
    private BigDecimal tejia;     // 团购价
    private LocalDateTime jieshushijian; // 截止时间
    private Long userid;          // 团长用户ID

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime addtime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatetime;

    @TableLogic
    private Integer isdelete;
}