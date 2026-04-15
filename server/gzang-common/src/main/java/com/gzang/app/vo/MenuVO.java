package com.gzang.app.vo;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 菜单视图对象
 *
 * @author G-Zang Team
 */
@Schema(description = "菜单视图")
public class MenuVO {

    @Schema(description = "菜单ID")
    private Long id;

    @Schema(description = "菜单名称")
    private String menuName;

    @Schema(description = "菜单编码")
    private String menuCode;

    @Schema(description = "菜单类型：1=目录, 2=菜单, 3=按钮, 4=外链")
    private Integer menuType;

    @Schema(description = "父菜单ID")
    private Long parentId;

    @Schema(description = "父菜单名称")
    private String parentName;

    @Schema(description = "菜单图标")
    private String icon;

    @Schema(description = "路由路径")
    private String path;

    @Schema(description = "前端组件路径")
    private String component;

    @Schema(description = "重定向路径")
    private String redirect;

    @Schema(description = "排序号")
    private Integer sortOrder;

    @Schema(description = "是否显示：0=隐藏, 1=显示")
    private Integer visible;

    @Schema(description = "是否缓存：0=否, 1=是")
    private Integer cache;

    @Schema(description = "是否固定：0=否, 1=是")
    private Integer affix;

    @Schema(description = "是否保活：0=否, 1=是")
    private Integer keepAlive;

    @Schema(description = "关联权限码")
    private String permissionCode;

    @Schema(description = "菜单级别：1=系统级, 2=企业级")
    private Integer menuLevel;

    @Schema(description = "企业ID")
    private Long companyId;

    @Schema(description = "菜单描述")
    private String description;

    @Schema(description = "是否为系统内置：0=否, 1=是")
    private Integer isSystem;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    @Schema(description = "关联的权限列表")
    private List<PermissionVO> permissions;

    @Schema(description = "子菜单列表")
    private List<MenuVO> children;

    @Schema(description = "关联的权限ID列表（仅用于数据传输）")
    private List<Long> permissionIds;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMenuName() {
        return menuName;
    }

    public void setMenuName(String menuName) {
        this.menuName = menuName;
    }

    public String getMenuCode() {
        return menuCode;
    }

    public void setMenuCode(String menuCode) {
        this.menuCode = menuCode;
    }

    public Integer getMenuType() {
        return menuType;
    }

    public void setMenuType(Integer menuType) {
        this.menuType = menuType;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public String getParentName() {
        return parentName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getComponent() {
        return component;
    }

    public void setComponent(String component) {
        this.component = component;
    }

    public String getRedirect() {
        return redirect;
    }

    public void setRedirect(String redirect) {
        this.redirect = redirect;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    public Integer getVisible() {
        return visible;
    }

    public void setVisible(Integer visible) {
        this.visible = visible;
    }

    public Integer getCache() {
        return cache;
    }

    public void setCache(Integer cache) {
        this.cache = cache;
    }

    public Integer getAffix() {
        return affix;
    }

    public void setAffix(Integer affix) {
        this.affix = affix;
    }

    public Integer getKeepAlive() {
        return keepAlive;
    }

    public void setKeepAlive(Integer keepAlive) {
        this.keepAlive = keepAlive;
    }

    public String getPermissionCode() {
        return permissionCode;
    }

    public void setPermissionCode(String permissionCode) {
        this.permissionCode = permissionCode;
    }

    public Integer getMenuLevel() {
        return menuLevel;
    }

    public void setMenuLevel(Integer menuLevel) {
        this.menuLevel = menuLevel;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getIsSystem() {
        return isSystem;
    }

    public void setIsSystem(Integer isSystem) {
        this.isSystem = isSystem;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }

    public List<PermissionVO> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<PermissionVO> permissions) {
        this.permissions = permissions;
    }

    public List<MenuVO> getChildren() {
        return children;
    }

    public void setChildren(List<MenuVO> children) {
        this.children = children;
    }

    public List<Long> getPermissionIds() {
        return permissionIds;
    }

    public void setPermissionIds(List<Long> permissionIds) {
        this.permissionIds = permissionIds;
    }
}
