package com.cgb.order.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 收货地址实体
 */
@Data
@TableName("address")
public class AddressEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("userid")
    private Long userId;                // 用户ID

    @TableField("dizhimingchen")
    private String addressLabel;        // 地址名称（如"家"、"公司"）

    @TableField("lianxidianhua")
    private String contactPhone;        // 联系电话

    @TableField("shouhuoren")
    private String receiverName;        // 收货人

    @TableField("provinces")
    private String province;            // 省

    @TableField("citys")
    private String city;                // 市

    @TableField("areas")
    private String district;            // 区/县

    @TableField("detailedaddress")
    private String detailAddress;       // 详细地址

    @TableField("isdefault")
    private Integer isDefault;          // 是否默认 0否 1是

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime addTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer isDelete;
}
