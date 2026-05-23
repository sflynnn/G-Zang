package com.gzang.mobile.service.impl;

import com.gzang.app.service.CategoryRecommendationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 智能分类推荐服务实现
 * 基于历史数据训练分类推荐模型
 *
 * @author G-Zang Team
 */
@Service
public class CategoryRecommendationServiceImpl implements CategoryRecommendationService {

    private static final Logger log = LoggerFactory.getLogger(CategoryRecommendationServiceImpl.class);

    // 用户交易模式: userId -> (keyword -> categoryId + count)
    private final Map<Long, Map<String, CategoryPattern>> userPatterns = new ConcurrentHashMap<>();

    // 分类使用统计: userId -> List<CategoryUsageStat>
    private final Map<Long, List<CategoryUsageStat>> categoryStats = new ConcurrentHashMap<>();

    // 推荐历史: userId -> List<RecommendationRecord>
    private final Map<Long, List<RecommendationRecord>> recommendationHistory = new ConcurrentHashMap<>();

    // 内置关键词映射（常见商家/关键词 -> 分类）
    private static final Map<String, Long> BUILT_IN_PATTERNS = new HashMap<>();

    static {
        // 餐饮类
        addPattern("餐厅", "餐饮", 1);
        addPattern("饭店", "餐饮", 1);
        addPattern("外卖", "餐饮", 1);
        addPattern("麦当劳", "餐饮", 1);
        addPattern("肯德基", "餐饮", 1);
        addPattern("星巴克", "餐饮", 1);
        addPattern("奶茶", "餐饮", 1);
        addPattern("咖啡", "餐饮", 1);

        // 交通类
        addPattern("地铁", "交通", 2);
        addPattern("公交", "交通", 2);
        addPattern("出租车", "交通", 2);
        addPattern("滴滴", "交通", 2);
        addPattern("加油", "交通", 2);
        addPattern("停车", "交通", 2);
        addPattern("高铁", "交通", 2);
        addPattern("火车", "交通", 2);
        addPattern("飞机", "交通", 2);

        // 购物类
        addPattern("超市", "购物", 3);
        addPattern("便利店", "购物", 3);
        addPattern("淘宝", "购物", 3);
        addPattern("京东", "购物", 3);
        addPattern("天猫", "购物", 3);
        addPattern("苏宁", "购物", 3);

        // 娱乐类
        addPattern("电影", "娱乐", 4);
        addPattern("KTV", "娱乐", 4);
        addPattern("网吧", "娱乐", 4);
        addPattern("游戏", "娱乐", 4);
        addPattern("演唱会", "娱乐", 4);

        // 居住类
        addPattern("房租", "居住", 5);
        addPattern("水电", "居住", 5);
        addPattern("物业", "居住", 5);

        // 医疗类
        addPattern("医院", "医疗", 6);
        addPattern("药店", "医疗", 6);
        addPattern("门诊", "医疗", 6);

        // 教育类
        addPattern("培训", "教育", 7);
        addPattern("学费", "教育", 7);
        addPattern("书店", "教育", 7);

        // 通讯类
        addPattern("话费", "通讯", 8);
        addPattern("流量", "通讯", 8);
        addPattern("宽带", "通讯", 8);
    }

    private static void addPattern(String keyword, String categoryName, long categoryId) {
        // 这里简化处理，实际应该通过分类名称查找ID
        BUILT_IN_PATTERNS.put(keyword, categoryId);
    }

    @Override
    public List<Long> recommendCategories(Long userId, String description) {
        if (description == null || description.trim().isEmpty()) {
            return Collections.emptyList();
        }

        log.debug("推荐分类: userId={}, description={}", userId, description);
        List<Long> recommendations = new ArrayList<>();
        Set<Long> added = new HashSet<>();

        String normalizedDesc = normalizeText(description);

        // 1. 先检查用户自定义模式
        Map<String, CategoryPattern> patterns = userPatterns.get(userId);
        if (patterns != null) {
            for (Map.Entry<String, CategoryPattern> entry : patterns.entrySet()) {
                if (normalizedDesc.contains(entry.getKey().toLowerCase())) {
                    Long categoryId = entry.getValue().categoryId;
                    if (added.add(categoryId)) {
                        recommendations.add(categoryId);
                    }
                }
            }
        }

        // 2. 检查内置模式
        for (Map.Entry<String, Long> entry : BUILT_IN_PATTERNS.entrySet()) {
            if (normalizedDesc.contains(entry.getKey())) {
                if (added.add(entry.getValue())) {
                    recommendations.add(entry.getValue());
                }
            }
        }

        // 3. 添加用户最常用的分类作为兜底
        List<CategoryUsageStat> stats = getCategoryUsageStats(userId);
        for (CategoryUsageStat stat : stats) {
            if (added.add(stat.getCategoryId())) {
                recommendations.add(stat.getCategoryId());
            }
            if (recommendations.size() >= 3) break;
        }

        log.debug("推荐结果: userId={}, recommendations={}", userId, recommendations);
        return recommendations;
    }

    @Override
    public List<Long> recommendCategoriesWithAmount(Long userId, String description, Double amount) {
        List<Long> recommendations = recommendCategories(userId, description);

        // 如果金额较大，增加"大额消费"相关分类的权重
        if (amount != null && amount > 1000) {
            // 可以添加金额相关的逻辑
        }

        return recommendations;
    }

    @Override
    public void learnTransactionPattern(Long userId, String merchantName, Long categoryId) {
        if (merchantName == null || merchantName.trim().isEmpty() || categoryId == null) {
            return;
        }

        log.info("学习交易模式: userId={}, merchant={}, categoryId={}", userId, merchantName, categoryId);

        String normalizedMerchant = normalizeText(merchantName);
        Map<String, CategoryPattern> patterns = userPatterns.computeIfAbsent(userId, k -> new ConcurrentHashMap<>());

        patterns.compute(normalizedMerchant.toLowerCase(), (k, existing) -> {
            if (existing == null) {
                return new CategoryPattern(categoryId, 1);
            } else {
                existing.count++;
                return existing;
            }
        });

        // 记录推荐历史
        RecommendationRecord record = new RecommendationRecord();
        record.setDescription(merchantName);
        record.setRecommendedCategoryId(categoryId);
        record.setActualCategoryId(categoryId);
        record.setAccepted(true);
        record.setTimestamp(System.currentTimeMillis());

        List<RecommendationRecord> history = recommendationHistory.computeIfAbsent(userId, k -> new ArrayList<>());
        synchronized (history) {
            history.add(record);
            // 只保留最近100条记录
            if (history.size() > 100) {
                history.remove(0);
            }
        }
    }

    @Override
    public List<CategoryUsageStat> getCategoryUsageStats(Long userId) {
        return categoryStats.computeIfAbsent(userId, k -> {
            List<RecommendationRecord> history = recommendationHistory.get(userId);
            if (history == null || history.isEmpty()) {
                return Collections.emptyList();
            }

            // 统计分类使用频率
            Map<Long, Integer> categoryCount = new HashMap<>();
            Map<Long, Long> lastUsed = new HashMap<>();
            long now = System.currentTimeMillis();

            for (RecommendationRecord record : history) {
                if (record.isAccepted() && record.getActualCategoryId() != null) {
                    categoryCount.merge(record.getActualCategoryId(), 1, Integer::sum);
                    lastUsed.put(record.getActualCategoryId(), record.getTimestamp());
                }
            }

            int total = categoryCount.values().stream().mapToInt(Integer::intValue).sum();

            return categoryCount.entrySet().stream()
                    .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
                    .map(entry -> {
                        CategoryUsageStat stat = new CategoryUsageStat();
                        stat.setCategoryId(entry.getKey());
                        stat.setUsageCount(entry.getValue());
                        stat.setUsagePercentage(total > 0 ? (entry.getValue() * 100.0 / total) : 0);
                        stat.setLastUsedTime(lastUsed.get(entry.getKey()));
                        return stat;
                    })
                    .collect(Collectors.toList());
        });
    }

    @Override
    public List<RecommendationRecord> getRecommendationHistory(Long userId, int limit) {
        List<RecommendationRecord> history = recommendationHistory.get(userId);
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
    public void clearRecommendationHistory(Long userId) {
        recommendationHistory.remove(userId);
        userPatterns.remove(userId);
        categoryStats.remove(userId);
        log.info("清除推荐历史: userId={}", userId);
    }

    /**
     * 文本归一化
     */
    private String normalizeText(String text) {
        if (text == null) return "";
        return text.toLowerCase()
                .replaceAll("[\\s,，。、！!？?]+", "")
                .replaceAll("[^\\u4e00-\\u9fa5a-z0-9]", "");
    }

    /**
     * 分类模式
     */
    private static class CategoryPattern {
        Long categoryId;
        int count;

        CategoryPattern(Long categoryId, int count) {
            this.categoryId = categoryId;
            this.count = count;
        }
    }
}
