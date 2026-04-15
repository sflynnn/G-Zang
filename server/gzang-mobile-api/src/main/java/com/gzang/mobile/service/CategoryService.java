package com.gzang.mobile.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.gzang.app.entity.Category;

import java.util.List;

/**
 * 分类服务接口
 *
 * @author G-Zang Team
 */
public interface CategoryService extends IService<Category> {

    /**
     * 创建分类
     *
     * @param category 分类信息
     * @return 是否成功
     */
    boolean createCategory(Category category);

    /**
     * 更新分类
     *
     * @param category 分类信息
     * @return 是否成功
     */
    boolean updateCategory(Category category);

    /**
     * 删除分类
     *
     * @param id 分类ID
     * @param userId 用户ID（用于权限验证）
     * @return 是否成功
     */
    boolean deleteCategory(Long id, Long userId);

    /**
     * 根据用户ID查询分类列表
     *
     * @param userId 用户ID
     * @param companyId 公司ID（可选）
     * @param type 类型（可选）
     * @return 分类列表
     */
    List<Category> getCategoriesByUserId(Long userId, Long companyId, Integer type);

    /**
     * 分页查询分类
     *
     * @param page 分页参数
     * @param userId 用户ID
     * @param companyId 公司ID（可选）
     * @param type 类型
     * @param parentId 父分类ID
     * @return 分页结果
     */
    IPage<Category> getCategoryPage(Page<Category> page, Long userId, Long companyId, Integer type, Long parentId);

    /**
     * 查询子分类
     *
     * @param parentId 父分类ID
     * @return 子分类列表
     */
    List<Category> getChildrenByParentId(Long parentId);

    /**
     * 获取系统预设分类
     *
     * @param type 类型（可选）
     * @return 系统预设分类列表
     */
    List<Category> getSystemCategories(Integer type);

    /**
     * 初始化用户分类（复制系统预设分类）
     *
     * @param userId 用户ID
     * @return 是否成功
     */
    boolean initUserCategories(Long userId);

    /**
     * 检查分类名称是否可用
     *
     * @param categoryName 分类名称
     * @param userId 用户ID
     * @param companyId 公司ID（可选）
     * @param excludeId 排除的分类ID（用于更新时）
     * @return 是否可用
     */
    boolean isCategoryNameAvailable(String categoryName, Long userId, Long companyId, Long excludeId);
}
