package com.gzang.app.config;

import com.gzang.app.entity.Permission;
import com.gzang.app.entity.Role;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 自定义 UserDetails，包含用户基本信息、角色信息和权限码列表
 *
 * @author G-Zang Team
 */
public class CustomUserDetails implements UserDetails {

    private final Long userId;
    private final String username;
    private final String password;
    private final Long companyId;
    private final Integer status;
    private final Role role;
    private final List<Permission> permissions;
    private final Collection<? extends GrantedAuthority> authorities;

    public CustomUserDetails(Long userId, String username, String password,
                             Long companyId, Integer status,
                             Role role, List<Permission> permissions) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.companyId = companyId;
        this.status = status;
        this.role = role;
        this.permissions = permissions;
        this.authorities = permissions.stream()
                .map(p -> new SimpleGrantedAuthority(p.getPermissionCode()))
                .collect(Collectors.toList());
    }

    public Long getUserId() {
        return userId;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public Role getRole() {
        return role;
    }

    public List<Permission> getPermissions() {
        return permissions;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return status != null && status == 1;
    }

    /**
     * 检查是否拥有指定权限码
     */
    public boolean hasPermission(String permissionCode) {
        return authorities.stream()
                .anyMatch(a -> a.getAuthority().equals(permissionCode));
    }

    /**
     * 检查是否拥有指定角色编码
     */
    public boolean hasRole(String roleCode) {
        return role != null && role.getRoleCode().equals(roleCode);
    }

    /**
     * 获取数据范围
     */
    public String getDataScope() {
        return role != null ? role.getDataScope() : "OWN";
    }

    /**
     * 获取角色ID
     */
    public Long getRoleId() {
        return role != null ? role.getId() : null;
    }
}
