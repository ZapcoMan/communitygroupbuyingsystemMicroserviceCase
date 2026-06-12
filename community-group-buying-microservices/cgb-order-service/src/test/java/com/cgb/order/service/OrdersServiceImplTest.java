package com.cgb.order.service;

import com.cgb.common.EIException;
import com.cgb.common.feign.FeignProductService;
import com.cgb.common.feign.FeignUserService;
import com.cgb.common.mq.MQTopics;
import com.cgb.order.dao.OrdersDao;
import com.cgb.order.entity.OrdersEntity;
import com.cgb.order.entity.dto.CreateOrderDTO;
import com.cgb.order.entity.vo.OrderVO;
import com.cgb.order.service.impl.OrdersServiceImpl;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 订单服务单元测试
 */
@ExtendWith(MockitoExtension.class)
class OrdersServiceImplTest {

    @Mock private OrdersDao ordersDao;
    @Mock private FeignProductService feignProductService;
    @Mock private FeignUserService feignUserService;
    @Mock private RocketMQTemplate rocketMQTemplate;

    @InjectMocks
    private OrdersServiceImpl ordersService;

    private OrdersEntity testOrder;

    @BeforeEach
    void setUp() {
        testOrder = new OrdersEntity();
        testOrder.setId(1L);
        testOrder.setOrderid("ORD20240612001");
        testOrder.setUserid(100L);
        testOrder.setShangpinid(200L);
        testOrder.setShuliang(2);
        testOrder.setZhuangtai(0);
        testOrder.setZongjia(java.math.BigDecimal.valueOf(99.9));
    }

    @Test
    @DisplayName("取消订单 - 只能取消待支付订单")
    void cancel_shouldRejectNonPendingOrder() {
        testOrder.setZhuangtai(1); // 已支付
        when(ordersDao.selectOne(any())).thenReturn(testOrder);

        assertThrows(EIException.class, () -> ordersService.cancel("ORD20240612001", 100L));
    }

    @Test
    @DisplayName("取消订单 - 非本人订单应拒绝")
    void cancel_shouldRejectOtherUserOrder() {
        when(ordersDao.selectOne(any())).thenReturn(null); // userId 不匹配，查不到

        assertThrows(EIException.class, () -> ordersService.cancel("ORD20240612001", 999L));
    }

    @Test
    @DisplayName("支付订单 - 只能支付待支付订单")
    void pay_shouldRejectNonPendingOrder() {
        testOrder.setZhuangtai(2); // 已取消
        when(ordersDao.selectOne(any())).thenReturn(testOrder);

        assertThrows(EIException.class, () -> ordersService.pay("ORD20240612001"));
    }

    @Test
    @DisplayName("Entity → VO 转换 - 字段完整映射")
    void toVO_shouldMapAllFields() {
        OrderVO vo = ordersService.toVO(testOrder);

        assertNotNull(vo);
        assertEquals(testOrder.getId(), vo.getId());
        assertEquals(testOrder.getOrderid(), vo.getOrderId());
        assertEquals(testOrder.getUserid(), vo.getUserId());
        assertEquals(testOrder.getShangpinid(), vo.getProductId());
        assertEquals(testOrder.getShangpinming(), vo.getProductName());
        assertEquals(testOrder.getShuliang(), vo.getQuantity());
        assertEquals(testOrder.getZhuangtai(), vo.getStatus());
        assertEquals(testOrder.getZongjia(), vo.getTotalPrice());
    }

    @Test
    @DisplayName("Entity → VO 转换 - null 输入返回 null")
    void toVO_shouldReturnNullForNullInput() {
        assertNull(ordersService.toVO(null));
    }
}
