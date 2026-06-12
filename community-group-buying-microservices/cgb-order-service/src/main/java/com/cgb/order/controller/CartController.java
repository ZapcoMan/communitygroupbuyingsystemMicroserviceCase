package com.cgb.order.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.cgb.common.R;
import com.cgb.common.annotation.RateLimit;
import com.cgb.order.entity.CartEntity;
import com.cgb.order.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "购物车")
@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @Operation(summary = "加入购物车")
    @PostMapping
    public R<?> add(@RequestBody CartEntity entity, HttpServletRequest request) {
        Long userId = Long.parseLong(request.getHeader("X-User-Id"));
        entity.setUserid(userId);
        cartService.save(entity);
        return R.ok("添加成功");
    }

    @Operation(summary = "我的购物车")
    @GetMapping("/my")
    public R<?> myList(@Parameter(hidden = true) CartEntity params,
                       @RequestParam(defaultValue = "1") Integer page,
                       @RequestParam(defaultValue = "10") Integer limit) {
        IPage<CartEntity> result = cartService.queryPage(params);
        return R.ok(result);
    }

    @Operation(summary = "购物车结算（Seata分布式事务：批量下单+扣库存+清空购物车）")
    @PostMapping("/checkout")
    @RateLimit(key = "cart_checkout", count = 5, period = 1, unit = RateLimit.TimeUnit.MINUTES)
    public R<?> checkout(HttpServletRequest request) {
        Long userId = Long.parseLong(request.getHeader("X-User-Id"));
        var orders = cartService.checkout(userId);
        return R.ok("结算成功，共创建" + orders.size() + "个订单", orders);
    }

    @Operation(summary = "清空购物车")
    @DeleteMapping("/clear")
    public R<?> clear(HttpServletRequest request) {
        Long userId = Long.parseLong(request.getHeader("X-User-Id"));
        cartService.clear(userId);
        return R.ok("清空成功");
    }

    @Operation(summary = "更新购物车项")
    @PutMapping("/{id}")
    public R<?> update(@PathVariable Long id, @RequestBody CartEntity entity) {
        entity.setId(id);
        cartService.update(entity);
        return R.ok("更新成功");
    }

    @Operation(summary = "删除购物车项")
    @DeleteMapping("/{id}")
    public R<?> delete(@PathVariable Long id) {
        cartService.delete(id);
        return R.ok("删除成功");
    }

    @Operation(summary = "管理员查询所有购物车")
    @GetMapping("/list")
    public R<?> list(@Parameter(hidden = true) CartEntity params,
                     @RequestParam(defaultValue = "1") Integer page,
                     @RequestParam(defaultValue = "10") Integer limit) {
        IPage<CartEntity> result = cartService.queryPage(params);
        return R.ok(result);
    }

    @Operation(summary = "批量删除购物车")
    @DeleteMapping("/batch")
    public R<?> batchDelete(@RequestBody List<Long> ids) {
        ids.forEach(cartService::delete);
        return R.ok("批量删除成功");
    }
}
