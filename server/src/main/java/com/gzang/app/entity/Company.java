package com.gzang.app.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 公司/组织实体类
 *
 * @author G-Zang Team
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_company")
public class Company extends BaseEntity {

    /**
     * 公司名称
     */
    @TableField("company_name")
    private String companyName;

    /**
     * 公司管理员用户ID
     */
    @TableField("admin_user_id")
    private Long adminUserId;
}


