package com.cgb.order.service.impl;

import com.cgb.common.EIException;
import com.cgb.order.dao.OrdersDao;
import com.cgb.order.entity.OrdersEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("订单服务测试")
class OrdersServiceImplTest {

    @Mock
    private OrdersDao ordersDao;

    @InjectMocks
    private OrdersServiceImpl ordersService;

    // ========== 辅助方法 ==========

    private OrdersEntity buildOrder(Long id, String orderId, Integer status) {
        OrdersEntity entity = new OrdersEntity();
        entity.setId(id);
        entity.setOrderid(orderId);
        entity.setUserId(100L);
        entity.setShangpinid(1L);
        entity.setShangpinming("测试商品");
        entity.setShuliang(2);
        entity.setJiage(new BigDecimal("99.90"));
        entity.setZongjia(new BigDecimal("199.80"));
        entity.setZhuangtai(status);
        return entity;
    }

    // ========== save 测试 ==========

    @Nested
    @DisplayName("保存订单")
    class SaveTests {

        @Test
        @DisplayName("保存订单 - 无订单号时自动生成")
        void save_noOrderId_autoGenerate() {
            OrdersEntity entity = buildOrder(null, null, null);
            when(ordersDao.insert(any(OrdersEntity.class))).thenReturn(1);

            ordersService.save(entity);

            assertNotNull(entity.getOrderid(), "应自动生成订单号");
            assertEquals(0, entity.getZhuangtai(), "默认状态应为0(待支付)");
            verify(ordersDao).insert(entity);
        }

        @Test
        @DisplayName("保存订单 - 空字符串订单号时自动生成")
        void save_emptyOrderId_autoGenerate() {
            OrdersEntity entity = buildOrder(null, "", null);
            when(ordersDao.insert(any(OrdersEntity.class))).thenReturn(1);

            ordersService.save(entity);

            assertNotNull(entity.getOrderid());
            assertFalse(entity.getOrderid().isEmpty());
            verify(ordersDao).insert(entity);
        }

        @Test
        @DisplayName("保存订单 - 已有订单号时保留原值")
        void save_withOrderId_keepOriginal() {
            OrdersEntity entity = buildOrder(null, "ORD-20250101-001", 1);
            when(ordersDao.insert(any(OrdersEntity.class))).thenReturn(1);

            ordersService.save(entity);

            assertEquals("ORD-20250101-001", entity.getOrderid());
            assertEquals(1, entity.getZhuangtai());
            verify(ordersDao).insert(entity);
        }
    }

    // ========== update 测试 ==========

    @Nested
    @DisplayName("更新订单")
    class UpdateTests {

        @Test
        @DisplayName("更新订单 - 成功")
        void update_success() {
            OrdersEntity entity = buildOrder(1L, "ORD-001", 1);
            when(ordersDao.updateById(any(OrdersEntity.class))).thenReturn(1);

            assertDoesNotThrow(() -> ordersService.update(entity));
            verify(ordersDao).updateById(entity);
        }

        @Test
        @DisplayName("更新订单 - ID为空抛出异常")
        void update_nullId_throwsException() {
            OrdersEntity entity = buildOrder(null, "ORD-001", 1);

            EIException ex = assertThrows(EIException.class,
                    () -> ordersService.update(entity));
            assertEquals("订单ID不能为空", ex.getMessage());
        }
    }

    // ========== delete 测试 ==========

    @Nested
    @DisplayName("删除订单")
    class DeleteTests {

        @Test
        @DisplayName("删除订单 - 成功")
        void delete_success() {
            when(ordersDao.deleteById(1L)).thenReturn(1);

            ordersService.delete(1L);

            verify(ordersDao).deleteById(1L);
        }
    }

    // ========== getById 测试 ==========

    @Nested
    @DisplayName("根据ID查询订单")
    class GetByIdTests {

        @Test
        @DisplayName("根据ID查询 - 存在")
        void getById_exists() {
            OrdersEntity expected = buildOrder(1L, "ORD-001", 0);
            when(ordersDao.selectById(1L)).thenReturn(expected);

            OrdersEntity result = ordersService.getById(1L);

            assertNotNull(result);
            assertEquals("ORD-001", result.getOrderid());
        }

        @Test
        @DisplayName("根据ID查询 - 不存在抛出异常")
        void getById_notFound_throwsException() {
            when(ordersDao.selectById(999L)).thenReturn(null);

            assertThrows(EIException.class, () -> ordersService.getById(999L));
        }
    }

    // ========== getByOrderId 测试 ==========

    @Nested
    @DisplayName("根据订单号查询")
    class GetByOrderIdTests {

        @Test
        @DisplayName("根据订单号查询 - 存在")
        void getByOrderId_exists() {
            OrdersEntity expected = buildOrder(1L, "ORD-001", 0);
            when(ordersDao.selectOne(any())).thenReturn(expected);

            OrdersEntity result = ordersService.getByOrderId("ORD-001");

            assertNotNull(result);
            assertEquals(1L, result.getId());
        }

        @Test
        @DisplayName("根据订单号查询 - 不存在抛出异常")
        void getByOrderId_notFound_throwsException() {
            when(ordersDao.selectOne(any())).thenReturn(null);

            assertThrows(EIException.class, () -> ordersService.getByOrderId("NOT-EXIST"));
        }
    }

    // ========== cancel 测试 ==========

    @Nested
    @DisplayName("取消订单")
    class CancelTests {

        @Test
        @DisplayName("取消订单 - 待支付状态成功取消")
        void cancel_pendingOrder_success() {
            OrdersEntity order = buildOrder(1L, "ORD-001", 0);
            when(ordersDao.selectOne(any())).thenReturn(order);
            when(ordersDao.updateById(any(OrdersEntity.class))).thenReturn(1);

            ordersService.cancel("ORD-001", 100L);

            assertEquals(2, order.getZhuangtai(), "取消后状态应为2");
            verify(ordersDao).updateById(order);
        }

        @Test
        @DisplayName("取消订单 - 订单不存在抛出异常")
        void cancel_notFound_throwsException() {
            when(ordersDao.selectOne(any())).thenReturn(null);

            assertThrows(EIException.class, () -> ordersService.cancel("NOT-EXIST", 100L));
        }

        @Test
        @DisplayName("取消订单 - 已支付状态不允许取消")
        void cancel_paidOrder_throwsException() {
            OrdersEntity order = buildOrder(1L, "ORD-001", 1);
            when(ordersDao.selectOne(any())).thenReturn(order);

            EIException ex = assertThrows(EIException.class,
                    () -> ordersService.cancel("ORD-001", 100L));
            assertEquals("只能取消待支付订单", ex.getMessage());
        }

        @Test
        @DisplayName("取消订单 - 已取消状态不允许重复取消")
        void cancel_alreadyCancelled_throwsException() {
            OrdersEntity order = buildOrder(1L, "ORD-001", 2);
            when(ordersDao.selectOne(any())).thenReturn(order);

            assertThrows(EIException.class, () -> ordersService.cancel("ORD-001", 100L));
        }
    }

    // ========== pay 测试 ==========

    @Nested
    @DisplayName("支付订单")
    class PayTests {

        @Test
        @DisplayName("支付订单 - 待支付状态成功支付")
        void pay_pendingOrder_success() {
            OrdersEntity order = buildOrder(1L, "ORD-001", 0);
            when(ordersDao.selectOne(any())).thenReturn(order);
            when(ordersDao.updateById(any(OrdersEntity.class))).thenReturn(1);

            ordersService.pay("ORD-001");

            assertEquals(1, order.getZhuangtai(), "支付后状态应为1");
            verify(ordersDao).updateById(order);
        }

        @Test
        @DisplayName("支付订单 - 订单不存在抛出异常")
        void pay_notFound_throwsException() {
            when(ordersDao.selectOne(any())).thenReturn(null);

            assertThrows(EIException.class, () -> ordersService.pay("NOT-EXIST"));
        }

        @Test
        @DisplayName("支付订单 - 已支付状态不允许重复支付")
        void pay_alreadyPaid_throwsException() {
            OrdersEntity order = buildOrder(1L, "ORD-001", 1);
            when(ordersDao.selectOne(any())).thenReturn(order);

            EIException ex = assertThrows(EIException.class,
                    () -> ordersService.pay("ORD-001"));
            assertEquals("订单状态不允许支付", ex.getMessage());
        }

        @Test
        @DisplayName("支付订单 - 已取消状态不允许支付")
        void pay_cancelledOrder_throwsException() {
            OrdersEntity order = buildOrder(1L, "ORD-001", 2);
            when(ordersDao.selectOne(any())).thenReturn(order);

            assertThrows(EIException.class, () -> ordersService.pay("ORD-001"));
        }
    }
}
