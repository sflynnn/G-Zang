import httpClient from './http';
import {
  Transaction,
  CreateTransactionRequest,
  TransactionQueryParams,
  Account,
  Category,
  PageResult,
  FinanceSummary,
  Result
} from '../types';

// 记账相关API
export const accountingApi = {
  // 交易记录相关
  createTransaction: (data: CreateTransactionRequest): Promise<Result<void>> => {
    return httpClient.post('/accounting/transaction', data);
  },

  updateTransaction: (id: number, data: Partial<CreateTransactionRequest>): Promise<Result<void>> => {
    return httpClient.put(`/accounting/transaction/${id}`, data);
  },

  deleteTransaction: (id: number): Promise<Result<void>> => {
    return httpClient.delete(`/accounting/transaction/${id}`);
  },

  getTransaction: (id: number): Promise<Result<Transaction>> => {
    return httpClient.get(`/accounting/transaction/${id}`);
  },

  getTransactions: (params?: TransactionQueryParams): Promise<Result<PageResult<Transaction>>> => {
    return httpClient.get('/accounting/transactions', { params });
  },

  // 账户相关
  getAccounts: (): Promise<Result<Account[]>> => {
    return httpClient.get('/accounting/accounts');
  },

  createAccount: (data: Omit<Account, 'id' | 'createTime' | 'updateTime'>): Promise<Result<void>> => {
    return httpClient.post('/accounting/account', data);
  },

  updateAccount: (id: number, data: Partial<Account>): Promise<Result<void>> => {
    return httpClient.put(`/accounting/account/${id}`, data);
  },

  deleteAccount: (id: number): Promise<Result<void>> => {
    return httpClient.delete(`/accounting/account/${id}`);
  },

  // 分类相关
  getCategories: (type?: number): Promise<Result<Category[]>> => {
    return httpClient.get('/accounting/categories', { params: { type } });
  },

  createCategory: (data: Omit<Category, 'id' | 'createTime' | 'updateTime'>): Promise<Result<void>> => {
    return httpClient.post('/accounting/category', data);
  },

  updateCategory: (id: number, data: Partial<Category>): Promise<Result<void>> => {
    return httpClient.put(`/accounting/category/${id}`, data);
  },

  deleteCategory: (id: number): Promise<Result<void>> => {
    return httpClient.delete(`/accounting/category/${id}`);
  }
};
