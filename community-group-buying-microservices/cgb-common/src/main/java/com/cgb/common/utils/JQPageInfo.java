package com.cgb.common.utils;

/**
 * 前端分页参数封装
 */
public class JQPageInfo {
    private Integer page;    // 当前页（从1开始）
    private Integer limit;   // 每页条数
    private String sidx;     // 排序字段
    private String order;    // asc / desc
    private String offset;   // 计算出的偏移量

    public Integer getPage() { return page; }
    public void setPage(Integer page) {
        this.page = page;
        this.offset = (page - 1) * limit + "";
    }
    public Integer getLimit() { return limit; }
    public void setLimit(Integer limit) { this.limit = limit; }
    public String getSidx() { return sidx; }
    public void setSidx(String sidx) { this.sidx = sidx; }
    public String getOrder() { return order; }
    public void setOrder(String order) { this.order = order; }
    public String getOffset() { return offset; }
    public void setOffset(String offset) { this.offset = offset; }
}