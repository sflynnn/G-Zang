import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { accountingApi } from '@shared/api'

// 类型定义
export interface Category {
  id: number
  name: string
  icon: string
  type: 1 | 2 // 1: 收入, 2: 支出
  parentId?: number
  children?: Category[]
}

export interface Account {
  id: number
  name: string
  type: string
  balance: number
  currency: string
}

export interface TransactionForm {
  type: 1 | 2 | 3 // 1: 收入, 2: 支出, 3: 转账
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
    accounts.value.filter(account => account.balance > 0)
  )

  // 加载分类
  const loadCategories = async () => {
    try {
      loading.value = true
      const response = await accountingApi.getCategories()
      categories.value = response.data

      // 缓存最近使用的分类
      loadRecentCategories()
    } catch (error) {
      console.error('加载分类失败:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  // 加载账户
  const loadAccounts = async () => {
    try {
      loading.value = true
      const response = await accountingApi.getAccounts()
      accounts.value = response.data

      // 缓存最近使用的账户
      loadRecentAccounts()
    } catch (error) {
      console.error('加载账户失败:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  // 创建交易
  const createTransaction = async (form: TransactionForm) => {
    try {
      loading.value = true
      const response = await accountingApi.createTransaction(form)

      // 更新账户余额
      await loadAccounts()

      // 更新最近使用的分类和账户
      updateRecentCategory(form.categoryId)
      updateRecentAccount(form.accountId)

      return response
    } catch (error) {
      console.error('创建交易失败:', error)
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
        console.error('解析最近分类失败:', error)
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
        console.error('解析最近账户失败:', error)
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

  // 获取账户信息
  const getAccountInfo = (accountId: number) => {
    return accounts.value.find(a => a.id === accountId)
  }

  // 清空状态
  const clearState = () => {
    categories.value = []
    accounts.value = []
    recentCategories.value = []
    recentAccounts.value = []
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
    getAccountInfo,
    clearState
  }
})
