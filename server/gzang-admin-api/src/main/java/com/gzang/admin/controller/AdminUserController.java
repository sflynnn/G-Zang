package com.gzang.admin.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gzang.app.util.JwtUtil;
import com.gzang.app.dto.user.AssignRoleDTO;
import com.gzang.app.entity.User;
import com.gzang.app.exception.BusinessException;
import com.gzang.admin.service.UserService;
import com.gzang.app.util.TenantContextHolder;
import com.gzang.app.vo.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

import static com.gzang.app.constant.ErrorCode.DATA_NOT_FOUND;
import static com.gzang.app.constant.ErrorCode.PERMISSION_DENIED;

/**
 * 管理端用户管理控制器
 *
 * @author G-Zang Team
 */
@RestController
@RequestMapping("/api/admin/users")
@Tag(name = "管理端用户管理", description = "用户管理相关接口")
public class AdminUserController {

    private static final Logger log = LoggerFactory.getLogger(AdminUserController.class);

    private final UserService userService;
    private final JwtUtil jwtUtil;

    public AdminUserController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    /**
     * 获取用户列表
     */
    @GetMapping
    @PreAuthorize("hasAuthority('USER_MANAGE')")
    @Operation(summary = "获取用户列表", description = "分页获取用户列表")
    public Result<IPage<User>> getUserList(
            @Parameter(description = "页码") @RequestParam(name = "current", defaultValue = "1") Integer current,
            @Parameter(description = "每页大小") @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("=== AdminUserController.getUserList called ===");
        log.info("current={}, size={}", current, size);
        Long companyId = TenantContextHolder.getCurrentCompanyId();
        log.info("TenantContextHolder companyId={}", companyId);
        try {
            Page<User> page = new Page<>(current, size);
            IPage<User> userPage = userService.getUsersByCompanyId(page, companyId);
            log.info("getUsersByCompanyId returned, records={}", userPage.getRecords().size());
            return Result.success(userPage);
        } catch (Exception e) {
            log.error("Error in getUserList", e);
            throw e;
        }
    }

    /**
     * 获取用户详情
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('USER_MANAGE')")
    @Operation(summary = "获取用户详情", description = "根据ID获取用户详情")
    public Result<User> getUserById(
            @Parameter(description = "用户ID") @PathVariable Long id) {
        User user = userService.getById(id);
        if (user == null) {
            throw new BusinessException(DATA_NOT_FOUND, "用户不存在");
        }
        return Result.success(user);
    }

    /**
     * 更新用户
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('USER_MANAGE')")
    @Operation(summary = "更新用户", description = "更新指定用户信息")
    public Result<Void> updateUser(
            @Parameter(description = "用户ID") @PathVariable Long id,
            @RequestBody User user,
            Principal principal) {
        Long currentUserId = jwtUtil.getUserIdFromToken(principal.getName());
        Long currentCompanyId = TenantContextHolder.getCurrentCompanyId();

        User existingUser = userService.getById(id);
        if (existingUser == null) {
            throw new BusinessException(DATA_NOT_FOUND, "用户不存在");
        }

        user.setId(id);
        user.setUsername(null);
        user.setPassword(null);

        boolean success = userService.updateById(user);
        if (!success) {
            throw new BusinessException(500, "更新用户信息失败");
        }
        return Result.success();
    }

    /**
     * 删除用户
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('USER_MANAGE')")
    @Operation(summary = "删除用户", description = "删除指定用户")
    public Result<Void> deleteUser(
            @Parameter(description = "用户ID") @PathVariable Long id,
            Principal principal) {
        Long currentUserId = jwtUtil.getUserIdFromToken(principal.getName());

        if (currentUserId.equals(id)) {
            throw new BusinessException(PERMISSION_DENIED, "不能删除当前登录用户");
        }

        User user = userService.getById(id);
        if (user == null) {
            throw new BusinessException(DATA_NOT_FOUND, "用户不存在");
        }

        boolean success = userService.removeById(id);
        if (!success) {
            throw new BusinessException(500, "删除用户失败");
        }
        return Result.success();
    }

    /**
     * 分配用户角色
     */
    @PutMapping("/{id}/role")
    @PreAuthorize("hasAuthority('USER_MANAGE')")
    @Operation(summary = "分配用户角色", description = "为用户分配角色")
    public Result<Void> assignRole(
            @Parameter(description = "用户ID") @PathVariable Long id,
            @Valid @RequestBody AssignRoleDTO dto) {
        User user = userService.getById(id);
        if (user == null) {
            throw new BusinessException(DATA_NOT_FOUND, "用户不存在");
        }
        user.setRoleId(dto.getRoleId());
        userService.updateById(user);
        return Result.success();
    }
}
