package com.gzang.app.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 多租户上下文持有者
 * 使用 ThreadLocal 在请求线程中存储当前用户ID和公司ID
 *
 * @author G-Zang Team
 */
public class TenantContextHolder {

    private static final Logger log = LoggerFactory.getLogger(TenantContextHolder.class);
    private static final ThreadLocal<Long> CURRENT_USER_ID = new ThreadLocal<>();
    private static final ThreadLocal<Long> CURRENT_COMPANY_ID = new ThreadLocal<>();

    /**
     * 设置当前用户ID
     */
    public static void setCurrentUserId(Long userId) {
        CURRENT_USER_ID.set(userId);
        log.debug("设置当前用户ID: {}", userId);
    }

    /**
     * 获取当前用户ID
     */
    public static Long getCurrentUserId() {
        return CURRENT_USER_ID.get();
    }

    /**
     * 设置当前公司ID
     */
    public static void setCurrentCompanyId(Long companyId) {
        CURRENT_COMPANY_ID.set(companyId);
        log.debug("设置当前公司ID: {}", companyId);
    }

    /**
     * 获取当前公司ID
     */
    public static Long getCurrentCompanyId() {
        return CURRENT_COMPANY_ID.get();
    }

    /**
     * 清除当前线程的租户信息
     */
    public static void clear() {
        CURRENT_USER_ID.remove();
        CURRENT_COMPANY_ID.remove();
        log.debug("清除当前线程租户信息");
    }
}


