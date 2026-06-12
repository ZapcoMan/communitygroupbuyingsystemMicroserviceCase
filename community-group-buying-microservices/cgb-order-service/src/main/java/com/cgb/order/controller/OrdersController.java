package com.cgb.order.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.cgb.common.R;
import com.cgb.common.annotation.RateLimit;
import com.cgb.order.entity.OrdersEntity;
import com.cgb.order.entity.dto.CreateOrderDTO;
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

    @Operation(summary = "创建订单（Seata分布式事务）")
    @PostMapping("/create")
    @RateLimit(key = "order_create", count = 10, period = 1, unit = RateLimit.TimeUnit.MINUTES)
    public R<?> createOrder(@RequestBody CreateOrderDTO dto, HttpServletRequest request) {
        Long userId = Long.parseLong(request.getHeader("X-User-Id"));
        return R.ok("下单成功", ordersService.createOrderFromDTO(dto, userId));
    }

    @Operation(summary = "支付订单")
    @PostMapping("/pay/{orderId}")
    @RateLimit(key = "order_pay", count = 5, period = 1, unit = RateLimit.TimeUnit.MINUTES)
    public R<?> pay(@PathVariable String orderId) {
        ordersService.pay(orderId);
        return R.ok("支付成功");
    }

    @Operation(summary = "取消订单（Seata分布式事务）")
    @PostMapping("/cancel/{orderId}")
    public R<?> cancel(@PathVariable String orderId, HttpServletRequest request) {
        Long userId = Long.parseLong(request.getHeader("X-User-Id"));
        ordersService.cancel(orderId, userId);
        return R.ok("取消成功");
    }

    @Operation(summary = "发货（管理员）")
    @PostMapping("/ship/{orderId}")
    public R<?> ship(@PathVariable String orderId) {
        ordersService.ship(orderId);
        return R.ok("发货成功");
    }

    @Operation(summary = "确认收货")
    @PostMapping("/confirm/{orderId}")
    public R<?> confirmReceive(@PathVariable String orderId, HttpServletRequest request) {
        Long userId = Long.parseLong(request.getHeader("X-User-Id"));
        ordersService.confirmReceive(orderId, userId);
        return R.ok("确认收货成功");
    }

    @Operation(summary = "我的订单")
    @GetMapping("/my")
    public R<?> myList(@Parameter(hidden = true) OrdersEntity params,
                       @RequestParam(defaultValue = "1") Integer page,
                       @RequestParam(defaultValue = "10") Integer limit) {
        IPage<OrdersEntity> result = ordersService.queryPage(params);
        return R.ok(result);
    }

    @Operation(summary = "订单详情")
    @GetMapping("/{id}")
    public R<?> detail(@PathVariable Long id) {
        return R.ok(ordersService.getById(id));
    }

    @Operation(summary = "修改订单")
    @PutMapping
    public R<?> update(@RequestBody OrdersEntity entity) {
        ordersService.update(entity);
        return R.ok("更新成功");
    }

    @Operation(summary = "删除订单")
    @DeleteMapping("/{id}")
    public R<?> delete(@PathVariable Long id) {
        ordersService.delete(id);
        return R.ok("删除成功");
    }

    @Operation(summary = "批量删除订单")
    @DeleteMapping("/batch")
    public R<?> batchDelete(@RequestBody java.util.List<Long> ids) {
        ids.forEach(ordersService::delete);
        return R.ok("批量删除成功");
    }

    @Operation(summary = "管理员查询所有订单")
    @GetMapping("/list")
    public R<?> list(@Parameter(hidden = true) OrdersEntity params,
                     @RequestParam(defaultValue = "1") Integer page,
                     @RequestParam(defaultValue = "10") Integer limit) {
        IPage<OrdersEntity> result = ordersService.queryPage(params);
        return R.ok(result);
    }

    /** 内部接口 - 获取订单详情 */
    @GetMapping("/internal/orderDetail")
    public R<?> internalOrderDetail(@RequestParam String orderId) {
        return R.ok(ordersService.getByOrderId(orderId));
    }

    /** 内部接口 - 取消订单 */
    @PostMapping("/internal/cancel")
    public R<?> internalCancel(@RequestParam String orderId, @RequestParam Long userId) {
        ordersService.cancel(orderId, userId);
        return R.ok();
    }
}
