package com.gzang.app.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
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
    @TableId(value = "id", type = IdType.AUTO)
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
}
