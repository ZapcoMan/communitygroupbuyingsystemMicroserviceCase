package com.cgb.common;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 统一 API 响应格式
 */
@Getter
@Setter
public class R<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    private int code;
    private String msg;
    private T data;
    private String token;

    public R() {
    }

    public R(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public R(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public static <T> R<T> ok() {
        return new R<>(0, "操作成功");
    }

    public static <T> R<T> ok(String msg) {
        return new R<>(0, msg);
    }

    public static <T> R<T> ok(T data) {
        return new R<>(0, "操作成功", data);
    }

    public static <T> R<T> ok(String msg, T data) {
        return new R<>(0, msg, data);
    }


    public static <T> R<T> ok(String msg, T data, String token) {
        R<T> r = new R<>(0, msg, data);
        r.setToken(token);
        return r;
    }

    public static <T> R<T> fail() {
        return new R<>(-1, "操作失败");
    }

    public static <T> R<T> fail(String msg) {
        return new R<>(-1, msg);
    }

    public static <T> R<T> fail(int code, String msg) {
        return new R<>(code, msg);
    }

    public static <T> R<T> fail(ErrorCode code) {
        return new R<>(code.getCode(), code.getMsg());
    }

    public R<T> msg(String msg) {
        this.msg = msg;
        return this;
    }

    public R<T> code(int code) {
        this.code = code;
        return this;
    }

    public R<T> data(T data) {
        this.data = data;
        return this;
    }

    public R<T> token(String token) {
        this.token = token;
        return this;
    }

}