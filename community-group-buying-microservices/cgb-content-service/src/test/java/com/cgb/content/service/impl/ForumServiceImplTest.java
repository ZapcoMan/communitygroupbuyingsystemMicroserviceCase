package com.cgb.content.service.impl;

import com.cgb.common.EIException;
import com.cgb.content.dao.ForumDao;
import com.cgb.content.entity.ForumEntity;
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
@DisplayName("论坛帖子服务测试")
class ForumServiceImplTest {

    @Mock
    private ForumDao forumDao;

    @InjectMocks
    private ForumServiceImpl forumService;

    private ForumEntity buildForum(Long id, Integer thumbsup, Integer caixihao) {
        ForumEntity entity = new ForumEntity();
        entity.setId(id);
        entity.setTitle("测试帖子");
        entity.setContent("帖子内容");
        entity.setUserid(100L);
        entity.setUsername("testuser");
        entity.setThumbsupnum(thumbsup);
        entity.setCainixihao(caixihao);
        return entity;
    }

    @Nested
    @DisplayName("保存帖子")
    class SaveTests {
        @Test
        @DisplayName("保存帖子 - 未指定点赞和踩时默认设为0")
        void save_noDefaults_setToZero() {
            ForumEntity entity = buildForum(null, null, null);
            when(forumDao.insert(any(ForumEntity.class))).thenReturn(1);

            forumService.save(entity);

            assertEquals(0, entity.getThumbsupnum(), "默认点赞数应为0");
            assertEquals(0, entity.getCainixihao(), "默认踩数应为0");
            verify(forumDao).insert(entity);
        }

        @Test
        @DisplayName("保存帖子 - 已有值时保留原值")
        void save_withValues_keepOriginal() {
            ForumEntity entity = buildForum(null, 5, 2);
            when(forumDao.insert(any(ForumEntity.class))).thenReturn(1);

            forumService.save(entity);

            assertEquals(5, entity.getThumbsupnum());
            assertEquals(2, entity.getCainixihao());
            verify(forumDao).insert(entity);
        }
    }

    @Nested
    @DisplayName("更新帖子")
    class UpdateTests {
        @Test
        @DisplayName("更新帖子 - 成功")
        void update_success() {
            ForumEntity entity = buildForum(1L, 3, 1);
            when(forumDao.updateById(any(ForumEntity.class))).thenReturn(1);

            forumService.update(entity);

            verify(forumDao).updateById(entity);
        }
    }

    @Nested
    @DisplayName("删除帖子")
    class DeleteTests {
        @Test
        @DisplayName("删除帖子 - 成功")
        void delete_success() {
            when(forumDao.deleteById(1L)).thenReturn(1);

            forumService.delete(1L);

            verify(forumDao).deleteById(1L);
        }
    }

    @Nested
    @DisplayName("查询帖子")
    class GetByIdTests {
        @Test
        @DisplayName("根据ID查询 - 存在")
        void getById_exists() {
            ForumEntity expected = buildForum(1L, 10, 2);
            when(forumDao.selectById(1L)).thenReturn(expected);

            ForumEntity result = forumService.getById(1L);

            assertNotNull(result);
            assertEquals("测试帖子", result.getTitle());
        }

        @Test
        @DisplayName("根据ID查询 - 不存在返回null")
        void getById_notFound_returnsNull() {
            when(forumDao.selectById(999L)).thenReturn(null);

            assertNull(forumService.getById(999L));
        }
    }

    @Nested
    @DisplayName("点赞")
    class ThumbUpTests {
        @Test
        @DisplayName("点赞 - 成功增加点赞数")
        void thumbUp_success() {
            ForumEntity entity = buildForum(1L, 5, 0);
            when(forumDao.selectById(1L)).thenReturn(entity);
            when(forumDao.updateById(any(ForumEntity.class))).thenReturn(1);

            forumService.thumbUp(1L);

            ArgumentCaptor<ForumEntity> captor = ArgumentCaptor.forClass(ForumEntity.class);
            verify(forumDao).updateById(captor.capture());
            assertEquals(6, captor.getValue().getThumbsupnum(), "点赞数应从5变为6");
        }

        @Test
        @DisplayName("点赞 - 帖子不存在抛出异常")
        void thumbUp_notFound_throwsException() {
            when(forumDao.selectById(999L)).thenReturn(null);

            EIException ex = assertThrows(EIException.class,
                    () -> forumService.thumbUp(999L));
            assertEquals("帖子不存在", ex.getMessage());
        }
    }
}
