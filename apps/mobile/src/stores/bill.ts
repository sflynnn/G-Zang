import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import * as transactionApi from '@/api/transaction'

// 类型定义
export interface Transaction {
  id: number
  type: 1 | 2 | 3 // 1: 收入, 2: 支出, 3: 转账
  amount: number
  categoryId: number
  categoryName: string
  accountId: number
  accountName: string
  targetAccountId?: number
  targetAccountName?: string
  remark?: string
  transactionTime: string
  createTime: string
  tags?: string[]
}

export interface BillState {
  transactions: Transaction[]
  loading: boolean
  finished: boolean
  page: number
  size: number
  total: number
  filters: {
    type?: number
    categoryId?: number
    accountId?: number
    dateRange?: string[]
    keyword?: string
  }
}

// Store定义
export const useBillStore = defineStore('bill', () => {
  // 状态
  const transactions = ref<Transaction[]>([])
  const loading = ref(false)
  const finished = ref(false)
  const page = ref(1)
  const size = ref(20)
  const total = ref(0)
  const filters = ref({
    type: undefined as number | undefined,
    categoryId: undefined as number | undefined,
    accountId: undefined as number | undefined,
    dateRange: undefined as string[] | undefined,
    keyword: undefined as string | undefined
  })

  // 计算属性
  const hasMore = computed(() => transactions.value.length < total.value)
  const filteredTransactions = computed(() => {
    let result = transactions.value

    // 类型筛选
    if (filters.value.type) {
      result = result.filter(t => t.type === filters.value.type)
    }

    // 分类筛选
    if (filters.value.categoryId) {
      result = result.filter(t => t.categoryId === filters.value.categoryId)
    }

    // 账户筛选
    if (filters.value.accountId) {
      result = result.filter(t => t.accountId === filters.value.accountId)
    }

    // 关键词搜索
    if (filters.value.keyword) {
      const keyword = filters.value.keyword.toLowerCase()
      result = result.filter(t =>
        t.categoryName.toLowerCase().includes(keyword) ||
        t.accountName.toLowerCase().includes(keyword) ||
        t.remark?.toLowerCase().includes(keyword)
      )
    }

    return result
  })

  // 加载交易记录
  const loadTransactions = async (loadMore = false) => {
    try {
      loading.value = true

      if (!loadMore) {
        page.value = 1
        transactions.value = []
        finished.value = false
      }

      const params = {
        page: page.value,
        size: size.value,
        ...filters.value
      }

      const data = await transactionApi.getTransactions(params)

      if (loadMore) {
        transactions.value.push(...data.records as Transaction[])
      } else {
        transactions.value = data.records as Transaction[]
      }

      total.value = data.total
      finished.value = transactions.value.length >= total.value

      if (!finished.value) {
        page.value++
      }

      return data
    } catch (error) {
      throw error
    } finally {
      loading.value = false
    }
  }

  // 刷新数据
  const refresh = async () => {
    page.value = 1
    finished.value = false
    await loadTransactions(false)
  }

  // 设置筛选条件
  const setFilters = (newFilters: Partial<{
    type?: number
    categoryId?: number
    accountId?: number
    dateRange?: string[]
    keyword?: string
  }>) => {
    filters.value = { ...filters.value, ...newFilters }
  }

  // 清空筛选条件
  const clearFilters = () => {
    filters.value = {
      type: undefined as number | undefined,
      categoryId: undefined as number | undefined,
      accountId: undefined as number | undefined,
      dateRange: undefined as string[] | undefined,
      keyword: undefined as string | undefined
    }
  }

  // 删除交易记录
  const deleteTransaction = async (id: number) => {
    try {
      loading.value = true
      await transactionApi.deleteTransaction(id)

      // 从列表中移除
      transactions.value = transactions.value.filter(t => t.id !== id)
      total.value--

      return true
    } catch (error) {
      throw error
    } finally {
      loading.value = false
    }
  }

  // 批量删除
  const batchDelete = async (ids: number[]) => {
    try {
      loading.value = true
      await Promise.all(ids.map(id => transactionApi.deleteTransaction(id)))

      // 从列表中移除
      transactions.value = transactions.value.filter(t => !ids.includes(t.id))
      total.value -= ids.length

      return true
    } catch (error) {
      throw error
    } finally {
      loading.value = false
    }
  }

  // 获取交易详情
  const getTransactionDetail = async (id: number) => {
    try {
      loading.value = true
      const data = await transactionApi.getTransaction(id)
      return data
    } catch (error) {
      throw error
    } finally {
      loading.value = false
    }
  }

  // 清空状态
  const clearState = () => {
    transactions.value = []
    loading.value = false
    finished.value = false
    page.value = 1
    total.value = 0
    clearFilters()
  }

  return {
    // 状态
    transactions: computed(() => transactions.value),
    loading: computed(() => loading.value),
    finished: computed(() => finished.value),
    page: computed(() => page.value),
    size: computed(() => size.value),
    total: computed(() => total.value),
    filters: computed(() => filters.value),

    // 计算属性
    hasMore,
    filteredTransactions,

    // 方法
    loadTransactions,
    refresh,
    setFilters,
    clearFilters,
    deleteTransaction,
    batchDelete,
    getTransactionDetail,
    clearState
  }
})
