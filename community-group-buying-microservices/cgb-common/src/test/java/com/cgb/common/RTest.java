package com.cgb.common;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 统一 API 响应对象 R 的单元测试
 */
@DisplayName("R - 统一响应格式")
class RTest {

    @Nested
    @DisplayName("ok() 成功响应")
    class OkTests {

        @Test
        @DisplayName("ok() - 默认成功响应，code=0，msg='操作成功'")
        void ok_default() {
            R<Object> r = R.ok();
            assertEquals(0, r.getCode());
            assertEquals("操作成功", r.getMsg());
            assertNull(r.getData());
        }

        @Test
        @DisplayName("ok(msg) - 自定义成功消息")
        void ok_withMessage() {
            R<Object> r = R.ok("保存成功");
            assertEquals(0, r.getCode());
            assertEquals("保存成功", r.getMsg());
        }

        @Test
        @DisplayName("ok(data) - 携带数据")
        void ok_withData() {
            Integer data = 42;
            R<Integer> r = R.ok(data);
            assertEquals(0, r.getCode());
            assertEquals("操作成功", r.getMsg());
            assertEquals(42, r.getData());
        }

        @Test
        @DisplayName("ok(msg, data) - 自定义消息+数据")
        void ok_withMessageAndData() {
            R<Integer> r = R.ok("查询成功", 42);
            assertEquals(0, r.getCode());
            assertEquals("查询成功", r.getMsg());
            assertEquals(42, r.getData());
        }

        @Test
        @DisplayName("ok(msg, data, token) - 登录场景，携带 token")
        void ok_withToken() {
            R<String> r = R.ok("登录成功", "user-info", "jwt-token-xxx");
            assertEquals(0, r.getCode());
            assertEquals("登录成功", r.getMsg());
            assertEquals("user-info", r.getData());
            assertEquals("jwt-token-xxx", r.getToken());
        }
    }

    @Nested
    @DisplayName("fail() 失败响应")
    class FailTests {

        @Test
        @DisplayName("fail() - 默认失败响应，code=-1")
        void fail_default() {
            R<Object> r = R.fail();
            assertEquals(-1, r.getCode());
            assertEquals("操作失败", r.getMsg());
        }

        @Test
        @DisplayName("fail(msg) - 自定义失败消息")
        void fail_withMessage() {
            R<Object> r = R.fail("参数错误");
            assertEquals(-1, r.getCode());
            assertEquals("参数错误", r.getMsg());
        }

        @Test
        @DisplayName("fail(code, msg) - 自定义状态码+消息")
        void fail_withCodeAndMessage() {
            R<Object> r = R.fail(403, "禁止访问");
            assertEquals(403, r.getCode());
            assertEquals("禁止访问", r.getMsg());
        }

        @Test
        @DisplayName("fail(ErrorCode) - 通过错误码枚举构建")
        void fail_withErrorCode() {
            R<Object> r = R.fail(ErrorCode.UNAUTHORIZED);
            assertEquals(401, r.getCode());
            assertEquals("未授权，请重新登录", r.getMsg());
        }
    }

    @Nested
    @DisplayName("链式调用")
    class FluentApiTests {

        @Test
        @DisplayName("msg() / code() / data() / token() 链式设置")
        void fluentApi() {
            R<String> r = new R<String>()
                    .code(200)
                    .msg("OK")
                    .data("payload")
                    .token("abc-token");
            assertEquals(200, r.getCode());
            assertEquals("OK", r.getMsg());
            assertEquals("payload", r.getData());
            assertEquals("abc-token", r.getToken());
        }
    }

    @Nested
    @DisplayName("Getter / Setter")
    class AccessorTests {

        @Test
        @DisplayName("setter 和 getter 正常工作")
        void gettersAndSetters() {
            R<String> r = new R<>();
            r.setCode(500);
            r.setMsg("系统异常");
            r.setData("error-detail");
            r.setToken("tok");

            assertEquals(500, r.getCode());
            assertEquals("系统异常", r.getMsg());
            assertEquals("error-detail", r.getData());
            assertEquals("tok", r.getToken());
        }
    }

    @Nested
    @DisplayName("构造函数")
    class ConstructorTests {

        @Test
        @DisplayName("R(code, msg) 两参构造")
        void twoArgConstructor() {
            R<Object> r = new R<>(404, "未找到");
            assertEquals(404, r.getCode());
            assertEquals("未找到", r.getMsg());
            assertNull(r.getData());
        }

        @Test
        @DisplayName("R(code, msg, data) 三参构造")
        void threeArgConstructor() {
            R<String> r = new R<>(0, "成功", "data");
            assertEquals(0, r.getCode());
            assertEquals("成功", r.getMsg());
            assertEquals("data", r.getData());
        }
    }
}
