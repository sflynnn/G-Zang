package com.gzang.admin.controller;

import com.gzang.app.entity.User;
import com.gzang.app.exception.BusinessException;
import com.gzang.admin.service.RoleService;
import com.gzang.admin.service.UserService;
import com.gzang.app.util.JwtUtil;
import com.gzang.app.dto.role.CreateRoleDTO;
import com.gzang.app.dto.role.UpdateRoleDTO;
import com.gzang.app.vo.Result;
import com.gzang.app.vo.RoleVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

import static com.gzang.app.constant.ErrorCode.DATA_NOT_FOUND;

/**
 * 管理端角色管理控制器
 *
 * @author G-Zang Team
 */
@RestController
@RequestMapping("/api/admin/roles")
@Tag(name = "管理端角色管理", description = "角色管理相关接口")
public class AdminRoleController {

    private final RoleService roleService;
    private final UserService userService;
    private final JwtUtil jwtUtil;

    public AdminRoleController(RoleService roleService, UserService userService, JwtUtil jwtUtil) {
        this.roleService = roleService;
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    /**
     * 获取角色列表
     */
    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_MANAGE')")
    @Operation(summary = "获取角色列表", description = "获取所有角色列表")
    public Result<List<RoleVO>> getRoleList() {
        List<RoleVO> roles = roleService.listRoles();
        return Result.success(roles);
    }

    /**
     * 获取角色详情
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_MANAGE')")
    @Operation(summary = "获取角色详情", description = "获取角色详情")
    public Result<RoleVO> getRoleById(
            @Parameter(description = "角色ID") @PathVariable Long id) {
        RoleVO roleVO = roleService.getRoleDetail(id);
        if (roleVO == null) {
            throw new BusinessException(DATA_NOT_FOUND, "角色不存在");
        }
        return Result.success(roleVO);
    }

    /**
     * 创建角色
     */
    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_MANAGE')")
    @Operation(summary = "创建角色", description = "创建新角色")
    public Result<Void> createRole(
            @Valid @RequestBody CreateRoleDTO dto,
            Principal principal) {
        Long userId = jwtUtil.getUserIdFromToken(principal.getName());
        User user = userService.getById(userId);
        Long companyId = user != null ? user.getCompanyId() : null;
        roleService.createRole(dto, companyId);
        return Result.success();
    }

    /**
     * 更新角色
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_MANAGE')")
    @Operation(summary = "更新角色", description = "更新角色信息")
    public Result<Void> updateRole(
            @Parameter(description = "角色ID") @PathVariable Long id,
            @Valid @RequestBody UpdateRoleDTO dto) {
        roleService.updateRole(id, dto);
        return Result.success();
    }

    /**
     * 删除角色
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_MANAGE')")
    @Operation(summary = "删除角色", description = "删除角色")
    public Result<Void> deleteRole(
            @Parameter(description = "角色ID") @PathVariable Long id) {
        roleService.deleteRole(id);
        return Result.success();
    }

    /**
     * 获取角色权限
     */
    @GetMapping("/{id}/permissions")
    @PreAuthorize("hasAuthority('ROLE_MANAGE')")
    @Operation(summary = "获取角色权限", description = "获取角色的权限列表")
    public Result<List<String>> getRolePermissions(
            @Parameter(description = "角色ID") @PathVariable Long id) {
        List<String> permissions = roleService.getRolePermissionCodes(id);
        return Result.success(permissions);
    }

    /**
     * 分配角色权限
     */
    @PutMapping("/{id}/permissions")
    @PreAuthorize("hasAuthority('ROLE_MANAGE')")
    @Operation(summary = "分配角色权限", description = "为角色分配权限")
    public Result<Void> assignPermissions(
            @Parameter(description = "角色ID") @PathVariable Long id,
            @RequestBody List<Long> permissionIds) {
        roleService.updateRolePermissions(id, permissionIds);
        return Result.success();
    }
}
