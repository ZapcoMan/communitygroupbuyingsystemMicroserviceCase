package com.cgb.order.service.impl;

import com.cgb.order.dao.CartDao;
import com.cgb.order.entity.CartEntity;
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
@DisplayName("购物车服务测试")
class CartServiceImplTest {

    @Mock
    private CartDao cartDao;

    @InjectMocks
    private CartServiceImpl cartService;

    // ========== 辅助方法 ==========

    private CartEntity buildCart(Long id, Long userId, Long productId, Integer quantity) {
        CartEntity entity = new CartEntity();
        entity.setId(id);
        entity.setUserid(userId);
        entity.setShangpinid(productId);
        entity.setShuliang(quantity);
        return entity;
    }

    // ========== save 测试 ==========

    @Nested
    @DisplayName("添加购物车")
    class SaveTests {

        @Test
        @DisplayName("添加购物车 - 新商品直接插入")
        void save_newProduct_insert() {
            CartEntity entity = buildCart(null, 100L, 1L, 2);
            when(cartDao.selectOne(any())).thenReturn(null);
            when(cartDao.insert(any(CartEntity.class))).thenReturn(1);

            cartService.save(entity);

            verify(cartDao).insert(entity);
        }

        @Test
        @DisplayName("添加购物车 - 已存在同商品则累加数量")
        void save_existingProduct_updateQuantity() {
            CartEntity newEntity = buildCart(null, 100L, 1L, 3);
            CartEntity existing = buildCart(10L, 100L, 1L, 5);
            when(cartDao.selectOne(any())).thenReturn(existing);
            when(cartDao.updateById(any(CartEntity.class))).thenReturn(1);

            cartService.save(newEntity);

            ArgumentCaptor<CartEntity> captor = ArgumentCaptor.forClass(CartEntity.class);
            verify(cartDao).updateById(captor.capture());
            assertEquals(8, captor.getValue().getShuliang(), "数量应累加: 5 + 3 = 8");
            verify(cartDao, never()).insert(any(CartEntity.class));
        }

        @Test
        @DisplayName("添加购物车 - 已存在同商品数量为1时累加")
        void save_existingProduct_singleQuantity() {
            CartEntity newEntity = buildCart(null, 100L, 2L, 1);
            CartEntity existing = buildCart(10L, 100L, 2L, 1);
            when(cartDao.selectOne(any())).thenReturn(existing);
            when(cartDao.updateById(any(CartEntity.class))).thenReturn(1);

            cartService.save(newEntity);

            ArgumentCaptor<CartEntity> captor = ArgumentCaptor.forClass(CartEntity.class);
            verify(cartDao).updateById(captor.capture());
            assertEquals(2, captor.getValue().getShuliang(), "数量应累加: 1 + 1 = 2");
        }
    }

    // ========== update 测试 ==========

    @Nested
    @DisplayName("更新购物车")
    class UpdateTests {

        @Test
        @DisplayName("更新购物车 - 成功")
        void update_success() {
            CartEntity entity = buildCart(1L, 100L, 1L, 10);
            when(cartDao.updateById(any(CartEntity.class))).thenReturn(1);

            cartService.update(entity);

            verify(cartDao).updateById(entity);
        }
    }

    // ========== delete 测试 ==========

    @Nested
    @DisplayName("删除购物车项")
    class DeleteTests {

        @Test
        @DisplayName("删除购物车项 - 成功")
        void delete_success() {
            when(cartDao.deleteById(1L)).thenReturn(1);

            cartService.delete(1L);

            verify(cartDao).deleteById(1L);
        }
    }

    // ========== clear 测试 ==========

    @Nested
    @DisplayName("清空购物车")
    class ClearTests {

        @Test
        @DisplayName("清空用户购物车 - 成功删除所有项")
        void clear_success() {
            when(cartDao.delete(any())).thenReturn(3);

            cartService.clear(100L);

            verify(cartDao).delete(any());
        }

        @Test
        @DisplayName("清空用户购物车 - 购物车为空时也正常执行")
        void clear_emptyCart_success() {
            when(cartDao.delete(any())).thenReturn(0);

            cartService.clear(100L);

            verify(cartDao).delete(any());
        }
    }
}
