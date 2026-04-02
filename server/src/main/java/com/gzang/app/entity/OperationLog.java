package com.gzang.app.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 操作日志实体类
 *
 * @author G-Zang Team
 */
@Data
@TableName("t_operation_log")
public class OperationLog {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long userId;

    private String username;

    private Long companyId;

    private String action;

    private String targetType;

    private String targetId;

    private String targetName;

    private String detail;

    private String ipAddress;

    private String userAgent;

    private String requestUri;

    private String requestMethod;

    private String requestParams;

    private Integer responseStatus;

    private String errorMessage;

    private Integer durationMs;

    private LocalDateTime createTime;
}
