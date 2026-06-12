package com.cgb.product.service.impl;

import com.cgb.product.dao.ProductCommentDao;
import com.cgb.product.entity.ProductCommentEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProductCommentServiceImpl - 商品评价服务测试")
class ProductCommentServiceImplTest {

    @Mock
    private ProductCommentDao commentDao;

    @InjectMocks
    private ProductCommentServiceImpl commentService;

    @Nested
    @DisplayName("save - 新增评价")
    class SaveTests {
        @Test
        @DisplayName("正常保存评价")
        void save_validEntity_callsInsert() {
            when(commentDao.insert(any(ProductCommentEntity.class))).thenReturn(1);
            ProductCommentEntity entity = new ProductCommentEntity();
            entity.setShangpinid(1L);
            entity.setPingfen(5);
            commentService.save(entity);
            verify(commentDao).insert(entity);
        }
    }

    @Nested
    @DisplayName("update - 更新评价")
    class UpdateTests {
        @Test
        @DisplayName("正常更新评价")
        void update_validEntity_callsUpdateById() {
            when(commentDao.updateById(any(ProductCommentEntity.class))).thenReturn(1);
            ProductCommentEntity entity = new ProductCommentEntity();
            entity.setId(1L);
            commentService.update(entity);
            verify(commentDao).updateById(entity);
        }
    }

    @Nested
    @DisplayName("delete - 删除评价")
    class DeleteTests {
        @Test
        @DisplayName("正常删除评价")
        void delete_validId_callsDeleteById() {
            when(commentDao.deleteById(1L)).thenReturn(1);
            commentService.delete(1L);
            verify(commentDao).deleteById(1L);
        }
    }

    @Nested
    @DisplayName("getAverageScore - 获取平均评分")
    class GetAverageScoreTests {
        @Test
        @DisplayName("有评价时返回正确平均分")
        void getAverageScore_hasComments_returnsAverage() {
            when(commentDao.selectCount(any())).thenReturn(2L);
            ProductCommentEntity c1 = new ProductCommentEntity();
            c1.setPingfen(4);
            ProductCommentEntity c2 = new ProductCommentEntity();
            c2.setPingfen(6);
            when(commentDao.selectList(any())).thenReturn(List.of(c1, c2));

            Double avg = commentService.getAverageScore(1L);
            assertEquals(5.0, avg);
        }

        @Test
        @DisplayName("无评价时返回 0.0")
        void getAverageScore_noComments_returnsZero() {
            when(commentDao.selectCount(any())).thenReturn(0L);
            assertEquals(0.0, commentService.getAverageScore(1L));
        }

        @Test
        @DisplayName("selectCount 返回 null 时返回 0.0")
        void getAverageScore_countNull_returnsZero() {
            when(commentDao.selectCount(any())).thenReturn(null);
            assertEquals(0.0, commentService.getAverageScore(1L));
        }
    }
}
