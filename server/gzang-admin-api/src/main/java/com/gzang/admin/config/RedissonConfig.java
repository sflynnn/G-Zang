package com.gzang.admin.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Redisson 配置类
 * 禁用密码认证以适配无密码的本地 Redis
 *
 * @author G-Zang Team
 */
@Configuration
public class RedissonConfig {

    @Bean(destroyMethod = "shutdown")
    public RedissonClient redissonClient() {
        Config config = new Config();
        config.useSingleServer()
                .setAddress("redis://127.0.0.1:6379")
                .setConnectionMinimumIdleSize(5)
                .setConnectionPoolSize(10)
                .setTimeout(3000)
                .setRetryAttempts(3)
                .setRetryInterval(1500);
        return Redisson.create(config);
    }
}
