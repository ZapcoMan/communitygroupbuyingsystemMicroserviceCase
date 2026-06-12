package com.cgb.product.service.impl;

import com.cgb.common.EIException;
import com.cgb.product.dao.ProductDao;
import com.cgb.product.entity.ProductEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * ProductServiceImpl 单元测试
 * 覆盖商品 CRUD 和 Redis 库存管理
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ProductServiceImpl - 商品服务测试")
class ProductServiceImplTest {

    @Mock
    private ProductDao shangpinDao;

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ValueOperations<String, Object> valueOperations;

    @InjectMocks
    private ProductServiceImpl shangpinService;

    @Nested
    @DisplayName("save - 新增商品")
    class SaveTests {
        @Test
        @DisplayName("正常保存商品")
        void save_validEntity_callsInsert() {
            when(shangpinDao.insert(any(ProductEntity.class))).thenReturn(1);
            ProductEntity entity = new ProductEntity();
            entity.setMingcheng("苹果");
            shangpinService.save(entity);
            verify(shangpinDao).insert(entity);
        }
    }

    @Nested
    @DisplayName("update - 更新商品")
    class UpdateTests {
        @Test
        @DisplayName("ID 为空时抛出异常")
        void update_nullId_throwsException() {
            ProductEntity entity = new ProductEntity();
            assertThrows(EIException.class, () -> shangpinService.update(entity));
        }

        @Test
        @DisplayName("正常更新商品")
        void update_validEntity_callsUpdateById() {
            when(shangpinDao.updateById(any(ProductEntity.class))).thenReturn(1);
            ProductEntity entity = new ProductEntity();
            entity.setId(1L);
            shangpinService.update(entity);
            verify(shangpinDao).updateById(entity);
        }
    }

    @Nested
    @DisplayName("delete - 删除商品")
    class DeleteTests {
        @Test
        @DisplayName("正常删除商品")
        void delete_validId_callsDeleteById() {
            when(shangpinDao.deleteById(1L)).thenReturn(1);
            shangpinService.delete(1L);
            verify(shangpinDao).deleteById(1L);
        }
    }

    @Nested
    @DisplayName("getById - 根据ID查询商品")
    class GetByIdTests {
        @Test
        @DisplayName("商品存在时正常返回")
        void getById_exists_returnsEntity() {
            ProductEntity expected = new ProductEntity();
            expected.setId(1L);
            when(shangpinDao.selectById(1L)).thenReturn(expected);
            ProductEntity result = shangpinService.getById(1L);
            assertNotNull(result);
            assertEquals(1L, result.getId());
        }

        @Test
        @DisplayName("商品不存在时抛出异常")
        void getById_notExists_throwsException() {
            when(shangpinDao.selectById(999L)).thenReturn(null);
            EIException ex = assertThrows(EIException.class, () -> shangpinService.getById(999L));
            assertEquals(404, ex.getCode());
        }
    }

    @Nested
    @DisplayName("decreaseStock - 扣减库存")
    class DecreaseStockTests {
        @Test
        @DisplayName("库存充足时正常扣减")
        void decreaseStock_sufficientStock_decrementsRedis() {
            when(redisTemplate.opsForValue()).thenReturn(valueOperations);
            when(valueOperations.decrement("cgb:stock:1", 2)).thenReturn(8L);
            when(shangpinDao.update(isNull(), any())).thenReturn(1);

            shangpinService.decreaseStock(1L, 2);

            verify(valueOperations).decrement("cgb:stock:1", 2);
            verify(shangpinDao).update(isNull(), any());
        }

        @Test
        @DisplayName("库存不足时回补并抛出异常")
        void decreaseStock_insufficientStock_rollbackAndThrows() {
            when(redisTemplate.opsForValue()).thenReturn(valueOperations);
            when(valueOperations.decrement("cgb:stock:1", 10)).thenReturn(-2L);

            EIException ex = assertThrows(EIException.class,
                    () -> shangpinService.decreaseStock(1L, 10));
            assertEquals("库存不足", ex.getMessage());
            verify(valueOperations).increment("cgb:stock:1", 10);
        }

        @Test
        @DisplayName("Redis 返回 null 时不抛异常")
        void decreaseStock_redisReturnsNull_noException() {
            when(redisTemplate.opsForValue()).thenReturn(valueOperations);
            when(valueOperations.decrement("cgb:stock:1", 1)).thenReturn(null);
            when(shangpinDao.update(isNull(), any())).thenReturn(1);

            assertDoesNotThrow(() -> shangpinService.decreaseStock(1L, 1));
        }
    }

    @Nested
    @DisplayName("increaseStock - 增加库存")
    class IncreaseStockTests {
        @Test
        @DisplayName("正常增加库存")
        void increaseStock_callsIncrementAndUpdate() {
            when(redisTemplate.opsForValue()).thenReturn(valueOperations);
            when(shangpinDao.update(isNull(), any())).thenReturn(1);

            shangpinService.increaseStock(1L, 5);

            verify(valueOperations).increment("cgb:stock:1", 5);
            verify(shangpinDao).update(isNull(), any());
        }
    }
}
