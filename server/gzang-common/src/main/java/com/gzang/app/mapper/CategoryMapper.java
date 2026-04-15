package com.gzang.app.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gzang.app.entity.Category;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 分类Mapper接口
 *
 * @author G-Zang Team
 */
public interface CategoryMapper extends BaseMapper<Category> {

    /**
     * 根据用户ID查询分类列表
     *
     * @param userId 用户ID
     * @param companyId 公司ID（可选）
     * @param type 类型（可选）
     * @return 分类列表
     */
    @Select("SELECT * FROM t_category WHERE " +
            "(user_id = #{userId} OR (user_id IS NULL AND company_id IS NULL)) " +
            "#{companyId != null ? 'OR (company_id IS NULL OR company_id = #{companyId})' : ''} " +
            "#{type != null ? 'AND type = #{type}' : ''} " +
            "ORDER BY parent_id ASC, id ASC")
    List<Category> selectCategoriesByUserId(@Param("userId") Long userId, @Param("companyId") Long companyId, @Param("type") Integer type);

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
    IPage<Category> selectCategoryPage(
            Page<Category> page,
            @Param("userId") Long userId,
            @Param("companyId") Long companyId,
            @Param("type") Integer type,
            @Param("parentId") Long parentId);

    /**
     * 查询子分类
     *
     * @param parentId 父分类ID
     * @return 子分类列表
     */
    @Select("SELECT * FROM t_category WHERE parent_id = #{parentId} ORDER BY id ASC")
    List<Category> selectChildrenByParentId(@Param("parentId") Long parentId);

    /**
     * 检查分类名称是否重复
     *
     * @param categoryName 分类名称
     * @param userId 用户ID
     * @param companyId 公司ID（可选）
     * @param excludeId 排除的分类ID（用于更新时）
     * @return 是否重复
     */
    @Select("SELECT COUNT(*) FROM t_category WHERE category_name = #{categoryName} " +
            "AND ((user_id = #{userId} AND user_id IS NOT NULL) " +
            "OR (company_id = #{companyId} AND company_id IS NOT NULL) " +
            "OR (user_id IS NULL AND company_id IS NULL)) " +
            "#{excludeId != null ? 'AND id != #{excludeId}' : ''}")
    int countByNameAndOwner(@Param("categoryName") String categoryName,
                           @Param("userId") Long userId,
                           @Param("companyId") Long companyId,
                           @Param("excludeId") Long excludeId);

    /**
     * 获取系统预设分类
     *
     * @param type 类型（可选）
     * @return 系统预设分类列表
     */
    @Select("SELECT * FROM t_category WHERE is_system = 1 " +
            "#{type != null ? 'AND type = #{type}' : ''} " +
            "ORDER BY type ASC, parent_id ASC, id ASC")
    List<Category> selectSystemCategories(@Param("type") Integer type);
}
