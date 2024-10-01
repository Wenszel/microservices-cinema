package com.example.cinema.configuration;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

@Configuration
public class RedissonConfig {
    @Value("${redis.server.address}")
    private String redisServerAddress;
    @Bean
    public RedissonClient createRedissonClient() {
        Config config = new Config();
        config.useSingleServer().setAddress(redisServerAddress);
        return Redisson.create(config);
    }
}
