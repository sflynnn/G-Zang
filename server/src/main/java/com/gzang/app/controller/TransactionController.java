package com.gzang.app.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gzang.app.dto.transaction.CreateTransactionDTO;
import com.gzang.app.dto.transaction.UpdateTransactionDTO;
import com.gzang.app.entity.Transaction;
import com.gzang.app.exception.BusinessException;
import com.gzang.app.mapper.TransactionMapper;
import com.gzang.app.service.TransactionService;
import com.gzang.app.util.JwtUtil;
import com.gzang.app.vo.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

import static com.gzang.app.constant.ErrorCode.DATA_NOT_FOUND;

/**
 * 交易记录控制器
 * 提供记账相关的API接口
 *
 * @author G-Zang Team
 */
@RestController
@RequestMapping("/api/v1/transactions")
@Tag(name = "交易管理", description = "交易记录、记账管理相关接口")
public class TransactionController {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(TransactionController.class);

    private final TransactionService transactionService;
    private final JwtUtil jwtUtil;

    public TransactionController(TransactionService transactionService, JwtUtil jwtUtil) {
        this.transactionService = transactionService;
        this.jwtUtil = jwtUtil;
    }

    /**
     * 创建交易记录
     */
    @PostMapping
    @PreAuthorize("hasAuthority('TRANSACTION_ADD')")
    @Operation(summary = "创建交易记录", description = "新增一笔交易记录")
    public Result<Void> createTransaction(@Validated @RequestBody CreateTransactionDTO dto, Principal principal) {
        log.info("创建交易记录请求: user={}, amount={}, type={}",
                principal.getName(), dto.getAmount(), dto.getType());

        Long userId = jwtUtil.getUserIdFromToken(principal.getName());
        Long companyId = jwtUtil.getCompanyIdFromToken(principal.getName());

        Transaction transaction = new Transaction();
        transaction.setAmount(dto.getAmount());
        transaction.setType(dto.getType());
        transaction.setCategoryId(dto.getCategoryId());
        transaction.setAccountId(dto.getAccountId());
        transaction.setTransactionTime(dto.getTransactionTime() != null ? dto.getTransactionTime() : LocalDateTime.now());
        transaction.setRemark(dto.getRemark());
        transaction.setUserId(userId);
        transaction.setCompanyId(companyId);

        boolean success = transactionService.createTransaction(transaction);
        if (!success) {
            throw new BusinessException(500, "创建交易记录失败");
        }

        log.info("交易记录创建成功: id={}", transaction.getId());
        return Result.success();
    }

    /**
     * 更新交易记录
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('TRANSACTION_EDIT')")
    @Operation(summary = "更新交易记录", description = "更新指定交易记录")
    public Result<Void> updateTransaction(
            @Parameter(description = "交易记录ID") @PathVariable Long id,
            @Validated @RequestBody UpdateTransactionDTO dto,
            Principal principal) {

        log.info("更新交易记录请求: id={}, user={}", id, principal.getName());

        Transaction transaction = new Transaction();
        transaction.setId(id);

        Long userId = jwtUtil.getUserIdFromToken(principal.getName());
        transaction.setUserId(userId);

        if (dto.getAmount() != null) transaction.setAmount(dto.getAmount());
        if (dto.getType() != null) transaction.setType(dto.getType());
        if (dto.getCategoryId() != null) transaction.setCategoryId(dto.getCategoryId());
        if (dto.getAccountId() != null) transaction.setAccountId(dto.getAccountId());
        if (dto.getTransactionTime() != null) transaction.setTransactionTime(dto.getTransactionTime());
        if (dto.getRemark() != null) transaction.setRemark(dto.getRemark());

        boolean success = transactionService.updateTransaction(transaction);
        if (!success) {
            throw new BusinessException(500, "更新交易记录失败");
        }

        log.info("交易记录更新成功: id={}", id);
        return Result.success();
    }

    /**
     * 删除交易记录
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('TRANSACTION_DELETE')")
    @Operation(summary = "删除交易记录", description = "删除指定交易记录")
    public Result<Void> deleteTransaction(
            @Parameter(description = "交易记录ID") @PathVariable Long id,
            Principal principal) {

        log.info("删除交易记录请求: id={}, user={}", id, principal.getName());

        // 从JWT中获取用户ID
        Long userId = jwtUtil.getUserIdFromToken(principal.getName());

        boolean success = transactionService.deleteTransaction(id, userId);
        if (!success) {
            throw new BusinessException(500, "删除交易记录失败");
        }

        log.info("交易记录删除成功: id={}", id);
        return Result.success();
    }

    /**
     * 获取交易记录详情
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('TRANSACTION_VIEW')")
    @Operation(summary = "获取交易记录详情", description = "根据ID获取交易记录详情")
    public Result<Transaction> getTransactionById(@Parameter(description = "交易记录ID") @PathVariable Long id) {
        Transaction transaction = transactionService.getById(id);
        if (transaction == null) {
            throw new BusinessException(DATA_NOT_FOUND, "交易记录不存在");
        }
        return Result.success(transaction);
    }

    /**
     * 分页查询交易记录
     */
    @GetMapping
    @PreAuthorize("hasAuthority('TRANSACTION_VIEW')")
    @Operation(summary = "分页查询交易记录", description = "分页获取交易记录列表")
    public Result<IPage<Transaction>> getTransactionList(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer current,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer size,
            @Parameter(description = "开始时间") @RequestParam(required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @Parameter(description = "结束时间") @RequestParam(required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime,
            @Parameter(description = "交易类型 (1:收入, 2:支出)") @RequestParam(required = false) Integer type,
            @Parameter(description = "分类ID") @RequestParam(required = false) Long categoryId,
            Principal principal) {

        // 从JWT中获取用户ID
        Long userId = jwtUtil.getUserIdFromToken(principal.getName());
        Long companyId = jwtUtil.getCompanyIdFromToken(principal.getName());

        Page<Transaction> page = new Page<>(current, size);
        IPage<Transaction> result = transactionService.getTransactionPage(
                page, userId, companyId, startTime, endTime, type, categoryId);

        return Result.success(result);
    }

    /**
     * 获取交易汇总信息
     */
    @GetMapping("/summary")
    @PreAuthorize("hasAuthority('TRANSACTION_VIEW')")
    @Operation(summary = "获取交易汇总", description = "获取交易汇总统计信息")
    public Result<TransactionMapper.TransactionSummary> getTransactionSummary(
            @Parameter(description = "开始时间") @RequestParam(required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @Parameter(description = "结束时间") @RequestParam(required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime,
            Principal principal) {

        // 从JWT中获取用户ID
        Long userId = jwtUtil.getUserIdFromToken(principal.getName());
        Long companyId = jwtUtil.getCompanyIdFromToken(principal.getName());

        TransactionMapper.TransactionSummary summary = transactionService.getTransactionSummary(
                userId, companyId, startTime, endTime);

        return Result.success(summary);
    }

    /**
     * 按分类统计交易金额
     */
    @GetMapping("/category-summary")
    @PreAuthorize("hasAuthority('TRANSACTION_VIEW')")
    @Operation(summary = "分类统计", description = "按分类统计交易金额")
    public Result<List<TransactionMapper.CategorySummary>> getCategorySummary(
            @Parameter(description = "开始时间") @RequestParam(required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @Parameter(description = "结束时间") @RequestParam(required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime,
            @Parameter(description = "交易类型 (1:收入, 2:支出)") @RequestParam(required = false) Integer type,
            Principal principal) {

        // 从JWT中获取用户ID
        Long userId = jwtUtil.getUserIdFromToken(principal.getName());
        Long companyId = jwtUtil.getCompanyIdFromToken(principal.getName());

        List<TransactionMapper.CategorySummary> summary = transactionService.getCategorySummary(
                userId, companyId, startTime, endTime, type);

        return Result.success(summary);
    }
}
