package com.gzang.app.annotation;

import com.gzang.app.constant.BusinessType;

import java.lang.annotation.*;

/**
 * 操作日志注解
 * 标注在 Controller 方法上，自动记录操作日志
 *
 * @author G-Zang Team
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface OperationLog {

    /**
     * 操作模块
     */
    String module() default "";

    /**
     * 操作类型
     */
    BusinessType action() default BusinessType.OTHER;

    /**
     * 操作描述，支持 SpEL 表达式
     * 例如："'新增用户：' + #dto.username"
     */
    String describe() default "";
}
