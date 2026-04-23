/**
 * 交易记录相关 API
 * MobileTransactionController - /api/mobile/transactions
 * 对应后端: server/gzang-mobile-api/.../MobileTransactionController.java
 */
import { api } from './index';
import type { Transaction, TransactionType } from '@/types/transaction';

/** 后端 CreateTransactionDTO 字段 */
export interface CreateTransactionDTO {
  amount: number;
  type: number;
  categoryId: number;
  accountId: number;
  transactionTime?: string;
  remark?: string;
}

/** 后端 UpdateTransactionDTO 字段 */
export interface UpdateTransactionDTO extends Partial<CreateTransactionDTO> {
  id: number;
}

/** 分页查询参数 */
export interface TransactionQueryParams {
  current?: number;
  size?: number;
  startTime?: string;
  endTime?: string;
  type?: TransactionType;
  categoryId?: number;
}

/**
 * 获取交易记录列表（分页）
 * GET /api/mobile/transactions
 */
export async function getTransactions(
  params?: TransactionQueryParams
): Promise<{ records: Transaction[]; total: number; current: number; size: number }> {
  return api.get('/transactions', params);
}

/**
 * 获取交易记录详情
 * GET /api/mobile/transactions/{id}
 */
export async function getTransaction(id: number): Promise<Transaction> {
  return api.get<Transaction>(`/transactions/${id}`);
}

/**
 * 创建交易记录
 * POST /api/mobile/transactions
 */
export async function createTransaction(data: CreateTransactionDTO): Promise<Transaction> {
  return api.post<Transaction>('/transactions', data);
}

/**
 * 更新交易记录
 * PUT /api/mobile/transactions/{id}
 */
export async function updateTransaction(data: UpdateTransactionDTO): Promise<Transaction> {
  return api.put<Transaction>(`/transactions/${data.id}`, data);
}

/**
 * 删除交易记录
 * DELETE /api/mobile/transactions/{id}
 */
export async function deleteTransaction(id: number): Promise<void> {
  return api.delete(`/transactions/${id}`);
}

/**
 * 获取交易汇总统计
 * GET /api/mobile/transactions/summary
 */
export async function getTransactionSummary(params?: {
  startTime?: string;
  endTime?: string;
}): Promise<{
  totalIncome: number;
  totalExpense: number;
  balance: number;
  transactionCount: number;
}> {
  return api.get('/transactions/summary', params);
}

/**
 * 按分类统计交易金额
 * GET /api/mobile/transactions/category-summary
 */
export async function getCategorySummary(params?: {
  startTime?: string;
  endTime?: string;
  type?: TransactionType;
}): Promise<
  Array<{
    categoryId: number;
    categoryName: string;
    totalAmount: number;
    count: number;
    percentage?: number;
  }>
> {
  return api.get('/transactions/category-summary', params);
}

export const transactionApi = {
  getTransactions,
  getTransaction,
  createTransaction,
  updateTransaction,
  deleteTransaction,
  getTransactionSummary,
  getCategorySummary,
};

export default transactionApi;
