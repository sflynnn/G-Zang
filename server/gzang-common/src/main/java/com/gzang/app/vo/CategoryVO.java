package com.gzang.app.vo;

import lombok.Data;

/**
 * 分类数据传输对象，用于在企业多账本场景下传递分类信息
 * 支持二级分类体系，可关联交易类型和图标样式
 *
 * @author G-Zang Team
 */
@Data
public class CategoryVO {

    /**
     * 分类唯一标识
     */
    private Long id;

    /**
     * 分类名称
     */
    private String categoryName;

    /**
     * 父级分类ID，顶级分类为null，支持二级分类
     */
    private Long parentId;

    /**
     * 分类类型(1:收入 2:支出)
     */
    private Integer type;

    /**
     * 是否系统预设分类(0:否 1:是)，系统预设不可删除
     */
    private Integer isSystem;

    /**
     * 分类图标名称，关联AppleIcon图标库，如:sf-cart, sf-house
     */
    private String icon;

    /**
     * 分类颜色，用于UI展示，采用十六进制颜色码如:#FF6B6B
     */
    private String color;

    /**
     * 排序权重，数字越小排序越靠前
     */
    private Integer sortOrder;
}
