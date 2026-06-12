package com.cgb.common.auth;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 内部接口认证测试
 */
class InternalAuthConstantsTest {

    @Test
    @DisplayName("有效 Token 验证通过")
    void isValid_shouldAcceptValidToken() {
        assertTrue(InternalAuthConstants.isValid(InternalAuthConstants.TOKEN));
    }

    @Test
    @DisplayName("无效 Token 验证失败")
    void isValid_shouldRejectInvalidToken() {
        assertFalse(InternalAuthConstants.isValid("wrong-token"));
    }

    @Test
    @DisplayName("null Token 验证失败")
    void isValid_shouldRejectNullToken() {
        assertFalse(InternalAuthConstants.isValid(null));
    }

    @Test
    @DisplayName("空字符串 Token 验证失败")
    void isValid_shouldRejectEmptyToken() {
        assertFalse(InternalAuthConstants.isValid(""));
    }
}
