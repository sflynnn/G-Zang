package com.gzang.app.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 分类图标颜色配置实体类
 *
 * @author G-Zang Team
 */
@Data
@TableName("t_category_icon")
public class CategoryIcon {

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 分类ID
     */
    @TableField("category_id")
    private Long categoryId;

    /**
     * 图标（emoji 或图标名称）
     */
    @TableField("icon")
    private String icon;

    /**
     * 主题色十六进制
     */
    @TableField("color")
    private String color;

    /**
     * 排序
     */
    @TableField("sort_order")
    private Integer sortOrder;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private Date createTime;
}
