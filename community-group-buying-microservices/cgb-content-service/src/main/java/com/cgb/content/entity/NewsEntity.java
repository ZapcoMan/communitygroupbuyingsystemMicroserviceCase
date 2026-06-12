package com.cgb.content.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 社区公告实体
 */
@Data
@TableName("news")
public class NewsEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;
    private String title;          // 标题
    private String content;         // 内容

    @TableField("picture")
    private String coverImage;      // 封面图

    private String type;            // 类型

    @TableField("publishtime")
    private String publishTime;     // 发布时间

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime addTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer isDelete;
}
