package com.gzang.app.constant;

/**
 * 错误码常量类
 *
 * @author G-Zang Team
 */
public final class ErrorCode {

    private ErrorCode() {
    }

    // ========== 通用错误码 1xxx ==========
    /**
     * 操作成功
     */
    public static final Integer SUCCESS = 0;

    /**
     * 系统内部错误
     */
    public static final Integer SYSTEM_ERROR = 1000;

    /**
     * 参数错误
     */
    public static final Integer PARAM_ERROR = 1001;

    /**
     * 数据不存在
     */
    public static final Integer DATA_NOT_FOUND = 1002;

    /**
     * 数据已存在
     */
    public static final Integer DATA_ALREADY_EXISTS = 1003;

    /**
     * 请求过于频繁
     */
    public static final Integer REQUEST_TOO_FREQUENT = 1004;

    // ========== 认证错误码 2xxx ==========
    /**
     * 未认证（Token 无效或已过期）
     */
    public static final Integer UNAUTHORIZED = 2001;

    /**
     * 登录失败
     */
    public static final Integer LOGIN_FAILED = 2002;

    /**
     * 账号已禁用
     */
    public static final Integer ACCOUNT_DISABLED = 2003;

    /**
     * 账号已被锁定
     */
    public static final Integer ACCOUNT_LOCKED = 2004;

    /**
     * Token 已过期
     */
    public static final Integer TOKEN_EXPIRED = 2005;

    // ========== 权限错误码 3xxx ==========
    /**
     * 权限不足
     */
    public static final Integer PERMISSION_DENIED = 3001;

    /**
     * 禁止访问
     */
    public static final Integer ACCESS_DENIED = 3002;

    /**
     * 权限码不存在
     */
    public static final Integer PERMISSION_NOT_FOUND = 3003;

    /**
     * 角色不存在
     */
    public static final Integer ROLE_NOT_FOUND = 3004;

    /**
     * 角色编码已存在
     */
    public static final Integer ROLE_CODE_EXISTS = 3005;

    /**
     * 无法删除系统内置角色
     */
    public static final Integer CANNOT_DELETE_SYSTEM_ROLE = 3006;

    /**
     * 无法修改系统内置角色
     */
    public static final Integer CANNOT_MODIFY_SYSTEM_ROLE = 3007;

    /**
     * 数据范围超出权限
     */
    public static final Integer DATA_SCOPE_EXCEED = 3008;

    // ========== 用户错误码 4xxx ==========
    /**
     * 用户不存在
     */
    public static final Integer USER_NOT_FOUND = 4001;

    /**
     * 用户名已存在
     */
    public static final Integer USERNAME_EXISTS = 4002;

    /**
     * 密码错误
     */
    public static final Integer PASSWORD_ERROR = 4003;

    /**
     * 旧密码错误
     */
    public static final Integer OLD_PASSWORD_ERROR = 4004;

    // ========== 业务错误码 5xxx ==========
    /**
     * 业务数据验证失败
     */
    public static final Integer BUSINESS_VALIDATION_FAILED = 5001;

    /**
     * 状态不允许操作
     */
    public static final Integer STATUS_NOT_ALLOWED = 5002;

    /**
     * 关联数据不存在
     */
    public static final Integer RELATION_NOT_FOUND = 5003;
}
