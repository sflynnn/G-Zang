import { defineStore } from 'pinia';
import { Transaction, Account, Category, FinanceSummary } from '@gzang/shared';

interface PersonalState {
  transactions: Transaction[];
  accounts: Account[];
  categories: Category[];
  summary: FinanceSummary | null;
  loading: boolean;
}

export const usePersonalStore = defineStore('personal', {
  state: (): PersonalState => ({
    transactions: [],
    accounts: [],
    categories: [],
    summary: null,
    loading: false
  }),

  getters: {
    // 获取收入分类
    incomeCategories: (state) => state.categories.filter(c => c.type === 1),

    // 获取支出分类
    expenseCategories: (state) => state.categories.filter(c => c.type === 2),

    // 获取总收入
    totalIncome: (state) => {
      return state.transactions
        .filter(t => t.type === 1)
        .reduce((sum, t) => sum + t.amount, 0);
    },

    // 获取总支出
    totalExpense: (state) => {
      return state.transactions
        .filter(t => t.type === 2)
        .reduce((sum, t) => sum + t.amount, 0);
    },

    // 获取净收入
    netIncome: (state) => {
      return state.totalIncome - state.totalExpense;
    }
  },

  actions: {
    // 设置加载状态
    setLoading(loading: boolean) {
      this.loading = loading;
    },

    // 设置交易记录
    setTransactions(transactions: Transaction[]) {
      this.transactions = transactions;
    },

    // 添加交易记录
    addTransaction(transaction: Transaction) {
      this.transactions.unshift(transaction);
    },

    // 更新交易记录
    updateTransaction(updatedTransaction: Transaction) {
      const index = this.transactions.findIndex(t => t.id === updatedTransaction.id);
      if (index > -1) {
        this.transactions[index] = updatedTransaction;
      }
    },

    // 删除交易记录
    removeTransaction(id: number) {
      const index = this.transactions.findIndex(t => t.id === id);
      if (index > -1) {
        this.transactions.splice(index, 1);
      }
    },

    // 设置账户列表
    setAccounts(accounts: Account[]) {
      this.accounts = accounts;
    },

    // 添加账户
    addAccount(account: Account) {
      this.accounts.push(account);
    },

    // 更新账户
    updateAccount(updatedAccount: Account) {
      const index = this.accounts.findIndex(a => a.id === updatedAccount.id);
      if (index > -1) {
        this.accounts[index] = updatedAccount;
      }
    },

    // 删除账户
    removeAccount(id: number) {
      const index = this.accounts.findIndex(a => a.id === id);
      if (index > -1) {
        this.accounts.splice(index, 1);
      }
    },

    // 设置分类列表
    setCategories(categories: Category[]) {
      this.categories = categories;
    },

    // 添加分类
    addCategory(category: Category) {
      this.categories.push(category);
    },

    // 更新分类
    updateCategory(updatedCategory: Category) {
      const index = this.categories.findIndex(c => c.id === updatedCategory.id);
      if (index > -1) {
        this.categories[index] = updatedCategory;
      }
    },

    // 删除分类
    removeCategory(id: number) {
      const index = this.categories.findIndex(c => c.id === id);
      if (index > -1) {
        this.categories.splice(index, 1);
      }
    },

    // 设置财务汇总
    setSummary(summary: FinanceSummary) {
      this.summary = summary;
    },

    // 清除所有数据
    clearData() {
      this.transactions = [];
      this.accounts = [];
      this.categories = [];
      this.summary = null;
    }
  }
});
