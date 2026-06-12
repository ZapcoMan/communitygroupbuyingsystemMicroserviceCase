package com.cgb.order.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.cgb.common.EIException;
import com.cgb.common.feign.FeignProductService;
import com.cgb.common.utils.*;
import com.cgb.order.dao.CartDao;
import com.cgb.order.entity.CartEntity;
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
        CartEntity exist = cartDao.selectOne(new LambdaQueryWrapper<CartEntity>()
                .eq(CartEntity::getUserId, entity.getUserId())
                .eq(CartEntity::getProductId, entity.getProductId()));
        if (exist != null) {
            exist.setQuantity(exist.getQuantity() + entity.getQuantity());
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
                .eq(params.getUserId() != null, CartEntity::getUserId, params.getUserId())
                .orderByDesc(CartEntity::getId));
    }

    @Override
    public void clear(Long userId) {
        cartDao.delete(new LambdaQueryWrapper<CartEntity>().eq(CartEntity::getUserId, userId));
    }

    @Override
    @GlobalTransactional(name = "cgb-cart-checkout", rollbackFor = Exception.class)
    public List<OrderVO> checkout(Long userId) {
        List<CartEntity> cartItems = cartDao.selectList(
                new LambdaQueryWrapper<CartEntity>().eq(CartEntity::getUserId, userId));
        if (cartItems.isEmpty()) throw new EIException("购物车为空");

        List<OrderVO> orders = new ArrayList<>();
        for (CartEntity item : cartItems) {
            CreateOrderDTO dto = new CreateOrderDTO();
            dto.setProductId(item.getProductId());
            dto.setQuantity(item.getQuantity());
            OrderVO vo = ordersService.createOrderFromDTO(dto, userId);
            orders.add(vo);
            log.info("购物车结算 - 订单创建成功: userId={}, productId={}, quantity={}",
                    userId, item.getProductId(), item.getQuantity());
        }
        clear(userId);
        log.info("购物车结算完成: userId={}, 共创建{}个订单", userId, orders.size());
        return orders;
    }
}
