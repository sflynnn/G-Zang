<template>
  <view class="budget-page apple-style">
    <!-- Large Title Navigation -->
    <view class="nav-large-title">
      <view class="nav-header">
        <text class="nav-title">{{ t('budget.title') }}</text>
        <view class="nav-add" @click="goToCreate">
          <AppleIcon name="plus" :size="20" color="var(--gzang-primary)" />
        </view>
      </view>
    </view>

    <!-- Period Tabs -->
    <view class="period-tabs">
      <view 
        v-for="tab in periodOptions" 
        :key="tab.value"
        class="period-tab"
        :class="{ active: currentPeriod === tab.value }"
        @click="changePeriod(tab.value)"
      >
        {{ tab.label }}
      </view>
    </view>

    <scroll-view class="main-content" scroll-y="true">
      <!-- Budget Overview Card -->
      <view class="overview-card">
        <view class="overview-header">
          <text class="overview-title">{{ periodLabel }}预算</text>
          <text class="overview-percent" :class="overallStatus">{{ overallPercent }}%</text>
        </view>
        <view class="overview-progress">
          <view 
            class="progress-fill"
            :class="overallStatus"
            :style="{ width: Math.min(overallPercent, 100) + '%' }"
          ></view>
        </view>
        <view class="overview-footer">
          <view class="footer-item">
            <text class="footer-label">已花费</text>
            <text class="footer-value expense">{{ currentCurrency }}{{ formatAmount(totalSpent) }}</text>
          </view>
          <view class="footer-item">
            <text class="footer-label">剩余</text>
            <text class="footer-value" :class="remaining >= 0 ? 'income' : 'expense'">
              {{ remaining >= 0 ? '' : '-' }}{{ currentCurrency }}{{ formatAmount(Math.abs(remaining)) }}
            </text>
          </view>
          <view class="footer-item">
            <text class="footer-label">预算</text>
            <text class="footer-value primary">{{ currentCurrency }}{{ formatAmount(totalBudget) }}</text>
          </view>
        </view>
      </view>

      <!-- Budget List -->
      <view class="budget-list">
        <view 
          v-for="item in budgets" 
          :key="item.id"
          class="budget-card"
          :class="{ overbudget: item.percent > 100 }"
          @click="goToDetail(item.id)"
        >
          <view class="card-header">
            <view class="category-info">
              <view class="category-icon" :style="{ background: item.color + '20' }">
                <AppleIcon :name="item.icon" :size="20" :color="item.color" />
              </view>
              <text class="category-name">{{ item.name }}</text>
            </view>
            <text class="budget-percent" :class="getStatusClass(item.percent)">{{ item.percent }}%</text>
          </view>
          
          <view class="card-progress">
            <view class="progress-track">
              <view 
                class="progress-fill"
                :class="getStatusClass(item.percent)"
                :style="{ width: Math.min(item.percent, 100) + '%' }"
              ></view>
            </view>
          </view>
          
          <view class="card-footer">
            <text class="spent">
              已花费 <text class="amount">{{ currentCurrency }}{{ formatAmount(item.spent) }}</text>
            </text>
            <text class="budget">
              预算 <text class="amount">{{ currentCurrency }}{{ formatAmount(item.budget) }}</text>
            </text>
          </view>
          
          <view v-if="item.percent > 100" class="overbudget-badge">
            超支 {{ currentCurrency }}{{ formatAmount(item.spent - item.budget) }}
          </view>
        </view>
      </view>

      <!-- Empty State -->
      <view v-if="budgets.length === 0" class="empty-state">
        <view class="empty-icon-wrapper">
          <AppleIcon name="chart" :size="48" color="var(--gzang-text-tertiary)" />
        </view>
        <text class="empty-title">暂无预算</text>
        <text class="empty-desc">点击右上角添加预算</text>
      </view>

      <view class="bottom-safe-area"></view>
    </scroll-view>
  </view>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useI18n } from 'vue-i18n'
import AppleIcon from '@/components/common/AppleIcon/index.vue'

const { t } = useI18n()

const currentPeriod = ref('month')
const periodOptions = [
  { label: '周', value: 'week' },
  { label: '月', value: 'month' },
  { label: '年', value: 'year' }
]

const budgets = ref([
  { id: 1, name: '餐饮', icon: 'food', color: '#FB8B24', budget: 2500, spent: 1700, percent: 68 },
  { id: 2, name: '购物', icon: 'shopping', color: '#EF476F', budget: 2000, spent: 2160, percent: 108 },
  { id: 3, name: '交通', icon: 'transport', color: '#0F4C5C', budget: 400, spent: 180, percent: 45 },
  { id: 4, name: '娱乐', icon: 'entertainment', color: '#9B59B6', budget: 800, spent: 650, percent: 81 },
])

const periodLabel = computed(() => periodOptions.find(t => t.value === currentPeriod.value)?.label || '月')

const totalBudget = computed(() => budgets.value.reduce((sum, b) => sum + b.budget, 0))
const totalSpent = computed(() => budgets.value.reduce((sum, b) => sum + b.spent, 0))
const remaining = computed(() => totalBudget.value - totalSpent.value)

const overallPercent = computed(() => {
  if (totalBudget.value === 0) return 0
  return Math.round((totalSpent.value / totalBudget.value) * 100)
})

const overallStatus = computed(() => getStatusClass(overallPercent.value))

function getStatusClass(percent: number): string {
  if (percent > 100) return 'danger'
  if (percent >= 80) return 'warning'
  return 'success'
}

const currentCurrency = '¥'

function changePeriod(period: string) {
  currentPeriod.value = period
}

function formatAmount(amount: number): string {
  return Math.abs(amount).toLocaleString('zh-CN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })
}

function goBack() { uni.navigateBack() }
function goToCreate() { uni.navigateTo({ url: '/pages/budget/create' }) }
function goToDetail(id: number) { uni.showToast({ title: '预算详情开发中', icon: 'none' }) }
</script>

<style lang="scss" scoped>
.budget-page {
  min-height: 100vh;
  background: var(--gzang-bg);
}

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

.nav-title {
  font-size: var(--apple-text-4xl);
  font-weight: var(--apple-font-bold);
  color: var(--gzang-text-primary);
}

.nav-add {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background: var(--gzang-surface);
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: var(--apple-shadow-xs);
}

.period-tabs {
  display: flex;
  padding: 0 var(--apple-space-4);
  gap: var(--apple-space-2);
  margin-bottom: var(--apple-space-4);
}

.period-tab {
  flex: 1;
  padding: var(--apple-space-3);
  text-align: center;
  background: var(--gzang-surface);
  border-radius: var(--apple-radius-lg);
  font-size: var(--apple-text-sm);
  font-weight: var(--apple-font-medium);
  color: var(--gzang-text-secondary);
  transition: all var(--apple-duration-fast);
  
  &.active {
    background: var(--gzang-primary);
    color: white;
    box-shadow: var(--apple-shadow-sm);
  }
}

.main-content {
  padding: 0 var(--apple-space-4);
}

.overview-card {
  background: linear-gradient(145deg, var(--gzang-primary) 0%, var(--gzang-primary-light) 100%);
  border-radius: var(--apple-radius-xl);
  padding: var(--apple-space-5);
  margin-bottom: var(--apple-space-5);
  color: white;
  box-shadow: 0 8px 32px rgba(15, 76, 92, 0.25);
}

.overview-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--apple-space-4);
}

.overview-title {
  font-size: var(--apple-text-sm);
  opacity: 0.8;
}

.overview-percent {
  font-size: var(--apple-text-2xl);
  font-weight: var(--apple-font-bold);
  
  &.success { color: var(--gzang-success); }
  &.warning { color: rgba(255, 209, 102, 1); }
  &.danger { color: var(--gzang-danger); }
}

.overview-progress {
  margin-bottom: var(--apple-space-4);
}

.progress-track {
  height: 8px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: var(--apple-radius-full);
  overflow: hidden;
}

.progress-fill {
  height: 100%;
  border-radius: var(--apple-radius-full);
  transition: width 0.5s var(--apple-ease-out);
  
  &.success { background: var(--gzang-success); }
  &.warning { background: rgba(255, 209, 102, 1); }
  &.danger { background: var(--gzang-danger); }
}

.overview-footer {
  display: flex;
  justify-content: space-between;
}

.footer-item {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.footer-label {
  font-size: var(--apple-text-xs);
  opacity: 0.7;
  margin-bottom: 4px;
}

.footer-value {
  font-size: var(--apple-text-base);
  font-weight: var(--apple-font-semibold);
  
  &.income { color: var(--gzang-success); }
  &.expense { color: var(--gzang-danger); }
  &.primary { color: white; }
}

.budget-list {
  display: flex;
  flex-direction: column;
  gap: var(--apple-space-4);
}

.budget-card {
  background: var(--gzang-surface);
  border-radius: var(--apple-radius-xl);
  padding: var(--apple-space-4);
  box-shadow: var(--apple-shadow-sm);
  position: relative;
  transition: all var(--apple-duration-fast);
  
  &:active { transform: scale(0.98); }
  
  &.overbudget {
    border: 1px solid var(--gzang-danger);
  }
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--apple-space-3);
}

.category-info {
  display: flex;
  align-items: center;
  gap: var(--apple-space-3);
}

.category-icon {
  width: 40px;
  height: 40px;
  border-radius: var(--apple-radius-md);
  display: flex;
  align-items: center;
  justify-content: center;
}

.category-name {
  font-size: var(--apple-text-base);
  font-weight: var(--apple-font-semibold);
  color: var(--gzang-text-primary);
}

.budget-percent {
  font-size: var(--apple-text-lg);
  font-weight: var(--apple-font-bold);
  
  &.success { color: var(--gzang-success); }
  &.warning { color: rgba(255, 209, 102, 1); }
  &.danger { color: var(--gzang-danger); }
}

.card-progress {
  margin-bottom: var(--apple-space-3);
}

.card-footer {
  display: flex;
  justify-content: space-between;
  font-size: var(--apple-text-sm);
  color: var(--gzang-text-secondary);
  
  .amount {
    font-weight: var(--apple-font-semibold);
    color: var(--gzang-text-primary);
  }
}

.overbudget-badge {
  position: absolute;
  top: -10px;
  right: 12px;
  background: var(--gzang-danger);
  color: white;
  padding: 4px 12px;
  border-radius: var(--apple-radius-full);
  font-size: var(--apple-text-xs);
  font-weight: var(--apple-font-semibold);
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: var(--apple-space-10) var(--apple-space-4);
}

.empty-icon-wrapper {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  background: var(--gzang-bg);
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: var(--apple-space-4);
}

.empty-title {
  font-size: var(--apple-text-lg);
  font-weight: var(--apple-font-semibold);
  color: var(--gzang-text-primary);
  margin-bottom: var(--apple-space-2);
}

.empty-desc {
  font-size: var(--apple-text-sm);
  color: var(--gzang-text-secondary);
}

.bottom-safe-area {
  height: var(--apple-space-4);
}
</style>
