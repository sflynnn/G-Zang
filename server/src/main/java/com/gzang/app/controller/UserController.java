package com.gzang.app.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gzang.app.annotation.OperationLog;
import com.gzang.app.annotation.RequirePermission;
import com.gzang.app.constant.BusinessType;
import com.gzang.app.constant.ErrorCode;
import com.gzang.app.constant.PermissionCode;
import com.gzang.app.dto.user.AssignRoleDTO;
import com.gzang.app.entity.User;
import com.gzang.app.exception.BusinessException;
import com.gzang.app.service.UserService;
import com.gzang.app.util.JwtUtil;
import com.gzang.app.util.TenantContextHolder;
import com.gzang.app.vo.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

import static com.gzang.app.constant.ErrorCode.DATA_NOT_FOUND;
import static com.gzang.app.constant.ErrorCode.PERMISSION_DENIED;

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
     * <p>
     * 超级管理员（companyId=null）可查看全部用户；
     * 企业用户只能查看本公司用户。
     * </p>
     */
    @GetMapping
    @PreAuthorize("hasAuthority('USER_MANAGE')")
    @Operation(summary = "获取用户列表", description = "分页获取用户列表，支持公司级数据隔离")
    public Result<IPage<User>> getUserList(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer current,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer size) {

        Long companyId = TenantContextHolder.getCurrentCompanyId();
        Page<User> page = new Page<>(current, size);
        IPage<User> userPage = userService.getUsersByCompanyId(page, companyId);

        return Result.success(userPage);
    }

    /**
     * 获取用户信息
     * <p>
     * 拥有 USER_MANAGE 权限的管理员可查看任意用户；
     * 普通用户只能查看自己的信息。
     * </p>
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('USER_MANAGE')")
    @Operation(summary = "获取用户信息", description = "根据用户ID获取用户信息")
    public Result<User> getUserById(
            @Parameter(description = "用户ID") @PathVariable Long id,
            Principal principal) {

        Long currentUserId = jwtUtil.getUserIdFromToken(principal.getName());
        Long currentCompanyId = TenantContextHolder.getCurrentCompanyId();

        User user = userService.getById(id);
        if (user == null) {
            throw new BusinessException(DATA_NOT_FOUND, "用户不存在");
        }

        // 数据归属验证：本公司用户 或 本人
        boolean isOwnData = (currentCompanyId != null && currentCompanyId.equals(user.getCompanyId()))
                || currentUserId.equals(user.getId());
        boolean hasManagePermission = hasAuthority("USER_MANAGE");

        if (!hasManagePermission && !isOwnData) {
            throw new BusinessException(PERMISSION_DENIED, "无权查看该用户信息");
        }

        return Result.success(user);
    }

    /**
     * 更新用户信息
     * <p>
     * 拥有 USER_MANAGE 权限的管理员可更新任意用户；
     * 普通用户只能更新自己的信息（昵称、头像等，禁止改 roleId）。
     * </p>
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('USER_MANAGE')")
    @Operation(summary = "更新用户信息", description = "更新指定用户的信息")
    public Result<Void> updateUser(
            @Parameter(description = "用户ID") @PathVariable Long id,
            @Validated @RequestBody User user,
            Principal principal) {

        Long currentUserId = jwtUtil.getUserIdFromToken(principal.getName());
        Long currentCompanyId = TenantContextHolder.getCurrentCompanyId();

        User existingUser = userService.getById(id);
        if (existingUser == null) {
            throw new BusinessException(DATA_NOT_FOUND, "用户不存在");
        }

        // 数据归属验证
        boolean isOwnData = (currentCompanyId != null && currentCompanyId.equals(existingUser.getCompanyId()))
                || currentUserId.equals(existingUser.getId());
        boolean hasManagePermission = hasAuthority("USER_MANAGE");

        if (!hasManagePermission && !isOwnData) {
            throw new BusinessException(PERMISSION_DENIED, "无权更新该用户信息");
        }

        // 普通用户不能修改角色
        if (!hasManagePermission) {
            user.setRoleId(null);
            user.setCompanyId(null);
        }

        user.setId(id);
        user.setUsername(null);  // 禁止修改用户名
        user.setPassword(null);  // 禁止通过此接口修改密码

        boolean success = userService.updateById(user);
        if (!success) {
            throw new BusinessException(500, "更新用户信息失败");
        }

        return Result.success();
    }

    /**
     * 删除用户
     * <p>
     * 拥有 USER_MANAGE 权限的管理员可删除本公司用户。
     * 不能删除超级管理员（companyId=null）。
     * </p>
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('USER_MANAGE')")
    @Operation(summary = "删除用户", description = "删除指定用户（仅限本公司用户）")
    public Result<Void> deleteUser(
            @Parameter(description = "用户ID") @PathVariable Long id,
            Principal principal) {

        Long currentCompanyId = TenantContextHolder.getCurrentCompanyId();
        Long currentUserId = jwtUtil.getUserIdFromToken(principal.getName());

        // 禁止删除自己
        if (currentUserId.equals(id)) {
            throw new BusinessException(PERMISSION_DENIED, "不能删除当前登录用户");
        }

        User user = userService.getById(id);
        if (user == null) {
            throw new BusinessException(DATA_NOT_FOUND, "用户不存在");
        }

        // 超级管理员可删除所有用户；企业管理员只能删除本公司用户
        if (currentCompanyId != null) {
            if (!currentCompanyId.equals(user.getCompanyId())) {
                throw new BusinessException(PERMISSION_DENIED, "无权删除其他公司的用户");
            }
        }

        boolean success = userService.removeById(id);
        if (!success) {
            throw new BusinessException(500, "删除用户失败");
        }

        log.info("用户删除成功: id={}, operator={}", id, currentUserId);
        return Result.success();
    }

    /**
     * 获取当前用户信息
     */
    @GetMapping("/me")
    @Operation(summary = "获取当前用户信息", description = "获取当前登录用户的信息")
    public Result<User> getCurrentUser(Principal principal) {
        log.info("获取当前用户信息请求: user={}", principal.getName());

        Long userId = jwtUtil.getUserIdFromToken(principal.getName());
        User user = userService.getById(userId);

        if (user == null) {
            throw new BusinessException(DATA_NOT_FOUND, "用户不存在");
        }

        log.info("获取当前用户信息成功: userId={}", userId);
        return Result.success(user);
    }

    /**
     * 检查当前用户是否拥有指定权限
     */
    private boolean hasAuthority(String authority) {
        org.springframework.security.core.Authentication auth =
                org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) {
            return false;
        }
        return auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals(authority));
    }

    // ==================== 用户角色管理 ====================

    /**
     * 获取用户的角色
     */
    @GetMapping("/{id}/role")
    @RequirePermission(PermissionCode.USER_VIEW)
    @Operation(summary = "获取用户角色", description = "获取指定用户的角色ID")
    public Result<Long> getUserRole(@Parameter(description = "用户ID") @PathVariable Long id) {
        User user = userService.getById(id);
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND, "用户不存在");
        }
        return Result.success(user.getRoleId());
    }

    /**
     * 分配/变更用户角色
     */
    @PutMapping("/{id}/role")
    @RequirePermission(PermissionCode.USER_EDIT)
    @OperationLog(module = "USER", action = BusinessType.GRANT, describe = "分配角色给用户ID：#{#id}")
    @Operation(summary = "分配用户角色", description = "为用户分配或变更角色（仅管理员可操作）")
    public Result<Void> assignRole(
            @Parameter(description = "用户ID") @PathVariable Long id,
            @Valid @RequestBody AssignRoleDTO dto) {
        User user = userService.getById(id);
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND, "用户不存在");
        }
        // 更新用户表中的 role_id
        user.setRoleId(dto.getRoleId());
        userService.updateById(user);
        return Result.success();
    }
}
