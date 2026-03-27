package com.gzang.app.constant;

/**
 * 数据范围枚举
 *
 * @author G-Zang Team
 */
public enum DataScope {

    /**
     * 仅本人数据
     */
    OWN("OWN", "仅本人数据"),

    /**
     * 本部门/成本中心数据（预留）
     */
    DEPARTMENT("DEPARTMENT", "本部门数据"),

    /**
     * 本公司全部数据
     */
    COMPANY("COMPANY", "本公司全部数据"),

    /**
     * 全部数据（仅超级管理员）
     */
    ALL("ALL", "全部数据");

    private final String code;
    private final String description;

    DataScope(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static DataScope fromCode(String code) {
        for (DataScope scope : values()) {
            if (scope.code.equals(code)) {
                return scope;
            }
        }
        return OWN;
    }
}
