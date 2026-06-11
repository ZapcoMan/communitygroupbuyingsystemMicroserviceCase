package com.cgb.common.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 前端分页参数封装 JQPageInfo 的单元测试
 */
@DisplayName("JQPageInfo - 前端分页参数封装")
class JQPageInfoTest {

    @Nested
    @DisplayName("setPage() 偏移量计算")
    class OffsetCalculationTests {

        @Test
        @DisplayName("第1页，每页10条 - offset=0")
        void firstPage() {
            JQPageInfo pageInfo = new JQPageInfo();
            pageInfo.setLimit(10);
            pageInfo.setPage(1);
            assertEquals("0", pageInfo.getOffset());
        }

        @Test
        @DisplayName("第2页，每页10条 - offset=10")
        void secondPage() {
            JQPageInfo pageInfo = new JQPageInfo();
            pageInfo.setLimit(10);
            pageInfo.setPage(2);
            assertEquals("10", pageInfo.getOffset());
        }

        @Test
        @DisplayName("第3页，每页20条 - offset=40")
        void thirdPage() {
            JQPageInfo pageInfo = new JQPageInfo();
            pageInfo.setLimit(20);
            pageInfo.setPage(3);
            assertEquals("40", pageInfo.getOffset());
        }
    }

    @Nested
    @DisplayName("Setter / Getter")
    class SetterGetterTests {

        @Test
        @DisplayName("所有字段的 setter 和 getter")
        void allFields() {
            JQPageInfo pageInfo = new JQPageInfo();
            pageInfo.setLimit(15);
            pageInfo.setPage(2);
            pageInfo.setSidx("id");
            pageInfo.setOrder("desc");

            assertEquals(2, pageInfo.getPage());
            assertEquals(15, pageInfo.getLimit());
            assertEquals("id", pageInfo.getSidx());
            assertEquals("desc", pageInfo.getOrder());
            assertEquals("15", pageInfo.getOffset());
        }
    }
}
