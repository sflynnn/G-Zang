<template>
  <view class="report-page">
    <uni-nav-bar left-icon="back" title="财务报告" @clickLeft="goBack" />
    
    <!-- 时间选择 -->
    <view class="time-selector">
      <view 
        v-for="tab in timeTabs" 
        :key="tab.value"
        :class="['time-tab', { active: currentPeriod === tab.value }]"
        @click="changePeriod(tab.value)"
      >
        {{ tab.label }}
      </view>
    </view>

    <scroll-view class="report-scroll" scroll-y>
      <!-- 收支概览 -->
      <view class="overview-card">
        <view class="overview-header">
          <text class="overview-title">{{ periodLabel }}收支概况</text>
        </view>
        
        <view class="overview-content">
          <view class="overview-item">
            <view class="item-icon income-icon">📈</view>
            <view class="item-info">
              <view class="item-label">总收入</view>
              <view class="item-value income">+{{ currentCurrency }}{{ formatAmount(summary.totalIncome) }}</view>
            </view>
          </view>
          
          <view class="overview-item">
            <view class="item-icon expense-icon">📉</view>
            <view class="item-info">
              <view class="item-label">总支出</view>
              <view class="item-value expense">-{{ currentCurrency }}{{ formatAmount(summary.totalExpense) }}</view>
            </view>
          </view>
          
          <view class="overview-divider"></view>
          
          <view class="overview-item balance">
            <view class="item-info">
              <view class="item-label">结余</view>
              <view class="item-value" :class="summary.balance >= 0 ? 'income' : 'expense'">
                {{ summary.balance >= 0 ? '+' : '' }}{{ currentCurrency }}{{ formatAmount(summary.balance) }}
              </view>
            </view>
          </view>
        </view>
      </view>

      <!-- 月度趋势 -->
      <view class="trend-section">
        <view class="section-header">
          <text class="section-title">月度趋势</text>
        </view>
        
        <view class="trend-chart">
          <view class="chart-y-axis">
            <text v-for="val in yAxisValues" :key="val">{{ val }}</text>
          </view>
          <view class="chart-bars">
            <view 
              v-for="(item, index) in monthlyTrend" 
              :key="index"
              class="bar-wrapper"
            >
              <view class="bar-container">
                <view 
                  class="bar income-bar"
                  :style="{ height: (item.income / maxValue * 100) + '%' }"
                >
                  <text class="bar-value" v-if="item.income > 0">{{ formatShortAmount(item.income) }}</text>
                </view>
                <view 
                  class="bar expense-bar"
                  :style="{ height: (item.expense / maxValue * 100) + '%' }"
                >
                  <text class="bar-value" v-if="item.expense > 0">{{ formatShortAmount(item.expense) }}</text>
                </view>
              </view>
              <text class="bar-label">{{ item.month }}月</text>
            </view>
          </view>
        </view>
        
        <view class="chart-legend">
          <view class="legend-item">
            <view class="legend-color income"></view>
            <text>收入</text>
          </view>
          <view class="legend-item">
            <view class="legend-color expense"></view>
            <text>支出</text>
          </view>
        </view>
      </view>

      <!-- 分类统计 -->
      <view class="category-section">
        <view class="section-header">
          <text class="section-title">支出分类</text>
          <text class="see-more" @click="goToChart">详情 ›</text>
        </view>
        
        <view class="category-list">
          <view 
            v-for="(item, index) in topCategories" 
            :key="index"
            class="category-item"
          >
            <view class="category-rank">{{ index + 1 }}</view>
            <view class="category-icon" :style="{ background: item.iconBg }">
              {{ item.icon }}
            </view>
            <view class="category-info">
              <view class="category-name">{{ item.name }}</view>
              <view class="category-bar-wrapper">
                <view 
                  class="category-bar"
                  :style="{ 
                    width: item.percent + '%',
                    background: item.color 
                  }"
                ></view>
              </view>
            </view>
            <view class="category-amount">
              <text class="amount-value">{{ currentCurrency }}{{ formatAmount(item.amount) }}</text>
              <text class="amount-percent">{{ item.percent }}%</text>
            </view>
          </view>
        </view>
      </view>

      <!-- 导出按钮 -->
      <view class="export-section">
        <button class="export-btn" @click="exportReport">
          <text class="icon">📤</text>
          <text>导出报告</text>
        </button>
      </view>
    </scroll-view>
  </view>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useReportStore } from '@/stores/report'

const reportStore = useReportStore()

const currentPeriod = ref('month')
const timeTabs = [
  { label: '周', value: 'week' },
  { label: '月', value: 'month' },
  { label: '年', value: 'year' }
]

const summary = ref({
  totalIncome: 0,
  totalExpense: 0,
  balance: 0
})

const monthlyTrend = ref<any[]>([])
const topCategories = ref<any[]>([])

const colors = ['#FB8B24', '#0F4C5C', '#06D6A0', '#EF476F', '#FFD166', '#3A86FF']
const icons = ['🍜', '🚇', '🛒', '🎮', '💊', '📚']

const periodLabel = computed(() => {
  return timeTabs.find(t => t.value === currentPeriod.value)?.label || '月'
})

const maxValue = computed(() => {
  let max = 0
  monthlyTrend.value.forEach(item => {
    if (item.income > max) max = item.income
    if (item.expense > max) max = item.expense
  })
  return max || 1000
})

const yAxisValues = computed(() => {
  const max = maxValue.value
  return [
    formatShortAmount(max),
    formatShortAmount(max * 0.75),
    formatShortAmount(max * 0.5),
    formatShortAmount(max * 0.25),
    '0'
  ]
})

const currentCurrency = '¥'

onMounted(async () => {
  await loadData()
})

async function loadData() {
  try {
    await reportStore.fetchSummary()
    await reportStore.fetchMonthlyTrend({ year: new Date().getFullYear() })
    await reportStore.fetchCategoryReport({ type: 2 })
    
    const report = reportStore
    summary.value = {
      totalIncome: report.summary?.totalIncome || 0,
      totalExpense: report.summary?.totalExpense || 0,
      balance: (report.summary?.totalIncome || 0) - (report.summary?.totalExpense || 0)
    }
    
    monthlyTrend.value = (report.monthlyTrend || []).slice(0, 6).map((item: any, index: number) => ({
      month: item.month || index + 1,
      income: parseFloat(item.income) || 0,
      expense: parseFloat(item.expense) || 0
    }))
    
    const categories = report.categoryReport || []
    const total = categories.reduce((sum: number, c: any) => sum + (parseFloat(c.totalAmount) || 0), 0)
    
    topCategories.value = categories.slice(0, 5).map((item: any, index: number) => ({
      name: item.categoryName || '未分类',
      amount: parseFloat(item.totalAmount) || 0,
      percent: total > 0 ? Math.round((parseFloat(item.totalAmount) / total) * 100) : 0,
      icon: icons[index % icons.length],
      iconBg: ['#FFF3E0', '#E3F2FD', '#FCE4EC', '#F3E5F5', '#FFEBEE'][index % 5],
      color: colors[index % colors.length]
    }))
  } catch (error) {
    // ignore
  }
}

function changePeriod(period: string) {
  currentPeriod.value = period
  loadData()
}

function formatAmount(amount: any): string {
  if (!amount && amount !== 0) return '0.00'
  const num = typeof amount === 'string' ? parseFloat(amount) : amount
  return num.toFixed(2).replace(/\B(?=(\d{3})+(?!\d))/g, ',')
}

function formatShortAmount(amount: number): string {
  if (amount >= 10000) {
    return (amount / 10000).toFixed(1) + 'w'
  }
  return amount.toFixed(0)
}

function goBack() {
  uni.navigateBack()
}

function goToChart() {
  uni.navigateTo({ url: '/pages/analysis/chart' })
}

function exportReport() {
  uni.showToast({ title: '导出功能开发中', icon: 'none' })
}
</script>

<style scoped lang="scss">
.report-page {
  min-height: 100vh;
  background-color: #F8F9FA;
}

.time-selector {
  display: flex;
  background: #fff;
  padding: 16rpx 24rpx;
  gap: 16rpx;
  border-bottom: 1rpx solid #F0F0F0;
}

.time-tab {
  padding: 12rpx 32rpx;
  border-radius: 20rpx;
  font-size: 28rpx;
  color: #666;
  background: #F8F9FA;
  
  &.active {
    background: #0F4C5C;
    color: #fff;
    font-weight: 500;
  }
}

.report-scroll {
  height: calc(100vh - 200rpx);
  padding: 24rpx;
}

.overview-card {
  background: #fff;
  border-radius: 20rpx;
  padding: 32rpx;
  margin-bottom: 24rpx;
}

.overview-header {
  margin-bottom: 24rpx;
}

.overview-title {
  font-size: 32rpx;
  font-weight: 600;
  color: #1F2937;
}

.overview-content {
  display: flex;
  flex-wrap: wrap;
  gap: 24rpx;
}

.overview-item {
  display: flex;
  align-items: center;
  gap: 16rpx;
  flex: 1;
  min-width: 45%;
  
  &.balance {
    flex: 100%;
    justify-content: center;
    padding-top: 24rpx;
    border-top: 1rpx solid #F3F4F6;
    margin-top: 8rpx;
  }
}

.item-icon {
  width: 64rpx;
  height: 64rpx;
  border-radius: 16rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28rpx;
}

.income-icon {
  background: rgba(6, 214, 160, 0.1);
}

.expense-icon {
  background: rgba(239, 71, 111, 0.1);
}

.item-info {
  .item-label {
    font-size: 24rpx;
    color: #666;
    margin-bottom: 4rpx;
  }
  
  .item-value {
    font-size: 32rpx;
    font-weight: 700;
    
    &.income {
      color: #06D6A0;
    }
    
    &.expense {
      color: #EF476F;
    }
  }
}

.overview-divider {
  width: 100%;
  height: 1rpx;
  background: #F3F4F6;
}

.trend-section,
.category-section {
  background: #fff;
  border-radius: 20rpx;
  padding: 32rpx;
  margin-bottom: 24rpx;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24rpx;
}

.section-title {
  font-size: 32rpx;
  font-weight: 600;
  color: #1F2937;
}

.see-more {
  font-size: 26rpx;
  color: #0F4C5C;
}

.trend-chart {
  display: flex;
  height: 300rpx;
}

.chart-y-axis {
  width: 80rpx;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  padding: 10rpx 0;
  
  text {
    font-size: 20rpx;
    color: #9CA3AF;
    text-align: right;
    padding-right: 8rpx;
  }
}

.chart-bars {
  flex: 1;
  display: flex;
  justify-content: space-around;
  align-items: flex-end;
  padding-bottom: 40rpx;
}

.bar-wrapper {
  display: flex;
  flex-direction: column;
  align-items: center;
  flex: 1;
}

.bar-container {
  display: flex;
  gap: 4rpx;
  align-items: flex-end;
  height: 240rpx;
}

.bar {
  width: 24rpx;
  border-radius: 4rpx 4rpx 0 0;
  position: relative;
}

.income-bar {
  background: #06D6A0;
}

.expense-bar {
  background: #EF476F;
}

.bar-value {
  position: absolute;
  top: -30rpx;
  left: 50%;
  transform: translateX(-50%);
  font-size: 18rpx;
  color: #666;
  white-space: nowrap;
}

.bar-label {
  margin-top: 8rpx;
  font-size: 22rpx;
  color: #666;
}

.chart-legend {
  display: flex;
  justify-content: center;
  gap: 48rpx;
  margin-top: 24rpx;
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 8rpx;
  font-size: 24rpx;
  color: #666;
}

.legend-color {
  width: 16rpx;
  height: 16rpx;
  border-radius: 4rpx;
  
  &.income {
    background: #06D6A0;
  }
  
  &.expense {
    background: #EF476F;
  }
}

.category-list {
  .category-item {
    display: flex;
    align-items: center;
    gap: 16rpx;
    padding: 20rpx 0;
    border-bottom: 1rpx solid #F3F4F6;
    
    &:last-child {
      border-bottom: none;
    }
  }
}

.category-rank {
  width: 32rpx;
  height: 32rpx;
  background: #F8F9FA;
  border-radius: 8rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24rpx;
  font-weight: 600;
  color: #666;
}

.category-icon {
  width: 56rpx;
  height: 56rpx;
  border-radius: 12rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28rpx;
}

.category-info {
  flex: 1;
}

.category-name {
  font-size: 28rpx;
  color: #1F2937;
  margin-bottom: 8rpx;
}

.category-bar-wrapper {
  height: 8rpx;
  background: #F3F4F6;
  border-radius: 4rpx;
  overflow: hidden;
}

.category-bar {
  height: 100%;
  border-radius: 4rpx;
}

.category-amount {
  text-align: right;
}

.amount-value {
  font-size: 28rpx;
  font-weight: 600;
  color: #1F2937;
  display: block;
}

.amount-percent {
  font-size: 22rpx;
  color: #9CA3AF;
}

.export-section {
  padding: 32rpx 0;
}

.export-btn {
  width: 100%;
  height: 96rpx;
  background: #fff;
  border-radius: 24rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8rpx;
  font-size: 30rpx;
  color: #0F4C5C;
  border: none;
  
  .icon {
    font-size: 32rpx;
  }
}
</style>
