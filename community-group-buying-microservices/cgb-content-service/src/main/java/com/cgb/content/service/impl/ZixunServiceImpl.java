package com.cgb.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.cgb.common.utils.*;
import com.cgb.content.dao.ZixunDao;
import com.cgb.content.entity.ZixunEntity;
import com.cgb.content.service.ZixunService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ZixunServiceImpl implements ZixunService {

    private final ZixunDao zixunDao;

    @Override
    public void save(ZixunEntity entity) { zixunDao.insert(entity); }

    @Override
    public void update(ZixunEntity entity) { zixunDao.updateById(entity); }

    @Override
    public void delete(Long id) { zixunDao.deleteById(id); }

    @Override
    public ZixunEntity getById(Long id) { return zixunDao.selectById(id); }

    @Override
    public IPage<ZixunEntity> queryPage(ZixunEntity params) {
        IPage<ZixunEntity> page = new Query<ZixunEntity>().getPage(
                CommonUtil.convert(params, Map.class));
        LambdaQueryWrapper<ZixunEntity> wrapper = new LambdaQueryWrapper<>();
        if (CommonUtil.isNotEmpty(params.getTitle())) {
            wrapper.like(ZixunEntity::getTitle, params.getTitle());
        }
        wrapper.orderByDesc(ZixunEntity::getId);
        return zixunDao.selectPage(page, wrapper);
    }
}