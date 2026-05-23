package com.gzang.app.service;

/**
 * OCR识别服务接口
 * 支持票据、发票、收据等识别
 *
 * @author G-Zang Team
 */
public interface OcrService {

    /**
     * 通用文字识别
     *
     * @param imageData 图片数据
     * @return 识别结果
     */
    OcrResult recognizeText(byte[] imageData);

    /**
     * 识别票据/发票
     *
     * @param imageData 图片数据
     * @return 票据识别结果
     */
    InvoiceResult recognizeInvoice(byte[] imageData);

    /**
     * 识别收据
     *
     * @param imageData 图片数据
     * @return 收据识别结果
     */
    ReceiptResult recognizeReceipt(byte[] imageData);

    /**
     * OCR识别结果
     */
    class OcrResult {
        private boolean success;
        private String rawText;
        private String[] lines;
        private double confidence;

        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        public String getRawText() { return rawText; }
        public void setRawText(String rawText) { this.rawText = rawText; }
        public String[] getLines() { return lines; }
        public void setLines(String[] lines) { this.lines = lines; }
        public double getConfidence() { return confidence; }
        public void setConfidence(double confidence) { this.confidence = confidence; }
    }

    /**
     * 发票识别结果
     */
    class InvoiceResult {
        private boolean success;
        private String invoiceType;      // 发票类型
        private String invoiceCode;      // 发票代码
        private String invoiceNumber;    // 发票号码
        private String invoiceDate;      // 开票日期
        private Double totalAmount;      // 总金额
        private Double taxAmount;        // 税额
        private String sellerName;       // 销售方名称
        private String buyerName;       // 购买方名称
        private String[] items;         // 商品明细

        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        public String getInvoiceType() { return invoiceType; }
        public void setInvoiceType(String invoiceType) { this.invoiceType = invoiceType; }
        public String getInvoiceCode() { return invoiceCode; }
        public void setInvoiceCode(String invoiceCode) { this.invoiceCode = invoiceCode; }
        public String getInvoiceNumber() { return invoiceNumber; }
        public void setInvoiceNumber(String invoiceNumber) { this.invoiceNumber = invoiceNumber; }
        public String getInvoiceDate() { return invoiceDate; }
        public void setInvoiceDate(String invoiceDate) { this.invoiceDate = invoiceDate; }
        public Double getTotalAmount() { return totalAmount; }
        public void setTotalAmount(Double totalAmount) { this.totalAmount = totalAmount; }
        public Double getTaxAmount() { return taxAmount; }
        public void setTaxAmount(Double taxAmount) { this.taxAmount = taxAmount; }
        public String getSellerName() { return sellerName; }
        public void setSellerName(String sellerName) { this.sellerName = sellerName; }
        public String getBuyerName() { return buyerName; }
        public void setBuyerName(String buyerName) { this.buyerName = buyerName; }
        public String[] getItems() { return items; }
        public void setItems(String[] items) { this.items = items; }
    }

    /**
     * 收据识别结果
     */
    class ReceiptResult {
        private boolean success;
        private String merchantName;     // 商户名称
        private String receiptDate;      // 交易日期
        private Double amount;           // 交易金额
        private String transactionType;  // 交易类型
        private String referenceNo;      // 参考号

        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        public String getMerchantName() { return merchantName; }
        public void setMerchantName(String merchantName) { this.merchantName = merchantName; }
        public String getReceiptDate() { return receiptDate; }
        public void setReceiptDate(String receiptDate) { this.receiptDate = receiptDate; }
        public Double getAmount() { return amount; }
        public void setAmount(Double amount) { this.amount = amount; }
        public String getTransactionType() { return transactionType; }
        public void setTransactionType(String transactionType) { this.transactionType = transactionType; }
        public String getReferenceNo() { return referenceNo; }
        public void setReferenceNo(String referenceNo) { this.referenceNo = referenceNo; }
    }
}
