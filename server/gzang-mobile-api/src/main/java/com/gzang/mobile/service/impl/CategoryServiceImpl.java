package com.gzang.mobile.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gzang.app.entity.Category;
import com.gzang.app.entity.CategoryIcon;
import com.gzang.app.entity.Transaction;
import com.gzang.app.mapper.CategoryIconMapper;
import com.gzang.app.mapper.CategoryMapper;
import com.gzang.app.mapper.TransactionMapper;
import com.gzang.mobile.service.CategoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 分类服务实现类
 *
 * @author G-Zang Team
 */
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    private static final Logger log = LoggerFactory.getLogger(CategoryServiceImpl.class);

    private final TransactionMapper transactionMapper;
    private final CategoryIconMapper categoryIconMapper;

    public CategoryServiceImpl(TransactionMapper transactionMapper, CategoryIconMapper categoryIconMapper) {
        this.transactionMapper = transactionMapper;
        this.categoryIconMapper = categoryIconMapper;
    }

    @Override
    public boolean createCategory(Category category) {
        log.info("创建分类: userId={}, categoryName={}", category.getUserId(), category.getCategoryName());

        // 检查分类名称是否重复
        if (!isCategoryNameAvailable(category.getCategoryName(), category.getUserId(), category.getCompanyId(), null)) {
            log.warn("分类名称已存在: userId={}, categoryName={}", category.getUserId(), category.getCategoryName());
            return false;
        }

        // 如果没有设置系统标志，默认设置为非系统分类
        if (category.getIsSystem() == null) {
            category.setIsSystem(0);
        }

        boolean result = save(category);
        if (result) {
            log.info("分类创建成功: id={}", category.getId());
        }

        return result;
    }

    @Override
    public boolean updateCategory(Category category) {
        log.info("更新分类: id={}, userId={}", category.getId(), category.getUserId());

        // 获取原分类信息
        Category oldCategory = getById(category.getId());
        if (oldCategory == null) {
            log.warn("分类不存在: id={}", category.getId());
            return false;
        }

        // 验证权限
        if (!hasPermission(oldCategory, category.getUserId())) {
            log.warn("用户无权修改此分类: userId={}, categoryId={}", category.getUserId(), category.getId());
            return false;
        }

        // 检查分类名称是否重复（排除自己）
        if (!isCategoryNameAvailable(category.getCategoryName(), category.getUserId(), category.getCompanyId(), category.getId())) {
            log.warn("分类名称已存在: userId={}, categoryName={}", category.getUserId(), category.getCategoryName());
            return false;
        }

        boolean result = updateById(category);
        if (result) {
            log.info("分类更新成功: id={}", category.getId());
        }

        return result;
    }

    @Override
    @Transactional
    public boolean deleteCategory(Long id, Long userId) {
        log.info("删除分类: id={}, userId={}", id, userId);

        // 获取分类信息
        Category category = getById(id);
        if (category == null) {
            log.warn("分类不存在: id={}", id);
            return false;
        }

        // 验证权限
        if (!hasPermission(category, userId)) {
            log.warn("用户无权删除此分类: userId={}, categoryId={}", userId, id);
            return false;
        }

        // 检查是否为系统预设分类
        if (category.getIsSystem() != null && category.getIsSystem() == 1) {
            log.warn("不能删除系统预设分类: id={}", id);
            return false;
        }

        // 检查是否有子分类
        List<Category> children = getChildrenByParentId(id);
        if (!children.isEmpty()) {
            log.warn("分类还有子分类，不能删除: id={}, childrenCount={}", id, children.size());
            return false;
        }

        boolean result = removeById(id);
        if (result) {
            log.info("分类删除成功: id={}", id);
        }

        return result;
    }

    @Override
    public List<Category> getCategoriesByUserId(Long userId, Long companyId, Integer type) {
        List<Category> categories = getBaseMapper().selectCategoriesByUserId(userId, companyId, type);
        
        // 关联图标信息
        enrichCategoriesWithIcons(categories);
        
        return categories;
    }

    @Override
    public IPage<Category> getCategoryPage(Page<Category> page, Long userId, Long companyId, Integer type, Long parentId) {
        return getBaseMapper().selectCategoryPage(page, userId, companyId, type, parentId);
    }

    @Override
    public List<Category> getChildrenByParentId(Long parentId) {
        List<Category> children = getBaseMapper().selectChildrenByParentId(parentId);
        
        // 关联图标信息
        enrichCategoriesWithIcons(children);
        
        return children;
    }

    @Override
    public List<Category> getSystemCategories(Integer type) {
        List<Category> categories = getBaseMapper().selectSystemCategories(type);
        
        // 关联图标信息
        enrichCategoriesWithIcons(categories);
        
        return categories;
    }

    @Override
    @Transactional
    public boolean initUserCategories(Long userId) {
        log.info("初始化用户分类: userId={}", userId);

        try {
            // 获取所有系统预设分类
            List<Category> systemCategories = getSystemCategories(null);

            // 为用户复制系统分类
            for (Category systemCategory : systemCategories) {
                Category userCategory = new Category();
                userCategory.setUserId(userId);
                userCategory.setCompanyId(null); // 个人分类
                userCategory.setCategoryName(systemCategory.getCategoryName());
                userCategory.setParentId(systemCategory.getParentId());
                userCategory.setType(systemCategory.getType());
                userCategory.setIsSystem(0); // 用户的自定义分类

                save(userCategory);
            }

            log.info("用户分类初始化成功: userId={}, categoriesCount={}", userId, systemCategories.size());
            return true;
        } catch (Exception e) {
            log.error("用户分类初始化失败: userId={}", userId, e);
            return false;
        }
    }

    @Override
    public boolean isCategoryNameAvailable(String categoryName, Long userId, Long companyId, Long excludeId) {
        int count = getBaseMapper().countByNameAndOwner(categoryName, userId, companyId, excludeId);
        return count == 0;
    }

    /**
     * 为分类列表关联图标信息
     */
    private void enrichCategoriesWithIcons(List<Category> categories) {
        if (categories == null || categories.isEmpty()) {
            return;
        }

        // 收集所有分类ID
        List<Long> categoryIds = categories.stream()
                .map(Category::getId)
                .collect(java.util.stream.Collectors.toList());

        // 批量查询图标信息
        LambdaQueryWrapper<CategoryIcon> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(CategoryIcon::getCategoryId, categoryIds);
        List<CategoryIcon> icons = categoryIconMapper.selectList(wrapper);

        // 构建 Map 便于快速查找
        Map<Long, CategoryIcon> iconMap = new HashMap<>();
        for (CategoryIcon icon : icons) {
            iconMap.put(icon.getCategoryId(), icon);
        }

        // 为每个分类设置图标信息
        for (Category category : categories) {
            CategoryIcon icon = iconMap.get(category.getId());
            if (icon != null) {
                category.setIcon(icon.getIcon());
                category.setColor(icon.getColor());
            }
        }
    }

    /**
     * 检查用户是否有权限操作分类
     */
    private boolean hasPermission(Category category, Long userId) {
        // 系统预设分类（全局）或用户自己的分类或公司分类
        return (category.getUserId() == null && category.getCompanyId() == null) ||
               (category.getUserId() != null && category.getUserId().equals(userId)) ||
               (category.getCompanyId() != null && category.getCompanyId().equals(userId)); // 暂时用userId作为companyId
    }

    /**
     * 检查分类是否有关联的交易记录
     */
    public long countTransactionsByCategory(Long categoryId) {
        LambdaQueryWrapper<Transaction> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Transaction::getCategoryId, categoryId);
        return transactionMapper.selectCount(wrapper);
    }

    @Override
    public List<Category> getCategoriesWithChildren(Long userId, Long bookId, String yearMonth) {
        log.info("获取带子分类和额度的分类: userId={}, bookId={}, yearMonth={}", userId, bookId, yearMonth);

        // 获取用户的一级分类
        List<Category> topLevelCategories = getCategoriesByUserId(userId, null, null);

        // 过滤出一级分类
        List<Category> parentCategories = topLevelCategories.stream()
                .filter(c -> c.getParentId() == null || c.getParentId() == 0L)
                .collect(java.util.stream.Collectors.toList());

        // 为每个一级分类添加子分类
        for (Category parent : parentCategories) {
            List<Category> children = topLevelCategories.stream()
                    .filter(c -> parent.getId().equals(c.getParentId()))
                    .collect(java.util.stream.Collectors.toList());
            parent.setChildren(children);
        }

        return parentCategories;
    }

    @Override
    public CategoryBudgetInfo getCategoryBudget(Long categoryId, Long userId, Long bookId, String yearMonth) {
        log.info("获取分类预算: categoryId={}, userId={}, bookId={}, yearMonth={}", categoryId, userId, bookId, yearMonth);

        CategoryBudgetInfo info = new CategoryBudgetInfo();
        info.setBudget(0);
        info.setSpent(0);
        info.setRemaining(0);
        info.setPercentUsed(0);
        info.setWarningThreshold(80);

        // TODO: 从 t_category_budget 表查询实际预算数据
        // 目前返回默认值，后续接入预算表

        return info;
    }

    /**
     * 检查分类是否可以安全删除
     */
    public DeleteCheckResult canDeleteCategory(Long categoryId, Long userId) {
        Category category = getById(categoryId);
        if (category == null) {
            return new DeleteCheckResult(false, "分类不存在");
        }

        // 检查是否为系统预设分类
        if (category.getIsSystem() != null && category.getIsSystem() == 1) {
            return new DeleteCheckResult(false, "系统预设分类不能删除");
        }

        // 检查是否有子分类
        List<Category> children = getChildrenByParentId(categoryId);
        if (!children.isEmpty()) {
            return new DeleteCheckResult(false, "分类还有子分类，请先删除子分类");
        }

        // 检查是否有交易关联
        long transactionCount = countTransactionsByCategory(categoryId);
        if (transactionCount > 0) {
            return new DeleteCheckResult(false, "该分类已有 " + transactionCount + " 笔交易关联，不能直接删除");
        }

        return new DeleteCheckResult(true, "可以删除");
    }

    /**
     * 删除检查结果
     */
    public static class DeleteCheckResult {
        private final boolean canDelete;
        private final String message;

        public DeleteCheckResult(boolean canDelete, String message) {
            this.canDelete = canDelete;
            this.message = message;
        }

        public boolean canDelete() {
            return canDelete;
        }

        public String getMessage() {
            return message;
        }
    }
}
