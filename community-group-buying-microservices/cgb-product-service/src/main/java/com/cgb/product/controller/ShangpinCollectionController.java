package com.cgb.product.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.cgb.common.R;
import com.cgb.product.entity.ShangpinCollectionEntity;
import com.cgb.product.service.ShangpinCollectionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "商品收藏")
@RestController
@RequestMapping("/shangpin/collection")
@RequiredArgsConstructor
public class ShangpinCollectionController {

    private final ShangpinCollectionService collectionService;

    @Operation(summary = "收藏/取消收藏商品")
    @PostMapping
    public R<?> toggle(@RequestBody ShangpinCollectionEntity entity, HttpServletRequest request) {
        Long userId = Long.parseLong(request.getHeader("X-User-Id"));
        entity.setUserid(userId);
        ShangpinCollectionEntity exist = collectionService.getByUserAndProduct(userId, entity.getShangpinid());
        if (exist != null) {
            collectionService.delete(exist.getId());
            return R.ok("取消收藏成功");
        }
        collectionService.save(entity);
        return R.ok("收藏成功");
    }

    @Operation(summary = "我的收藏列表")
    @GetMapping("/my")
    public R<?> myList(@Parameter(hidden = true) ShangpinCollectionEntity params,
                       @RequestParam(defaultValue = "1") Integer page,
                       @RequestParam(defaultValue = "10") Integer limit) {
        IPage<ShangpinCollectionEntity> result = collectionService.queryPage(params);
        return R.ok(result.getRecords());
    }
}