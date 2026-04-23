<template>
  <view class="transaction-list">
    <view
      v-if="showGrouped"
      v-for="group in groupedTransactions"
      :key="group.date"
      class="group"
    >
      <view class="group-header">
        <text class="group-date">{{ formatDate(group.date) }}</text>
        <view class="group-summary">
          <text v-if="group.totalIncome > 0" class="summary-income">
            收 {{ formatAmount(group.totalIncome) }}
          </text>
          <text v-if="group.totalExpense > 0" class="summary-expense">
            支 {{ formatAmount(group.totalExpense) }}
          </text>
        </view>
      </view>

      <view class="group-items">
        <view
          v-for="item in group.transactions"
          :key="item.id"
          class="transaction-item"
          @click="handleClick(item)"
        >
          <view class="item-left">
            <view
              class="item-icon"
              :style="{ backgroundColor: getIconBg(item.categoryIcon) }"
            >
              <uni-icons
                :type="getIconType(item.categoryIcon)"
                :size="18"
                :color="getIconColor(item.categoryIcon)"
              />
            </view>
            <view class="item-info">
              <text class="item-category">{{ item.categoryName }}</text>
              <text class="item-account">{{ item.accountName }}</text>
            </view>
          </view>
          <view class="item-right">
            <text
              class="item-amount amount"
              :class="getAmountClass(item.type)"
            >
              {{ formatAmountWithSign(item.amount, item.type) }}
            </text>
            <text class="item-time">{{ formatTime(item.transactionTime) }}</text>
          </view>
        </view>
      </view>
    </view>

    <view v-else class="list-items">
      <view
        v-for="item in transactions"
        :key="item.id"
        class="transaction-item"
        @click="handleClick(item)"
      >
        <view class="item-left">
          <view
            class="item-icon"
            :style="{ backgroundColor: getIconBg(item.categoryIcon) }"
          >
            <uni-icons
              :type="getIconType(item.categoryIcon)"
              :size="18"
              :color="getIconColor(item.categoryIcon)"
            />
          </view>
          <view class="item-info">
            <text class="item-category">{{ item.categoryName }}</text>
            <text class="item-account">{{ item.accountName }}</text>
          </view>
        </view>
        <view class="item-right">
          <text
            class="item-amount amount"
            :class="getAmountClass(item.type)"
          >
            {{ formatAmountWithSign(item.amount, item.type) }}
          </text>
          <text class="item-time">{{ formatTime(item.transactionTime) }}</text>
        </view>
      </view>
    </view>

    <view v-if="loading" class="loading-more">
      <uni-load-more status="loading" />
    </view>

    <view v-if="!loading && transactions.length === 0" class="empty-state">
      <uni-icons type="list" :size="48" color="#9CA3AF" />
      <text class="empty-text">暂无交易记录</text>
    </view>
  </view>
</template>

<script setup lang="ts">
import dayjs from 'dayjs'
import type { Transaction, TransactionGroup, TransactionType } from '@/types/transaction'

interface Props {
  transactions: Transaction[]
  loading?: boolean
  showGrouped?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  transactions: () => [],
  loading: false,
  showGrouped: true
})

const emit = defineEmits<{
  'item-click': [transaction: Transaction]
}>()

const groupedTransactions = computed(() => {
  const groups: Map<string, Transaction[]> = new Map()

  for (const item of props.transactions) {
    const date = dayjs(item.transactionTime).format('YYYY-MM-DD')
    if (!groups.has(date)) {
      groups.set(date, [])
    }
    groups.get(date)!.push(item)
  }

  const result: TransactionGroup[] = []
  groups.forEach((items, date) => {
    result.push({
      date,
      transactions: items,
      totalIncome: items.filter(i => i.type === 1).reduce((sum, i) => sum + i.amount, 0),
      totalExpense: items.filter(i => i.type === 2).reduce((sum, i) => sum + i.amount, 0)
    })
  })

  return result.sort((a, b) => b.date.localeCompare(a.date))
})

const formatDate = (date: string): string => {
  const d = dayjs(date)
  const today = dayjs().startOf('day')
  const yesterday = today.subtract(1, 'day')

  if (d.isSame(today, 'day')) return '今天'
  if (d.isSame(yesterday, 'day')) return '昨天'
  if (d.isSame(today, 'week')) return d.format('dddd')
  return d.format('MM月DD日')
}

const formatTime = (time: string): string => {
  return dayjs(time).format('HH:mm')
}

const formatAmount = (amount: number): string => {
  return amount.toLocaleString('zh-CN', {
    minimumFractionDigits: 2,
    maximumFractionDigits: 2
  })
}

const formatAmountWithSign = (amount: number, type: TransactionType): string => {
  const prefix = type === 1 ? '+' : type === 2 ? '-' : ''
  return `${prefix}¥${formatAmount(amount)}`
}

const getAmountClass = (type: TransactionType): string => {
  const classMap: Record<string, string> = {
    '1': 'amount--income',
    '2': 'amount--expense',
    '3': 'amount--transfer'
  }
  return classMap[String(type)] || ''
}

const getIconType = (icon?: string): string => {
  const iconMap: Record<string, string> = {
    food: 'paperplane',
    transport: 'location',
    shopping: 'cart',
    entertainment: 'star',
    medical: 'first-aid-box',
    education: 'bookmark',
    housing: 'home',
    communication: 'phone',
    salary: 'wallet',
    investment: 'chartbars',
    gift: 'gift',
    other: 'circle'
  }
  return iconMap[icon || ''] || 'circle'
}

const getIconBg = (icon?: string): string => {
  const colorMap: Record<string, string> = {
    food: '#FFF3E0',
    transport: '#E3F2FD',
    shopping: '#FCE4EC',
    entertainment: '#F3E5F5',
    medical: '#FFEBEE',
    education: '#E8F5E9',
    housing: '#FFF8E1',
    communication: '#E0F7FA',
    salary: '#E8F5E9',
    investment: '#FFF8E1',
    gift: '#FCE4EC',
    other: '#F5F5F5'
  }
  return colorMap[icon || ''] || '#F5F5F5'
}

const getIconColor = (icon?: string): string => {
  const colorMap: Record<string, string> = {
    food: '#FF9800',
    transport: '#2196F3',
    shopping: '#E91E63',
    entertainment: '#9C27B0',
    medical: '#F44336',
    education: '#4CAF50',
    housing: '#FFC107',
    communication: '#00BCD4',
    salary: '#4CAF50',
    investment: '#FFC107',
    gift: '#E91E63',
    other: '#9E9E9E'
  }
  return colorMap[icon || ''] || '#9E9E9E'
}

const handleClick = (item: Transaction) => {
  emit('item-click', item)
}
</script>

<script lang="ts">
import { computed } from 'vue'
export default {}
</script>

<style scoped lang="scss">
.transaction-list {
  min-height: 100%;
}

.group {
  margin-bottom: 16rpx;
}

.group-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16rpx 24rpx;
  background-color: #fafafa;
}

.group-date {
  font-size: 26rpx;
  color: var(--gzang-text-secondary);
  font-weight: 500;
}

.group-summary {
  display: flex;
  gap: 16rpx;
}

.summary-income,
.summary-expense {
  font-size: 24rpx;
  font-family: 'Roboto Mono', monospace;
}

.summary-income {
  color: var(--gzang-success);
}

.summary-expense {
  color: var(--gzang-danger);
}

.group-items,
.list-items {
  background-color: var(--gzang-surface);
}

.transaction-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 24rpx;
  margin: 0 24rpx;
  border-bottom: 1rpx solid var(--gzang-border);
  transition: background-color 0.15s;

  &:last-child {
    border-bottom: none;
  }

  &:active {
    background-color: #f8f8f8;
  }
}

.item-left {
  display: flex;
  align-items: center;
  gap: 16rpx;
}

.item-icon {
  width: 72rpx;
  height: 72rpx;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.item-info {
  display: flex;
  flex-direction: column;
  gap: 4rpx;
}

.item-category {
  font-size: 30rpx;
  color: var(--gzang-text-primary);
  font-weight: 500;
}

.item-account {
  font-size: 24rpx;
  color: var(--gzang-text-secondary);
}

.item-right {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 4rpx;
}

.item-amount {
  font-size: 32rpx;
  font-family: 'Roboto Mono', monospace;
  font-variant-numeric: tabular-nums;
}

.item-time {
  font-size: 24rpx;
  color: var(--gzang-text-secondary);
}

.loading-more {
  padding: 24rpx;
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 120rpx 0;
  gap: 16rpx;
}

.empty-text {
  font-size: 28rpx;
  color: var(--gzang-text-secondary);
}
</style>
