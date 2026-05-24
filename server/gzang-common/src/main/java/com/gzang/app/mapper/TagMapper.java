package com.gzang.app.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gzang.app.entity.Tag;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 标签Mapper接口
 *
 * @author G-Zang Team
 */
public interface TagMapper extends BaseMapper<Tag> {

    /**
     * 根据用户ID查询标签列表
     *
     * @param userId 用户ID
     * @return 标签列表
     */
    @Select("SELECT * FROM t_tag WHERE user_id = #{userId} ORDER BY is_frequent DESC, usage_count DESC")
    List<Tag> selectTagsByUserId(@Param("userId") Long userId);

    /**
     * 获取用户常用标签
     *
     * @param userId 用户ID
     * @param limit 数量限制
     * @return 常用标签列表
     */
    @Select("SELECT * FROM t_tag WHERE user_id = #{userId} AND is_frequent = 1 ORDER BY usage_count DESC LIMIT #{limit}")
    List<Tag> selectFrequentTags(@Param("userId") Long userId, @Param("limit") int limit);

    /**
     * 根据标签名称查询标签
     *
     * @param userId 用户ID
     * @param tagName 标签名称
     * @return 标签
     */
    @Select("SELECT * FROM t_tag WHERE user_id = #{userId} AND tag_name = #{tagName} LIMIT 1")
    Tag selectByName(@Param("userId") Long userId, @Param("tagName") String tagName);

    /**
     * 检查标签是否存在
     *
     * @param userId 用户ID
     * @param tagName 标签名称
     * @return 数量
     */
    @Select("SELECT COUNT(*) FROM t_tag WHERE user_id = #{userId} AND tag_name = #{tagName}")
    int countByName(@Param("userId") Long userId, @Param("tagName") String tagName);

    /**
     * 增加标签使用次数
     *
     * @param tagId 标签ID
     */
    @Select("UPDATE t_tag SET usage_count = usage_count + 1 WHERE id = #{tagId}")
    void incrementUsageCount(@Param("tagId") Long tagId);
}
