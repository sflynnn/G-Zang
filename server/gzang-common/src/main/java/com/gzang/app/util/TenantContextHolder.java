package com.gzang.app.util;

import org.springframework.stereotype.Component;

/**
 * 租户上下文持有器
 * 用于在当前线程中存储和获取租户信息
 *
 * @author G-Zang Team
 */
@Component
public class TenantContextHolder {

    private static final ThreadLocal<Long> TENANT_ID = new ThreadLocal<>();
    private static final ThreadLocal<Long> USER_ID = new ThreadLocal<>();
    private static final ThreadLocal<Long> COMPANY_ID = new ThreadLocal<>();

    public static void setTenantId(Long tenantId) {
        TENANT_ID.set(tenantId);
    }

    public static Long getTenantId() {
        return TENANT_ID.get();
    }

    public static void setUserId(Long userId) {
        USER_ID.set(userId);
    }

    public static void setCurrentUserId(Long userId) {
        USER_ID.set(userId);
    }

    public static Long getUserId() {
        return USER_ID.get();
    }

    public static Long getCurrentUserId() {
        return USER_ID.get();
    }

    public static void setCompanyId(Long companyId) {
        COMPANY_ID.set(companyId);
    }

    public static void setCurrentCompanyId(Long companyId) {
        COMPANY_ID.set(companyId);
    }

    public static Long getCompanyId() {
        return COMPANY_ID.get();
    }

    public static Long getCurrentCompanyId() {
        return COMPANY_ID.get();
    }

    public static void clear() {
        TENANT_ID.remove();
        USER_ID.remove();
        COMPANY_ID.remove();
    }

    public static boolean hasTenant() {
        return TENANT_ID.get() != null;
    }

    public static boolean hasCompany() {
        return COMPANY_ID.get() != null;
    }
}
