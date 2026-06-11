package com.cgb.gateway.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("网关 Redis Token 会话服务测试")
class RedisTokenServiceTest {

    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @InjectMocks
    private RedisTokenService redisTokenService;

    // ========== saveToken 测试 ==========

    @Nested
    @DisplayName("保存Token会话")
    class SaveTokenTests {

        @Test
        @DisplayName("保存Token - 正确构造key和value")
        void saveToken_validInput_savedCorrectly() {
            when(redisTemplate.opsForValue()).thenReturn(valueOperations);

            redisTokenService.saveToken("jwt-token-xyz", "1", "admin", "users");

            verify(valueOperations).set(
                    eq("cgb:token:jwt-token-xyz"),
                    eq("1:admin:users"),
                    eq(3600L),
                    eq(TimeUnit.SECONDS));
        }
    }

    // ========== refreshToken 测试 ==========

    @Nested
    @DisplayName("刷新Token有效期")
    class RefreshTokenTests {

        @Test
        @DisplayName("刷新Token - key存在时刷新有效期")
        void refreshToken_exists_refreshed() {
            when(redisTemplate.hasKey("cgb:token:jwt-token-xyz")).thenReturn(true);

            redisTokenService.refreshToken("jwt-token-xyz");

            verify(redisTemplate).expire("cgb:token:jwt-token-xyz", 3600L, TimeUnit.SECONDS);
        }

        @Test
        @DisplayName("刷新Token - key不存在时不执行刷新")
        void refreshToken_notExists_notRefreshed() {
            when(redisTemplate.hasKey("cgb:token:not-exist")).thenReturn(false);

            redisTokenService.refreshToken("not-exist");

            verify(redisTemplate, never()).expire(anyString(), anyLong(), any());
        }
    }

    // ========== getTokenSession 测试 ==========

    @Nested
    @DisplayName("获取Token会话")
    class GetTokenSessionTests {

        @Test
        @DisplayName("获取会话 - 存在时返回会话值")
        void getTokenSession_exists_returnsSession() {
            when(redisTemplate.opsForValue()).thenReturn(valueOperations);
            when(valueOperations.get("cgb:token:jwt-token-xyz")).thenReturn("1:admin:users");

            String session = redisTokenService.getTokenSession("jwt-token-xyz");

            assertEquals("1:admin:users", session);
        }

        @Test
        @DisplayName("获取会话 - 不存在时返回null")
        void getTokenSession_notExists_returnsNull() {
            when(redisTemplate.opsForValue()).thenReturn(valueOperations);
            when(valueOperations.get("cgb:token:not-exist")).thenReturn(null);

            assertNull(redisTokenService.getTokenSession("not-exist"));
        }
    }

    // ========== existsToken 测试 ==========

    @Nested
    @DisplayName("检查Token是否存在")
    class ExistsTokenTests {

        @Test
        @DisplayName("Token存在 - 返回true")
        void existsToken_exists_returnsTrue() {
            when(redisTemplate.hasKey("cgb:token:jwt-token-xyz")).thenReturn(true);

            assertTrue(redisTokenService.existsToken("jwt-token-xyz"));
        }

        @Test
        @DisplayName("Token不存在 - 返回false")
        void existsToken_notExists_returnsFalse() {
            when(redisTemplate.hasKey("cgb:token:not-exist")).thenReturn(false);

            assertFalse(redisTokenService.existsToken("not-exist"));
        }

        @Test
        @DisplayName("Redis返回null - 返回false")
        void existsToken_nullResult_returnsFalse() {
            when(redisTemplate.hasKey("cgb:token:test")).thenReturn(null);

            assertFalse(redisTokenService.existsToken("test"));
        }
    }

    // ========== deleteToken 测试 ==========

    @Nested
    @DisplayName("删除Token")
    class DeleteTokenTests {

        @Test
        @DisplayName("删除Token - 成功")
        void deleteToken_success() {
            when(redisTemplate.delete("cgb:token:jwt-token-xyz")).thenReturn(true);

            redisTokenService.deleteToken("jwt-token-xyz");

            verify(redisTemplate).delete("cgb:token:jwt-token-xyz");
        }

        @Test
        @DisplayName("删除Token - key不存在也正常执行")
        void deleteToken_notExists_noError() {
            when(redisTemplate.delete("cgb:token:not-exist")).thenReturn(false);

            redisTokenService.deleteToken("not-exist");

            verify(redisTemplate).delete("cgb:token:not-exist");
        }
    }
}
