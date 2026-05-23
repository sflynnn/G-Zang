import { Page, Locator } from '@playwright/test';

export class CategoryListPage {
  readonly page: Page;
  readonly title: Locator;
  readonly addButton: Locator;
  readonly tabAll: Locator;
  readonly tabExpense: Locator;
  readonly tabIncome: Locator;
  readonly categoryGrid: Locator;

  constructor(page: Page) {
    this.page = page;
    this.title = page.locator('h1').filter({ hasText: /分类/ });
    this.addButton = page.locator('button').filter({ hasText: /添加分类/ });
    this.tabAll = page.locator('button').filter({ hasText: '全部' });
    this.tabExpense = page.locator('button').filter({ hasText: '支出' });
    this.tabIncome = page.locator('button').filter({ hasText: '收入' });
    this.categoryGrid = page.locator('.grid').filter({ has: page.locator('.rounded-2xl') });
  }

  async goto() {
    await this.page.goto('/categories');
    await this.page.waitForLoadState('networkidle');
  }

  clickAddButton() {
    return this.addButton.click();
  }

  clickExpenseTab() {
    return this.tabExpense.click();
  }

  clickIncomeTab() {
    return this.tabIncome.click();
  }

  getFirstCategory(): Locator {
    return this.categoryGrid.locator('.bg-surface').first();
  }

  async fillCategoryForm(data: { categoryName: string; type: number; icon?: string; color?: string }) {
    const nameInput = this.page.locator('input').first();
    await nameInput.fill(data.categoryName);
    
    const typeSelect = this.page.locator('.n-select').first();
    await typeSelect.click();
    await this.page.locator('.n-base-select-option').filter({ hasText: data.type === 1 ? '收入' : '支出' }).click();
    
    if (data.icon) {
      const iconSelect = this.page.locator('.n-select').nth(1);
      await iconSelect.click();
      await this.page.locator('.n-base-select-option').filter({ hasText: data.icon }).click();
    }
    
    if (data.color) {
      await this.page.locator('button').filter({ hasText: data.color }).or(
        this.page.locator(`button[style*="${data.color}"]`)
      ).first().click();
    }
  }

  submitForm() {
    return this.page.locator('button').filter({ hasText: '保存' }).click();
  }
}
