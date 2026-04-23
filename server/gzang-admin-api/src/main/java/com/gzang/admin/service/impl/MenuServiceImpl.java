package com.gzang.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gzang.app.constant.ErrorCode;
import com.gzang.app.dto.menu.CreateMenuDTO;
import com.gzang.app.dto.menu.UpdateMenuDTO;
import com.gzang.app.entity.Menu;
import com.gzang.app.entity.MenuPermission;
import com.gzang.app.exception.BusinessException;
import com.gzang.app.mapper.MenuMapper;
import com.gzang.app.mapper.MenuPermissionMapper;
import com.gzang.admin.service.MenuService;
import com.gzang.app.vo.MenuVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 菜单服务实现类
 *
 * @author G-Zang Team
 */
@Service
public class MenuServiceImpl extends ServiceImpl<MenuMapper, Menu> implements MenuService {

    private static final Logger log = LoggerFactory.getLogger(MenuServiceImpl.class);

    private final MenuPermissionMapper menuPermissionMapper;

    public MenuServiceImpl(MenuPermissionMapper menuPermissionMapper) {
        this.menuPermissionMapper = menuPermissionMapper;
    }

    @Override
    public List<MenuVO> listMenuTree() {
        log.info("MenuServiceImpl.listMenuTree called");
        try {
            LambdaQueryWrapper<Menu> wrapper = new LambdaQueryWrapper<>();
            wrapper.orderByAsc(Menu::getSortOrder).orderByAsc(Menu::getCreateTime);
            List<Menu> menus = list(wrapper);
            log.info("list returned, menus count={}", menus.size());
            List<MenuVO> result = buildMenuTree(menus);
            log.info("buildMenuTree returned, tree size={}", result.size());
            return result;
        } catch (Exception e) {
            log.error("Error in listMenuTree", e);
            throw e;
        }
    }

    @Override
    public MenuVO getMenuDetail(Long menuId) {
        Menu menu = getById(menuId);
        if (menu == null) {
            throw new BusinessException(ErrorCode.MENU_NOT_FOUND, "菜单不存在");
        }
        MenuVO vo = convertToVO(menu);
        vo.setPermissionIds(getMenuPermissionIds(menuId));
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Menu createMenu(String menuName, String menuKey, String path, Integer menuType, Long parentId, Long companyId) {
        // 检查菜单编码是否已存在
        LambdaQueryWrapper<Menu> codeWrapper = new LambdaQueryWrapper<>();
        codeWrapper.eq(Menu::getMenuCode, menuKey);
        if (count(codeWrapper) > 0) {
            throw new BusinessException(ErrorCode.MENU_CODE_EXISTS, "菜单编码已存在");
        }

        // 检查父菜单是否存在
        if (parentId != null && parentId > 0) {
            Menu parent = getById(parentId);
            if (parent == null) {
                throw new BusinessException(ErrorCode.MENU_NOT_FOUND, "父菜单不存在");
            }
        }

        Menu menu = new Menu();
        menu.setMenuName(menuName);
        menu.setMenuCode(menuKey);
        menu.setPath(path);
        menu.setMenuType(menuType);
        menu.setParentId(parentId);

        save(menu);

        return menu;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Menu createMenu(CreateMenuDTO dto, Long companyId) {
        // 检查菜单编码是否已存在
        LambdaQueryWrapper<Menu> codeWrapper = new LambdaQueryWrapper<>();
        codeWrapper.eq(Menu::getMenuCode, dto.getMenuCode());
        if (count(codeWrapper) > 0) {
            throw new BusinessException(ErrorCode.MENU_CODE_EXISTS, "菜单编码已存在");
        }

        // 检查父菜单是否存在
        if (dto.getParentId() != null && dto.getParentId() > 0) {
            Menu parent = getById(dto.getParentId());
            if (parent == null) {
                throw new BusinessException(ErrorCode.MENU_NOT_FOUND, "父菜单不存在");
            }
        }

        Menu menu = new Menu();
        BeanUtils.copyProperties(dto, menu);

        // 设置企业ID（系统级菜单不需要设置）
        if (dto.getMenuLevel() != null && dto.getMenuLevel() == 2) {
            menu.setCompanyId(companyId);
        }

        save(menu);

        // 分配权限
        if (dto.getPermissionIds() != null && !dto.getPermissionIds().isEmpty()) {
            menuPermissionMapper.deletePermissionsByMenuId(menu.getId());
            menuPermissionMapper.batchInsertPermissions(menu.getId(), dto.getPermissionIds().toArray(new Long[0]));
        }

        return menu;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateMenu(Long menuId, String menuName, String menuKey, String path, Integer menuType, Long parentId) {
        Menu menu = getById(menuId);
        if (menu == null) {
            throw new BusinessException(ErrorCode.MENU_NOT_FOUND, "菜单不存在");
        }

        // 系统内置菜单不允许修改编码
        if (menuName != null) {
            menu.setMenuName(menuName);
        }
        if (menuType != null) {
            menu.setMenuType(menuType);
        }
        if (parentId != null) {
            // 检查是否将自己设置为父菜单
            if (parentId.equals(menuId)) {
                throw new BusinessException(ErrorCode.INVALID_MENU_PARENT, "不能将自己设置为父菜单");
            }
            menu.setParentId(parentId);
        }
        if (path != null) {
            menu.setPath(path);
        }

        updateById(menu);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateMenu(Long menuId, UpdateMenuDTO dto) {
        Menu menu = getById(menuId);
        if (menu == null) {
            throw new BusinessException(ErrorCode.MENU_NOT_FOUND, "菜单不存在");
        }

        // 系统内置菜单不允许修改编码
        if (dto.getMenuName() != null) {
            menu.setMenuName(dto.getMenuName());
        }
        if (dto.getMenuType() != null) {
            menu.setMenuType(dto.getMenuType());
        }
        if (dto.getParentId() != null) {
            // 检查是否将自己设置为父菜单
            if (dto.getParentId().equals(menuId)) {
                throw new BusinessException(ErrorCode.INVALID_MENU_PARENT, "不能将自己设置为父菜单");
            }
            menu.setParentId(dto.getParentId());
        }
        if (dto.getIcon() != null) {
            menu.setIcon(dto.getIcon());
        }
        if (dto.getPath() != null) {
            menu.setPath(dto.getPath());
        }
        if (dto.getComponent() != null) {
            menu.setComponent(dto.getComponent());
        }
        if (dto.getRedirect() != null) {
            menu.setRedirect(dto.getRedirect());
        }
        if (dto.getSortOrder() != null) {
            menu.setSortOrder(dto.getSortOrder());
        }
        if (dto.getVisible() != null) {
            menu.setVisible(dto.getVisible());
        }
        if (dto.getCache() != null) {
            menu.setCache(dto.getCache());
        }
        if (dto.getAffix() != null) {
            menu.setAffix(dto.getAffix());
        }
        if (dto.getKeepAlive() != null) {
            menu.setKeepAlive(dto.getKeepAlive());
        }
        if (dto.getPermissionCode() != null) {
            menu.setPermissionCode(dto.getPermissionCode());
        }
        if (dto.getMenuLevel() != null) {
            menu.setMenuLevel(dto.getMenuLevel());
        }
        if (dto.getDescription() != null) {
            menu.setDescription(dto.getDescription());
        }

        updateById(menu);

        // 更新权限
        if (dto.getPermissionIds() != null) {
            menuPermissionMapper.deletePermissionsByMenuId(menuId);
            if (!dto.getPermissionIds().isEmpty()) {
                menuPermissionMapper.batchInsertPermissions(menuId, dto.getPermissionIds().toArray(new Long[0]));
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteMenu(Long menuId) {
        Menu menu = getById(menuId);
        if (menu == null) {
            throw new BusinessException(ErrorCode.MENU_NOT_FOUND, "菜单不存在");
        }

        // 检查是否有子菜单
        LambdaQueryWrapper<Menu> childWrapper = new LambdaQueryWrapper<>();
        childWrapper.eq(Menu::getParentId, menuId);
        if (count(childWrapper) > 0) {
            throw new BusinessException(ErrorCode.MENU_HAS_CHILDREN, "存在子菜单，不允许删除");
        }

        // 删除菜单权限关联
        menuPermissionMapper.deletePermissionsByMenuId(menuId);
        // 删除菜单
        removeById(menuId);
    }

    @Override
    public List<MenuVO> getMenuTreeOptions() {
        return listMenuTree();
    }

    @Override
    public List<MenuVO> getRoleMenus(Long roleId) {
        // 注意：t_menu_permission 表没有 role_id 字段，此功能暂不支持
        // 返回空列表，后续可以通过角色权限关联间接实现
        return new ArrayList<>();
    }

    @Override
    public List<MenuVO> getUserMenus(Long userId, Long companyId) {
        // 获取所有系统级菜单和企业级菜单（本公司）
        LambdaQueryWrapper<Menu> wrapper = new LambdaQueryWrapper<>();
        wrapper.and(w -> w
                .eq(Menu::getMenuLevel, 1)
                .or()
                .and(w1 -> w1.eq(Menu::getMenuLevel, 2).eq(Menu::getCompanyId, companyId))
        );
        wrapper.eq(Menu::getVisible, 1); // 只返回显示的菜单
        wrapper.orderByAsc(Menu::getSortOrder);

        List<Menu> menus = list(wrapper);
        return buildMenuTree(menus);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateMenuPermissions(Long menuId, List<Long> permissionIds) {
        Menu menu = getById(menuId);
        if (menu == null) {
            throw new BusinessException(ErrorCode.MENU_NOT_FOUND, "菜单不存在");
        }

        menuPermissionMapper.deletePermissionsByMenuId(menuId);
        if (permissionIds != null && !permissionIds.isEmpty()) {
            menuPermissionMapper.batchInsertPermissions(menuId, permissionIds.toArray(new Long[0]));
        }
    }

    @Override
    public List<Long> getMenuPermissionIds(Long menuId) {
        LambdaQueryWrapper<MenuPermission> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MenuPermission::getMenuId, menuId);
        List<MenuPermission> menuPermissions = menuPermissionMapper.selectList(wrapper);
        return menuPermissions.stream()
                .map(MenuPermission::getPermissionId)
                .collect(Collectors.toList());
    }

    @Override
    public Menu findMenuById(Long id) {
        return getById(id);
    }

    @Override
    public List<Long> getMenuAndChildrenIds(Long menuId) {
        List<Long> result = new ArrayList<>();
        result.add(menuId);
        collectChildrenIds(menuId, result);
        return result;
    }

    /**
     * 递归收集子菜单ID
     */
    private void collectChildrenIds(Long parentId, List<Long> result) {
        LambdaQueryWrapper<Menu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Menu::getParentId, parentId);
        List<Menu> children = list(wrapper);

        for (Menu child : children) {
            result.add(child.getId());
            collectChildrenIds(child.getId(), result);
        }
    }

    /**
     * 将菜单列表构建为树形结构
     */
    private List<MenuVO> buildMenuTree(List<Menu> menus) {
        // 获取所有菜单ID
        List<Long> menuIds = menus.stream().map(Menu::getId).collect(Collectors.toList());

        // 获取所有菜单关联的权限
        Map<Long, List<Long>> menuPermissionMap = new HashMap<>();
        if (!menuIds.isEmpty()) {
            LambdaQueryWrapper<MenuPermission> permWrapper = new LambdaQueryWrapper<>();
            permWrapper.in(MenuPermission::getMenuId, menuIds);
            List<MenuPermission> menuPermissions = menuPermissionMapper.selectList(permWrapper);

            menuPermissionMap = menuPermissions.stream()
                    .collect(Collectors.groupingBy(
                            MenuPermission::getMenuId,
                            Collectors.mapping(MenuPermission::getPermissionId, Collectors.toList())
                    ));
        }

        // 构建菜单Map
        Map<Long, MenuVO> menuMap = new LinkedHashMap<>();
        Map<Long, String> idToNameMap = new HashMap<>();

        for (Menu menu : menus) {
            MenuVO vo = convertToVO(menu);
            vo.setPermissionIds(menuPermissionMap.getOrDefault(menu.getId(), new ArrayList<>()));
            menuMap.put(menu.getId(), vo);
            idToNameMap.put(menu.getId(), menu.getMenuName());
        }

        // 设置父菜单名称
        for (MenuVO vo : menuMap.values()) {
            if (vo.getParentId() != null && vo.getParentId() > 0) {
                vo.setParentName(idToNameMap.get(vo.getParentId()));
            }
        }

        // 构建树形结构
        List<MenuVO> rootMenus = new ArrayList<>();
        for (MenuVO vo : menuMap.values()) {
            if (vo.getParentId() == null || vo.getParentId() == 0) {
                rootMenus.add(vo);
            } else {
                MenuVO parent = menuMap.get(vo.getParentId());
                if (parent != null) {
                    if (parent.getChildren() == null) {
                        parent.setChildren(new ArrayList<>());
                    }
                    parent.getChildren().add(vo);
                } else {
                    // 父菜单不存在，作为根节点
                    rootMenus.add(vo);
                }
            }
        }

        return rootMenus;
    }

    /**
     * 将Menu实体转换为MenuVO
     */
    private MenuVO convertToVO(Menu menu) {
        MenuVO vo = new MenuVO();
        BeanUtils.copyProperties(menu, vo);
        return vo;
    }
}
