package com.cgb.product.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 商品收藏实体
 */
@Data
@TableName("shangpin_collection")
public class ShangpinCollectionEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userid;     // 收藏用户
    private Long shangpinid; // 商品ID
    private LocalDateTime addtime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatetime;

    @TableLogic
    private Integer isdelete;
}