package com.cgb.groupbuy.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.cgb.common.R;
import com.cgb.common.annotation.RateLimit;
import com.cgb.groupbuy.dao.TuanweiDao;
import com.cgb.groupbuy.entity.TuanweiEntity;
import com.cgb.groupbuy.entity.TuanxinxiEntity;
import com.cgb.groupbuy.service.TuanweiService;
import com.cgb.groupbuy.service.TuanxinxiService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "团长管理")
@RestController
@RequestMapping("/tuanwei")
@RequiredArgsConstructor
public class TuanweiController {

    private final TuanweiService tuanweiService;
    private final TuanxinxiService tuanxinxiService;
    private final TuanweiDao tuanweiDao;

    @Operation(summary = "发起团购")
    @PostMapping
    @RateLimit(key = "groupbuy_create", count = 5, period = 1, unit = RateLimit.TimeUnit.MINUTES)
    public R<?> save(@RequestBody TuanweiEntity entity, HttpServletRequest request) {
        Long userId = Long.parseLong(request.getHeader("X-User-Id"));
        entity.setUserId(userId);
        tuanweiService.save(entity);
        return R.ok("发起成功");
    }

    @Operation(summary = "参团（分布式事务：+1人+扣库存+MQ通知）")
    @PostMapping("/join/{groupBuyId}")
    @RateLimit(key = "groupbuy_join", count = 10, period = 1, unit = RateLimit.TimeUnit.MINUTES)
    public R<?> joinGroupBuy(@PathVariable Long groupBuyId,
                             @RequestParam(defaultValue = "1") Integer quantity,
                             HttpServletRequest request) {
        Long userId = Long.parseLong(request.getHeader("X-User-Id"));
        tuanxinxiService.joinGroupBuy(groupBuyId, userId, quantity);
        return R.ok("参团成功");
    }

    @Operation(summary = "分页查询团购")
    @GetMapping("/list")
    public R<?> list(@Parameter(hidden = true) TuanweiEntity params) {
        IPage<TuanweiEntity> result = tuanweiService.queryPage(params);
        return R.ok(result);
    }

    @Operation(summary = "团购详情")
    @GetMapping("/{id}")
    public R<?> detail(@PathVariable Long id) {
        return R.ok(tuanweiService.getById(id));
    }

    @Operation(summary = "修改团购")
    @PutMapping
    public R<?> update(@RequestBody TuanweiEntity entity) {
        tuanweiService.update(entity);
        return R.ok("更新成功");
    }

    @Operation(summary = "删除团购")
    @DeleteMapping("/{id}")
    public R<?> delete(@PathVariable Long id) {
        tuanweiService.delete(id);
        return R.ok("删除成功");
    }

    @Operation(summary = "批量删除团购")
    @DeleteMapping("/batch")
    public R<?> batchDelete(@RequestBody java.util.List<Long> ids) {
        ids.forEach(tuanweiService::delete);
        return R.ok("批量删除成功");
    }

    @Operation(summary = "扫描过期团购（管理端）")
    @PostMapping("/expireScan")
    public R<?> expireScan() {
        int count = tuanweiService.expireGroupBuys();
        return R.ok("扫描完成，共处理" + count + "个过期团购");
    }

    /** 内部接口 - 获取团购详情 */
    @GetMapping("/internal/detail")
    public R<?> internalDetail(@RequestParam Long id) {
        return R.ok(tuanweiService.getById(id));
    }

    /** 内部接口 - 原子增加参团人数 */
    @PostMapping("/internal/increaseMember")
    public R<?> internalIncreaseMember(@RequestParam Long id, @RequestParam Integer count) {
        int rows = tuanweiDao.increaseMember(id, count);
        return rows > 0 ? R.ok() : R.fail("操作失败");
    }

    /** 内部接口 - 获取参团人数 */
    @GetMapping("/internal/memberCount")
    public R<?> internalMemberCount(@RequestParam Long id) {
        return R.ok(tuanxinxiService.countByTuanId(id));
    }
}
