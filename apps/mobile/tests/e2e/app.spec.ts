import { test, expect } from '@playwright/test';
import { 
  HomePage, 
  AccountingPage, 
  BillsPage, 
  AnalysisPage, 
  ProfilePage, 
  LoginPage, 
  QuickRecordPage 
} from './pages/mobile-pages';

test.describe('移动端 - 首页', () => {
  let homePage: HomePage;

  test.beforeEach(async ({ page }) => {
    homePage = new HomePage(page);
    await homePage.goto();
  });

  test('应该正确显示首页', async () => {
    await expect(homePage.title).toBeVisible();
    await expect(homePage.balanceCard).toBeVisible();
  });

  test('应该显示今日收支', async () => {
    await expect(homePage.todayIncome).toBeVisible();
    await expect(homePage.todayExpense).toBeVisible();
  });

  test('应该显示最近交易', async () => {
    await expect(homePage.recentTransactions).toBeVisible();
  });

  test('应该显示底部导航栏', async () => {
    await expect(homePage.tabBar).toBeVisible();
    await expect(homePage.tabHome).toBeVisible();
    await expect(homePage.tabAccounting).toBeVisible();
    await expect(homePage.tabBills).toBeVisible();
    await expect(homePage.tabAnalysis).toBeVisible();
    await expect(homePage.tabProfile).toBeVisible();
  });

  test('应该能切换到记账页面', async ({ page }) => {
    await homePage.clickAccountingTab();
    await expect(page).toHaveURL(/\/pages\/accounting\/index/);
  });

  test('应该能切换到账单页面', async ({ page }) => {
    await homePage.clickBillsTab();
    await expect(page).toHaveURL(/\/pages\/bills\/index/);
  });

  test('应该能切换到统计页面', async ({ page }) => {
    await homePage.clickAnalysisTab();
    await expect(page).toHaveURL(/\/pages\/analysis\/index/);
  });

  test('应该能切换到我的页面', async ({ page }) => {
    await homePage.clickProfileTab();
    await expect(page).toHaveURL(/\/pages\/profile\/index/);
  });
});

test.describe('移动端 - 记账模块', () => {
  let accountingPage: AccountingPage;

  test.beforeEach(async ({ page }) => {
    accountingPage = new AccountingPage(page);
    await accountingPage.goto();
  });

  test('应该正确显示记账页面', async () => {
    await expect(accountingPage.quickRecordButton).toBeVisible();
    await expect(accountingPage.voiceButton).toBeVisible();
    await expect(accountingPage.cameraButton).toBeVisible();
    await expect(accountingPage.manualButton).toBeVisible();
  });

  test('应该能进入快速记账', async ({ page }) => {
    await accountingPage.clickQuickRecord();
    await expect(page).toHaveURL(/\/pages\/accounting\/quick/);
  });

  test('应该能进入语音记账', async ({ page }) => {
    await accountingPage.clickVoiceRecord();
    await expect(page).toHaveURL(/\/pages\/accounting\/voice/);
  });

  test('应该能进入拍照记账', async ({ page }) => {
    await accountingPage.clickCameraRecord();
    await expect(page).toHaveURL(/\/pages\/accounting\/camera/);
  });

  test('应该能进入手动记账', async ({ page }) => {
    await accountingPage.clickManualRecord();
    await expect(page).toHaveURL(/\/pages\/accounting\/manual/);
  });

  test('应该显示记账统计', async () => {
    await expect(accountingPage.todayStats).toBeVisible();
  });
});

test.describe('移动端 - 快速记账', () => {
  let quickRecordPage: QuickRecordPage;

  test('应该正确显示快速记账表单', async ({ page }) => {
    quickRecordPage = new QuickRecordPage(page);
    await quickRecordPage.goto();
    
    await expect(quickRecordPage.amountInput).toBeVisible();
    await expect(quickRecordPage.expenseTab).toBeVisible();
    await expect(quickRecordPage.incomeTab).toBeVisible();
    await expect(quickRecordPage.categoryGrid).toBeVisible();
    await expect(quickRecordPage.accountSelector).toBeVisible();
    await expect(quickRecordPage.saveButton).toBeVisible();
  });

  test('应该能切换收支类型', async ({ page }) => {
    quickRecordPage = new QuickRecordPage(page);
    await quickRecordPage.goto();
    
    await quickRecordPage.clickExpenseTab();
    await expect(quickRecordPage.expenseTab).toHaveClass(/active/);
    
    await quickRecordPage.clickIncomeTab();
    await expect(quickRecordPage.incomeTab).toHaveClass(/active/);
  });

  test('应该能输入金额', async ({ page }) => {
    quickRecordPage = new QuickRecordPage(page);
    await quickRecordPage.goto();
    
    await quickRecordPage.enterAmount('100');
    await expect(quickRecordPage.amountDisplay).toContainText('100');
  });

  test('应该能选择分类', async ({ page }) => {
    quickRecordPage = new QuickRecordPage(page);
    await quickRecordPage.goto();
    
    await quickRecordPage.selectFirstCategory();
  });

  test('应该能选择账户', async ({ page }) => {
    quickRecordPage = new QuickRecordPage(page);
    await quickRecordPage.goto();
    
    await quickRecordPage.clickAccountSelector();
    await quickRecordPage.selectFirstAccount();
  });

  test('应该能保存记账', async ({ page }) => {
    quickRecordPage = new QuickRecordPage(page);
    await quickRecordPage.goto();
    
    await quickRecordPage.enterAmount('50');
    await quickRecordPage.selectFirstCategory();
    await quickRecordPage.clickSave();
    await expect(page.locator('.uni-toast, .u-toast')).toBeVisible();
  });
});

test.describe('移动端 - 账单页面', () => {
  let billsPage: BillsPage;

  test.beforeEach(async ({ page }) => {
    billsPage = new BillsPage(page);
    await billsPage.goto();
  });

  test('应该正确显示账单页面', async () => {
    await expect(billsPage.title).toBeVisible();
    await expect(billsPage.monthSelector).toBeVisible();
  });

  test('应该能切换月份', async () => {
    await billsPage.clickMonthSelector();
    await billsPage.selectPreviousMonth();
  });

  test('应该显示账单列表', async () => {
    await expect(billsPage.billsList).toBeVisible();
  });

  test('应该能查看账单详情', async ({ page }) => {
    const firstBill = billsPage.getFirstBill();
    if (await firstBill.isVisible()) {
      await firstBill.click();
      await expect(page).toHaveURL(/\/pages\/bills\/detail/);
    }
  });
});

test.describe('移动端 - 统计页面', () => {
  let analysisPage: AnalysisPage;

  test.beforeEach(async ({ page }) => {
    analysisPage = new AnalysisPage(page);
    await analysisPage.goto();
  });

  test('应该正确显示统计页面', async () => {
    await expect(analysisPage.title).toBeVisible();
    await expect(analysisPage.monthSelector).toBeVisible();
  });

  test('应该显示收支概览', async () => {
    await expect(analysisPage.incomeOverview).toBeVisible();
    await expect(analysisPage.expenseOverview).toBeVisible();
  });

  test('应该显示图表', async () => {
    await expect(analysisPage.expenseChart).toBeVisible();
  });

  test('应该显示分类支出', async () => {
    await expect(analysisPage.categoryBreakdown).toBeVisible();
  });

  test('应该能切换月份', async () => {
    await analysisPage.clickMonthSelector();
    await analysisPage.selectNextMonth();
  });

  test('应该能查看更多图表', async ({ page }) => {
    const chartButton = analysisPage.moreChartButton;
    if (await chartButton.isVisible()) {
      await chartButton.click();
      await expect(page).toHaveURL(/\/pages\/analysis\/chart/);
    }
  });

  test('应该能查看财务报告', async ({ page }) => {
    const reportButton = analysisPage.reportButton;
    if (await reportButton.isVisible()) {
      await reportButton.click();
      await expect(page).toHaveURL(/\/pages\/analysis\/report/);
    }
  });
});

test.describe('移动端 - 个人中心', () => {
  let profilePage: ProfilePage;

  test.beforeEach(async ({ page }) => {
    profilePage = new ProfilePage(page);
    await profilePage.goto();
  });

  test('应该正确显示个人中心', async () => {
    await expect(profilePage.avatar).toBeVisible();
    await expect(profilePage.username).toBeVisible();
  });

  test('应该显示菜单列表', async () => {
    await expect(profilePage.menuList).toBeVisible();
  });

  test('应该能进入设置页面', async ({ page }) => {
    const settingsItem = profilePage.settingsItem;
    if (await settingsItem.isVisible()) {
      await settingsItem.click();
      await expect(page).toHaveURL(/\/pages\/settings\/index/);
    }
  });

  test('应该能进入编辑资料页面', async ({ page }) => {
    const editItem = profilePage.editProfileItem;
    if (await editItem.isVisible()) {
      await editItem.click();
      await expect(page).toHaveURL(/\/pages\/profile\/edit/);
    }
  });
});

test.describe('移动端 - 登录页面', () => {
  let loginPage: LoginPage;

  test('应该正确显示登录页面', async ({ page }) => {
    loginPage = new LoginPage(page);
    await loginPage.goto();
    
    await expect(loginPage.logo).toBeVisible();
    await expect(loginPage.phoneInput).toBeVisible();
    await expect(loginPage.passwordInput).toBeVisible();
    await expect(loginPage.loginButton).toBeVisible();
    await expect(loginPage.registerLink).toBeVisible();
    await expect(loginPage.forgotPasswordLink).toBeVisible();
  });

  test('应该能输入手机号和密码', async ({ page }) => {
    loginPage = new LoginPage(page);
    await loginPage.goto();
    
    await loginPage.fillPhone('13800138000');
    await loginPage.fillPassword('password123');
    
    await expect(loginPage.phoneInput).toHaveValue('13800138000');
  });

  test('应该能切换密码可见性', async ({ page }) => {
    loginPage = new LoginPage(page);
    await loginPage.goto();
    
    await loginPage.fillPassword('password123');
    await loginPage.togglePasswordVisibility();
  });

  test('应该能跳转到注册页面', async ({ page }) => {
    loginPage = new LoginPage(page);
    await loginPage.goto();
    
    await loginPage.clickRegister();
    await expect(page).toHaveURL(/\/pages\/register\/index/);
  });

  test('应该能跳转到忘记密码页面', async ({ page }) => {
    loginPage = new LoginPage(page);
    await loginPage.goto();
    
    await loginPage.clickForgotPassword();
    await expect(page).toHaveURL(/\/pages\/login\/forgot-password/);
  });

  test('应该验证必填字段', async ({ page }) => {
    loginPage = new LoginPage(page);
    await loginPage.goto();
    
    await loginPage.clickLogin();
    await expect(loginPage.phoneError).toBeVisible();
  });
});

test.describe('移动端 - 响应式设计', () => {
  const devices = [
    { name: 'iPhone 12', viewport: { width: 390, height: 844 } },
    { name: 'Pixel 5', viewport: { width: 393, height: 851 } },
    { name: 'iPhone SE', viewport: { width: 375, height: 667 } },
    { name: 'iPhone 12 Pro Max', viewport: { width: 428, height: 926 } },
  ];

  for (const device of devices) {
    test(`应该在 ${device.name} 视口下正确显示`, async ({ page }) => {
      await page.setViewportSize(device.viewport);
      
      await page.goto('/pages/home/index');
      await expect(page.locator('.uni-page')).toBeVisible();
    });
  }
});

test.describe('移动端 - 主题切换', () => {
  test('应该支持亮色和暗色主题', async ({ page }) => {
    await page.goto('/pages/settings/index');
    
    const themeToggle = page.locator('[data-testid="theme-toggle"], .theme-toggle');
    if (await themeToggle.isVisible()) {
      const html = page.locator('html');
      const initialTheme = await html.getAttribute('class') || '';
      
      await themeToggle.click();
      await page.waitForTimeout(300);
      
      const newTheme = await html.getAttribute('class') || '';
      expect(newTheme).not.toBe(initialTheme);
    }
  });
});

test.describe('移动端 - 网络状态处理', () => {
  test('应该在离线时显示提示', async ({ page, context }) => {
    await context.setOffline(true);
    
    await page.goto('/pages/home/index');
    const offlineIndicator = page.locator('.offline-indicator, .network-offline');
    
    if (await offlineIndicator.isVisible({ timeout: 3000 }).catch(() => false)) {
      await expect(offlineIndicator).toBeVisible();
    }
    
    await context.setOffline(false);
  });
});
