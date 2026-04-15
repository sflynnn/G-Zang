package com.gzang.app.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 交易记录实体类
 *
 * @author G-Zang Team
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_transaction")
public class Transaction extends BaseEntity {

    /**
     * 记录用户ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 所属公司ID（个人交易为NULL）
     */
    @TableField("company_id")
    private Long companyId;

    /**
     * 交易金额
     */
    @TableField("amount")
    private BigDecimal amount;

    /**
     * 交易类型 (1:收入, 2:支出)
     */
    @TableField("type")
    private Integer type;

    /**
     * 分类ID
     */
    @TableField("category_id")
    private Long categoryId;

    /**
     * 账户ID
     */
    @TableField("account_id")
    private Long accountId;

    /**
     * 交易发生时间
     */
    @TableField("transaction_time")
    private LocalDateTime transactionTime;

    /**
     * 备注
     */
    @TableField("remark")
    private String remark;

    /**
     * 关联业务ID（如维修单号）
     */
    @TableField("related_business_id")
    private String relatedBusinessId;
}
