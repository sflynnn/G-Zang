<template>
  <view class="budget-card">
    <view class="card-header">
      <view class="header-left">
        <view
          class="category-icon"
          :style="{ backgroundColor: categoryColor || '#F5F5F5' }"
        >
          <uni-icons
            :type="categoryIcon || 'circle'"
            :size="18"
            color="white"
          />
        </view>
        <text class="category-name">{{ categoryName }}</text>
      </view>
      <text class="period">{{ period }}</text>
    </view>

    <view class="budget-info">
      <view class="info-row">
        <text class="info-label">预算</text>
        <text class="info-value amount">¥{{ formatAmount(budgetAmount) }}</text>
      </view>
      <view class="info-row">
        <text class="info-label">已用</text>
        <text class="info-value amount info-value--expense">
          ¥{{ formatAmount(spentAmount) }}
        </text>
      </view>
      <view class="info-row">
        <text class="info-label">剩余</text>
        <text class="info-value amount" :class="remainingClass">
          ¥{{ formatAmount(remainingAmount) }}
        </text>
      </view>
    </view>

    <view class="progress-section">
      <view class="progress-bar">
        <view
          class="progress-fill"
          :style="{ width: `${progressWidth}%`, backgroundColor: progressColor }"
        ></view>
      </view>
      <text class="progress-text">{{ progressPercent }}%</text>
    </view>

    <view v-if="remainingAmount < 0" class="alert-message">
      <uni-icons type="alert" :size="14" color="var(--gzang-danger)" />
      <text class="alert-text">已超支 {{ formatAmount(Math.abs(remainingAmount)) }} 元</text>
    </view>
  </view>
</template>

<script setup lang="ts">
import { computed } from 'vue'

interface Props {
  categoryName: string
  categoryIcon?: string
  categoryColor?: string
  budgetAmount: number
  spentAmount: number
  period?: string
}

const props = withDefaults(defineProps<Props>(), {
  period: '本月'
})

const remainingAmount = computed(() => props.budgetAmount - props.spentAmount)

const progressPercent = computed(() => {
  if (props.budgetAmount === 0) return 0
  return Math.min(100, Math.round((props.spentAmount / props.budgetAmount) * 100))
})

const progressWidth = computed(() => {
  return Math.min(100, (props.spentAmount / props.budgetAmount) * 100)
})

const progressColor = computed(() => {
  const percent = progressPercent.value
  if (percent >= 100) return 'var(--gzang-danger)'
  if (percent >= 80) return 'var(--gzang-warning)'
  return 'var(--gzang-success)'
})

const remainingClass = computed(() => {
  if (remainingAmount.value < 0) return 'amount--expense'
  if (remainingAmount.value < props.budgetAmount * 0.2) return 'amount--warning'
  return 'amount--income'
})

const formatAmount = (amount: number): string => {
  return Math.abs(amount).toLocaleString('zh-CN', {
    minimumFractionDigits: 2,
    maximumFractionDigits: 2
  })
}
</script>

<style scoped lang="scss">
.budget-card {
  background-color: var(--gzang-surface);
  border-radius: 20rpx;
  padding: 24rpx;
  box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.04);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20rpx;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 12rpx;
}

.category-icon {
  width: 56rpx;
  height: 56rpx;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.category-name {
  font-size: 30rpx;
  font-weight: 600;
  color: var(--gzang-text-primary);
}

.period {
  font-size: 24rpx;
  color: var(--gzang-text-secondary);
}

.budget-info {
  display: flex;
  flex-direction: column;
  gap: 8rpx;
  margin-bottom: 20rpx;
}

.info-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.info-label {
  font-size: 26rpx;
  color: var(--gzang-text-secondary);
}

.info-value {
  font-size: 28rpx;
  font-family: 'Roboto Mono', monospace;
  font-variant-numeric: tabular-nums;
}

.info-value--expense {
  color: var(--gzang-danger);
}

.amount--income {
  color: var(--gzang-success);
}

.amount--warning {
  color: var(--gzang-warning);
}

.amount--expense {
  color: var(--gzang-danger);
}

.progress-section {
  display: flex;
  align-items: center;
  gap: 16rpx;
  margin-bottom: 12rpx;
}

.progress-bar {
  flex: 1;
  height: 12rpx;
  background-color: #f0f0f0;
  border-radius: 6rpx;
  overflow: hidden;
}

.progress-fill {
  height: 100%;
  border-radius: 6rpx;
  transition: width 0.3s ease, background-color 0.3s ease;
}

.progress-text {
  font-size: 24rpx;
  color: var(--gzang-text-secondary);
  min-width: 80rpx;
  text-align: right;
}

.alert-message {
  display: flex;
  align-items: center;
  gap: 6rpx;
  padding: 10rpx 16rpx;
  background-color: rgba(239, 71, 111, 0.06);
  border-radius: 8rpx;
}

.alert-text {
  font-size: 24rpx;
  color: var(--gzang-danger);
}
</style>
