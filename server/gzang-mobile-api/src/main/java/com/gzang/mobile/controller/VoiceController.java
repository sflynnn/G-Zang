package com.gzang.mobile.controller;

import com.gzang.app.util.TenantContextHolder;
import com.gzang.app.vo.Result;
import com.gzang.mobile.service.VoiceRecognitionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 语音记账控制器
 * 提供语音识别和智能记账功能
 *
 * @author G-Zang Team
 */
@RestController
@RequestMapping("/api/mobile/voice")
@Tag(name = "语音记账", description = "语音识别记账相关接口")
public class VoiceController {

    private static final Logger log = LoggerFactory.getLogger(VoiceController.class);

    private final VoiceRecognitionService voiceRecognitionService;

    public VoiceController(VoiceRecognitionService voiceRecognitionService) {
        this.voiceRecognitionService = voiceRecognitionService;
    }

    /**
     * 上传语音并获取记账意图
     * 
     * @param file 语音文件 (支持 wav, mp3, m4a, amr 格式)
     * @return 识别的记账意图
     */
    @PostMapping("/recognize")
    @Operation(summary = "语音识别记账", description = "上传语音文件，自动识别金额、分类等信息")
    public Result<VoiceRecognitionService.VoiceIntentResult> recognizeVoice(
            @Parameter(description = "语音文件") @RequestParam("file") MultipartFile file) {
        
        Long userId = TenantContextHolder.getUserId();
        log.info("语音识别请求: userId={}, fileName={}, fileSize={}", 
                userId, file.getOriginalFilename(), file.getSize());

        try {
            // 获取文件格式
            String filename = file.getOriginalFilename();
            String format = "wav";
            if (filename != null && filename.contains(".")) {
                format = filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
            }

            // 处理语音文件
            byte[] audioData = file.getBytes();
            VoiceRecognitionService.VoiceIntentResult result = 
                    voiceRecognitionService.processVoiceTransaction(audioData, format);

            if (result.getSuccess()) {
                log.info("语音识别成功: userId={}, amount={}, category={}", 
                        userId, result.getAmount(), result.getCategoryName());
                return Result.success(result);
            } else {
                log.warn("语音识别失败: userId={}, error={}", userId, result.getErrorMessage());
                return Result.error(500, result.getErrorMessage(), null);
            }

        } catch (Exception e) {
            log.error("语音识别异常: userId={}", userId, e);
            return Result.error(500, "语音识别失败: " + e.getMessage(), null);
        }
    }

    /**
     * 直接传入文字进行意图解析
     * 用于调试或手动输入场景
     * 
     * @param request 请求体
     * @return 识别的记账意图
     */
    @PostMapping("/parse-intent")
    @Operation(summary = "解析记账意图", description = "解析文字中的金额、分类等信息")
    public Result<VoiceRecognitionService.VoiceIntentResult> parseIntent(
            @Parameter(description = "待解析的文字") @RequestBody ParseIntentRequest request) {
        
        Long userId = TenantContextHolder.getUserId();
        log.info("意图解析请求: userId={}, text={}", userId, request.getText());

        try {
            VoiceRecognitionService.VoiceIntentResult result = 
                    voiceRecognitionService.parseIntent(request.getText());

            if (result.getSuccess()) {
                log.info("意图解析成功: userId={}, amount={}, category={}", 
                        userId, result.getAmount(), result.getCategoryName());
                return Result.success(result);
            } else {
                log.warn("意图解析失败: userId={}, error={}", userId, result.getErrorMessage());
                return Result.error(500, result.getErrorMessage(), null);
            }

        } catch (Exception e) {
            log.error("意图解析异常: userId={}", userId, e);
            return Result.error(500, "意图解析失败: " + e.getMessage(), null);
        }
    }

    /**
     * 请求体
     */
    public static class ParseIntentRequest {
        private String text;

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }
    }
}
