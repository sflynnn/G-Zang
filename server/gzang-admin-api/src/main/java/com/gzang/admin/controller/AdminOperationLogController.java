package com.gzang.admin.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gzang.app.service.OperationLogService;
import com.gzang.app.vo.OperationLogVO;
import com.gzang.app.vo.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 管理端操作日志控制器
 *
 * @author G-Zang Team
 */
@RestController
@RequestMapping("/api/admin/operation-logs")
@Tag(name = "管理端操作日志", description = "审计日志相关接口")
public class AdminOperationLogController {

    private final OperationLogService operationLogService;

    public AdminOperationLogController(OperationLogService operationLogService) {
        this.operationLogService = operationLogService;
    }

    /**
     * 获取操作日志列表
     */
    @GetMapping
    @PreAuthorize("hasAuthority('LOG_VIEW')")
    @Operation(summary = "获取操作日志", description = "分页获取操作日志列表")
    public Result<IPage<OperationLogVO>> getOperationLogList(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer current,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "20") Integer size,
            @Parameter(description = "操作人") @RequestParam(required = false) String operator,
            @Parameter(description = "操作类型") @RequestParam(required = false) String action) {

        Page<OperationLogVO> page = new Page<>(current, size);
        IPage<OperationLogVO> logPage = operationLogService.pageQuery(page, null, null, operator, action);
        return Result.success(logPage);
    }

    /**
     * 获取操作日志详情
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('LOG_VIEW')")
    @Operation(summary = "获取操作日志详情", description = "获取指定操作日志详情")
    public Result<OperationLogVO> getOperationLogById(
            @Parameter(description = "日志ID") @PathVariable Long id) {
        OperationLogVO logVO = operationLogService.getOperationLogById(id);
        return Result.success(logVO);
    }
}
