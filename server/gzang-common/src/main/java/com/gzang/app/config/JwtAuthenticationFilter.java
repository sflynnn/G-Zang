package com.gzang.app.config;

import com.gzang.app.util.JwtUtil;
import com.gzang.app.util.TenantContextHolder;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT认证过滤器 拦截请求，验证JWT Token，并设置认证信息
 *
 * @author G-Zang Team
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtUtil jwtUtil;
    private final ApplicationContext applicationContext;

    public JwtAuthenticationFilter(JwtUtil jwtUtil, ApplicationContext applicationContext) {
        this.jwtUtil = jwtUtil;
        this.applicationContext = applicationContext;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        try {
            String jwt = getJwtFromRequest(request);

            if (StringUtils.hasText(jwt) && jwtUtil.validateToken(jwt)) {
                Long userId = jwtUtil.getUserIdFromToken(jwt);
                String username = jwtUtil.getUsernameFromToken(jwt);

                // 设置租户上下文
                TenantContextHolder.setCurrentUserId(userId);
                Long companyId = jwtUtil.getCompanyIdFromToken(jwt);
                TenantContextHolder.setCurrentCompanyId(companyId);

                // 懒加载UserDetailsService以避免循环依赖
                UserDetailsService userDetailsService = applicationContext.getBean(CustomUserDetailsService.class);
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                UsernamePasswordAuthenticationToken authentication
                        = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);

                // 设置用户认证信息
            }
        } catch (Exception ex) {
            // 数据库或业务异常不在此处处理，让 filterChain 继续，controller 层会正常返回业务错误
            log.error("JWT验证失败: uri={}, error={}, message={}", request.getRequestURI(), ex.getClass().getSimpleName(), ex.getMessage());
            if (log.isDebugEnabled()) {
                log.debug("JWT验证堆栈:", ex);
            }
        }

        filterChain.doFilter(request, response);

        // 请求结束后清除租户上下文
        TenantContextHolder.clear();
    }

    /**
     * 从请求头中获取JWT Token
     */
    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
