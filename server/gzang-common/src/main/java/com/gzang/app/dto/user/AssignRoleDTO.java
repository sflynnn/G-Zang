package com.gzang.app.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

/**
 * 分配角色请求DTO
 *
 * @author G-Zang Team
 */
@Schema(description = "分配角色请求")
public class AssignRoleDTO {

    @Schema(description = "角色ID")
    @NotNull(message = "角色ID不能为空")
    private Long roleId;

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }
}
