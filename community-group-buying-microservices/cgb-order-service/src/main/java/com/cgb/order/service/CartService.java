package com.cgb.order.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.cgb.order.entity.CartEntity;
import com.cgb.order.entity.vo.OrderVO;

import java.util.List;

public interface CartService {
    void save(CartEntity entity);
    void update(CartEntity entity);
    void delete(Long id);
    IPage<CartEntity> queryPage(CartEntity params);
    void clear(Long userId);

    /** 购物车结算（批量创建订单，Seata分布式事务） */
    List<OrderVO> checkout(Long userId);
}
