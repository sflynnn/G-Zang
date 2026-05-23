package com.gzang.mobile.service.impl;

import com.gzang.app.entity.Budget;
import com.gzang.app.mapper.BudgetMapper;
import com.gzang.app.service.BudgetAlertService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 预算预警服务实现
 * BR021: 实现80%预警机制和通知推送
 *
 * @author G-Zang Team
 */
@Service
public class BudgetAlertServiceImpl implements BudgetAlertService {

    private static final Logger log = LoggerFactory.getLogger(BudgetAlertServiceImpl.class);

    // 预警级别阈值配置
    private static final double WARNING_THRESHOLD = 80.0;  // 80%预警
    private static final double DANGER_THRESHOLD = 90.0;    // 90%预警
    private static final double EXCEEDED_THRESHOLD = 100.0; // 超支预警

    // 已发送预警的预算记录（避免重复发送）
    private final Set<String> alertedBudgets = Collections.newSetFromMap(new ConcurrentHashMap<>());

    // 预警历史记录: budgetId -> List<AlertHistoryRecord>
    private final Map<Long, List<AlertHistoryRecord>> alertHistoryMap = new ConcurrentHashMap<>();

    private final BudgetMapper budgetMapper;

    public BudgetAlertServiceImpl(BudgetMapper budgetMapper) {
        this.budgetMapper = budgetMapper;
    }

    @Override
    public AlertResult checkBudgetAlert(Long budgetId) {
        log.debug("检查预算预警: budgetId={}", budgetId);

        Budget budget = budgetMapper.selectById(budgetId);
        if (budget == null) {
            return new AlertResult(budgetId, "未知", 0, null, 0, false);
        }

        double usageRate = getBudgetUsageRate(budgetId);
        AlertLevel alertLevel = calculateAlertLevel(usageRate);
        double remainingAmount = budget.getAmount().doubleValue() - budget.getUsedAmount().doubleValue();

        boolean shouldAlert = alertLevel != null && !hasAlreadyAlerted(budgetId, alertLevel);

        AlertResult result = new AlertResult(
            budgetId,
            budget.getName(),
            usageRate,
            alertLevel,
            remainingAmount,
            shouldAlert
        );

        if (shouldAlert) {
            // 记录预警并发送通知
            recordAlertHistory(budgetId, alertLevel);
            sendAlertNotification(budgetId, alertLevel);
            markAsAlerted(budgetId, alertLevel);
        }

        return result;
    }

    @Override
    public List<AlertResult> checkAllBudgetsAlert() {
        log.info("批量检查所有预算预警状态");

        List<Budget> activeBudgets = budgetMapper.selectActiveBudgets();
        List<AlertResult> results = new ArrayList<>();

        if (activeBudgets != null) {
            for (Budget budget : activeBudgets) {
                AlertResult result = checkBudgetAlert(budget.getId());
                if (result.shouldAlert()) {
                    results.add(result);
                }
            }
        }

        log.info("检查完成: 总预算数={}, 需要预警={}", activeBudgets != null ? activeBudgets.size() : 0, results.size());
        return results;
    }

    @Override
    public double getBudgetUsageRate(Long budgetId) {
        Budget budget = budgetMapper.selectById(budgetId);
        if (budget == null || budget.getAmount() == null || budget.getAmount().doubleValue() == 0) {
            return 0;
        }

        BigDecimal usedAmount = budget.getUsedAmount() != null ? budget.getUsedAmount() : BigDecimal.ZERO;
        return Math.round((usedAmount.doubleValue() / budget.getAmount().doubleValue()) * 10000) / 100.0;
    }

    @Override
    public void recordAlertHistory(Long budgetId, AlertLevel alertLevel) {
        AlertHistoryRecord record = new AlertHistoryRecord();
        record.setBudgetId(budgetId);
        record.setAlertLevel(alertLevel);
        record.setUsageRateAtAlert(getBudgetUsageRate(budgetId));
        record.setAlertTime(System.currentTimeMillis());
        record.setNotificationSent(true);
        record.setAcknowledged(false);

        List<AlertHistoryRecord> history = alertHistoryMap.computeIfAbsent(budgetId, k -> new ArrayList<>());
        synchronized (history) {
            history.add(record);
            // 只保留最近100条记录
            if (history.size() > 100) {
                history.remove(0);
            }
        }

        log.info("记录预警历史: budgetId={}, level={}, usageRate={}",
            budgetId, alertLevel, record.getUsageRateAtAlert());
    }

    @Override
    public List<AlertHistoryRecord> getAlertHistory(Long budgetId, int limit) {
        List<AlertHistoryRecord> history = alertHistoryMap.get(budgetId);
        if (history == null) {
            return Collections.emptyList();
        }

        synchronized (history) {
            int size = history.size();
            int from = Math.max(0, size - limit);
            return new ArrayList<>(history.subList(from, size));
        }
    }

    @Override
    public void sendAlertNotification(Long budgetId, AlertLevel alertLevel) {
        Budget budget = budgetMapper.selectById(budgetId);
        if (budget == null) {
            return;
        }

        // 构建预警消息
        String message = buildAlertMessage(budget, alertLevel);
        log.info("发送预算预警通知: budgetId={}, level={}, message={}",
            budgetId, alertLevel, message);

        // TODO: 实际发送通知（推送、邮件、短信等）
        // notificationService.sendPush(userId, message);
        // notificationService.sendEmail(userId, "预算预警", message);
    }

    /**
     * 计算预警级别
     */
    private AlertLevel calculateAlertLevel(double usageRate) {
        if (usageRate >= EXCEEDED_THRESHOLD) {
            return AlertLevel.EXCEEDED;
        } else if (usageRate >= DANGER_THRESHOLD) {
            return AlertLevel.DANGER;
        } else if (usageRate >= WARNING_THRESHOLD) {
            return AlertLevel.WARNING;
        }
        return null;
    }

    /**
     * 构建预警消息
     */
    private String buildAlertMessage(Budget budget, AlertLevel alertLevel) {
        String levelText;
        switch (alertLevel) {
            case WARNING:
                levelText = "已达到80%";
                break;
            case DANGER:
                levelText = "已达到90%";
                break;
            case EXCEEDED:
                levelText = "已超支";
                break;
            default:
                levelText = "预警";
        }

        return String.format(
            "【预算预警】您的预算「%s」%s，当前使用 %.2f%% (%.2f / %.2f)。请注意控制支出！",
            budget.getName(),
            levelText,
            getBudgetUsageRate(budget.getId()),
            budget.getUsedAmount(),
            budget.getAmount()
        );
    }

    /**
     * 检查是否已经发送过该级别预警
     */
    private boolean hasAlreadyAlerted(Long budgetId, AlertLevel level) {
        return alertedBudgets.contains(getAlertKey(budgetId, level));
    }

    /**
     * 标记为已预警
     */
    private void markAsAlerted(Long budgetId, AlertLevel level) {
        alertedBudgets.add(getAlertKey(budgetId, level));

        // 清理过期记录（只保留最近1000条预警记录）
        if (alertedBudgets.size() > 1000) {
            alertedBudgets.clear();
        }
    }

    /**
     * 获取预警记录key
     */
    private String getAlertKey(Long budgetId, AlertLevel level) {
        return budgetId + "_" + level.name();
    }
}
