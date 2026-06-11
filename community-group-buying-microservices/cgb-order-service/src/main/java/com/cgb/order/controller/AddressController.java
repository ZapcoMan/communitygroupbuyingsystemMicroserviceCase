package com.cgb.order.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.cgb.common.R;
import com.cgb.order.entity.AddressEntity;
import com.cgb.order.service.AddressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "收货地址")
@RestController
@RequestMapping("/address")
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;

    @Operation(summary = "新增地址")
    @PostMapping
    public R<?> save(@RequestBody AddressEntity entity, HttpServletRequest request) {
        Long userId = Long.parseLong(request.getHeader("X-User-Id"));
        entity.setUserid(userId);
        addressService.save(entity);
        return R.ok("保存成功");
    }

    @Operation(summary = "我的地址列表")
    @GetMapping("/my")
    public R<?> myList(@Parameter(hidden = true) AddressEntity params) {
        IPage<AddressEntity> result = addressService.queryPage(params);
        return R.ok(result.getRecords());
    }

    @Operation(summary = "修改地址")
    @PutMapping
    public R<?> update(@RequestBody AddressEntity entity) {
        addressService.update(entity);
        return R.ok("更新成功");
    }

    @Operation(summary = "删除地址")
    @DeleteMapping("/{id}")
    public R<?> delete(@PathVariable Long id) {
        addressService.delete(id);
        return R.ok("删除成功");
    }

    @Operation(summary = "设为默认地址")
    @PostMapping("/default/{id}")
    public R<?> setDefault(@PathVariable Long id, HttpServletRequest request) {
        Long userId = Long.parseLong(request.getHeader("X-User-Id"));
        addressService.setDefault(id, userId);
        return R.ok("设置成功");
    }
}