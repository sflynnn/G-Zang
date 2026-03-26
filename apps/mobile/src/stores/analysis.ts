import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { reportApi } from '@shared/api'

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
      const response = await reportApi.getOverview({
        timeRange: timeRange.value
      })

      overview.value = response.data
      return response
    } catch (error) {
      console.error('加载统计概览失败:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  // 加载分类统计图表
  const loadCategoryChart = async () => {
    try {
      loading.value = true
      const response = await reportApi.getCategoryStats({
        timeRange: timeRange.value
      })

      categoryChart.value = response.data.map((item: any) => ({
        ...item,
        percentage: (item.value / overview.value.totalExpense) * 100
      }))

      return response
    } catch (error) {
      console.error('加载分类统计失败:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  // 加载趋势图表
  const loadTrendChart = async () => {
    try {
      loading.value = true
      const response = await reportApi.getTrendStats({
        timeRange: timeRange.value
      })

      trendChart.value = response.data
      return response
    } catch (error) {
      console.error('加载趋势统计失败:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  // 加载账户统计图表
  const loadAccountChart = async () => {
    try {
      loading.value = true
      const response = await reportApi.getAccountStats()

      accountChart.value = response.data
      return response
    } catch (error) {
      console.error('加载账户统计失败:', error)
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
      console.error('加载统计数据失败:', error)
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

  // 导出数据
  const exportData = async (format: 'excel' | 'pdf' = 'excel') => {
    try {
      loading.value = true
      const response = await reportApi.exportReport({
        timeRange: timeRange.value,
        format
      })

      // 处理文件下载
      const fileName = `统计报告_${timeRange.value}_${new Date().toISOString().split('T')[0]}.${format}`
      await downloadFile(response.data.url, fileName)

      return response
    } catch (error) {
      console.error('导出数据失败:', error)
      throw error
    } finally {
      loading.value = false
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
        console.log('下载进度:', res.progress)
      })
    } catch (error) {
      console.error('下载文件失败:', error)
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
