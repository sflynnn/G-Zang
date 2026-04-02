package com.gzang.app.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 角色数据范围关联实体类（行级权限预留）
 *
 * @author G-Zang Team
 */
@Data
@TableName("t_role_data_scope")
public class RoleDataScope {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long roleId;

    private String scopeType;

    private String scopeValue;

    private LocalDateTime createTime;
}
