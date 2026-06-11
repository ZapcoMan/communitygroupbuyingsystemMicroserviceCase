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
public class YonghuEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private String zhanghao;    // 账号
    private String mima;         // 密码（BCrypt 加密存储）
    private String xingming;    // 姓名
    private String xingbie;     // 性别
    private String shouji;      // 手机
    private String youxiang;    // 邮箱
    private String touxiang;    // 头像
    private Double jifen;       // 积分
    private Double yue;         // 余额
    private Integer status;     // 账号状态 0-正常 1-禁用

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime addtime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatetime;

    @TableLogic
    private Integer isdelete;   // 逻辑删除
}