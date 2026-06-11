package com.cgb.content.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 留言板实体
 */
@Data
@TableName("messages")
public class MessagesEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userid;         // 留言用户
    private String username;     // 用户名
    private String content;      // 留言内容
    private Long parentid;      // 父留言ID（回复）
    private String replycontent; // 回复内容

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime addtime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatetime;

    @TableLogic
    private Integer isdelete;
}