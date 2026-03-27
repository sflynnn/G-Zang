package com.gzang.app.aspect;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gzang.app.annotation.OperationLog;
import com.gzang.app.config.CustomUserDetails;
import com.gzang.app.mapper.OperationLogMapper;
import com.gzang.app.util.TenantContextHolder;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.time.LocalDateTime;

/**
 * 操作日志 AOP 切面
 * 自动记录带有 @OperationLog 注解的方法调用
 *
 * @author G-Zang Team
 */
@Aspect
@Component
public class OperationLogAspect {

    private static final Logger log = LoggerFactory.getLogger(OperationLogAspect.class);

    private final OperationLogMapper operationLogMapper;
    private final ObjectMapper objectMapper;
    private final ExpressionParser expressionParser = new SpelExpressionParser();

    public OperationLogAspect(OperationLogMapper operationLogMapper, ObjectMapper objectMapper) {
        this.operationLogMapper = operationLogMapper;
        this.objectMapper = objectMapper;
    }

    @Around("@annotation(com.gzang.app.annotation.OperationLog)")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        OperationLog operationLog = method.getAnnotation(OperationLog.class);

        // 获取请求信息
        HttpServletRequest request = getRequest();
        String ipAddress = getClientIp(request);
        String userAgent = request != null ? request.getHeader("User-Agent") : "";
        String requestUri = request != null ? request.getRequestURI() : "";
        String requestMethod = request != null ? request.getMethod() : "";

        // 获取当前用户信息
        Long userId = TenantContextHolder.getCurrentUserId();
        String username = "anonymous";
        Long companyId = TenantContextHolder.getCurrentCompanyId();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            userId = userDetails.getUserId();
            username = userDetails.getUsername();
            companyId = userDetails.getCompanyId();
        }

        // 获取请求参数
        String requestParams = getRequestParams(joinPoint, method);

        // 执行目标方法
        Object result = null;
        Integer responseStatus = 200;
        String errorMessage = null;
        try {
            result = joinPoint.proceed();
            return result;
        } catch (Throwable e) {
            responseStatus = 500;
            errorMessage = e.getMessage();
            throw e;
        } finally {
            long duration = System.currentTimeMillis() - startTime;

            // 解析操作描述（支持 SpEL 表达式）
            String describe = parseDescribe(operationLog.describe(), joinPoint, method);

            // 构建日志对象
            com.gzang.app.entity.OperationLog logEntity = new com.gzang.app.entity.OperationLog();
            logEntity.setUserId(userId);
            logEntity.setUsername(username);
            logEntity.setCompanyId(companyId);
            logEntity.setAction(operationLog.action().name());
            logEntity.setTargetType(operationLog.module());
            logEntity.setTargetId(null); // 由业务方法自行设置
            logEntity.setTargetName(describe);
            logEntity.setDetail(null);
            logEntity.setIpAddress(ipAddress);
            logEntity.setUserAgent(userAgent);
            logEntity.setRequestUri(requestUri);
            logEntity.setRequestMethod(requestMethod);
            logEntity.setRequestParams(truncate(requestParams, 2000));
            logEntity.setResponseStatus(responseStatus);
            logEntity.setErrorMessage(truncate(errorMessage, 500));
            logEntity.setDurationMs((int) duration);
            logEntity.setCreateTime(LocalDateTime.now());

            // 异步记录日志（避免影响主流程）
            try {
                operationLogMapper.insert(logEntity);
            } catch (Exception e) {
                log.error("记录操作日志失败: {}", e.getMessage());
            }
        }
    }

    private HttpServletRequest getRequest() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attributes != null ? attributes.getRequest() : null;
    }

    private String getClientIp(HttpServletRequest request) {
        if (request == null) {
            return "";
        }
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    private String getRequestParams(ProceedingJoinPoint joinPoint, Method method) {
        try {
            Parameter[] parameters = method.getParameters();
            Object[] args = joinPoint.getArgs();
            if (parameters == null || args == null || parameters.length == 0) {
                return "";
            }
            ObjectMapper mapper = new ObjectMapper();
            mapper.setSerializationInclusion(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL);
            return mapper.writeValueAsString(args);
        } catch (Exception e) {
            return "";
        }
    }

    private String parseDescribe(String template, ProceedingJoinPoint joinPoint, Method method) {
        if (template == null || template.isEmpty()) {
            return "";
        }
        // 如果包含 SpEL 表达式
        if (template.contains("#")) {
            try {
                EvaluationContext context = new StandardEvaluationContext();
                Parameter[] parameters = method.getParameters();
                Object[] args = joinPoint.getArgs();
                if (parameters != null && args != null) {
                    for (int i = 0; i < parameters.length; i++) {
                        ((StandardEvaluationContext) context).setVariable(parameters[i].getName(), args[i]);
                    }
                }
                Expression expression = expressionParser.parseExpression(template);
                Object value = expression.getValue(context);
                return value != null ? value.toString() : template;
            } catch (Exception e) {
                log.warn("解析操作描述 SpEL 表达式失败: {}", e.getMessage());
            }
        }
        return template;
    }

    private String truncate(String str, int maxLength) {
        if (str == null) {
            return null;
        }
        return str.length() > maxLength ? str.substring(0, maxLength) : str;
    }
}
