package com.cgb.common.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * MD5 工具类的单元测试
 */
@DisplayName("MD5Util - MD5 哈希工具")
class MD5UtilTest {

    @Nested
    @DisplayName("md5() 哈希计算")
    class Md5Tests {

        @Test
        @DisplayName("md5('hello') - 返回已知 MD5 值")
        void md5_hello() {
            // 已知 hello 的 MD5 值为 5d41402abc4b2a76b9719d911017c592
            String expected = "5d41402abc4b2a76b9719d911017c592";
            assertEquals(expected, MD5Util.md5("hello"));
        }

        @Test
        @DisplayName("md5('') - 空字符串的 MD5")
        void md5_emptyString() {
            // 空字符串的 MD5 值为 d41d8cd98f00b204e9800998ecf8427e
            String expected = "d41d8cd98f00b204e9800998ecf8427e";
            assertEquals(expected, MD5Util.md5(""));
        }

        @Test
        @DisplayName("md5() - 同一输入多次调用结果一致")
        void md5_deterministic() {
            String input = "community-group-buying";
            String hash1 = MD5Util.md5(input);
            String hash2 = MD5Util.md5(input);
            assertEquals(hash1, hash2);
        }

        @Test
        @DisplayName("md5() - 不同输入产生不同哈希")
        void md5_differentInputs() {
            String hash1 = MD5Util.md5("password123");
            String hash2 = MD5Util.md5("password456");
            assertNotEquals(hash1, hash2);
        }

        @Test
        @DisplayName("md5() - 返回32位十六进制字符串")
        void md5_format() {
            String hash = MD5Util.md5("test");
            assertNotNull(hash);
            assertEquals(32, hash.length(), "MD5 哈希应为32位");
            assertTrue(hash.matches("[0-9a-f]{32}"), "应为小写十六进制字符串");
        }

        @Test
        @DisplayName("md5() - 中文输入")
        void md5_chinese() {
            String hash = MD5Util.md5("社区团购");
            assertNotNull(hash);
            assertEquals(32, hash.length());
        }
    }
}
