package com.cgb.common.feign;

import com.cgb.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

/**
 * 订单服务 Feign 降级工厂
 */
@Slf4j
@Component
public class FeignOrderServiceFallbackFactory implements FallbackFactory<FeignOrderService> {

    @Override
    public FeignOrderService create(Throwable cause) {
        log.error("订单服务调用失败", cause);
        return new FeignOrderService() {
            @Override
            public R<?> getOrderDetail(String orderId) {
                return R.fail("订单服务暂不可用，获取订单详情失败");
            }

            @Override
            public R<?> cancelOrder(String orderId, Long userId) {
                return R.fail("订单服务暂不可用，取消订单失败");
            }
        };
    }
}
