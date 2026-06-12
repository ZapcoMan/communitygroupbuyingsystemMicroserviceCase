package com.cgb.content.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("config")
public class ConfigEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;   // 配置名称
    private String value;  // 配置值

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime addtime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatetime;

    @TableLogic
    private Integer isdelete;
}
