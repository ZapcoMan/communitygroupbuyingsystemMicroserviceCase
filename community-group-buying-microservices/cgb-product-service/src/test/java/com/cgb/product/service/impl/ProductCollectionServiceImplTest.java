package com.cgb.product.service.impl;

import com.cgb.product.dao.ProductCollectionDao;
import com.cgb.product.entity.ProductCollectionEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProductCollectionServiceImpl - 商品收藏服务测试")
class ProductCollectionServiceImplTest {

    @Mock
    private ProductCollectionDao collectionDao;

    @InjectMocks
    private ProductCollectionServiceImpl collectionService;

    @Nested
    @DisplayName("save - 新增收藏")
    class SaveTests {
        @Test
        @DisplayName("正常保存收藏记录")
        void save_validEntity_callsInsert() {
            when(collectionDao.insert(any(ProductCollectionEntity.class))).thenReturn(1);
            ProductCollectionEntity entity = new ProductCollectionEntity();
            entity.setUserId(1L);
            entity.setShangpinid(10L);
            collectionService.save(entity);
            verify(collectionDao).insert(entity);
        }
    }

    @Nested
    @DisplayName("delete - 删除收藏")
    class DeleteTests {
        @Test
        @DisplayName("正常删除收藏")
        void delete_validId_callsDeleteById() {
            when(collectionDao.deleteById(1L)).thenReturn(1);
            collectionService.delete(1L);
            verify(collectionDao).deleteById(1L);
        }
    }

    @Nested
    @DisplayName("getByUserAndProduct - 根据用户和商品查询收藏")
    class GetByUserAndProductTests {
        @Test
        @DisplayName("收藏存在时返回实体")
        void getByUserAndProduct_exists_returnsEntity() {
            ProductCollectionEntity expected = new ProductCollectionEntity();
            expected.setId(1L);
            when(collectionDao.selectOne(any())).thenReturn(expected);
            ProductCollectionEntity result = collectionService.getByUserAndProduct(1L, 10L);
            assertNotNull(result);
        }

        @Test
        @DisplayName("收藏不存在时返回 null")
        void getByUserAndProduct_notExists_returnsNull() {
            when(collectionDao.selectOne(any())).thenReturn(null);
            assertNull(collectionService.getByUserAndProduct(1L, 999L));
        }
    }

    @Nested
    @DisplayName("isCollected - 判断是否已收藏")
    class IsCollectedTests {
        @Test
        @DisplayName("已收藏返回 true")
        void isCollected_collected_returnsTrue() {
            when(collectionDao.selectOne(any())).thenReturn(new ProductCollectionEntity());
            assertTrue(collectionService.isCollected(1L, 10L));
        }

        @Test
        @DisplayName("未收藏返回 false")
        void isCollected_notCollected_returnsFalse() {
            when(collectionDao.selectOne(any())).thenReturn(null);
            assertFalse(collectionService.isCollected(1L, 10L));
        }
    }
}
