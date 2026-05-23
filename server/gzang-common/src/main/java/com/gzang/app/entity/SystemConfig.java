package com.gzang.app.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 系统配置实体类
 *
 * @author G-Zang Team
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_system_config")
public class SystemConfig extends BaseEntity {

    /**
     * 配置分组
     */
    @TableField("config_group")
    private String configGroup;

    /**
     * 配置键
     */
    @TableField("config_key")
    private String configKey;

    /**
     * 配置值
     */
    @TableField("config_value")
    private String configValue;

    /**
     * 配置类型 (string, number, boolean, json)
     */
    @TableField("config_type")
    private String configType;

    /**
     * 配置名称
     */
    @TableField("config_name")
    private String configName;

    /**
     * 配置描述
     */
    @TableField("description")
    private String description;

    /**
     * 是否可编辑
     */
    @TableField("editable")
    private Boolean editable;

    /**
     * 排序号
     */
    @TableField("sort_order")
    private Integer sortOrder;
}
