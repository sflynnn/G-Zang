package com.gzang.app.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gzang.app.entity.Transaction;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 交易记录Mapper接口
 *
 * @author G-Zang Team
 */
public interface TransactionMapper extends BaseMapper<Transaction> {

    /**
     * 根据用户ID分页查询交易记录
     *
     * @param page 分页参数
     * @param userId 用户ID
     * @param companyId 公司ID（可选）
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param type 交易类型
     * @param categoryId 分类ID
     * @return 分页结果
     */
    IPage<Transaction> selectTransactionPage(
            Page<Transaction> page,
            @Param("userId") Long userId,
            @Param("companyId") Long companyId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime,
            @Param("type") Integer type,
            @Param("categoryId") Long categoryId);

    /**
     * 统计用户交易汇总
     *
     * @param userId 用户ID
     * @param companyId 公司ID（可选）
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 汇总信息
     */
    TransactionSummary selectTransactionSummary(
            @Param("userId") Long userId,
            @Param("companyId") Long companyId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);

    /**
     * 按分类统计交易金额
     *
     * @param userId 用户ID
     * @param companyId 公司ID（可选）
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param type 交易类型
     * @return 分类统计列表
     */
    List<CategorySummary> selectCategorySummary(
            @Param("userId") Long userId,
            @Param("companyId") Long companyId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime,
            @Param("type") Integer type);

    /**
     * 获取月度收支趋势
     *
     * @param userId 用户ID
     * @param companyId 公司ID（可选）
     * @param year 年份
     * @return 月度数据列表
     */
    List<MonthlyTrendData> selectMonthlyTrend(
            @Param("userId") Long userId,
            @Param("companyId") Long companyId,
            @Param("year") Integer year);

    /**
     * 获取年度对比
     *
     * @param userId 用户ID
     * @param companyId 公司ID（可选）
     * @param year 年份
     * @return 年度对比数据
     */
    YearlyComparisonData selectYearlyComparison(
            @Param("userId") Long userId,
            @Param("companyId") Long companyId,
            @Param("year") Integer year);

    /**
     * 月度趋势数据
     */
    class MonthlyTrendData {
        private Integer month;
        private BigDecimal income;
        private BigDecimal expense;

        public Integer getMonth() { return month; }
        public void setMonth(Integer month) { this.month = month; }
        public BigDecimal getIncome() { return income != null ? income : BigDecimal.ZERO; }
        public void setIncome(BigDecimal income) { this.income = income; }
        public BigDecimal getExpense() { return expense != null ? expense : BigDecimal.ZERO; }
        public void setExpense(BigDecimal expense) { this.expense = expense; }
        public BigDecimal getBalance() { return getIncome().subtract(getExpense()); }
    }

    /**
     * 年度对比数据
     */
    class YearlyComparisonData {
        private Integer year;
        private BigDecimal income;
        private BigDecimal expense;
        private Integer transactionCount;

        public Integer getYear() { return year; }
        public void setYear(Integer year) { this.year = year; }
        public BigDecimal getIncome() { return income != null ? income : BigDecimal.ZERO; }
        public void setIncome(BigDecimal income) { this.income = income; }
        public BigDecimal getExpense() { return expense != null ? expense : BigDecimal.ZERO; }
        public void setExpense(BigDecimal expense) { this.expense = expense; }
        public Integer getTransactionCount() { return transactionCount != null ? transactionCount : 0; }
        public void setTransactionCount(Integer transactionCount) { this.transactionCount = transactionCount; }
    }

    /**
     * 交易汇总DTO
     */
    class TransactionSummary {
        private BigDecimal totalIncome;
        private BigDecimal totalExpense;
        private Integer totalCount;

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

        public Integer getTotalCount() {
            return totalCount != null ? totalCount : 0;
        }

        public void setTotalCount(Integer totalCount) {
            this.totalCount = totalCount;
        }

        public BigDecimal getBalance() {
            return getTotalIncome().subtract(getTotalExpense());
        }
    }

    /**
     * 分类汇总DTO
     */
    class CategorySummary {
        private Long categoryId;
        private BigDecimal totalAmount;
        private Integer count;

        public Long getCategoryId() {
            return categoryId;
        }

        public void setCategoryId(Long categoryId) {
            this.categoryId = categoryId;
        }

        public BigDecimal getTotalAmount() {
            return totalAmount != null ? totalAmount : BigDecimal.ZERO;
        }

        public void setTotalAmount(BigDecimal totalAmount) {
            this.totalAmount = totalAmount;
        }

        public Integer getCount() {
            return count != null ? count : 0;
        }

        public void setCount(Integer count) {
            this.count = count;
        }
    }
}
