package com.cgb.content.service.impl;

import com.cgb.content.dao.InformationDao;
import com.cgb.content.entity.InformationEntity;
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
@DisplayName("团购资讯服务测试")
class InformationServiceImplTest {

    @Mock
    private InformationDao zixunDao;

    @InjectMocks
    private InformationServiceImpl zixunService;

    private InformationEntity buildZixun(Long id) {
        InformationEntity entity = new InformationEntity();
        entity.setId(id);
        entity.setTitle("团购资讯");
        entity.setContent("资讯内容详情");
        entity.setCoverImage("/images/zixun/1.jpg");
        entity.setSource("官方");
        entity.setPublishtime("2025-01-01");
        return entity;
    }

    @Nested
    @DisplayName("保存资讯")
    class SaveTests {
        @Test
        @DisplayName("保存资讯 - 成功")
        void save_success() {
            InformationEntity entity = buildZixun(null);
            when(zixunDao.insert(any(InformationEntity.class))).thenReturn(1);

            zixunService.save(entity);

            verify(zixunDao).insert(entity);
        }
    }

    @Nested
    @DisplayName("更新资讯")
    class UpdateTests {
        @Test
        @DisplayName("更新资讯 - 成功")
        void update_success() {
            InformationEntity entity = buildZixun(1L);
            when(zixunDao.updateById(any(InformationEntity.class))).thenReturn(1);

            zixunService.update(entity);

            verify(zixunDao).updateById(entity);
        }
    }

    @Nested
    @DisplayName("删除资讯")
    class DeleteTests {
        @Test
        @DisplayName("删除资讯 - 成功")
        void delete_success() {
            when(zixunDao.deleteById(1L)).thenReturn(1);

            zixunService.delete(1L);

            verify(zixunDao).deleteById(1L);
        }
    }

    @Nested
    @DisplayName("查询资讯")
    class GetByIdTests {
        @Test
        @DisplayName("根据ID查询 - 存在")
        void getById_exists() {
            InformationEntity expected = buildZixun(1L);
            when(zixunDao.selectById(1L)).thenReturn(expected);

            InformationEntity result = zixunService.getById(1L);

            assertNotNull(result);
            assertEquals("团购资讯", result.getTitle());
            assertEquals("官方", result.getSource());
        }

        @Test
        @DisplayName("根据ID查询 - 不存在返回null")
        void getById_notFound_returnsNull() {
            when(zixunDao.selectById(999L)).thenReturn(null);

            assertNull(zixunService.getById(999L));
        }
    }
}
