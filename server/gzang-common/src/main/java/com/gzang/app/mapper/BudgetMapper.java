package com.gzang.app.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gzang.app.entity.Budget;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 预算Mapper接口
 *
 * @author G-Zang Team
 */
public interface BudgetMapper extends BaseMapper<Budget> {

    /**
     * 根据用户ID查询预算列表
     *
     * @param userId 用户ID
     * @param companyId 公司ID（可选）
     * @param bookId 账本ID（可选）
     * @param periodType 周期类型（可选）
     * @return 预算列表
     */
    List<Budget> selectBudgetsByUserId(
            @Param("userId") Long userId,
            @Param("companyId") Long companyId,
            @Param("bookId") Long bookId,
            @Param("periodType") Integer periodType);

    /**
     * 分页查询预算
     *
     * @param page 分页参数
     * @param userId 用户ID
     * @param companyId 公司ID（可选）
     * @param bookId 账本ID（可选）
     * @param periodType 周期类型
     * @return 分页结果
     */
    IPage<Budget> selectBudgetPage(
            Page<Budget> page,
            @Param("userId") Long userId,
            @Param("companyId") Long companyId,
            @Param("bookId") Long bookId,
            @Param("periodType") Integer periodType);

    /**
     * 查询指定分类在周期内的支出总额
     *
     * @param userId 用户ID
     * @param companyId 公司ID（可选）
     * @param bookId 账本ID（可选）
     * @param categoryId 分类ID
     * @param periodStart 周期开始时间
     * @param periodEnd 周期结束时间
     * @return 支出总额
     */
    @org.apache.ibatis.annotations.Select(
            "SELECT COALESCE(SUM(amount), 0) FROM t_transaction " +
            "WHERE user_id = #{userId} AND type = 2 " +
            "<if test='companyId != null'>AND company_id = #{companyId}</if> " +
            "<if test='bookId != null'>AND book_id = #{bookId}</if> " +
            "<if test='categoryId != null'>AND category_id = #{categoryId}</if> " +
            "AND transaction_time &gt;= #{periodStart} " +
            "AND transaction_time &lt;= #{periodEnd}"
    )
    BigDecimal selectUsedAmountByCategory(
            @Param("userId") Long userId,
            @Param("companyId") Long companyId,
            @Param("bookId") Long bookId,
            @Param("categoryId") Long categoryId,
            @Param("periodStart") LocalDateTime periodStart,
            @Param("periodEnd") LocalDateTime periodEnd);

    /**
     * 查询指定账本在周期内的支出总额（总预算）
     *
     * @param userId 用户ID
     * @param companyId 公司ID（可选）
     * @param bookId 账本ID
     * @param periodStart 周期开始时间
     * @param periodEnd 周期结束时间
     * @return 支出总额
     */
    @org.apache.ibatis.annotations.Select(
            "SELECT COALESCE(SUM(amount), 0) FROM t_transaction " +
            "WHERE user_id = #{userId} AND type = 2 " +
            "<if test='companyId != null'>AND company_id = #{companyId}</if> " +
            "AND book_id = #{bookId} " +
            "AND transaction_time &gt;= #{periodStart} " +
            "AND transaction_time &lt;= #{periodEnd}"
    )
    BigDecimal selectUsedAmountByBook(
            @Param("userId") Long userId,
            @Param("companyId") Long companyId,
            @Param("bookId") Long bookId,
            @Param("periodStart") LocalDateTime periodStart,
            @Param("periodEnd") LocalDateTime periodEnd);

    /**
     * 获取用户所有启用了预警的预算
     *
     * @param userId 用户ID
     * @param companyId 公司ID（可选）
     * @return 启用了预警的预算列表
     */
    List<Budget> selectWarningEnabledBudgets(
            @Param("userId") Long userId,
            @Param("companyId") Long companyId);

    /**
     * 查询所有活跃预算（用于预警检查）
     * 查询当前周期内有效的预算
     *
     * @return 活跃预算列表
     */
    List<Budget> selectActiveBudgets();
}
