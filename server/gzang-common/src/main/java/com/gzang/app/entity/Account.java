package com.gzang.app.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 账户实体类
 *
 * @author G-Zang Team
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_account")
public class Account extends BaseEntity {

    /**
     * 所属用户ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 所属公司ID（个人账户为NULL）
     */
    @TableField("company_id")
    private Long companyId;

    /**
     * 账户名称（如：现金、支付宝、招商银行）
     */
    @TableField("account_name")
    private String accountName;

    /**
     * 账户类型 (1:现金, 2:银行卡, 3:电子支付)
     */
    @TableField("account_type")
    private Integer accountType;

    /**
     * 当前余额
     */
    @TableField("balance")
    private BigDecimal balance;
}
