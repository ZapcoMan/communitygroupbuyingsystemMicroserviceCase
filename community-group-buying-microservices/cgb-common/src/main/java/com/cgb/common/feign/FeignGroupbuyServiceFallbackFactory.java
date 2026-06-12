package com.cgb.common.feign;

import com.cgb.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

/**
 * 团购服务降级工厂
 */
@Slf4j
@Component
public class FeignGroupbuyServiceFallbackFactory implements FallbackFactory<FeignGroupbuyService> {

    @Override
    public FeignGroupbuyService create(Throwable cause) {
        log.error("团购服务调用失败，触发降级: {}", cause.getMessage());
        return new FeignGroupbuyService() {
            @Override
            public R<?> getGroupBuyDetail(Long id) {
                return R.fail("团购服务暂不可用");
            }
            @Override
            public R<?> increaseMember(Long id, Integer count) {
                return R.fail("团购服务暂不可用");
            }
            @Override
            public R<?> getMemberCount(Long id) {
                return R.fail("团购服务暂不可用");
            }
        };
    }
}
