package com.gzang.app.dto.system;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 创建/更新系统配置请求DTO
 *
 * @author G-Zang Team
 */
@Schema(description = "系统配置请求")
public class SystemConfigDTO {

    @Schema(description = "配置分组", example = "general")
    @NotBlank(message = "配置分组不能为空")
    @Size(max = 50, message = "配置分组长度不能超过50")
    private String configGroup;

    @Schema(description = "配置键", example = "site_name")
    @NotBlank(message = "配置键不能为空")
    @Size(max = 100, message = "配置键长度不能超过100")
    private String configKey;

    @Schema(description = "配置值", example = "G-Zang 归藏财务管理系统")
    private String configValue;

    @Schema(description = "配置类型：string, number, boolean, json", example = "string")
    @Size(max = 20, message = "配置类型长度不能超过20")
    private String configType;

    @Schema(description = "配置名称", example = "网站名称")
    @Size(max = 100, message = "配置名称长度不能超过100")
    private String configName;

    @Schema(description = "配置描述", example = "系统网站的显示名称")
    @Size(max = 500, message = "配置描述长度不能超过500")
    private String description;

    @Schema(description = "是否可编辑", example = "true")
    private Boolean editable;

    @Schema(description = "排序号", example = "1")
    private Integer sortOrder;

    public String getConfigGroup() {
        return configGroup;
    }

    public void setConfigGroup(String configGroup) {
        this.configGroup = configGroup;
    }

    public String getConfigKey() {
        return configKey;
    }

    public void setConfigKey(String configKey) {
        this.configKey = configKey;
    }

    public String getConfigValue() {
        return configValue;
    }

    public void setConfigValue(String configValue) {
        this.configValue = configValue;
    }

    public String getConfigType() {
        return configType;
    }

    public void setConfigType(String configType) {
        this.configType = configType;
    }

    public String getConfigName() {
        return configName;
    }

    public void setConfigName(String configName) {
        this.configName = configName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getEditable() {
        return editable;
    }

    public void setEditable(Boolean editable) {
        this.editable = editable;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }
}
