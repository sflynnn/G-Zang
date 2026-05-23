import { Page, Locator } from '@playwright/test';

export class AccountListPage {
  readonly page: Page;
  readonly title: Locator;
  readonly addButton: Locator;
  readonly totalBalance: Locator;
  readonly accountList: Locator;

  constructor(page: Page) {
    this.page = page;
    this.title = page.locator('h1').filter({ hasText: /账户/ });
    this.addButton = page.locator('button').filter({ hasText: /添加账户/ });
    this.totalBalance = page.locator('text=总资产').locator('..').locator('.text-4xl');
    this.accountList = page.locator('.bg-surface').filter({ has: page.locator('.rounded-xl') });
  }

  async goto() {
    await this.page.goto('/accounts');
    await this.page.waitForLoadState('networkidle');
  }

  clickAddButton() {
    return this.addButton.click();
  }

  getFirstAccount(): Locator {
    return this.accountList.first();
  }

  async getTotalBalance(): Promise<number> {
    const text = await this.totalBalance.textContent();
    if (!text) return 0;
    const match = text.replace(/[¥,\s]/g, '').match(/[\d.]+/);
    return match ? parseFloat(match[0]) : 0;
  }

  async fillAccountForm(data: { accountName: string; accountType: number; balance?: number }) {
    await this.page.locator('input').first().fill(data.accountName);
    
    const typeSelect = this.page.locator('.n-select').first();
    await typeSelect.click();
    await this.page.locator('.n-base-select-option').filter({ hasText: data.accountType === 1 ? '现金' : '银行卡' }).click();
    
    if (data.balance !== undefined) {
      const balanceInput = this.page.locator('.n-input-number input');
      await balanceInput.fill(data.balance.toString());
    }
  }

  submitForm() {
    return this.page.locator('button').filter({ hasText: '保存' }).click();
  }

  clickDeleteButton() {
    return this.page.locator('button').filter({ hasText: '删除' }).click();
  }
}
