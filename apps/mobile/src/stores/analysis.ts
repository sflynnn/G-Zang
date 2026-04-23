import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import * as reportApi from '@/api/report'

// 类型定义
export interface ChartData {
  name: string
  value: number
  percentage?: number
}

export interface AnalysisState {
  overview: {
    totalIncome: number
    totalExpense: number
    balance: number
    transactionCount: number
  }
  categoryChart: ChartData[]
  trendChart: Array<{
    date: string
    income: number
    expense: number
    balance: number
  }>
  accountChart: ChartData[]
  timeRange: 'week' | 'month' | 'year'
  loading: boolean
}

// Store定义
export const useAnalysisStore = defineStore('analysis', () => {
  // 状态
  const overview = ref({
    totalIncome: 0,
    totalExpense: 0,
    balance: 0,
    transactionCount: 0
  })

  const categoryChart = ref<ChartData[]>([])
  const trendChart = ref<Array<{
    date: string
    income: number
    expense: number
    balance: number
  }>>([])

  const accountChart = ref<ChartData[]>([])
  const timeRange = ref<'week' | 'month' | 'year'>('month')
  const loading = ref(false)

  // 计算属性
  const incomeExpenseRatio = computed(() => {
    const { totalIncome, totalExpense } = overview.value
    if (totalIncome === 0) return 0
    return (totalExpense / totalIncome) * 100
  })

  const averageDailyExpense = computed(() => {
    const { totalExpense } = overview.value
    const days = timeRange.value === 'week' ? 7 :
                 timeRange.value === 'month' ? 30 : 365
    return totalExpense / days
  })

  const topExpenseCategories = computed(() => {
    return categoryChart.value
      .filter(item => item.value > 0)
      .sort((a, b) => b.value - a.value)
      .slice(0, 5)
  })

  // 加载统计概览
  const loadOverview = async () => {
    try {
      loading.value = true
      const data = await reportApi.getSummary()
      overview.value = data
    } catch (error) {
      throw error
    } finally {
      loading.value = false
    }
  }

  // 加载分类统计图表
  const loadCategoryChart = async () => {
    try {
      loading.value = true
      const data = await reportApi.getCategoryReport()

      categoryChart.value = data.map((item) => ({
        name: item.categoryName,
        value: item.totalAmount,
        percentage: item.percentage || 0
      }))
    } catch (error) {
      throw error
    } finally {
      loading.value = false
    }
  }

  // 加载趋势图表
  const loadTrendChart = async () => {
    try {
      loading.value = true
      const year = new Date().getFullYear()
      const data = await reportApi.getMonthlyTrend({ year })

      trendChart.value = data.map(item => ({
        date: `${item.month}`,
        income: item.income,
        expense: item.expense,
        balance: item.balance || 0
      }))
    } catch (error) {
      throw error
    } finally {
      loading.value = false
    }
  }

  // 加载账户统计图表
  const loadAccountChart = async () => {
    try {
      loading.value = true
      const data = await reportApi.getAccountBalance()

      accountChart.value = data.map(item => ({
        name: item.accountName,
        value: item.balance,
        percentage: item.percentage || 0
      }))
    } catch (error) {
      throw error
    } finally {
      loading.value = false
    }
  }

  // 加载所有统计数据
  const loadAllStats = async () => {
    try {
      loading.value = true

      // 并行加载所有统计数据
      await Promise.all([
        loadOverview(),
        loadCategoryChart(),
        loadTrendChart(),
        loadAccountChart()
      ])
    } catch (error) {
      throw error
    } finally {
      loading.value = false
    }
  }

  // 设置时间范围
  const setTimeRange = async (range: 'week' | 'month' | 'year') => {
    timeRange.value = range
    await loadAllStats()
  }

  // 导出数据（移动端暂不支持服务器端导出，提示用户使用本地功能）
  const exportData = async (format: 'excel' | 'pdf' = 'excel') => {
    try {
      uni.showToast({
        title: '导出功能开发中，请稍候',
        icon: 'none'
      })
    } catch (error) {
      throw error
    }
  }

  // 文件下载辅助函数
  const downloadFile = async (url: string, fileName: string) => {
    try {
      const downloadTask = uni.downloadFile({
        url,
        success: (res) => {
          if (res.statusCode === 200) {
            uni.saveFile({
              tempFilePath: res.tempFilePath,
              success: (saveRes) => {
                uni.showToast({
                  title: '文件已保存',
                  icon: 'success'
                })
              }
            })
          }
        }
      })

      downloadTask.onProgressUpdate((res) => {
        // 下载进度: res.progress
      })
    } catch (error) {
      throw error
    }
  }

  // 清空状态
  const clearState = () => {
    overview.value = {
      totalIncome: 0,
      totalExpense: 0,
      balance: 0,
      transactionCount: 0
    }
    categoryChart.value = []
    trendChart.value = []
    accountChart.value = []
    timeRange.value = 'month'
  }

  return {
    // 状态
    overview: computed(() => overview.value),
    categoryChart: computed(() => categoryChart.value),
    trendChart: computed(() => trendChart.value),
    accountChart: computed(() => accountChart.value),
    timeRange: computed(() => timeRange.value),
    loading: computed(() => loading.value),

    // 计算属性
    incomeExpenseRatio,
    averageDailyExpense,
    topExpenseCategories,

    // 方法
    loadOverview,
    loadCategoryChart,
    loadTrendChart,
    loadAccountChart,
    loadAllStats,
    setTimeRange,
    exportData,
    clearState
  }
})
