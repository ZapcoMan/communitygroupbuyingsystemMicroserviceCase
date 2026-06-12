package com.cgb.common.feign;

import com.cgb.common.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

/**
 * 用户服务 Feign 客户端
 */
@FeignClient(name = "cgb-user-service", contextId = "user", fallbackFactory = FeignUserServiceFallbackFactory.class)
public interface FeignUserService {

    @GetMapping("/yonghu/internal/userInfo")
    R<?> getUserInfo(@RequestParam("userId") Long userId);

    @GetMapping("/yonghu/internal/checkUser")
    R<?> checkUser(@RequestParam("userId") Long userId);

    @GetMapping("/yonghu/internal/getUsername")
    R<?> getUsername(@RequestParam("userId") Long userId);

    /**
     * 增加用户积分（订单支付成功后调用）
     */
    @PostMapping("/yonghu/internal/addPoints")
    R<?> addPoints(@RequestParam("userId") Long userId, @RequestParam("points") Double points);
}
