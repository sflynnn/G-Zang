<template>
  <view class="budget-progress" :class="{ compact, warning: isWarning, danger: isDanger }">
    <!-- 进度信息 -->
    <view class="progress-info">
      <view class="info-left">
        <text class="budget-label">{{ label || '本月额度' }}</text>
        <text class="budget-amount">
          <text class="remaining">{{ formattedRemaining }}</text>
          <text class="separator"> / </text>
          <text class="total">{{ formattedBudget }}</text>
        </text>
      </view>
      <view class="info-right">
        <text class="percent-text">{{ percentUsed }}%</text>
      </view>
    </view>

    <!-- 进度条 -->
    <view class="progress-bar">
      <view 
        class="progress-fill" 
        :style="{ width: `${Math.min(percentUsed, 100)}%` }"
      ></view>
      <!-- 警告线（80%） -->
      <view 
        v-if="showWarningLine && warningThreshold > 0"
        class="warning-line"
        :style="{ left: `${warningThreshold}%` }"
      ></view>
    </view>

    <!-- 详情提示 -->
    <view v-if="showDetail" class="progress-detail">
      <view class="detail-item">
        <text class="detail-label">已用</text>
        <text class="detail-value spent">{{ formattedSpent }}</text>
      </view>
      <view class="detail-item">
        <text class="detail-label">剩余</text>
        <text class="detail-value remaining">{{ formattedRemaining }}</text>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { computed } from 'vue'

interface Props {
  budget: number           // 总额度
  spent: number          // 已使用
  remaining?: number    // 剩余（可选，不传则自动计算）
  label?: string         // 标签文本
  showWarningLine?: boolean   // 是否显示警告线
  warningThreshold?: number   // 警告阈值百分比
  showDetail?: boolean   // 是否显示详情
  compact?: boolean      // 紧凑模式
}

const props = withDefaults(defineProps<Props>(), {
  remaining: -1,
  label: '',
  showWarningLine: true,
  warningThreshold: 80,
  showDetail: false,
  compact: false
})

// 格式化金额
const formatAmount = (amount: number): string => {
  if (amount >= 10000) {
    return (amount / 10000).toFixed(1) + 'w'
  }
  if (amount >= 1000) {
    return (amount / 1000).toFixed(1) + 'k'
  }
  return amount.toFixed(0)
}

const formattedBudget = computed(() => formatAmount(props.budget))
const formattedSpent = computed(() => formatAmount(props.spent))
const formattedRemaining = computed(() => formatAmount(props.remaining >= 0 ? props.remaining : props.budget - props.spent))

// 计算剩余
const actualRemaining = computed(() => {
  return props.remaining >= 0 ? props.remaining : props.budget - props.spent
})

// 计算百分比
const percentUsed = computed(() => {
  if (props.budget <= 0) return 0
  return Math.round((props.spent / props.budget) * 100)
})

// 是否警告状态
const isWarning = computed(() => {
  return percentUsed.value >= props.warningThreshold && percentUsed.value < 100
})

// 是否危险状态
const isDanger = computed(() => {
  return percentUsed.value >= 100
})
</script>

<style lang="scss" scoped>
.budget-progress {
  width: 100%;
  padding: 20rpx 24rpx;
  background: var(--gzang-surface);
  border-radius: var(--apple-radius-lg);
  box-shadow: var(--apple-shadow-xs);

  &.compact {
    padding: 12rpx 16rpx;
  }

  &.warning {
    background: linear-gradient(135deg, rgba(255, 209, 102, 0.1) 0%, rgba(255, 193, 7, 0.1) 100%);
    border: 1px solid rgba(255, 209, 102, 0.3);
  }

  &.danger {
    background: linear-gradient(135deg, rgba(239, 71, 111, 0.1) 0%, rgba(220, 53, 69, 0.1) 100%);
    border: 1px solid rgba(239, 71, 111, 0.3);
  }
}

.progress-info {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  margin-bottom: 16rpx;
}

.compact .progress-info {
  margin-bottom: 12rpx;
}

.info-left {
  display: flex;
  flex-direction: column;
  gap: 4rpx;
}

.budget-label {
  font-size: var(--apple-text-xs);
  color: var(--gzang-text-tertiary);
}

.budget-amount {
  font-size: var(--apple-text-base);
  font-family: var(--font-mono);
  font-variant-numeric: tabular-nums;
}

.separator {
  color: var(--gzang-text-tertiary);
  margin: 0 4rpx;
}

.remaining {
  color: var(--gzang-success);
  font-weight: var(--apple-font-semibold);
}

.danger .remaining {
  color: var(--gzang-danger);
}

.warning .remaining {
  color: #ffc107;
}

.total {
  color: var(--gzang-text-secondary);
  font-weight: var(--apple-font-medium);
}

.info-right {
  .percent-text {
    font-size: var(--apple-text-lg);
    font-weight: var(--apple-font-bold);
    font-family: var(--font-mono);
    color: var(--gzang-success);
  }

  .danger & .percent-text {
    color: var(--gzang-danger);
  }

  .warning & .percent-text {
    color: #ffc107;
  }
}

.progress-bar {
  position: relative;
  height: 12rpx;
  background: var(--gzang-bg);
  border-radius: 6rpx;
  overflow: hidden;

  .compact & {
    height: 8rpx;
    border-radius: 4rpx;
  }
}

.progress-fill {
  height: 100%;
  background: linear-gradient(90deg, var(--gzang-success) 0%, #06d6a0 100%);
  border-radius: 6rpx;
  transition: width 0.3s ease-out;

  .compact & {
    border-radius: 4rpx;
  }

  .warning & {
    background: linear-gradient(90deg, #ffc107 0%, #ff9800 100%);
  }

  .danger & {
    background: linear-gradient(90deg, var(--gzang-danger) 0%, #dc3545 100%);
  }
}

.warning-line {
  position: absolute;
  top: 0;
  bottom: 0;
  width: 2rpx;
  background: var(--gzang-danger);
  opacity: 0.6;
}

.progress-detail {
  display: flex;
  justify-content: space-between;
  margin-top: 16rpx;
  padding-top: 16rpx;
  border-top: 1px dashed var(--gzang-border);

  .compact & {
    margin-top: 12rpx;
    padding-top: 12rpx;
  }
}

.detail-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4rpx;
}

.detail-label {
  font-size: var(--apple-text-xs);
  color: var(--gzang-text-tertiary);
}

.detail-value {
  font-size: var(--apple-text-sm);
  font-weight: var(--apple-font-semibold);
  font-family: var(--font-mono);

  &.spent {
    color: var(--gzang-text-secondary);
  }

  &.remaining {
    color: var(--gzang-success);
  }
}
</style>
