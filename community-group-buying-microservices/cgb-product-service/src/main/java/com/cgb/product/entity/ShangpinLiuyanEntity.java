package com.cgb.product.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 商品留言实体
 */
@Data
@TableName("shangpin_liuyan")
public class ShangpinLiuyanEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("shangpinid")
    private Long productId;             // 商品ID

    @TableField("userid")
    private Long userId;                // 留言用户

    @TableField("liuyanneirong")
    private String messageContent;      // 留言内容

    @TableField("parentid")
    private Long parentId;              // 父留言ID

    private LocalDateTime addtime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatetime;

    @TableLogic
    private Integer isdelete;
}
