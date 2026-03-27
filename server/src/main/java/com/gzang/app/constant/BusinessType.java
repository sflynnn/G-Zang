package com.gzang.app.constant;

/**
 * 操作日志业务类型枚举
 *
 * @author G-Zang Team
 */
public enum BusinessType {

    /**
     * 其他操作
     */
    OTHER,

    /**
     * 查询
     */
    SELECT,

    /**
     * 新增
     */
    CREATE,

    /**
     * 更新
     */
    UPDATE,

    /**
     * 删除
     */
    DELETE,

    /**
     * 导出
     */
    EXPORT,

    /**
     * 导入
     */
    IMPORT,

    /**
     * 授权
     */
    GRANT,

    /**
     * 登录
     */
    LOGIN,

    /**
     * 登出
     */
    LOGOUT,

    /**
     * 权限变更
     */
    PERMISSION_CHANGE
}
