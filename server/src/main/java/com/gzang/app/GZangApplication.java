package com.gzang.app;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * G-Zang (归藏) 财务管理系统主启动类
 *
 * @author G-Zang Team
 */
@SpringBootApplication
@MapperScan("com.gzang.app.mapper")
@EnableAsync
public class GZangApplication {

    public static void main(String[] args) {
        SpringApplication.run(GZangApplication.class, args);
    }
}
