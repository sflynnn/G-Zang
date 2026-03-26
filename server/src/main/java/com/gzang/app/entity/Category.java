package com.gzang.app.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 记账分类实体类
 *
 * @author G-Zang Team
 */
@Data
@TableName("t_category")
public class Category {

    /**
     * 主键ID
     */
    @TableField("id")
    private Long id;

    /**
     * 所属用户ID（公司分类为NULL）
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 所属公司ID（个人分类为NULL）
     */
    @TableField("company_id")
    private Long companyId;

    /**
     * 分类名称（如：餐饮、交通、配件采购）
     */
    @TableField("category_name")
    private String categoryName;

    /**
     * 父分类ID（0为一级分类）
     */
    @TableField("parent_id")
    private Long parentId;

    /**
     * 类型 (1:收入, 2:支出)
     */
    @TableField("type")
    private Integer type;

    /**
     * 是否系统预设 (0:否, 1:是)
     */
    @TableField("is_system")
    private Integer isSystem;

    // Explicit getter and setter methods to ensure Lombok generates them properly
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getIsSystem() {
        return isSystem;
    }

    public void setIsSystem(Integer isSystem) {
        this.isSystem = isSystem;
    }
}


