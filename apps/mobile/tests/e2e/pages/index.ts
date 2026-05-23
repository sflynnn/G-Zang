import { Page, Locator } from '@playwright/test';

export class HomePage {
  readonly page: Page;
  readonly title: Locator;
  readonly balanceCard: Locator;
  readonly todayIncome: Locator;
  readonly todayExpense: Locator;
  readonly recentTransactions: Locator;
  readonly tabBar: Locator;
  readonly tabHome: Locator;
  readonly tabAccounting: Locator;
  readonly tabBills: Locator;
  readonly tabAnalysis: Locator;
  readonly tabProfile: Locator;

  constructor(page: Page) {
    this.page = page;
    this.title = page.locator('.nav-title').or(page.locator('text=首页'));
    this.balanceCard = page.locator('.balance-card, .overview-card, text=总资产').first();
    this.todayIncome = page.locator('text=今日收入, text=收入').first();
    this.todayExpense = page.locator('text=今日支出, text=支出').first();
    this.recentTransactions = page.locator('.recent-list, .transaction-list, text=最近交易').first();
    this.tabBar = page.locator('.uni-tabbar, .tab-bar, .tabbar');
    this.tabHome = page.locator('text=首页').first();
    this.tabAccounting = page.locator('text=记账').first();
    this.tabBills = page.locator('text=账单').first();
    this.tabAnalysis = page.locator('text=统计').first();
    this.tabProfile = page.locator('text=我的').first();
  }

  async goto() {
    await this.page.goto('/pages/home/index');
    await this.page.waitForLoadState('domcontentloaded');
  }

  clickAccountingTab() {
    return this.tabAccounting.click();
  }

  clickBillsTab() {
    return this.tabBills.click();
  }

  clickAnalysisTab() {
    return this.tabAnalysis.click();
  }

  clickProfileTab() {
    return this.tabProfile.click();
  }
}

export class AccountingPage {
  readonly page: Page;
  readonly quickRecordButton: Locator;
  readonly voiceButton: Locator;
  readonly cameraButton: Locator;
  readonly manualButton: Locator;
  readonly todayStats: Locator;

  constructor(page: Page) {
    this.page = page;
    this.quickRecordButton = page.locator('text=快速记账, text=记一笔').first();
    this.voiceButton = page.locator('text=语音记账, text=语音').first();
    this.cameraButton = page.locator('text=拍照记账, text=拍照').first();
    this.manualButton = page.locator('text=手动记账, text=手动').first();
    this.todayStats = page.locator('text=今日').first();
  }

  async goto() {
    await this.page.goto('/pages/accounting/index');
    await this.page.waitForLoadState('domcontentloaded');
  }

  clickQuickRecord() {
    return this.quickRecordButton.click();
  }

  clickVoiceRecord() {
    return this.voiceButton.click();
  }

  clickCameraRecord() {
    return this.cameraButton.click();
  }

  clickManualRecord() {
    return this.manualButton.click();
  }
}

export class QuickRecordPage {
  readonly page: Page;
  readonly amountInput: Locator;
  readonly amountDisplay: Locator;
  readonly expenseTab: Locator;
  readonly incomeTab: Locator;
  readonly categoryGrid: Locator;
  readonly accountSelector: Locator;
  readonly saveButton: Locator;

  constructor(page: Page) {
    this.page = page;
    this.amountInput = page.locator('input[type="number"], input[type="tel"], .amount-input');
    this.amountDisplay = page.locator('.amount-display, .amount-value, .amount-text');
    this.expenseTab = page.locator('text=支出').first();
    this.incomeTab = page.locator('text=收入').first();
    this.categoryGrid = page.locator('.category-grid, .category-list');
    this.accountSelector = page.locator('text=选择账户, .account-selector');
    this.saveButton = page.locator('text=保存, text=确定, .save-btn').first();
  }

  async goto() {
    await this.page.goto('/pages/accounting/quick');
    await this.page.waitForLoadState('domcontentloaded');
  }

  clickExpenseTab() {
    return this.expenseTab.click();
  }

  clickIncomeTab() {
    return this.incomeTab.click();
  }

  async enterAmount(amount: string) {
    await this.amountInput.fill(amount);
  }

  selectFirstCategory() {
    return this.categoryGrid.locator('.category-item, .category-btn').first().click();
  }

  clickAccountSelector() {
    return this.accountSelector.click();
  }

  selectFirstAccount() {
    return this.page.locator('.account-item, text=现金').first().click();
  }

  clickSave() {
    return this.saveButton.click();
  }
}

export class BillsPage {
  readonly page: Page;
  readonly title: Locator;
  readonly monthSelector: Locator;
  readonly billsList: Locator;

  constructor(page: Page) {
    this.page = page;
    this.title = page.locator('.nav-title').or(page.locator('text=账单'));
    this.monthSelector = page.locator('.month-selector, .date-picker');
    this.billsList = page.locator('.bill-list, .transaction-list, .list-content');
  }

  async goto() {
    await this.page.goto('/pages/bills/index');
    await this.page.waitForLoadState('domcontentloaded');
  }

  clickMonthSelector() {
    return this.monthSelector.click();
  }

  selectPreviousMonth() {
    return this.page.locator('text=上一月, text=上月').first().click();
  }

  selectNextMonth() {
    return this.page.locator('text=下一月, text=下月').first().click();
  }

  getFirstBill(): Locator {
    return this.billsList.locator('.bill-item, .transaction-item').first();
  }
}

export class AnalysisPage {
  readonly page: Page;
  readonly title: Locator;
  readonly monthSelector: Locator;
  readonly incomeOverview: Locator;
  readonly expenseOverview: Locator;
  readonly expenseChart: Locator;
  readonly categoryBreakdown: Locator;
  readonly moreChartButton: Locator;
  readonly reportButton: Locator;

  constructor(page: Page) {
    this.page = page;
    this.title = page.locator('.nav-title').or(page.locator('text=统计'));
    this.monthSelector = page.locator('.month-selector, .date-picker');
    this.incomeOverview = page.locator('text=收入').first();
    this.expenseOverview = page.locator('text=支出').first();
    this.expenseChart = page.locator('.chart-container, .echarts, canvas').first();
    this.categoryBreakdown = page.locator('text=分类, .category-list').first();
    this.moreChartButton = page.locator('text=更多图表, text=图表').first();
    this.reportButton = page.locator('text=报告, text=财务报告').first();
  }

  async goto() {
    await this.page.goto('/pages/analysis/index');
    await this.page.waitForLoadState('domcontentloaded');
  }

  clickMonthSelector() {
    return this.monthSelector.click();
  }

  selectPreviousMonth() {
    return this.page.locator('text=上一月, text=上月').first().click();
  }

  selectNextMonth() {
    return this.page.locator('text=下一月, text=下月').first().click();
  }
}

export class ProfilePage {
  readonly page: Page;
  readonly avatar: Locator;
  readonly username: Locator;
  readonly menuList: Locator;
  readonly settingsItem: Locator;
  readonly editProfileItem: Locator;

  constructor(page: Page) {
    this.page = page;
    this.avatar = page.locator('.avatar, image');
    this.username = page.locator('.username, text=用户名');
    this.menuList = page.locator('.menu-list, .profile-menu');
    this.settingsItem = page.locator('text=设置').first();
    this.editProfileItem = page.locator('text=编辑资料, text=个人资料').first();
  }

  async goto() {
    await this.page.goto('/pages/profile/index');
    await this.page.waitForLoadState('domcontentloaded');
  }
}

export class LoginPage {
  readonly page: Page;
  readonly logo: Locator;
  readonly phoneInput: Locator;
  readonly passwordInput: Locator;
  readonly loginButton: Locator;
  readonly registerLink: Locator;
  readonly forgotPasswordLink: Locator;
  readonly phoneError: Locator;
  readonly passwordError: Locator;
  readonly togglePasswordButton: Locator;

  constructor(page: Page) {
    this.page = page;
    this.logo = page.locator('.logo, .logo-img, image');
    this.phoneInput = page.locator('input[type="tel"], input[placeholder*="手机"]');
    this.passwordInput = page.locator('input[type="password"], input[placeholder*="密码"]');
    this.loginButton = page.locator('button').filter({ hasText: /登录|登录/ }).first();
    this.registerLink = page.locator('text=注册, text=立即注册');
    this.forgotPasswordLink = page.locator('text=忘记密码');
    this.phoneError = page.locator('text=请输入手机号, text=手机号格式不正确');
    this.passwordError = page.locator('text=请输入密码');
    this.togglePasswordButton = page.locator('.toggle-password, .password-toggle');
  }

  async goto() {
    await this.page.goto('/pages/login/index');
    await this.page.waitForLoadState('domcontentloaded');
  }

  async fillPhone(phone: string) {
    await this.phoneInput.fill(phone);
  }

  async fillPassword(password: string) {
    await this.passwordInput.fill(password);
  }

  clickLogin() {
    return this.loginButton.click();
  }

  clickRegister() {
    return this.registerLink.first().click();
  }

  clickForgotPassword() {
    return this.forgotPasswordLink.click();
  }

  togglePasswordVisibility() {
    return this.togglePasswordButton.click();
  }
}
