package com.cgb.common.feign;

import com.cgb.common.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

/**
 * 商品服务 Feign 客户端
 */
@FeignClient(name = "cgb-product-service", contextId = "product", fallbackFactory = FeignProductServiceFallbackFactory.class)
public interface FeignProductService {

    @GetMapping("/shangpin/internal/productDetail")
    R<?> getProductDetail(@RequestParam("id") Long id);

    @GetMapping("/shangpin/internal/productName")
    R<?> getProductName(@RequestParam("id") Long id);

    /**
     * 扣减库存（分布式事务 RM 端）
     */
    @PostMapping("/shangpin/internal/decreaseStock")
    R<?> decreaseStock(@RequestParam("id") Long id, @RequestParam("quantity") Integer quantity);

    /**
     * 回补库存（订单取消时回滚）
     */
    @PostMapping("/shangpin/internal/increaseStock")
    R<?> increaseStock(@RequestParam("id") Long id, @RequestParam("quantity") Integer quantity);
}
