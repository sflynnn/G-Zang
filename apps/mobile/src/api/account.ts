/**
 * 账户相关 API
 * MobileAccountController - /api/mobile/accounts
 * 对应后端: server/gzang-mobile-api/.../MobileAccountController.java
 */
import { api } from './index';
import type { Account, AccountType } from '@/types/account';

/** 后端 CreateAccountDTO 字段（与前端表单字段名不同） */
export interface CreateAccountDTO {
  accountName: string;
  accountType: number;
  balance?: number;
}

/** 后端 UpdateAccountDTO 字段 */
export interface UpdateAccountDTO extends Partial<CreateAccountDTO> {
  id: number;
}

/**
 * 获取账户列表
 * GET /api/mobile/accounts
 */
export async function getAccounts(options?: { skipLoading?: boolean }): Promise<Account[]> {
  return api.get<Account[]>('/accounts', undefined, options);
}

/**
 * 获取账户详情
 * GET /api/mobile/accounts/{id}
 */
export async function getAccount(id: number, options?: { skipLoading?: boolean }): Promise<Account> {
  return api.get<Account>(`/accounts/${id}`, undefined, options);
}

/**
 * 创建账户
 * POST /api/mobile/accounts
 * 字段映射: name → accountName, type → accountType, initialBalance → balance
 */
export async function createAccount(data: {
  name: string;
  type: AccountType | number;
  initialBalance?: number;
  icon?: string;
  color?: string;
  remark?: string;
}): Promise<Account> {
  return api.post<Account>('/accounts', {
    accountName: data.name,
    accountType: data.type,
    balance: data.initialBalance ?? 0,
  });
}

/**
 * 更新账户
 * PUT /api/mobile/accounts/{id}
 * 字段映射: name → accountName, type → accountType
 */
export async function updateAccount(data: {
  id: number;
  name?: string;
  type?: AccountType | number;
}): Promise<Account> {
  const payload: Record<string, any> = { id: data.id };
  if (data.name !== undefined) payload.accountName = data.name;
  if (data.type !== undefined) payload.accountType = data.type;
  return api.put<Account>(`/accounts/${data.id}`, payload);
}

/**
 * 删除账户
 * DELETE /api/mobile/accounts/{id}
 */
export async function deleteAccount(id: number): Promise<void> {
  return api.delete(`/accounts/${id}`);
}

/**
 * 获取账户总余额
 * GET /api/mobile/accounts/total-balance
 */
export async function getTotalBalance(): Promise<number> {
  return api.get('/accounts/total-balance', undefined, { skipLoading: true });
}

/**
 * 获取账户分页列表
 * GET /api/mobile/accounts/page
 */
export async function getAccountPage(params?: {
  current?: number;
  size?: number;
}): Promise<{ records: Account[]; total: number; current: number; size: number }> {
  return api.get('/accounts/page', params);
}

export const accountApi = {
  getAccounts,
  getAccount,
  createAccount,
  updateAccount,
  deleteAccount,
  getTotalBalance,
  getAccountPage,
};

export default accountApi;
