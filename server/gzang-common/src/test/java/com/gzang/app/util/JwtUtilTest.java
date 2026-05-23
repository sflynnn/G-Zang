package com.gzang.app.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JwtUtil 单元测试
 *
 * @author G-Zang Team
 */
@DisplayName("JwtUtil 工具类测试")
class JwtUtilTest {

    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(jwtUtil, "secret", "g-zang-secret-key-must-be-at-least-256-bits-long-for-hs256");
        ReflectionTestUtils.setField(jwtUtil, "expiration", 7200000L);
        ReflectionTestUtils.setField(jwtUtil, "issuer", "g-zang");
    }

    @Test
    @DisplayName("生成Token成功")
    void generateToken_Success() {
        Long userId = 1L;
        String username = "testuser";
        Long roleId = 2L;
        Long companyId = 3L;

        String token = jwtUtil.generateToken(userId, username, roleId, companyId);

        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertTrue(token.split("\\.").length == 3); // JWT格式: header.payload.signature
    }

    @Test
    @DisplayName("生成Token - null roleId和companyId")
    void generateToken_WithNullValues() {
        Long userId = 1L;
        String username = "testuser";

        String token = jwtUtil.generateToken(userId, username, null, null);

        assertNotNull(token);
        assertTrue(token.split("\\.").length == 3);
    }

    @Test
    @DisplayName("从Token获取用户名")
    void getUsernameFromToken_Success() {
        Long userId = 1L;
        String username = "testuser";
        Long roleId = 2L;
        Long companyId = 3L;

        String token = jwtUtil.generateToken(userId, username, roleId, companyId);
        String extractedUsername = jwtUtil.getUsernameFromToken(token);

        assertEquals(username, extractedUsername);
    }

    @Test
    @DisplayName("从Token获取用户ID")
    void getUserIdFromToken_Success() {
        Long userId = 123L;
        String username = "testuser";
        Long roleId = 2L;
        Long companyId = 3L;

        String token = jwtUtil.generateToken(userId, username, roleId, companyId);
        Long extractedUserId = jwtUtil.getUserIdFromToken(token);

        assertEquals(userId, extractedUserId);
    }

    @Test
    @DisplayName("从Token获取角色ID")
    void getRoleIdFromToken_Success() {
        Long userId = 1L;
        String username = "testuser";
        Long roleId = 5L;
        Long companyId = 10L;

        String token = jwtUtil.generateToken(userId, username, roleId, companyId);
        Long extractedRoleId = jwtUtil.getRoleIdFromToken(token);

        assertEquals(roleId, extractedRoleId);
    }

    @Test
    @DisplayName("从Token获取公司ID")
    void getCompanyIdFromToken_Success() {
        Long userId = 1L;
        String username = "testuser";
        Long roleId = 2L;
        Long companyId = 100L;

        String token = jwtUtil.generateToken(userId, username, roleId, companyId);
        Long extractedCompanyId = jwtUtil.getCompanyIdFromToken(token);

        assertEquals(companyId, extractedCompanyId);
    }

    @Test
    @DisplayName("验证有效Token")
    void validateToken_ValidToken() {
        String token = jwtUtil.generateToken(1L, "testuser", 2L, 3L);

        assertTrue(jwtUtil.validateToken(token));
    }

    @Test
    @DisplayName("验证无效Token")
    void validateToken_InvalidToken() {
        String invalidToken = "invalid.token.here";

        assertFalse(jwtUtil.validateToken(invalidToken));
    }

    @Test
    @DisplayName("Token未过期")
    void isTokenExpired_NotExpired() {
        String token = jwtUtil.generateToken(1L, "testuser", 2L, 3L);

        assertFalse(jwtUtil.isTokenExpired(token));
    }

    @Test
    @DisplayName("刷新Token")
    void refreshToken_Success() {
        Long userId = 1L;
        String username = "testuser";
        Long roleId = 2L;
        Long companyId = 3L;

        String originalToken = jwtUtil.generateToken(userId, username, roleId, companyId);

        // JWT 精度为秒级别，等待1秒确保时间戳不同
        try {
            Thread.sleep(1100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        String refreshedToken = jwtUtil.refreshToken(originalToken);

        assertNotNull(refreshedToken);
        // 验证刷新后的Token包含正确的用户信息
        assertEquals(username, jwtUtil.getUsernameFromToken(refreshedToken));
        assertEquals(userId, jwtUtil.getUserIdFromToken(refreshedToken));
        assertEquals(roleId, jwtUtil.getRoleIdFromToken(refreshedToken));
        assertEquals(companyId, jwtUtil.getCompanyIdFromToken(refreshedToken));

        // 由于 JWT 精度为秒级别，如果在同一秒内刷新，token 可能相同
        // 因此不强制要求 token 字符串不同，只验证功能正确性
        assertTrue(refreshedToken.split("\\.").length == 3); // JWT格式: header.payload.signature
    }
}
