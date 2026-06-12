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
public class MessageBoardEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("userid")
    private Long userId;              // 留言用户
    private String username;          // 用户名
    private String content;           // 留言内容

    @TableField("parentid")
    private Long parentId;            // 父留言ID（回复）

    @TableField("replycontent")
    private String replyContent;      // 回复内容

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime addTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer isDelete;
}
