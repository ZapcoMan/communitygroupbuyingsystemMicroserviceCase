package com.cgb.content.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.cgb.content.entity.MessagesEntity;

public interface MessagesService {
    void save(MessagesEntity entity);
    void delete(Long id);
    IPage<MessagesEntity> queryPage(MessagesEntity params);
    void reply(Long id, String replyContent);
}