package com.cgb.gateway.utils;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("网关 JWT 工具类测试")
class JwtUtilsTest {

    private JwtUtils jwtUtils;

    private static final String SECRET = "cgb-community-group-buying-jwt-secret-key-2025-must-be-at-least-256-bits";
    private static final long EXPIRATION = 3600L;

    @BeforeEach
    void setUp() {
        jwtUtils = new JwtUtils(SECRET, EXPIRATION);
    }

    // ========== generateToken 测试 ==========

    @Nested
    @DisplayName("生成Token")
    class GenerateTokenTests {

        @Test
        @DisplayName("生成Token - 正常输入返回非空字符串")
        void generateToken_validInput_returnsToken() {
            String token = jwtUtils.generateToken(1L, "admin", "127.0.0.1");

            assertNotNull(token);
            assertFalse(token.isEmpty());
        }

        @Test
        @DisplayName("生成Token - 包含正确的userId和role")
        void generateToken_containsCorrectClaims() {
            String token = jwtUtils.generateToken(100L, "yonghu", "192.168.1.1");

            Claims claims = jwtUtils.parseToken(token);
            assertNotNull(claims);
            assertEquals("100", claims.getSubject());
            assertEquals("yonghu", claims.get("role", String.class));
            assertEquals("192.168.1.1", claims.get("clientIP", String.class));
        }

        @Test
        @DisplayName("生成Token - 不同userId生成不同Token")
        void generateToken_differentInput_producesDifferentTokens() {
            String token1 = jwtUtils.generateToken(1L, "admin", "127.0.0.1");
            String token2 = jwtUtils.generateToken(2L, "admin", "127.0.0.1");

            assertNotEquals(token1, token2);
        }
    }

    // ========== validateToken 测试 ==========

    @Nested
    @DisplayName("验证Token")
    class ValidateTokenTests {

        @Test
        @DisplayName("验证Token - 有效Token返回true")
        void validateToken_validToken_returnsTrue() {
            String token = jwtUtils.generateToken(1L, "admin", "127.0.0.1");

            assertTrue(jwtUtils.validateToken(token, "127.0.0.1"));
        }

        @Test
        @DisplayName("验证Token - IP不匹配返回false")
        void validateToken_ipMismatch_returnsFalse() {
            String token = jwtUtils.generateToken(1L, "admin", "127.0.0.1");

            assertFalse(jwtUtils.validateToken(token, "10.0.0.1"));
        }

        @Test
        @DisplayName("验证Token - 无效Token返回false")
        void validateToken_invalidToken_returnsFalse() {
            assertFalse(jwtUtils.validateToken("invalid.token.here", "127.0.0.1"));
        }

        @Test
        @DisplayName("验证Token - 过期Token返回false")
        void validateToken_expiredToken_returnsFalse() {
            JwtUtils shortExpiryJwt = new JwtUtils(SECRET, 0L);
            String token = shortExpiryJwt.generateToken(1L, "admin", "127.0.0.1");

            assertFalse(jwtUtils.validateToken(token, "127.0.0.1"));
        }

        @Test
        @DisplayName("验证Token - IP为null时跳过IP验证")
        void validateToken_nullTokenIP_skipIpValidation() {
            // 生成Token时IP为null
            String token = jwtUtils.generateToken(1L, "admin", null);

            assertTrue(jwtUtils.validateToken(token, "10.0.0.1"));
        }
    }

    // ========== parseToken 测试 ==========

    @Nested
    @DisplayName("解析Token")
    class ParseTokenTests {

        @Test
        @DisplayName("解析Token - 有效Token返回Claims")
        void parseToken_validToken_returnsClaims() {
            String token = jwtUtils.generateToken(1L, "admin", "127.0.0.1");

            Claims claims = jwtUtils.parseToken(token);

            assertNotNull(claims);
            assertEquals("1", claims.getSubject());
        }

        @Test
        @DisplayName("解析Token - 无效Token返回null")
        void parseToken_invalidToken_returnsNull() {
            Claims claims = jwtUtils.parseToken("not.a.valid.jwt.token");

            assertNull(claims);
        }
    }

    // ========== getUserId 测试 ==========

    @Nested
    @DisplayName("获取用户ID")
    class GetUserIdTests {

        @Test
        @DisplayName("获取用户ID - 正常返回")
        void getUserId_validToken_returnsUserId() {
            String token = jwtUtils.generateToken(42L, "admin", "127.0.0.1");

            Long userId = jwtUtils.getUserId(token);

            assertEquals(42L, userId);
        }

        @Test
        @DisplayName("获取用户ID - 无效Token返回null")
        void getUserId_invalidToken_returnsNull() {
            assertNull(jwtUtils.getUserId("invalid"));
        }
    }

    // ========== getRole 测试 ==========

    @Nested
    @DisplayName("获取角色")
    class GetRoleTests {

        @Test
        @DisplayName("获取角色 - 正常返回")
        void getRole_validToken_returnsRole() {
            String token = jwtUtils.generateToken(1L, "yonghu", "127.0.0.1");

            String role = jwtUtils.getRole(token);

            assertEquals("yonghu", role);
        }

        @Test
        @DisplayName("获取角色 - 无效Token返回null")
        void getRole_invalidToken_returnsNull() {
            assertNull(jwtUtils.getRole("invalid"));
        }
    }
}
