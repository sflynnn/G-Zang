package com.gzang.app.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gzang.app.entity.Transaction;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

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
    List<com.gzang.app.service.ReportService.MonthlyTrendData> selectMonthlyTrend(
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
    com.gzang.app.service.ReportService.YearlyComparisonData selectYearlyComparison(
            @Param("userId") Long userId,
            @Param("companyId") Long companyId,
            @Param("year") Integer year);

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
