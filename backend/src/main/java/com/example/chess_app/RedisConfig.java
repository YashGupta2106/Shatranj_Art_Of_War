package com.example.chess_app;

import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;

@Configuration
public class RedisConfig {
    
    @Value("${spring.redis.password}")
    private String pswd;

    @Value("${spring.redis.host}")
    private String host;

    @Value("${spring.redis.port}")
    private int port;

    @Bean
    public RedisURI redisURI() {
        return RedisURI.builder()
                .withHost(host)
                .withPort(port)
                .withPassword(pswd.toCharArray())
                .build();
    }

    @Bean
    public RedisClient redisClient(RedisURI redisURI) {
        return RedisClient.create(redisURI);
    }

    @Bean
    public StatefulRedisConnection<String, String> redisConnection(RedisClient redisClient) {
        return redisClient.connect();
    }
}


