package com.gzang.app.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT 工具类
 * 用于生成和验证 JWT Token
 *
 * @author G-Zang Team
 */
@Component
public class JwtUtil {

    private static final Logger log = LoggerFactory.getLogger(JwtUtil.class);

    private static final String CLAIM_KEY_USER_ID = "userId";
    private static final String CLAIM_KEY_USERNAME = "username";
    private static final String CLAIM_KEY_ROLE_ID = "roleId";
    private static final String CLAIM_KEY_COMPANY_ID = "companyId";

    @Value("${jwt.secret:g-zang-secret-key-must-be-at-least-256-bits-long-for-hs256}")
    private String secret;

    @Value("${jwt.expiration:7200000}") // 默认2小时
    private Long expiration;

    @Value("${jwt.issuer:g-zang}")
    private String issuer;

    /**
     * 生成JWT Token
     */
    public String generateToken(Long userId, String username, Long roleId, Long companyId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(CLAIM_KEY_USER_ID, userId);
        claims.put(CLAIM_KEY_USERNAME, username);
        if (roleId != null) {
            claims.put(CLAIM_KEY_ROLE_ID, roleId);
        }
        if (companyId != null) {
            claims.put(CLAIM_KEY_COMPANY_ID, companyId);
        }

        Date now = new Date();
        Date expirationDate = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuer(issuer)
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(expirationDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 解析Token获取用户名
     */
    public String getUsernameFromToken(String token) {
        return getClaimsFromToken(token).getSubject();
    }

    /**
     * 解析Token获取用户ID
     */
    public Long getUserIdFromToken(String token) {
        Long userId = getClaimsFromToken(token).get(CLAIM_KEY_USER_ID, Long.class);
        return userId;
    }

    /**
     * 解析Token获取角色ID
     */
    public Long getRoleIdFromToken(String token) {
        return getClaimsFromToken(token).get(CLAIM_KEY_ROLE_ID, Long.class);
    }

    /**
     * 解析Token获取公司ID
     */
    public Long getCompanyIdFromToken(String token) {
        return getClaimsFromToken(token).get(CLAIM_KEY_COMPANY_ID, Long.class);
    }

    /**
     * 验证Token是否有效
     */
    public boolean validateToken(String token) {
        try {
            getClaimsFromToken(token);
            return !isTokenExpired(token);
        } catch (JwtException e) {
            log.warn("JWT Token验证失败: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 判断Token是否已过期
     */
    public boolean isTokenExpired(String token) {
        Date expiration = getClaimsFromToken(token).getExpiration();
        return expiration.before(new Date());
    }

    /**
     * 刷新Token
     */
    public String refreshToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return generateToken(
                claims.get(CLAIM_KEY_USER_ID, Long.class),
                claims.getSubject(),
                claims.get(CLAIM_KEY_ROLE_ID, Long.class),
                claims.get(CLAIM_KEY_COMPANY_ID, Long.class)
        );
    }

    private Claims getClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private SecretKey getSigningKey() {
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
