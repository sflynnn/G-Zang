package com.gzang.app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.gzang.app.aspect.OperationLogAspect;

/**
 * G-Zang Common 自动配置类
 * 供 Spring Boot 应用自动扫描加载
 *
 * @author G-Zang Team
 */
@Configuration
@Import({
    MyBatisPlusConfig.class,
    MyBatisPlusMetaObjectHandler.class,
    SwaggerConfig.class,
    WebMvcConfig.class,
    SecurityConfig.class,
    JwtAuthenticationFilter.class,
    CustomUserDetailsService.class,
    TenantInterceptor.class,
    PermissionInterceptor.class,
    OperationLogAspect.class
})
public class GzangCommonAutoConfiguration {

    /**
     * 配置 Druid 数据源监控页面
     * 注意：DruidConfig 应该在 API 模块中配置，因为每个 API 模块可能有不同的数据源
     */
}
