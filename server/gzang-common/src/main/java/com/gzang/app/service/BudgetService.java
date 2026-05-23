package com.gzang.app.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.gzang.app.entity.Budget;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 预算服务接口
 *
 * @author G-Zang Team
 */
public interface BudgetService extends IService<Budget> {

    /**
     * 创建预算
     *
     * @param budget 预算信息
     * @return 是否成功
     */
    boolean createBudget(Budget budget);

    /**
     * 更新预算
     *
     * @param budget 预算信息
     * @return 是否成功
     */
    boolean updateBudget(Budget budget);

    /**
     * 删除预算
     *
     * @param id 预算ID
     * @param userId 用户ID（用于权限验证）
     * @return 是否成功
     */
    boolean deleteBudget(Long id, Long userId);

    /**
     * 根据用户ID查询预算列表
     *
     * @param userId 用户ID
     * @param companyId 公司ID（可选）
     * @param bookId 账本ID（可选）
     * @param periodType 周期类型（可选）
     * @return 预算列表
     */
    List<Budget> getBudgetsByUserId(Long userId, Long companyId, Long bookId, Integer periodType);

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
    IPage<Budget> getBudgetPage(Page<Budget> page, Long userId, Long companyId, Long bookId, Integer periodType);

    /**
     * 获取当前周期内已使用的金额
     *
     * @param userId 用户ID
     * @param companyId 公司ID（可选）
     * @param categoryId 分类ID（可选，NULL表示总预算）
     * @param bookId 账本ID
     * @param periodStart 周期开始时间
     * @param periodEnd 周期结束时间
     * @return 已使用金额
     */
    BigDecimal getUsedAmount(Long userId, Long companyId, Long categoryId, Long bookId,
                              LocalDateTime periodStart, LocalDateTime periodEnd);

    /**
     * 更新预算已使用金额
     *
     * @param budgetId 预算ID
     * @param usedAmount 新的已使用金额
     * @return 是否成功
     */
    boolean updateUsedAmount(Long budgetId, BigDecimal usedAmount);

    /**
     * 获取所有需要预警的预算
     *
     * @param userId 用户ID
     * @param companyId 公司ID（可选）
     * @return 需要预警的预算列表
     */
    List<Budget> getWarningBudgets(Long userId, Long companyId);

    /**
     * 检查用户是否有权限操作此预算
     *
     * @param budgetId 预算ID
     * @param userId 用户ID
     * @return 是否有权限
     */
    boolean hasPermission(Long budgetId, Long userId);
}
