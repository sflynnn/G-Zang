/**
 * ECharts 按需加载 Composable
 * 用于优化 PC 端图表性能
 */
import { ref, onMounted, onUnmounted, type Ref } from 'vue'
import * as echarts from 'echarts/core'

// 按需引入 ECharts 组件
import { LineChart, BarChart, PieChart, GaugeChart, ScatterChart } from 'echarts/charts'
import {
  TitleComponent,
  TooltipComponent,
  GridComponent,
  LegendComponent,
  DataZoomComponent,
  ToolboxComponent
} from 'echarts/components'
import { CanvasRenderer } from 'echarts/renderers'

// 注册必需的组件
echarts.use([
  LineChart,
  BarChart,
  PieChart,
  GaugeChart,
  ScatterChart,
  TitleComponent,
  TooltipComponent,
  GridComponent,
  LegendComponent,
  DataZoomComponent,
  ToolboxComponent,
  CanvasRenderer
])

export type EChartsType = echarts.ECharts
export type ChartOption = echarts.SetOption

export function useECharts(
  chartRef: Ref<HTMLElement | null>,
  options?: ChartOption
) {
  const chartInstance = ref<EChartsType | null>(null)
  const isLoading = ref(false)

  // 初始化图表
  const initChart = () => {
    if (!chartRef.value) return

    chartInstance.value = echarts.init(chartRef.value)
    
    if (options) {
      chartInstance.value.setOption(options)
    }
  }

  // 设置配置项
  const setOption = (option: ChartOption) => {
    if (chartInstance.value) {
      chartInstance.value.setOption(option)
    }
  }

  // 更新配置项（不合并）
  const updateOption = (option: ChartOption) => {
    if (chartInstance.value) {
      chartInstance.value.setOption(option, { notMerge: true })
    }
  }

  // 显示加载状态
  const showLoading = () => {
    if (chartInstance.value) {
      chartInstance.value.showLoading('default', {
        text: '加载中...',
        color: '#0F4C5C',
        textColor: '#666',
        maskColor: 'rgba(255, 255, 255, 0.9)',
        zlevel: 0
      })
      isLoading.value = true
    }
  }

  // 隐藏加载状态
  const hideLoading = () => {
    if (chartInstance.value) {
      chartInstance.value.hideLoading()
      isLoading.value = false
    }
  }

  // 调整图表大小
  const resize = () => {
    if (chartInstance.value) {
      chartInstance.value.resize()
    }
  }

  // 清除图表
  const clear = () => {
    if (chartInstance.value) {
      chartInstance.value.clear()
    }
  }

  // 销毁图表
  const dispose = () => {
    if (chartInstance.value) {
      chartInstance.value.dispose()
      chartInstance.value = null
    }
  }

  // 窗口大小变化时自动调整
  const handleResize = () => {
    resize()
  }

  onMounted(() => {
    initChart()
    window.addEventListener('resize', handleResize)
  })

  onUnmounted(() => {
    window.removeEventListener('resize', handleResize)
    dispose()
  })

  return {
    chartInstance,
    isLoading,
    setOption,
    updateOption,
    showLoading,
    hideLoading,
    resize,
    clear,
    dispose
  }
}

// 常用的图表配置模板
export const chartTemplates = {
  // 基础折线图配置
  lineBase: {
    tooltip: {
      trigger: 'axis'
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: []
    },
    yAxis: {
      type: 'value'
    },
    series: []
  },

  // 基础柱状图配置
  barBase: {
    tooltip: {
      trigger: 'axis'
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: []
    },
    yAxis: {
      type: 'value'
    },
    series: []
  },

  // 饼图配置
  pieBase: {
    tooltip: {
      trigger: 'item',
      formatter: '{a} <br/>{b}: {c} ({d}%)'
    },
    legend: {
      orient: 'horizontal',
      bottom: 0
    },
    series: [
      {
        name: '',
        type: 'pie',
        radius: ['40%', '70%'],
        avoidLabelOverlap: false,
        label: {
          show: false,
          position: 'center'
        },
        emphasis: {
          label: {
            show: true,
            fontSize: 16,
            fontWeight: 'bold'
          }
        },
        labelLine: {
          show: false
        },
        data: []
      }
    ]
  }
}

export default useECharts
