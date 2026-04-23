/**
 * 报表相关 API
 * MobileReportController - /api/mobile/reports
 * 对应后端: server/gzang-mobile-api/.../MobileReportController.java
 */
import { api } from './index';
import type { TransactionType } from '@/types/transaction';

/** 收支汇总 */
export interface SummaryResult {
  totalIncome: number;
  totalExpense: number;
  balance: number;
  transactionCount: number;
  incomeCount?: number;
  expenseCount?: number;
}

/** 月度趋势数据 */
export interface MonthlyTrendItem {
  month: number;
  income: number;
  expense: number;
  balance?: number;
}

/** 分类统计 */
export interface CategoryReportItem {
  categoryId: number;
  categoryName: string;
  totalAmount: number;
  count: number;
  percentage?: number;
  avgAmount?: number;
  trend?: 'up' | 'down' | 'stable';
}

/** 账户余额数据 */
export interface AccountBalanceItem {
  accountId: number;
  accountName: string;
  accountType: string;
  balance: number;
  percentage?: number;
}

/** 年度对比数据 */
export interface YearlyComparisonItem {
  year: number;
  income: number;
  expense: number;
  balance: number;
  transactionCount: number;
}

/**
 * 获取收支汇总报表
 * GET /api/mobile/reports/summary
 */
export async function getSummary(params?: {
  startTime?: string;
  endTime?: string;
  bookId?: number;
}): Promise<SummaryResult> {
  return api.get('/reports/summary', params);
}

/**
 * 获取月度收支趋势
 * GET /api/mobile/reports/monthly-trend?year=2026
 * 后端需要 year 参数
 */
export async function getMonthlyTrend(params: {
  year: number;
  bookId?: number;
}): Promise<MonthlyTrendItem[]> {
  return api.get('/reports/monthly-trend', params);
}

/**
 * 获取分类统计报表
 * GET /api/mobile/reports/category-report
 */
export async function getCategoryReport(params?: {
  startTime?: string;
  endTime?: string;
  type?: TransactionType;
  bookId?: number;
}): Promise<CategoryReportItem[]> {
  return api.get('/reports/category-report', params);
}

/**
 * 获取账户余额报表
 * GET /api/mobile/reports/account-balance
 */
export async function getAccountBalance(params?: {
  bookId?: number;
}): Promise<AccountBalanceItem[]> {
  return api.get('/reports/account-balance', params);
}

/**
 * 获取多年度收支对比
 * GET /api/mobile/reports/yearly-comparison?years=2025,2026
 */
export async function getYearlyComparison(params: {
  years: number[];
  bookId?: number;
}): Promise<YearlyComparisonItem[]> {
  return api.get('/reports/yearly-comparison', { years: params.years.join(','), bookId: params.bookId });
}

export const reportApi = {
  getSummary,
  getMonthlyTrend,
  getCategoryReport,
  getAccountBalance,
  getYearlyComparison,
};

export default reportApi;
