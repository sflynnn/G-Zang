package com.gzang.app.service;

import com.gzang.app.mapper.TransactionMapper;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 报表统计服务接口
 *
 * @author G-Zang Team
 */
public interface ReportService {

    /**
     * 获取收入支出汇总报表
     *
     * @param userId 用户ID
     * @param companyId 公司ID（可选）
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 汇总信息
     */
    TransactionMapper.TransactionSummary getIncomeExpenseSummary(
            Long userId, Long companyId, LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 获取月度收支趋势
     *
     * @param userId 用户ID
     * @param companyId 公司ID（可选）
     * @param year 年份
     * @return 月度数据列表
     */
    List<MonthlyTrendData> getMonthlyTrend(Long userId, Long companyId, Integer year);

    /**
     * 获取分类统计报表
     *
     * @param userId 用户ID
     * @param companyId 公司ID（可选）
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param type 交易类型
     * @return 分类统计列表
     */
    List<CategoryReportData> getCategoryReport(
            Long userId, Long companyId, LocalDateTime startTime, LocalDateTime endTime, Integer type);

    /**
     * 获取账户余额报表
     *
     * @param userId 用户ID
     * @param companyId 公司ID（可选）
     * @return 账户余额列表
     */
    List<AccountBalanceData> getAccountBalanceReport(Long userId, Long companyId);

    /**
     * 获取年度收支对比
     *
     * @param userId 用户ID
     * @param companyId 公司ID（可选）
     * @param years 年份列表
     * @return 年度对比数据
     */
    List<YearlyComparisonData> getYearlyComparison(Long userId, Long companyId, List<Integer> years);

    /**
     * 月度趋势数据DTO
     */
    class MonthlyTrendData {
        private Integer month;
        private BigDecimal income;
        private BigDecimal expense;
        private BigDecimal balance;

        public Integer getMonth() {
            return month;
        }

        public void setMonth(Integer month) {
            this.month = month;
        }

        public BigDecimal getIncome() {
            return income != null ? income : BigDecimal.ZERO;
        }

        public void setIncome(BigDecimal income) {
            this.income = income;
        }

        public BigDecimal getExpense() {
            return expense != null ? expense : BigDecimal.ZERO;
        }

        public void setExpense(BigDecimal expense) {
            this.expense = expense;
        }

        public BigDecimal getBalance() {
            return getIncome().subtract(getExpense());
        }

        public void setBalance(BigDecimal balance) {
            this.balance = balance;
        }
    }

    /**
     * 分类报表数据DTO
     */
    class CategoryReportData {
        private Long categoryId;
        private String categoryName;
        private Integer type;
        private BigDecimal totalAmount;
        private Integer transactionCount;
        private BigDecimal percentage;

        public Long getCategoryId() {
            return categoryId;
        }

        public void setCategoryId(Long categoryId) {
            this.categoryId = categoryId;
        }

        public String getCategoryName() {
            return categoryName;
        }

        public void setCategoryName(String categoryName) {
            this.categoryName = categoryName;
        }

        public Integer getType() {
            return type;
        }

        public void setType(Integer type) {
            this.type = type;
        }

        public BigDecimal getTotalAmount() {
            return totalAmount != null ? totalAmount : BigDecimal.ZERO;
        }

        public void setTotalAmount(BigDecimal totalAmount) {
            this.totalAmount = totalAmount;
        }

        public Integer getTransactionCount() {
            return transactionCount != null ? transactionCount : 0;
        }

        public void setTransactionCount(Integer transactionCount) {
            this.transactionCount = transactionCount;
        }

        public BigDecimal getPercentage() {
            return percentage != null ? percentage : BigDecimal.ZERO;
        }

        public void setPercentage(BigDecimal percentage) {
            this.percentage = percentage;
        }
    }

    /**
     * 账户余额数据DTO
     */
    class AccountBalanceData {
        private Long accountId;
        private String accountName;
        private Integer accountType;
        private BigDecimal balance;
        private BigDecimal percentage;

        public Long getAccountId() {
            return accountId;
        }

        public void setAccountId(Long accountId) {
            this.accountId = accountId;
        }

        public String getAccountName() {
            return accountName;
        }

        public void setAccountName(String accountName) {
            this.accountName = accountName;
        }

        public Integer getAccountType() {
            return accountType;
        }

        public void setAccountType(Integer accountType) {
            this.accountType = accountType;
        }

        public BigDecimal getBalance() {
            return balance != null ? balance : BigDecimal.ZERO;
        }

        public void setBalance(BigDecimal balance) {
            this.balance = balance;
        }

        public BigDecimal getPercentage() {
            return percentage != null ? percentage : BigDecimal.ZERO;
        }

        public void setPercentage(BigDecimal percentage) {
            this.percentage = percentage;
        }
    }

    /**
     * 年度对比数据DTO
     */
    class YearlyComparisonData {
        private Integer year;
        private BigDecimal totalIncome;
        private BigDecimal totalExpense;
        private BigDecimal netIncome;
        private Integer transactionCount;

        public Integer getYear() {
            return year;
        }

        public void setYear(Integer year) {
            this.year = year;
        }

        public BigDecimal getTotalIncome() {
            return totalIncome != null ? totalIncome : BigDecimal.ZERO;
        }

        public void setTotalIncome(BigDecimal totalIncome) {
            this.totalIncome = totalIncome;
        }

        public BigDecimal getTotalExpense() {
            return totalExpense != null ? totalExpense : BigDecimal.ZERO;
        }

        public void setTotalExpense(BigDecimal totalExpense) {
            this.totalExpense = totalExpense;
        }

        public BigDecimal getNetIncome() {
            return getTotalIncome().subtract(getTotalExpense());
        }

        public void setNetIncome(BigDecimal netIncome) {
            this.netIncome = netIncome;
        }

        public Integer getTransactionCount() {
            return transactionCount != null ? transactionCount : 0;
        }

        public void setTransactionCount(Integer transactionCount) {
            this.transactionCount = transactionCount;
        }
    }
}
