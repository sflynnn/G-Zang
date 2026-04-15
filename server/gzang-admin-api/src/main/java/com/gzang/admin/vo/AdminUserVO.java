package com.gzang.admin.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 管理端用户视图对象
 *
 * @author G-Zang Team
 */
@Data
@Schema(description = "管理端用户信息")
public class AdminUserVO {

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "昵称")
    private String nickname;

    @Schema(description = "头像URL")
    private String avatar;

    @Schema(description = "角色ID")
    private Long roleId;

    @Schema(description = "角色编码")
    private String roleCode;

    @Schema(description = "角色名称")
    private String roleName;

    @Schema(description = "公司ID")
    private Long companyId;

    @Schema(description = "用户状态")
    private Integer status;

    @Schema(description = "权限码列表")
    private List<String> permissions;

    @Schema(description = "JWT Token")
    private String token;
}
