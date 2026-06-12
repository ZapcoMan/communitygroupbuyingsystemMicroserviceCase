package com.cgb.order.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.cgb.common.EIException;
import com.cgb.common.feign.FeignProductService;
import com.cgb.common.utils.*;
import com.cgb.order.dao.CartDao;
import com.cgb.order.entity.CartEntity;
import com.cgb.order.entity.OrdersEntity;
import com.cgb.order.entity.dto.CreateOrderDTO;
import com.cgb.order.entity.vo.OrderVO;
import com.cgb.order.service.CartService;
import com.cgb.order.service.OrdersService;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartDao cartDao;
    private final OrdersService ordersService;
    private final FeignProductService feignProductService;

    @Override
    public void save(CartEntity entity) {
        // 检查是否已存在同一用户同一商品
        CartEntity exist = cartDao.selectOne(new LambdaQueryWrapper<CartEntity>()
                .eq(CartEntity::getUserid, entity.getUserid())
                .eq(CartEntity::getShangpinid, entity.getShangpinid()));
        if (exist != null) {
            exist.setShuliang(exist.getShuliang() + entity.getShuliang());
            cartDao.updateById(exist);
        } else {
            cartDao.insert(entity);
        }
    }

    @Override
    public void update(CartEntity entity) { cartDao.updateById(entity); }

    @Override
    public void delete(Long id) { cartDao.deleteById(id); }

    @Override
    public IPage<CartEntity> queryPage(CartEntity params) {
        IPage<CartEntity> page = new Query<CartEntity>().getPage(
                CommonUtil.convert(params, Map.class));
        return cartDao.selectPage(page, new LambdaQueryWrapper<CartEntity>()
                .eq(params.getUserid() != null, CartEntity::getUserid, params.getUserid())
                .orderByDesc(CartEntity::getId));
    }

    @Override
    public void clear(Long userId) {
        cartDao.delete(new LambdaQueryWrapper<CartEntity>().eq(CartEntity::getUserid, userId));
    }

    /**
     * 购物车结算（Seata分布式事务：批量创建订单 + 扣库存 + 清空购物车）
     */
    @Override
    @GlobalTransactional(name = "cgb-cart-checkout", rollbackFor = Exception.class)
    public List<OrderVO> checkout(Long userId) {
        // 1. 查询用户购物车所有商品
        List<CartEntity> cartItems = cartDao.selectList(
                new LambdaQueryWrapper<CartEntity>().eq(CartEntity::getUserid, userId));
        if (cartItems.isEmpty()) throw new EIException("购物车为空");

        List<OrderVO> orders = new ArrayList<>();

        // 2. 逐个商品创建订单（每个商品一个订单）
        for (CartEntity item : cartItems) {
            CreateOrderDTO dto = new CreateOrderDTO();
            dto.setProductId(item.getShangpinid());
            dto.setQuantity(item.getShuliang());
            OrderVO vo = ordersService.createOrderFromDTO(dto, userId);
            orders.add(vo);
            log.info("购物车结算 - 订单创建成功: userId={}, productId={}, quantity={}",
                    userId, item.getShangpinid(), item.getShuliang());
        }

        // 3. 清空购物车
        clear(userId);

        log.info("购物车结算完成: userId={}, 共创建{}个订单", userId, orders.size());
        return orders;
    }
}
