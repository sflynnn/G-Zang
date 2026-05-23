import { Page, Locator } from '@playwright/test';

export class ReportPage {
  readonly page: Page;
  readonly title: Locator;
  readonly dateRangePicker: Locator;
  readonly periodButtons: Locator;
  readonly weekButton: Locator;
  readonly monthButton: Locator;
  readonly yearButton: Locator;
  readonly totalIncomeCard: Locator;
  readonly totalExpenseCard: Locator;
  readonly netBalanceCard: Locator;
  readonly expenseChart: Locator;
  readonly trendChart: Locator;
  readonly categoryDetail: Locator;

  constructor(page: Page) {
    this.page = page;
    this.title = page.locator('h1').filter({ hasText: /报表|报告/ });
    this.dateRangePicker = page.locator('.n-date-picker').first();
    this.periodButtons = page.locator('button').filter({ hasText: /本周|本月|本年/ });
    this.weekButton = page.locator('button').filter({ hasText: '本周' });
    this.monthButton = page.locator('button').filter({ hasText: '本月' });
    this.yearButton = page.locator('button').filter({ hasText: '本年' });
    this.totalIncomeCard = page.locator('text=总收入').locator('..');
    this.totalExpenseCard = page.locator('text=总支出').locator('..');
    this.netBalanceCard = page.locator('text=净余额').locator('..');
    this.expenseChart = page.locator('.bg-surface').filter({ has: page.locator('text=支出分类') }).locator('[class*="chart"]');
    this.trendChart = page.locator('.bg-surface').filter({ has: page.locator('text=趋势') }).locator('[class*="chart"]');
    this.categoryDetail = page.locator('.bg-surface').filter({ has: page.locator('text=分类明细') });
  }

  async goto() {
    await this.page.goto('/report');
    await this.page.waitForLoadState('networkidle');
  }

  clickWeekPeriod() {
    return this.weekButton.click();
  }

  clickMonthPeriod() {
    return this.monthButton.click();
  }

  clickYearPeriod() {
    return this.yearButton.click();
  }

  async selectCustomDateRange(start: string, end: string) {
    await this.dateRangePicker.click();
    await this.page.locator('.n-date-panel').waitFor();
    await this.page.locator('.n-date-panel-date').filter({ hasText: '1' }).first().click();
    await this.page.locator('.n-date-panel-date').filter({ hasText: '31' }).first().click();
  }

  async waitForDataRefresh() {
    await this.page.waitForTimeout(500);
  }

  async getTotalIncome(): Promise<number> {
    const text = await this.totalIncomeCard.locator('.font-mono').textContent();
    if (!text) return 0;
    const match = text.replace(/[¥+\s]/g, '').match(/[\d.]+/);
    return match ? parseFloat(match[0]) : 0;
  }

  async getTotalExpense(): Promise<number> {
    const text = await this.totalExpenseCard.locator('.font-mono').textContent();
    if (!text) return 0;
    const match = text.replace(/[¥-\s]/g, '').match(/[\d.]+/);
    return match ? parseFloat(match[0]) : 0;
  }

  async getNetBalance(): Promise<number> {
    const text = await this.netBalanceCard.locator('.font-mono').textContent();
    if (!text) return 0;
    const match = text.replace(/[¥±+\-\s]/g, '').match(/[\d.]+/);
    return match ? parseFloat(match[0]) : 0;
  }
}
