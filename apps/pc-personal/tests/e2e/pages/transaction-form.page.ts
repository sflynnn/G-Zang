import { Page, Locator } from '@playwright/test';

export class TransactionFormPage {
  readonly page: Page;
  readonly title: Locator;
  readonly amountInput: Locator;
  readonly typeSelector: Locator;
  readonly incomeButton: Locator;
  readonly expenseButton: Locator;
  readonly categorySelect: Locator;
  readonly accountSelect: Locator;
  readonly timePicker: Locator;
  readonly remarkInput: Locator;
  readonly submitButton: Locator;
  readonly deleteButton: Locator;
  readonly amountError: Locator;

  constructor(page: Page) {
    this.page = page;
    this.title = page.locator('h1');
    this.amountInput = page.locator('.n-input-number input');
    this.typeSelector = page.locator('[class*="type"]');
    this.incomeButton = page.locator('button').filter({ hasText: /收入/ });
    this.expenseButton = page.locator('button').filter({ hasText: /支出/ });
    this.categorySelect = page.locator('.n-select').filter({ has: page.locator('text=分类') }).locator('.n-base-selection');
    this.accountSelect = page.locator('.n-select').filter({ has: page.locator('text=账户') }).locator('.n-base-selection');
    this.timePicker = page.locator('.n-date-picker');
    this.remarkInput = page.locator('textarea');
    this.submitButton = page.locator('button[type="submit"], button').filter({ hasText: '保存' });
    this.deleteButton = page.locator('button').filter({ hasText: '删除' });
    this.amountError = page.locator('.n-form-item-feedback-wrapper').filter({ hasText: /金额/ }).locator('.n-form-item-feedback');
  }

  async gotoAdd() {
    await this.page.goto('/transaction/add');
    await this.page.waitForLoadState('networkidle');
  }

  async gotoEdit(id: number) {
    await this.page.goto(`/transaction/edit/${id}`);
    await this.page.waitForLoadState('networkidle');
  }

  async selectIncomeType() {
    await this.incomeButton.click();
  }

  async selectExpenseType() {
    await this.expenseButton.click();
  }

  async fillAmount(amount: number) {
    await this.amountInput.fill(amount.toString());
  }

  async selectCategory(categoryName: string) {
    await this.categorySelect.click();
    await this.page.locator('.n-base-select-option').filter({ hasText: categoryName }).click();
  }

  async selectAccount(accountName: string) {
    await this.accountSelect.click();
    await this.page.locator('.n-base-select-option').filter({ hasText: accountName }).click();
  }

  async selectTime() {
    await this.timePicker.click();
    await this.page.locator('.n-date-panel').waitFor();
    await this.page.locator('.n-date-panel-time-btn').click();
    await this.page.locator('.n-button').filter({ hasText: '确定' }).last().click();
  }

  async fillRemark(remark: string) {
    await this.remarkInput.fill(remark);
  }

  async fillForm(data: { amount: number; type: number; category: number; account: number; remark?: string }) {
    if (data.type === 1) {
      await this.selectIncomeType();
    } else {
      await this.selectExpenseType();
    }
    
    await this.fillAmount(data.amount);
    
    const categoryOption = await this.page.locator('.n-base-select-option').nth(data.category - 1).textContent();
    if (categoryOption) {
      await this.selectCategory(categoryOption);
    }
    
    const accountOption = await this.page.locator('.n-base-select-option').nth(data.account - 1).textContent();
    if (accountOption) {
      await this.selectAccount(accountOption);
    }
    
    if (data.remark) {
      await this.fillRemark(data.remark);
    }
  }

  async submitForm() {
    await this.submitButton.click();
    await this.page.waitForLoadState('networkidle');
  }

  async submitFormWithoutData() {
    await this.submitForm();
  }

  async getCategoryOptions(): Promise<string[]> {
    await this.categorySelect.click();
    await this.page.locator('.n-base-select-menu').waitFor();
    const options = await this.page.locator('.n-base-select-option').allTextContents();
    await this.page.keyboard.press('Escape');
    return options;
  }

  async deleteTransaction() {
    await this.deleteButton.click();
    await this.page.locator('.n-popconfirm .n-button').filter({ hasText: '确定' }).click();
  }
}
