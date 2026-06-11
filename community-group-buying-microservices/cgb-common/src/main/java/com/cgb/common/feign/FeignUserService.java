package com.cgb.common.feign;

import com.cgb.common.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

/**
 * 用户服务 Feign 客户端
 */
@FeignClient(name = "cgb-user-service", contextId = "user")
public interface FeignUserService {

    @GetMapping("/user/internal/userInfo")
    R<?> getUserInfo(@RequestParam("userId") Long userId);

    @GetMapping("/user/internal/checkUser")
    R<?> checkUser(@RequestParam("userId") Long userId);

    @GetMapping("/user/internal/getUsername")
    R<?> getUsername(@RequestParam("userId") Long userId);
}