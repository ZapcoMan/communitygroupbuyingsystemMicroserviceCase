package com.cgb.common.annotation;

import java.lang.annotation.*;

/**
 * 标注公开接口，跳过登录鉴权
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface IgnoreAuth {
}