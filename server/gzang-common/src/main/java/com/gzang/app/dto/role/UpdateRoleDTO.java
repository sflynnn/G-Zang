package com.gzang.app.dto.role;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

import java.util.List;

/**
 * 更新角色请求DTO
 *
 * @author G-Zang Team
 */
@Schema(description = "更新角色请求")
public class UpdateRoleDTO {

    @Schema(description = "角色名称")
    @Size(max = 64, message = "角色名称长度不能超过64")
    private String roleName;

    @Schema(description = "角色描述")
    @Size(max = 255, message = "角色描述长度不能超过255")
    private String description;

    @Schema(description = "数据范围：OWN/DEPARTMENT/COMPANY/ALL")
    private String dataScope;

    @Schema(description = "权限ID列表")
    private List<Long> permissionIds;

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
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
