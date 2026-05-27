import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { getAccounts as apiGetAccounts } from '@/api/account'
import { getCategories as apiGetCategories, getCategoriesWithChildren as apiGetCategoriesWithChildren } from '@/api/category'
import { createTransaction as apiCreateTransaction } from '@/api/transaction'
import { getTags as apiGetTags, getFrequentTags as apiGetFrequentTags, createTag as apiCreateTag, useTag as apiUseTag } from '@/api/tag'
import { getPaymentMethods as apiGetPaymentMethods } from '@/api/paymentMethod'
import type { Account, Category, TransactionType } from '@/types'
import type { TagVO } from '@/api/tag'
import type { PaymentMethodVO } from '@/api/paymentMethod'

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
  paymentMethod?: string
}

// 分类额度信息
export interface CategoryBudget {
  budget: number
  spent: number
  remaining: number
  percentUsed?: number
  warningThreshold?: number
}

// 分类（带子分类和额度）
export interface CategoryWithChildren extends Category {
  children?: Category[]
  budget?: CategoryBudget
}

// 月度统计
export interface MonthStats {
  expense: number
  income: number
  balance: number
}

export interface AccountingState {
  categories: Category[]
  categoriesWithChildren: CategoryWithChildren[]
  accounts: Account[]
  tags: TagVO[]
  frequentTags: TagVO[]
  paymentMethods: PaymentMethodVO[]
  recentCategories: Category[]
  recentAccounts: Account[]
  currentMonthStats: MonthStats | null
  loading: boolean
}

// 将后端分类数据转换为前端格式
function transformCategory(category: any): Category {
  return {
    ...category,
    name: category.categoryName,
    isSystem: category.isSystem ? true : false,
    children: category.children?.map(transformCategory),
  }
}

// 将后端分类数据（带子分类和额度）转换为前端格式
function transformCategoryWithChildren(category: any): CategoryWithChildren {
  return {
    ...category,
    id: category.id,
    name: category.categoryName,
    icon: category.icon || '📂',
    color: category.color || '#6B7280',
    isSystem: category.isSystem ? true : false,
    children: category.children?.map((child: any) => ({
      ...child,
      id: child.id,
      name: child.categoryName,
      icon: child.icon || '📂',
      color: child.color || category.color || '#6B7280',
      parentId: child.parentId
    })),
    budget: category.budget ? {
      budget: category.budget.budget || 0,
      spent: category.budget.spent || 0,
      remaining: category.budget.remaining || 0,
      percentUsed: category.budget.percentUsed || 0,
      warningThreshold: category.budget.warningThreshold || 80
    } : undefined
  }
}

// 将后端账户数据转换为前端格式
function transformAccount(account: Account): Account {
  return {
    ...account,
    name: account.accountName, // 前端用 name 方便展示
    type: String(account.accountType), // 前端用 string 类型
    // 过滤掉后端返回的 currency 对象，防止 JSON 形式显示
    currency: typeof account.currency === 'string' ? account.currency : 'CNY',
  }
}

// Store定义
export const useAccountingStore = defineStore('accounting', () => {
  // 状态
  const categories = ref<Category[]>([])
  const categoriesWithChildren = ref<CategoryWithChildren[]>([])
  const accounts = ref<Account[]>([])
  const tags = ref<TagVO[]>([])
  const frequentTags = ref<TagVO[]>([])
  const paymentMethods = ref<PaymentMethodVO[]>([])
  const recentCategories = ref<Category[]>([])
  const recentAccounts = ref<Account[]>([])
  const currentMonthStats = ref<MonthStats | null>(null)
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

  // 加载带子分类和额度的分类
  const loadCategoriesWithChildren = async (bookId?: number, month?: string) => {
    try {
      loading.value = true
      const data = await apiGetCategoriesWithChildren(bookId, month)
      categoriesWithChildren.value = data.map(transformCategoryWithChildren)
      
      // 同时更新基础分类列表
      categories.value = data.map(transformCategory)

      // 缓存最近使用的分类
      loadRecentCategories()
    } catch (error: any) {
      // 如果API不支持（400错误），使用本地分类数据构建树
      console.warn('API /categories/with-children 不可用，使用本地数据:', error?.message)
      const allCategories = await apiGetCategories()
      categoriesWithChildren.value = buildCategoryTree(allCategories)
      categories.value = allCategories.map(transformCategory)
    } finally {
      loading.value = false
    }
  }

  // 将平铺分类构建为树形结构
  const buildCategoryTree = (flatCategories: any[]): CategoryWithChildren[] => {
    const tree: CategoryWithChildren[] = []
    
    flatCategories.forEach(cat => {
      if (!cat.parentId || cat.parentId === 0) {
        const item: CategoryWithChildren = {
          ...cat,
          name: cat.categoryName,
          children: []
        }
        
        // 查找子分类
        flatCategories.forEach(sub => {
          if (sub.parentId === cat.id) {
            item.children!.push({
              ...sub,
              id: sub.id,
              name: sub.categoryName,
              parentId: sub.parentId
            })
          }
        })
        
        tree.push(item)
      }
    })
    
    return tree
  }

  // 加载月度统计
  const loadMonthStats = async (bookId?: number, month?: string) => {
    // TODO: 调用API获取月度统计
    // 目前返回模拟数据
    currentMonthStats.value = {
      expense: 0,
      income: 0,
      balance: 0
    }
  }

  // 加载账户
  const loadAccounts = async () => {
    const data = await apiGetAccounts()
    accounts.value = data.map(transformAccount)

    // 缓存最近使用的账户
    loadRecentAccounts()
  }

  // 加载标签
  const loadTags = async () => {
    try {
      const data = await apiGetTags()
      tags.value = data
    } catch (error) {
      console.warn('加载标签失败:', error)
      tags.value = []
    }
  }

  // 加载常用标签
  const loadFrequentTags = async (limit: number = 8) => {
    try {
      const data = await apiGetFrequentTags(limit)
      frequentTags.value = data
    } catch (error) {
      console.warn('加载常用标签失败:', error)
      frequentTags.value = []
    }
  }

  // 加载支付方式
  const loadPaymentMethods = async () => {
    try {
      const data = await apiGetPaymentMethods()
      paymentMethods.value = data.filter(pm => pm.isEnabled === 1)
    } catch (error) {
      console.warn('加载支付方式失败:', error)
      paymentMethods.value = []
    }
  }

  // 创建标签
  const createTag = async (tagName: string, tagColor?: string) => {
    const created = await apiCreateTag({ tagName, tagColor })
    // 立即刷新列表（也可直接 push，但刷新能拿到后端排序/字段）
    await Promise.all([loadTags(), loadFrequentTags(8)])
    return created
  }

  // 记录标签使用
  const useTag = async (tagId: number) => {
    await apiUseTag(tagId)
    // 使用后常用列表可能变化，轻量刷新
    await loadFrequentTags(8)
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
        targetAccountId: form.targetAccountId,
        transactionTime: form.transactionTime,
        remark: form.remark,
        tags: form.tags,
        paymentMethod: form.paymentMethod
      } as any)

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

  // 获取账户详情（从API）
  const getAccountById = async (accountId: number) => {
    const existing = accounts.value.find(a => a.id === accountId)
    if (existing) return existing
    return await apiGetAccounts().then(data => data.find(a => a.id === accountId))
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
    tags.value = []
    frequentTags.value = []
    paymentMethods.value = []
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
    categoriesWithChildren: computed(() => categoriesWithChildren.value),
    accounts: computed(() => accounts.value),
    tags: computed(() => tags.value),
    frequentTags: computed(() => frequentTags.value),
    paymentMethods: computed(() => paymentMethods.value),
    recentCategories: computed(() => recentCategories.value),
    recentAccounts: computed(() => recentAccounts.value),
    currentMonthStats: computed(() => currentMonthStats.value),
    loading: computed(() => loading.value),

    // 计算属性
    incomeCategories,
    expenseCategories,
    activeAccounts,

    // 方法
    loadCategories,
    loadCategoriesWithChildren,
    loadMonthStats,
    loadAccounts,
    loadTags,
    loadFrequentTags,
    loadPaymentMethods,
    createTag,
    useTag,
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
