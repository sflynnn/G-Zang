/**
 * Account Store - 账户状态管理
 */
import { defineStore } from 'pinia';
import { ref, computed } from 'vue';
import { Account, AccountForm, AccountSummary, AccountType } from '@/types/account';
import * as accountApi from '@/api/account';

export interface AccountState {
  accountList: Account[];
  totalBalance: AccountSummary | null;
  loading: boolean;
}

// G-Zang 品牌色
const BRAND_COLORS = {
  primary: '#0F4C5C',
  accent: '#FB8B24',
  success: '#06D6A0',
  danger: '#EF476F',
  warning: '#FFD166',
};

// 账户类型对应的颜色（后端 accountType 为数字 1=现金, 2=银行卡, 3=电子支付）
const ACCOUNT_TYPE_COLORS: Record<string, string> = {
  '1': BRAND_COLORS.success,  // 现金
  '2': BRAND_COLORS.primary,  // 银行卡
  '3': BRAND_COLORS.accent,   // 电子支付
};

export const useAccountStore = defineStore('account', () => {
  // 状态
  const accountList = ref<Account[]>([]);
  const totalBalance = ref<AccountSummary | null>(null);
  const loading = ref(false);

  // 计算属性
  const activeAccounts = computed(() =>
    accountList.value
  );

  const totalAssets = computed(() =>
    accountList.value
      .reduce((sum, a) => sum + Number(a.balance), 0)
  );

  const totalLiabilities = computed(() => 0);
  const netAssets = computed(() => totalAssets.value);

  const accountsByType = computed(() => {
    const grouped: Record<number, typeof accountList.value> = {};
    accountList.value.forEach((account) => {
      const type = Number(account.accountType);
      if (!grouped[type]) {
        grouped[type] = [];
      }
      grouped[type].push(account);
    });
    return grouped;
  });

  const cashAccounts = computed(() =>
    accountList.value.filter((a) => Number(a.accountType) === AccountType.Cash)
  );

  const bankCardAccounts = computed(() =>
    accountList.value.filter((a) => Number(a.accountType) === AccountType.BankCard)
  );

  const creditCardAccounts = computed(() =>
    accountList.value.filter((a) => Number(a.accountType) === AccountType.Electronic)
  );

  const eWalletAccounts = computed(() =>
    accountList.value.filter((a) => Number(a.accountType) === AccountType.Electronic)
  );

  // 获取账户颜色（后端无 color/icon，按类型映射默认值）
  const getAccountColor = (account: Account) => {
    return ACCOUNT_TYPE_COLORS[String(account.accountType)] || BRAND_COLORS.primary;
  };

  // 获取账户图标（后端无 icon，按类型映射默认值）
  const getAccountIcon = (account: Account) => {
    const typeIcons: Record<string, string> = {
      '1': '💵', // 现金
      '2': '🏦', // 银行卡
      '3': '📱', // 电子支付
    };
    return typeIcons[String(account.accountType)] || '💰';
  };

  // 获取账户列表
  const fetchAccounts = async () => {
    try {
      loading.value = true;
      const data = await accountApi.getAccounts({ skipLoading: true });
      accountList.value = data;
      return data;
    } catch (error) {
      throw error;
    } finally {
      loading.value = false;
    }
  };

  // 创建账户
  const createAccount = async (form: AccountForm) => {
    try {
      loading.value = true;
      const newAccount = await accountApi.createAccount({
        name: form.name,
        type: form.type,
        initialBalance: form.initialBalance,
      });

      accountList.value.push(newAccount);
      return newAccount;
    } catch (error) {
      throw error;
    } finally {
      loading.value = false;
    }
  };

  // 更新账户
  const updateAccount = async (id: number, form: Partial<AccountForm>) => {
    try {
      loading.value = true;
      const updatedAccount = await accountApi.updateAccount({
        id,
        ...form,
      });

      const index = accountList.value.findIndex((a) => a.id === id);
      if (index !== -1) {
        accountList.value[index] = updatedAccount;
      }

      return updatedAccount;
    } catch (error) {
      throw error;
    } finally {
      loading.value = false;
    }
  };

  // 删除账户
  const deleteAccount = async (id: number) => {
    try {
      loading.value = true;
      await accountApi.deleteAccount(id);
      accountList.value = accountList.value.filter((a) => a.id !== id);
      return true;
    } catch (error) {
      throw error;
    } finally {
      loading.value = false;
    }
  };

  // 获取账户总余额
  const fetchTotalBalance = async () => {
    try {
      loading.value = true;
      const total = await accountApi.getTotalBalance();
      // 后端直接返回 BigDecimal number，构造 Summary 对象
      totalBalance.value = {
        totalAssets: total,
        totalLiabilities: 0,
        netAssets: total,
        accountCount: accountList.value.length,
      };
      return total;
    } catch (error) {
      throw error;
    } finally {
      loading.value = false;
    }
  };

  // 获取账户信息
  const getAccountInfo = (accountId: number) => {
    return accountList.value.find((a) => a.id === accountId);
  };

  // 更新账户余额（本地，用于即时反馈）
  const updateLocalBalance = (accountId: number, amount: number) => {
    const account = accountList.value.find((a) => a.id === accountId);
    if (account) {
      account.balance = (account.balance || 0) + amount;
    }
  };

  // 清空状态
  const clearState = () => {
    accountList.value = [];
    totalBalance.value = null;
  };

  return {
    // 状态
    accountList: computed(() => accountList.value),
    totalBalance: computed(() => totalBalance.value),
    loading: computed(() => loading.value),

    // 计算属性
    activeAccounts,
    totalAssets,
    totalLiabilities,
    netAssets,
    accountsByType,
    cashAccounts,
    bankCardAccounts,
    creditCardAccounts,
    eWalletAccounts,

    // 方法
    fetchAccounts,
    createAccount,
    updateAccount,
    deleteAccount,
    fetchTotalBalance,
    getAccountInfo,
    getAccountColor,
    getAccountIcon,
    updateLocalBalance,
    clearState,
  };
});
