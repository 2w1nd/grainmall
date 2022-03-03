package com.w1nd.grainmall.seckill.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.concurrent.ConcurrentHashMap;

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
