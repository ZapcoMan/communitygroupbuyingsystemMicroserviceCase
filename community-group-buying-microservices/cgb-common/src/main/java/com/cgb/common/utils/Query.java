package com.cgb.common.utils;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Map;

/**
 * 查询参数构建工具
 */
@SuppressWarnings("unchecked")
public class Query<T> {

    public IPage<T> getPage(Map<String, Object> params) {
        long curPage = 1;
        long limit = 10;
        String orderField = "id";
        String orderDirection = "desc";

        if (params.get("page") != null) {
            curPage = Long.parseLong(params.get("page").toString());
        }
        if (params.get("limit") != null) {
            limit = Long.parseLong(params.get("limit").toString());
        }
        if (params.get("orderField") != null) {
            orderField = params.get("orderField").toString();
        }
        if (params.get("orderDirection") != null) {
            orderDirection = params.get("orderDirection").toString();
        }

        Page<T> page = new Page<>(curPage, limit);
        // 升序/降序
        if ("asc".equalsIgnoreCase(orderDirection)) {
            page.setAsc(orderField);
        } else {
            page.setDesc(orderField);
        }

        return page;
    }

    /**
     * 通用条件构造（排除分页参数）
     */
    public QueryWrapper<T> getCondition(Map<String, Object> params) {
        QueryWrapper<T> wrapper = new QueryWrapper<>();
        if (params == null || params.isEmpty()) return wrapper;

        params.forEach((key, value) -> {
            if (value == null || "".equals(value)) return;
            if ("page".equals(key) || "limit".equals(key)
                || "orderField".equals(key) || "orderDirection".equals(key)) return;

            if (value instanceof String && StringUtils.isNotBlank((String) value)) {
                wrapper.like(key, value);
            } else {
                wrapper.eq(key, value);
            }
        });
        return wrapper;
    }
}