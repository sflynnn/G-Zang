package com.gzang.app.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT工具类 用于生成和解析JWT Token
 *
 * @author G-Zang Team
 */
@Component
public class JwtUtil {

    private static final Logger log = LoggerFactory.getLogger(JwtUtil.class);

    /**
     * JWT密钥
     */
    @Value("${jwt.secret:gzang-secret-key-2024}")
    private String secret;

    /**
     * JWT过期时间（毫秒）
     */
    @Value("${jwt.expiration:86400000}")
    private long expiration;

    /**
     * 获取签名密钥
     */
    private SecretKey getSigningKey() {
        // 确保密钥长度至少为256位（32字节）
        byte[] keyBytes = secret.getBytes();
        if (keyBytes.length < 32) {
            // 如果密钥太短，使用默认的安全密钥
            return Keys.secretKeyFor(SignatureAlgorithm.HS256);
        }
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * 生成JWT Token
     *
     * @param userId 用户ID
     * @param username 用户名
     * @param roleId 角色ID
     * @param companyId 公司ID（可选）
     * @return JWT Token
     */
    public String generateToken(Long userId, String username, Long roleId, Long companyId) {
        // 参数校验
        if (userId == null) {
            throw new IllegalArgumentException("userId cannot be null");
        }
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("username cannot be null or empty");
        }
        if (roleId == null) {
            throw new IllegalArgumentException("roleId cannot be null");
        }

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("username", username);
        claims.put("roleId", roleId);
        if (companyId != null) {
            claims.put("companyId", companyId);
        }

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 生成JWT Token（兼容旧方法）
     *
     * @param userId 用户ID
     * @param username 用户名
     * @param roleId 角色ID
     * @return JWT Token
     */
    public String generateToken(Long userId, String username, Long roleId) {
        return generateToken(userId, username, roleId, null);
    }

    /**
     * 从Token中获取用户名
     *
     * @param token JWT Token
     * @return 用户名
     */
    public String getUsernameFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.getSubject();
    }

    /**
     * 从Token中获取用户ID
     *
     * @param token JWT Token
     * @return 用户ID
     */
    public Long getUserIdFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return Long.valueOf(claims.get("userId").toString());
    }

    /**
     * 从Token中获取角色ID
     *
     * @param token JWT Token
     * @return 角色ID
     */
    public Long getRoleIdFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return Long.valueOf(claims.get("roleId").toString());
    }

    /**
     * 从Token中获取公司ID
     *
     * @param token JWT Token
     * @return 公司ID，如果不存在返回null
     */
    public Long getCompanyIdFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        Object companyId = claims.get("companyId");
        return companyId != null ? Long.valueOf(companyId.toString()) : null;
    }

    /**
     * 验证Token是否有效
     *
     * @param token JWT Token
     * @return 是否有效
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (SecurityException ex) {
            log.error("Invalid JWT signature: {}", ex.getMessage());
        } catch (MalformedJwtException ex) {
            log.error("Invalid JWT token: {}", ex.getMessage());
        } catch (ExpiredJwtException ex) {
            log.error("Expired JWT token: {}", ex.getMessage());
        } catch (UnsupportedJwtException ex) {
            log.error("Unsupported JWT token: {}", ex.getMessage());
        } catch (IllegalArgumentException ex) {
            log.error("JWT claims string is empty: {}", ex.getMessage());
        }
        return false;
    }

    /**
     * 获取Token中的Claims
     *
     * @param token JWT Token
     * @return Claims
     */
    private Claims getClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * 检查Token是否过期
     *
     * @param token JWT Token
     * @return 是否过期
     */
    public boolean isTokenExpired(String token) {
        try {
            Claims claims = getClaimsFromToken(token);
            return claims.getExpiration().before(new Date());
        } catch (Exception e) {
            return true;
        }
    }
}
