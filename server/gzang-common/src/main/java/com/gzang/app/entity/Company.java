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

    @TableField("company_name")
    private String companyName;

    @TableField("company_code")
    private String companyCode;

    @TableField("contact_name")
    private String contactName;

    @TableField("contact_phone")
    private String contactPhone;

    @TableField("contact_email")
    private String contactEmail;

    @TableField("address")
    private String address;

    @TableField("status")
    private Integer status = 1;

    @TableField("admin_user_id")
    private Long adminUserId;
}


