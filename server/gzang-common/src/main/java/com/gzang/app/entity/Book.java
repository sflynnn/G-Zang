package com.gzang.app.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 账本实体类
 *
 * @author G-Zang Team
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_book")
public class Book extends BaseEntity {

    /**
     * 所属用户ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 所属公司ID（个人账本为NULL）
     */
    @TableField("company_id")
    private Long companyId;

    /**
     * 账本名称（如：日常开销、家庭账本）
     */
    @TableField("name")
    private String name;

    /**
     * 账本图标（emoji）
     */
    @TableField("icon")
    private String icon;

    /**
     * 账本颜色
     */
    @TableField("color")
    private String color;

    /**
     * 货币代码（如：CNY、USD）
     */
    @TableField("currency")
    private String currency;

    /**
     * 货币符号（如：¥、$）
     */
    @TableField("currency_symbol")
    private String currencySymbol;

    /**
     * 账本类型 (PERSONAL, FAMILY, TEAM, BUSINESS)
     */
    @TableField("type")
    private String type;

    /**
     * 是否默认账本
     */
    @TableField("is_default")
    private Boolean isDefault;

    /**
     * 备注
     */
    @TableField("remark")
    private String remark;
}
