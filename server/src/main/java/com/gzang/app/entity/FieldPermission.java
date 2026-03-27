package com.gzang.app.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 字段权限实体类（预留扩展）
 *
 * @author G-Zang Team
 */
@Data
@TableName("t_field_permission")
public class FieldPermission {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long roleId;

    private String entityType;

    private String fieldName;

    private Integer fieldVisible = 1;

    private Integer fieldEditable = 1;

    private LocalDateTime createTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public Integer getFieldVisible() {
        return fieldVisible;
    }

    public void setFieldVisible(Integer fieldVisible) {
        this.fieldVisible = fieldVisible;
    }

    public Integer getFieldEditable() {
        return fieldEditable;
    }

    public void setFieldEditable(Integer fieldEditable) {
        this.fieldEditable = fieldEditable;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }
}
