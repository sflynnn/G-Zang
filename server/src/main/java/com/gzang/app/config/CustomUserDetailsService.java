package com.gzang.app.config;

import com.gzang.app.entity.Permission;
import com.gzang.app.entity.Role;
import com.gzang.app.entity.User;
import com.gzang.app.exception.BusinessException;
import com.gzang.app.mapper.PermissionMapper;
import com.gzang.app.mapper.RoleMapper;
import com.gzang.app.mapper.UserMapper;
import com.gzang.app.util.TenantContextHolder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 自定义 UserDetailsService
 * 实现从数据库加载用户、角色和权限信息
 *
 * @author G-Zang Team
 */
@Component
public class CustomUserDetailsService implements UserDetailsService {

    private final UserMapper userMapper;
    private final RoleMapper roleMapper;
    private final PermissionMapper permissionMapper;

    public CustomUserDetailsService(UserMapper userMapper,
                                     RoleMapper roleMapper,
                                     PermissionMapper permissionMapper) {
        this.userMapper = userMapper;
        this.roleMapper = roleMapper;
        this.permissionMapper = permissionMapper;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userMapper.selectByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("用户不存在: " + username);
        }

        Role role = null;
        if (user.getRoleId() != null) {
            role = roleMapper.selectRoleById(user.getRoleId());  // 使用显式SQL避免BaseEntity字段缺失问题
        }

        List<Permission> permissions = Collections.emptyList();
        if (user.getRoleId() != null) {
            permissions = permissionMapper.selectByRoleId(user.getRoleId());
        }

        return new CustomUserDetails(
                user.getId(),
                user.getUsername(),
                user.getPassword(),
                user.getCompanyId(),
                user.getStatus(),
                role,
                permissions
        );
    }

    /**
     * 根据用户ID加载用户详情（用于JWT刷新等场景）
     */
    public UserDetails loadUserById(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new UsernameNotFoundException("用户不存在，ID: " + userId);
        }

        Role role = null;
        if (user.getRoleId() != null) {
            role = roleMapper.selectRoleById(user.getRoleId());  // 使用显式SQL避免BaseEntity字段缺失问题
        }

        List<Permission> permissions = Collections.emptyList();
        if (user.getRoleId() != null) {
            permissions = permissionMapper.selectByRoleId(user.getRoleId());
        }

        return new CustomUserDetails(
                user.getId(),
                user.getUsername(),
                user.getPassword(),
                user.getCompanyId(),
                user.getStatus(),
                role,
                permissions
        );
    }

    /**
     * 根据用户ID获取权限码列表
     */
    public List<String> getPermissionCodesByUserId(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null || user.getRoleId() == null) {
            return Collections.emptyList();
        }

        List<Permission> permissions = permissionMapper.selectByRoleId(user.getRoleId());
        return permissions.stream()
                .map(Permission::getPermissionCode)
                .collect(Collectors.toList());
    }
}
