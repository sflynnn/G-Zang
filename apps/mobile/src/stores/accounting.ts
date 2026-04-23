import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { getAccounts as apiGetAccounts } from '@/api/account'
import { getCategories as apiGetCategories, type CategoryVO } from '@/api/category'
import { createTransaction as apiCreateTransaction } from '@/api/transaction'
import type { Account, Category, TransactionType } from '@/types'

// 类型定义
export type { Category, Account, TransactionType }
export type { TransactionForm, TransactionFilters, TransactionSummary } from '@/types'

// 交易表单类型（带 targetAccountId，用于转账）
export interface AccountingTransactionForm {
  type: TransactionType | 1 | 2 | 3 // 1: 收入, 2: 支出, 3: 转账
  amount: number
  categoryId: number
  accountId: number
  targetAccountId?: number // 转账目标账户
  remark?: string
  transactionTime: string
  tags?: string[]
}

export interface AccountingState {
  categories: Category[]
  accounts: Account[]
  recentCategories: Category[]
  recentAccounts: Account[]
  loading: boolean
}

// 将后端分类数据转换为前端格式
function transformCategory(category: CategoryVO): Category {
  return {
    ...category,
    name: category.categoryName, // 前端用 name 方便展示
  }
}

// 将后端账户数据转换为前端格式
function transformAccount(account: Account): Account {
  return {
    ...account,
    name: account.accountName, // 前端用 name 方便展示
    type: String(account.accountType), // 前端用 string 类型
    currency: account.currency || 'CNY', // 默认货币
  }
}

// Store定义
export const useAccountingStore = defineStore('accounting', () => {
  // 状态
  const categories = ref<Category[]>([])
  const accounts = ref<Account[]>([])
  const recentCategories = ref<Category[]>([])
  const recentAccounts = ref<Account[]>([])
  const loading = ref(false)

  // 计算属性
  const incomeCategories = computed(() =>
    categories.value.filter(cat => cat.type === 1)
  )

  const expenseCategories = computed(() =>
    categories.value.filter(cat => cat.type === 2)
  )

  const activeAccounts = computed(() =>
    accounts.value.filter(account => Number(account.balance) > 0)
  )

  // 加载分类
  const loadCategories = async () => {
    try {
      loading.value = true
      const data = await apiGetCategories()
      categories.value = data.map(transformCategory)

      // 缓存最近使用的分类
      loadRecentCategories()
    } catch (error) {
      throw error
    } finally {
      loading.value = false
    }
  }

  // 加载账户
  const loadAccounts = async () => {
    try {
      loading.value = true
      const data = await apiGetAccounts({ skipLoading: true })
      accounts.value = data.map(transformAccount)

      // 缓存最近使用的账户
      loadRecentAccounts()
    } catch (error) {
      throw error
    } finally {
      loading.value = false
    }
  }

  // 创建交易
  const createTransaction = async (form: AccountingTransactionForm) => {
    try {
      loading.value = true
      await apiCreateTransaction({
        amount: form.amount,
        type: form.type as number,
        categoryId: form.categoryId,
        accountId: form.accountId,
        transactionTime: form.transactionTime,
        remark: form.remark,
      })

      // 更新账户余额
      await loadAccounts()

      // 更新最近使用的分类和账户
      updateRecentCategory(form.categoryId)
      updateRecentAccount(form.accountId)
    } catch (error) {
      throw error
    } finally {
      loading.value = false
    }
  }

  // 加载最近使用的分类
  const loadRecentCategories = () => {
    const recent = uni.getStorageSync('recentCategories')
    if (recent) {
      try {
        recentCategories.value = JSON.parse(recent)
      } catch (error) {
        // ignore parse error
      }
    }
  }

  // 加载最近使用的账户
  const loadRecentAccounts = () => {
    const recent = uni.getStorageSync('recentAccounts')
    if (recent) {
      try {
        recentAccounts.value = JSON.parse(recent)
      } catch (error) {
        // ignore parse error
      }
    }
  }

  // 更新最近使用的分类
  const updateRecentCategory = (categoryId: number) => {
    const category = categories.value.find(c => c.id === categoryId)
    if (category) {
      const filtered = recentCategories.value.filter(c => c.id !== categoryId)
      recentCategories.value = [category, ...filtered].slice(0, 8) // 保留最近8个

      uni.setStorageSync('recentCategories', JSON.stringify(recentCategories.value))
    }
  }

  // 更新最近使用的账户
  const updateRecentAccount = (accountId: number) => {
    const account = accounts.value.find(a => a.id === accountId)
    if (account) {
      const filtered = recentAccounts.value.filter(a => a.id !== accountId)
      recentAccounts.value = [account, ...filtered].slice(0, 5) // 保留最近5个

      uni.setStorageSync('recentAccounts', JSON.stringify(recentAccounts.value))
    }
  }

  // 获取分类图标
  const getCategoryIcon = (categoryId: number) => {
    const category = categories.value.find(c => c.id === categoryId)
    return category?.icon || 'circle'
  }

  // 获取分类名称
  const getCategoryName = (categoryId: number) => {
    const category = categories.value.find(c => c.id === categoryId)
    return category?.name || category?.categoryName || '未知分类'
  }

  // 获取账户信息
  const getAccountInfo = (accountId: number) => {
    return accounts.value.find(a => a.id === accountId)
  }

  // 获取账户名称
  const getAccountName = (accountId: number) => {
    const account = getAccountInfo(accountId)
    return account?.name || account?.accountName || '未知账户'
  }

  // 清空状态
  const clearState = () => {
    categories.value = []
    accounts.value = []
    recentCategories.value = []
    recentAccounts.value = []
  }

  // 设置账户数据（用于模拟数据加载）
  const setAccounts = (data: Account[]) => {
    accounts.value = data
  }

  return {
    // 状态
    categories: computed(() => categories.value),
    accounts: computed(() => accounts.value),
    recentCategories: computed(() => recentCategories.value),
    recentAccounts: computed(() => recentAccounts.value),
    loading: computed(() => loading.value),

    // 计算属性
    incomeCategories,
    expenseCategories,
    activeAccounts,

    // 方法
    loadCategories,
    loadAccounts,
    createTransaction,
    loadRecentCategories,
    loadRecentAccounts,
    updateRecentCategory,
    updateRecentAccount,
    getCategoryIcon,
    getCategoryName,
    getAccountInfo,
    getAccountName,
    clearState,
    setAccounts
  }
})
