package com.gzang.app.vo;

/**
 * 统一响应格式类 用于统一API接口的响应格式
 *
 * @author G-Zang Team
 */
public class Result<T> {

    /**
     * 业务状态码，0 表示成功，非 0 表示失败
     */
    private Integer code;

    /**
     * 提示信息
     */
    private String message;

    /**
     * 业务数据，成功时返回，失败时可能为 null
     */
    private T data;

    public Result() {
    }

    public Result(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    /**
     * 成功响应（无数据）
     */
    public static Result<Void> success() {
        return new Result<>(0, "操作成功", null);
    }

    /**
     * 成功响应（有数据）
     */
    public static <T> Result<T> success(T data) {
        return new Result<>(0, "操作成功", data);
    }

    /**
     * 成功响应（自定义消息）
     */
    public static <T> Result<T> success(String message, T data) {
        return new Result<>(0, message, data);
    }

    /**
     * 失败响应
     */
    public static Result<Void> error(Integer code, String message) {
        return new Result<>(code, message, null);
    }

    /**
     * 失败响应（有数据）
     */
    public static <T> Result<T> error(Integer code, String message, T data) {
        return new Result<>(code, message, data);
    }

    /**
     * 参数错误响应
     */
    public static Result<Void> paramError() {
        return new Result<>(400, "参数错误", null);
    }

    /**
     * 参数错误响应（自定义消息）
     */
    public static Result<Void> paramError(String message) {
        return new Result<>(400, message, null);
    }

    /**
     * 未认证响应
     */
    public static Result<Void> unauthorized() {
        return new Result<>(401, "未认证", null);
    }

    /**
     * 未认证响应（自定义消息）
     */
    public static Result<Void> unauthorized(String message) {
        return new Result<>(401, message, null);
    }

    /**
     * 无权限响应
     */
    public static Result<Void> forbidden() {
        return new Result<>(403, "无权限", null);
    }

    /**
     * 无权限响应（自定义消息）
     */
    public static Result<Void> forbidden(String message) {
        return new Result<>(403, message, null);
    }

    /**
     * 服务器错误响应
     */
    public static Result<Void> serverError() {
        return new Result<>(500, "服务器内部错误", null);
    }

    /**
     * 服务器错误响应（自定义消息）
     */
    public static Result<Void> serverError(String message) {
        return new Result<>(500, message, null);
    }
}
