package com.gzang.app.dto.budget;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 创建预算请求DTO
 *
 * @author G-Zang Team
 */
@Schema(description = "创建预算请求")
public class CreateBudgetDTO {

    @Schema(description = "预算名称", example = "月度餐饮预算")
    @NotBlank(message = "预算名称不能为空")
    @Size(max = 100, message = "预算名称长度不能超过100")
    private String name;

    @Schema(description = "账本ID", example = "1")
    private Long bookId;

    @Schema(description = "分类ID（NULL表示总预算）", example = "5")
    private Long categoryId;

    @Schema(description = "预算金额", example = "3000.00", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "预算金额不能为空")
    @DecimalMin(value = "0.01", message = "预算金额必须大于0")
    private BigDecimal amount;

    @Schema(description = "预算周期类型：1=月预算, 2=年预算, 3=周预算", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "预算周期类型不能为空")
    @Min(value = 1, message = "预算周期类型值无效")
    @Max(value = 3, message = "预算周期类型值无效")
    private Integer periodType;

    @Schema(description = "预算周期开始日期", example = "2026-05-01T00:00:00")
    private LocalDateTime periodStart;

    @Schema(description = "预算周期结束日期", example = "2026-05-31T23:59:59")
    private LocalDateTime periodEnd;

    @Schema(description = "预警阈值百分比（1-100）", example = "80")
    @Min(value = 1, message = "预警阈值最小为1")
    @Max(value = 100, message = "预警阈值最大为100")
    private Integer warningThreshold;

    @Schema(description = "是否启用预警", example = "true")
    private Boolean warningEnabled;

    @Schema(description = "备注", example = "日常餐饮开支预算")
    @Size(max = 500, message = "备注长度不能超过500")
    private String remark;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getBookId() {
        return bookId;
    }

    public void setBookId(Long bookId) {
        this.bookId = bookId;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Integer getPeriodType() {
        return periodType;
    }

    public void setPeriodType(Integer periodType) {
        this.periodType = periodType;
    }

    public LocalDateTime getPeriodStart() {
        return periodStart;
    }

    public void setPeriodStart(LocalDateTime periodStart) {
        this.periodStart = periodStart;
    }

    public LocalDateTime getPeriodEnd() {
        return periodEnd;
    }

    public void setPeriodEnd(LocalDateTime periodEnd) {
        this.periodEnd = periodEnd;
    }

    public Integer getWarningThreshold() {
        return warningThreshold;
    }

    public void setWarningThreshold(Integer warningThreshold) {
        this.warningThreshold = warningThreshold;
    }

    public Boolean getWarningEnabled() {
        return warningEnabled;
    }

    public void setWarningEnabled(Boolean warningEnabled) {
        this.warningEnabled = warningEnabled;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
