package com.gzang.app.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

/**
 * 预算视图对象
 *
 * @author G-Zang Team
 */
@Data
public class BudgetVO {

    private Long id;

    private Long userId;

    private Long companyId;

    private Long bookId;

    private String bookName;

    private Long categoryId;

    private String categoryName;

    private String categoryIcon;

    private String categoryColor;

    private BigDecimal amount;

    private BigDecimal usedAmount;

    private BigDecimal remainingAmount;

    private BigDecimal usageRate;

    private Integer periodType;

    private String periodTypeName;

    private LocalDateTime periodStart;

    private LocalDateTime periodEnd;

    private String name;

    private Integer warningThreshold;

    private Boolean warningEnabled;

    private Boolean isWarning;

    private String remark;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    /**
     * 计算剩余金额
     */
    public BigDecimal getRemainingAmount() {
        if (remainingAmount != null) {
            return remainingAmount;
        }
        if (amount == null || usedAmount == null) {
            return BigDecimal.ZERO;
        }
        return amount.subtract(usedAmount).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * 计算使用率百分比
     */
    public BigDecimal getUsageRate() {
        if (usageRate != null) {
            return usageRate;
        }
        if (amount == null || amount.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        if (usedAmount == null) {
            return BigDecimal.ZERO;
        }
        return usedAmount.multiply(BigDecimal.valueOf(100))
                .divide(amount, 2, RoundingMode.HALF_UP);
    }

    /**
     * 判断是否触发预警
     */
    public Boolean getIsWarning() {
        if (isWarning != null) {
            return isWarning;
        }
        if (Boolean.FALSE.equals(warningEnabled) || warningThreshold == null) {
            return false;
        }
        return getUsageRate().compareTo(BigDecimal.valueOf(warningThreshold)) >= 0;
    }

    /**
     * 获取周期类型名称
     */
    public String getPeriodTypeName() {
        if (periodTypeName != null) {
            return periodTypeName;
        }
        if (periodType == null) {
            return "未知";
        }
        return switch (periodType) {
            case 1 -> "月预算";
            case 2 -> "年预算";
            case 3 -> "周预算";
            default -> "未知";
        };
    }
}
