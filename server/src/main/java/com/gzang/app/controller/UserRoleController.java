package com.gzang.app.controller;

import com.gzang.app.annotation.OperationLog;
import com.gzang.app.annotation.RequirePermission;
import com.gzang.app.constant.BusinessType;
import com.gzang.app.constant.ErrorCode;
import com.gzang.app.constant.PermissionCode;
import com.gzang.app.dto.user.AssignRoleDTO;
import com.gzang.app.entity.User;
import com.gzang.app.entity.UserRole;
import com.gzang.app.exception.BusinessException;
import com.gzang.app.mapper.UserMapper;
import com.gzang.app.mapper.UserRoleMapper;
import com.gzang.app.service.UserService;
import com.gzang.app.vo.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

/**
 * 用户角色管理控制器
 *
 * @author G-Zang Team
 */
@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "用户角色管理", description = "用户角色分配相关接口")
public class UserRoleController {

    private final UserService userService;
    private final UserRoleMapper userRoleMapper;
    private final UserMapper userMapper;

    public UserRoleController(UserService userService, UserRoleMapper userRoleMapper, UserMapper userMapper) {
        this.userService = userService;
        this.userRoleMapper = userRoleMapper;
        this.userMapper = userMapper;
    }

    /**
     * 获取用户的角色
     */
    @GetMapping("/{id}/role")
    @RequirePermission(PermissionCode.USER_VIEW)
    @Operation(summary = "获取用户角色", description = "获取指定用户的角色信息")
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
    @Operation(summary = "分配用户角色", description = "为用户分配或变更角色")
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
