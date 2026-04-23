/**
 * Transaction Store - 交易记录状态管理
 */
import { defineStore } from 'pinia';
import { ref, computed } from 'vue';
import type {
  Transaction,
  TransactionForm,
  TransactionFilters,
  TransactionGroup,
  TransactionSummary,
} from '@/types/transaction';
import { TransactionType } from '@/types/transaction';
import * as transactionApi from '@/api/transaction';

// G-Zang 品牌色
const BRAND_COLORS = {
  primary: '#0F4C5C',
  accent: '#FB8B24',
  success: '#06D6A0',
  danger: '#EF476F',
  warning: '#FFD166',
};

export interface TransactionState {
  transactionList: Transaction[];
  currentTransaction: Transaction | null;
  summary: TransactionSummary | null;
  categorySummary: Array<{
    categoryId: number;
    categoryName: string;
    categoryIcon: string;
    totalAmount: number;
    count: number;
    percentage: number;
  }>;
  loading: boolean;
  page: number;
  pageSize: number;
  hasMore: boolean;
}

export const useTransactionStore = defineStore('transaction', () => {
  // 状态
  const transactionList = ref<Transaction[]>([]);
  const currentTransaction = ref<Transaction | null>(null);
  const summary = ref<TransactionSummary | null>(null);
  const categorySummary = ref<TransactionState['categorySummary']>([]);
  const loading = ref(false);
  const page = ref(1);
  const pageSize = ref(20);
  const hasMore = ref(true);
  const currentFilters = ref<TransactionFilters>({});

  // 计算属性
  const incomeTransactions = computed(() =>
    transactionList.value.filter((t) => t.type === TransactionType.Income)
  );

  const expenseTransactions = computed(() =>
    transactionList.value.filter((t) => t.type === TransactionType.Expense)
  );

  const transferTransactions = computed(() =>
    transactionList.value // 后端 Transaction 无 Transfer 类型
  );

  // 按日期分组
  const groupedTransactions = computed((): TransactionGroup[] => {
    const groups: Record<string, TransactionGroup> = {};

    transactionList.value.forEach((t) => {
      const date = t.transactionTime.split('T')[0];
      if (!groups[date]) {
        groups[date] = {
          date,
          transactions: [],
          totalIncome: 0,
          totalExpense: 0,
        };
      }
      groups[date].transactions.push(t);
      if (t.type === TransactionType.Income) {
        groups[date].totalIncome += t.amount;
      } else if (t.type === TransactionType.Expense) {
        groups[date].totalExpense += t.amount;
      }
    });

    return Object.values(groups).sort(
      (a, b) => new Date(b.date).getTime() - new Date(a.date).getTime()
    );
  });

  // 获取交易类型颜色
  const getTransactionColor = (type: TransactionType) => {
    switch (type) {
      case TransactionType.Income:
        return BRAND_COLORS.success;
      case TransactionType.Expense:
        return BRAND_COLORS.danger;
      default:
        return BRAND_COLORS.primary;
    }
  };

  // 获取交易类型名称
  const getTransactionTypeName = (type: TransactionType) => {
    switch (type) {
      case TransactionType.Income:
        return '收入';
      case TransactionType.Expense:
        return '支出';
      default:
        return '未知';
    }
  };

  // 获取交易列表
  const fetchTransactions = async (filters?: TransactionFilters, resetPage = true) => {
    try {
      loading.value = true;

      if (resetPage) {
        page.value = 1;
        hasMore.value = true;
        transactionList.value = [];
      }

      currentFilters.value = { ...currentFilters.value, ...filters };

      const params = {
        current: page.value,
        size: pageSize.value,
        startTime: currentFilters.value.dateRange?.[0],
        endTime: currentFilters.value.dateRange?.[1],
        type: currentFilters.value.type,
        categoryId: currentFilters.value.categoryId,
      };

      const result = await transactionApi.getTransactions(params);

      if (resetPage) {
        transactionList.value = result.records;
      } else {
        transactionList.value.push(...result.records);
      }

      hasMore.value = transactionList.value.length < result.total;
      page.value += 1;

      return result;
    } catch (error) {
      throw error;
    } finally {
      loading.value = false;
    }
  };

  // 加载更多
  const loadMore = async () => {
    if (!hasMore.value || loading.value) return;
    await fetchTransactions(currentFilters.value, false);
  };

  // 创建交易（后端 DTO 不支持 targetAccountId/tags）
  const createTransaction = async (form: TransactionForm) => {
    try {
      loading.value = true;
      const newTransaction = await transactionApi.createTransaction({
        type: form.type,
        amount: form.amount,
        categoryId: form.categoryId,
        accountId: form.accountId,
        remark: form.remark,
        transactionTime: form.transactionTime,
      });

      // 添加到列表开头
      transactionList.value.unshift(newTransaction);

      // 更新统计
      await fetchSummary();
      await fetchCategorySummary();

      return newTransaction;
    } catch (error) {
      throw error;
    } finally {
      loading.value = false;
    }
  };

  // 更新交易
  const updateTransaction = async (id: number, form: Partial<TransactionForm>) => {
    try {
      loading.value = true;
      const updatedTransaction = await transactionApi.updateTransaction({
        id,
        ...form,
      });

      const index = transactionList.value.findIndex((t) => t.id === id);
      if (index !== -1) {
        transactionList.value[index] = updatedTransaction;
      }

      if (currentTransaction.value?.id === id) {
        currentTransaction.value = updatedTransaction;
      }

      return updatedTransaction;
    } catch (error) {
      throw error;
    } finally {
      loading.value = false;
    }
  };

  // 删除交易
  const deleteTransaction = async (id: number) => {
    try {
      loading.value = true;
      await transactionApi.deleteTransaction(id);

      transactionList.value = transactionList.value.filter((t) => t.id !== id);

      if (currentTransaction.value?.id === id) {
        currentTransaction.value = null;
      }

      return true;
    } catch (error) {
      throw error;
    } finally {
      loading.value = false;
    }
  };

  const fetchSummary = async (params?: { startDate?: string; endDate?: string }) => {
    try {
      const data = await transactionApi.getTransactionSummary({
        startTime: params?.startDate,
        endTime: params?.endDate
      });
      summary.value = data;
      return data;
    } catch (error) {
      throw error;
    }
  };

  // 获取分类统计
  const fetchCategorySummary = async (params?: {
    startDate?: string;
    endDate?: string;
    type?: TransactionType;
  }) => {
    try {
      const data = await transactionApi.getCategorySummary({
        startTime: params?.startDate,
        endTime: params?.endDate,
        type: params?.type
      });
      categorySummary.value = data.map(item => ({
        categoryId: item.categoryId,
        categoryName: item.categoryName,
        categoryIcon: '',
        totalAmount: item.totalAmount,
        count: item.count,
        percentage: item.percentage || 0
      }));
      return categorySummary.value;
    } catch (error) {
      throw error;
    }
  };

  // 获取交易详情
  const fetchTransaction = async (id: number) => {
    try {
      loading.value = true;
      const data = await transactionApi.getTransaction(id);
      currentTransaction.value = data;
      return data;
    } catch (error) {
      throw error;
    } finally {
      loading.value = false;
    }
  };

  // 清空状态
  const clearState = () => {
    transactionList.value = [];
    currentTransaction.value = null;
    summary.value = null;
    categorySummary.value = [];
    page.value = 1;
    hasMore.value = true;
    currentFilters.value = {};
  };

  return {
    // 状态
    transactionList: computed(() => transactionList.value),
    currentTransaction: computed(() => currentTransaction.value),
    summary: computed(() => summary.value),
    categorySummary: computed(() => categorySummary.value),
    loading: computed(() => loading.value),
    hasMore: computed(() => hasMore.value),

    // 计算属性
    incomeTransactions,
    expenseTransactions,
    transferTransactions,
    groupedTransactions,

    // 方法
    fetchTransactions,
    loadMore,
    createTransaction,
    updateTransaction,
    deleteTransaction,
    fetchSummary,
    fetchCategorySummary,
    fetchTransaction,
    getTransactionColor,
    getTransactionTypeName,
    clearState,
  };
});
