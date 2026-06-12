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
    private String picture;             // 封面图

    @TableField("parentid")
    private String parentId;            // 父帖子ID

    @TableField("userid")
    private Long userId;                // 发帖用户

    private String username;            // 用户名
    private String avatar;              // 头像

    @TableField("thumbsupnum")
    private Integer thumbUpCount;       // 点赞数

    @TableField("cainixihao")
    private Integer dislikeCount;       // 踩

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime addtime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatetime;

    @TableLogic
    private Integer isdelete;
}
