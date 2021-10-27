package com.w1nd.grainmall.product.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class MyRedissonConfig {
    @Bean(destroyMethod = "shutdown")
    RedissonClient redisson() throws IOException {
        // 1. 创建配置
        Config config = new Config();
        config.useSingleServer().setAddress("redis://192.168.9.3:6379");
        RedissonClient redissonClient = Redisson.create(config);
        return redissonClient;
    }
}
