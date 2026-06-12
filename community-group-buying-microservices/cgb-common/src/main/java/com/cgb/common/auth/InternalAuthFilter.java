package com.cgb.common.auth;

import com.cgb.common.R;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * 内部接口认证过滤器
 * 拦截 /internal/** 路径，校验 X-Internal-Token Header
 */
@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 10)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class InternalAuthFilter extends OncePerRequestFilter {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String uri = request.getRequestURI();

        if (uri.contains("/internal/")) {
            String token = request.getHeader(InternalAuthConstants.HEADER_NAME);
            if (!InternalAuthConstants.isValid(token)) {
                log.warn("内部接口未授权访问: uri={}, remoteAddr={}", uri, request.getRemoteAddr());
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write(objectMapper.writeValueAsString(R.fail("内部接口无权访问")));
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}
