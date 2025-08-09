package com.example.chess_app;

import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RedisService {

    private final RedisCommands<String, String> redisCommands;
    private static final long DEFAULT_SESSION_TTL_SECONDS = 3600; // 1 hour

    @Autowired
    public RedisService(StatefulRedisConnection<String,String> redisConnection){
        this.redisCommands = redisConnection.sync();
    }

    public void saveSession(String sessionId, String userId) {
        redisCommands.setex(sessionId, DEFAULT_SESSION_TTL_SECONDS, userId);
    }

    public String getUserIdBySession(String sessionId) {
        return redisCommands.get(sessionId);
    }

    public void deleteSession(String sessionId) {
        redisCommands.del(sessionId);
    }

    public boolean isValidSession(String sessionId) {
        return redisCommands.exists(sessionId) > 0;
    }

    public void refreshSessionTTL(String sessionId) {
        redisCommands.expire(sessionId, DEFAULT_SESSION_TTL_SECONDS);
    }
    public Long getSessionTTL(String sessionId) {
        return redisCommands.ttl(sessionId);
    }
}
