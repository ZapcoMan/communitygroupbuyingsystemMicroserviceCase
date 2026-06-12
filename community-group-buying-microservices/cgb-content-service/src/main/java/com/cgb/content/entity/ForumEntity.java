package com.cgb.content.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 论坛帖子实体
 */
@Data
@TableName("forum")
public class ForumEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;
    private String title;               // 标题
    private String content;             // 内容

    @TableField("picture")
    private String coverImage;          // 封面�?
    @TableField("parentid")
    private String parentId;            // 父帖子ID

    @TableField("userid")
    private Long userId;                // 发帖用户

    private String username;            // 用户?
     private String avatar;              // 头像

    @TableField("thumbsupnum")
    private Integer likeCount;          // 点赞�?
    @TableField("cainixihao")
    private Integer dislikeCount;       // �?
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime addTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer isDelete;
}
