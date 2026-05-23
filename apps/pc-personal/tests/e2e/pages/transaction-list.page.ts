import { Page, Locator } from '@playwright/test';

export class TransactionListPage {
  readonly page: Page;
  readonly title: Locator;
  readonly addButton: Locator;
  readonly filterToolbar: Locator;
  readonly dateRangePicker: Locator;
  readonly typeSelect: Locator;
  readonly categorySelect: Locator;
  readonly accountSelect: Locator;
  readonly searchInput: Locator;
  readonly resetButton: Locator;
  readonly totalIncomeCard: Locator;
  readonly totalExpenseCard: Locator;
  readonly netBalanceCard: Locator;
  readonly transactionTable: Locator;
  readonly pagination: Locator;

  constructor(page: Page) {
    this.page = page;
    this.title = page.locator('h1').filter({ hasText: /交易/ });
    this.addButton = page.locator('button').filter({ hasText: /添加交易|记一笔/ });
    this.filterToolbar = page.locator('.bg-surface').filter({ has: page.locator('.n-date-picker') });
    this.dateRangePicker = page.locator('.n-date-picker').first();
    this.typeSelect = page.locator('.n-select').nth(0);
    this.categorySelect = page.locator('.n-select').nth(1);
    this.accountSelect = page.locator('.n-select').nth(2);
    this.searchInput = page.locator('input[placeholder*="搜索"]');
    this.resetButton = page.locator('button').filter({ hasText: '重置' });
    this.totalIncomeCard = page.locator('text=总收入').locator('..');
    this.totalExpenseCard = page.locator('text=总支出').locator('..');
    this.netBalanceCard = page.locator('text=净余额').locator('..');
    this.transactionTable = page.locator('.divide-y');
    this.pagination = page.locator('.n-pagination');
  }

  async goto() {
    await this.page.goto('/transactions');
    await this.page.waitForLoadState('networkidle');
  }

  clickAddButton() {
    return this.addButton.click();
  }

  async selectDateRange(start: string, end: string) {
    await this.dateRangePicker.click();
    await this.page.locator('.n-date-panel').waitFor();
    await this.page.locator('.n-date-panel-date').filter({ hasText: '1' }).first().click();
    await this.page.locator('.n-date-panel-date').filter({ hasText: '31' }).first().click();
  }

  async selectType(type: number) {
    await this.typeSelect.click();
    await this.page.locator('.n-base-select-option').filter({ hasText: type === 1 ? '收入' : '支出' }).click();
  }

  async selectCategory(categoryId: number) {
    await this.categorySelect.click();
    await this.page.locator('.n-base-select-option').nth(categoryId - 1).click();
  }

  async selectAccount(accountId: number) {
    await this.accountSelect.click();
    await this.page.locator('.n-base-select-option').nth(accountId - 1).click();
  }

  async search(keyword: string) {
    await this.searchInput.fill(keyword);
    await this.page.waitForTimeout(300);
  }

  async waitForDataRefresh() {
    await this.page.waitForTimeout(500);
  }

  async resetFilters() {
    await this.resetButton.click();
  }

  async goToPage(page: number) {
    await this.pagination.locator('input').fill(page.toString());
    await this.page.keyboard.press('Enter');
  }

  getFirstTransaction(): Locator {
    return this.transactionTable.locator('.grid').first();
  }

  getFirstDeleteButton(): Locator {
    return this.transactionTable.locator('button').filter({ has: this.page.locator('svg') }).last();
  }
}
