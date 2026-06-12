package com.cgb.content.service.impl;

import com.cgb.content.dao.NewsDao;
import com.cgb.content.entity.NewsEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("社区公告服务测试")
class NewsServiceImplTest {

    @Mock
    private NewsDao newsDao;

    @InjectMocks
    private NewsServiceImpl newsService;

    private NewsEntity buildNews(Long id) {
        NewsEntity entity = new NewsEntity();
        entity.setId(id);
        entity.setTitle("社区公告");
        entity.setContent("公告内容");
        entity.setCoverImage("/images/news/1.jpg");
        entity.setType("公告");
        entity.setPublishtime("2025-01-01");
        return entity;
    }

    @Nested
    @DisplayName("保存公告")
    class SaveTests {
        @Test
        @DisplayName("保存公告 - 成功")
        void save_success() {
            NewsEntity entity = buildNews(null);
            when(newsDao.insert(any(NewsEntity.class))).thenReturn(1);

            newsService.save(entity);

            verify(newsDao).insert(entity);
        }
    }

    @Nested
    @DisplayName("更新公告")
    class UpdateTests {
        @Test
        @DisplayName("更新公告 - 成功")
        void update_success() {
            NewsEntity entity = buildNews(1L);
            when(newsDao.updateById(any(NewsEntity.class))).thenReturn(1);

            newsService.update(entity);

            verify(newsDao).updateById(entity);
        }
    }

    @Nested
    @DisplayName("删除公告")
    class DeleteTests {
        @Test
        @DisplayName("删除公告 - 成功")
        void delete_success() {
            when(newsDao.deleteById(1L)).thenReturn(1);

            newsService.delete(1L);

            verify(newsDao).deleteById(1L);
        }
    }

    @Nested
    @DisplayName("查询公告")
    class GetByIdTests {
        @Test
        @DisplayName("根据ID查询 - 存在")
        void getById_exists() {
            NewsEntity expected = buildNews(1L);
            when(newsDao.selectById(1L)).thenReturn(expected);

            NewsEntity result = newsService.getById(1L);

            assertNotNull(result);
            assertEquals("社区公告", result.getTitle());
        }

        @Test
        @DisplayName("根据ID查询 - 不存在返回null")
        void getById_notFound_returnsNull() {
            when(newsDao.selectById(999L)).thenReturn(null);

            assertNull(newsService.getById(999L));
        }
    }
}
