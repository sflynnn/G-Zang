package com.gzang.app.dto.transaction;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 创建交易记录请求DTO
 *
 * @author G-Zang Team
 */
@Schema(description = "创建交易记录请求")
public class CreateTransactionDTO {

    @Schema(description = "金额", example = "100.00", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "金额不能为空")
    @DecimalMin(value = "0.01", message = "金额必须大于0")
    private BigDecimal amount;

    @Schema(description = "交易类型：1=收入, 2=支出, 3=转账", example = "2", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "交易类型不能为空")
    @Min(value = 1, message = "交易类型值无效")
    @Max(value = 3, message = "交易类型值无效")
    private Integer type;

    @Schema(description = "分类ID", example = "5", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "分类ID不能为空")
    private Long categoryId;

    @Schema(description = "账户ID", example = "3", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "账户ID不能为空")
    private Long accountId;

    @Schema(description = "账本ID", example = "1")
    private Long bookId;

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

    public Long getBookId() {
        return bookId;
    }

    public void setBookId(Long bookId) {
        this.bookId = bookId;
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
