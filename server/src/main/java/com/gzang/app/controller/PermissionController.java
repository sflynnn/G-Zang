package com.gzang.app.controller;

import com.gzang.app.annotation.RequirePermission;
import com.gzang.app.constant.PermissionCode;
import com.gzang.app.service.PermissionService;
import com.gzang.app.vo.PermissionVO;
import com.gzang.app.vo.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 权限管理控制器
 *
 * @author G-Zang Team
 */
@RestController
@RequestMapping("/api/v1/permissions")
@Tag(name = "权限管理", description = "权限查询相关接口")
public class PermissionController {

    private final PermissionService permissionService;

    public PermissionController(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    /**
     * 获取所有权限列表（分组）
     */
    @GetMapping
    @RequirePermission(PermissionCode.ROLE_VIEW)
    @Operation(summary = "获取所有权限列表", description = "查询所有权限，按分组排序")
    public Result<Map<String, List<PermissionVO>>> getAllPermissions() {
        Map<String, List<PermissionVO>> grouped = permissionService.getPermissionGrouped();
        return Result.success(grouped);
    }

    /**
     * 获取权限模块分组
     */
    @GetMapping("/modules")
    @RequirePermission(PermissionCode.ROLE_VIEW)
    @Operation(summary = "获取权限模块分组", description = "获取权限按模块分组的结构")
    public Result<Map<String, List<PermissionVO>>> getPermissionModules() {
        Map<String, List<PermissionVO>> grouped = permissionService.getPermissionGrouped();
        return Result.success(grouped);
    }
}
