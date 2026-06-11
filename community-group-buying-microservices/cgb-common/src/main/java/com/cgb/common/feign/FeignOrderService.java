package com.cgb.common.feign;

import com.cgb.common.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

/**
 * 订单服务 Feign 客户端
 */
@FeignClient(name = "cgb-order-service", contextId = "order")
public interface FeignOrderService {

    @GetMapping("/order/internal/orderDetail")
    R<?> getOrderDetail(@RequestParam("orderId") String orderId);

    @PostMapping("/order/internal/cancel")
    R<?> cancelOrder(@RequestParam("orderId") String orderId, @RequestParam("userId") Long userId);
}