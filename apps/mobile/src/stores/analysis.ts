import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import * as reportApi from '@/api/report'
import { toast } from '@/composables/useToast'

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
    const data = await reportApi.getSummary()
    overview.value = data
  }

  // 加载分类统计图表
  const loadCategoryChart = async () => {
    const data = await reportApi.getCategoryReport()

    categoryChart.value = data.map((item) => ({
      name: item.categoryName,
      value: item.totalAmount,
      percentage: item.percentage || 0
    }))
  }

  // 加载趋势图表
  const loadTrendChart = async () => {
    const year = new Date().getFullYear()
    const data = await reportApi.getMonthlyTrend({ year })

    trendChart.value = data.map(item => ({
      date: `${item.month}`,
      income: item.income,
      expense: item.expense,
      balance: item.balance || 0
    }))
  }

  // 加载账户统计图表
  const loadAccountChart = async () => {
    const data = await reportApi.getAccountBalance()

    accountChart.value = data.map(item => ({
      name: item.accountName,
      value: item.balance,
      percentage: item.percentage || 0
    }))
  }

  // 加载所有统计数据
  const loadAllStats = async () => {
    // 并行加载所有统计数据
    await Promise.all([
      loadOverview(),
      loadCategoryChart(),
      loadTrendChart(),
      loadAccountChart()
    ])
  }

  // 设置时间范围
  const setTimeRange = async (range: 'week' | 'month' | 'year') => {
    timeRange.value = range
    await loadAllStats()
  }

  // 导出数据
  const exportData = async (format: 'excel' | 'pdf' = 'excel') => {
    try {
      uni.showLoading({ title: '正在生成导出文件...', mask: true })

      // 生成 CSV 数据
      const categoryRows = categoryChart.value.map(c =>
        `${c.name},${c.value},${c.percentage?.toFixed(1) || 0}%`
      ).join('\n')
      const trendRows = trendChart.value.map(t =>
        `${t.date},${t.income},${t.expense},${t.balance}`
      ).join('\n')

      const csvContent = [
        '归藏财务管理系统 - 收支报告',
        `导出时间：${new Date().toLocaleString('zh-CN')}`,
        '',
        '=== 收支概览 ===',
        `总收入,${overview.value.totalIncome}`,
        `总支出,${overview.value.totalExpense}`,
        `结余,${overview.value.balance}`,
        `交易笔数,${overview.value.transactionCount}`,
        '',
        '=== 分类统计 ===',
        '分类,金额,占比',
        categoryRows,
        '',
        '=== 趋势数据 ===',
        '月份,收入,支出,结余',
        trendRows,
      ].join('\n')

      const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' })
      const tempFilePath = `${(uni as any).env?.USER_DATA_PATH || '/tmp'}/gzang_report_${Date.now()}.csv`

      // 写入临时文件
      const fs = (uni as any).getFileSystemManager?.()
      if (fs) {
        fs.writeFile({
          filePath: tempFilePath,
          data: csvContent,
          encoding: 'utf8',
          success: () => {
            uni.hideLoading()
            uni.share({
              title: '归藏财务报告',
              filePath: tempFilePath,
              success: () => toast.success('导出成功'),
              fail: (e: any) => {
                if (e.errMsg?.includes('cancel')) return
                uni.share({
                  title: '归藏财务报告',
                  summary: `收支概览：收入 ${overview.value.totalIncome}，支出 ${overview.value.totalExpense}，结余 ${overview.value.balance}`,
                  fail: () => toast.error('分享失败')
                })
              }
            })
          },
          fail: () => {
            uni.hideLoading()
            toast.error('文件生成失败')
          }
        })
      } else {
        // 降级：直接分享文本
        uni.hideLoading()
        uni.share({
          title: '归藏财务报告',
          summary: `收入 ${overview.value.totalIncome} | 支出 ${overview.value.totalExpense} | 结余 ${overview.value.balance}`,
          success: () => toast.success('导出成功'),
          fail: () => toast.error('分享失败')
        })
      }
    } catch (error) {
      uni.hideLoading()
      toast.error('导出失败')
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
              success: () => {
                toast.success('文件已保存')
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
