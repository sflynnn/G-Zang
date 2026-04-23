/**
 * 账户相关类型定义
 * 对齐后端: server/gzang-common/.../entity/Account.java
 *
 * 设计原则：
 * - 后端字段（accountName/accountType）: API 请求时使用
 * - 前端别名（name/type/icon/color...）: 组件展示兼容
 */

// 账户类型 (1=现金, 2=银行卡, 3=电子支付, 4=信用卡, 5=电子钱包, 6=投资, 7=债务, 8=其他)
export enum AccountType {
  Cash = 1,         // 现金
  BankCard = 2,     // 银行卡
  Electronic = 3,   // 电子支付
  CreditCard = 4,   // 信用卡
  EWallet = 5,      // 电子钱包
  Investment = 6,   // 投资账户
  Debt = 7,         // 债务
  Other = 8,        // 其他
}

// 账户 (对齐后端 Account 实体，同时保留前端别名)
export interface Account {
  // --- 后端实体字段 ---
  id: number;
  accountName?: string; // 后端字段；前端模拟数据中用 name
  accountType?: number; // 后端字段；前端模拟数据中用 type
  balance?: number;
  userId?: number;
  companyId?: number;
  createTime?: string;
  updateTime?: string;

  // --- 前端别名（用于组件展示，后端无此字段） ---
  /** 别名: accountName */
  name?: string;
  /** 别名: accountType */
  type?: AccountType | string;
  icon?: string;
  color?: string;
  remark?: string;
  isActive?: boolean;
  initialBalance?: number;
  currency?: string;
}

// 账户表单
export interface AccountForm {
  name: string;
  type: AccountType | number;
  initialBalance?: number;
  currency?: string;
  icon?: string;
  color?: string;
  remark?: string;
}

// 账户统计
export interface AccountSummary {
  totalAssets: number;
  totalLiabilities: number;
  netAssets: number;
  accountCount: number;
}
