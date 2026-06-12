package com.cgb.product.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.cgb.common.R;
import com.cgb.product.entity.ProductInquiryEntity;
import com.cgb.product.service.ProductInquiryService;
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
public class ProductInquiryController {

    private final ProductInquiryService liuyanService;

    @Operation(summary = "发表留言")
    @PostMapping
    public R<?> save(@RequestBody ProductInquiryEntity entity, HttpServletRequest request) {
        Long userId = Long.parseLong(request.getHeader("X-User-Id"));
        entity.setUserId(userId);
        liuyanService.save(entity);
        return R.ok("留言成功");
    }

    @Operation(summary = "分页查询留言")
    @GetMapping("/list")
    public R<?> list(@Parameter(hidden = true) ProductInquiryEntity params,
                     @RequestParam(defaultValue = "1") Integer page,
                     @RequestParam(defaultValue = "10") Integer limit) {
        IPage<ProductInquiryEntity> result = liuyanService.queryPage(params);
        return R.ok(result);
    }

    @Operation(summary = "删除留言")
    @DeleteMapping("/{id}")
    public R<?> delete(@PathVariable Long id) {
        liuyanService.delete(id);
        return R.ok("删除成功");
    }
}