package com.gzang.app.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 预算实体类
 *
 * @author G-Zang Team
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_budget")
public class Budget extends BaseEntity {

    /**
     * 所属用户ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 所属公司ID（个人预算为NULL）
     */
    @TableField("company_id")
    private Long companyId;

    /**
     * 账本ID
     */
    @TableField("book_id")
    private Long bookId;

    /**
     * 分类ID（NULL表示总预算）
     */
    @TableField("category_id")
    private Long categoryId;

    /**
     * 预算金额
     */
    @TableField("amount")
    private BigDecimal amount;

    /**
     * 已使用金额
     */
    @TableField("used_amount")
    private BigDecimal usedAmount;

    /**
     * 预算周期类型 (1:月预算, 2:年预算, 3:周预算)
     */
    @TableField("period_type")
    private Integer periodType;

    /**
     * 预算周期开始日期
     */
    @TableField("period_start")
    private LocalDateTime periodStart;

    /**
     * 预算周期结束日期
     */
    @TableField("period_end")
    private LocalDateTime periodEnd;

    /**
     * 预算名称
     */
    @TableField("name")
    private String name;

    /**
     * 预警阈值（百分比，如80表示80%时预警）
     */
    @TableField("warning_threshold")
    private Integer warningThreshold;

    /**
     * 是否启用预警
     */
    @TableField("warning_enabled")
    private Boolean warningEnabled;

    /**
     * 备注
     */
    @TableField("remark")
    private String remark;
}
