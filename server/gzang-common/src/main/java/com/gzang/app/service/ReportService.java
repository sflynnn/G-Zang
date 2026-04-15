package com.gzang.app.service;

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
    List<TransactionMapper.MonthlyTrendData> getMonthlyTrend(Long userId, Long companyId, Integer year);

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
    List<TransactionMapper.CategorySummary> getCategoryReport(
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
    List<TransactionMapper.YearlyComparisonData> getYearlyComparison(Long userId, Long companyId, List<Integer> years);

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
}
