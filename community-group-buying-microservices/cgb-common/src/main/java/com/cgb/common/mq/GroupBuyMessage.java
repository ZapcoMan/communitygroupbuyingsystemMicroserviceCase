package com.cgb.common.mq;

import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 团购状态变更消�? */
@Data
public class GroupBuyMessage implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 团购ID（tuanwei.id�?*/
    private Long groupBuyId;
    /** 团长用户ID */
    private Long leaderUserId;
    /** 参团用户ID */
    private Long joinUserId;
    /** 商品ID */
    private Long productId;
    /** 购买数量 */
    private Integer quantity;
    /** 团购�?*/
    private BigDecimal groupPrice;
    /** 成团人数 */
    private Integer targetMemberCount;
    /** 当前人数 */
    private Integer currentMemberCount;
    /** 状态：0参团 1成团 2过期 */
    private Integer status;
}
