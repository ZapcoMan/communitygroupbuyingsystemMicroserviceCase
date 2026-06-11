package com.cgb.product.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 商品评价实体
 */
@Data
@TableName("shangpin_comment")
public class ShangpinCommentEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long shangpinid; // 商品ID
    private Long userid;     // 评论用户
    private Integer pingfen; // 评分 1-5
    private String pingjianeirong; // 评价内容
    private Long parentid;  // 父评价ID（回复）
    private LocalDateTime addtime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatetime;

    @TableLogic
    private Integer isdelete;
}