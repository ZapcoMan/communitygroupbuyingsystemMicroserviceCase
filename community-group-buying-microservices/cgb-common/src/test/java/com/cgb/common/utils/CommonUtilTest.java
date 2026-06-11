package com.cgb.common.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 通用工具类 CommonUtil 的单元测试
 */
@DisplayName("CommonUtil - 通用工具类")
class CommonUtilTest {

    // ========== ID 生成 ==========

    @Nested
    @DisplayName("ID 生成")
    class IdGenerationTests {

        @Test
        @DisplayName("generateId() - 生成非空 UUID")
        void generateId_notEmpty() {
            String id = CommonUtil.generateId();
            assertNotNull(id);
            assertFalse(id.isEmpty());
        }

        @Test
        @DisplayName("generateId() - 多次调用生成不同 ID")
        void generateId_unique() {
            Set<String> ids = new HashSet<>();
            for (int i = 0; i < 100; i++) {
                ids.add(CommonUtil.generateId());
            }
            assertEquals(100, ids.size(), "100次生成应全部唯一");
        }

        @Test
        @DisplayName("generateOrderId() - 生成非空订单号")
        void generateOrderId_notEmpty() {
            String orderId = CommonUtil.generateOrderId();
            assertNotNull(orderId);
            assertFalse(orderId.isEmpty());
        }

        @Test
        @DisplayName("generateOrderId() - 多次调用生成不同订单号")
        void generateOrderId_unique() {
            Set<String> ids = new HashSet<>();
            for (int i = 0; i < 100; i++) {
                ids.add(CommonUtil.generateOrderId());
            }
            assertEquals(100, ids.size(), "100次生成应全部唯一");
        }
    }

    // ========== 时间戳 ==========

    @Nested
    @DisplayName("时间戳")
    class TimestampTests {

        @Test
        @DisplayName("currentTimeSeconds() - 返回秒级时间戳")
        void currentTimeSeconds() {
            long seconds = CommonUtil.currentTimeSeconds();
            long expected = System.currentTimeMillis() / 1000;
            assertTrue(Math.abs(seconds - expected) <= 1, "秒级时间戳误差应≤1秒");
        }

        @Test
        @DisplayName("currentTimeMillis() - 返回毫秒级时间戳")
        void currentTimeMillis() {
            long millis = CommonUtil.currentTimeMillis();
            long expected = System.currentTimeMillis();
            assertTrue(Math.abs(millis - expected) <= 100, "毫秒时间戳误差应≤100ms");
        }
    }

    // ========== 日期格式化 ==========

    @Nested
    @DisplayName("日期格式化")
    class DateFormatTests {

        @Test
        @DisplayName("dateFormat() - 正常格式化日期")
        void dateFormat_normal() {
            Date date = new Date(1700000000000L); // 2023-11-14
            String result = CommonUtil.dateFormat(date, "yyyy-MM-dd");
            assertNotNull(result);
            assertTrue(result.matches("\\d{4}-\\d{2}-\\d{2}"));
        }

        @Test
        @DisplayName("dateFormat(null) - 返回空字符串")
        void dateFormat_null() {
            assertEquals("", CommonUtil.dateFormat(null, "yyyy-MM-dd"));
        }
    }

    // ========== 字符串判空 ==========

    @Nested
    @DisplayName("字符串判空 isEmpty / isNotEmpty")
    class StringEmptyTests {

        @Test
        @DisplayName("isEmpty(null) = true")
        void isEmpty_null() {
            assertTrue(CommonUtil.isEmpty((String) null));
        }

        @Test
        @DisplayName("isEmpty('') = true")
        void isEmpty_empty() {
            assertTrue(CommonUtil.isEmpty(""));
        }

        @Test
        @DisplayName("isEmpty('   ') = true（纯空格）")
        void isEmpty_blank() {
            assertTrue(CommonUtil.isEmpty("   "));
        }

        @Test
        @DisplayName("isEmpty('hello') = false")
        void isEmpty_notEmpty() {
            assertFalse(CommonUtil.isEmpty("hello"));
        }

        @Test
        @DisplayName("isNotEmpty('hello') = true")
        void isNotEmpty_normal() {
            assertTrue(CommonUtil.isNotEmpty("hello"));
        }

        @Test
        @DisplayName("isNotEmpty(null) = false")
        void isNotEmpty_null() {
            assertFalse(CommonUtil.isNotEmpty(null));
        }
    }

    // ========== 对象/集合判空 ==========

    @Nested
    @DisplayName("对象/集合/Map 判空")
    class ObjectEmptyTests {

        @Test
        @DisplayName("isEmpty(Object) - null 返回 true")
        void isEmptyObject_null() {
            assertTrue(CommonUtil.isEmpty((Object) null));
        }

        @Test
        @DisplayName("isEmpty(Object) - 非 null 返回 false")
        void isEmptyObject_notNull() {
            assertFalse(CommonUtil.isEmpty("hello"));
        }

        @Test
        @DisplayName("isEmpty(Map) - null 返回 true")
        void isEmptyMap_null() {
            assertTrue(CommonUtil.isEmpty((Map<?, ?>) null));
        }

        @Test
        @DisplayName("isEmpty(Map) - 空 Map 返回 true")
        void isEmptyMap_empty() {
            assertTrue(CommonUtil.isEmpty(new HashMap<>()));
        }

        @Test
        @DisplayName("isEmpty(Map) - 非空 Map 返回 false")
        void isEmptyMap_notEmpty() {
            Map<String, String> map = new HashMap<>();
            map.put("key", "value");
            assertFalse(CommonUtil.isEmpty(map));
        }

        @Test
        @DisplayName("isEmpty(Collection) - null 返回 true")
        void isEmptyCollection_null() {
            assertTrue(CommonUtil.isEmpty((Collection<?>) null));
        }

        @Test
        @DisplayName("isEmpty(Collection) - 空集合返回 true")
        void isEmptyCollection_empty() {
            assertTrue(CommonUtil.isEmpty(new ArrayList<>()));
        }

        @Test
        @DisplayName("isEmpty(Collection) - 非空集合返回 false")
        void isEmptyCollection_notEmpty() {
            assertFalse(CommonUtil.isEmpty(List.of("a")));
        }
    }

    // ========== 类型转换 ==========

    @Nested
    @DisplayName("类型转换")
    class TypeConversionTests {

        @Test
        @DisplayName("toInteger('123') = 123")
        void toInteger_normal() {
            assertEquals(123, CommonUtil.toInteger("123"));
        }

        @Test
        @DisplayName("toInteger(null) = null")
        void toInteger_null() {
            assertNull(CommonUtil.toInteger(null));
        }

        @Test
        @DisplayName("toLong('9999999999') = 9999999999")
        void toLong_normal() {
            assertEquals(9999999999L, CommonUtil.toLong("9999999999"));
        }

        @Test
        @DisplayName("toLong(null) = null")
        void toLong_null() {
            assertNull(CommonUtil.toLong(null));
        }

        @Test
        @DisplayName("toDouble('3.14') = 3.14")
        void toDouble_normal() {
            assertEquals(3.14, CommonUtil.toDouble("3.14"), 0.001);
        }

        @Test
        @DisplayName("toDouble(null) = null")
        void toDouble_null() {
            assertNull(CommonUtil.toDouble(null));
        }

        @Test
        @DisplayName("toDecimal('99.99') = 99.99")
        void toDecimal_normal() {
            BigDecimal result = CommonUtil.toDecimal("99.99");
            assertEquals(new BigDecimal("99.99"), result);
        }

        @Test
        @DisplayName("toDecimal(null) = null")
        void toDecimal_null() {
            assertNull(CommonUtil.toDecimal(null));
        }

        @Test
        @DisplayName("toInteger('abc') - 抛出 NumberFormatException")
        void toInteger_invalid() {
            assertThrows(NumberFormatException.class, () -> CommonUtil.toInteger("abc"));
        }
    }

    // ========== isNumeric ==========

    @Nested
    @DisplayName("isNumeric 数字判断")
    class IsNumericTests {

        @Test
        @DisplayName("isNumeric('123') = true")
        void isNumeric_integer() {
            assertTrue(CommonUtil.isNumeric("123"));
        }

        @Test
        @DisplayName("isNumeric('3.14') = true")
        void isNumeric_decimal() {
            assertTrue(CommonUtil.isNumeric("3.14"));
        }

        @Test
        @DisplayName("isNumeric('-10') = true")
        void isNumeric_negative() {
            assertTrue(CommonUtil.isNumeric("-10"));
        }

        @Test
        @DisplayName("isNumeric('abc') = false")
        void isNumeric_notNumeric() {
            assertFalse(CommonUtil.isNumeric("abc"));
        }

        @Test
        @DisplayName("isNumeric(null) = false")
        void isNumeric_null() {
            assertFalse(CommonUtil.isNumeric(null));
        }

        @Test
        @DisplayName("isNumeric('') = false")
        void isNumeric_empty() {
            assertFalse(CommonUtil.isNumeric(""));
        }
    }

    // ========== JSON 解析 ==========

    @Nested
    @DisplayName("getJsonVal JSON 字段提取")
    class JsonValTests {

        @Test
        @DisplayName("getJsonVal - 正常提取字段")
        void getJsonVal_normal() {
            String json = "{\"name\":\"张三\",\"age\":25}";
            assertEquals("张三", CommonUtil.getJsonVal(json, "name"));
        }

        @Test
        @DisplayName("getJsonVal - 字段不存在返回 null")
        void getJsonVal_keyNotExist() {
            String json = "{\"name\":\"张三\"}";
            assertNull(CommonUtil.getJsonVal(json, "phone"));
        }

        @Test
        @DisplayName("getJsonVal - 非法 JSON 返回 null")
        void getJsonVal_invalidJson() {
            assertNull(CommonUtil.getJsonVal("not-a-json", "key"));
        }

        @Test
        @DisplayName("getJsonVal - null JSON 返回 null")
        void getJsonVal_nullJson() {
            assertNull(CommonUtil.getJsonVal(null, "key"));
        }
    }

    // ========== 随机码生成 ==========

    @Nested
    @DisplayName("随机码生成")
    class RandomCodeTests {

        @Test
        @DisplayName("generateRandomCode() - 生成6位数字")
        void generateRandomCode() {
            String code = CommonUtil.generateRandomCode();
            assertNotNull(code);
            assertEquals(6, code.length());
            assertTrue(code.matches("\\d{6}"), "应为6位纯数字");
        }

        @Test
        @DisplayName("generateRandomStr(length) - 生成指定长度字符串")
        void generateRandomStr() {
            String str = CommonUtil.generateRandomStr(10);
            assertNotNull(str);
            assertEquals(10, str.length());
        }
    }
}
