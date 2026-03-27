package com.gzang.app.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gzang.app.constant.ErrorCode;
import com.gzang.app.dto.role.CreateRoleDTO;
import com.gzang.app.dto.role.UpdateRoleDTO;
import com.gzang.app.entity.Permission;
import com.gzang.app.entity.Role;
import com.gzang.app.exception.BusinessException;
import com.gzang.app.mapper.PermissionMapper;
import com.gzang.app.mapper.RoleMapper;
import com.gzang.app.mapper.RolePermissionMapper;
import com.gzang.app.service.RoleService;
import com.gzang.app.vo.RoleVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 角色服务实现类
 *
 * @author G-Zang Team
 */
@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {

    private final RolePermissionMapper rolePermissionMapper;
    private final PermissionMapper permissionMapper;

    public RoleServiceImpl(RolePermissionMapper rolePermissionMapper, PermissionMapper permissionMapper) {
        this.rolePermissionMapper = rolePermissionMapper;
        this.permissionMapper = permissionMapper;
    }

    @Override
    public List<RoleVO> listRoles() {
        LambdaQueryWrapper<Role> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(Role::getRoleType).orderByAsc(Role::getCreateTime);
        List<Role> roles = list(wrapper);
        return roles.stream().map(this::convertToVO).collect(Collectors.toList());
    }

    @Override
    public RoleVO getRoleDetail(Long roleId) {
        Role role = getById(roleId);
        if (role == null) {
            throw new BusinessException(ErrorCode.ROLE_NOT_FOUND, "角色不存在");
        }
        RoleVO vo = convertToVO(role);
        vo.setPermissionIds(getRolePermissionIds(roleId));
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Role createRole(CreateRoleDTO dto, Long companyId) {
        // 检查角色编码是否已存在
        LambdaQueryWrapper<Role> codeWrapper = new LambdaQueryWrapper<>();
        codeWrapper.eq(Role::getRoleCode, dto.getRoleCode());
        if (count(codeWrapper) > 0) {
            throw new BusinessException(ErrorCode.ROLE_CODE_EXISTS, "角色编码已存在");
        }

        Role role = new Role();
        BeanUtils.copyProperties(dto, role);
        role.setRoleType(2); // 企业自定义角色
        role.setCompanyId(companyId);
        role.setIsDefault(0);
        save(role);

        // 分配权限
        if (dto.getPermissionIds() != null && !dto.getPermissionIds().isEmpty()) {
            rolePermissionMapper.deleteByRoleId(role.getId());
            rolePermissionMapper.batchInsert(role.getId(), dto.getPermissionIds().toArray(new Long[0]));
        }

        return role;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateRole(Long roleId, UpdateRoleDTO dto) {
        Role role = getById(roleId);
        if (role == null) {
            throw new BusinessException(ErrorCode.ROLE_NOT_FOUND, "角色不存在");
        }

        // 系统内置角色不允许修改
        if (role.getRoleType() != null && role.getRoleType() == 1) {
            throw new BusinessException(ErrorCode.CANNOT_MODIFY_SYSTEM_ROLE, "系统内置角色不允许修改");
        }

        if (dto.getRoleName() != null) {
            role.setRoleName(dto.getRoleName());
        }
        if (dto.getDescription() != null) {
            role.setDescription(dto.getDescription());
        }
        if (dto.getDataScope() != null) {
            role.setDataScope(dto.getDataScope());
        }
        updateById(role);

        // 更新权限
        if (dto.getPermissionIds() != null) {
            rolePermissionMapper.deleteByRoleId(roleId);
            if (!dto.getPermissionIds().isEmpty()) {
                rolePermissionMapper.batchInsert(roleId, dto.getPermissionIds().toArray(new Long[0]));
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteRole(Long roleId) {
        Role role = getById(roleId);
        if (role == null) {
            throw new BusinessException(ErrorCode.ROLE_NOT_FOUND, "角色不存在");
        }

        // 系统内置角色不允许删除
        if (role.getRoleType() != null && role.getRoleType() == 1) {
            throw new BusinessException(ErrorCode.CANNOT_DELETE_SYSTEM_ROLE, "系统内置角色不允许删除");
        }

        // 删除角色权限关联
        rolePermissionMapper.deleteByRoleId(roleId);
        // 删除角色
        removeById(roleId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateRolePermissions(Long roleId, List<Long> permissionIds) {
        Role role = getById(roleId);
        if (role == null) {
            throw new BusinessException(ErrorCode.ROLE_NOT_FOUND, "角色不存在");
        }

        rolePermissionMapper.deleteByRoleId(roleId);
        if (permissionIds != null && !permissionIds.isEmpty()) {
            rolePermissionMapper.batchInsert(roleId, permissionIds.toArray(new Long[0]));
        }
    }

    @Override
    public List<Long> getRolePermissionIds(Long roleId) {
        LambdaQueryWrapper<com.gzang.app.entity.RolePermission> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(com.gzang.app.entity.RolePermission::getRoleId, roleId);
        wrapper.select(com.gzang.app.entity.RolePermission::getPermissionId);
        List<com.gzang.app.entity.RolePermission> rolePermissions = rolePermissionMapper.selectList(wrapper);
        return rolePermissions.stream()
                .map(com.gzang.app.entity.RolePermission::getPermissionId)
                .collect(Collectors.toList());
    }

    private RoleVO convertToVO(Role role) {
        RoleVO vo = new RoleVO();
        BeanUtils.copyProperties(role, vo);
        return vo;
    }
}
