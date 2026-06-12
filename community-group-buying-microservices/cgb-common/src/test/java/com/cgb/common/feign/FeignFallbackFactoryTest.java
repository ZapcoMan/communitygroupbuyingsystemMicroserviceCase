package com.cgb.common.feign;

import com.cgb.common.R;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Feign 降级工厂测试
 */
class FeignFallbackFactoryTest {

    @Test
    @DisplayName("商品服务降级 - 返回失败响应")
    void productFallback_shouldReturnFailResponse() {
        FeignProductServiceFallbackFactory factory = new FeignProductServiceFallbackFactory();
        FeignProductService fallback = factory.create(new RuntimeException("test"));

        R<?> result = fallback.getProductDetail(1L);
        assertNotNull(result);
        assertNotEquals(0, result.getCode());
        assertTrue(result.getMsg().contains("暂不可用"));
    }

    @Test
    @DisplayName("商品服务降级 - 扣减库存返回失败")
    void productFallback_decreaseStock_shouldReturnFail() {
        FeignProductServiceFallbackFactory factory = new FeignProductServiceFallbackFactory();
        FeignProductService fallback = factory.create(new RuntimeException("connection refused"));

        R<?> result = fallback.decreaseStock(1L, 10);
        assertNotNull(result);
        assertNotEquals(0, result.getCode());
    }

    @Test
    @DisplayName("用户服务降级 - 返回失败响应")
    void userFallback_shouldReturnFailResponse() {
        FeignUserServiceFallbackFactory factory = new FeignUserServiceFallbackFactory();
        FeignUserService fallback = factory.create(new RuntimeException("test"));

        R<?> result = fallback.getUserInfo(1L);
        assertNotNull(result);
        assertNotEquals(0, result.getCode());
    }

    @Test
    @DisplayName("订单服务降级 - 返回失败响应")
    void orderFallback_shouldReturnFailResponse() {
        FeignOrderServiceFallbackFactory factory = new FeignOrderServiceFallbackFactory();
        FeignOrderService fallback = factory.create(new RuntimeException("test"));

        R<?> result = fallback.getOrderDetail("ORD001");
        assertNotNull(result);
        assertNotEquals(0, result.getCode());
    }
}
