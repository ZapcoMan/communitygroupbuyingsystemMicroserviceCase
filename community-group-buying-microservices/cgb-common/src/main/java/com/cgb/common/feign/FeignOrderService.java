package com.cgb.common.feign;

import com.cgb.common.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

/**
 * 订单服务 Feign 客户端
 */
@FeignClient(name = "cgb-order-service", contextId = "order", fallbackFactory = FeignOrderServiceFallbackFactory.class)
public interface FeignOrderService {

    @GetMapping("/orders/internal/orderDetail")
    R<?> getOrderDetail(@RequestParam("orderId") String orderId);

    @PostMapping("/orders/internal/cancel")
    R<?> cancelOrder(@RequestParam("orderId") String orderId, @RequestParam("userId") Long userId);
}
