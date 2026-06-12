package com.cgb.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.cgb.common.EIException;
import com.cgb.common.utils.*;
import com.cgb.content.dao.MessageBoardDao;
import com.cgb.content.entity.MessageBoardEntity;
import com.cgb.content.service.MessageBoardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageBoardServiceImpl implements MessageBoardService {

    private final MessageBoardDao messagesDao;

    @Override
    public void save(MessageBoardEntity entity) { messagesDao.insert(entity); }

    @Override
    public void delete(Long id) { messagesDao.deleteById(id); }

    @Override
    public IPage<MessageBoardEntity> queryPage(MessageBoardEntity params) {
        IPage<MessageBoardEntity> page = new Query<MessageBoardEntity>().getPage(
                CommonUtil.convert(params, Map.class));
        LambdaQueryWrapper<MessageBoardEntity> wrapper = new LambdaQueryWrapper<>();
        if (params.getUserId() != null) wrapper.eq(MessageBoardEntity::getUserId, params.getUserId());
        wrapper.isNull(MessageBoardEntity::getParentId);
        wrapper.orderByDesc(MessageBoardEntity::getId);
        return messagesDao.selectPage(page, wrapper);
    }

    @Override
    public void reply(Long id, String replyContent) {
        MessageBoardEntity entity = messagesDao.selectById(id);
        if (entity == null) throw new EIException("留言不存在");
        entity.setReplyContent(replyContent);
        messagesDao.updateById(entity);
    }
}
