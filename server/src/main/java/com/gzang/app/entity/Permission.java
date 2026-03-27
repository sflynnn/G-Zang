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

    /**
     * 权限分组：SYSTEM/ORGANIZATION/FINANCE/BUSINESS
     */
    @TableField("permission_group")
    private String permissionGroup;

    /**
     * 权限级别：1=系统级, 2=企业级, 3=个人级
     */
    @TableField("permission_level")
    private Integer permissionLevel = 2;

    /**
     * 排序号
     */
    @TableField("sort_order")
    private Integer sortOrder = 0;

    /**
     * 权限描述
     */
    @TableField("description")
    private String description;
}
