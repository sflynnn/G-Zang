package com.gzang.admin.controller;

import com.gzang.app.util.JwtUtil;
import com.gzang.app.dto.LoginRequest;
import com.gzang.app.dto.RegisterRequest;
import com.gzang.app.entity.User;
import com.gzang.app.exception.BusinessException;
import com.gzang.app.service.UserService;
import com.gzang.app.vo.AdminUserVO;
import com.gzang.app.vo.Result;
import com.gzang.admin.vo.AdminLoginVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Collections;

/**
 * 管理端认证控制器
 *
 * @author G-Zang Team
 */
@RestController
@RequestMapping("/api/admin/auth")
@Tag(name = "管理端认证", description = "管理端认证接口")
public class AdminAuthController {

    private static final Logger log = LoggerFactory.getLogger(AdminAuthController.class);

    private final UserService userService;
    private final JwtUtil jwtUtil;

    public AdminAuthController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    /**
     * 管理员登录
     */
    @PostMapping("/login")
    @Operation(summary = "管理员登录", description = "管理端用户登录接口")
    public Result<AdminLoginVO> login(@Validated @RequestBody LoginRequest loginRequest) {
        log.info("管理端登录请求: {}", loginRequest.getUsername());

        User user = userService.login(loginRequest.getUsername(), loginRequest.getPassword());
        if (user == null) {
            throw new BusinessException(401, "用户名或密码错误");
        }

        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRoleId(), user.getCompanyId());

        AdminLoginVO loginVO = new AdminLoginVO();
        loginVO.setToken(token);
        loginVO.setExpiresIn(7200000L);
        loginVO.setUser(buildAdminUserVO(user));
        loginVO.setPermissions(Collections.emptyList());

        log.info("管理端登录成功: {}", user.getUsername());
        return Result.success("登录成功", loginVO);
    }

    /**
     * 获取当前用户信息
     */
    @GetMapping("/current-user")
    @Operation(summary = "获取当前用户", description = "获取当前登录用户信息")
    public Result<AdminUserVO> getCurrentUser(Principal principal) {
        if (principal == null) {
            throw new BusinessException(401, "未登录");
        }
        Long userId = jwtUtil.getUserIdFromToken(principal.getName());
        User user = userService.getById(userId);
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }
        return Result.success(buildAdminUserVO(user));
    }

    /**
     * 用户登出
     */
    @PostMapping("/logout")
    @Operation(summary = "用户登出", description = "退出当前登录")
    public Result<Void> logout() {
        return Result.success();
    }

    /**
     * 构建管理端用户视图
     */
    private AdminUserVO buildAdminUserVO(User user) {
        AdminUserVO vo = new AdminUserVO();
        vo.setUserId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setNickname(user.getNickname());
        vo.setAvatar(user.getAvatar());
        vo.setRoleId(user.getRoleId());
        vo.setCompanyId(user.getCompanyId());
        vo.setStatus(user.getStatus());
        return vo;
    }
}
