package com.gzang.mobile.service;

import com.gzang.app.mapper.TransactionMapper;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 报表统计服务接口
 *
 * @author G-Zang Team
 */
public interface ReportService {

    TransactionMapper.TransactionSummary getIncomeExpenseSummary(
            Long userId, Long companyId, LocalDateTime startTime, LocalDateTime endTime, Long bookId);

    List<TransactionMapper.MonthlyTrendData> getMonthlyTrend(Long userId, Long companyId, Integer year, Long bookId);

    List<TransactionMapper.CategorySummary> getCategoryReport(
            Long userId, Long companyId, LocalDateTime startTime, LocalDateTime endTime, Integer type, Long bookId);

    List<AccountBalanceData> getAccountBalanceReport(Long userId, Long companyId, Long bookId);

    List<YearlyComparisonVO> getYearlyComparison(Long userId, Long companyId, List<Integer> years, Long bookId);

    class AccountBalanceData {
        private Long accountId;
        private String accountName;
        private Integer accountType;
        private BigDecimal balance;
        private BigDecimal percentage;

        public Long getAccountId() { return accountId; }
        public void setAccountId(Long accountId) { this.accountId = accountId; }
        public String getAccountName() { return accountName; }
        public void setAccountName(String accountName) { this.accountName = accountName; }
        public Integer getAccountType() { return accountType; }
        public void setAccountType(Integer accountType) { this.accountType = accountType; }
        public BigDecimal getBalance() { return balance != null ? balance : BigDecimal.ZERO; }
        public void setBalance(BigDecimal balance) { this.balance = balance; }
        public BigDecimal getPercentage() { return percentage != null ? percentage : BigDecimal.ZERO; }
        public void setPercentage(BigDecimal percentage) { this.percentage = percentage; }
    }

    class YearlyComparisonVO {
        private Integer year;
        private BigDecimal totalIncome;
        private BigDecimal totalExpense;
        private BigDecimal netIncome;
        private Integer transactionCount;

        public Integer getYear() { return year; }
        public void setYear(Integer year) { this.year = year; }
        public BigDecimal getTotalIncome() { return totalIncome != null ? totalIncome : BigDecimal.ZERO; }
        public void setTotalIncome(BigDecimal totalIncome) { this.totalIncome = totalIncome; }
        public BigDecimal getTotalExpense() { return totalExpense != null ? totalExpense : BigDecimal.ZERO; }
        public void setTotalExpense(BigDecimal totalExpense) { this.totalExpense = totalExpense; }
        public BigDecimal getNetIncome() { return getTotalIncome().subtract(getTotalExpense()); }
        public void setNetIncome(BigDecimal netIncome) { this.netIncome = netIncome; }
        public Integer getTransactionCount() { return transactionCount != null ? transactionCount : 0; }
        public void setTransactionCount(Integer transactionCount) { this.transactionCount = transactionCount; }
    }
}
