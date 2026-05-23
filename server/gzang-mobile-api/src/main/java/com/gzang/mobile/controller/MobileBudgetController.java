package com.gzang.mobile.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gzang.app.converter.BudgetConverter;
import com.gzang.app.dto.budget.CreateBudgetDTO;
import com.gzang.app.dto.budget.UpdateBudgetDTO;
import com.gzang.app.entity.Budget;
import com.gzang.app.exception.BusinessException;
import com.gzang.app.service.BudgetService;
import com.gzang.app.util.TenantContextHolder;
import com.gzang.app.vo.BudgetVO;
import com.gzang.app.vo.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

/**
 * 移动端预算控制器
 * 提供预算管理相关接口
 *
 * @author G-Zang Team
 */
@RestController
@RequestMapping("/api/mobile/budgets")
@Tag(name = "移动端预算管理", description = "预算管理相关接口")
public class MobileBudgetController {

    private static final Logger log = LoggerFactory.getLogger(MobileBudgetController.class);

    private final BudgetService budgetService;

    public MobileBudgetController(BudgetService budgetService) {
        this.budgetService = budgetService;
    }

    /**
     * 获取预算列表
     */
    @GetMapping
    @Operation(summary = "获取预算列表", description = "获取当前用户的预算列表")
    public Result<List<BudgetVO>> getBudgetList(
            @Parameter(description = "账本ID") @RequestParam(required = false) Long bookId,
            @Parameter(description = "周期类型：1=月预算, 2=年预算, 3=周预算") @RequestParam(required = false) Integer periodType) {

        Long userId = TenantContextHolder.getUserId();
        Long companyId = TenantContextHolder.getCompanyId();

        List<Budget> budgets = budgetService.getBudgetsByUserId(userId, companyId, bookId, periodType);
        List<BudgetVO> vos = convertToVOList(budgets, userId, companyId);

        return Result.success(vos);
    }

    /**
     * 获取预算详情
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取预算详情", description = "根据ID获取预算详情")
    public Result<BudgetVO> getBudgetById(
            @Parameter(description = "预算ID") @PathVariable Long id) {

        Budget budget = budgetService.getById(id);
        if (budget == null) {
            throw new BusinessException(404, "预算不存在");
        }

        Long userId = TenantContextHolder.getUserId();
        Long companyId = TenantContextHolder.getCompanyId();

        BudgetVO vo = convertToVO(budget, userId, companyId);
        return Result.success(vo);
    }

    /**
     * 创建预算
     */
    @PostMapping
    @Operation(summary = "创建预算", description = "新增一个预算")
    public Result<Void> createBudget(@Validated @RequestBody CreateBudgetDTO dto) {
        Long userId = TenantContextHolder.getUserId();
        Long companyId = TenantContextHolder.getCompanyId();

        log.info("创建预算请求: userId={}, name={}", userId, dto.getName());

        Budget budget = new Budget();
        budget.setName(dto.getName());
        budget.setBookId(dto.getBookId());
        budget.setCategoryId(dto.getCategoryId());
        budget.setAmount(dto.getAmount());
        budget.setPeriodType(dto.getPeriodType());
        budget.setPeriodStart(dto.getPeriodStart());
        budget.setPeriodEnd(dto.getPeriodEnd());
        budget.setWarningThreshold(dto.getWarningThreshold() != null ? dto.getWarningThreshold() : 80);
        budget.setWarningEnabled(dto.getWarningEnabled() != null ? dto.getWarningEnabled() : true);
        budget.setRemark(dto.getRemark());
        budget.setUsedAmount(BigDecimal.ZERO);
        budget.setUserId(userId);
        budget.setCompanyId(companyId);

        boolean success = budgetService.createBudget(budget);
        if (!success) {
            throw new BusinessException(400, "创建预算失败");
        }

        log.info("预算创建成功: id={}", budget.getId());
        return Result.success();
    }

    /**
     * 更新预算
     */
    @PutMapping("/{id}")
    @Operation(summary = "更新预算", description = "更新指定预算信息")
    public Result<Void> updateBudget(
            @Parameter(description = "预算ID") @PathVariable Long id,
            @Validated @RequestBody UpdateBudgetDTO dto) {

        Long userId = TenantContextHolder.getUserId();

        log.info("更新预算请求: id={}, userId={}", id, userId);

        Budget existing = budgetService.getById(id);
        if (existing == null) {
            throw new BusinessException(404, "预算不存在");
        }

        Budget budget = new Budget();
        budget.setId(id);
        budget.setUserId(userId);

        if (dto.getName() != null) {
            budget.setName(dto.getName());
        }
        if (dto.getAmount() != null) {
            budget.setAmount(dto.getAmount());
        }
        if (dto.getPeriodStart() != null) {
            budget.setPeriodStart(dto.getPeriodStart());
        }
        if (dto.getPeriodEnd() != null) {
            budget.setPeriodEnd(dto.getPeriodEnd());
        }
        if (dto.getWarningThreshold() != null) {
            budget.setWarningThreshold(dto.getWarningThreshold());
        }
        if (dto.getWarningEnabled() != null) {
            budget.setWarningEnabled(dto.getWarningEnabled());
        }
        if (dto.getRemark() != null) {
            budget.setRemark(dto.getRemark());
        }

        boolean success = budgetService.updateBudget(budget);
        if (!success) {
            throw new BusinessException(400, "更新预算失败");
        }

        log.info("预算更新成功: id={}", id);
        return Result.success();
    }

    /**
     * 删除预算
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除预算", description = "删除指定预算")
    public Result<Void> deleteBudget(
            @Parameter(description = "预算ID") @PathVariable Long id) {

        Long userId = TenantContextHolder.getUserId();
        log.info("删除预算请求: id={}, userId={}", id, userId);

        boolean success = budgetService.deleteBudget(id, userId);
        if (!success) {
            throw new BusinessException(400, "删除预算失败");
        }

        log.info("预算删除成功: id={}", id);
        return Result.success();
    }

    /**
     * 分页查询预算
     */
    @GetMapping("/page")
    @Operation(summary = "分页查询预算", description = "分页获取预算列表")
    public Result<IPage<BudgetVO>> getBudgetPage(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer current,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer size,
            @Parameter(description = "账本ID") @RequestParam(required = false) Long bookId,
            @Parameter(description = "周期类型") @RequestParam(required = false) Integer periodType) {

        Long userId = TenantContextHolder.getUserId();
        Long companyId = TenantContextHolder.getCompanyId();

        Page<Budget> page = new Page<>(current, size);
        IPage<Budget> result = budgetService.getBudgetPage(page, userId, companyId, bookId, periodType);

        IPage<BudgetVO> voPage = result.convert(BudgetConverter::toVO);

        return Result.success(voPage);
    }

    /**
     * 获取预警预算列表
     */
    @GetMapping("/warnings")
    @Operation(summary = "获取预警预算", description = "获取所有触发预警的预算")
    public Result<List<BudgetVO>> getWarningBudgets() {
        Long userId = TenantContextHolder.getUserId();
        Long companyId = TenantContextHolder.getCompanyId();

        List<Budget> budgets = budgetService.getWarningBudgets(userId, companyId);
        List<BudgetVO> vos = convertToVOList(budgets, userId, companyId);

        return Result.success(vos);
    }

    /**
     * 刷新预算使用金额
     */
    @PostMapping("/{id}/refresh-used")
    @Operation(summary = "刷新预算使用金额", description = "根据交易记录重新计算预算已使用金额")
    public Result<Void> refreshUsedAmount(
            @Parameter(description = "预算ID") @PathVariable Long id) {

        Long userId = TenantContextHolder.getUserId();
        log.info("刷新预算使用金额: id={}, userId={}", id, userId);

        Budget budget = budgetService.getById(id);
        if (budget == null) {
            throw new BusinessException(404, "预算不存在");
        }

        BigDecimal usedAmount = budgetService.getUsedAmount(
                userId,
                TenantContextHolder.getCompanyId(),
                budget.getCategoryId(),
                budget.getBookId(),
                budget.getPeriodStart(),
                budget.getPeriodEnd()
        );

        budgetService.updateUsedAmount(id, usedAmount);

        log.info("预算使用金额刷新成功: id={}, usedAmount={}", id, usedAmount);
        return Result.success();
    }

    private BudgetVO convertToVO(Budget budget, Long userId, Long companyId) {
        return BudgetConverter.toVO(budget);
    }

    private List<BudgetVO> convertToVOList(List<Budget> budgets, Long userId, Long companyId) {
        if (budgets == null || budgets.isEmpty()) {
            return List.of();
        }
        return budgets.stream()
                .map(BudgetConverter::toVO)
                .toList();
    }
}
