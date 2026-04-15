package com.gzang.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gzang.app.dto.role.CreateRoleDTO;
import com.gzang.app.dto.role.UpdateRoleDTO;
import com.gzang.app.entity.Role;
import com.gzang.app.vo.RoleVO;

import java.util.List;

/**
 * 角色服务接口
 *
 * @author G-Zang Team
 */
public interface RoleService extends IService<Role> {

    /**
     * 分页查询角色列表
     */
    List<RoleVO> listRoles();

    /**
     * 获取角色详情（含权限）
     */
    RoleVO getRoleDetail(Long roleId);

    /**
     * 创建自定义角色
     */
    Role createRole(CreateRoleDTO dto, Long companyId);

    /**
     * 更新角色（含权限变更）
     */
    void updateRole(Long roleId, UpdateRoleDTO dto);

    /**
     * 删除自定义角色
     */
    void deleteRole(Long roleId);

    /**
     * 更新角色权限
     */
    void updateRolePermissions(Long roleId, List<Long> permissionIds);

    /**
     * 获取角色权限ID列表
     */
    List<Long> getRolePermissionIds(Long roleId);

    /**
     * 获取角色权限码列表
     */
    List<String> getRolePermissionCodes(Long roleId);

    /**
     * 根据ID查询角色（使用显式SQL，绕过MP字段检测问题）
     */
    Role findRoleById(Long id);
}
