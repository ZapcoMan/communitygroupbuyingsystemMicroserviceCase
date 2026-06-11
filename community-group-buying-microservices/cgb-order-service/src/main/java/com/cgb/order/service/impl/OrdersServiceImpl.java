package com.cgb.order.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.cgb.common.EIException;
import com.cgb.common.ErrorCode;
import com.cgb.common.utils.*;
import com.cgb.order.dao.OrdersDao;
import com.cgb.order.entity.OrdersEntity;
import com.cgb.order.service.OrdersService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrdersServiceImpl implements OrdersService {

    private final OrdersDao ordersDao;

    @Override
    public void save(OrdersEntity entity) {
        if (entity.getOrderid() == null || "".equals(entity.getOrderid())) {
            entity.setOrderid(CommonUtil.generateOrderId());
        }
        if (entity.getZhuangtai() == null) entity.setZhuangtai(0);
        ordersDao.insert(entity);
    }

    @Override
    public void update(OrdersEntity entity) {
        if (entity.getId() == null) throw new EIException("订单ID不能为空");
        ordersDao.updateById(entity);
    }

    @Override
    public void delete(Long id) { ordersDao.deleteById(id); }

    @Override
    public OrdersEntity getById(Long id) {
        OrdersEntity entity = ordersDao.selectById(id);
        if (entity == null) throw new EIException(ErrorCode.RESOURCE_NOT_FOUND);
        return entity;
    }

    @Override
    public OrdersEntity getByOrderId(String orderId) {
        OrdersEntity entity = ordersDao.selectOne(new LambdaQueryWrapper<OrdersEntity>()
                .eq(OrdersEntity::getOrderid, orderId));
        if (entity == null) throw new EIException(ErrorCode.RESOURCE_NOT_FOUND);
        return entity;
    }

    @Override
    public IPage<OrdersEntity> queryPage(OrdersEntity params) {
        IPage<OrdersEntity> page = new Query<OrdersEntity>().getPage(
                CommonUtil.convert(params, Map.class));
        LambdaQueryWrapper<OrdersEntity> wrapper = new LambdaQueryWrapper<>();
        if (params.getUserid() != null) wrapper.eq(OrdersEntity::getUserid, params.getUserid());
        if (params.getZhuangtai() != null) wrapper.eq(OrdersEntity::getZhuangtai, params.getZhuangtai());
        if (CommonUtil.isNotEmpty(params.getOrderid())) wrapper.like(OrdersEntity::getOrderid, params.getOrderid());
        wrapper.orderByDesc(OrdersEntity::getId);
        return ordersDao.selectPage(page, wrapper);
    }

    @Override
    public void cancel(String orderId, Long userId) {
        OrdersEntity order = ordersDao.selectOne(new LambdaQueryWrapper<OrdersEntity>()
                .eq(OrdersEntity::getOrderid, orderId)
                .eq(OrdersEntity::getUserid, userId));
        if (order == null) throw new EIException(ErrorCode.RESOURCE_NOT_FOUND);
        if (order.getZhuangtai() != 0) throw new EIException("只能取消待支付订单");
        order.setZhuangtai(2);
        ordersDao.updateById(order);
    }

    @Override
    public void pay(String orderId) {
        OrdersEntity order = ordersDao.selectOne(new LambdaQueryWrapper<OrdersEntity>()
                .eq(OrdersEntity::getOrderid, orderId));
        if (order == null) throw new EIException(ErrorCode.RESOURCE_NOT_FOUND);
        if (order.getZhuangtai() != 0) throw new EIException("订单状态不允许支付");
        order.setZhuangtai(1);
        ordersDao.updateById(order);
    }
}