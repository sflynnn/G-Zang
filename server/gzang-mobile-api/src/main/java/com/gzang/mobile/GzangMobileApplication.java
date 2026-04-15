package com.gzang.mobile;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * G-Zang 移动端应用启动类
 * 移动端 + PC个人记账 共用的 API 服务
 *
 * @author G-Zang Team
 */
@SpringBootApplication
@ComponentScan(basePackages = {
    "com.gzang.app",
    "com.gzang.app.config",
    "com.gzang.mobile"
})
@MapperScan({"com.gzang.app.mapper", "com.gzang.mobile.mapper"})
public class GzangMobileApplication {

    public static void main(String[] args) {
        SpringApplication.run(GzangMobileApplication.class, args);
    }
}
