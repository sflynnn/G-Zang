package com.gzang.admin.controller;

import com.gzang.app.entity.Menu;
import com.gzang.app.entity.User;
import com.gzang.app.exception.BusinessException;
import com.gzang.admin.service.MenuService;
import com.gzang.admin.service.UserService;
import com.gzang.app.util.JwtUtil;
import com.gzang.app.dto.menu.CreateMenuDTO;
import com.gzang.app.dto.menu.UpdateMenuDTO;
import com.gzang.app.vo.MenuVO;
import com.gzang.app.vo.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

import static com.gzang.app.constant.ErrorCode.DATA_NOT_FOUND;

/**
 * 管理端菜单管理控制器
 *
 * @author G-Zang Team
 */
@RestController
@RequestMapping("/api/admin/menus")
@Tag(name = "管理端菜单管理", description = "菜单管理相关接口")
public class AdminMenuController {

    private static final Logger log = LoggerFactory.getLogger(AdminMenuController.class);

    private final MenuService menuService;
    private final UserService userService;
    private final JwtUtil jwtUtil;

    public AdminMenuController(MenuService menuService, UserService userService, JwtUtil jwtUtil) {
        this.menuService = menuService;
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    /**
     * 获取菜单列表（树形）
     */
    @GetMapping
    @PreAuthorize("hasAuthority('MENU_MANAGE')")
    @Operation(summary = "获取菜单列表", description = "获取完整菜单树形结构")
    public Result<List<MenuVO>> getMenuList() {
        log.info("=== AdminMenuController.getMenuList called ===");
        try {
            List<MenuVO> menuTree = menuService.listMenuTree();
            log.info("listMenuTree returned, menus={}", menuTree.size());
            return Result.success(menuTree);
        } catch (Exception e) {
            log.error("Error in getMenuList", e);
            throw e;
        }
    }

    /**
     * 获取菜单树
     */
    @GetMapping("/tree")
    @PreAuthorize("hasAuthority('MENU_MANAGE')")
    @Operation(summary = "获取菜单树", description = "获取完整菜单树形结构")
    public Result<List<MenuVO>> getMenuTree() {
        List<MenuVO> menuTree = menuService.listMenuTree();
        return Result.success(menuTree);
    }

    /**
     * 获取菜单详情
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('MENU_MANAGE')")
    @Operation(summary = "获取菜单详情", description = "获取菜单详情")
    public Result<MenuVO> getMenuById(
            @Parameter(description = "菜单ID") @PathVariable Long id) {
        MenuVO menuVO = menuService.getMenuDetail(id);
        if (menuVO == null) {
            throw new BusinessException(DATA_NOT_FOUND, "菜单不存在");
        }
        return Result.success(menuVO);
    }

    /**
     * 创建菜单
     */
    @PostMapping
    @PreAuthorize("hasAuthority('MENU_MANAGE')")
    @Operation(summary = "创建菜单", description = "创建新菜单")
    public Result<Void> createMenu(
            @RequestBody CreateMenuDTO dto,
            Principal principal) {
        Long userId = jwtUtil.getUserIdFromToken(principal.getName());
        User user = userService.getById(userId);
        Long companyId = user != null ? user.getCompanyId() : null;
        Menu menu = menuService.createMenu(dto.getMenuName(), dto.getMenuCode(), dto.getPath(), dto.getMenuType(), dto.getParentId(), companyId);
        if (menu == null) {
            throw new BusinessException(400, "创建菜单失败");
        }
        return Result.success();
    }

    /**
     * 更新菜单
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('MENU_MANAGE')")
    @Operation(summary = "更新菜单", description = "更新菜单信息")
    public Result<Void> updateMenu(
            @Parameter(description = "菜单ID") @PathVariable Long id,
            @RequestBody UpdateMenuDTO dto) {
        menuService.updateMenu(id, dto.getMenuName(), dto.getMenuCode(), dto.getPath(), dto.getMenuType(), dto.getParentId());
        return Result.success();
    }

    /**
     * 删除菜单
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('MENU_MANAGE')")
    @Operation(summary = "删除菜单", description = "删除菜单")
    public Result<Void> deleteMenu(
            @Parameter(description = "菜单ID") @PathVariable Long id) {
        menuService.deleteMenu(id);
        return Result.success();
    }

    /**
     * 获取用户菜单树
     */
    @GetMapping("/user/tree")
    @Operation(summary = "获取用户菜单", description = "获取当前用户的菜单树")
    public Result<List<MenuVO>> getUserMenuTree(Principal principal) {
        Long userId = jwtUtil.getUserIdFromToken(principal.getName());
        User user = userService.getById(userId);
        Long companyId = user != null ? user.getCompanyId() : null;
        List<MenuVO> menuTree = menuService.getUserMenus(userId, companyId);
        return Result.success(menuTree);
    }
}