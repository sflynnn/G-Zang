package com.gzang.app.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gzang.app.entity.Category;
import com.gzang.app.mapper.CategoryMapper;
import com.gzang.app.service.CategoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 分类服务实现类
 *
 * @author G-Zang Team
 */
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    private static final Logger log = LoggerFactory.getLogger(CategoryServiceImpl.class);

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
        return getBaseMapper().selectCategoriesByUserId(userId, companyId, type);
    }

    @Override
    public IPage<Category> getCategoryPage(Page<Category> page, Long userId, Long companyId, Integer type, Long parentId) {
        return getBaseMapper().selectCategoryPage(page, userId, companyId, type, parentId);
    }

    @Override
    public List<Category> getChildrenByParentId(Long parentId) {
        return getBaseMapper().selectChildrenByParentId(parentId);
    }

    @Override
    public List<Category> getSystemCategories(Integer type) {
        return getBaseMapper().selectSystemCategories(type);
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
     * 检查用户是否有权限操作分类
     *
     * @param category 分类信息
     * @param userId 用户ID
     * @return 是否有权限
     */
    private boolean hasPermission(Category category, Long userId) {
        // 系统预设分类（全局）或用户自己的分类或公司分类
        return (category.getUserId() == null && category.getCompanyId() == null) ||
               (category.getUserId() != null && category.getUserId().equals(userId)) ||
               (category.getCompanyId() != null && category.getCompanyId().equals(userId)); // 暂时用userId作为companyId
    }
}
