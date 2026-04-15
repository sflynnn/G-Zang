package com.gzang.app.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户实体类
 *
 * @author G-Zang Team
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_user")
public class User extends BaseEntity {

    /**
     * 用户名（手机号/邮箱）
     */
    @TableField("username")
    private String username;

    /**
     * 密码（加密存储）
     */
    @JsonIgnore
    @TableField("password")
    private String password;

    /**
     * 用户昵称
     */
    @TableField("nickname")
    private String nickname;

    /**
     * 用户头像URL
     */
    @TableField("avatar")
    private String avatar;

    /**
     * 角色ID
     */
    @TableField("role_id")
    private Long roleId;

    /**
     * 所属公司ID（个人用户为NULL）
     */
    @TableField("company_id")
    private Long companyId;

    /**
     * 状态 (0:禁用, 1:正常)
     */
    @TableField("status")
    private Integer status;
}
