package com.gzang.app.constant;

/**
 * 权限码常量类
 * 所有系统权限码在此定义
 *
 * @author G-Zang Team
 */
public final class PermissionCode {

    private PermissionCode() {
    }

    // ========== 系统管理权限（SYSTEM） ==========
    /**
     * 系统配置
     */
    public static final String SYSTEM_CONFIG = "SYSTEM_CONFIG";

    /**
     * 审计日志查看
     */
    public static final String SYSTEM_AUDIT = "SYSTEM_AUDIT";

    // ========== 组织管理权限（ORGANIZATION） ==========
    /**
     * 用户管理（含增删改查）
     */
    public static final String USER_MANAGE = "USER_MANAGE";

    /**
     * 添加用户
     */
    public static final String USER_ADD = "USER_ADD";

    /**
     * 查看用户
     */
    public static final String USER_VIEW = "USER_VIEW";

    /**
     * 编辑用户
     */
    public static final String USER_EDIT = "USER_EDIT";

    /**
     * 删除用户
     */
    public static final String USER_DELETE = "USER_DELETE";

    /**
     * 公司管理
     */
    public static final String COMPANY_MANAGE = "COMPANY_MANAGE";

    /**
     * 角色管理（含增删改查）
     */
    public static final String ROLE_MANAGE = "ROLE_MANAGE";

    /**
     * 添加角色
     */
    public static final String ROLE_ADD = "ROLE_ADD";

    /**
     * 查看角色
     */
    public static final String ROLE_VIEW = "ROLE_VIEW";

    /**
     * 编辑角色
     */
    public static final String ROLE_EDIT = "ROLE_EDIT";

    /**
     * 删除角色
     */
    public static final String ROLE_DELETE = "ROLE_DELETE";

    // ========== 财务核心权限（FINANCE） ==========
    /**
     * 录入交易记录
     */
    public static final String TRANSACTION_ADD = "TRANSACTION_ADD";

    /**
     * 查看交易记录
     */
    public static final String TRANSACTION_VIEW = "TRANSACTION_VIEW";

    /**
     * 编辑交易记录
     */
    public static final String TRANSACTION_EDIT = "TRANSACTION_EDIT";

    /**
     * 删除交易记录
     */
    public static final String TRANSACTION_DELETE = "TRANSACTION_DELETE";

    /**
     * 导出交易记录
     */
    public static final String TRANSACTION_EXPORT = "TRANSACTION_EXPORT";

    /**
     * 添加账户
     */
    public static final String ACCOUNT_ADD = "ACCOUNT_ADD";

    /**
     * 查看账户
     */
    public static final String ACCOUNT_VIEW = "ACCOUNT_VIEW";

    /**
     * 编辑账户
     */
    public static final String ACCOUNT_EDIT = "ACCOUNT_EDIT";

    /**
     * 删除账户
     */
    public static final String ACCOUNT_DELETE = "ACCOUNT_DELETE";

    /**
     * 添加分类
     */
    public static final String CATEGORY_ADD = "CATEGORY_ADD";

    /**
     * 查看分类
     */
    public static final String CATEGORY_VIEW = "CATEGORY_VIEW";

    /**
     * 编辑分类
     */
    public static final String CATEGORY_EDIT = "CATEGORY_EDIT";

    /**
     * 删除分类
     */
    public static final String CATEGORY_DELETE = "CATEGORY_DELETE";

    /**
     * 查看报表
     */
    public static final String REPORT_VIEW = "REPORT_VIEW";

    /**
     * 导出报表
     */
    public static final String REPORT_EXPORT = "REPORT_EXPORT";

    // ========== 业务管理权限（BUSINESS） ==========
    /**
     * 成本中心管理
     */
    public static final String COST_CENTER_MANAGE = "COST_CENTER_MANAGE";

    /**
     * 添加业务订单
     */
    public static final String BUSINESS_ORDER_ADD = "BUSINESS_ORDER_ADD";

    /**
     * 查看业务订单
     */
    public static final String BUSINESS_ORDER_VIEW = "BUSINESS_ORDER_VIEW";

    /**
     * 编辑业务订单
     */
    public static final String BUSINESS_ORDER_EDIT = "BUSINESS_ORDER_EDIT";

    /**
     * 删除业务订单
     */
    public static final String BUSINESS_ORDER_DELETE = "BUSINESS_ORDER_DELETE";

    /**
     * 审批业务订单
     */
    public static final String BUSINESS_ORDER_APPROVE = "BUSINESS_ORDER_APPROVE";
}
