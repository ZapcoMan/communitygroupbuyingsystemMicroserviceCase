package com.cgb.order.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.cgb.common.R;
import com.cgb.order.entity.OrdersEntity;
import com.cgb.order.service.OrdersService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "订单管理")
@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrdersController {

    private final OrdersService ordersService;

    @Operation(summary = "创建订单")
    @PostMapping
    public R<?> create(@RequestBody OrdersEntity entity, HttpServletRequest request) {
        Long userId = Long.parseLong(request.getHeader("X-User-Id"));
        entity.setUserid(userId);
        ordersService.save(entity);
        return R.ok("下单成功", entity);
    }

    @Operation(summary = "我的订单列表")
    @GetMapping("/my")
    public R<?> myList(@Parameter(hidden = true) OrdersEntity params,
                       @RequestParam(defaultValue = "1") Integer page,
                       @RequestParam(defaultValue = "10") Integer limit) {
        IPage<OrdersEntity> result = ordersService.queryPage(params);
        return R.ok(result.getRecords());
    }

    @Operation(summary = "订单详情")
    @GetMapping("/{id}")
    public R<?> detail(@PathVariable Long id) {
        return R.ok(ordersService.getById(id));
    }

    @Operation(summary = "支付订单")
    @PostMapping("/pay/{orderId}")
    public R<?> pay(@PathVariable String orderId) {
        ordersService.pay(orderId);
        return R.ok("支付成功");
    }

    @Operation(summary = "取消订单")
    @PostMapping("/cancel/{orderId}")
    public R<?> cancel(@PathVariable String orderId, HttpServletRequest request) {
        Long userId = Long.parseLong(request.getHeader("X-User-Id"));
        ordersService.cancel(orderId, userId);
        return R.ok("取消成功");
    }

    @Operation(summary = "删除订单")
    @DeleteMapping("/{id}")
    public R<?> delete(@PathVariable Long id) {
        ordersService.delete(id);
        return R.ok("删除成功");
    }

    /** 内部接口 */
    @GetMapping("/internal/orderDetail")
    public R<?> internalOrderDetail(@RequestParam String orderId) {
        return R.ok(ordersService.getByOrderId(orderId));
    }

    @PostMapping("/internal/cancel")
    public R<?> internalCancel(@RequestParam String orderId, @RequestParam Long userId) {
        ordersService.cancel(orderId, userId);
        return R.ok();
    }
}