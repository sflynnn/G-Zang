package com.gzang.mobile.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gzang.app.entity.Budget;
import com.gzang.app.mapper.BudgetMapper;
import com.gzang.app.service.BudgetService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 预算服务实现类
 *
 * @author G-Zang Team
 */
@Service
public class BudgetServiceImpl extends ServiceImpl<BudgetMapper, Budget> implements BudgetService {

    private static final Logger log = LoggerFactory.getLogger(BudgetServiceImpl.class);

    // BR021: 默认预警阈值 80%
    private static final int DEFAULT_WARNING_THRESHOLD = 80;

    @Override
    public boolean createBudget(Budget budget) {
        log.info("创建预算: userId={}, name={}, amount={}",
                budget.getUserId(), budget.getName(), budget.getAmount());

        // BR021: 设置默认预警阈值
        if (budget.getWarningThreshold() == null) {
            budget.setWarningThreshold(DEFAULT_WARNING_THRESHOLD);
        }
        if (budget.getWarningEnabled() == null) {
            budget.setWarningEnabled(true);
        }

        boolean result = save(budget);
        if (result) {
            log.info("预算创建成功: id={}", budget.getId());
        }
        return result;
    }

    @Override
    public boolean updateBudget(Budget budget) {
        log.info("更新预算: id={}, userId={}", budget.getId(), budget.getUserId());

        Budget existing = getById(budget.getId());
        if (existing == null) {
            log.warn("预算不存在: id={}", budget.getId());
            return false;
        }

        if (!hasPermission(budget.getId(), budget.getUserId())) {
            log.warn("用户无权修改此预算: userId={}, budgetId={}", budget.getUserId(), budget.getId());
            return false;
        }

        boolean result = updateById(budget);
        if (result) {
            log.info("预算更新成功: id={}", budget.getId());
        }
        return result;
    }

    @Override
    @Transactional
    public boolean deleteBudget(Long id, Long userId) {
        log.info("删除预算: id={}, userId={}", id, userId);

        Budget budget = getById(id);
        if (budget == null) {
            log.warn("预算不存在: id={}", id);
            return false;
        }

        if (!hasPermission(id, userId)) {
            log.warn("用户无权删除此预算: userId={}, budgetId={}", userId, id);
            return false;
        }

        boolean result = removeById(id);
        if (result) {
            log.info("预算删除成功: id={}", id);
        }
        return result;
    }

    @Override
    public List<Budget> getBudgetsByUserId(Long userId, Long companyId, Long bookId, Integer periodType) {
        return getBaseMapper().selectBudgetsByUserId(userId, companyId, bookId, periodType);
    }

    @Override
    public IPage<Budget> getBudgetPage(Page<Budget> page, Long userId, Long companyId, Long bookId, Integer periodType) {
        return getBaseMapper().selectBudgetPage(page, userId, companyId, bookId, periodType);
    }

    @Override
    public BigDecimal getUsedAmount(Long userId, Long companyId, Long categoryId, Long bookId,
                                     LocalDateTime periodStart, LocalDateTime periodEnd) {
        if (categoryId != null) {
            return getBaseMapper().selectUsedAmountByCategory(userId, companyId, bookId, categoryId, periodStart, periodEnd);
        } else {
            return getBaseMapper().selectUsedAmountByBook(userId, companyId, bookId, periodStart, periodEnd);
        }
    }

    @Override
    public boolean updateUsedAmount(Long budgetId, BigDecimal usedAmount) {
        Budget budget = getById(budgetId);
        if (budget == null) {
            return false;
        }
        budget.setUsedAmount(usedAmount);
        return updateById(budget);
    }

    @Override
    public List<Budget> getWarningBudgets(Long userId, Long companyId) {
        return getBaseMapper().selectWarningEnabledBudgets(userId, companyId);
    }

    @Override
    public boolean hasPermission(Long budgetId, Long userId) {
        Budget budget = getById(budgetId);
        if (budget == null) {
            return false;
        }
        return budget.getUserId() != null && budget.getUserId().equals(userId);
    }

    /**
     * 计算预算使用率
     * BR021: 预算使用率 = 已使用金额 / 预算金额 * 100
     *
     * @param budget 预算对象
     * @return 使用率百分比 (0-100+，可超过100表示超支)
     */
    public BigDecimal calculateUsageRate(Budget budget) {
        if (budget == null || budget.getAmount() == null || budget.getAmount().compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        if (budget.getUsedAmount() == null) {
            return BigDecimal.ZERO;
        }
        return budget.getUsedAmount()
                .multiply(BigDecimal.valueOf(100))
                .divide(budget.getAmount(), 2, RoundingMode.HALF_UP);
    }

    /**
     * 判断预算是否触发预警
     * BR021: 当使用率 >= 预警阈值时触发预警
     *
     * @param budget 预算对象
     * @return 是否触发预警
     */
    public boolean isBudgetWarning(Budget budget) {
        if (budget == null) {
            return false;
        }
        // 如果未启用预警，直接返回false
        if (Boolean.FALSE.equals(budget.getWarningEnabled())) {
            return false;
        }
        // 获取预警阈值，默认为80%
        int threshold = budget.getWarningThreshold() != null ? budget.getWarningThreshold() : DEFAULT_WARNING_THRESHOLD;
        BigDecimal usageRate = calculateUsageRate(budget);
        return usageRate.compareTo(BigDecimal.valueOf(threshold)) >= 0;
    }

    /**
     * 获取预算预警状态详情
     * BR021: 返回预警的详细信息
     *
     * @param budget 预算对象
     * @return 预警状态详情
     */
    public BudgetWarningStatus getBudgetWarningStatus(Budget budget) {
        if (budget == null) {
            return new BudgetWarningStatus(false, "预算不存在", BigDecimal.ZERO, 0);
        }

        BigDecimal usageRate = calculateUsageRate(budget);
        int threshold = budget.getWarningThreshold() != null ? budget.getWarningThreshold() : DEFAULT_WARNING_THRESHOLD;
        boolean isWarning = Boolean.TRUE.equals(budget.getWarningEnabled()) && usageRate.compareTo(BigDecimal.valueOf(threshold)) >= 0;

        String message;
        if (!budget.getWarningEnabled()) {
            message = "预警已关闭";
        } else if (usageRate.compareTo(BigDecimal.valueOf(100)) >= 0) {
            message = String.format("预算已超支！已使用 %.0f%%", usageRate);
        } else if (usageRate.compareTo(BigDecimal.valueOf(threshold)) >= 0) {
            message = String.format("预算使用率已达 %.0f%%（预警阈值：%d%%）", usageRate, threshold);
        } else {
            message = String.format("预算使用正常（%.0f%%）", usageRate);
        }

        return new BudgetWarningStatus(isWarning, message, usageRate, threshold);
    }

    /**
     * 预算预警状态
     */
    public static class BudgetWarningStatus {
        private final boolean warning;
        private final String message;
        private final BigDecimal usageRate;
        private final int threshold;

        public BudgetWarningStatus(boolean warning, String message, BigDecimal usageRate, int threshold) {
            this.warning = warning;
            this.message = message;
            this.usageRate = usageRate;
            this.threshold = threshold;
        }

        public boolean isWarning() {
            return warning;
        }

        public String getMessage() {
            return message;
        }

        public BigDecimal getUsageRate() {
            return usageRate;
        }

        public int getThreshold() {
            return threshold;
        }
    }
}
