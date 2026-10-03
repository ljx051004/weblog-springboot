package com.xinyang.weblog.common.aspect;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)//这个元注解用于指定注解的保留策略，即注解在何时生效
@Target({ElementType.METHOD})//这个元注解用于指定注解的目标元素，即可以在哪些地方使用这个注解
@Documented
public @interface ApiOperationLog {
    /**
     * API 功能描述
     *
     * @return
     */
    String description() default "";
}
