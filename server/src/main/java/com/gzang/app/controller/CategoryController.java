package com.gzang.app.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gzang.app.dto.category.CreateCategoryDTO;
import com.gzang.app.dto.category.UpdateCategoryDTO;
import com.gzang.app.entity.Category;
import com.gzang.app.exception.BusinessException;
import com.gzang.app.service.CategoryService;
import com.gzang.app.util.JwtUtil;
import com.gzang.app.vo.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

import static com.gzang.app.constant.ErrorCode.DATA_NOT_FOUND;

/**
 * 分类管理控制器 提供记账分类管理的相关接口
 *
 * @author G-Zang Team
 */
@RestController
@RequestMapping("/api/v1/categories")
@Tag(name = "分类管理", description = "记账分类管理相关接口")
public class CategoryController {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(CategoryController.class);

    private final CategoryService categoryService;
    private final JwtUtil jwtUtil;

    public CategoryController(CategoryService categoryService, JwtUtil jwtUtil) {
        this.categoryService = categoryService;
        this.jwtUtil = jwtUtil;
    }

    /**
     * 创建分类
     */
    @PostMapping
    @PreAuthorize("hasAuthority('CATEGORY_MANAGE')")
    @Operation(summary = "创建分类", description = "新增一个记账分类")
    public Result<Void> createCategory(@Validated @RequestBody CreateCategoryDTO dto, Principal principal) {
        log.info("创建分类请求: user={}, categoryName={}", principal.getName(), dto.getCategoryName());

        Long userId = jwtUtil.getUserIdFromToken(principal.getName());
        Long companyId = jwtUtil.getCompanyIdFromToken(principal.getName());

        Category category = new Category();
        category.setCategoryName(dto.getCategoryName());
        category.setType(dto.getType());
        category.setParentId(dto.getParentId());
        category.setUserId(userId);
        category.setCompanyId(companyId);

        boolean success = categoryService.createCategory(category);
        if (!success) {
            throw new BusinessException(400, "创建分类失败，可能分类名称已存在");
        }

        log.info("分类创建成功: id={}", category.getId());
        return Result.success();
    }

    /**
     * 更新分类
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('CATEGORY_MANAGE')")
    @Operation(summary = "更新分类", description = "更新指定分类信息")
    public Result<Void> updateCategory(
            @Parameter(description = "分类ID") @PathVariable Long id,
            @Validated @RequestBody UpdateCategoryDTO dto,
            Principal principal) {

        log.info("更新分类请求: id={}, user={}", id, principal.getName());

        Category category = new Category();
        category.setId(id);
        if (dto.getCategoryName() != null) {
            category.setCategoryName(dto.getCategoryName());
        }
        if (dto.getParentId() != null) {
            category.setParentId(dto.getParentId());
        }

        boolean success = categoryService.updateCategory(category);
        if (!success) {
            throw new BusinessException(400, "更新分类失败，可能分类名称已存在或无权限");
        }

        log.info("分类更新成功: id={}", id);
        return Result.success();
    }

    /**
     * 删除分类
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('CATEGORY_MANAGE')")
    @Operation(summary = "删除分类", description = "删除指定分类")
    public Result<Void> deleteCategory(
            @Parameter(description = "分类ID") @PathVariable Long id,
            Principal principal) {

        log.info("删除分类请求: id={}, user={}", id, principal.getName());

        // 从JWT中获取用户ID
        Long userId = jwtUtil.getUserIdFromToken(principal.getName());

        boolean success = categoryService.deleteCategory(id, userId);
        if (!success) {
            throw new BusinessException(400, "删除分类失败，可能分类不存在、无权限或还有子分类");
        }

        log.info("分类删除成功: id={}", id);
        return Result.success();
    }

    /**
     * 获取分类详情
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('CATEGORY_MANAGE')")
    @Operation(summary = "获取分类详情", description = "根据ID获取分类详情")
    public Result<Category> getCategoryById(@Parameter(description = "分类ID") @PathVariable Long id) {
        Category category = categoryService.getById(id);
        if (category == null) {
            throw new BusinessException(DATA_NOT_FOUND, "分类不存在");
        }
        return Result.success(category);
    }

    /**
     * 获取分类列表
     */
    @GetMapping
    @PreAuthorize("hasAuthority('CATEGORY_MANAGE')")
    @Operation(summary = "获取分类列表", description = "获取用户的分类列表")
    public Result<List<Category>> getCategoryList(
            @Parameter(description = "类型 (1:收入, 2:支出)") @RequestParam(required = false) Integer type,
            Principal principal) {

        // 从JWT中获取用户ID
        Long userId = jwtUtil.getUserIdFromToken(principal.getName());
        Long companyId = jwtUtil.getCompanyIdFromToken(principal.getName());

        List<Category> categories = categoryService.getCategoriesByUserId(userId, companyId, type);
        return Result.success(categories);
    }

    /**
     * 分页查询分类
     */
    @GetMapping("/page")
    @PreAuthorize("hasAuthority('CATEGORY_MANAGE')")
    @Operation(summary = "分页查询分类", description = "分页获取分类列表")
    public Result<IPage<Category>> getCategoryPage(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer current,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer size,
            @Parameter(description = "类型 (1:收入, 2:支出)") @RequestParam(required = false) Integer type,
            @Parameter(description = "父分类ID") @RequestParam(required = false) Long parentId,
            Principal principal) {

        // 从JWT中获取用户ID
        Long userId = jwtUtil.getUserIdFromToken(principal.getName());
        Long companyId = jwtUtil.getCompanyIdFromToken(principal.getName());

        Page<Category> page = new Page<>(current, size);
        IPage<Category> result = categoryService.getCategoryPage(page, userId, companyId, type, parentId);

        return Result.success(result);
    }

    /**
     * 获取子分类
     */
    @GetMapping("/{parentId}/children")
    @PreAuthorize("hasAuthority('CATEGORY_MANAGE')")
    @Operation(summary = "获取子分类", description = "获取指定父分类的子分类列表")
    public Result<List<Category>> getChildrenCategories(
            @Parameter(description = "父分类ID") @PathVariable Long parentId) {

        List<Category> children = categoryService.getChildrenByParentId(parentId);
        return Result.success(children);
    }

    /**
     * 获取系统预设分类
     */
    @GetMapping("/system")
    @Operation(summary = "获取系统预设分类", description = "获取系统预设的分类列表")
    public Result<List<Category>> getSystemCategories(
            @Parameter(description = "类型 (1:收入, 2:支出)") @RequestParam(required = false) Integer type) {

        List<Category> systemCategories = categoryService.getSystemCategories(type);
        return Result.success(systemCategories);
    }

    /**
     * 初始化用户分类
     */
    @PostMapping("/init")
    @PreAuthorize("hasAuthority('CATEGORY_MANAGE')")
    @Operation(summary = "初始化用户分类", description = "为用户初始化默认分类（复制系统预设分类）")
    public Result<Void> initUserCategories(Principal principal) {
        log.info("初始化用户分类请求: user={}", principal.getName());

        // 从JWT中获取用户ID
        Long userId = jwtUtil.getUserIdFromToken(principal.getName());

        boolean success = categoryService.initUserCategories(userId);
        if (!success) {
            throw new BusinessException(500, "初始化用户分类失败");
        }

        log.info("用户分类初始化成功: user={}", principal.getName());
        return Result.success();
    }
}
