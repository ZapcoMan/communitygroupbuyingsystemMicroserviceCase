package com.cgb.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.cgb.common.EIException;
import com.cgb.common.utils.*;
import com.cgb.content.dao.MessagesDao;
import com.cgb.content.entity.MessagesEntity;
import com.cgb.content.service.MessagesService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessagesServiceImpl implements MessagesService {

    private final MessagesDao messagesDao;

    @Override
    public void save(MessagesEntity entity) { messagesDao.insert(entity); }

    @Override
    public void delete(Long id) { messagesDao.deleteById(id); }

    @Override
    public IPage<MessagesEntity> queryPage(MessagesEntity params) {
        IPage<MessagesEntity> page = new Query<MessagesEntity>().getPage(
                CommonUtil.convert(params, Map.class));
        LambdaQueryWrapper<MessagesEntity> wrapper = new LambdaQueryWrapper<>();
        if (params.getUserid() != null) wrapper.eq(MessagesEntity::getUserid, params.getUserid());
        wrapper.isNull(MessagesEntity::getParentid);
        wrapper.orderByDesc(MessagesEntity::getId);
        return messagesDao.selectPage(page, wrapper);
    }

    @Override
    public void reply(Long id, String replyContent) {
        MessagesEntity entity = messagesDao.selectById(id);
        if (entity == null) throw new EIException("留言不存在");
        entity.setReplycontent(replyContent);
        messagesDao.updateById(entity);
    }
}