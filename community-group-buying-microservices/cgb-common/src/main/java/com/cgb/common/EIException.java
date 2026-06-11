package com.cgb.common;

import lombok.Getter;

/**
 * 业务异常（可预知的异常）
 */
@Getter
public class EIException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private final int code;

    public EIException() {
        super("业务异常");
        this.code = 500;
    }

    public EIException(String msg) {
        super(msg);
        this.code = 500;
    }

    public EIException(int code, String msg) {
        super(msg);
        this.code = code;
    }

    public EIException(ErrorCode errorCode) {
        super(errorCode.getMsg());
        this.code = errorCode.getCode();
    }

    public EIException(String message, Throwable cause) {
        super(message, cause);
        this.code = 500;
    }

    public EIException(int code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }
}