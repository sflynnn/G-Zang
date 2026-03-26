package com.gzang.app.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gzang.app.entity.User;
import com.gzang.app.service.UserService;
import com.gzang.app.util.JwtUtil;
import com.gzang.app.vo.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

/**
 * 用户管理控制器
 * 提供用户管理的相关接口
 *
 * @author G-Zang Team
 */
@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "用户管理", description = "用户管理相关接口")
public class UserController {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(UserController.class);

    private final UserService userService;
    private final JwtUtil jwtUtil;

    public UserController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    /**
     * 获取用户列表（分页）
     */
    @GetMapping
    @PreAuthorize("hasAuthority('USER_MANAGE')")
    @Operation(summary = "获取用户列表", description = "分页获取用户列表")
    public Result<IPage<User>> getUserList(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer current,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer size) {

        Page<User> page = new Page<>(current, size);
        IPage<User> userPage = userService.page(page);

        return Result.success(userPage);
    }

    /**
     * 获取用户信息
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('USER_MANAGE') or #id == authentication.principal.id")
    @Operation(summary = "获取用户信息", description = "根据用户ID获取用户信息")
    public Result<User> getUserById(@Parameter(description = "用户ID") @PathVariable Long id) {
        User user = userService.getById(id);
        if (user == null) {
            return Result.error(404, "用户不存在", null);
        }
        return Result.success(user);
    }

    /**
     * 更新用户信息
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('USER_MANAGE') or #id == authentication.principal.id")
    @Operation(summary = "更新用户信息", description = "更新指定用户的信息")
    public Result<Void> updateUser(
            @Parameter(description = "用户ID") @PathVariable Long id,
            @Validated @RequestBody User user) {

        user.setId(id);
        boolean success = userService.updateById(user);
        if (!success) {
            return Result.error(500, "更新失败", null);
        }

        return Result.success();
    }

    /**
     * 删除用户
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('USER_MANAGE')")
    @Operation(summary = "删除用户", description = "删除指定用户")
    public Result<Void> deleteUser(@Parameter(description = "用户ID") @PathVariable Long id) {
        boolean success = userService.removeById(id);
        if (!success) {
            return Result.error(500, "删除失败", null);
        }

        return Result.success();
    }

    /**
     * 获取当前用户信息
     */
    @GetMapping("/me")
    @Operation(summary = "获取当前用户信息", description = "获取当前登录用户的信息")
    public Result<User> getCurrentUser(Principal principal) {
        log.info("获取当前用户信息请求: user={}", principal.getName());

        // 从JWT中获取用户ID
        Long userId = jwtUtil.getUserIdFromToken(principal.getName());
        User user = userService.getById(userId);

        if (user == null) {
            return Result.error(404, "用户不存在", null);
        }

        log.info("获取当前用户信息成功: userId={}", userId);
        return Result.success(user);
    }
}
