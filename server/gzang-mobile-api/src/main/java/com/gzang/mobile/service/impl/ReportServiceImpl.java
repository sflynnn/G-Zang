package com.gzang.mobile.service.impl;

import com.gzang.app.entity.Account;
import com.gzang.app.entity.Category;
import com.gzang.app.mapper.TransactionMapper;
import com.gzang.mobile.service.AccountService;
import com.gzang.mobile.service.CategoryService;
import com.gzang.mobile.service.ReportService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 报表统计服务实现类
 *
 * @author G-Zang Team
 */
@Slf4j
@Service
public class ReportServiceImpl implements ReportService {

    private final TransactionMapper transactionMapper;
    private final AccountService accountService;
    private final CategoryService categoryService;

    public ReportServiceImpl(TransactionMapper transactionMapper, AccountService accountService, CategoryService categoryService) {
        this.transactionMapper = transactionMapper;
        this.accountService = accountService;
        this.categoryService = categoryService;
    }

    @Override
    public TransactionMapper.TransactionSummary getIncomeExpenseSummary(
            Long userId, Long companyId, LocalDateTime startTime, LocalDateTime endTime) {

        return transactionMapper.selectTransactionSummary(userId, companyId, startTime, endTime);
    }

    @Override
    public List<MonthlyTrendData> getMonthlyTrend(Long userId, Long companyId, Integer year) {
        List<TransactionMapper.MonthlyTrendData> rawData = transactionMapper.selectMonthlyTrend(userId, companyId, year);

        // 转换为 DTO
        List<MonthlyTrendData> trendData = new ArrayList<>();
        for (TransactionMapper.MonthlyTrendData item : rawData) {
            MonthlyTrendData dto = new MonthlyTrendData();
            dto.setMonth(item.getMonth());
            dto.setIncome(item.getIncome());
            dto.setExpense(item.getExpense());
            trendData.add(dto);
        }

        // 填充缺失的月份
        for (int month = 1; month <= 12; month++) {
            final int currentMonth = month;
            boolean exists = trendData.stream().anyMatch(data -> data.getMonth().equals(currentMonth));
            if (!exists) {
                MonthlyTrendData emptyData = new MonthlyTrendData();
                emptyData.setMonth(currentMonth);
                emptyData.setIncome(BigDecimal.ZERO);
                emptyData.setExpense(BigDecimal.ZERO);
                trendData.add(emptyData);
            }
        }

        trendData.sort((a, b) -> a.getMonth().compareTo(b.getMonth()));
        return trendData;
    }

    @Override
    public List<CategoryReportData> getCategoryReport(
            Long userId, Long companyId, LocalDateTime startTime, LocalDateTime endTime, Integer type) {

        List<TransactionMapper.CategorySummary> summaries =
                transactionMapper.selectCategorySummary(userId, companyId, startTime, endTime, type);

        // 计算总金额
        BigDecimal totalAmount = summaries.stream()
                .map(TransactionMapper.CategorySummary::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<CategoryReportData> reportData = new ArrayList<>();
        for (TransactionMapper.CategorySummary summary : summaries) {
            CategoryReportData data = new CategoryReportData();
            data.setCategoryId(summary.getCategoryId());
            data.setTotalAmount(summary.getTotalAmount());
            data.setTransactionCount(summary.getCount());

            // 计算百分比
            if (totalAmount.compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal percentage = summary.getTotalAmount()
                        .divide(totalAmount, 4, RoundingMode.HALF_UP)
                        .multiply(new BigDecimal("100"));
                data.setPercentage(percentage);
            }

            // 获取分类信息
            Category category = categoryService.getById(summary.getCategoryId());
            if (category != null) {
                data.setCategoryName(category.getCategoryName());
                data.setType(category.getType());
            }

            reportData.add(data);
        }

        return reportData;
    }

    @Override
    public List<AccountBalanceData> getAccountBalanceReport(Long userId, Long companyId) {
        List<Account> accounts = accountService.getAccountsByUserId(userId, companyId);

        // 计算总余额
        BigDecimal totalBalance = accounts.stream()
                .map(Account::getBalance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<AccountBalanceData> reportData = new ArrayList<>();
        for (Account account : accounts) {
            AccountBalanceData data = new AccountBalanceData();
            data.setAccountId(account.getId());
            data.setAccountName(account.getAccountName());
            data.setAccountType(account.getAccountType());
            data.setBalance(account.getBalance());

            // 计算百分比
            if (totalBalance.compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal percentage = account.getBalance()
                        .divide(totalBalance, 4, RoundingMode.HALF_UP)
                        .multiply(new BigDecimal("100"));
                data.setPercentage(percentage);
            }

            reportData.add(data);
        }

        return reportData;
    }

    @Override
    public List<YearlyComparisonData> getYearlyComparison(Long userId, Long companyId, List<Integer> years) {
        List<YearlyComparisonData> comparisonData = new ArrayList<>();

        for (Integer year : years) {
            TransactionMapper.YearlyComparisonData raw = transactionMapper.selectYearlyComparison(userId, companyId, year);

            YearlyComparisonData data = new YearlyComparisonData();
            data.setYear(year);
            data.setTotalIncome(raw.getIncome());
            data.setTotalExpense(raw.getExpense());
            data.setTransactionCount(raw.getTransactionCount());

            comparisonData.add(data);
        }

        return comparisonData;
    }
}
