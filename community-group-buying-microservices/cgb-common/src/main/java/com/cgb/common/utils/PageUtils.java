package com.cgb.common.utils;

import java.io.Serializable;
import java.util.List;

/**
 * 分页工具类
 */
public class PageUtils implements Serializable {
    private static final long serialVersionUID = 1L;

    private int total;        // 总记录数
    private int page;         // 当前页
    private int limit;        // 每页记录数
    private int totalPage;    // 总页数

    public PageUtils() {}

    public PageUtils(List<?> list, int total, int page, int limit) {
        this.total = total;
        this.page = page;
        this.limit = limit;
        this.totalPage = (total + limit - 1) / limit;
    }

    public int getTotal() { return total; }
    public void setTotal(int total) { this.total = total; }
    public int getPage() { return page; }
    public void setPage(int page) { this.page = page; }
    public int getLimit() { return limit; }
    public void setLimit(int limit) { this.limit = limit; }
    public int getTotalPage() { return totalPage; }
    public void setTotalPage(int totalPage) { this.totalPage = totalPage; }
}