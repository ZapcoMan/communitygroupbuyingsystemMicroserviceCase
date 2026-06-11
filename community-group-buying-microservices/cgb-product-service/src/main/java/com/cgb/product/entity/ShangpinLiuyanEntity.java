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

    private Long shangpinid;  // 商品ID
    private Long userid;      // 留言用户
    private String liuyanneirong; // 留言内容
    private Long parentid;    // 父留言ID
    private LocalDateTime addtime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatetime;

    @TableLogic
    private Integer isdelete;
}