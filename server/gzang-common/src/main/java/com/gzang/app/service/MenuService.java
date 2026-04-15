package com.gzang.app.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gzang.app.dto.menu.CreateMenuDTO;
import com.gzang.app.dto.menu.UpdateMenuDTO;
import com.gzang.app.entity.Menu;
import com.gzang.app.vo.MenuVO;

import java.util.List;

/**
 * 菜单服务接口
 *
 * @author G-Zang Team
 */
public interface MenuService extends IService<Menu> {

    /**
     * 获取所有菜单列表（树形）
     */
    List<MenuVO> listMenuTree();

    /**
     * 获取菜单详情（含权限）
     */
    MenuVO getMenuDetail(Long menuId);

    /**
     * 创建菜单（通过DTO）
     *
     * @param dto        菜单创建DTO
     * @param companyId 企业ID
     * @return 创建的菜单
     */
    Menu createMenu(CreateMenuDTO dto, Long companyId);

    /**
     * 创建菜单（通过参数）
     *
     * @param menuName  菜单名称
     * @param menuKey   菜单编码
     * @param path      路由路径
     * @param menuType  菜单类型
     * @param parentId  父菜单ID
     * @param companyId 企业ID
     * @return 创建的菜单
     */
    Menu createMenu(String menuName, String menuKey, String path, Integer menuType, Long parentId, Long companyId);

    /**
     * 更新菜单（通过DTO，含权限变更）
     *
     * @param menuId 菜单ID
     * @param dto    菜单更新DTO
     */
    void updateMenu(Long menuId, UpdateMenuDTO dto);

    /**
     * 更新菜单（通过参数）
     *
     * @param menuId   菜单ID
     * @param menuName 菜单名称
     * @param menuKey  菜单编码
     * @param path     路由路径
     * @param menuType 菜单类型
     * @param parentId 父菜单ID
     */
    void updateMenu(Long menuId, String menuName, String menuKey, String path, Integer menuType, Long parentId);

    /**
     * 删除菜单
     */
    void deleteMenu(Long menuId);

    /**
     * 获取菜单树（用于选择器）
     */
    List<MenuVO> getMenuTreeOptions();

    /**
     * 获取角色的菜单权限列表
     */
    List<MenuVO> getRoleMenus(Long roleId);

    /**
     * 获取用户菜单树（根据用户角色和权限过滤）
     */
    List<MenuVO> getUserMenus(Long userId, Long companyId);

    /**
     * 更新菜单权限
     */
    void updateMenuPermissions(Long menuId, List<Long> permissionIds);

    /**
     * 获取菜单关联的权限ID列表
     */
    List<Long> getMenuPermissionIds(Long menuId);

    /**
     * 根据ID查询菜单（使用显式SQL）
     */
    Menu findMenuById(Long id);

    /**
     * 获取菜单下的所有菜单ID（包括子菜单）
     */
    List<Long> getMenuAndChildrenIds(Long menuId);
}
