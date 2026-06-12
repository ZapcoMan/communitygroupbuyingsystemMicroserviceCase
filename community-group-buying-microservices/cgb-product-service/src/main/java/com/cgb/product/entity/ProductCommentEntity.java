package com.cgb.product.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 商品评价实体
 */
@Data
@TableName("product_comment")
public class ProductCommentEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("shangpinid")
    private Long productId;             // 商品ID

    @TableField("userid")
    private Long userId;                // 评论用户

    @TableField("pingfen")
    private Integer rating;             // 评分 1-5

    @TableField("pingjianeirong")
    private String reviewContent;       // 评价内容

    @TableField("parentid")
    private Long parentId;              // 父评价ID（回复）

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime addTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer isDelete;
}
