package com.cgb.content.service.impl;

import com.cgb.common.EIException;
import com.cgb.content.dao.MessagesDao;
import com.cgb.content.entity.MessagesEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("留言板服务测试")
class MessagesServiceImplTest {

    @Mock
    private MessagesDao messagesDao;

    @InjectMocks
    private MessagesServiceImpl messagesService;

    private MessagesEntity buildMessage(Long id, Long userId) {
        MessagesEntity entity = new MessagesEntity();
        entity.setId(id);
        entity.setUserId(userId);
        entity.setUsername("testuser");
        entity.setContent("这是一条留言");
        return entity;
    }

    @Nested
    @DisplayName("保存留言")
    class SaveTests {
        @Test
        @DisplayName("保存留言 - 成功")
        void save_success() {
            MessagesEntity entity = buildMessage(null, 100L);
            when(messagesDao.insert(any(MessagesEntity.class))).thenReturn(1);

            messagesService.save(entity);

            verify(messagesDao).insert(entity);
        }
    }

    @Nested
    @DisplayName("删除留言")
    class DeleteTests {
        @Test
        @DisplayName("删除留言 - 成功")
        void delete_success() {
            when(messagesDao.deleteById(1L)).thenReturn(1);

            messagesService.delete(1L);

            verify(messagesDao).deleteById(1L);
        }
    }

    @Nested
    @DisplayName("回复留言")
    class ReplyTests {
        @Test
        @DisplayName("回复留言 - 成功")
        void reply_success() {
            MessagesEntity entity = buildMessage(1L, 100L);
            when(messagesDao.selectById(1L)).thenReturn(entity);
            when(messagesDao.updateById(any(MessagesEntity.class))).thenReturn(1);

            messagesService.reply(1L, "感谢您的反馈");

            ArgumentCaptor<MessagesEntity> captor = ArgumentCaptor.forClass(MessagesEntity.class);
            verify(messagesDao).updateById(captor.capture());
            assertEquals("感谢您的反馈", captor.getValue().getReplycontent());
        }

        @Test
        @DisplayName("回复留言 - 留言不存在抛出异常")
        void reply_notFound_throwsException() {
            when(messagesDao.selectById(999L)).thenReturn(null);

            EIException ex = assertThrows(EIException.class,
                    () -> messagesService.reply(999L, "回复内容"));
            assertEquals("留言不存在", ex.getMessage());
        }
    }
}
