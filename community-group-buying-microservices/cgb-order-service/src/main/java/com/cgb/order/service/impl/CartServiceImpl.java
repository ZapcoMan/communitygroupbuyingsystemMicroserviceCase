package com.cgb.order.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.cgb.common.EIException;
import com.cgb.common.utils.*;
import com.cgb.order.dao.CartDao;
import com.cgb.order.entity.CartEntity;
import com.cgb.order.service.CartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartDao cartDao;

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
}