package com.cgb.content.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.cgb.content.entity.MessageBoardEntity;

public interface MessageBoardService {
    void save(MessageBoardEntity entity);
    void delete(Long id);
    IPage<MessageBoardEntity> queryPage(MessageBoardEntity params);
    void reply(Long id, String replyContent);
}