package com.cgb.user.service;

import org.junit.jupiter.api.BeforeEach;
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

/**
 * RedisTokenService 单元测试
 * 覆盖 Token 的存储、刷新、查询、存在性检查和删除操作
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("RedisTokenService - Redis Token 会话管理测试")
class RedisTokenServiceTest {

    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @InjectMocks
    private RedisTokenService redisTokenService;

    private static final String TOKEN_PREFIX = "cgb:token:";

    @Nested
    @DisplayName("saveToken - 保存 Token")
    class SaveTokenTests {

        @Test
        @DisplayName("正常保存 Token，调用 Redis set 方法")
        void saveToken_validParams_callsRedisSet() {
            when(redisTemplate.opsForValue()).thenReturn(valueOperations);

            redisTokenService.saveToken("abc123", "1", "admin", "users");

            verify(valueOperations).set(
                    eq(TOKEN_PREFIX + "abc123"),
                    eq("1:admin:users"),
                    eq(3600L),
                    eq(TimeUnit.SECONDS)
            );
        }

        @Test
        @DisplayName("保存的值为 userId:role:tableName 格式")
        void saveToken_correctFormat() {
            when(redisTemplate.opsForValue()).thenReturn(valueOperations);

            redisTokenService.saveToken("token-xyz", "42", "user", "yonghu");

            verify(valueOperations).set(
                    eq(TOKEN_PREFIX + "token-xyz"),
                    eq("42:user:yonghu"),
                    anyLong(),
                    any(TimeUnit.class)
            );
        }
    }

    @Nested
    @DisplayName("refreshToken - 刷新 Token 过期时间")
    class RefreshTokenTests {

        @Test
        @DisplayName("Token 存在时刷新过期时间")
        void refreshToken_tokenExists_callsExpire() {
            when(redisTemplate.hasKey(TOKEN_PREFIX + "mytoken")).thenReturn(true);

            redisTokenService.refreshToken("mytoken");

            verify(redisTemplate).expire(TOKEN_PREFIX + "mytoken", 3600L, TimeUnit.SECONDS);
        }

        @Test
        @DisplayName("Token 不存在时不刷新")
        void refreshToken_tokenNotExists_doesNothing() {
            when(redisTemplate.hasKey(TOKEN_PREFIX + "notexist")).thenReturn(false);

            redisTokenService.refreshToken("notexist");

            verify(redisTemplate, never()).expire(anyString(), anyLong(), any(TimeUnit.class));
        }

        @Test
        @DisplayName("Redis 返回 null 时不刷新")
        void refreshToken_redisReturnsNull_doesNothing() {
            when(redisTemplate.hasKey(TOKEN_PREFIX + "mytoken")).thenReturn(null);

            redisTokenService.refreshToken("mytoken");

            verify(redisTemplate, never()).expire(anyString(), anyLong(), any(TimeUnit.class));
        }
    }

    @Nested
    @DisplayName("getTokenSession - 获取 Token 会话信息")
    class GetTokenSessionTests {

        @Test
        @DisplayName("Token 存在时返回会话字符串")
        void getTokenSession_exists_returnsSession() {
            when(redisTemplate.opsForValue()).thenReturn(valueOperations);
            when(valueOperations.get(TOKEN_PREFIX + "mytoken")).thenReturn("1:admin:users");

            String session = redisTokenService.getTokenSession("mytoken");

            assertEquals("1:admin:users", session);
        }

        @Test
        @DisplayName("Token 不存在时返回 null")
        void getTokenSession_notExists_returnsNull() {
            when(redisTemplate.opsForValue()).thenReturn(valueOperations);
            when(valueOperations.get(TOKEN_PREFIX + "notexist")).thenReturn(null);

            String session = redisTokenService.getTokenSession("notexist");

            assertNull(session);
        }
    }

    @Nested
    @DisplayName("existsToken - 检查 Token 是否存在")
    class ExistsTokenTests {

        @Test
        @DisplayName("Token 存在返回 true")
        void existsToken_exists_returnsTrue() {
            when(redisTemplate.hasKey(TOKEN_PREFIX + "mytoken")).thenReturn(true);
            assertTrue(redisTokenService.existsToken("mytoken"));
        }

        @Test
        @DisplayName("Token 不存在返回 false")
        void existsToken_notExists_returnsFalse() {
            when(redisTemplate.hasKey(TOKEN_PREFIX + "notexist")).thenReturn(false);
            assertFalse(redisTokenService.existsToken("notexist"));
        }

        @Test
        @DisplayName("Redis 返回 null 时返回 false")
        void existsToken_redisReturnsNull_returnsFalse() {
            when(redisTemplate.hasKey(TOKEN_PREFIX + "token")).thenReturn(null);
            assertFalse(redisTokenService.existsToken("token"));
        }
    }

    @Nested
    @DisplayName("deleteToken - 删除 Token")
    class DeleteTokenTests {

        @Test
        @DisplayName("正常删除 Token")
        void deleteToken_callsRedisDelete() {
            redisTokenService.deleteToken("mytoken");
            verify(redisTemplate).delete(TOKEN_PREFIX + "mytoken");
        }

        @Test
        @DisplayName("删除不存在的 Token 不抛异常")
        void deleteToken_notExists_noException() {
            assertDoesNotThrow(() -> redisTokenService.deleteToken("nonexistent"));
            verify(redisTemplate).delete(TOKEN_PREFIX + "nonexistent");
        }
    }
}
