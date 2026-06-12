package com.cgb.groupbuy.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("tuan_comment")
public class TuanCommentEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("tuanweiid")
    private Long groupBuyId;            // 团购ID

    @TableField("userid")
    private Long userId;                // 评论用户

    private String nickname;            // 评论用户昵称
    private String content;             // 评论内容
    private String reply;               // 回复内容
    private String tablename;           // 类型标识

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime addtime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatetime;

    @TableLogic
    private Integer isdelete;
}
