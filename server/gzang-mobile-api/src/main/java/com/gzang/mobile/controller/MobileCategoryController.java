package com.gzang.mobile.controller;

import com.gzang.app.util.TenantContextHolder;
import com.gzang.app.dto.category.CreateCategoryDTO;
import com.gzang.app.entity.Category;
import com.gzang.app.exception.BusinessException;
import com.gzang.mobile.service.CategoryService;
import com.gzang.app.vo.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 移动端分类控制器
 * 移动端 + PC个人记账 共用的分类管理接口
 *
 * @author G-Zang Team
 */
@RestController
@RequestMapping("/api/mobile/categories")
@Tag(name = "移动端分类管理", description = "记账分类管理相关接口")
public class MobileCategoryController {

    private static final Logger log = LoggerFactory.getLogger(MobileCategoryController.class);

    private final CategoryService categoryService;

    public MobileCategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    /**
     * 获取分类列表
     */
    @GetMapping
    @Operation(summary = "获取分类列表", description = "获取用户的分类列表")
    public Result<List<Category>> getCategoryList(
            @Parameter(description = "类型 (1:收入, 2:支出)") @RequestParam(name = "type", required = false) Integer type) {

        Long userId = TenantContextHolder.getUserId();
        Long companyId = TenantContextHolder.getCompanyId();

        List<Category> categories = categoryService.getCategoriesByUserId(userId, companyId, type);
        return Result.success(categories);
    }

    /**
     * 获取分类详情
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取分类详情", description = "根据ID获取分类详情")
    public Result<Category> getCategoryById(
            @Parameter(description = "分类ID") @PathVariable Long id) {
        Category category = categoryService.getById(id);
        if (category == null) {
            throw new BusinessException(404, "分类不存在");
        }
        return Result.success(category);
    }

    /**
     * 创建分类
     */
    @PostMapping
    @Operation(summary = "创建分类", description = "新增一个记账分类")
    public Result<Void> createCategory(@Validated @RequestBody CreateCategoryDTO dto) {
        Long userId = TenantContextHolder.getUserId();
        Long companyId = TenantContextHolder.getCompanyId();
        
        log.info("创建分类请求: userId={}, categoryName={}", userId, dto.getCategoryName());

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
    @Operation(summary = "更新分类", description = "更新指定分类信息")
    public Result<Void> updateCategory(
            @Parameter(description = "分类ID") @PathVariable Long id,
            @RequestBody com.gzang.app.dto.category.UpdateCategoryDTO dto) {

        Long userId = TenantContextHolder.getUserId();
        log.info("更新分类请求: id={}, userId={}", id, userId);

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
            throw new BusinessException(400, "更新分类失败");
        }

        log.info("分类更新成功: id={}", id);
        return Result.success();
    }

    /**
     * 删除分类
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除分类", description = "删除指定分类")
    public Result<Void> deleteCategory(
            @Parameter(description = "分类ID") @PathVariable Long id) {

        Long userId = TenantContextHolder.getUserId();
        log.info("删除分类请求: id={}, userId={}", id, userId);

        boolean success = categoryService.deleteCategory(id, userId);
        if (!success) {
            throw new BusinessException(400, "删除分类失败");
        }

        log.info("分类删除成功: id={}", id);
        return Result.success();
    }

    /**
     * 获取系统预设分类
     */
    @GetMapping("/system")
    @Operation(summary = "获取系统预设分类", description = "获取系统预设的分类列表")
    public Result<List<Category>> getSystemCategories(
            @Parameter(description = "类型 (1:收入, 2:支出)") @RequestParam(name = "type", required = false) Integer type) {

        List<Category> systemCategories = categoryService.getSystemCategories(type);
        return Result.success(systemCategories);
    }

    /**
     * 初始化用户分类
     */
    @PostMapping("/init")
    @Operation(summary = "初始化用户分类", description = "为用户初始化默认分类（复制系统预设分类）")
    public Result<Void> initUserCategories() {
        Long userId = TenantContextHolder.getUserId();
        log.info("初始化用户分类请求: userId={}", userId);

        boolean success = categoryService.initUserCategories(userId);
        if (!success) {
            throw new BusinessException(500, "初始化用户分类失败");
        }

        log.info("用户分类初始化成功: userId={}", userId);
        return Result.success();
    }
}
