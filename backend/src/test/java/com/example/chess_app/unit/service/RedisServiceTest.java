package com.example.chess_app.unit.service;

import com.example.chess_app.RedisService;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for RedisService.
 * Tests session management operations with mocked Redis commands.
 * 
 * Note: These are simplified unit tests. Full integration tests with real Redis
 * are in the integration test suite using Testcontainers.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("RedisService Tests")
class RedisServiceTest {

    @Mock
    private StatefulRedisConnection<String, String> mockConnection;

    @Mock
    private RedisCommands<String, String> redisCommands;

    private RedisService redisService;

    @BeforeEach
    void setUp() {
        when(mockConnection.sync()).thenReturn(redisCommands);
        redisService = new RedisService(mockConnection);
    }

    @Test
    @DisplayName("Should save session with TTL")
    void testSaveSession() {
        String sessionId = "session-123";
        String userId = "user-456";
        
        redisService.saveSession(sessionId, userId);
        
        verify(redisCommands).setex(eq(sessionId), eq(3600L), eq(userId));
    }

    @Test
    @DisplayName("Should retrieve user ID by session")
    void testGetUserIdBySession() {
        String sessionId = "session-123";
        String userId = "user-456";
        
        when(redisCommands.get(sessionId)).thenReturn(userId);
        
        String result = redisService.getUserIdBySession(sessionId);
        
        assertThat(result).isEqualTo(userId);
        verify(redisCommands).get(sessionId);
    }

    @Test
    @DisplayName("Should return null for non-existent session")
    void testGetUserIdForNonExistentSession() {
        when(redisCommands.get(anyString())).thenReturn(null);
        
        String result = redisService.getUserIdBySession("non-existent");
        
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("Should delete session")
    void testDeleteSession() {
        String sessionId = "session-123";
        
        when(redisCommands.del(sessionId)).thenReturn(1L);
        
        redisService.deleteSession(sessionId);
        
        verify(redisCommands).del(sessionId);
    }

    @Test
    @DisplayName("Should check if session is valid")
    void testIsValidSession() {
        String validSession = "valid-session";
        String invalidSession = "invalid-session";
        
        when(redisCommands.exists(validSession)).thenReturn(1L);
        when(redisCommands.exists(invalidSession)).thenReturn(0L);
        
        assertThat(redisService.isValidSession(validSession)).isTrue();
        assertThat(redisService.isValidSession(invalidSession)).isFalse();
    }

    @Test
    @DisplayName("Should refresh session TTL")
    void testRefreshSessionTTL() {
        String sessionId = "session-123";
        
        when(redisCommands.expire(sessionId, 3600L)).thenReturn(true);
        
        redisService.refreshSessionTTL(sessionId);
        
        verify(redisCommands).expire(eq(sessionId), eq(3600L));
    }

    @Test
    @DisplayName("Should get session TTL")
    void testGetSessionTTL() {
        String sessionId = "session-123";
        Long expectedTTL = 3000L;
        
        when(redisCommands.ttl(sessionId)).thenReturn(expectedTTL);
        
        Long ttl = redisService.getSessionTTL(sessionId);
        
        assertThat(ttl).isEqualTo(expectedTTL);
        verify(redisCommands).ttl(sessionId);
    }
}
