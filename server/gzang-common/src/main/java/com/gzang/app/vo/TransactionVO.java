package com.gzang.app.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 交易记录数据传输对象，包含交易金额、类型、分类、账户等核心字段，支持企业多账本场景
 *
 * @author G-Zang Team
 */
@Data
public class TransactionVO {

    /**
     * 交易记录唯一标识
     */
    private Long id;

    /**
     * 交易金额，精确到分
     */
    private BigDecimal amount;

    /**
     * 交易类型(1:收入 2:支出)
     */
    private Integer type;

    /**
     * 交易分类唯一标识
     */
    private Long categoryId;

    /**
     * 交易分类名称
     */
    private String categoryName;

    /**
     * 交易分类图标
     */
    private String categoryIcon;

    /**
     * 交易分类颜色
     */
    private String categoryColor;

    /**
     * 账户唯一标识
     */
    private Long accountId;

    /**
     * 账户名称
     */
    private String accountName;

    /**
     * 交易发生时间，精确到秒
     */
    private LocalDateTime transactionTime;

    /**
     * 交易备注信息
     */
    private String remark;

    /**
     * 账本唯一标识
     */
    private Long bookId;

    /**
     * 用户唯一标识
     */
    private Long userId;

    /**
     * 企业唯一标识
     */
    private Long companyId;

    /**
     * 记录创建时间
     */
    private LocalDateTime createTime;

    /**
     * 记录更新时间
     */
    private LocalDateTime updateTime;
}
