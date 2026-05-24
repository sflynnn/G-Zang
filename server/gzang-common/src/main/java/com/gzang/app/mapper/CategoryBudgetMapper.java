package com.gzang.app.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gzang.app.entity.CategoryBudget;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;
import java.util.List;

/**
 * 分类预算Mapper接口
 *
 * @author G-Zang Team
 */
public interface CategoryBudgetMapper extends BaseMapper<CategoryBudget> {

    /**
     * 查询分类预算
     *
     * @param userId 用户ID
     * @param bookId 账本ID
     * @param categoryId 分类ID
     * @param yearMonth 年月
     * @return 预算
     */
    @Select("SELECT * FROM t_category_budget WHERE user_id = #{userId} " +
            "AND (book_id = #{bookId} OR book_id IS NULL) " +
            "AND category_id = #{categoryId} " +
            "AND `year_month` = #{yearMonth} " +
            "AND is_enabled = 1 LIMIT 1")
    CategoryBudget selectBudget(@Param("userId") Long userId,
                                @Param("bookId") Long bookId,
                                @Param("categoryId") Long categoryId,
                                @Param("yearMonth") String yearMonth);

    /**
     * 查询用户所有分类预算
     *
     * @param userId 用户ID
     * @param bookId 账本ID
     * @param yearMonth 年月
     * @return 预算列表
     */
    @Select("SELECT * FROM t_category_budget WHERE user_id = #{userId} " +
            "AND (book_id = #{bookId} OR book_id IS NULL) " +
            "AND `year_month` = #{yearMonth} " +
            "AND is_enabled = 1")
    List<CategoryBudget> selectBudgetsByMonth(@Param("userId") Long userId,
                                               @Param("bookId") Long bookId,
                                               @Param("yearMonth") String yearMonth);

    /**
     * 统计某分类在指定月份的支出总额
     *
     * @param userId 用户ID
     * @param bookId 账本ID
     * @param categoryId 分类ID
     * @param yearMonth 年月
     * @return 支出总额
     */
    @Select("SELECT COALESCE(SUM(amount), 0) FROM t_transaction " +
            "WHERE user_id = #{userId} " +
            "AND book_id = #{bookId} " +
            "AND category_id = #{categoryId} " +
            "AND type = 2 " +
            "AND DATE_FORMAT(transaction_time, '%Y-%m') = #{yearMonth}")
    BigDecimal selectCategorySpent(@Param("userId") Long userId,
                                    @Param("bookId") Long bookId,
                                    @Param("categoryId") Long categoryId,
                                    @Param("yearMonth") String yearMonth);

    /**
     * 检查预算是否存在
     *
     * @param userId 用户ID
     * @param bookId 账本ID
     * @param categoryId 分类ID
     * @param yearMonth 年月
     * @param excludeId 排除的ID
     * @return 数量
     */
    @Select("SELECT COUNT(*) FROM t_category_budget WHERE user_id = #{userId} " +
            "AND (book_id = #{bookId} OR book_id IS NULL) " +
            "AND category_id = #{categoryId} " +
            "AND `year_month` = #{yearMonth} " +
            "AND id != #{excludeId}")
    int countBudget(@Param("userId") Long userId,
                    @Param("bookId") Long bookId,
                    @Param("categoryId") Long categoryId,
                    @Param("yearMonth") String yearMonth,
                    @Param("excludeId") Long excludeId);
}
