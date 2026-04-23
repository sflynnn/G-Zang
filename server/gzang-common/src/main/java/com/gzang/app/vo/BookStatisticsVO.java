package com.gzang.app.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 账本统计VO
 */
@Data
public class BookStatisticsVO {

    private Long bookId;

    private BigDecimal totalIncome;

    private BigDecimal totalExpense;

    private BigDecimal balance;

    private Integer transactionCount;
}
