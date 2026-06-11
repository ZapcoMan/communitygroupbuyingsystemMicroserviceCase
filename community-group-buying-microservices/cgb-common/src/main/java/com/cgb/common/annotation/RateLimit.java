package com.cgb.common.annotation;

import java.lang.annotation.*;

/**
 * 接口限流注解，配合 Redis + Lua 实现
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RateLimit {
    /** 限流 key，默认使用 "default" */
    String key() default "default";
    /** 单位时间内的最大请求次数 */
    int count() default 100;
    /** 时间周期 */
    int period() default 60;
    /** 时间单位，默认秒 */
    TimeUnit unit() default TimeUnit.SECONDS;

    enum TimeUnit {
        SECONDS(1),
        MINUTES(60),
        HOURS(3600);
        private final int seconds;
        TimeUnit(int seconds) { this.seconds = seconds; }
        public int getSeconds() { return seconds; }
    }
}