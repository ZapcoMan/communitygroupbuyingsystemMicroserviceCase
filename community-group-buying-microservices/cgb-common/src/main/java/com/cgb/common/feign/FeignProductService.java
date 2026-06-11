package com.cgb.common.feign;

import com.cgb.common.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

/**
 * 商品服务 Feign 客户端
 */
@FeignClient(name = "cgb-product-service", contextId = "product")
public interface FeignProductService {

    @GetMapping("/product/internal/productDetail")
    R<?> getProductDetail(@RequestParam("id") Long id);

    @GetMapping("/product/internal/productName")
    R<?> getProductName(@RequestParam("id") Long id);
}