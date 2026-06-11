package com.cgb.common.config;

import com.cgb.common.EIException;
import com.cgb.common.R;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.stream.Collectors;

/**
 * 全局统一异常处理
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 业务异常
     */
    @ExceptionHandler(EIException.class)
    public R<?> handleEIException(EIException e) {
        log.warn("业务异常: {}", e.getMessage());
        return R.fail(e.getCode(), e.getMessage());
    }

    /**
     * 参数校验异常（@Valid 注解）
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public R<?> handleValidationException(MethodArgumentNotValidException e) {
        String msg = e.getBindingResult().getFieldErrors().stream()
            .map(FieldError::getDefaultMessage)
            .collect(Collectors.joining(";"));
        log.warn("参数校验异常: {}", msg);
        return R.fail(400, msg);
    }

    /**
     * 单一参数校验异常（@Validated）
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public R<?> handleConstraintViolation(ConstraintViolationException e) {
        String msg = e.getConstraintViolations().stream()
            .map(ConstraintViolation::getMessage)
            .collect(Collectors.joining(";"));
        log.warn("约束校验异常: {}", msg);
        return R.fail(400, msg);
    }

    /**
     * 参数绑定异常
     */
    @ExceptionHandler(BindException.class)
    public R<?> handleBindException(BindException e) {
        String msg = e.getFieldErrors().stream()
            .map(FieldError::getDefaultMessage)
            .collect(Collectors.joining(";"));
        log.warn("参数绑定异常: {}", msg);
        return R.fail(400, msg);
    }

    /**
     * 参数类型不匹配
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public R<?> handleTypeMismatch(MethodArgumentTypeMismatchException e) {
        log.warn("参数类型不匹配: {} -> {}", e.getName(), e.getRequiredType());
        return R.fail(400, "参数类型错误: " + e.getName());
    }

    /**
     * 缺少请求参数
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public R<?> handleMissingParam(MissingServletRequestParameterException e) {
        log.warn("缺少请求参数: {}", e.getParameterName());
        return R.fail(400, "缺少请求参数: " + e.getParameterName());
    }

    /**
     * 文件上传超限
     */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public R<?> handleMaxUploadSize(MaxUploadSizeExceededException e) {
        log.warn("文件上传超限: {}", e.getMessage());
        return R.fail(400, "文件大小不能超过 10MB");
    }

    /**
     * 404 异常（开启后端 404 才生效）
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    public R<?> handleNotFound(NoHandlerFoundException e) {
        log.warn("接口不存在: {} {}", e.getHttpMethod(), e.getRequestURL());
        return R.fail(404, "接口不存在");
    }

    /**
     * 请求方法不支持
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public R<?> handleMethodNotSupported(HttpRequestMethodNotSupportedException e) {
        log.warn("请求方法不支持: {}", e.getMessage());
        return R.fail(405, "不支持的请求方法: " + e.getMethod());
    }

    /**
     * 其他未处理异常
     */
    @ExceptionHandler(Exception.class)
    public R<?> handleException(Exception e) {
        log.error("系统异常", e);
        return R.fail(500, "系统异常，请稍后重试");
    }
}