import { test, expect } from '@playwright/test';
import { AccountListPage } from './pages/account-list.page';
import { CategoryListPage } from './pages/category-list.page';
import { TransactionListPage } from './pages/transaction-list.page';
import { TransactionFormPage } from './pages/transaction-form.page';
import { ReportPage } from './pages/report.page';

test.describe('PC 个人端 - 账户管理', () => {
  let accountListPage: AccountListPage;

  test.beforeEach(async ({ page }) => {
    accountListPage = new AccountListPage(page);
    await accountListPage.goto();
  });

  test('应该正确显示账户列表页面', async () => {
    await expect(accountListPage.title).toBeVisible();
    await expect(accountListPage.addButton).toBeVisible();
    await expect(accountListPage.totalBalance).toBeVisible();
  });

  test('应该能够添加新账户', async ({ page }) => {
    await accountListPage.clickAddButton();
    await accountListPage.fillAccountForm({
      accountName: '测试账户',
      accountType: 2,
      balance: 1000
    });
    await accountListPage.submitForm();
    await expect(page.locator('.n-message')).toContainText('创建成功');
  });

  test('应该能够编辑现有账户', async ({ page }) => {
    const firstAccount = accountListPage.getFirstAccount();
    await firstAccount.click();
    await accountListPage.fillAccountForm({ accountName: '更新后的账户名' });
    await accountListPage.submitForm();
    await expect(page.locator('.n-message')).toContainText('更新成功');
  });

  test('应该能够删除账户', async ({ page }) => {
    const firstAccount = accountListPage.getFirstAccount();
    await firstAccount.click();
    await accountListPage.clickDeleteButton();
    await page.locator('.n-button').filter({ hasText: '确定' }).click();
    await expect(page.locator('.n-message')).toContainText('删除成功');
  });

  test('应该正确显示总资产', async () => {
    const totalBalance = await accountListPage.getTotalBalance();
    expect(typeof totalBalance).toBe('number');
  });
});

test.describe('PC 个人端 - 分类管理', () => {
  let categoryListPage: CategoryListPage;

  test.beforeEach(async ({ page }) => {
    categoryListPage = new CategoryListPage(page);
    await categoryListPage.goto();
  });

  test('应该正确显示分类列表', async () => {
    await expect(categoryListPage.title).toBeVisible();
    await expect(categoryListPage.addButton).toBeVisible();
    await expect(categoryListPage.tabAll).toBeVisible();
    await expect(categoryListPage.tabExpense).toBeVisible();
    await expect(categoryListPage.tabIncome).toBeVisible();
  });

  test('应该能够按类型筛选分类', async () => {
    await categoryListPage.clickExpenseTab();
    await expect(categoryListPage.tabExpense).toHaveClass(/active/);
    
    await categoryListPage.clickIncomeTab();
    await expect(categoryListPage.tabIncome).toHaveClass(/active/);
  });

  test('应该能够添加新分类', async ({ page }) => {
    await categoryListPage.clickAddButton();
    await categoryListPage.fillCategoryForm({
      categoryName: '测试分类',
      type: 2,
      icon: '📦',
      color: '#0F4C5C'
    });
    await categoryListPage.submitForm();
    await expect(page.locator('.n-message')).toContainText('创建成功');
  });

  test('应该能够编辑分类', async ({ page }) => {
    const firstCategory = categoryListPage.getFirstCategory();
    await firstCategory.click();
    await categoryListPage.fillCategoryForm({ categoryName: '更新后的分类' });
    await categoryListPage.submitForm();
    await expect(page.locator('.n-message')).toContainText('更新成功');
  });

  test('应该显示分类图标和颜色', async () => {
    const firstCategory = categoryListPage.getFirstCategory();
    await expect(firstCategory.locator('.category-icon')).toBeVisible();
    await expect(firstCategory.locator('.category-name')).toBeVisible();
  });
});

test.describe('PC 个人端 - 交易记录', () => {
  let transactionListPage: TransactionListPage;

  test.beforeEach(async ({ page }) => {
    transactionListPage = new TransactionListPage(page);
    await transactionListPage.goto();
  });

  test('应该正确显示交易列表页面', async () => {
    await expect(transactionListPage.title).toBeVisible();
    await expect(transactionListPage.addButton).toBeVisible();
    await expect(transactionListPage.filterToolbar).toBeVisible();
  });

  test('应该显示筛选工具栏', async () => {
    await expect(transactionListPage.dateRangePicker).toBeVisible();
    await expect(transactionListPage.typeSelect).toBeVisible();
    await expect(transactionListPage.categorySelect).toBeVisible();
    await expect(transactionListPage.accountSelect).toBeVisible();
    await expect(transactionListPage.searchInput).toBeVisible();
  });

  test('应该显示统计概览', async () => {
    await expect(transactionListPage.totalIncomeCard).toBeVisible();
    await expect(transactionListPage.totalExpenseCard).toBeVisible();
    await expect(transactionListPage.netBalanceCard).toBeVisible();
  });

  test('应该能够按日期筛选', async () => {
    await transactionListPage.selectDateRange('2024-01-01', '2024-01-31');
    await transactionListPage.waitForDataRefresh();
  });

  test('应该能够按类型筛选', async () => {
    await transactionListPage.selectType(2);
    await transactionListPage.waitForDataRefresh();
  });

  test('应该能够搜索交易', async () => {
    await transactionListPage.search('测试');
    await transactionListPage.waitForDataRefresh();
  });

  test('应该能够分页', async ({ page }) => {
    const pagination = transactionListPage.pagination;
    if (await pagination.isVisible()) {
      await transactionListPage.goToPage(2);
      await transactionListPage.waitForDataRefresh();
    }
  });

  test('应该能够跳转到新增页面', async ({ page }) => {
    await transactionListPage.clickAddButton();
    await expect(page).toHaveURL(/\/transaction\/add/);
  });

  test('应该能够编辑交易', async ({ page }) => {
    const firstTransaction = transactionListPage.getFirstTransaction();
    if (await firstTransaction.isVisible()) {
      await firstTransaction.click();
      await expect(page).toHaveURL(/\/transaction\/edit/);
    }
  });

  test('应该能够删除交易', async ({ page }) => {
    const deleteButton = transactionListPage.getFirstDeleteButton();
    if (await deleteButton.isVisible()) {
      await deleteButton.click();
      await page.locator('.n-popconfirm').locator('.n-button').filter({ hasText: '确定' }).click();
      await expect(page.locator('.n-message')).toContainText('删除成功');
    }
  });
});

test.describe('PC 个人端 - 交易表单', () => {
  let transactionFormPage: TransactionFormPage;

  test('应该显示新增交易表单', async ({ page }) => {
    transactionFormPage = new TransactionFormPage(page);
    await transactionFormPage.gotoAdd();
    
    await expect(transactionFormPage.title).toContainText('记一笔');
    await expect(transactionFormPage.amountInput).toBeVisible();
    await expect(transactionFormPage.typeSelector).toBeVisible();
    await expect(transactionFormPage.categorySelect).toBeVisible();
    await expect(transactionFormPage.accountSelect).toBeVisible();
    await expect(transactionFormPage.timePicker).toBeVisible();
    await expect(transactionFormPage.submitButton).toBeVisible();
  });

  test('应该正确切换收支类型', async ({ page }) => {
    transactionFormPage = new TransactionFormPage(page);
    await transactionFormPage.gotoAdd();
    
    await transactionFormPage.selectExpenseType();
    await expect(transactionFormPage.expenseButton).toHaveClass(/active/);
    
    await transactionFormPage.selectIncomeType();
    await expect(transactionFormPage.incomeButton).toHaveClass(/active/);
  });

  test('应该根据类型过滤分类选项', async ({ page }) => {
    transactionFormPage = new TransactionFormPage(page);
    await transactionFormPage.gotoAdd();
    
    await transactionFormPage.selectExpenseType();
    const expenseCategories = await transactionFormPage.getCategoryOptions();
    expect(expenseCategories.length).toBeGreaterThan(0);
    
    await transactionFormPage.selectIncomeType();
    const incomeCategories = await transactionFormPage.getCategoryOptions();
    expect(incomeCategories.length).toBeGreaterThan(0);
  });

  test('应该验证必填字段', async ({ page }) => {
    transactionFormPage = new TransactionFormPage(page);
    await transactionFormPage.gotoAdd();
    
    await transactionFormPage.submitFormWithoutData();
    await expect(transactionFormPage.amountError).toBeVisible();
  });

  test('应该能够提交有效表单', async ({ page }) => {
    transactionFormPage = new TransactionFormPage(page);
    await transactionFormPage.gotoAdd();
    
    await transactionFormPage.fillForm({
      amount: 100,
      type: 2,
      category: 1,
      account: 1,
      remark: 'E2E 测试交易'
    });
    await transactionFormPage.submitForm();
    await expect(page.locator('.n-message')).toContainText('创建成功');
    await page.waitForURL(/\/transactions/);
  });
});

test.describe('PC 个人端 - 财务报表', () => {
  let reportPage: ReportPage;

  test.beforeEach(async ({ page }) => {
    reportPage = new ReportPage(page);
    await reportPage.goto();
  });

  test('应该正确显示报表页面', async () => {
    await expect(reportPage.title).toBeVisible();
    await expect(reportPage.dateRangePicker).toBeVisible();
    await expect(reportPage.periodButtons).toBeVisible();
  });

  test('应该显示收支统计', async () => {
    await expect(reportPage.totalIncomeCard).toBeVisible();
    await expect(reportPage.totalExpenseCard).toBeVisible();
    await expect(reportPage.netBalanceCard).toBeVisible();
  });

  test('应该显示图表', async () => {
    await expect(reportPage.expenseChart).toBeVisible();
    await expect(reportPage.trendChart).toBeVisible();
  });

  test('应该显示分类明细', async () => {
    await expect(reportPage.categoryDetail).toBeVisible();
  });

  test('应该能够切换时间范围', async () => {
    await reportPage.clickWeekPeriod();
    await reportPage.waitForDataRefresh();
    
    await reportPage.clickMonthPeriod();
    await reportPage.waitForDataRefresh();
    
    await reportPage.clickYearPeriod();
    await reportPage.waitForDataRefresh();
  });

  test('应该能够选择自定义日期范围', async () => {
    await reportPage.selectCustomDateRange('2024-01-01', '2024-12-31');
    await reportPage.waitForDataRefresh();
  });
});

test.describe('PC 个人端 - 响应式设计', () => {
  const viewports = [
    { width: 1280, height: 720, name: 'Desktop HD' },
    { width: 1024, height: 768, name: 'Desktop' },
    { width: 768, height: 1024, name: 'Tablet' },
  ];

  for (const viewport of viewports) {
    test(`应该在 ${viewport.name} 视口下正确显示`, async ({ page }) => {
      await page.setViewportSize({ width: viewport.width, height: viewport.height });
      
      await page.goto('/accounts');
      await expect(page.locator('h1')).toBeVisible();
      
      await page.goto('/transactions');
      await expect(page.locator('h1')).toBeVisible();
    });
  }
});

test.describe('PC 个人端 - 主题切换', () => {
  test('应该支持亮色和暗色主题', async ({ page }) => {
    await page.goto('/accounts');
    
    const themeToggle = page.locator('[data-testid="theme-toggle"]');
    if (await themeToggle.isVisible()) {
      const html = page.locator('html');
      const initialTheme = await html.getAttribute('class');
      
      await themeToggle.click();
      await page.waitForTimeout(300);
      
      const newTheme = await html.getAttribute('class');
      expect(newTheme).not.toBe(initialTheme);
    }
  });
});

test.describe('PC 个人端 - 国际化', () => {
  test('应该支持中英文切换', async ({ page }) => {
    await page.goto('/accounts');
    
    const langToggle = page.locator('[data-testid="language-toggle"]');
    if (await langToggle.isVisible()) {
      const initialTitle = await page.locator('h1').textContent();
      
      await langToggle.click();
      await page.waitForTimeout(300);
      
      const newTitle = await page.locator('h1').textContent();
      expect(newTitle).not.toBe(initialTitle);
    }
  });
});
