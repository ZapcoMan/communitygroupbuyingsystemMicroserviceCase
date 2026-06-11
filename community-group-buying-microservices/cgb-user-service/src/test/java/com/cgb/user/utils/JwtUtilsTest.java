package com.cgb.user.utils;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JwtUtils 单元测试
 * 覆盖 Token 生成、验证、解析、IP 绑定等核心场景
 */
@DisplayName("JwtUtils - JWT 工具类测试")
class JwtUtilsTest {

    private static final String SECRET = "mySecretKeyForHmacSha256MustBeAtLeast256Bits!!";
    private static final long EXPIRATION = 3600L;
    private JwtUtils jwtUtils;

    @BeforeEach
    void setUp() {
        jwtUtils = new JwtUtils(SECRET, EXPIRATION);
    }

    @Nested
    @DisplayName("generateToken - 生成 Token")
    class GenerateTokenTests {

        @Test
        @DisplayName("正常生成 Token，返回非空字符串")
        void generateToken_returnsNonEmptyString() {
            String token = jwtUtils.generateToken(1L, "admin", "127.0.0.1");
            assertNotNull(token);
            assertFalse(token.isEmpty());
        }

        @Test
        @DisplayName("生成的 Token 包含三段（header.payload.signature）")
        void generateToken_hasThreeParts() {
            String token = jwtUtils.generateToken(1L, "user", "192.168.1.1");
            assertEquals(3, token.split("\\.").length);
        }

        @Test
        @DisplayName("不同 userId 生成不同的 Token")
        void generateToken_differentInput_producesDifferentTokens() {
            String token1 = jwtUtils.generateToken(1L, "admin", "127.0.0.1");
            String token2 = jwtUtils.generateToken(2L, "admin", "127.0.0.1");
            assertNotEquals(token1, token2);
        }

        @Test
        @DisplayName("Token 中正确携带 userId")
        void generateToken_containsUserId() {
            String token = jwtUtils.generateToken(42L, "admin", "10.0.0.1");
            Long userId = jwtUtils.getUserId(token);
            assertEquals(42L, userId);
        }

        @Test
        @DisplayName("Token 中正确携带 role")
        void generateToken_containsRole() {
            String token = jwtUtils.generateToken(1L, "user", "10.0.0.1");
            String role = jwtUtils.getRole(token);
            assertEquals("user", role);
        }

        @Test
        @DisplayName("Token 中正确携带 clientIP")
        void generateToken_containsClientIP() {
            String token = jwtUtils.generateToken(1L, "admin", "192.168.1.100");
            Claims claims = jwtUtils.parseToken(token);
            assertNotNull(claims);
            assertEquals("192.168.1.100", claims.get("clientIP", String.class));
        }
    }

    @Nested
    @DisplayName("validateToken - Token 验证")
    class ValidateTokenTests {

        @Test
        @DisplayName("合法 Token 且 IP 匹配，验证通过")
        void validateToken_validToken_sameIP_returnsTrue() {
            String token = jwtUtils.generateToken(1L, "admin", "127.0.0.1");
            assertTrue(jwtUtils.validateToken(token, "127.0.0.1"));
        }

        @Test
        @DisplayName("合法 Token 但 IP 不匹配，验证失败")
        void validateToken_validToken_differentIP_returnsFalse() {
            String token = jwtUtils.generateToken(1L, "admin", "127.0.0.1");
            assertFalse(jwtUtils.validateToken(token, "192.168.1.1"));
        }

        @Test
        @DisplayName("null Token 验证失败")
        void validateToken_nullToken_returnsFalse() {
            assertFalse(jwtUtils.validateToken(null, "127.0.0.1"));
        }

        @Test
        @DisplayName("无效格式 Token 验证失败")
        void validateToken_invalidFormat_returnsFalse() {
            assertFalse(jwtUtils.validateToken("not.a.valid.jwt", "127.0.0.1"));
        }

        @Test
        @DisplayName("过期 Token 验证失败")
        void validateToken_expiredToken_returnsFalse() {
            // 使用 0 秒过期时间创建工具实例
            JwtUtils expiredJwt = new JwtUtils(SECRET, 0L);
            String token = expiredJwt.generateToken(1L, "admin", "127.0.0.1");
            assertFalse(expiredJwt.validateToken(token, "127.0.0.1"));
        }

        @Test
        @DisplayName("使用错误密钥签名，验证失败")
        void validateToken_wrongSecret_returnsFalse() {
            String otherSecret = "anotherSecretKeyThatIsAtLeast256BitsLong!!!";
            JwtUtils otherJwt = new JwtUtils(otherSecret, EXPIRATION);
            String token = otherJwt.generateToken(1L, "admin", "127.0.0.1");
            assertFalse(jwtUtils.validateToken(token, "127.0.0.1"));
        }

        @Test
        @DisplayName("IP 为 null 时不影响验证")
        void validateToken_tokenIPNull_returnsTrue() {
            // clientIP 传 null
            String token = jwtUtils.generateToken(1L, "admin", null);
            // 验证时传任意 IP 都应通过（因为 token 中 IP 为 null，跳过了 IP 检查）
            assertTrue(jwtUtils.validateToken(token, "any-ip"));
        }
    }

    @Nested
    @DisplayName("parseToken - 解析 Token")
    class ParseTokenTests {

        @Test
        @DisplayName("正常解析返回 Claims 对象")
        void parseToken_validToken_returnsClaims() {
            String token = jwtUtils.generateToken(1L, "admin", "127.0.0.1");
            Claims claims = jwtUtils.parseToken(token);
            assertNotNull(claims);
            assertEquals("1", claims.getSubject());
        }

        @Test
        @DisplayName("无效 Token 返回 null")
        void parseToken_invalidToken_returnsNull() {
            Claims claims = jwtUtils.parseToken("invalid-token");
            assertNull(claims);
        }

        @Test
        @DisplayName("null Token 返回 null")
        void parseToken_null_returnsNull() {
            // jjwt 对 null/空字符串抛出 IllegalArgumentException
            assertThrows(IllegalArgumentException.class, () -> jwtUtils.parseToken(null));
        }
    }

    @Nested
    @DisplayName("getUserId - 获取用户ID")
    class GetUserIdTests {

        @Test
        @DisplayName("正常获取用户ID")
        void getUserId_validToken_returnsId() {
            String token = jwtUtils.generateToken(100L, "user", "10.0.0.1");
            assertEquals(100L, jwtUtils.getUserId(token));
        }

        @Test
        @DisplayName("无效 Token 返回 null")
        void getUserId_invalidToken_returnsNull() {
            assertNull(jwtUtils.getUserId("invalid"));
        }
    }

    @Nested
    @DisplayName("getRole - 获取角色")
    class GetRoleTests {

        @Test
        @DisplayName("正常获取角色")
        void getRole_validToken_returnsRole() {
            String token = jwtUtils.generateToken(1L, "admin", "10.0.0.1");
            assertEquals("admin", jwtUtils.getRole(token));
        }

        @Test
        @DisplayName("无效 Token 返回 null")
        void getRole_invalidToken_returnsNull() {
            assertNull(jwtUtils.getRole("invalid"));
        }
    }
}
