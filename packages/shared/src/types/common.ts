// 通用类型定义

export interface BaseEntity {
  id: number;
  createTime: string;
  updateTime: string;
}

export interface Result<T = any> {
  code: number;
  message: string;
  data: T;
}

export interface PageResult<T> {
  total: number;
  records: T[];
  current: number;
  size: number;
  pages: number;
}

// 枚举类型
export enum TransactionType {
  INCOME = 1,
  EXPENSE = 2
}

export enum AccountType {
  CASH = 1,
  BANK_CARD = 2,
  ELECTRONIC_PAYMENT = 3
}

export enum UserStatus {
  DISABLED = 0,
  NORMAL = 1
}

export enum CategoryType {
  INCOME = 1,
  EXPENSE = 2
}
