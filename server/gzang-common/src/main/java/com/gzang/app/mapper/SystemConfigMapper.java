package com.gzang.app.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gzang.app.entity.SystemConfig;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 系统配置Mapper接口
 *
 * @author G-Zang Team
 */
public interface SystemConfigMapper extends BaseMapper<SystemConfig> {

    /**
     * 根据配置分组查询配置列表
     *
     * @param configGroup 配置分组
     * @return 配置列表
     */
    List<SystemConfig> selectByGroup(@Param("configGroup") String configGroup);

    /**
     * 根据配置键查询配置
     *
     * @param configKey 配置键
     * @return 配置
     */
    SystemConfig selectByKey(@Param("configKey") String configKey);

    /**
     * 查询所有分组
     *
     * @return 分组列表
     */
    List<String> selectAllGroups();

    /**
     * 根据配置键列表查询
     *
     * @param keys 配置键列表
     * @return 配置列表
     */
    List<SystemConfig> selectByKeys(@Param("keys") List<String> keys);
}
