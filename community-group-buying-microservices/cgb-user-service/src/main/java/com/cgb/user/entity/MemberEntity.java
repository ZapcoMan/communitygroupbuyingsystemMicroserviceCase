package com.cgb.user.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户实体（买家端）
 */
@Data
@TableName("yonghu")
public class MemberEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("zhanghao")
    private String account;             // 账号

    @TableField("mima")
    private String password;            // 密码（BCrypt 加密存储）

    @TableField("xingming")
    private String realName;            // 姓名

    @TableField("xingbie")
    private String gender;              // 性别

    @TableField("shouji")
    private String phone;               // 手机

    @TableField("youxiang")
    private String email;               // 邮箱

    @TableField("touxiang")
    private String avatar;              // 头像

    @TableField("jifen")
    private Double points;              // 积分

    @TableField("yue")
    private Double balance;             // 余额

    private Integer status;             // 账号状态 0-正常 1-禁用

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime addTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer isDelete;           // 逻辑删除
}
