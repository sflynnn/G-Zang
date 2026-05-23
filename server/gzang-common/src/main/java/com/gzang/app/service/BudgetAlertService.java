package com.gzang.app.service;

/**
 * 预算预警服务接口
 * BR021: 实现80%预警机制和通知推送
 *
 * @author G-Zang Team
 */
public interface BudgetAlertService {

    /**
     * 检查预算使用情况并发送预警
     *
     * @param budgetId 预算ID
     * @return 预警结果
     */
    AlertResult checkBudgetAlert(Long budgetId);

    /**
     * 批量检查所有活跃预算的预警状态
     *
     * @return 需要预警的预算列表
     */
    java.util.List<AlertResult> checkAllBudgetsAlert();

    /**
     * 获取预算使用率
     *
     * @param budgetId 预算ID
     * @return 使用率百分比 (0-100+)
     */
    double getBudgetUsageRate(Long budgetId);

    /**
     * 记录预警历史
     *
     * @param budgetId 预算ID
     * @param alertLevel 预警级别 (WARNING: 80%, DANGER: 90%, EXCEEDED: 100%)
     */
    void recordAlertHistory(Long budgetId, AlertLevel alertLevel);

    /**
     * 获取预警历史
     *
     * @param budgetId 预算ID
     * @param limit 返回记录数
     * @return 预警历史列表
     */
    java.util.List<AlertHistoryRecord> getAlertHistory(Long budgetId, int limit);

    /**
     * 发送预警通知
     *
     * @param budgetId 预算ID
     * @param alertLevel 预警级别
     */
    void sendAlertNotification(Long budgetId, AlertLevel alertLevel);

    /**
     * 预警级别枚举
     */
    enum AlertLevel {
        WARNING(80, "warning"),      // 80%预警
        DANGER(90, "danger"),        // 90%预警
        EXCEEDED(100, "exceeded");  // 超支预警

        private final int threshold;
        private final String level;

        AlertLevel(int threshold, String level) {
            this.threshold = threshold;
            this.level = level;
        }

        public int getThreshold() {
            return threshold;
        }

        public String getLevel() {
            return level;
        }
    }

    /**
     * 预警结果
     */
    class AlertResult {
        private Long budgetId;
        private String budgetName;
        private double usageRate;
        private AlertLevel alertLevel;
        private double remainingAmount;
        private boolean shouldAlert;

        public AlertResult() {}

        public AlertResult(Long budgetId, String budgetName, double usageRate,
                          AlertLevel alertLevel, double remainingAmount, boolean shouldAlert) {
            this.budgetId = budgetId;
            this.budgetName = budgetName;
            this.usageRate = usageRate;
            this.alertLevel = alertLevel;
            this.remainingAmount = remainingAmount;
            this.shouldAlert = shouldAlert;
        }

        public Long getBudgetId() { return budgetId; }
        public void setBudgetId(Long budgetId) { this.budgetId = budgetId; }
        public String getBudgetName() { return budgetName; }
        public void setBudgetName(String budgetName) { this.budgetName = budgetName; }
        public double getUsageRate() { return usageRate; }
        public void setUsageRate(double usageRate) { this.usageRate = usageRate; }
        public AlertLevel getAlertLevel() { return alertLevel; }
        public void setAlertLevel(AlertLevel alertLevel) { this.alertLevel = alertLevel; }
        public double getRemainingAmount() { return remainingAmount; }
        public void setRemainingAmount(double remainingAmount) { this.remainingAmount = remainingAmount; }
        public boolean shouldAlert() { return shouldAlert; }
        public void setShouldAlert(boolean shouldAlert) { this.shouldAlert = shouldAlert; }
    }

    /**
     * 预警历史记录
     */
    class AlertHistoryRecord {
        private Long id;
        private Long budgetId;
        private AlertLevel alertLevel;
        private double usageRateAtAlert;
        private long alertTime;
        private boolean notificationSent;
        private boolean acknowledged;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public Long getBudgetId() { return budgetId; }
        public void setBudgetId(Long budgetId) { this.budgetId = budgetId; }
        public AlertLevel getAlertLevel() { return alertLevel; }
        public void setAlertLevel(AlertLevel alertLevel) { this.alertLevel = alertLevel; }
        public double getUsageRateAtAlert() { return usageRateAtAlert; }
        public void setUsageRateAtAlert(double usageRateAtAlert) { this.usageRateAtAlert = usageRateAtAlert; }
        public long getAlertTime() { return alertTime; }
        public void setAlertTime(long alertTime) { this.alertTime = alertTime; }
        public boolean isNotificationSent() { return notificationSent; }
        public void setNotificationSent(boolean notificationSent) { this.notificationSent = notificationSent; }
        public boolean isAcknowledged() { return acknowledged; }
        public void setAcknowledged(boolean acknowledged) { this.acknowledged = acknowledged; }
    }
}
