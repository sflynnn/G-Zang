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

    /**
     * 角色类型：1=系统内置, 2=企业自定义
     */
    @TableField("role_type")
    private Integer roleType = 1;

    /**
     * 数据范围：OWN/DEPARTMENT/COMPANY/ALL
     */
    @TableField("data_scope")
    private String dataScope = "OWN";

    /**
     * 自定义角色所属公司（内置角色为NULL）
     */
    @TableField("company_id")
    private Long companyId;

    /**
     * 是否为某角色的默认角色（预留）
     */
    @TableField("is_default")
    private Integer isDefault = 0;
}
