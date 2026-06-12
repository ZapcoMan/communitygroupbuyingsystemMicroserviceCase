package com.cgb.user.entity.vo;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 用户视图对象（脱敏，英文字段对外暴露�? */
@Data
public class MemberVO {
    private Long id;
    private String account;       // 账号
    private String realName;      // 姓名
    private String gender;        // 性别
    private String phone;         // 手机
    private String email;         // 邮箱
    private String avatar;        // 头像
    private Double points;        // 积分
    private Double balance;       // 余额
    private Integer status;       // 状�?    private LocalDateTime addTime;
}
