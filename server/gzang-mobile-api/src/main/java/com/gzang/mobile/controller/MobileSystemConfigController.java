package com.gzang.mobile.controller;

import com.gzang.app.dto.system.SystemConfigDTO;
import com.gzang.app.entity.SystemConfig;
import com.gzang.app.exception.BusinessException;
import com.gzang.app.service.SystemConfigService;
import com.gzang.app.vo.Result;
import com.gzang.app.vo.SystemConfigVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 移动端系统配置控制器
 *
 * @author G-Zang Team
 */
@RestController
@RequestMapping("/api/mobile/system-config")
@Tag(name = "移动端系统配置", description = "系统配置相关接口")
public class MobileSystemConfigController {

    private static final Logger log = LoggerFactory.getLogger(MobileSystemConfigController.class);

    private final SystemConfigService systemConfigService;

    public MobileSystemConfigController(SystemConfigService systemConfigService) {
        this.systemConfigService = systemConfigService;
    }

    /**
     * 获取所有配置（按分组）
     */
    @GetMapping
    @Operation(summary = "获取所有配置", description = "获取所有系统配置，按分组返回")
    public Result<Map<String, List<SystemConfigVO>>> getAllConfigs() {
        Map<String, List<SystemConfig>> grouped = systemConfigService.getAllGrouped();
        Map<String, List<SystemConfigVO>> result = new HashMap<>();

        for (Map.Entry<String, List<SystemConfig>> entry : grouped.entrySet()) {
            List<SystemConfigVO> vos = new ArrayList<>();
            for (SystemConfig config : entry.getValue()) {
                vos.add(convertToVO(config));
            }
            result.put(entry.getKey(), vos);
        }

        return Result.success(result);
    }

    /**
     * 根据分组获取配置
     */
    @GetMapping("/group/{group}")
    @Operation(summary = "获取分组配置", description = "根据配置分组获取配置列表")
    public Result<List<SystemConfigVO>> getConfigByGroup(
            @Parameter(description = "配置分组") @PathVariable String group) {

        List<SystemConfig> configs = systemConfigService.getByGroup(group);
        List<SystemConfigVO> vos = new ArrayList<>();
        for (SystemConfig config : configs) {
            vos.add(convertToVO(config));
        }

        return Result.success(vos);
    }

    /**
     * 获取单个配置值
     */
    @GetMapping("/key/{key}")
    @Operation(summary = "获取配置值", description = "根据配置键获取单个配置值")
    public Result<String> getConfigValue(
            @Parameter(description = "配置键") @PathVariable String key) {

        String value = systemConfigService.getValue(key);
        return Result.success(value);
    }

    /**
     * 更新配置值
     */
    @PutMapping("/{key}")
    @Operation(summary = "更新配置值", description = "更新指定配置键的配置值")
    public Result<Void> updateConfigValue(
            @Parameter(description = "配置键") @PathVariable String key,
            @RequestBody Map<String, String> body) {

        String value = body.get("value");
        if (value == null) {
            throw new BusinessException(400, "配置值不能为空");
        }

        log.info("更新系统配置: key={}, value={}", key, value);
        boolean success = systemConfigService.updateValue(key, value);

        if (!success) {
            throw new BusinessException(400, "更新配置失败，配置可能不存在或不可编辑");
        }

        return Result.success();
    }

    /**
     * 创建配置
     */
    @PostMapping
    @Operation(summary = "创建配置", description = "创建新的系统配置")
    public Result<Void> createConfig(@Validated @RequestBody SystemConfigDTO dto) {
        log.info("创建系统配置: group={}, key={}", dto.getConfigGroup(), dto.getConfigKey());

        boolean success = systemConfigService.createOrUpdate(
                dto.getConfigGroup(),
                dto.getConfigKey(),
                dto.getConfigValue(),
                dto.getConfigType(),
                dto.getConfigName(),
                dto.getDescription()
        );

        if (!success) {
            throw new BusinessException(400, "创建配置失败");
        }

        return Result.success();
    }

    /**
     * 删除配置
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除配置", description = "删除指定ID的系统配置")
    public Result<Void> deleteConfig(
            @Parameter(description = "配置ID") @PathVariable Long id) {

        log.info("删除系统配置: id={}", id);
        boolean success = systemConfigService.deleteConfig(id);

        if (!success) {
            throw new BusinessException(400, "删除配置失败");
        }

        return Result.success();
    }

    /**
     * 批量获取配置值
     */
    @PostMapping("/batch")
    @Operation(summary = "批量获取配置", description = "根据配置键列表批量获取配置值")
    public Result<Map<String, String>> getBatchConfigs(@RequestBody List<String> keys) {
        Map<String, String> result = new HashMap<>();
        for (String key : keys) {
            result.put(key, systemConfigService.getValue(key));
        }
        return Result.success(result);
    }

    private SystemConfigVO convertToVO(SystemConfig config) {
        SystemConfigVO vo = new SystemConfigVO();
        vo.setId(config.getId());
        vo.setConfigGroup(config.getConfigGroup());
        vo.setConfigKey(config.getConfigKey());
        vo.setConfigValue(config.getConfigValue());
        vo.setConfigType(config.getConfigType());
        vo.setConfigName(config.getConfigName());
        vo.setDescription(config.getDescription());
        vo.setEditable(config.getEditable());
        vo.setSortOrder(config.getSortOrder());
        return vo;
    }
}
