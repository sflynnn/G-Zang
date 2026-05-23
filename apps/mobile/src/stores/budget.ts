/**
 * Budget Store - 预算状态管理
 */
import { defineStore } from 'pinia';
import { ref, computed } from 'vue';
import type { Budget, CreateBudgetRequest, UpdateBudgetRequest } from '@/api/budget';
import * as budgetApi from '@/api/budget';

export const useBudgetStore = defineStore('budget', () => {
  // 状态
  const budgetList = ref<Budget[]>([]);
  const currentBudget = ref<Budget | null>(null);
  const warningBudgets = ref<Budget[]>([]);
  const loading = ref(false);

  // 计算属性
  const totalBudgetAmount = computed(() =>
    budgetList.value.reduce((sum, b) => sum + b.amount, 0)
  );

  const totalUsedAmount = computed(() =>
    budgetList.value.reduce((sum, b) => sum + b.usedAmount, 0)
  );

  const totalRemainingAmount = computed(() =>
    budgetList.value.reduce((sum, b) => sum + (b.amount - b.usedAmount), 0)
  );

  const warningBudgetCount = computed(() =>
    budgetList.value.filter(b => b.isWarning).length
  );

  const budgetsByPeriodType = computed(() => {
    const grouped: Record<number, Budget[]> = {};
    budgetList.value.forEach(budget => {
      if (!grouped[budget.periodType]) {
        grouped[budget.periodType] = [];
      }
      grouped[budget.periodType].push(budget);
    });
    return grouped;
  });

  const monthlyBudgets = computed(() =>
    budgetList.value.filter(b => b.periodType === 1)
  );

  const yearlyBudgets = computed(() =>
    budgetList.value.filter(b => b.periodType === 2)
  );

  // 获取预算列表
  const fetchBudgets = async (params?: { bookId?: number; periodType?: number }) => {
    try {
      loading.value = true;
      const data = await budgetApi.getBudgets(params);
      budgetList.value = data;
      return data;
    } catch (error) {
      throw error;
    } finally {
      loading.value = false;
    }
  };

  // 获取预算详情
  const fetchBudget = async (id: number) => {
    try {
      loading.value = true;
      const data = await budgetApi.getBudget(id);
      currentBudget.value = data;
      return data;
    } catch (error) {
      throw error;
    } finally {
      loading.value = false;
    }
  };

  // 创建预算
  const createBudget = async (data: CreateBudgetRequest) => {
    try {
      loading.value = true;
      const newBudget = await budgetApi.createBudget(data);
      budgetList.value.push(newBudget);
      return newBudget;
    } catch (error) {
      throw error;
    } finally {
      loading.value = false;
    }
  };

  // 更新预算
  const updateBudget = async (id: number, data: UpdateBudgetRequest) => {
    try {
      loading.value = true;
      const updatedBudget = await budgetApi.updateBudget(id, data);
      const index = budgetList.value.findIndex(b => b.id === id);
      if (index !== -1) {
        budgetList.value[index] = updatedBudget;
      }
      if (currentBudget.value?.id === id) {
        currentBudget.value = updatedBudget;
      }
      return updatedBudget;
    } catch (error) {
      throw error;
    } finally {
      loading.value = false;
    }
  };

  // 删除预算
  const deleteBudget = async (id: number) => {
    try {
      loading.value = true;
      await budgetApi.deleteBudget(id);
      budgetList.value = budgetList.value.filter(b => b.id !== id);
      if (currentBudget.value?.id === id) {
        currentBudget.value = null;
      }
      return true;
    } catch (error) {
      throw error;
    } finally {
      loading.value = false;
    }
  };

  // 获取预警预算
  const fetchWarningBudgets = async () => {
    try {
      loading.value = true;
      const data = await budgetApi.getWarningBudgets();
      warningBudgets.value = data;
      return data;
    } catch (error) {
      throw error;
    } finally {
      loading.value = false;
    }
  };

  // 刷新预算使用金额
  const refreshUsedAmount = async (id: number) => {
    try {
      loading.value = true;
      await budgetApi.refreshBudgetUsedAmount(id);
      // 重新获取预算详情
      await fetchBudget(id);
    } catch (error) {
      throw error;
    } finally {
      loading.value = false;
    }
  };

  // 清空状态
  const clearState = () => {
    budgetList.value = [];
    currentBudget.value = null;
    warningBudgets.value = [];
  };

  return {
    // 状态
    budgetList: computed(() => budgetList.value),
    currentBudget: computed(() => currentBudget.value),
    warningBudgets: computed(() => warningBudgets.value),
    loading: computed(() => loading.value),

    // 计算属性
    totalBudgetAmount,
    totalUsedAmount,
    totalRemainingAmount,
    warningBudgetCount,
    budgetsByPeriodType,
    monthlyBudgets,
    yearlyBudgets,

    // 方法
    fetchBudgets,
    fetchBudget,
    createBudget,
    updateBudget,
    deleteBudget,
    fetchWarningBudgets,
    refreshUsedAmount,
    clearState,
  };
});
