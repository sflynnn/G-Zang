package com.gzang.admin.config;

import com.gzang.app.config.SecurityConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * 管理端安全配置
 * 继承公共安全配置并添加管理端特定配置
 *
 * @author G-Zang Team
 */
@Configuration
@Import(SecurityConfig.class)
public class AdminSecurityConfig {
}
