package com.gzang.mobile.controller;

import com.gzang.app.entity.User;
import com.gzang.app.service.UserService;
import com.gzang.app.util.JwtUtil;
import com.gzang.app.vo.Result;
import com.gzang.mobile.vo.MobileLoginVO;
import com.gzang.mobile.vo.MobileUserVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * MobileAuthController 单元测试
 *
 * @author G-Zang Team
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("MobileAuthController 移动端认证控制器测试")
class MobileAuthControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private JwtUtil jwtUtil;

    private MobileAuthController controller;

    @BeforeEach
    void setUp() {
        controller = new MobileAuthController(userService, jwtUtil);
    }

    @Test
    @DisplayName("用户登出成功")
    void logout_Success() {
        // When
        Result<Void> result = controller.logout();

        // Then
        assertNotNull(result);
        assertEquals(0, result.getCode()); // 0表示成功
    }

    @Test
    @DisplayName("获取当前用户信息 - 未登录")
    void getCurrentUser_NotLoggedIn() {
        // Given
        try {
            // 设置用户上下文为null
            com.gzang.app.util.TenantContextHolder.clear();
        } catch (Exception e) {
            // 忽略，设置失败不影响测试
        }

        // When & Then
        assertThrows(Exception.class, () -> controller.getCurrentUser());
    }
}
