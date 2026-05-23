/**
 * Report Store - 报表状态管理
 * 对齐后端: server/gzang-mobile-api/.../MobileReportController.java
 */
import { defineStore } from 'pinia';
import { ref, computed } from 'vue';
import { TransactionType } from '@/types/transaction';
import * as reportApi from '@/api/report';

// G-Zang 品牌色
const BRAND_COLORS = {
  primary: '#0F4C5C',
  accent: '#FB8B24',
  success: '#06D6A0',
  danger: '#EF476F',
  warning: '#FFD166',
};

export interface SummaryResult {
  totalIncome: number;
  totalExpense: number;
  balance: number;
  transactionCount: number;
  incomeCount?: number;
  expenseCount?: number;
}

export interface MonthlyTrendItem {
  month: number;
  income: number;
  expense: number;
}

export interface CategoryReportItem {
  categoryId: number;
  categoryName: string;
  totalAmount: number;
  count: number;
  percentage?: number;
}

export interface AccountBalanceItem {
  accountId: number;
  accountName: string;
  accountType: string;
  balance: number;
  percentage?: number;
}

export interface ReportState {
  summary: SummaryResult | null;
  monthlyTrend: MonthlyTrendItem[];
  categoryReport: CategoryReportItem[];
  accountBalance: AccountBalanceItem[];
  loading: boolean;
  dateRange: { startDate: string; endDate: string } | null;
}

export const useReportStore = defineStore('report', () => {
  // 状态
  const summary = ref<SummaryResult | null>(null);
  const monthlyTrend = ref<MonthlyTrendItem[]>([]);
  const categoryReport = ref<CategoryReportItem[]>([]);
  const accountBalance = ref<AccountBalanceItem[]>([]);
  const loading = ref(false);
  const dateRange = ref<{ startDate: string; endDate: string } | null>(null);

  // 计算属性
  const totalIncome = computed(() => summary.value?.totalIncome || 0);
  const totalExpense = computed(() => summary.value?.totalExpense || 0);
  const balance = computed(() => summary.value?.balance || 0);

  const incomePercentage = computed(() => {
    if (totalIncome.value === 0) return 0;
    return Math.round((totalIncome.value / (totalIncome.value + totalExpense.value)) * 100);
  });

  const expensePercentage = computed(() => {
    if (totalExpense.value === 0) return 0;
    return Math.round((totalExpense.value / (totalIncome.value + totalExpense.value)) * 100);
  });

  const accountBalanceSorted = computed(() =>
    [...accountBalance.value].sort((a, b) => b.balance - a.balance)
  );

  const trendData = computed(() =>
    monthlyTrend.value.map((item) => ({
      month: String(item.month),
      income: item.income,
      expense: item.expense,
      balance: 0,
    }))
  );

  // 获取分类颜色
  const getCategoryColor = (type: number) => {
    return type === TransactionType.Income ? BRAND_COLORS.success : BRAND_COLORS.danger;
  };

  // 获取收支汇总
  const fetchSummary = async (params?: {
    startTime?: string;
    endTime?: string;
  }) => {
    try {
      loading.value = true;
      if (params?.startTime && params?.endTime) {
        dateRange.value = { startDate: params.startTime, endDate: params.endTime };
      }
      const data = await reportApi.getSummary(params);
      summary.value = data;
      return data;
    } catch (error) {
      throw error;
    } finally {
      loading.value = false;
    }
  };

  // 获取月度趋势（后端需要 year 参数）
  const fetchMonthlyTrend = async (year: number) => {
    try {
      loading.value = true;
      const data = await reportApi.getMonthlyTrend({ year });
      monthlyTrend.value = data;
      return data;
    } catch (error) {
      throw error;
    } finally {
      loading.value = false;
    }
  };

  // 获取分类报表
  const fetchCategoryReport = async (params?: {
    startTime?: string;
    endTime?: string;
    type?: TransactionType;
  }) => {
    try {
      loading.value = true;
      const data = await reportApi.getCategoryReport(params);
      categoryReport.value = data;
      return data;
    } catch (error) {
      throw error;
    } finally {
      loading.value = false;
    }
  };

  // 获取账户余额报表
  const fetchAccountBalance = async () => {
    try {
      loading.value = true;
      const data = await reportApi.getAccountBalance();
      accountBalance.value = data;
      return data;
    } catch (error) {
      throw error;
    } finally {
      loading.value = false;
    }
  };

  // 设置日期范围
  const setDateRange = (startDate: string, endDate: string) => {
    dateRange.value = { startDate, endDate };
  };

  // 导出数据
  const exportData = async (format: 'csv' | 'pdf' = 'csv') => {
    try {
      uni.showLoading({ title: '正在生成导出文件...', mask: true })

      const s = summary.value
      const categoryRows = categoryReport.value.map(c =>
        `${c.categoryName},${c.totalAmount},${c.count},${c.percentage?.toFixed(1) || 0}%`
      ).join('\n')
      const trendRows = monthlyTrend.value.map(t =>
        `${t.month}月,${t.income},${t.expense}`
      ).join('\n')

      const csvContent = [
        '归藏财务管理系统 - 收支报告',
        `导出时间：${new Date().toLocaleString('zh-CN')}`,
        '',
        '=== 收支概览 ===',
        `总收入,${s?.totalIncome || 0}`,
        `总支出,${s?.totalExpense || 0}`,
        `结余,${s?.balance || 0}`,
        `交易笔数,${s?.transactionCount || 0}`,
        '',
        '=== 分类统计 ===',
        '分类,金额,笔数,占比',
        categoryRows,
        '',
        '=== 趋势数据 ===',
        '月份,收入,支出',
        trendRows,
      ].join('\n')

      const fs = (uni as any).getFileSystemManager?.()
      const tempFilePath = `${(uni as any).env?.USER_DATA_PATH || '/tmp'}/gzang_report_${Date.now()}.csv`

      if (fs) {
        fs.writeFile({
          filePath: tempFilePath,
          data: csvContent,
          encoding: 'utf8',
          success: () => {
            uni.hideLoading()
            uni.share({
              title: '归藏财务报告',
              filePath: tempFilePath,
              success: () => uni.showToast({ title: '导出成功', icon: 'success' }),
              fail: (e: any) => {
                if (e.errMsg?.includes('cancel')) return
                uni.share({
                  title: '归藏财务报告',
                  summary: `收入 ${s?.totalIncome || 0} | 支出 ${s?.totalExpense || 0} | 结余 ${s?.balance || 0}`,
                  fail: () => uni.showToast({ title: '分享失败', icon: 'none' })
                })
              }
            })
          },
          fail: () => {
            uni.hideLoading()
            uni.showToast({ title: '文件生成失败', icon: 'none' })
          }
        })
      } else {
        uni.hideLoading()
        uni.share({
          title: '归藏财务报告',
          summary: `收入 ${s?.totalIncome || 0} | 支出 ${s?.totalExpense || 0} | 结余 ${s?.balance || 0}`,
          success: () => uni.showToast({ title: '导出成功', icon: 'success' }),
          fail: () => uni.showToast({ title: '分享失败', icon: 'none' })
        })
      }
    } catch (error) {
      uni.hideLoading()
      uni.showToast({ title: '导出失败', icon: 'none' })
      throw error
    }
  };

  // 清空状态
  const clearState = () => {
    summary.value = null;
    monthlyTrend.value = [];
    categoryReport.value = [];
    accountBalance.value = [];
    dateRange.value = null;
  };

  return {
    // 状态
    summary: computed(() => summary.value),
    monthlyTrend: computed(() => monthlyTrend.value),
    categoryReport: computed(() => categoryReport.value),
    accountBalance: computed(() => accountBalance.value),
    loading: computed(() => loading.value),
    dateRange: computed(() => dateRange.value),

    // 计算属性
    totalIncome,
    totalExpense,
    balance,
    incomePercentage,
    expensePercentage,
    accountBalanceSorted,
    trendData,

    // 方法
    fetchSummary,
    fetchMonthlyTrend,
    fetchCategoryReport,
    fetchAccountBalance,
    setDateRange,
    exportData,
    getCategoryColor,
    clearState,
  };
});
