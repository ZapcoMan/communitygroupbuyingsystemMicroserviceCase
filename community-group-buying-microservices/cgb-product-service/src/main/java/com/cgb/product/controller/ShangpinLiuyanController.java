package com.cgb.product.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.cgb.common.R;
import com.cgb.product.entity.ShangpinLiuyanEntity;
import com.cgb.product.service.ShangpinLiuyanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "商品留言")
@RestController
@RequestMapping("/shangpin/liuyan")
@RequiredArgsConstructor
public class ShangpinLiuyanController {

    private final ShangpinLiuyanService liuyanService;

    @Operation(summary = "发表留言")
    @PostMapping
    public R<?> save(@RequestBody ShangpinLiuyanEntity entity, HttpServletRequest request) {
        Long userId = Long.parseLong(request.getHeader("X-User-Id"));
        entity.setUserid(userId);
        liuyanService.save(entity);
        return R.ok("留言成功");
    }

    @Operation(summary = "分页查询留言")
    @GetMapping("/list")
    public R<?> list(@Parameter(hidden = true) ShangpinLiuyanEntity params,
                     @RequestParam(defaultValue = "1") Integer page,
                     @RequestParam(defaultValue = "10") Integer limit) {
        IPage<ShangpinLiuyanEntity> result = liuyanService.queryPage(params);
        return R.ok(result.getRecords());
    }

    @Operation(summary = "删除留言")
    @DeleteMapping("/{id}")
    public R<?> delete(@PathVariable Long id) {
        liuyanService.delete(id);
        return R.ok("删除成功");
    }
}