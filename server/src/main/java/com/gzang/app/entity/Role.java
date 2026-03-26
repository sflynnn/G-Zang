package com.gzang.app.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 角色实体类
 *
 * @author G-Zang Team
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_role")
public class Role extends BaseEntity {

    /**
     * 角色名称（如：管理员、财务、普通用户）
     */
    @TableField("role_name")
    private String roleName;

    /**
     * 角色编码
     */
    @TableField("role_code")
    private String roleCode;

    /**
     * 角色描述
     */
    @TableField("description")
    private String description;

    // Explicit getters to ensure Lombok generates them properly
    public String getRoleName() {
        return roleName;
    }

    public String getRoleCode() {
        return roleCode;
    }

    public String getDescription() {
        return description;
    }
}


