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
}
