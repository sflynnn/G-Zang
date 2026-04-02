package com.gzang.app.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gzang.app.entity.Permission;
import com.gzang.app.mapper.PermissionMapper;
import com.gzang.app.service.PermissionService;
import com.gzang.app.vo.PermissionVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 权限服务实现类
 *
 * @author G-Zang Team
 */
@Service
public class PermissionServiceImpl extends ServiceImpl<PermissionMapper, Permission> implements PermissionService {

    @Override
    public List<PermissionVO> listAllPermissions() {
        LambdaQueryWrapper<Permission> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(Permission::getSortOrder);
        List<Permission> permissions = list(wrapper);
        return permissions.stream().map(this::convertToVO).collect(Collectors.toList());
    }

    @Override
    public Map<String, List<PermissionVO>> getPermissionGrouped() {
        System.out.println(">>> getPermissionGrouped called");
        LambdaQueryWrapper<Permission> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(Permission::getSortOrder);
        List<Permission> permissions = list(wrapper);
        System.out.println(">>> permissions count = " + (permissions != null ? permissions.size() : "null"));

        Map<String, List<PermissionVO>> grouped = new LinkedHashMap<>();
        for (Permission permission : permissions) {
            String group = permission.getPermissionGroup() != null ? permission.getPermissionGroup() : "OTHER";
            grouped.computeIfAbsent(group, k -> new ArrayList<>()).add(convertToVO(permission));
        }
        return grouped;
    }

    @Override
    public Map<String, List<PermissionVO>> getPermissionByModule() {
        LambdaQueryWrapper<Permission> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(Permission::getSortOrder);
        List<Permission> permissions = list(wrapper);

        Map<String, List<PermissionVO>> byModule = new LinkedHashMap<>();
        for (Permission permission : permissions) {
            String module = permission.getPermissionModule() != null
                    ? permission.getPermissionModule()
                    : permission.getPermissionGroup() != null
                            ? permission.getPermissionGroup()
                            : "OTHER";
            byModule.computeIfAbsent(module, k -> new ArrayList<>()).add(convertToVO(permission));
        }
        return byModule;
    }

    @Override
    public List<Permission> getPermissionsByRoleId(Long roleId) {
        return baseMapper.selectByRoleId(roleId);
    }

    private PermissionVO convertToVO(Permission permission) {
        PermissionVO vo = new PermissionVO();
        BeanUtils.copyProperties(permission, vo);
        return vo;
    }
}
