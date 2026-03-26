package com.gzang.app.controller;

import com.gzang.app.dto.LoginRequest;
import com.gzang.app.entity.User;
import com.gzang.app.service.UserService;
import com.gzang.app.util.JwtUtil;
import com.gzang.app.vo.LoginResponse;
import com.gzang.app.vo.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 认证控制器
 * 处理用户登录、注册等认证相关操作
 *
 * @author G-Zang Team
 */
@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "认证管理", description = "用户登录、注册等认证接口")
public class AuthController {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(AuthController.class);

    private final UserService userService;
    private final JwtUtil jwtUtil;

    public AuthController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    /**
     * 用户登录
     */
    @PostMapping("/login")
    @Operation(summary = "用户登录", description = "用户登录接口，返回JWT Token")
    public Result<LoginResponse> login(@Validated @RequestBody LoginRequest loginRequest) {
        log.info("用户登录请求: {}", loginRequest.getUsername());

        // 验证用户登录
        User user = userService.login(loginRequest.getUsername(), loginRequest.getPassword());
        if (user == null) {
            return Result.error(401, "用户名或密码错误", null);
        }

        // 生成JWT Token
        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRoleId(), user.getCompanyId());

        // 构建登录响应
        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setToken(token);
        loginResponse.setUserId(user.getId());
        loginResponse.setUsername(user.getUsername());
        loginResponse.setNickname(user.getNickname());
        loginResponse.setAvatar(user.getAvatar());
        loginResponse.setRoleId(user.getRoleId());
        loginResponse.setCompanyId(user.getCompanyId());

        log.info("用户登录成功: {}", user.getUsername());
        return Result.success("登录成功", loginResponse);
    }

    /**
     * 用户注册
     */
    @PostMapping("/register")
    @Operation(summary = "用户注册", description = "新用户注册接口")
    public Result<Void> register(@Validated @RequestBody User user) {
        log.info("用户注册请求: {}", user.getUsername());

        boolean success = userService.register(user);
        if (!success) {
            return Result.error(400, "用户名已存在", null);
        }

        log.info("用户注册成功: {}", user.getUsername());
        return Result.success();
    }
}
