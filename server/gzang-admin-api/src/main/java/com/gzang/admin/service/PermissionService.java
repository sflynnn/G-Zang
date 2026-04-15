package com.gzang.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gzang.app.entity.Permission;
import com.gzang.app.vo.PermissionVO;

import java.util.List;
import java.util.Map;

/**
 * 权限服务接口
 *
 * @author G-Zang Team
 */
public interface PermissionService extends IService<Permission> {

    /**
     * 获取所有权限列表（分组）
     */
    List<PermissionVO> listAllPermissions();

    /**
     * 获取权限模块分组
     */
    Map<String, List<PermissionVO>> getPermissionGrouped();

    /**
     * 按模块分组（树形结构，用于角色权限配置）
     */
    Map<String, List<PermissionVO>> getPermissionByModule();

    /**
     * 根据角色ID获取权限列表
     */
    List<Permission> getPermissionsByRoleId(Long roleId);
}
