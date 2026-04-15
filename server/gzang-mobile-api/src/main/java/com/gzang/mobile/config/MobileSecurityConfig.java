package com.gzang.mobile.config;

import com.gzang.app.config.SecurityConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * 移动端安全配置
 * 继承公共安全配置并添加移动端特定配置
 *
 * @author G-Zang Team
 */
@Configuration
@Import(SecurityConfig.class)
public class MobileSecurityConfig {
}
