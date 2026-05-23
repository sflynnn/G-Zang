package com.gzang.mobile.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gzang.app.entity.SystemConfig;
import com.gzang.app.mapper.SystemConfigMapper;
import com.gzang.app.service.SystemConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 系统配置服务实现类
 *
 * @author G-Zang Team
 */
@Service
public class SystemConfigServiceImpl extends ServiceImpl<SystemConfigMapper, SystemConfig> implements SystemConfigService {

    private static final Logger log = LoggerFactory.getLogger(SystemConfigServiceImpl.class);

    @Override
    public List<SystemConfig> getByGroup(String configGroup) {
        return getBaseMapper().selectByGroup(configGroup);
    }

    @Override
    @Cacheable(value = "systemConfig", key = "#configKey")
    public String getValue(String configKey) {
        SystemConfig config = getBaseMapper().selectByKey(configKey);
        return config != null ? config.getConfigValue() : null;
    }

    @Override
    @Cacheable(value = "systemConfig", key = "#configKey")
    public String getValue(String configKey, String defaultValue) {
        String value = getValue(configKey);
        return value != null ? value : defaultValue;
    }

    @Override
    @CacheEvict(value = "systemConfig", key = "#configKey")
    public boolean updateValue(String configKey, String configValue) {
        SystemConfig config = getBaseMapper().selectByKey(configKey);
        if (config == null) {
            log.warn("配置不存在: configKey={}", configKey);
            return false;
        }
        if (Boolean.FALSE.equals(config.getEditable())) {
            log.warn("配置不可编辑: configKey={}", configKey);
            return false;
        }
        config.setConfigValue(configValue);
        return updateById(config);
    }

    @Override
    public Map<String, List<SystemConfig>> getAllGrouped() {
        List<SystemConfig> allConfigs = list();
        return allConfigs.stream()
                .collect(Collectors.groupingBy(
                        config -> config.getConfigGroup() != null ? config.getConfigGroup() : "other"
                ));
    }

    @Override
    @CacheEvict(value = "systemConfig", allEntries = true)
    public boolean createOrUpdate(String configGroup, String configKey, String configValue,
                                  String configType, String configName, String description) {
        SystemConfig existing = getBaseMapper().selectByKey(configKey);
        if (existing != null) {
            existing.setConfigValue(configValue);
            existing.setConfigName(configName);
            existing.setDescription(description);
            return updateById(existing);
        } else {
            SystemConfig config = new SystemConfig();
            config.setConfigGroup(configGroup);
            config.setConfigKey(configKey);
            config.setConfigValue(configValue);
            config.setConfigType(configType != null ? configType : "string");
            config.setConfigName(configName);
            config.setDescription(description);
            config.setEditable(true);
            config.setSortOrder(0);
            return save(config);
        }
    }

    @Override
    @CacheEvict(value = "systemConfig", allEntries = true)
    public boolean deleteConfig(Long id) {
        return removeById(id);
    }
}
