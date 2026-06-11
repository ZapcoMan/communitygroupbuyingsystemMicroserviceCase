package com.cgb.common.utils;

import com.cgb.common.EIException;
import org.apache.commons.lang3.StringUtils;

/**
 * SQL 注入过滤器，防止 SQL 注入攻击
 */
public class SQLFilter {

    public static String join(String[] arr) {
        if (arr == null || arr.length == 0) return "";
        return String.join(",", arr);
    }

    /**
     * 校验 value 是否包含 SQL 关键字
     */
    public static void filter(String value, String name) {
        if (value == null || "".equals(value)) return;

        String sql = "and|exec|execute|insert|delete|update|drop|create|alter|truncate|"
                   + "call|declare|sql|script|scripting|javascript|expression";
        String[] keywords = sql.split("\\|");
        String lowerValue = value.toLowerCase();

        for (String keyword : keywords) {
            if (lowerValue.contains(keyword)) {
                throw new EIException("参数 " + name + " 包含非法字符: " + keyword);
            }
        }
    }

    public static void filter(String[] values, String name) {
        if (values == null || values.length == 0) return;
        for (String v : values) filter(v, name);
    }
}