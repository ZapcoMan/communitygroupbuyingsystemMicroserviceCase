package com.cgb.common.feign;

import com.cgb.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

/**
 * 商品服务 Feign 降级工厂
 */
@Slf4j
@Component
public class FeignProductServiceFallbackFactory implements FallbackFactory<FeignProductService> {

    @Override
    public FeignProductService create(Throwable cause) {
        log.error("商品服务调用失败", cause);
        return new FeignProductService() {
            @Override
            public R<?> getProductDetail(Long id) {
                return R.fail("商品服务暂不可用，获取商品详情失败");
            }

            @Override
            public R<?> getProductName(Long id) {
                return R.fail("商品服务暂不可用，获取商品名称失败");
            }

            @Override
            public R<?> decreaseStock(Long id, Integer quantity) {
                return R.fail("商品服务暂不可用，扣减库存失败");
            }

            @Override
            public R<?> increaseStock(Long id, Integer quantity) {
                return R.fail("商品服务暂不可用，回补库存失败");
            }
        };
    }
}
