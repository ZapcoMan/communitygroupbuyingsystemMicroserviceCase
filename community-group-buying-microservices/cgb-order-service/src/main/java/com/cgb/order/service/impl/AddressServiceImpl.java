package com.cgb.order.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.cgb.common.EIException;
import com.cgb.common.utils.*;
import com.cgb.order.dao.AddressDao;
import com.cgb.order.entity.AddressEntity;
import com.cgb.order.service.AddressService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {

    private final AddressDao addressDao;

    @Override
    public void save(AddressEntity entity) {
        if (entity.getIsdefault() == null) entity.setIsdefault(0);
        addressDao.insert(entity);
    }

    @Override
    public void update(AddressEntity entity) { addressDao.updateById(entity); }

    @Override
    public void delete(Long id) { addressDao.deleteById(id); }

    @Override
    public AddressEntity getById(Long id) { return addressDao.selectById(id); }

    @Override
    public IPage<AddressEntity> queryPage(AddressEntity params) {
        IPage<AddressEntity> page = new Query<AddressEntity>().getPage(
                CommonUtil.convert(params, Map.class));
        return addressDao.selectPage(page, new LambdaQueryWrapper<AddressEntity>()
                .eq(params.getUserid() != null, AddressEntity::getUserid, params.getUserid())
                .orderByDesc(AddressEntity::getId));
    }

    @Override
    @Transactional
    public void setDefault(Long id, Long userId) {
        // 取消该用户所有默认地址
        addressDao.update(null, new com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper<AddressEntity>()
                .eq(AddressEntity::getUserid, userId)
                .set(AddressEntity::getIsdefault, 0));
        // 设置新默认地址
        addressDao.update(null, new com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper<AddressEntity>()
                .eq(AddressEntity::getId, id)
                .set(AddressEntity::getIsdefault, 1));
    }
}