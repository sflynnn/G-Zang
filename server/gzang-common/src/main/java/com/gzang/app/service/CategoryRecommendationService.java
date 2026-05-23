package com.gzang.app.service;

import com.gzang.app.entity.Transaction;

import java.util.List;

/**
 * 智能分类推荐服务接口
 * 基于历史数据训练分类推荐模型
 *
 * @author G-Zang Team
 */
public interface CategoryRecommendationService {

    /**
     * 根据交易描述推荐分类
     *
     * @param userId 用户ID
     * @param description 交易描述（可以是商家名称、备注等）
     * @return 推荐的分类ID列表，按置信度排序
     */
    List<Long> recommendCategories(Long userId, String description);

    /**
     * 根据交易描述和金额推荐分类
     *
     * @param userId 用户ID
     * @param description 交易描述
     * @param amount 交易金额
     * @return 推荐的分类ID列表
     */
    List<Long> recommendCategoriesWithAmount(Long userId, String description, Double amount);

    /**
     * 根据商家名称学习交易模式
     *
     * @param userId 用户ID
     * @param merchantName 商家名称
     * @param categoryId 关联的分类ID
     */
    void learnTransactionPattern(Long userId, String merchantName, Long categoryId);

    /**
     * 获取用户的分类使用统计
     *
     * @param userId 用户ID
     * @return 分类使用统计列表
     */
    List<CategoryUsageStat> getCategoryUsageStats(Long userId);

    /**
     * 获取推荐历史记录
     *
     * @param userId 用户ID
     * @param limit 返回记录数
     * @return 推荐历史列表
     */
    List<RecommendationRecord> getRecommendationHistory(Long userId, int limit);

    /**
     * 清除用户的推荐历史
     *
     * @param userId 用户ID
     */
    void clearRecommendationHistory(Long userId);

    /**
     * 分类使用统计
     */
    class CategoryUsageStat {
        private Long categoryId;
        private String categoryName;
        private int usageCount;
        private double usagePercentage;
        private Long lastUsedTime;

        public Long getCategoryId() { return categoryId; }
        public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }
        public String getCategoryName() { return categoryName; }
        public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
        public int getUsageCount() { return usageCount; }
        public void setUsageCount(int usageCount) { this.usageCount = usageCount; }
        public double getUsagePercentage() { return usagePercentage; }
        public void setUsagePercentage(double usagePercentage) { this.usagePercentage = usagePercentage; }
        public Long getLastUsedTime() { return lastUsedTime; }
        public void setLastUsedTime(Long lastUsedTime) { this.lastUsedTime = lastUsedTime; }
    }

    /**
     * 推荐记录
     */
    class RecommendationRecord {
        private String description;
        private Long recommendedCategoryId;
        private Long actualCategoryId;
        private boolean accepted;
        private long timestamp;

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public Long getRecommendedCategoryId() { return recommendedCategoryId; }
        public void setRecommendedCategoryId(Long recommendedCategoryId) { this.recommendedCategoryId = recommendedCategoryId; }
        public Long getActualCategoryId() { return actualCategoryId; }
        public void setActualCategoryId(Long actualCategoryId) { this.actualCategoryId = actualCategoryId; }
        public boolean isAccepted() { return accepted; }
        public void setAccepted(boolean accepted) { this.accepted = accepted; }
        public long getTimestamp() { return timestamp; }
        public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    }
}
