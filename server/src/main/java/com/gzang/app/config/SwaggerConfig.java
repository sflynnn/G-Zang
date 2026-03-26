package com.gzang.app.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger配置类
 *
 * @author G-Zang Team
 */
@Configuration
public class SwaggerConfig {

    /**
     * 配置OpenAPI文档信息
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("G-Zang (归藏) API文档")
                        .description("G-Zang (归藏) 财务管理系统API文档")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("G-Zang Team")
                                .email("team@gzang.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .components(new Components()
                        .addSecuritySchemes("Bearer Authentication",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("在请求头中添加 JWT Token：Authorization: Bearer {token}")))
                .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"));
    }
}
