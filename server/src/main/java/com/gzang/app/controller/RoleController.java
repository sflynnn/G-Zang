package com.gzang.app.controller;

import com.gzang.app.annotation.OperationLog;
import com.gzang.app.annotation.RequirePermission;
import com.gzang.app.constant.BusinessType;
import com.gzang.app.constant.PermissionCode;
import com.gzang.app.dto.role.CreateRoleDTO;
import com.gzang.app.dto.role.UpdateRoleDTO;
import com.gzang.app.service.RoleService;
import com.gzang.app.util.TenantContextHolder;
import com.gzang.app.vo.Result;
import com.gzang.app.vo.RoleVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 角色管理控制器
 *
 * @author G-Zang Team
 */
@RestController
@RequestMapping("/api/v1/roles")
@Tag(name = "角色管理", description = "角色管理相关接口")
public class RoleController {

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    /**
     * 获取角色列表
     */
    @GetMapping
    @RequirePermission(PermissionCode.ROLE_VIEW)
    @Operation(summary = "获取角色列表", description = "查询所有角色列表")
    public Result<List<RoleVO>> getRoleList() {
        List<RoleVO> roles = roleService.listRoles();
        return Result.success(roles);
    }

    /**
     * 获取角色详情（含权限）
     */
    @GetMapping("/{id}")
    @RequirePermission(PermissionCode.ROLE_VIEW)
    @Operation(summary = "获取角色详情", description = "根据角色ID获取角色详情和关联权限")
    public Result<RoleVO> getRoleDetail(@Parameter(description = "角色ID") @PathVariable Long id) {
        RoleVO role = roleService.getRoleDetail(id);
        return Result.success(role);
    }

    /**
     * 创建自定义角色
     */
    @PostMapping
    @RequirePermission(PermissionCode.ROLE_ADD)
    @OperationLog(module = "ROLE", action = BusinessType.CREATE, describe = "创建角色：#{#dto.roleName}")
    @Operation(summary = "创建角色", description = "创建企业自定义角色")
    public Result<Void> createRole(@Validated @RequestBody CreateRoleDTO dto) {
        Long companyId = TenantContextHolder.getCurrentCompanyId();
        roleService.createRole(dto, companyId);
        return Result.success();
    }

    /**
     * 更新角色
     */
    @PutMapping("/{id}")
    @RequirePermission(PermissionCode.ROLE_EDIT)
    @OperationLog(module = "ROLE", action = BusinessType.UPDATE, describe = "更新角色ID：#{#id}")
    @Operation(summary = "更新角色", description = "更新角色信息及权限")
    public Result<Void> updateRole(
            @Parameter(description = "角色ID") @PathVariable Long id,
            @Validated @RequestBody UpdateRoleDTO dto) {
        roleService.updateRole(id, dto);
        return Result.success();
    }

    /**
     * 删除自定义角色
     */
    @DeleteMapping("/{id}")
    @RequirePermission(PermissionCode.ROLE_DELETE)
    @OperationLog(module = "ROLE", action = BusinessType.DELETE, describe = "删除角色ID：#{#id}")
    @Operation(summary = "删除角色", description = "删除企业自定义角色")
    public Result<Void> deleteRole(@Parameter(description = "角色ID") @PathVariable Long id) {
        roleService.deleteRole(id);
        return Result.success();
    }

    /**
     * 获取角色权限列表
     */
    @GetMapping("/{id}/permissions")
    @RequirePermission(PermissionCode.ROLE_VIEW)
    @Operation(summary = "获取角色权限列表", description = "根据角色ID获取关联的权限列表")
    public Result<List<Long>> getRolePermissions(@Parameter(description = "角色ID") @PathVariable Long id) {
        List<Long> permissionIds = roleService.getRolePermissionIds(id);
        return Result.success(permissionIds);
    }

    /**
     * 更新角色权限
     */
    @PutMapping("/{id}/permissions")
    @RequirePermission(PermissionCode.ROLE_EDIT)
    @OperationLog(module = "ROLE", action = BusinessType.PERMISSION_CHANGE, describe = "更新角色权限，角色ID：#{#id}")
    @Operation(summary = "更新角色权限", description = "更新角色的关联权限")
    public Result<Void> updateRolePermissions(
            @Parameter(description = "角色ID") @PathVariable Long id,
            @RequestBody List<Long> permissionIds) {
        roleService.updateRolePermissions(id, permissionIds);
        return Result.success();
    }
}
