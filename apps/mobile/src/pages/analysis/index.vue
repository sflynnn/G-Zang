<template>
  <view class="analysis-page apple-style">
    <!-- Large Title Navigation -->
    <view class="nav-large-title">
      <view class="nav-header">
        <view class="nav-titles">
          <text class="nav-title">{{ t('analysis.title') }}</text>
        </view>
        <view class="nav-actions">
          <view class="nav-action" @click="handleExport">
            <AppleIcon name="export" :size="20" color="var(--gzang-text-secondary)" />
          </view>
        </view>
      </view>
    </view>

    <!-- Period Selector -->
    <view class="period-selector">
      <scroll-view class="period-scroll" scroll-x="true" enhanced="true" show-scrollbar="false">
        <view 
          v-for="period in periods" 
          :key="period.key"
          class="period-chip"
          :class="{ active: activePeriod === period.key }"
          @click="activePeriod = period.key"
        >
          {{ period.label }}
        </view>
      </scroll-view>
    </view>

    <scroll-view class="main-content" scroll-y="true">
      <!-- Expense Overview Card -->
      <view class="overview-card">
        <view class="overview-header">
          <view class="overview-period">
            <text class="period-label">{{ getCurrentPeriodLabel() }}</text>
            <view class="period-trend" :class="trendTrend >= 0 ? 'trend-up' : 'trend-down'">
              <AppleIcon :name="trendTrend >= 0 ? 'trend-up' : 'trend-down'" :size="12" :color="trendTrend >= 0 ? 'var(--gzang-success)' : 'var(--gzang-danger)'" />
              <text>{{ Math.abs(trendTrend).toFixed(1) }}%</text>
            </view>
          </view>
          <text class="vs-label">vs {{ t('datetime.lastMonth') }}</text>
        </view>
        <view class="overview-amount">
          <text class="amount-label">{{ t('analysis.expense') }}</text>
          <text class="amount-value expense amount">{{ formatAmount(overview.totalExpense) }}</text>
        </view>
        <view class="overview-bar">
          <view class="bar-track">
            <view class="bar-fill" :style="{ width: expenseProgress + '%' }"></view>
          </view>
        </view>
      </view>

      <!-- Pie Chart Card -->
      <view class="chart-card">
        <view class="chart-header">
          <text class="chart-title">{{ t('analysis.expenseAnalysis') }}</text>
        </view>
        <view class="pie-section">
          <view class="pie-wrapper">
            <svg class="pie-svg" viewBox="0 0 100 100">
              <circle cx="50" cy="50" r="40" fill="none" stroke="var(--gzang-bg)" stroke-width="12" />
              <circle 
                v-for="(segment, index) in pieSegments" 
                :key="index"
                cx="50" 
                cy="50" 
                r="40" 
                fill="none" 
                :stroke="segment.color"
                stroke-width="12"
                :stroke-dasharray="segment.dashArray"
                :stroke-dashoffset="segment.dashOffset"
                stroke-linecap="butt"
                transform="rotate(-90 50 50)"
              />
            </svg>
            <view class="pie-center">
              <text class="pie-center-value">{{ categoryChart.length }}</text>
              <text class="pie-center-label">项分类</text>
            </view>
          </view>
          <view class="pie-legend">
            <view 
              v-for="(item, index) in categoryChart" 
              :key="index"
              class="legend-item"
            >
              <view class="legend-color" :style="{ background: pieColors[index % pieColors.length] }"></view>
              <text class="legend-name">{{ item.name }}</text>
              <text class="legend-value">{{ item.percentage?.toFixed(0) || 0 }}%</text>
            </view>
          </view>
        </view>
      </view>

      <!-- Bar Chart Card -->
      <view class="chart-card">
        <view class="chart-header">
          <text class="chart-title">{{ t('analysis.monthTrend') }}</text>
        </view>
        <view class="bar-chart">
          <view 
            v-for="(item, index) in trendChart" 
            :key="index"
            class="bar-column"
          >
            <view class="bar-wrapper">
              <view 
                class="bar-fill"
                :style="{ 
                  height: (item.expense / maxTrendExpense * 100) + '%',
                  background: index === trendChart.length - 1 ? 'var(--gzang-secondary)' : 'var(--gzang-primary)'
                }"
              ></view>
            </view>
            <text class="bar-label">{{ item.month }}</text>
          </view>
        </view>
      </view>

      <!-- Category Breakdown -->
      <view class="breakdown-card">
        <view class="chart-header">
          <text class="chart-title">{{ t('analysis.topCategory') }}</text>
        </view>
        <view class="breakdown-list">
          <view 
            v-for="(item, index) in categoryChart" 
            :key="index"
            class="breakdown-item"
          >
            <view class="breakdown-left">
              <view class="breakdown-rank">{{ index + 1 }}</view>
              <view class="breakdown-icon" :style="{ background: pieColors[index % pieColors.length] + '20' }">
                <AppleIcon :name="item.icon || 'shopping'" :size="16" :color="pieColors[index % pieColors.length]" />
              </view>
              <text class="breakdown-name">{{ item.name }}</text>
            </view>
            <view class="breakdown-right">
              <view class="breakdown-bar">
                <view 
                  class="breakdown-fill" 
                  :style="{ width: (item.percentage || 0) + '%', background: pieColors[index % pieColors.length] }"
                ></view>
              </view>
              <text class="breakdown-amount expense amount">{{ formatAmount(item.amount || 0) }}</text>
            </view>
          </view>
        </view>
      </view>

      <!-- Balance Card -->
      <view class="balance-card">
        <view class="balance-item">
          <view class="balance-header">
            <AppleIcon name="income" :size="18" color="var(--gzang-success)" />
            <text class="balance-label">{{ t('analysis.income') }}</text>
          </view>
          <text class="balance-value income amount">{{ formatAmount(overview.totalIncome) }}</text>
        </view>
        <view class="balance-divider"></view>
        <view class="balance-item">
          <view class="balance-header">
            <AppleIcon name="expense" :size="18" color="var(--gzang-danger)" />
            <text class="balance-label">{{ t('analysis.expense') }}</text>
          </view>
          <text class="balance-value expense amount">{{ formatAmount(overview.totalExpense) }}</text>
        </view>
        <view class="balance-divider"></view>
        <view class="balance-item">
          <view class="balance-header">
            <AppleIcon name="chart" :size="18" color="var(--gzang-primary)" />
            <text class="balance-label">{{ t('analysis.balance') }}</text>
          </view>
          <text class="balance-value" :class="overview.balance >= 0 ? 'income' : 'expense'">
            {{ overview.balance >= 0 ? '+' : '' }}{{ formatAmount(overview.balance) }}
          </text>
        </view>
      </view>

      <view class="bottom-safe-area"></view>
    </scroll-view>

    <!-- 自定义 TabBar -->
    <CustomTabBar />
  </view>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useI18n } from 'vue-i18n'
import { useAnalysisStore } from '@/stores/analysis'
import AppleIcon from '@/components/common/AppleIcon/index.vue'
import CustomTabBar from '@/components/CustomTabBar/index.vue'

const { t } = useI18n()

// Store
const analysisStore = useAnalysisStore()

// State
const activePeriod = ref<'week' | 'month' | 'year'>('month')

// Periods
const periods = computed(() => [
  { key: 'week' as const, label: t('analysis.thisWeek') },
  { key: 'month' as const, label: t('analysis.thisMonth') },
  { key: 'year' as const, label: t('analysis.thisYear') },
])

// Overview data
const overview = computed(() => ({
  totalExpense: 8234.50,
  totalIncome: 15000,
  balance: 6765.50
}))

// Trend percentage
const trendTrend = computed(() => {
  return -12.5 // vs last month
})

// Expense progress
const expenseProgress = computed(() => {
  return 65 // percentage of budget
})

// Category chart data
const categoryChart = ref([
  { name: '餐饮', amount: 1234.5, percentage: 25, icon: 'food' },
  { name: '交通', amount: 890, percentage: 18, icon: 'transport' },
  { name: '购物', amount: 756, percentage: 15, icon: 'shopping' },
  { name: '居住', amount: 2500, percentage: 20, icon: 'housing' },
  { name: '娱乐', amount: 423, percentage: 8, icon: 'entertainment' },
])

// Trend chart data
const trendChart = ref([
  { month: '1月', expense: 12000, income: 15000 },
  { month: '2月', expense: 9000, income: 15000 },
  { month: '3月', expense: 15000, income: 15000 },
  { month: '4月', expense: 8234, income: 15000 },
])

// Max expense for bar chart
const maxTrendExpense = computed(() => {
  return Math.max(...trendChart.value.map(item => item.expense), 1)
})

// Pie colors
const pieColors = ['#FB8B24', '#0F4C5C', '#06D6A0', '#EF476F', '#FFD166', '#3A86FF']

// Pie segments calculation
const pieSegments = computed(() => {
  let currentOffset = 0
  const circumference = 2 * Math.PI * 40 // r=40
  
  return categoryChart.value.map((item, index) => {
    const percentage = item.percentage || 0
    const dashLength = (percentage / 100) * circumference
    const segment = {
      color: pieColors[index % pieColors.length],
      dashArray: `${dashLength} ${circumference - dashLength}`,
      dashOffset: -currentOffset
    }
    currentOffset += dashLength
    return segment
  })
})

// Methods
const formatAmount = (amount: number) => {
  if (!amount) return '0.00'
  return Math.abs(amount).toLocaleString('zh-CN', {
    minimumFractionDigits: 2,
    maximumFractionDigits: 2
  })
}

const getCurrentPeriodLabel = () => {
  const labels: Record<string, string> = {
    week: t('analysis.thisWeek'),
    month: t('analysis.thisMonth'),
    year: t('analysis.thisYear'),
  }
  return labels[activePeriod.value]
}

const handleExport = () => {
  uni.showActionSheet({
    itemList: ['导出为 Excel', '导出为 PDF', '分享报告'],
    success: (res) => {
      // handle export
    }
  })
}

onMounted(async () => {
  // Load data
})
</script>

<style lang="scss" scoped>
.analysis-page {
  min-height: 100vh;
  background: var(--gzang-bg);
  position: relative;
}

// ================== Large Title Navigation ==================
.nav-large-title {
  background: var(--gzang-bg);
  padding: 0 var(--apple-space-4);
  padding-top: calc(constant(safe-area-inset-top) + var(--apple-space-3));
}

.nav-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--apple-space-3);
}

.nav-titles {
  display: flex;
  flex-direction: column;
}

.nav-title {
  font-size: var(--apple-text-4xl);
  font-weight: var(--apple-font-bold);
  color: var(--gzang-text-primary);
  letter-spacing: -0.5px;
}

.nav-actions {
  display: flex;
  align-items: center;
  gap: var(--apple-space-3);
}

.nav-action {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background: var(--gzang-surface);
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: var(--apple-shadow-xs);
  transition: all var(--apple-duration-fast) var(--apple-ease-out);
  
  &:active {
    transform: scale(0.95);
    background: var(--gzang-bg);
  }
}

// ================== Period Selector ==================
.period-selector {
  padding: var(--apple-space-3) var(--apple-space-4);
}

.period-scroll {
  display: flex;
  white-space: nowrap;
}

.period-chip {
  display: inline-flex;
  align-items: center;
  padding: 8px 16px;
  background: var(--gzang-surface);
  border-radius: var(--apple-radius-full);
  margin-right: var(--apple-space-2);
  font-size: var(--apple-text-sm);
  font-weight: var(--apple-font-medium);
  color: var(--gzang-text-secondary);
  transition: all var(--apple-duration-fast) var(--apple-ease-out);
  box-shadow: var(--apple-shadow-xs);
  
  &.active {
    background: var(--gzang-primary);
    color: white;
  }
}

// ================== Main Content ==================
.main-content {
  padding: 0 var(--apple-space-4);
}

// ================== Overview Card ==================
.overview-card {
  background: linear-gradient(145deg, var(--gzang-primary) 0%, var(--gzang-primary-light) 100%);
  border-radius: var(--apple-radius-xl);
  padding: var(--apple-space-5);
  margin-bottom: var(--apple-space-4);
  color: white;
  position: relative;
  overflow: hidden;
  
  &::before {
    content: '';
    position: absolute;
    top: -30px;
    right: -30px;
    width: 120px;
    height: 120px;
    border-radius: 50%;
    background: rgba(255, 255, 255, 0.05);
  }
}

.overview-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: var(--apple-space-3);
}

.overview-period {
  display: flex;
  align-items: center;
  gap: var(--apple-space-2);
}

.period-label {
  font-size: var(--apple-text-sm);
  opacity: 0.8;
}

.period-trend {
  display: flex;
  align-items: center;
  gap: 2px;
  padding: 2px 6px;
  background: rgba(255, 255, 255, 0.15);
  border-radius: var(--apple-radius-full);
  font-size: 10px;
  font-weight: var(--apple-font-semibold);
  
  &.trend-up {
    background: rgba(6, 214, 160, 0.2);
  }
  
  &.trend-down {
    background: rgba(255, 209, 102, 0.2);
  }
}

.vs-label {
  font-size: var(--apple-text-xs);
  opacity: 0.6;
}

.overview-amount {
  margin-bottom: var(--apple-space-4);
}

.amount-label {
  font-size: var(--apple-text-sm);
  opacity: 0.8;
  display: block;
  margin-bottom: var(--apple-space-1);
}

.amount-value {
  font-size: 36px;
  font-weight: var(--apple-font-bold);
  letter-spacing: -1px;
  
  &.income {
    color: var(--gzang-success);
  }
  
  &.expense {
    color: rgba(255, 209, 102, 1);
  }
}

.overview-bar {
  position: relative;
  z-index: 1;
}

.bar-track {
  width: 100%;
  height: 6px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: var(--apple-radius-full);
  overflow: hidden;
}

.bar-fill {
  height: 100%;
  background: var(--gzang-secondary);
  border-radius: var(--apple-radius-full);
  transition: width 0.5s var(--apple-ease-out);
}

// ================== Chart Card ==================
.chart-card {
  background: var(--gzang-surface);
  border-radius: var(--apple-radius-xl);
  padding: var(--apple-space-4);
  margin-bottom: var(--apple-space-4);
  box-shadow: var(--apple-shadow-sm);
}

.chart-header {
  margin-bottom: var(--apple-space-4);
}

.chart-title {
  font-size: var(--apple-text-lg);
  font-weight: var(--apple-font-semibold);
  color: var(--gzang-text-primary);
}

// Pie Chart
.pie-section {
  display: flex;
  align-items: center;
  gap: var(--apple-space-4);
}

.pie-wrapper {
  position: relative;
  width: 100px;
  height: 100px;
  flex-shrink: 0;
}

.pie-svg {
  width: 100%;
  height: 100%;
  transform: rotate(0deg);
}

.pie-center {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  text-align: center;
}

.pie-center-value {
  font-size: var(--apple-text-xl);
  font-weight: var(--apple-font-bold);
  color: var(--gzang-text-primary);
  display: block;
}

.pie-center-label {
  font-size: var(--apple-text-xs);
  color: var(--gzang-text-secondary);
}

.pie-legend {
  flex: 1;
}

.legend-item {
  display: flex;
  align-items: center;
  padding: 6px 0;
}

.legend-color {
  width: 8px;
  height: 8px;
  border-radius: 2px;
  margin-right: var(--apple-space-2);
  flex-shrink: 0;
}

.legend-name {
  flex: 1;
  font-size: var(--apple-text-sm);
  color: var(--gzang-text-secondary);
}

.legend-value {
  font-size: var(--apple-text-sm);
  font-weight: var(--apple-font-semibold);
  color: var(--gzang-text-primary);
}

// Bar Chart
.bar-chart {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  height: 120px;
  gap: var(--apple-space-2);
}

.bar-column {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  height: 100%;
}

.bar-wrapper {
  flex: 1;
  width: 100%;
  display: flex;
  align-items: flex-end;
  justify-content: center;
}

.bar-fill {
  width: 80%;
  min-height: 4px;
  border-radius: var(--apple-radius-xs) var(--apple-radius-xs) 0 0;
  transition: height 0.5s var(--apple-ease-out);
}

.bar-label {
  font-size: var(--apple-text-xs);
  color: var(--gzang-text-tertiary);
  margin-top: var(--apple-space-2);
}

// ================== Breakdown Card ==================
.breakdown-card {
  background: var(--gzang-surface);
  border-radius: var(--apple-radius-xl);
  padding: var(--apple-space-4);
  margin-bottom: var(--apple-space-4);
  box-shadow: var(--apple-shadow-sm);
}

.breakdown-list {
  display: flex;
  flex-direction: column;
  gap: var(--apple-space-3);
}

.breakdown-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.breakdown-left {
  display: flex;
  align-items: center;
  gap: var(--apple-space-3);
}

.breakdown-rank {
  width: 20px;
  height: 20px;
  border-radius: 50%;
  background: var(--gzang-bg);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 10px;
  font-weight: var(--apple-font-bold);
  color: var(--gzang-text-tertiary);
}

.breakdown-icon {
  width: 32px;
  height: 32px;
  border-radius: var(--apple-radius-sm);
  display: flex;
  align-items: center;
  justify-content: center;
}

.breakdown-name {
  font-size: var(--apple-text-sm);
  color: var(--gzang-text-primary);
  font-weight: var(--apple-font-medium);
}

.breakdown-right {
  display: flex;
  align-items: center;
  gap: var(--apple-space-3);
}

.breakdown-bar {
  width: 60px;
  height: 6px;
  background: var(--gzang-bg);
  border-radius: var(--apple-radius-full);
  overflow: hidden;
}

.breakdown-fill {
  height: 100%;
  border-radius: var(--apple-radius-full);
  transition: width 0.5s var(--apple-ease-out);
}

.breakdown-amount {
  font-size: var(--apple-text-sm);
  font-weight: var(--apple-font-semibold);
  min-width: 70px;
  text-align: right;
  
  &.income {
    color: var(--gzang-success);
  }
  
  &.expense {
    color: var(--gzang-text-primary);
  }
}

// ================== Balance Card ==================
.balance-card {
  background: var(--gzang-surface);
  border-radius: var(--apple-radius-xl);
  padding: var(--apple-space-4);
  margin-bottom: var(--apple-space-4);
  box-shadow: var(--apple-shadow-sm);
  display: flex;
  align-items: center;
}

.balance-item {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.balance-header {
  display: flex;
  align-items: center;
  gap: var(--apple-space-2);
  margin-bottom: var(--apple-space-2);
}

.balance-label {
  font-size: var(--apple-text-xs);
  color: var(--gzang-text-secondary);
}

.balance-value {
  font-size: var(--apple-text-lg);
  font-weight: var(--apple-font-bold);
  
  &.income {
    color: var(--gzang-success);
  }
  
  &.expense {
    color: var(--gzang-danger);
  }
}

.balance-divider {
  width: 1px;
  height: 40px;
  background: var(--gzang-border);
}

// Bottom Safe Area
.bottom-safe-area {
  height: calc(var(--apple-space-4) + 84px);
}

// ================== Dark Mode ==================
@media (prefers-color-scheme: dark) {
  .nav-large-title {
    background: var(--gzang-bg, #000000);
  }
  
  .nav-title {
    color: var(--gzang-text-primary, #FFFFFF);
  }
  
  .chart-card,
  .breakdown-card,
  .balance-card {
    background: var(--gzang-surface, #1C1C1E);
  }
  
  .chart-title,
  .breakdown-name {
    color: var(--gzang-text-primary, #FFFFFF);
  }
  
  .pie-center-value {
    color: var(--gzang-text-primary, #FFFFFF);
  }
}

[data-theme="dark"] {
  .nav-large-title {
    background: var(--gzang-bg, #000000);
  }
  
  .nav-title {
    color: var(--gzang-text-primary, #FFFFFF);
  }
}
</style>
