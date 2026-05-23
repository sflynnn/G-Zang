package com.gzang.app.vo;

import lombok.Data;

/**
 * 系统配置视图对象
 *
 * @author G-Zang Team
 */
@Data
public class SystemConfigVO {

    private Long id;

    private String configGroup;

    private String configKey;

    private String configValue;

    private String configType;

    private String configName;

    private String description;

    private Boolean editable;

    private Integer sortOrder;
}
