import { createI18n } from 'vue-i18n';

// 定义语言包
export const messages = {
  zh: {
    app: {
      personalTitle: '个人记账',
      personalSubtitle: '智能财务，轻松管理'
    },
    common: {
      toggleTheme: '切换主题',
      notifications: '通知',
      settings: '设置',
      logout: '退出登录',
      language: '语言',
      chinese: '中文',
      english: 'English'
    },
    dashboard: {
      title: '财务概览',
      totalAssets: '总资产',
      monthIncome: '本月收入',
      monthExpense: '本月支出',
      budgetRemaining: '预算剩余',
      trend: '收支趋势',
      budget: '预算进度',
      quickActions: '快捷操作',
      recentTransactions: '最近交易',
      addTransaction: '记一笔',
      transfer: '转账',
      report: '报表',
      account: '账户',
      income: '收入',
      expense: '支出',
      transferType: '转账',
      balance: '余额',
      thisWeek: '本周',
      thisMonth: '本月',
      thisYear: '本年',
      noData: '暂无数据',
      addFirst: '添加第一笔交易',
      budgetWarning: '预算预警',
      budgetExceeded: '已超预算',
      daily: '日',
      weekly: '周',
      monthly: '月',
      yearly: '年',
      amount: '金额',
      time: '时间',
      note: '备注',
      food: '餐饮',
      transport: '交通',
      shopping: '购物',
      entertainment: '娱乐',
      housing: '居住',
      healthcare: '医疗',
      education: '教育',
      salary: '工资',
      investment: '投资',
      gift: '礼金',
      other: '其他',
      // 问候语
      night: '凌晨好',
      morning: '早上好',
      am: '上午好',
      noon: '中午好',
      pm: '下午好',
      evening: '晚上好',
      lateNight: '夜深了',
      today: '今天',
      yesterday: '昨天',
      daysAgo: '天前'
    },
    transaction: {
      add: '添加交易',
      edit: '编辑交易',
      delete: '删除交易',
      confirm: '确认',
      cancel: '取消',
      amount: '金额',
      category: '分类',
      account: '账户',
      date: '日期',
      note: '备注',
      type: '类型',
      income: '收入',
      expense: '支出'
    },
    account: {
      cash: '现金',
      bankCard: '银行卡',
      creditCard: '信用卡',
      alipay: '支付宝',
      wechat: '微信',
      other: '其他'
    }
  },
  en: {
    app: {
      personalTitle: 'Personal Finance',
      personalSubtitle: 'Smart Finance, Easy Management'
    },
    common: {
      toggleTheme: 'Toggle Theme',
      notifications: 'Notifications',
      settings: 'Settings',
      logout: 'Logout',
      language: 'Language',
      chinese: '中文',
      english: 'English'
    },
    dashboard: {
      title: 'Financial Overview',
      totalAssets: 'Total Assets',
      monthIncome: 'Monthly Income',
      monthExpense: 'Monthly Expense',
      budgetRemaining: 'Budget Remaining',
      trend: 'Income & Expense Trend',
      budget: 'Budget Progress',
      quickActions: 'Quick Actions',
      recentTransactions: 'Recent Transactions',
      addTransaction: 'Add Transaction',
      transfer: 'Transfer',
      report: 'Reports',
      account: 'Accounts',
      income: 'Income',
      expense: 'Expense',
      transferType: 'Transfer',
      balance: 'Balance',
      thisWeek: 'This Week',
      thisMonth: 'This Month',
      thisYear: 'This Year',
      noData: 'No Data',
      addFirst: 'Add Your First Transaction',
      budgetWarning: 'Budget Warning',
      budgetExceeded: 'Budget Exceeded',
      daily: 'Day',
      weekly: 'Week',
      monthly: 'Month',
      yearly: 'Year',
      amount: 'Amount',
      time: 'Time',
      note: 'Note',
      food: 'Food',
      transport: 'Transport',
      shopping: 'Shopping',
      entertainment: 'Entertainment',
      housing: 'Housing',
      healthcare: 'Healthcare',
      education: 'Education',
      salary: 'Salary',
      investment: 'Investment',
      gift: 'Gift',
      other: 'Other',
      // Greetings
      night: 'Good Night',
      morning: 'Good Morning',
      am: 'Good Morning',
      noon: 'Good Noon',
      pm: 'Good Afternoon',
      evening: 'Good Evening',
      lateNight: 'It\'s Late',
      today: 'Today',
      yesterday: 'Yesterday',
      daysAgo: ' days ago'
    },
    transaction: {
      add: 'Add Transaction',
      edit: 'Edit Transaction',
      delete: 'Delete Transaction',
      confirm: 'Confirm',
      cancel: 'Cancel',
      amount: 'Amount',
      category: 'Category',
      account: 'Account',
      date: 'Date',
      note: 'Note',
      type: 'Type',
      income: 'Income',
      expense: 'Expense'
    },
    account: {
      cash: 'Cash',
      bankCard: 'Bank Card',
      creditCard: 'Credit Card',
      alipay: 'Alipay',
      wechat: 'WeChat',
      other: 'Other'
    }
  }
};

// 创建i18n实例
const i18n = createI18n({
  legacy: false, // 使用Composition API模式
  locale: localStorage.getItem('personal_language') || 'zh', // 默认语言
  fallbackLocale: 'zh', // 回退语言
  messages,
  globalInjection: true, // 全局注入 $t 函数
});

export default i18n;
