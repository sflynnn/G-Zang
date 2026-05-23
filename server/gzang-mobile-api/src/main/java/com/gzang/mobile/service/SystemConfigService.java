package com.gzang.mobile.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gzang.app.entity.SystemConfig;

import java.util.List;
import java.util.Map;

/**
 * 系统配置服务接口（移动端）
 *
 * @author G-Zang Team
 */
public interface SystemConfigService extends IService<SystemConfig> {

    /**
     * 根据配置分组获取配置列表
     *
     * @param configGroup 配置分组
     * @return 配置列表
     */
    List<SystemConfig> getByGroup(String configGroup);

    /**
     * 根据配置键获取配置值
     *
     * @param configKey 配置键
     * @return 配置值
     */
    String getValue(String configKey);

    /**
     * 根据配置键获取配置值，带默认值
     *
     * @param configKey 配置键
     * @param defaultValue 默认值
     * @return 配置值
     */
    String getValue(String configKey, String defaultValue);

    /**
     * 更新配置值
     *
     * @param configKey 配置键
     * @param configValue 配置值
     * @return 是否成功
     */
    boolean updateValue(String configKey, String configValue);

    /**
     * 获取所有配置（按分组）
     *
     * @return 分组后的配置Map
     */
    Map<String, List<SystemConfig>> getAllGrouped();

    /**
     * 创建或更新配置
     *
     * @param configGroup 配置分组
     * @param configKey 配置键
     * @param configValue 配置值
     * @param configType 配置类型
     * @param configName 配置名称
     * @param description 描述
     * @return 是否成功
     */
    boolean createOrUpdate(String configGroup, String configKey, String configValue,
                          String configType, String configName, String description);

    /**
     * 删除配置
     *
     * @param id 配置ID
     * @return 是否成功
     */
    boolean deleteConfig(Long id);
}
