import { createI18n } from 'vue-i18n';

export const messages = {
  zh: {
    app: {
      businessTitle: '企业业务',
      businessSubtitle: '企业财务与审批管理'
    },
    dashboard: {
      title: '工作台',
      welcome: '欢迎回来',
      totalOrders: '待处理订单',
      pendingApprovals: '待审批',
      monthlyBudget: '本月预算',
      monthlyExpense: '本月支出',
      recentOrders: '最近订单',
      quickActions: '快捷操作',
      orderManagement: '订单管理',
      budgetControl: '预算控制',
      costAnalysis: '成本分析',
      approvalCenter: '审批中心',
      noOrders: '暂无待处理订单'
    },
    order: {
      title: '业务订单',
      orderNo: '订单编号',
      customer: '客户名称',
      amount: '订单金额',
      status: '状态',
      createTime: '创建时间',
      pending: '待审批',
      approved: '已通过',
      rejected: '已驳回',
      inProgress: '进行中',
      completed: '已完成',
      submit: '提交审批',
      approve: '审批通过',
      reject: '驳回',
      viewDetail: '查看详情'
    },
    budget: {
      title: '预算管理',
      budgetAmount: '预算金额',
      usedAmount: '已使用',
      remaining: '剩余',
      period: '预算周期',
      category: '费用类别',
      department: '部门',
      warning: '预警',
      exceeded: '已超支'
    },
    costCenter: {
      title: '成本中心',
      centerName: '中心名称',
      totalCost: '总成本',
      allocated: '已分摊',
      unallocated: '未分摊'
    },
    report: {
      title: '财务报表',
      incomeReport: '收入报表',
      expenseReport: '支出报表',
      profitReport: '利润报表',
      cashFlow: '现金流',
      period: '报表周期',
      export: '导出'
    },
    approval: {
      title: '审批中心',
      applyType: '申请类型',
      applicant: '申请人',
      applyTime: '申请时间',
      approve: '通过',
      reject: '驳回',
      pending: '待处理'
    }
  },
  en: {
    app: {
      businessTitle: 'Business',
      businessSubtitle: 'Enterprise Finance & Approval'
    },
    dashboard: {
      title: 'Dashboard',
      welcome: 'Welcome',
      totalOrders: 'Pending Orders',
      pendingApprovals: 'Pending Approvals',
      monthlyBudget: 'Monthly Budget',
      monthlyExpense: 'Monthly Expense',
      recentOrders: 'Recent Orders',
      quickActions: 'Quick Actions',
      orderManagement: 'Order Management',
      budgetControl: 'Budget Control',
      costAnalysis: 'Cost Analysis',
      approvalCenter: 'Approval Center',
      noOrders: 'No pending orders'
    },
    order: {
      title: 'Business Orders',
      orderNo: 'Order No',
      customer: 'Customer',
      amount: 'Amount',
      status: 'Status',
      createTime: 'Create Time',
      pending: 'Pending',
      approved: 'Approved',
      rejected: 'Rejected',
      inProgress: 'In Progress',
      completed: 'Completed',
      submit: 'Submit',
      approve: 'Approve',
      reject: 'Reject',
      viewDetail: 'View Detail'
    },
    budget: {
      title: 'Budget Management',
      budgetAmount: 'Budget',
      usedAmount: 'Used',
      remaining: 'Remaining',
      period: 'Period',
      category: 'Category',
      department: 'Department',
      warning: 'Warning',
      exceeded: 'Exceeded'
    },
    costCenter: {
      title: 'Cost Centers',
      centerName: 'Center Name',
      totalCost: 'Total Cost',
      allocated: 'Allocated',
      unallocated: 'Unallocated'
    },
    report: {
      title: 'Financial Reports',
      incomeReport: 'Income Report',
      expenseReport: 'Expense Report',
      profitReport: 'Profit Report',
      cashFlow: 'Cash Flow',
      period: 'Period',
      export: 'Export'
    },
    approval: {
      title: 'Approval Center',
      applyType: 'Type',
      applicant: 'Applicant',
      applyTime: 'Apply Time',
      approve: 'Approve',
      reject: 'Reject',
      pending: 'Pending'
    }
  }
};

const i18n = createI18n({
  legacy: false,
  locale: localStorage.getItem('business_language') || 'zh',
  fallbackLocale: 'zh',
  messages,
  globalInjection: true,
});

export default i18n;
