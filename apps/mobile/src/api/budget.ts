/**
 * 预算相关 API
 * 后端接口: /api/mobile/budgets
 */
import { api } from './index';

/** 预算响应对象 */
export interface Budget {
  id: number
  userId: number
  companyId?: number
  bookId?: number
  categoryId?: number
  categoryName?: string
  categoryIcon?: string
  categoryColor?: string
  amount: number
  usedAmount: number
  remainingAmount: number
  usageRate: number
  periodType: number // 1=月预算, 2=年预算, 3=周预算
  periodTypeName: string
  periodStart: string
  periodEnd: string
  name: string
  warningThreshold: number
  warningEnabled: boolean
  isWarning: boolean
  remark?: string
  createTime: string
  updateTime?: string
}

/** 创建预算请求 */
export interface CreateBudgetRequest {
  name: string
  bookId?: number
  categoryId?: number
  amount: number
  periodType: number
  periodStart?: string
  periodEnd?: string
  warningThreshold?: number
  warningEnabled?: boolean
  remark?: string
}

/** 更新预算请求 */
export interface UpdateBudgetRequest {
  name?: string
  amount?: number
  periodStart?: string
  periodEnd?: string
  warningThreshold?: number
  warningEnabled?: boolean
  remark?: string
}

/**
 * 获取预算列表
 */
export async function getBudgets(params?: {
  bookId?: number
  periodType?: number
}): Promise<Budget[]> {
  return api.get<Budget[]>('/budgets', params);
}

/**
 * 获取预算详情
 */
export async function getBudget(id: number): Promise<Budget> {
  return api.get<Budget>(`/budgets/${id}`);
}

/**
 * 创建预算
 */
export async function createBudget(data: CreateBudgetRequest): Promise<Budget> {
  return api.post<Budget>('/budgets', data);
}

/**
 * 更新预算
 */
export async function updateBudget(id: number, data: UpdateBudgetRequest): Promise<Budget> {
  return api.put<Budget>(`/budgets/${id}`, data);
}

/**
 * 删除预算
 */
export async function deleteBudget(id: number): Promise<void> {
  return api.delete(`/budgets/${id}`);
}

/**
 * 获取预警预算列表
 */
export async function getWarningBudgets(): Promise<Budget[]> {
  return api.get<Budget[]>('/budgets/warnings');
}

/**
 * 刷新预算使用金额
 */
export async function refreshBudgetUsedAmount(id: number): Promise<void> {
  return api.post(`/budgets/${id}/refresh-used`);
}

export const budgetApi = {
  getBudgets,
  getBudget,
  createBudget,
  updateBudget,
  deleteBudget,
  getWarningBudgets,
  refreshBudgetUsedAmount,
};

export default budgetApi;
