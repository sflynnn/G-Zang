/**
 * 交易相关类型定义
 * 对齐后端: server/gzang-common/.../entity/Transaction.java
 * 对齐后端: server/gzang-common/.../dto/transaction/CreateTransactionDTO.java
 *
 * 设计原则：
 * - 后端字段（categoryId/accountId）: API 请求时使用
 * - 前端别名（categoryName/accountName...）: 组件展示兼容
 */

// 交易类型 (1=收入, 2=支出)
export enum TransactionType {
  Income = 1,   // 收入
  Expense = 2,  // 支出
}

// 分类 (对齐后端 Category 实体，同时保留前端别名)
export interface Category {
  // --- 后端实体字段 ---
  id: number;
  categoryName?: string; // 后端字段；前端模拟数据中用 name
  type: number;
  parentId?: number;

  // --- 前端别名 ---
  /** 别名: categoryName */
  name?: string;
  /** 后端 Category 实体无 icon，后端可能通过其他字段扩展 */
  icon?: string;
  children?: Category[];
  amount?: number;
  sortOrder?: number;
  isSystem?: boolean;
}

// 交易记录 (对齐后端 Transaction 实体，同时保留前端别名)
export interface Transaction {
  // --- 后端实体字段 ---
  id: number;
  type: TransactionType;
  amount: number;
  categoryId: number;
  accountId: number;
  transactionTime: string;
  remark?: string;
  userId?: number;
  companyId?: number;
  createTime?: string;
  updateTime?: string;

  // --- 前端别名（用于组件展示） ---
  /** 分类名称 */
  categoryName?: string;
  /** 分类图标 */
  categoryIcon?: string;
  /** 账户名称 */
  accountName?: string;
  /** 目标账户ID（后端 DTO 不支持） */
  targetAccountId?: number;
  /** 目标账户名称 */
  targetAccountName?: string;
  tags?: string[];
  attachments?: string[];
}

// 交易表单
export interface TransactionForm {
  type: TransactionType;
  amount: number;
  categoryId: number;
  accountId: number;
  transactionTime: string;
  remark?: string;
  targetAccountId?: number;
  tags?: string[];
}

// 交易统计
export interface TransactionSummary {
  totalIncome: number;
  totalExpense: number;
  balance: number;
  transactionCount: number;
}

// 交易列表响应
export interface TransactionListResponse {
  records: Transaction[];
  total: number;
  current: number;
  size: number;
}

// 交易筛选条件
export interface TransactionFilters {
  type?: TransactionType;
  categoryId?: number;
  accountId?: number;
  startDate?: string;
  endDate?: string;
  keyword?: string;
  minAmount?: number;
  maxAmount?: number;
  dateRange?: [string, string];
}

// 交易分组（按日期）
export interface TransactionGroup {
  date: string;
  transactions: Transaction[];
  totalIncome: number;
  totalExpense: number;
}
