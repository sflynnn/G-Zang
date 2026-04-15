package com.gzang.app.dto.transaction;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 更新交易记录请求DTO
 *
 * @author G-Zang Team
 */
@Schema(description = "更新交易记录请求")
public class UpdateTransactionDTO {

    @Schema(description = "金额", example = "100.00")
    @DecimalMin(value = "0.01", message = "金额必须大于0")
    private BigDecimal amount;

    @Schema(description = "交易类型：1=收入, 2=支出, 3=转账", example = "2")
    @Min(value = 1, message = "交易类型值无效")
    @Max(value = 3, message = "交易类型值无效")
    private Integer type;

    @Schema(description = "分类ID", example = "5")
    private Long categoryId;

    @Schema(description = "账户ID", example = "3")
    private Long accountId;

    @Schema(description = "交易时间", example = "2026-03-28T10:30:00")
    private LocalDateTime transactionTime;

    @Schema(description = "备注", example = "午餐费用")
    @Size(max = 500, message = "备注长度不能超过500")
    private String remark;

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public LocalDateTime getTransactionTime() {
        return transactionTime;
    }

    public void setTransactionTime(LocalDateTime transactionTime) {
        this.transactionTime = transactionTime;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
