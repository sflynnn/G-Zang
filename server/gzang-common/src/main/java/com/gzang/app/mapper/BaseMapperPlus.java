package com.gzang.app.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 基础 Mapper 接口
 * 所有业务 Mapper 继承此类
 *
 * @author G-Zang Team
 * @param <T> 实体类型
 */
public interface BaseMapperPlus<T> extends BaseMapper<T> {

    /**
     * 自定义分页查询
     */
    default IPage<T> selectPage(Page<T> page) {
        return selectPage(page, null);
    }

    /**
     * 条件分页查询
     */
    IPage<T> selectPageByCondition(Page<T> page, @Param("condition") T condition);

    /**
     * 根据ID批量查询
     */
    default List<T> selectByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }
        return selectBatchIds(ids);
    }

    /**
     * 根据条件查询单条
     */
    default T selectOneByCondition(@Param("condition") T condition) {
        List<T> list = selectByCondition(condition);
        return list.isEmpty() ? null : list.get(0);
    }

    /**
     * 根据条件查询列表
     */
    List<T> selectByCondition(@Param("condition") T condition);

    /**
     * 根据条件统计数量
     */
    Long selectCountByCondition(@Param("condition") T condition);
}
