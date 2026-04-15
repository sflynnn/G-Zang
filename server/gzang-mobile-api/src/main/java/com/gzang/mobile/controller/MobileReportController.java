package com.gzang.mobile.controller;

import com.gzang.app.util.JwtUtil;
import com.gzang.app.mapper.TransactionMapper;
import com.gzang.app.service.ReportService;
import com.gzang.app.vo.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

/**
 * 移动端报表控制器
 * 移动端 + PC个人记账 共用的报表统计接口
 *
 * @author G-Zang Team
 */
@RestController
@RequestMapping("/api/mobile/reports")
@Tag(name = "移动端报表统计", description = "财务报表统计相关接口")
public class MobileReportController {

    private final ReportService reportService;
    private final JwtUtil jwtUtil;

    public MobileReportController(ReportService reportService, JwtUtil jwtUtil) {
        this.reportService = reportService;
        this.jwtUtil = jwtUtil;
    }

    /**
     * 获取收支汇总报表
     */
    @GetMapping("/summary")
    @Operation(summary = "收支汇总", description = "获取收入支出汇总统计")
    public Result<TransactionMapper.TransactionSummary> getIncomeExpenseSummary(
            @Parameter(description = "开始时间") @RequestParam(required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @Parameter(description = "结束时间") @RequestParam(required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime,
            Principal principal) {

        Long userId = jwtUtil.getUserIdFromToken(principal.getName());
        Long companyId = jwtUtil.getCompanyIdFromToken(principal.getName());

        var summary = reportService.getIncomeExpenseSummary(userId, companyId, startTime, endTime);
        return Result.success(summary);
    }

    /**
     * 获取月度收支趋势
     */
    @GetMapping("/monthly-trend")
    @Operation(summary = "月度趋势", description = "获取指定年份的月度收支趋势")
    public Result<List<TransactionMapper.MonthlyTrendData>> getMonthlyTrend(
            @Parameter(description = "年份") @RequestParam Integer year,
            Principal principal) {

        Long userId = jwtUtil.getUserIdFromToken(principal.getName());
        Long companyId = jwtUtil.getCompanyIdFromToken(principal.getName());

        List<TransactionMapper.MonthlyTrendData> trendData = reportService.getMonthlyTrend(userId, companyId, year);
        return Result.success(trendData);
    }

    /**
     * 获取分类统计报表
     */
    @GetMapping("/category-report")
    @Operation(summary = "分类统计", description = "按分类统计收支情况")
    public Result<List<TransactionMapper.CategorySummary>> getCategoryReport(
            @Parameter(description = "开始时间") @RequestParam(required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @Parameter(description = "结束时间") @RequestParam(required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime,
            @Parameter(description = "交易类型 (1:收入, 2:支出)") @RequestParam(required = false) Integer type,
            Principal principal) {

        Long userId = jwtUtil.getUserIdFromToken(principal.getName());
        Long companyId = jwtUtil.getCompanyIdFromToken(principal.getName());

        List<TransactionMapper.CategorySummary> reportData = reportService.getCategoryReport(
                userId, companyId, startTime, endTime, type);
        return Result.success(reportData);
    }

    /**
     * 获取账户余额报表
     */
    @GetMapping("/account-balance")
    @Operation(summary = "账户余额", description = "获取账户余额统计")
    public Result<List<ReportService.AccountBalanceData>> getAccountBalanceReport(Principal principal) {
        Long userId = jwtUtil.getUserIdFromToken(principal.getName());
        Long companyId = jwtUtil.getCompanyIdFromToken(principal.getName());

        List<ReportService.AccountBalanceData> balanceData = reportService.getAccountBalanceReport(userId, companyId);
        return Result.success(balanceData);
    }

    /**
     * 获取年度收支对比
     */
    @GetMapping("/yearly-comparison")
    @Operation(summary = "年度对比", description = "多年度收支对比分析")
    public Result<List<TransactionMapper.YearlyComparisonData>> getYearlyComparison(
            @Parameter(description = "年份列表，用逗号分隔") @RequestParam String years,
            Principal principal) {

        Long userId = jwtUtil.getUserIdFromToken(principal.getName());
        Long companyId = jwtUtil.getCompanyIdFromToken(principal.getName());

        List<Integer> yearList = Arrays.stream(years.split(","))
                .map(String::trim)
                .map(Integer::parseInt)
                .toList();

        List<TransactionMapper.YearlyComparisonData> comparisonData = reportService.getYearlyComparison(
                userId, companyId, yearList);
        return Result.success(comparisonData);
    }
}
