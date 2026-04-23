package com.gzang.mobile.controller;

import com.gzang.app.util.JwtUtil;
import com.gzang.app.util.TenantContextHolder;
import com.gzang.app.dto.LoginRequest;
import com.gzang.app.dto.RegisterRequest;
import com.gzang.app.entity.User;
import com.gzang.app.exception.BusinessException;
import com.gzang.app.service.UserService;
import com.gzang.app.vo.Result;
import com.gzang.mobile.vo.MobileUserVO;
import com.gzang.mobile.vo.MobileLoginVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 移动端认证控制器
 * 移动端 + PC个人记账 共用的认证接口
 *
 * @author G-Zang Team
 */
@RestController
@RequestMapping("/api/mobile/auth")
@Tag(name = "移动端认证", description = "移动端/PC个人记账认证接口")
public class MobileAuthController {

    private static final Logger log = LoggerFactory.getLogger(MobileAuthController.class);

    private final UserService userService;
    private final JwtUtil jwtUtil;

    public MobileAuthController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    /**
     * 用户登录
     */
    @PostMapping("/login")
    @Operation(summary = "用户登录", description = "移动端/PC个人记账用户登录接口")
    public Result<MobileLoginVO> login(@Validated @RequestBody LoginRequest loginRequest) {
        log.info("移动端登录请求: {}", loginRequest.getUsername());

        User user = userService.login(loginRequest.getUsername(), loginRequest.getPassword());
        if (user == null) {
            throw new BusinessException(401, "用户名或密码错误");
        }

        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRoleId(), user.getCompanyId());

        MobileLoginVO loginVO = new MobileLoginVO();
        loginVO.setToken(token);
        loginVO.setExpiresIn(7200000L);
        loginVO.setUser(convertToMobileUserVO(user));

        log.info("移动端登录成功: {}", user.getUsername());
        return Result.success("登录成功", loginVO);
    }

    /**
     * 用户注册
     */
    @PostMapping("/register")
    @Operation(summary = "用户注册", description = "新用户注册接口")
    public Result<Void> register(@Validated @RequestBody RegisterRequest request) {
        log.info("移动端注册请求: {}", request.getUsername());
        userService.register(request);
        log.info("移动端注册成功: {}", request.getUsername());
        return Result.success();
    }

    /**
     * 获取当前用户信息
     */
    @GetMapping("/current-user")
    @Operation(summary = "获取当前用户", description = "获取当前登录用户信息")
    public Result<MobileUserVO> getCurrentUser() {
        Long userId = TenantContextHolder.getUserId();
        if (userId == null) {
            throw new BusinessException(401, "未登录");
        }
        User user = userService.getById(userId);
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }
        return Result.success(convertToMobileUserVO(user));
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
     * 刷新Token
     */
    @PostMapping("/refresh-token")
    @Operation(summary = "刷新Token", description = "刷新JWT Token")
    public Result<MobileLoginVO> refreshToken() {
        Long userId = TenantContextHolder.getUserId();
        if (userId == null) {
            throw new BusinessException(401, "未登录");
        }
        User user = userService.getById(userId);
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }

        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRoleId(), user.getCompanyId());

        MobileLoginVO loginVO = new MobileLoginVO();
        loginVO.setToken(token);
        loginVO.setExpiresIn(7200000L);
        loginVO.setUser(convertToMobileUserVO(user));

        return Result.success(loginVO);
    }

    /**
     * 转换用户信息
     */
    private MobileUserVO convertToMobileUserVO(User user) {
        MobileUserVO vo = new MobileUserVO();
        vo.setId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setNickname(user.getNickname());
        vo.setAvatar(user.getAvatar());
        vo.setCompanyId(user.getCompanyId());
        vo.setStatus(user.getStatus());
        return vo;
    }
}
