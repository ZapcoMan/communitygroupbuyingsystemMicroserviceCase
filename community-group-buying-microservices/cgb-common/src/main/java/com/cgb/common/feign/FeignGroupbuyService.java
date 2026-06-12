package com.cgb.common.feign;

import com.cgb.common.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

/**
 * 团购服务 Feign 客户端
 */
@FeignClient(name = "cgb-groupbuy-service", contextId = "groupbuy", fallbackFactory = FeignGroupbuyServiceFallbackFactory.class)
public interface FeignGroupbuyService {

    /** 获取团购详情 */
    @GetMapping("/tuanwei/internal/detail")
    R<?> getGroupBuyDetail(@RequestParam("id") Long id);

    /** 增加参团人数（原子操作） */
    @PostMapping("/tuanwei/internal/increaseMember")
    R<?> increaseMember(@RequestParam("id") Long id, @RequestParam("count") Integer count);

    /** 获取团购参团人数 */
    @GetMapping("/tuanwei/internal/memberCount")
    R<?> getMemberCount(@RequestParam("id") Long id);
}
