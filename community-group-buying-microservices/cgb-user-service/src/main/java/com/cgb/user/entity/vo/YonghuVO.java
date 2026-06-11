package com.cgb.user.entity.vo;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 用户视图对象（脱敏）
 */
@Data
public class YonghuVO {
    private Long id;
    private String zhanghao;
    private String xingming;
    private String xingbie;
    private String shouji;
    private String youxiang;
    private String touxiang;
    private Double jifen;
    private Double yue;
    private Integer status;
    private LocalDateTime addtime;
}