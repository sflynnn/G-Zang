package com.gzang.admin;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * G-Zang 管理端应用启动类
 * 系统管理、用户管理、权限管理接口
 *
 * @author G-Zang Team
 */
@SpringBootApplication
@ComponentScan(basePackages = {"com.gzang.app", "com.gzang.admin"})
@MapperScan({"com.gzang.app.mapper", "com.gzang.admin.mapper"})
public class GzangAdminApplication {

    public static void main(String[] args) {
        SpringApplication.run(GzangAdminApplication.class, args);
    }
}