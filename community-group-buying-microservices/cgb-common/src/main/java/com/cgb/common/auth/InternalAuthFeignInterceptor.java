package com.cgb.common.auth;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Feign 请求拦截器 - 自动注入内部服务 Token
 * 所有 Feign 调用自动携带 X-Internal-Token，用于内部接口鉴权
 */
@Slf4j
@Component
public class InternalAuthFeignInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate template) {
        template.header(InternalAuthConstants.HEADER_NAME, InternalAuthConstants.TOKEN);
        log.debug("Feign 请求注入内部 Token: url={}", template.url());
    }
}
