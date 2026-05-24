package com.gzang.app.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gzang.app.entity.CategoryIcon;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 分类图标 Mapper
 *
 * @author G-Zang Team
 */
public interface CategoryIconMapper extends BaseMapper<CategoryIcon> {

    /**
     * 根据分类ID查询图标配置
     */
    @Select("SELECT * FROM t_category_icon WHERE category_id = #{categoryId}")
    CategoryIcon selectByCategoryId(@Param("categoryId") Long categoryId);

    /**
     * 批量查询分类图标
     */
    @Select("<script>SELECT * FROM t_category_icon WHERE category_id IN " +
            "<foreach collection='categoryIds' item='id' open='(' separator=',' close=')'>" +
            "#{id}</foreach></script>")
    List<CategoryIcon> selectByCategoryIds(@Param("categoryIds") List<Long> categoryIds);
}
