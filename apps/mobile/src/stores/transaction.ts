/**
 * Transaction Store - 交易记录状态管理
 * 替代页面内的 mock 数据，使用真实 API
 */
import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import {
  getTransactions,
  getTransaction,
  createTransaction as apiCreateTransaction,
  updateTransaction as apiUpdateTransaction,
  deleteTransaction as apiDeleteTransaction,
  getCalendarTransactions,
  type TransactionQueryParams,
} from '@/api/transaction'
import type { Transaction } from '@/types/transaction'

export interface TransactionState {
  transactions: Transaction[]
  recentTransactions: Transaction[]
  calendarData: Record<string, { income: number; expense: number; count: number }>
  total: number
  loading: boolean
  current: number
  pageSize: number
}

export const useTransactionStore = defineStore('transaction', () => {
  const transactions = ref<Transaction[]>([])
  const recentTransactions = ref<Transaction[]>([])
  const calendarData = ref<Record<string, { income: number; expense: number; count: number }>>({})
  const total = ref(0)
  const loading = ref(false)
  const current = ref(1)
  const pageSize = ref(20)

  const hasMore = computed(() => transactions.value.length < total.value)

  const groupedTransactions = computed(() => {
    const groups: Record<string, Transaction[]> = {}
    for (const tx of transactions.value) {
      const date = tx.transactionTime?.split('T')[0] || ''
      if (!groups[date]) groups[date] = []
      groups[date].push(tx)
    }
    return groups
  })

  // 加载近期交易（首页用）
  const fetchRecent = async (limit = 5) => {
    const data = await getTransactions({ size: limit })
    recentTransactions.value = data.records
    return data.records
  }

  // 加载日历视图数据
  const fetchCalendarData = async (year: number, month: number, bookId?: number) => {
    const data = await getCalendarTransactions({ year, month, bookId })
    const map: Record<string, { income: number; expense: number; count: number }> = {}
    for (const item of data) {
      map[item.date] = { income: item.income, expense: item.expense, count: item.count }
    }
    calendarData.value = map
    return map
  }

  // 加载交易列表（账单页用）
  const fetchPage = async (params: TransactionQueryParams = {}) => {
    const pageParams = {
      ...params,
      current: params.current || current.value,
      size: params.size || pageSize.value,
    }
    const data = await getTransactions(pageParams)
    if ((pageParams.current || 1) > 1) {
      transactions.value.push(...data.records)
    } else {
      transactions.value = data.records
    }
    total.value = data.total
    current.value = pageParams.current || 1
    return data
  }

  // 加载更多
  const loadMore = async (params: TransactionQueryParams = {}) => {
    return fetchPage({ ...params, current: (current.value || 1) + 1 })
  }

  // 获取单条详情
  const fetchById = async (id: number) => {
    return await getTransaction(id)
  }

  // 创建交易
  const create = async (data: {
    amount: number
    type: number
    categoryId: number
    accountId: number
    transactionTime?: string
    remark?: string
  }) => {
    const created = await apiCreateTransaction(data)
    // 新增记录插入到列表头部
    transactions.value.unshift(created)
    recentTransactions.value.unshift(created)
    if (recentTransactions.value.length > 5) {
      recentTransactions.value.pop()
    }
    total.value++
    return created
  }

  // 更新交易
  const update = async (id: number, data: Partial<{
    amount: number
    type: number
    categoryId: number
    accountId: number
    transactionTime?: string
    remark?: string
  }>) => {
    const updated = await apiUpdateTransaction({ id, ...data })
    const idx = transactions.value.findIndex(t => t.id === id)
    if (idx !== -1) transactions.value[idx] = updated
    return updated
  }

  // 更新交易（兼容页面调用方式）
  const updateTransaction = async (data: {
    id: number
    amount?: number
    type?: number
    categoryId?: number
    accountId?: number
    transactionTime?: string
    remark?: string
  }) => {
    return update(data.id, data)
  }

  // 删除交易
  const remove = async (id: number) => {
    await apiDeleteTransaction(id)
    transactions.value = transactions.value.filter(t => t.id !== id)
    recentTransactions.value = recentTransactions.value.filter(t => t.id !== id)
    total.value = Math.max(0, total.value - 1)
  }

  // 重置分页状态
  const resetPagination = () => {
    current.value = 1
    transactions.value = []
    total.value = 0
  }

  // 清空状态
  const clearState = () => {
    transactions.value = []
    recentTransactions.value = []
    calendarData.value = {}
    total.value = 0
    current.value = 1
  }

  return {
    // 状态
    transactions: computed(() => transactions.value),
    recentTransactions: computed(() => recentTransactions.value),
    calendarData: computed(() => calendarData.value),
    total: computed(() => total.value),
    loading: computed(() => loading.value),
    current: computed(() => current.value),
    pageSize: computed(() => pageSize.value),
    hasMore,

    // 计算属性
    groupedTransactions,

    // 方法
    fetchRecent,
    fetchCalendarData,
    fetchPage,
    fetchTransactions: fetchPage, // alias
    loadMore,
    fetchById,
    create,
    createTransaction: create,
    update,
    updateTransaction,
    remove,
    deleteTransaction: remove,
    resetPagination,
    clearState,
  }
})
