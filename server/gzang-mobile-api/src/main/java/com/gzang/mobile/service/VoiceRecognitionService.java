package com.gzang.mobile.service;

/**
 * 语音识别服务接口
 * 负责将语音转换为文字，并识别记账意图
 *
 * @author G-Zang Team
 */
public interface VoiceRecognitionService {

    /**
     * 语音转文字
     *
     * @param audioData 音频数据
     * @param format 音频格式 (wav, mp3, m4a等)
     * @return 识别后的文字
     */
    String speechToText(byte[] audioData, String format);

    /**
     * 解析记账意图
     * 从文字中提取金额、分类、时间等信息
     *
     * @param text 识别后的文字
     * @return 记账意图结果
     */
    VoiceIntentResult parseIntent(String text);

    /**
     * 完整的语音记账处理
     * 包含语音转文字和意图解析
     *
     * @param audioData 音频数据
     * @param format 音频格式
     * @return 记账意图结果
     */
    VoiceIntentResult processVoiceTransaction(byte[] audioData, String format);

    /**
     * 记账意图结果
     */
    class VoiceIntentResult {
        /** 识别的原始文字 */
        private String originalText;
        
        /** 识别的金额 */
        private Double amount;
        
        /** 识别的时间 */
        private String date;
        
        /** 识别的分类ID */
        private Long categoryId;
        
        /** 识别的分类名称 */
        private String categoryName;
        
        /** 交易类型：1-收入，2-支出 */
        private Integer type;
        
        /** 备注信息 */
        private String remark;
        
        /** 置信度 */
        private Double confidence;
        
        /** 是否识别成功 */
        private Boolean success;
        
        /** 错误信息 */
        private String errorMessage;

        // Getters and Setters
        public String getOriginalText() { return originalText; }
        public void setOriginalText(String originalText) { this.originalText = originalText; }
        
        public Double getAmount() { return amount; }
        public void setAmount(Double amount) { this.amount = amount; }
        
        public String getDate() { return date; }
        public void setDate(String date) { this.date = date; }
        
        public Long getCategoryId() { return categoryId; }
        public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }
        
        public String getCategoryName() { return categoryName; }
        public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
        
        public Integer getType() { return type; }
        public void setType(Integer type) { this.type = type; }
        
        public String getRemark() { return remark; }
        public void setRemark(String remark) { this.remark = remark; }
        
        public Double getConfidence() { return confidence; }
        public void setConfidence(Double confidence) { this.confidence = confidence; }
        
        public Boolean getSuccess() { return success; }
        public void setSuccess(Boolean success) { this.success = success; }
        
        public String getErrorMessage() { return errorMessage; }
        public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
    }
}
