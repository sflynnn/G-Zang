package com.gzang.app.constant;

/**
 * 权限码常量类
 *
 * @author G-Zang Team
 */
public final class PermissionCode {

    private PermissionCode() {
    }

    // ========== 用户管理 ==========
    public static final String USER_LIST = "system:user:list";
    public static final String USER_QUERY = "system:user:query";
    public static final String USER_CREATE = "system:user:create";
    public static final String USER_UPDATE = "system:user:update";
    public static final String USER_DELETE = "system:user:delete";
    public static final String USER_EXPORT = "system:user:export";
    public static final String USER_RESET_PASSWORD = "system:user:resetPassword";
    public static final String USER_CHANGE_STATUS = "system:user:changeStatus";
    public static final String USER_ASSIGN_ROLE = "system:user:assignRole";

    // ========== 角色管理 ==========
    public static final String ROLE_LIST = "system:role:list";
    public static final String ROLE_QUERY = "system:role:query";
    public static final String ROLE_CREATE = "system:role:create";
    public static final String ROLE_UPDATE = "system:role:update";
    public static final String ROLE_DELETE = "system:role:delete";
    public static final String ROLE_EXPORT = "system:role:export";
    public static final String ROLE_ASSIGN_PERMISSION = "system:role:assignPermission";

    // ========== 菜单管理 ==========
    public static final String MENU_LIST = "system:menu:list";
    public static final String MENU_QUERY = "system:menu:query";
    public static final String MENU_CREATE = "system:menu:create";
    public static final String MENU_UPDATE = "system:menu:update";
    public static final String MENU_DELETE = "system:menu:delete";

    // ========== 公司管理 ==========
    public static final String COMPANY_LIST = "system:company:list";
    public static final String COMPANY_QUERY = "system:company:query";
    public static final String COMPANY_CREATE = "system:company:create";
    public static final String COMPANY_UPDATE = "system:company:update";
    public static final String COMPANY_DELETE = "system:company:delete";
    public static final String COMPANY_EXPORT = "system:company:export";

    // ========== 账户管理 ==========
    public static final String ACCOUNT_LIST = "finance:account:list";
    public static final String ACCOUNT_QUERY = "finance:account:query";
    public static final String ACCOUNT_CREATE = "finance:account:create";
    public static final String ACCOUNT_UPDATE = "finance:account:update";
    public static final String ACCOUNT_DELETE = "finance:account:delete";

    // ========== 交易管理 ==========
    public static final String TRANSACTION_LIST = "finance:transaction:list";
    public static final String TRANSACTION_QUERY = "finance:transaction:query";
    public static final String TRANSACTION_CREATE = "finance:transaction:create";
    public static final String TRANSACTION_UPDATE = "finance:transaction:update";
    public static final String TRANSACTION_DELETE = "finance:transaction:delete";
    public static final String TRANSACTION_EXPORT = "finance:transaction:export";

    // ========== 分类管理 ==========
    public static final String CATEGORY_LIST = "finance:category:list";
    public static final String CATEGORY_QUERY = "finance:category:query";
    public static final String CATEGORY_CREATE = "finance:category:create";
    public static final String CATEGORY_UPDATE = "finance:category:update";
    public static final String CATEGORY_DELETE = "finance:category:delete";

    // ========== 报表统计 ==========
    public static final String REPORT_OVERVIEW = "report:overview:view";
    public static final String REPORT_TRANSACTION = "report:transaction:view";
    public static final String REPORT_TREND = "report:trend:view";

    // ========== 操作日志 ==========
    public static final String OPERATION_LOG_LIST = "monitor:operationLog:list";
    public static final String OPERATION_LOG_QUERY = "monitor:operationLog:query";
    public static final String OPERATION_LOG_DELETE = "monitor:operationLog:delete";
    public static final String OPERATION_LOG_EXPORT = "monitor:operationLog:export";
}
