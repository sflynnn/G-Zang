package com.gzang.app.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 权限实体类
 *
 * @author G-Zang Team
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_permission")
public class Permission extends BaseEntity {

    /**
     * 权限名称（如：用户管理、记账录入）
     */
    @TableField("permission_name")
    private String permissionName;

    /**
     * 权限编码
     */
    @TableField("permission_code")
    private String permissionCode;

    // Explicit getters to ensure Lombok generates them properly
    public String getPermissionName() {
        return permissionName;
    }

    public String getPermissionCode() {
        return permissionCode;
    }
}


