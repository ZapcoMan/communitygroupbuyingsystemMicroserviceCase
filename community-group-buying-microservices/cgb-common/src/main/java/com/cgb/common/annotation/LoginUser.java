package com.cgb.common.annotation;

import java.lang.annotation.*;

/**
 * 标注需要登录才能访问的接口
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LoginUser {
}