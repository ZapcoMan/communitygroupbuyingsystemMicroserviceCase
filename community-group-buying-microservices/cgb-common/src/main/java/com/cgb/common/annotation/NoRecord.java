package com.cgb.common.annotation;

import java.lang.annotation.*;

/**
 * 标注操作不记录操作日志（用于高频或敏感接口）
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface NoRecord {
}