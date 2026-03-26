package com.gzang.app.service.impl;

import com.gzang.app.entity.Permission;
import com.gzang.app.entity.Role;
import com.gzang.app.entity.User;
import com.gzang.app.mapper.PermissionMapper;
import com.gzang.app.mapper.RoleMapper;
import com.gzang.app.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 用户详情服务实现类
 * 实现Spring Security的UserDetailsService接口
 *
 * @author G-Zang Team
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private static final Logger log = LoggerFactory.getLogger(UserDetailsServiceImpl.class);
    private final UserService userService;
    private final RoleMapper roleMapper;
    private final PermissionMapper permissionMapper;

    public UserDetailsServiceImpl(UserService userService, RoleMapper roleMapper, PermissionMapper permissionMapper) {
        this.userService = userService;
        this.roleMapper = roleMapper;
        this.permissionMapper = permissionMapper;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userService.getUserByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("用户不存在: " + username);
        }

        // 获取用户角色
        Role role = roleMapper.selectById(user.getRoleId());
        if (role == null) {
            throw new UsernameNotFoundException("用户角色不存在: " + username);
        }

        // 获取用户权限
        List<Permission> permissions = permissionMapper.selectByRoleId(user.getRoleId());

        // 构建权限列表
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getRoleCode()));

        for (Permission permission : permissions) {
            authorities.add(new SimpleGrantedAuthority(permission.getPermissionCode()));
        }

        log.debug("加载用户详情: {}, 角色: {}, 权限数量: {}", username, role.getRoleName(), authorities.size());

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .authorities(authorities)
                .accountExpired(false)
                .accountLocked(user.getStatus() != 1)
                .credentialsExpired(false)
                .disabled(user.getStatus() != 1)
                .build();
    }
}
