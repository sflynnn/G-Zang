import { BaseEntity, TransactionType, AccountType, CategoryType } from './common';

// 账户实体
export interface Account extends BaseEntity {
  userId: number;
  companyId?: number;
  accountName: string;
  accountType: AccountType;
  balance: number;
}

// 分类实体
export interface Category extends BaseEntity {
  userId?: number;
  companyId?: number;
  categoryName: string;
  parentId: number;
  type: CategoryType;
  isSystem: boolean;
}

// 交易记录实体
export interface Transaction extends BaseEntity {
  userId: number;
  companyId?: number;
  amount: number;
  type: TransactionType;
  categoryId: number;
  accountId: number;
  transactionTime: string;
  remark?: string;
  relatedBusinessId?: string;
}

// 交易记录创建请求
export interface CreateTransactionRequest {
  amount: number;
  type: TransactionType;
  categoryId: number;
  accountId: number;
  transactionTime: string;
  remark?: string;
  relatedBusinessId?: string;
}

// 交易记录查询参数
export interface TransactionQueryParams {
  startDate?: string;
  endDate?: string;
  type?: TransactionType;
  categoryId?: number;
  accountId?: number;
  page?: number;
  size?: number;
}

// 收支汇总
export interface FinanceSummary {
  totalIncome: number;
  totalExpense: number;
  netIncome: number;
}
