package com.gzang.admin.controller;

import com.gzang.admin.service.PermissionService;
import com.gzang.app.vo.PermissionVO;
import com.gzang.app.vo.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 管理端权限管理控制器
 *
 * @author G-Zang Team
 */
@RestController
@RequestMapping("/api/admin/permissions")
@Tag(name = "管理端权限管理", description = "权限管理相关接口")
public class AdminPermissionController {

    private final PermissionService permissionService;

    public AdminPermissionController(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    /**
     * 获取所有权限（分组）
     */
    @GetMapping
    @PreAuthorize("hasAuthority('PERMISSION_MANAGE')")
    @Operation(summary = "获取权限列表", description = "获取所有权限（按分组）")
    public Result<List<PermissionVO>> getPermissionList() {
        List<PermissionVO> permissions = permissionService.listAllPermissions();
        return Result.success(permissions);
    }

    /**
     * 获取权限模块分组
     */
    @GetMapping("/modules")
    @PreAuthorize("hasAuthority('PERMISSION_MANAGE')")
    @Operation(summary = "获取权限模块", description = "获取权限按模块分组的树形结构")
    public Result<Map<String, List<PermissionVO>>> getPermissionModules() {
        Map<String, List<PermissionVO>> modules = permissionService.getPermissionGrouped();
        return Result.success(modules);
    }
}
