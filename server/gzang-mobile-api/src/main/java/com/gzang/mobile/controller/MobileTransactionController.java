package com.gzang.mobile.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gzang.app.util.TenantContextHolder;
import com.gzang.app.dto.transaction.CreateTransactionDTO;
import com.gzang.app.dto.transaction.UpdateTransactionDTO;
import com.gzang.app.entity.Transaction;
import com.gzang.app.exception.BusinessException;
import com.gzang.app.mapper.TransactionMapper;
import com.gzang.mobile.service.TransactionService;
import com.gzang.app.vo.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 移动端交易控制器
 * 移动端 + PC个人记账 共用的交易记录接口
 *
 * @author G-Zang Team
 */
@RestController
@RequestMapping("/api/mobile/transactions")
@Tag(name = "移动端交易管理", description = "交易记录、记账管理相关接口")
public class MobileTransactionController {

    private static final Logger log = LoggerFactory.getLogger(MobileTransactionController.class);

    private final TransactionService transactionService;

    public MobileTransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    /**
     * 创建交易记录
     */
    @PostMapping
    @Operation(summary = "创建交易记录", description = "新增一笔交易记录")
    public Result<Void> createTransaction(@Validated @RequestBody CreateTransactionDTO dto) {
        Long userId = TenantContextHolder.getUserId();
        Long companyId = TenantContextHolder.getCompanyId();

        log.info("创建交易记录请求: userId={}, amount={}, type={}", userId, dto.getAmount(), dto.getType());

        Transaction transaction = new Transaction();
        transaction.setAmount(dto.getAmount());
        transaction.setType(dto.getType());
        transaction.setCategoryId(dto.getCategoryId());
        transaction.setAccountId(dto.getAccountId());
        transaction.setBookId(dto.getBookId());
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
    @Operation(summary = "更新交易记录", description = "更新指定交易记录")
    public Result<Void> updateTransaction(
            @Parameter(description = "交易记录ID") @PathVariable Long id,
            @Validated @RequestBody UpdateTransactionDTO dto) {

        Long userId = TenantContextHolder.getUserId();
        log.info("更新交易记录请求: id={}, userId={}", id, userId);

        Transaction transaction = new Transaction();
        transaction.setId(id);
        transaction.setUserId(userId);

        if (dto.getAmount() != null) transaction.setAmount(dto.getAmount());
        if (dto.getType() != null) transaction.setType(dto.getType());
        if (dto.getCategoryId() != null) transaction.setCategoryId(dto.getCategoryId());
        if (dto.getAccountId() != null) transaction.setAccountId(dto.getAccountId());
        if (dto.getBookId() != null) transaction.setBookId(dto.getBookId());
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
    @Operation(summary = "删除交易记录", description = "删除指定交易记录")
    public Result<Void> deleteTransaction(
            @Parameter(description = "交易记录ID") @PathVariable Long id) {

        Long userId = TenantContextHolder.getUserId();
        log.info("删除交易记录请求: id={}, userId={}", id, userId);

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
    @Operation(summary = "获取交易记录详情", description = "根据ID获取交易记录详情")
    public Result<Transaction> getTransactionById(
            @Parameter(description = "交易记录ID") @PathVariable Long id) {
        Transaction transaction = transactionService.getById(id);
        if (transaction == null) {
            throw new BusinessException(404, "交易记录不存在");
        }
        return Result.success(transaction);
    }

    /**
     * 分页查询交易记录
     */
    @GetMapping
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
            @Parameter(description = "账本ID") @RequestParam(required = false) Long bookId) {

        Long userId = TenantContextHolder.getUserId();
        Long companyId = TenantContextHolder.getCompanyId();

        Page<Transaction> page = new Page<>(current, size);
        IPage<Transaction> result = transactionService.getTransactionPage(
                page, userId, companyId, startTime, endTime, type, categoryId, bookId);

        return Result.success(result);
    }

    /**
     * 获取交易汇总信息
     */
    @GetMapping("/summary")
    @Operation(summary = "获取交易汇总", description = "获取交易汇总统计信息")
    public Result<TransactionMapper.TransactionSummary> getTransactionSummary(
            @Parameter(description = "开始时间") @RequestParam(required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @Parameter(description = "结束时间") @RequestParam(required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime,
            @Parameter(description = "账本ID") @RequestParam(required = false) Long bookId) {

        Long userId = TenantContextHolder.getUserId();
        Long companyId = TenantContextHolder.getCompanyId();

        TransactionMapper.TransactionSummary summary = transactionService.getTransactionSummary(
                userId, companyId, startTime, endTime, bookId);

        return Result.success(summary);
    }

    /**
     * 按分类统计交易金额
     */
    @GetMapping("/category-summary")
    @Operation(summary = "分类统计", description = "按分类统计交易金额")
    public Result<List<TransactionMapper.CategorySummary>> getCategorySummary(
            @Parameter(description = "开始时间") @RequestParam(required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @Parameter(description = "结束时间") @RequestParam(required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime,
            @Parameter(description = "交易类型 (1:收入, 2:支出)") @RequestParam(required = false) Integer type,
            @Parameter(description = "账本ID") @RequestParam(required = false) Long bookId) {

        Long userId = TenantContextHolder.getUserId();
        Long companyId = TenantContextHolder.getCompanyId();

        List<TransactionMapper.CategorySummary> summary = transactionService.getCategorySummary(
                userId, companyId, startTime, endTime, type, bookId);

        return Result.success(summary);
    }
}
