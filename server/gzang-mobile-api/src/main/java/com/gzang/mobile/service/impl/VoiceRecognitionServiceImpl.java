package com.gzang.mobile.service.impl;

import com.gzang.mobile.service.VoiceRecognitionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 语音识别服务实现
 * 
 * @author G-Zang Team
 */
@Service
public class VoiceRecognitionServiceImpl implements VoiceRecognitionService {

    private static final Logger log = LoggerFactory.getLogger(VoiceRecognitionServiceImpl.class);

    // 金额匹配模式
    private static final Pattern AMOUNT_PATTERN = Pattern.compile("([0-9]+(?:\\.[0-9]{1,2})?)\\s*(?:块|元|钱)?");
    
    // 收入关键词
    private static final String[] INCOME_KEYWORDS = {"工资", "收入", "奖金", "红包", "退款", "报销", "利息", "分红", "兼职", "赚了", "收到"};
    
    // 支出关键词
    private static final String[] EXPENSE_KEYWORDS = {"花了", "买", "消费", "支出", "用了", "吃饭", "打车", "网购", "充值", "房租", "水电"};

    // 分类关键词映射
    private static final Map<String, Long> CATEGORY_KEYWORDS = new HashMap<>();
    
    static {
        // 餐饮
        CATEGORY_KEYWORDS.put("吃饭", 1L);
        CATEGORY_KEYWORDS.put("早餐", 1L);
        CATEGORY_KEYWORDS.put("午餐", 1L);
        CATEGORY_KEYWORDS.put("晚餐", 1L);
        CATEGORY_KEYWORDS.put("外卖", 1L);
        CATEGORY_KEYWORDS.put("奶茶", 1L);
        CATEGORY_KEYWORDS.put("咖啡", 1L);
        CATEGORY_KEYWORDS.put("餐饮", 1L);
        
        // 交通
        CATEGORY_KEYWORDS.put("打车", 2L);
        CATEGORY_KEYWORDS.put("地铁", 2L);
        CATEGORY_KEYWORDS.put("公交", 2L);
        CATEGORY_KEYWORDS.put("交通", 2L);
        CATEGORY_KEYWORDS.put("加油", 2L);
        CATEGORY_KEYWORDS.put("停车", 2L);
        
        // 购物
        CATEGORY_KEYWORDS.put("网购", 3L);
        CATEGORY_KEYWORDS.put("淘宝", 3L);
        CATEGORY_KEYWORDS.put("京东", 3L);
        CATEGORY_KEYWORDS.put("购物", 3L);
        CATEGORY_KEYWORDS.put("超市", 3L);
        CATEGORY_KEYWORDS.put("日用品", 3L);
        
        // 娱乐
        CATEGORY_KEYWORDS.put("电影", 4L);
        CATEGORY_KEYWORDS.put("游戏", 4L);
        CATEGORY_KEYWORDS.put("娱乐", 4L);
        CATEGORY_KEYWORDS.put("KTV", 4L);
        CATEGORY_KEYWORDS.put("唱歌", 4L);
        CATEGORY_KEYWORDS.put("健身", 4L);
        
        // 医疗
        CATEGORY_KEYWORDS.put("医疗", 5L);
        CATEGORY_KEYWORDS.put("买药", 5L);
        CATEGORY_KEYWORDS.put("医院", 5L);
        CATEGORY_KEYWORDS.put("看病", 5L);
        
        // 教育
        CATEGORY_KEYWORDS.put("教育", 6L);
        CATEGORY_KEYWORDS.put("学费", 6L);
        CATEGORY_KEYWORDS.put("培训", 6L);
        CATEGORY_KEYWORDS.put("书籍", 6L);
        
        // 居住
        CATEGORY_KEYWORDS.put("房租", 7L);
        CATEGORY_KEYWORDS.put("水电", 7L);
        CATEGORY_KEYWORDS.put("物业", 7L);
        CATEGORY_KEYWORDS.put("居住", 7L);
        
        // 通讯
        CATEGORY_KEYWORDS.put("通讯", 8L);
        CATEGORY_KEYWORDS.put("话费", 8L);
        CATEGORY_KEYWORDS.put("流量", 8L);
        CATEGORY_KEYWORDS.put("手机", 8L);
    }

    @Override
    public String speechToText(byte[] audioData, String format) {
        // TODO: 集成真实的语音识别服务（如百度、腾讯、阿里云ASR）
        // 目前返回模拟数据，实际项目中需要调用第三方API
        
        log.info("开始语音转文字, format={}, audioSize={}", format, audioData != null ? audioData.length : 0);
        
        // 模拟返回
        return "今天中午吃饭花了50块钱";
    }

    @Override
    public VoiceIntentResult parseIntent(String text) {
        log.info("开始解析记账意图, text={}", text);
        
        VoiceIntentResult result = new VoiceIntentResult();
        result.setOriginalText(text);
        
        try {
            // 解析金额
            Double amount = parseAmount(text);
            result.setAmount(amount);
            
            // 解析交易类型
            Integer type = parseType(text);
            result.setType(type);
            
            // 解析分类
            CategoryMatch match = parseCategory(text);
            result.setCategoryId(match.categoryId);
            result.setCategoryName(match.categoryName);
            result.setConfidence(match.confidence);
            
            // 设置成功
            result.setSuccess(true);
            
            log.info("记账意图解析成功: amount={}, type={}, category={}", amount, type, match.categoryName);
            
        } catch (Exception e) {
            log.error("记账意图解析失败", e);
            result.setSuccess(false);
            result.setErrorMessage(e.getMessage());
        }
        
        return result;
    }

    @Override
    public VoiceIntentResult processVoiceTransaction(byte[] audioData, String format) {
        log.info("开始处理语音记账, format={}", format);
        
        VoiceIntentResult result = new VoiceIntentResult();
        
        try {
            // 1. 语音转文字
            String text = speechToText(audioData, format);
            
            // 2. 解析意图
            result = parseIntent(text);
            
        } catch (Exception e) {
            log.error("语音记账处理失败", e);
            result.setSuccess(false);
            result.setErrorMessage("语音识别失败: " + e.getMessage());
        }
        
        return result;
    }

    /**
     * 解析金额
     */
    private Double parseAmount(String text) {
        Matcher matcher = AMOUNT_PATTERN.matcher(text);
        if (matcher.find()) {
            try {
                return Double.parseDouble(matcher.group(1));
            } catch (NumberFormatException e) {
                log.warn("金额解析失败: {}", text);
            }
        }
        return null;
    }

    /**
     * 解析交易类型
     */
    private Integer parseType(String text) {
        // 检查收入关键词
        for (String keyword : INCOME_KEYWORDS) {
            if (text.contains(keyword)) {
                return 1; // 收入
            }
        }
        
        // 检查支出关键词
        for (String keyword : EXPENSE_KEYWORDS) {
            if (text.contains(keyword)) {
                return 2; // 支出
            }
        }
        
        // 默认支出
        return 2;
    }

    /**
     * 解析分类
     */
    private CategoryMatch parseCategory(String text) {
        CategoryMatch match = new CategoryMatch();
        
        // 遍历所有分类关键词，找到最匹配的
        int maxScore = 0;
        Long bestCategoryId = null;
        String bestCategoryName = null;
        
        for (Map.Entry<String, Long> entry : CATEGORY_KEYWORDS.entrySet()) {
            String keyword = entry.getKey();
            if (text.contains(keyword)) {
                // 计算匹配分数（完全匹配优先）
                int score = keyword.length();
                if (score > maxScore) {
                    maxScore = score;
                    bestCategoryId = entry.getValue();
                    bestCategoryName = getCategoryNameById(entry.getValue());
                }
            }
        }
        
        if (bestCategoryId != null) {
            match.categoryId = bestCategoryId;
            match.categoryName = bestCategoryName;
            match.confidence = Math.min(1.0, maxScore / 10.0);
        } else {
            // 未识别到分类，返回默认分类
            match.categoryId = 1L;
            match.categoryName = "餐饮";
            match.confidence = 0.3;
        }
        
        return match;
    }

    /**
     * 根据分类ID获取分类名称
     */
    private String getCategoryNameById(Long categoryId) {
        Map<Long, String> categoryNames = new HashMap<>();
        categoryNames.put(1L, "餐饮");
        categoryNames.put(2L, "交通");
        categoryNames.put(3L, "购物");
        categoryNames.put(4L, "娱乐");
        categoryNames.put(5L, "医疗");
        categoryNames.put(6L, "教育");
        categoryNames.put(7L, "居住");
        categoryNames.put(8L, "通讯");
        return categoryNames.getOrDefault(categoryId, "其他");
    }

    /**
     * 分类匹配结果
     */
    private static class CategoryMatch {
        Long categoryId;
        String categoryName;
        Double confidence;
    }
}
