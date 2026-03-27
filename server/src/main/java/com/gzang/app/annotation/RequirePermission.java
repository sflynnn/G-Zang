package com.gzang.app.annotation;

import java.lang.annotation.*;

/**
 * 功能权限校验注解
 * 标注在 Controller 方法上，校验当前用户是否拥有指定权限码
 *
 * @author G-Zang Team
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequirePermission {

    /**
     * 权限码数组，支持多权限校验
     * 默认校验 AND 关系（必须全部拥有）
     */
    String[] value() default {};

    /**
     * 多权限时的逻辑关系
     * AND：必须全部拥有
     * OR：拥有任意一个即可
     */
    Logical logical() default Logical.AND;

    /**
     * 逻辑关系枚举
     */
    enum Logical {
        /**
         * 必须全部拥有
         */
        AND,
        /**
         * 拥有任意一个即可
         */
        OR
    }
}
