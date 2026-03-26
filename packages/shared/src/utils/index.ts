// 工具函数
import { TransactionType, AccountType } from '../types';

// 格式化金额
export const formatAmount = (amount: number): string => {
  return new Intl.NumberFormat('zh-CN', {
    style: 'currency',
    currency: 'CNY'
  }).format(amount);
};

// 格式化日期
export const formatDate = (date: string | Date): string => {
  const d = new Date(date);
  return d.toLocaleDateString('zh-CN');
};

// 格式化日期时间
export const formatDateTime = (date: string | Date): string => {
  const d = new Date(date);
  return d.toLocaleString('zh-CN');
};

// 获取交易类型文本
export const getTransactionTypeText = (type: TransactionType): string => {
  switch (type) {
    case TransactionType.INCOME:
      return '收入';
    case TransactionType.EXPENSE:
      return '支出';
    default:
      return '未知';
  }
};

// 获取账户类型文本
export const getAccountTypeText = (type: AccountType): string => {
  switch (type) {
    case AccountType.CASH:
      return '现金';
    case AccountType.BANK_CARD:
      return '银行卡';
    case AccountType.ELECTRONIC_PAYMENT:
      return '电子支付';
    default:
      return '未知';
  }
};

// 防抖函数
export const debounce = <T extends (...args: any[]) => any>(
  func: T,
  wait: number
): ((...args: Parameters<T>) => void) => {
  let timeout: NodeJS.Timeout;
  return (...args: Parameters<T>) => {
    clearTimeout(timeout);
    timeout = setTimeout(() => func(...args), wait);
  };
};

// 节流函数
export const throttle = <T extends (...args: any[]) => any>(
  func: T,
  limit: number
): ((...args: Parameters<T>) => void) => {
  let inThrottle: boolean;
  return (...args: Parameters<T>) => {
    if (!inThrottle) {
      func(...args);
      inThrottle = true;
      setTimeout(() => inThrottle = false, limit);
    }
  };
};
