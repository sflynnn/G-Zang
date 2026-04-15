package com.gzang.app.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 菜单实体类
 *
 * @author G-Zang Team
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_menu")
public class Menu extends BaseEntity {

    /**
     * 菜单名称
     */
    @TableField("menu_name")
    private String menuName;

    /**
     * 菜单编码（唯一标识）
     */
    @TableField("menu_code")
    private String menuCode;

    /**
     * 菜单类型：1=目录, 2=菜单, 3=按钮, 4=外链
     */
    @TableField("menu_type")
    private Integer menuType = 2;

    /**
     * 父菜单ID（0为一级菜单）
     */
    @TableField("parent_id")
    private Long parentId = 0L;

    /**
     * 菜单图标
     */
    @TableField("icon")
    private String icon;

    /**
     * 路由路径
     */
    @TableField("path")
    private String path;

    /**
     * 前端组件路径
     */
    @TableField("component")
    private String component;

    /**
     * 重定向路径
     */
    @TableField("redirect")
    private String redirect;

    /**
     * 排序号
     */
    @TableField("sort_order")
    private Integer sortOrder = 0;

    /**
     * 是否显示：0=隐藏, 1=显示
     */
    @TableField("visible")
    private Integer visible = 1;

    /**
     * 是否缓存：0=否, 1=是
     */
    @TableField("cache")
    private Integer cache = 0;

    /**
     * 是否固定：0=否, 1=是
     */
    @TableField("affix")
    private Integer affix = 0;

    /**
     * 是否保活：0=否, 1=是
     */
    @TableField("keep_alive")
    private Integer keepAlive = 0;

    /**
     * 关联权限码（按钮类型时使用）
     */
    @TableField("permission_code")
    private String permissionCode;

    /**
     * 菜单级别：1=系统级, 2=企业级
     */
    @TableField("menu_level")
    private Integer menuLevel = 1;

    /**
     * 企业ID（系统级菜单为NULL）
     */
    @TableField("company_id")
    private Long companyId;

    /**
     * 菜单描述
     */
    @TableField("description")
    private String description;

    /**
     * 是否为系统内置：0=否, 1=是
     */
    @TableField("is_system")
    private Integer isSystem = 0;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField("update_time")
    private LocalDateTime updateTime;
}
