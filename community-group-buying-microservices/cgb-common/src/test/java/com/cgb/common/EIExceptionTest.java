package com.cgb.common;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 业务异常 EIException 的单元测试
 */
@DisplayName("EIException - 业务异常")
class EIExceptionTest {

    @Nested
    @DisplayName("构造函数")
    class ConstructorTests {

        @Test
        @DisplayName("无参构造 - 默认消息'业务异常'，code=500")
        void defaultConstructor() {
            EIException ex = new EIException();
            assertEquals("业务异常", ex.getMessage());
            assertEquals(500, ex.getCode());
        }

        @Test
        @DisplayName("EIException(msg) - 自定义消息，code=500")
        void messageConstructor() {
            EIException ex = new EIException("用户名已存在");
            assertEquals("用户名已存在", ex.getMessage());
            assertEquals(500, ex.getCode());
        }

        @Test
        @DisplayName("EIException(code, msg) - 自定义状态码+消息")
        void codeMessageConstructor() {
            EIException ex = new EIException(403, "权限不足");
            assertEquals("权限不足", ex.getMessage());
            assertEquals(403, ex.getCode());
        }

        @Test
        @DisplayName("EIException(ErrorCode) - 通过错误码枚举构建")
        void errorCodeConstructor() {
            EIException ex = new EIException(ErrorCode.USER_NOT_FOUND);
            assertEquals("用户不存在", ex.getMessage());
            assertEquals(404, ex.getCode());
        }

        @Test
        @DisplayName("EIException(message, cause) - 携带原始异常")
        void messageCauseConstructor() {
            RuntimeException cause = new RuntimeException("root cause");
            EIException ex = new EIException("包装异常", cause);
            assertEquals("包装异常", ex.getMessage());
            assertEquals(500, ex.getCode());
            assertSame(cause, ex.getCause());
        }

        @Test
        @DisplayName("EIException(code, message, cause) - 自定义状态码+消息+原始异常")
        void fullConstructor() {
            Exception cause = new IllegalStateException("illegal");
            EIException ex = new EIException(400, "参数错误", cause);
            assertEquals("参数错误", ex.getMessage());
            assertEquals(400, ex.getCode());
            assertSame(cause, ex.getCause());
        }
    }

    @Nested
    @DisplayName("异常类型")
    class TypeTests {

        @Test
        @DisplayName("EIException 是 RuntimeException 的子类")
        void isRuntimeException() {
            EIException ex = new EIException("test");
            assertInstanceOf(RuntimeException.class, ex);
        }
    }
}
