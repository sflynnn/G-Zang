package com.gzang.mobile.service.impl;

import com.gzang.app.service.OcrService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * OCR识别服务实现
 * 支持票据、发票、收据等识别
 *
 * @author G-Zang Team
 */
@Service
public class OcrServiceImpl implements OcrService {

    private static final Logger log = LoggerFactory.getLogger(OcrServiceImpl.class);

    // 金额匹配模式
    private static final Pattern AMOUNT_PATTERN = Pattern.compile("(?:总价|合计|金额|实付)[:：]?\\s*￥?([0-9]+(?:\\.[0-9]{1,2})?)");
    private static final Pattern INVOICE_CODE_PATTERN = Pattern.compile("[A-Z0-9]{10,}");
    private static final Pattern DATE_PATTERN = Pattern.compile("(\\d{4}[年/-]\\d{1,2}[月/-]\\d{1,2}[日]?)");

    @Override
    public OcrResult recognizeText(byte[] imageData) {
        log.info("开始OCR文字识别, imageSize={}", imageData != null ? imageData.length : 0);

        OcrResult result = new OcrResult();

        try {
            // TODO: 集成真实的OCR服务（如阿里云、腾讯云OCR）
            // 目前返回模拟数据，实际项目中需要调用第三方API
            String mockText = "消费凭证\n商品名称\n测试商品\n合计金额: 100.00元\n日期: 2026-05-22";

            result.setSuccess(true);
            result.setRawText(mockText);
            result.setLines(mockText.split("\n"));
            result.setConfidence(0.85);

            log.info("OCR识别成功: lines={}", result.getLines().length);

        } catch (Exception e) {
            log.error("OCR识别失败", e);
            result.setSuccess(false);
        }

        return result;
    }

    @Override
    public InvoiceResult recognizeInvoice(byte[] imageData) {
        log.info("开始发票识别, imageSize={}", imageData != null ? imageData.length : 0);

        InvoiceResult result = new InvoiceResult();

        try {
            // 先进行通用文字识别
            OcrResult ocrResult = recognizeText(imageData);
            if (!ocrResult.isSuccess()) {
                result.setSuccess(false);
                return result;
            }

            // 解析发票信息
            String text = ocrResult.getRawText();
            result.setSuccess(true);

            // 解析发票代码
            Matcher codeMatcher = INVOICE_CODE_PATTERN.matcher(text);
            if (codeMatcher.find()) {
                result.setInvoiceCode(codeMatcher.group());
            }

            // 解析金额
            Matcher amountMatcher = AMOUNT_PATTERN.matcher(text);
            if (amountMatcher.find()) {
                result.setTotalAmount(Double.parseDouble(amountMatcher.group(1)));
            }

            // 解析日期
            Matcher dateMatcher = DATE_PATTERN.matcher(text);
            if (dateMatcher.find()) {
                result.setInvoiceDate(dateMatcher.group(1));
            }

            // 判断发票类型
            if (text.contains("增值税")) {
                result.setInvoiceType("增值税发票");
            } else if (text.contains("电子")) {
                result.setInvoiceType("电子发票");
            } else {
                result.setInvoiceType("普通发票");
            }

            log.info("发票识别成功: type={}, amount={}",
                result.getInvoiceType(), result.getTotalAmount());

        } catch (Exception e) {
            log.error("发票识别失败", e);
            result.setSuccess(false);
        }

        return result;
    }

    @Override
    public ReceiptResult recognizeReceipt(byte[] imageData) {
        log.info("开始收据识别, imageSize={}", imageData != null ? imageData.length : 0);

        ReceiptResult result = new ReceiptResult();

        try {
            // 先进行通用文字识别
            OcrResult ocrResult = recognizeText(imageData);
            if (!ocrResult.isSuccess()) {
                result.setSuccess(false);
                return result;
            }

            String text = ocrResult.getRawText();
            result.setSuccess(true);

            // 解析商户名称（通常在第一行或包含"商店"、"超市"等关键词）
            String[] lines = ocrResult.getLines();
            if (lines.length > 0) {
                result.setMerchantName(lines[0].trim());
            }

            // 解析金额
            Matcher amountMatcher = AMOUNT_PATTERN.matcher(text);
            if (amountMatcher.find()) {
                result.setAmount(Double.parseDouble(amountMatcher.group(1)));
            }

            // 解析日期
            Matcher dateMatcher = DATE_PATTERN.matcher(text);
            if (dateMatcher.find()) {
                result.setReceiptDate(dateMatcher.group(1));
            }

            // 判断交易类型（消费/退款）
            if (text.contains("退款") || text.contains("退货")) {
                result.setTransactionType("退款");
            } else {
                result.setTransactionType("消费");
            }

            // 生成参考号
            result.setReferenceNo(String.valueOf(System.currentTimeMillis()));

            log.info("收据识别成功: merchant={}, amount={}, type={}",
                result.getMerchantName(), result.getAmount(), result.getTransactionType());

        } catch (Exception e) {
            log.error("收据识别失败", e);
            result.setSuccess(false);
        }

        return result;
    }

    /**
     * 从文本中提取关键信息
     */
    public Map<String, String> extractKeyInfo(String text) {
        Map<String, String> info = new HashMap<>();

        // 提取金额
        Matcher amountMatcher = AMOUNT_PATTERN.matcher(text);
        if (amountMatcher.find()) {
            info.put("amount", amountMatcher.group(1));
        }

        // 提取日期
        Matcher dateMatcher = DATE_PATTERN.matcher(text);
        if (dateMatcher.find()) {
            info.put("date", dateMatcher.group(1));
        }

        return info;
    }
}
