package com.gzang.mobile.service.impl;

import com.gzang.app.entity.Account;
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
            Long userId, Long companyId, LocalDateTime startTime, LocalDateTime endTime, Long bookId) {
        return transactionMapper.selectTransactionSummary(userId, companyId, startTime, endTime, bookId);
    }

    @Override
    public List<TransactionMapper.MonthlyTrendData> getMonthlyTrend(Long userId, Long companyId, Integer year, Long bookId) {
        List<TransactionMapper.MonthlyTrendData> trendData = transactionMapper.selectMonthlyTrend(userId, companyId, year);

        // 填充缺失的月份
        for (int month = 1; month <= 12; month++) {
            final int currentMonth = month;
            boolean exists = trendData.stream().anyMatch(data -> data.getMonth().equals(currentMonth));
            if (!exists) {
                TransactionMapper.MonthlyTrendData emptyData = new TransactionMapper.MonthlyTrendData();
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
    public List<TransactionMapper.CategorySummary> getCategoryReport(
            Long userId, Long companyId, LocalDateTime startTime, LocalDateTime endTime, Integer type, Long bookId) {
        return transactionMapper.selectCategorySummary(userId, companyId, startTime, endTime, type, bookId);
    }

    @Override
    public List<AccountBalanceData> getAccountBalanceReport(Long userId, Long companyId, Long bookId) {
        List<Account> accounts = accountService.getAccountsByUserId(userId, companyId);

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

            if (totalBalance.compareTo(BigDecimal.ZERO) > 0 && account.getBalance() != null) {
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
    public List<YearlyComparisonVO> getYearlyComparison(Long userId, Long companyId, List<Integer> years, Long bookId) {
        List<YearlyComparisonVO> comparisonData = new ArrayList<>();

        for (Integer year : years) {
            TransactionMapper.YearlyComparisonData raw = transactionMapper.selectYearlyComparison(userId, companyId, year);

            YearlyComparisonVO data = new YearlyComparisonVO();
            data.setYear(year);
            data.setTotalIncome(raw.getIncome());
            data.setTotalExpense(raw.getExpense());
            data.setTransactionCount(raw.getTransactionCount());

            comparisonData.add(data);
        }

        return comparisonData;
    }
}
