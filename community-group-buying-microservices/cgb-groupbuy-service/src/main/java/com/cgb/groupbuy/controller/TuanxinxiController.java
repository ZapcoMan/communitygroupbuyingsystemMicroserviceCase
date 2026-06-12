package com.cgb.groupbuy.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.cgb.common.R;
import com.cgb.common.annotation.RateLimit;
import com.cgb.groupbuy.entity.GroupBuyEntity;
import com.cgb.groupbuy.service.GroupBuyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "参团管理")
@RestController
@RequestMapping("/tuanxinxi")
@RequiredArgsConstructor
public class GroupBuyController {

    private final GroupBuyService tuanxinxiService;

    @Operation(summary = "参与团购")
    @PostMapping
    @RateLimit(key = "groupbuy_join", count = 10, period = 1, unit = RateLimit.TimeUnit.MINUTES)
    public R<?> save(@RequestBody GroupBuyEntity entity, HttpServletRequest request) {
        Long userId = Long.parseLong(request.getHeader("X-User-Id"));
        entity.setUserId(userId);
        if (entity.getZhuangtai() == null) entity.setZhuangtai(0);
        tuanxinxiService.save(entity);
        return R.ok("参与成功");
    }

    @Operation(summary = "分页查询参团记录")
    @GetMapping("/list")
    public R<?> list(@Parameter(hidden = true) GroupBuyEntity params) {
        IPage<GroupBuyEntity> result = tuanxinxiService.queryPage(params);
        return R.ok(result);
    }

    @Operation(summary = "参团详情")
    @GetMapping("/{id}")
    public R<?> detail(@PathVariable Long id) {
        return R.ok(tuanxinxiService.getById(id));
    }

    @Operation(summary = "取消参团")
    @DeleteMapping("/{id}")
    public R<?> delete(@PathVariable Long id) {
        tuanxinxiService.delete(id);
        return R.ok("取消成功");
    }
}
