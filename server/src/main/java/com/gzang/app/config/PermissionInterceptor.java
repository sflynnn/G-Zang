package com.gzang.app.config;

import com.gzang.app.annotation.RequirePermission;
import com.gzang.app.exception.BusinessException;
import com.gzang.app.util.TenantContextHolder;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Arrays;

/**
 * 权限拦截器
 * 在 Controller 方法执行前校验功能权限码
 *
 * @author G-Zang Team
 */
@Component
public class PermissionInterceptor implements HandlerInterceptor {

    private static final Logger log = LoggerFactory.getLogger(PermissionInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        HandlerMethod handlerMethod = (HandlerMethod) handler;
        RequirePermission requirePermission = handlerMethod.getMethodAnnotation(RequirePermission.class);

        // 没有标注权限注解，直接放行
        if (requirePermission == null) {
            // 类级别也检查一下
            requirePermission = handlerMethod.getBeanType().getAnnotation(RequirePermission.class);
            if (requirePermission == null) {
                return true;
            }
        }

        String[] requiredPermissions = requirePermission.value();
        if (requiredPermissions == null || requiredPermissions.length == 0) {
            return true;
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof CustomUserDetails)) {
            throw new BusinessException(401, "未认证或认证信息无效");
        }

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        if (requirePermission.logical() == RequirePermission.Logical.AND) {
            // AND 关系：必须全部拥有
            for (String permission : requiredPermissions) {
                if (!userDetails.hasPermission(permission)) {
                    log.warn("权限不足：用户={}, 需要权限={}, 请求路径={}",
                            userDetails.getUsername(), permission, request.getRequestURI());
                    throw new BusinessException(403, "权限不足：" + permission);
                }
            }
        } else {
            // OR 关系：拥有任意一个即可
            boolean hasAnyPermission = Arrays.stream(requiredPermissions)
                    .anyMatch(userDetails::hasPermission);
            if (!hasAnyPermission) {
                log.warn("权限不足：用户={}, 需要任意权限={}, 请求路径={}",
                        userDetails.getUsername(), Arrays.toString(requiredPermissions), request.getRequestURI());
                throw new BusinessException(403, "权限不足");
            }
        }

        log.debug("权限校验通过：用户={}, 权限={}", userDetails.getUsername(), Arrays.toString(requiredPermissions));
        return true;
    }
}
