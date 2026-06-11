package com.cgb.common.utils;

import com.cgb.common.EIException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * SQL 注入过滤器 SQLFilter 的单元测试
 */
@DisplayName("SQLFilter - SQL 注入过滤器")
class SQLFilterTest {

    @Nested
    @DisplayName("filter(String, String) 单值过滤")
    class SingleValueFilterTests {

        @Test
        @DisplayName("正常值 - 不抛异常")
        void normalValue() {
            assertDoesNotThrow(() -> SQLFilter.filter("张三", "name"));
        }

        @Test
        @DisplayName("null 值 - 不抛异常")
        void nullValue() {
            assertDoesNotThrow(() -> SQLFilter.filter((String) null, "name"));
        }

        @Test
        @DisplayName("空字符串 - 不抛异常")
        void emptyValue() {
            assertDoesNotThrow(() -> SQLFilter.filter("", "name"));
        }

        @Test
        @DisplayName("包含 'select' 关键字 - 不抛异常（不在过滤列表中）")
        void selectKeyword() {
            // select 不在过滤关键字列表中，应放行
            assertDoesNotThrow(() -> SQLFilter.filter("selectBox", "field"));
        }

        @Test
        @DisplayName("包含 'delete' 关键字 - 抛出 EIException")
        void deleteKeyword() {
            EIException ex = assertThrows(EIException.class,
                    () -> SQLFilter.filter("delete from users", "sql"));
            assertTrue(ex.getMessage().contains("非法字符"));
            assertTrue(ex.getMessage().contains("delete"));
        }

        @Test
        @DisplayName("包含 'DROP' 关键字（大写）- 抛出 EIException")
        void dropKeywordUpperCase() {
            assertThrows(EIException.class,
                    () -> SQLFilter.filter("DROP TABLE users", "sql"));
        }

        @Test
        @DisplayName("包含 'insert' 关键字 - 抛出 EIException")
        void insertKeyword() {
            assertThrows(EIException.class,
                    () -> SQLFilter.filter("insert into table", "param"));
        }

        @Test
        @DisplayName("包含 'update' 关键字 - 抛出 EIException")
        void updateKeyword() {
            assertThrows(EIException.class,
                    () -> SQLFilter.filter("update users set name='x'", "param"));
        }

        @Test
        @DisplayName("包含 'script' 关键字 - 抛出 EIException（XSS 防护）")
        void scriptKeyword() {
            assertThrows(EIException.class,
                    () -> SQLFilter.filter("<script>alert(1)</script>", "input"));
        }

        @Test
        @DisplayName("包含 'javascript' 关键字 - 抛出 EIException")
        void javascriptKeyword() {
            assertThrows(EIException.class,
                    () -> SQLFilter.filter("javascript:void(0)", "url"));
        }

        @Test
        @DisplayName("包含 'exec' 关键字 - 抛出 EIException")
        void execKeyword() {
            assertThrows(EIException.class,
                    () -> SQLFilter.filter("exec sp_help", "cmd"));
        }

        @Test
        @DisplayName("包含 'truncate' 关键字 - 抛出 EIException")
        void truncateKeyword() {
            assertThrows(EIException.class,
                    () -> SQLFilter.filter("truncate table logs", "sql"));
        }
    }

    @Nested
    @DisplayName("filter(String[], String) 数组过滤")
    class ArrayFilterTests {

        @Test
        @DisplayName("正常数组 - 不抛异常")
        void normalArray() {
            assertDoesNotThrow(() -> SQLFilter.filter(new String[]{"name", "age"}, "fields"));
        }

        @Test
        @DisplayName("null 数组 - 不抛异常")
        void nullArray() {
            assertDoesNotThrow(() -> SQLFilter.filter((String[]) null, "fields"));
        }

        @Test
        @DisplayName("空数组 - 不抛异常")
        void emptyArray() {
            assertDoesNotThrow(() -> SQLFilter.filter(new String[]{}, "fields"));
        }

        @Test
        @DisplayName("数组中包含非法关键字 - 抛出 EIException")
        void arrayWithIllegalKeyword() {
            assertThrows(EIException.class,
                    () -> SQLFilter.filter(new String[]{"name", "delete from users"}, "fields"));
        }
    }

    @Nested
    @DisplayName("join() 数组拼接")
    class JoinTests {

        @Test
        @DisplayName("join(['a','b','c']) = 'a,b,c'")
        void join_normal() {
            assertEquals("a,b,c", SQLFilter.join(new String[]{"a", "b", "c"}));
        }

        @Test
        @DisplayName("join(null) = ''")
        void join_null() {
            assertEquals("", SQLFilter.join(null));
        }

        @Test
        @DisplayName("join([]) = ''")
        void join_empty() {
            assertEquals("", SQLFilter.join(new String[]{}));
        }

        @Test
        @DisplayName("join(['single']) = 'single'")
        void join_singleElement() {
            assertEquals("single", SQLFilter.join(new String[]{"single"}));
        }
    }
}
