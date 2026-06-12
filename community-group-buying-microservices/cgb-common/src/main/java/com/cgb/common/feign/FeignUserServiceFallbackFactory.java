package com.cgb.common.feign;

import com.cgb.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

/**
 * 用户服务 Feign 降级工厂
 */
@Slf4j
@Component
public class FeignUserServiceFallbackFactory implements FallbackFactory<FeignUserService> {

    @Override
    public FeignUserService create(Throwable cause) {
        log.error("用户服务调用失败", cause);
        return new FeignUserService() {
            @Override
            public R<?> getUserInfo(Long userId) {
                return R.fail("用户服务暂不可用，获取用户信息失败");
            }

            @Override
            public R<?> checkUser(Long userId) {
                return R.fail("用户服务暂不可用，校验用户失败");
            }

            @Override
            public R<?> getUsername(Long userId) {
                return R.fail("用户服务暂不可用，获取用户名失败");
            }

            @Override
            public R<?> addPoints(Long userId, Double points) {
                return R.fail("用户服务暂不可用，增加积分失败");
            }
        };
    }
}
