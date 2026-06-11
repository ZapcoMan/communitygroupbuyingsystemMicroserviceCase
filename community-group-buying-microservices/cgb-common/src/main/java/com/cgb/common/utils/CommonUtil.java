package com.cgb.common.utils;

import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson.JSONObject;

import jakarta.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 通用工具类
 */
public class CommonUtil {

    /** 生成唯一 ID */
    public static String generateId() {
        return IdUtil.fastSimpleUUID();
    }

    /** 生成订单号 */
    public static String generateOrderId() {
        return IdUtil.getSnowflakeNextIdStr();
    }

    /** 获取当前时间戳（秒） */
    public static long currentTimeSeconds() {
        return System.currentTimeMillis() / 1000;
    }

    /** 获取当前时间戳（毫秒） */
    public static long currentTimeMillis() {
        return System.currentTimeMillis();
    }

    /** 格式化日期 */
    public static String dateFormat(Date date, String pattern) {
        if (date == null) return "";
        return new SimpleDateFormat(pattern).format(date);
    }

    /** 获取客户端真实 IP */
    public static String getClientIP(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return "0:0:0:0:0:0:0:1".equals(ip) ? "127.0.0.1" : ip.split(",")[0].trim();
    }

    /** 判断字符串是否为空（包含 null 和空字符串） */
    public static boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    /** 判断字符串是否不为空 */
    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }

    /** 判断对象是否为空 */
    public static boolean isEmpty(Object obj) {
        return obj == null;
    }

    /** 判断 Map 是否为空 */
    public static boolean isEmpty(Map<?, ?> map) {
        return map == null || map.isEmpty();
    }

    /** 判断 Collection 是否为空 */
    public static boolean isEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

    /** 字符串转 Integer */
    public static Integer toInteger(Object obj) {
        if (obj == null) return null;
        return Integer.parseInt(obj.toString());
    }

    /** 字符串转 Long */
    public static Long toLong(Object obj) {
        if (obj == null) return null;
        return Long.parseLong(obj.toString());
    }

    /** 字符串转 Double */
    public static Double toDouble(Object obj) {
        if (obj == null) return null;
        return Double.parseDouble(obj.toString());
    }

    /** 字符串转 BigDecimal */
    public static BigDecimal toDecimal(Object obj) {
        if (obj == null) return null;
        return new BigDecimal(obj.toString());
    }

    /** 判断是否为数字 */
    public static boolean isNumeric(String str) {
        if (isEmpty(str)) return false;
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /** 获取 JSON 字符串中的字段值 */
    public static String getJsonVal(String json, String key) {
        try {
            JSONObject jsonObject = JSONObject.parseObject(json);
            return jsonObject == null ? null : jsonObject.getString(key);
        } catch (Exception e) {
            return null;
        }
    }

    /** 生成六位随机数 */
    public static String generateRandomCode() {
        Random random = new Random();
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            code.append(random.nextInt(10));
        }
        return code.toString();
    }

    /** 生成指定长度随机字符串 */
    public static String generateRandomStr(int length) {
        return IdUtil.fastSimpleUUID().substring(0, length);
    }
}