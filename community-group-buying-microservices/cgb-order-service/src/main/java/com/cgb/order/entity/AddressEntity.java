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
    private String addressName;         // 地址名称

    @TableField("lianxidianhua")
    private String contactPhone;        // 联系电话

    @TableField("shouhuoren")
    private String consignee;           // 收货人

    private String provinces;           // 省
    private String citys;               // 市
    private String areas;               // 区/县

    private String detailedaddress;     // 详细地址

    @TableField("isdefault")
    private Integer isDefault;          // 是否默认 0否 1是

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime addtime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatetime;

    @TableLogic
    private Integer isdelete;
}
