package com.cgb.common.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 分页工具类 PageUtils 的单元测试
 */
@DisplayName("PageUtils - 分页工具类")
class PageUtilsTest {

    @Nested
    @DisplayName("构造函数")
    class ConstructorTests {

        @Test
        @DisplayName("有参构造 - 正确计算总页数")
        void parameterizedConstructor() {
            List<String> list = new ArrayList<>();
            PageUtils pageUtils = new PageUtils(list, 100, 1, 10);

            assertEquals(100, pageUtils.getTotal());
            assertEquals(1, pageUtils.getPage());
            assertEquals(10, pageUtils.getLimit());
            assertEquals(10, pageUtils.getTotalPage());
        }

        @Test
        @DisplayName("总记录数不能被每页整除 - 总页数向上取整")
        void totalPageRoundingUp() {
            PageUtils pageUtils = new PageUtils(new ArrayList<>(), 101, 1, 10);
            assertEquals(11, pageUtils.getTotalPage(), "101条记录/每页10条 = 11页");
        }

        @Test
        @DisplayName("零记录 - 总页数为0")
        void zeroRecords() {
            PageUtils pageUtils = new PageUtils(new ArrayList<>(), 0, 1, 10);
            assertEquals(0, pageUtils.getTotalPage());
        }

        @Test
        @DisplayName("记录数少于每页条数 - 总页数为1")
        void lessThanOnePage() {
            PageUtils pageUtils = new PageUtils(new ArrayList<>(), 5, 1, 10);
            assertEquals(1, pageUtils.getTotalPage());
        }

        @Test
        @DisplayName("无参构造 - 所有字段为默认值0")
        void defaultConstructor() {
            PageUtils pageUtils = new PageUtils();
            assertEquals(0, pageUtils.getTotal());
            assertEquals(0, pageUtils.getPage());
            assertEquals(0, pageUtils.getLimit());
            assertEquals(0, pageUtils.getTotalPage());
        }
    }

    @Nested
    @DisplayName("Setter / Getter")
    class SetterGetterTests {

        @Test
        @DisplayName("通过 setter 设置所有字段")
        void settersAndGetters() {
            PageUtils pageUtils = new PageUtils();
            pageUtils.setTotal(50);
            pageUtils.setPage(3);
            pageUtils.setLimit(10);
            pageUtils.setTotalPage(5);

            assertEquals(50, pageUtils.getTotal());
            assertEquals(3, pageUtils.getPage());
            assertEquals(10, pageUtils.getLimit());
            assertEquals(5, pageUtils.getTotalPage());
        }
    }

    @Nested
    @DisplayName("序列化")
    class SerializableTests {

        @Test
        @DisplayName("PageUtils 实现了 Serializable 接口")
        void isSerializable() {
            PageUtils pageUtils = new PageUtils();
            assertInstanceOf(java.io.Serializable.class, pageUtils);
        }
    }
}
