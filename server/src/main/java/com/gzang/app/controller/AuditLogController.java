package com.gzang.app.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gzang.app.annotation.RequirePermission;
import com.gzang.app.constant.PermissionCode;
import com.gzang.app.service.OperationLogService;
import com.gzang.app.vo.OperationLogVO;
import com.gzang.app.vo.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

/**
 * 审计日志控制器
 *
 * @author G-Zang Team
 */
@RestController
@RequestMapping("/api/v1/audit-logs")
@Tag(name = "审计日志", description = "审计日志查询相关接口")
public class AuditLogController {

    private final OperationLogService operationLogService;

    public AuditLogController(OperationLogService operationLogService) {
        this.operationLogService = operationLogService;
    }

    /**
     * 分页查询操作日志
     */
    @GetMapping
    @RequirePermission(PermissionCode.SYSTEM_AUDIT)
    @Operation(summary = "分页查询操作日志", description = "分页查询操作日志，支持按操作类型、目标类型、用户ID过滤")
    public Result<IPage<OperationLogVO>> pageQuery(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer current,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer size,
            @Parameter(description = "操作类型") @RequestParam(required = false) String action,
            @Parameter(description = "目标类型") @RequestParam(required = false) String targetType,
            @Parameter(description = "用户ID") @RequestParam(required = false) Long userId) {

        com.baomidou.mybatisplus.extension.plugins.pagination.Page<com.gzang.app.entity.OperationLog> rawPage =
                (com.baomidou.mybatisplus.extension.plugins.pagination.Page<com.gzang.app.entity.OperationLog>)
                        operationLogService.pageQuery(current, size, action, targetType, userId);

        Page<OperationLogVO> page = new Page<>(rawPage.getCurrent(), rawPage.getSize(), rawPage.getTotal());
        java.util.List<OperationLogVO> voList = new java.util.ArrayList<>();
        for (com.gzang.app.entity.OperationLog record : rawPage.getRecords()) {
            OperationLogVO vo = new OperationLogVO();
            BeanUtils.copyProperties(record, vo);
            voList.add(vo);
        }
        page.setRecords(voList);
        return Result.success(page);
    }

    /**
     * 查看操作日志详情
     */
    @GetMapping("/{id}")
    @RequirePermission(PermissionCode.SYSTEM_AUDIT)
    @Operation(summary = "查看操作日志详情", description = "根据ID查看操作日志详情")
    public Result<OperationLogVO> getById(@Parameter(description = "日志ID") @PathVariable Long id) {
        Object record = operationLogService.getById(id);
        if (record == null) {
            return Result.error(404, "日志不存在", null);
        }
        OperationLogVO vo = new OperationLogVO();
        BeanUtils.copyProperties(record, vo);
        return Result.success(vo);
    }
}
