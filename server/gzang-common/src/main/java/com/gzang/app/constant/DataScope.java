package com.gzang.app.constant;

/**
 * 数据权限范围枚举
 *
 * @author G-Zang Team
 */
public enum DataScope {

    /**
     * 仅本人数据
     */
    PERSONAL(1, "仅本人数据"),

    /**
     * 本部门数据
     */
    DEPARTMENT(2, "本部门数据"),

    /**
     * 本部门及以下数据
     */
    DEPARTMENT_AND_CHILD(3, "本部门及以下数据"),

    /**
     * 全公司数据
     */
    COMPANY(4, "全公司数据"),

    /**
     * 全部数据
     */
    ALL(5, "全部数据");

    private final Integer code;
    private final String description;

    DataScope(Integer code, String description) {
        this.code = code;
        this.description = description;
    }

    public Integer getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static DataScope fromCode(Integer code) {
        if (code == null) {
            return PERSONAL;
        }
        for (DataScope scope : values()) {
            if (scope.code.equals(code)) {
                return scope;
            }
        }
        return PERSONAL;
    }
}
