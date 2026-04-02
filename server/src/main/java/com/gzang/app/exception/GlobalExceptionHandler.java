package com.gzang.app.exception;

import com.gzang.app.vo.Result;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.util.Set;

/**
 * 全局异常处理器 统一处理各种异常，返回标准格式的错误响应
 *
 * @author G-Zang Team
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理404 Not Found
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<Result<Void>> handleNoHandlerFoundException(NoHandlerFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Result.error(404, "接口不存在: " + ex.getRequestURL()));
    }

    /**
     * 处理404状态码（Spring Boot 2.6+需要配置）
     */
    @ExceptionHandler(org.springframework.web.HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<Result<Void>> handleHttpRequestMethodNotSupportedException(org.springframework.web.HttpRequestMethodNotSupportedException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Result.error(404, "请求方法不支持"));
    }

    /**
     * 处理参数校验异常 (MethodArgumentNotValidException)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Result<Void>> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        FieldError fieldError = ex.getBindingResult().getFieldError();
        String message = fieldError != null ? fieldError.getDefaultMessage() : "参数校验失败";
        // 参数校验异常
        return ResponseEntity.badRequest().body(Result.paramError(message));
    }

    /**
     * 处理数据绑定异常 (BindException)
     */
    @ExceptionHandler(BindException.class)
    public ResponseEntity<Result<Void>> handleBindException(BindException ex) {
        FieldError fieldError = ex.getBindingResult().getFieldError();
        String message = fieldError != null ? fieldError.getDefaultMessage() : "数据绑定失败";
        // 数据绑定异常
        return ResponseEntity.badRequest().body(Result.paramError(message));
    }

    /**
     * 处理约束违反异常 (ConstraintViolationException)
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Result<Void>> handleConstraintViolationException(ConstraintViolationException ex) {
        Set<ConstraintViolation<?>> violations = ex.getConstraintViolations();
        String message = violations.iterator().next().getMessage();
        // 约束违反异常
        return ResponseEntity.badRequest().body(Result.paramError(message));
    }

    /**
     * 处理认证异常 (BadCredentialsException)
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Result<Void>> handleBadCredentialsException(BadCredentialsException ex) {
        // 认证失败
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Result.unauthorized("用户名或密码错误"));
    }

    /**
     * 处理权限不足异常 (AccessDeniedException)
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Result<Void>> handleAccessDeniedException(AccessDeniedException ex) {
        // 权限不足
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Result.forbidden("权限不足"));
    }

    /**
     * 处理业务异常 (BusinessException)
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Result<Void>> handleBusinessException(BusinessException ex) {
        // 业务异常
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Result.error(ex.getCode(), ex.getMessage()));
    }

    /**
     * 处理非法参数异常 (IllegalArgumentException)
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Result<Void>> handleIllegalArgumentException(IllegalArgumentException ex) {
        // 非法参数异常
        return ResponseEntity.badRequest().body(Result.paramError(ex.getMessage()));
    }

    /**
     * 处理运行时异常 (RuntimeException)
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Result<Void>> handleRuntimeException(RuntimeException ex) {
        // 运行时异常
        ex.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Result.serverError("系统异常，请稍后重试: " + ex.getMessage()));
    }

    /**
     * 处理所有其他异常
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Result<Void>> handleException(Exception ex) {
        // 未知异常
        ex.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Result.serverError("系统异常，请稍后重试: " + ex.getMessage()));
    }
}
