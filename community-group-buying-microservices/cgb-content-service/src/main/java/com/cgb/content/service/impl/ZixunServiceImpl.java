package com.cgb.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.cgb.common.utils.*;
import com.cgb.content.dao.InformationDao;
import com.cgb.content.entity.InformationEntity;
import com.cgb.content.service.InformationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class InformationServiceImpl implements InformationService {

    private final InformationDao zixunDao;

    @Override
    public void save(InformationEntity entity) { zixunDao.insert(entity); }

    @Override
    public void update(InformationEntity entity) { zixunDao.updateById(entity); }

    @Override
    public void delete(Long id) { zixunDao.deleteById(id); }

    @Override
    public InformationEntity getById(Long id) { return zixunDao.selectById(id); }

    @Override
    public IPage<InformationEntity> queryPage(InformationEntity params) {
        IPage<InformationEntity> page = new Query<InformationEntity>().getPage(
                CommonUtil.convert(params, Map.class));
        LambdaQueryWrapper<InformationEntity> wrapper = new LambdaQueryWrapper<>();
        if (CommonUtil.isNotEmpty(params.getTitle())) {
            wrapper.like(InformationEntity::getTitle, params.getTitle());
        }
        wrapper.orderByDesc(InformationEntity::getId);
        return zixunDao.selectPage(page, wrapper);
    }
}