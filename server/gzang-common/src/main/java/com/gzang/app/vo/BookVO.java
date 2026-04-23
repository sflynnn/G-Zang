package com.gzang.app.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 账本响应VO
 */
@Data
public class BookVO {

    private Long id;

    private String name;

    private String icon;

    private String color;

    private String currency;

    private String currencySymbol;

    private String type;

    private Boolean isDefault;

    private String remark;

    private Integer categoryCount;

    private Integer transactionCount;

    private BigDecimal totalIncome;

    private BigDecimal totalExpense;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
