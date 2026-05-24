package com.gzang.mobile.controller;

import com.gzang.app.entity.CategoryBudget;
import com.gzang.app.exception.BusinessException;
import com.gzang.app.mapper.CategoryBudgetMapper;
import com.gzang.app.util.TenantContextHolder;
import com.gzang.app.vo.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 移动端分类预算控制器
 *
 * @author G-Zang Team
 */
@RestController
@RequestMapping("/api/mobile/category-budgets")
@io.swagger.v3.oas.annotations.tags.Tag(name = "移动端分类预算管理", description = "分类月度预算管理相关接口")
public class MobileCategoryBudgetController {

    private static final Logger log = LoggerFactory.getLogger(MobileCategoryBudgetController.class);

    private final CategoryBudgetMapper categoryBudgetMapper;

    public MobileCategoryBudgetController(CategoryBudgetMapper categoryBudgetMapper) {
        this.categoryBudgetMapper = categoryBudgetMapper;
    }

    /**
     * 获取分类预算
     */
    @GetMapping
    @Operation(summary = "获取分类预算", description = "获取指定分类的月度预算信息")
    public Result<CategoryBudgetVO> getCategoryBudget(
            @Parameter(description = "分类ID") @RequestParam Long categoryId,
            @Parameter(description = "账本ID") @RequestParam(required = false) Long bookId,
            @Parameter(description = "月份 (YYYY-MM)") @RequestParam String month) {
        
        Long userId = TenantContextHolder.getUserId();
        
        // 查询预算
        CategoryBudget budget = categoryBudgetMapper.selectBudget(
            userId, bookId != null ? bookId : 0L, categoryId, month);
        
        CategoryBudgetVO vo = new CategoryBudgetVO();
        if (budget != null) {
            vo.setBudget(budget.getBudgetAmount().doubleValue());
            vo.setWarningThreshold(budget.getWarningThreshold().doubleValue());
            
            // 查询已使用金额
            BigDecimal spent = categoryBudgetMapper.selectCategorySpent(
                userId, bookId != null ? bookId : 0L, categoryId, month);
            vo.setSpent(spent != null ? spent.doubleValue() : 0);
        } else {
            vo.setBudget(0);
            vo.setSpent(0);
            vo.setWarningThreshold(80);
        }
        
        vo.setRemaining(vo.getBudget() - vo.getSpent());
        if (vo.getBudget() > 0) {
            vo.setPercentUsed(vo.getSpent() / vo.getBudget() * 100);
        } else {
            vo.setPercentUsed(0);
        }
        
        return Result.success(vo);
    }

    /**
     * 获取用户所有分类预算
     */
    @GetMapping("/list")
    @Operation(summary = "获取预算列表", description = "获取用户指定月份的所有分类预算")
    public Result<List<CategoryBudget>> getBudgetList(
            @Parameter(description = "账本ID") @RequestParam(required = false) Long bookId,
            @Parameter(description = "月份 (YYYY-MM)") @RequestParam String month) {
        
        Long userId = TenantContextHolder.getUserId();
        List<CategoryBudget> budgets = categoryBudgetMapper.selectBudgetsByMonth(
            userId, bookId != null ? bookId : 0L, month);
        return Result.success(budgets);
    }

    /**
     * 创建/更新分类预算
     */
    @PostMapping
    @Operation(summary = "创建/更新预算", description = "设置或更新分类的月度预算")
    public Result<CategoryBudget> saveBudget(@RequestBody SaveBudgetDTO dto) {
        Long userId = TenantContextHolder.getUserId();
        
        log.info("保存分类预算: userId={}, categoryId={}, yearMonth={}, amount={}", 
            userId, dto.getCategoryId(), dto.getYearMonth(), dto.getBudgetAmount());

        // 查询是否已存在
        CategoryBudget existing = categoryBudgetMapper.selectBudget(
            userId, dto.getBookId() != null ? dto.getBookId() : 0L, 
            dto.getCategoryId(), dto.getYearMonth());
        
        if (existing != null) {
            // 更新
            existing.setBudgetAmount(BigDecimal.valueOf(dto.getBudgetAmount()));
            if (dto.getWarningThreshold() != null) {
                existing.setWarningThreshold(BigDecimal.valueOf(dto.getWarningThreshold()));
            }
            existing.setUpdateTime(LocalDateTime.now());
            categoryBudgetMapper.updateById(existing);
            log.info("预算更新成功: id={}", existing.getId());
            return Result.success(existing);
        } else {
            // 创建
            CategoryBudget budget = new CategoryBudget();
            budget.setUserId(userId);
            budget.setBookId(dto.getBookId());
            budget.setCategoryId(dto.getCategoryId());
            budget.setYearMonth(dto.getYearMonth());
            budget.setBudgetAmount(BigDecimal.valueOf(dto.getBudgetAmount()));
            budget.setWarningThreshold(BigDecimal.valueOf(dto.getWarningThreshold() != null ? dto.getWarningThreshold() : 80));
            budget.setIsEnabled(1);
            budget.setCreateTime(LocalDateTime.now());
            budget.setUpdateTime(LocalDateTime.now());
            
            categoryBudgetMapper.insert(budget);
            log.info("预算创建成功: id={}", budget.getId());
            return Result.success(budget);
        }
    }

    /**
     * 删除分类预算
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除预算", description = "删除指定分类预算")
    public Result<Void> deleteBudget(@PathVariable Long id) {
        Long userId = TenantContextHolder.getUserId();
        
        CategoryBudget budget = categoryBudgetMapper.selectById(id);
        if (budget == null || !budget.getUserId().equals(userId)) {
            throw new BusinessException(400, "预算不存在或无权删除");
        }
        
        categoryBudgetMapper.deleteById(id);
        log.info("预算删除成功: id={}", id);
        return Result.success();
    }

    // DTOs
    public static class CategoryBudgetVO {
        private double budget;
        private double spent;
        private double remaining;
        private double percentUsed;
        private double warningThreshold;

        public double getBudget() { return budget; }
        public void setBudget(double budget) { this.budget = budget; }
        public double getSpent() { return spent; }
        public void setSpent(double spent) { this.spent = spent; }
        public double getRemaining() { return remaining; }
        public void setRemaining(double remaining) { this.remaining = remaining; }
        public double getPercentUsed() { return percentUsed; }
        public void setPercentUsed(double percentUsed) { this.percentUsed = percentUsed; }
        public double getWarningThreshold() { return warningThreshold; }
        public void setWarningThreshold(double warningThreshold) { this.warningThreshold = warningThreshold; }
    }

    public static class SaveBudgetDTO {
        private Long categoryId;
        private Long bookId;
        private String yearMonth;
        private double budgetAmount;
        private Double warningThreshold;

        public Long getCategoryId() { return categoryId; }
        public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }
        public Long getBookId() { return bookId; }
        public void setBookId(Long bookId) { this.bookId = bookId; }
        public String getYearMonth() { return yearMonth; }
        public void setYearMonth(String yearMonth) { this.yearMonth = yearMonth; }
        public double getBudgetAmount() { return budgetAmount; }
        public void setBudgetAmount(double budgetAmount) { this.budgetAmount = budgetAmount; }
        public Double getWarningThreshold() { return warningThreshold; }
        public void setWarningThreshold(Double warningThreshold) { this.warningThreshold = warningThreshold; }
    }
}
