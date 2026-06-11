package com.cgb.content.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.cgb.common.R;
import com.cgb.content.entity.NewsEntity;
import com.cgb.content.service.NewsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "社区公告")
@RestController
@RequestMapping("/news")
@RequiredArgsConstructor
public class NewsController {

    private final NewsService newsService;

    @Operation(summary = "新增公告")
    @PostMapping
    public R<?> save(@RequestBody NewsEntity entity) {
        newsService.save(entity);
        return R.ok("发布成功");
    }

    @Operation(summary = "公告列表")
    @GetMapping("/list")
    public R<?> list(@Parameter(hidden = true) NewsEntity params) {
        IPage<NewsEntity> result = newsService.queryPage(params);
        return R.ok(result.getRecords());
    }

    @Operation(summary = "公告详情")
    @GetMapping("/{id}")
    public R<?> detail(@PathVariable Long id) {
        return R.ok(newsService.getById(id));
    }

    @Operation(summary = "修改公告")
    @PutMapping
    public R<?> update(@RequestBody NewsEntity entity) {
        newsService.update(entity);
        return R.ok("更新成功");
    }

    @Operation(summary = "删除公告")
    @DeleteMapping("/{id}")
    public R<?> delete(@PathVariable Long id) {
        newsService.delete(id);
        return R.ok("删除成功");
    }
}