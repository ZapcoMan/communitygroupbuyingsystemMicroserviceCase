package com.cgb.order.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.cgb.common.R;
import com.cgb.order.entity.CartEntity;
import com.cgb.order.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

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
        return R.ok(result.getRecords());
    }

    @Operation(summary = "清空购物车")
    @DeleteMapping("/clear")
    public R<?> clear(HttpServletRequest request) {
        Long userId = Long.parseLong(request.getHeader("X-User-Id"));
        cartService.clear(userId);
        return R.ok("清空成功");
    }

    @Operation(summary = "删除购物车项")
    @DeleteMapping("/{id}")
    public R<?> delete(@PathVariable Long id) {
        cartService.delete(id);
        return R.ok("删除成功");
    }
}