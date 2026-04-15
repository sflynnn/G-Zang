package com.gzang.app.dto.role;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

/**
 * 创建角色请求DTO
 *
 * @author G-Zang Team
 */
@Schema(description = "创建角色请求")
public class CreateRoleDTO {

    @Schema(description = "角色名称")
    @NotBlank(message = "角色名称不能为空")
    @Size(max = 64, message = "角色名称长度不能超过64")
    private String roleName;

    @Schema(description = "角色编码")
    @NotBlank(message = "角色编码不能为空")
    @Size(max = 64, message = "角色编码长度不能超过64")
    private String roleCode;

    @Schema(description = "角色描述")
    @Size(max = 255, message = "角色描述长度不能超过255")
    private String description;

    @Schema(description = "数据范围：OWN/DEPARTMENT/COMPANY/ALL")
    @NotBlank(message = "数据范围不能为空")
    private String dataScope;

    @Schema(description = "权限ID列表")
    private List<Long> permissionIds;

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getRoleCode() {
        return roleCode;
    }

    public void setRoleCode(String roleCode) {
        this.roleCode = roleCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDataScope() {
        return dataScope;
    }

    public void setDataScope(String dataScope) {
        this.dataScope = dataScope;
    }

    public List<Long> getPermissionIds() {
        return permissionIds;
    }

    public void setPermissionIds(List<Long> permissionIds) {
        this.permissionIds = permissionIds;
    }
}
