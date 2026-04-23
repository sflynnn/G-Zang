<template>
  <view class="transactions-page">
    <uni-nav-bar 
      left-icon="back" 
      title="交易记录" 
      @clickLeft="goBack"
      right-text="筛选"
      @clickRight="showFilter"
    />
    
    <!-- 筛选栏 -->
    <view class="filter-bar">
      <view class="filter-tabs">
        <view 
          v-for="tab in typeTabs" 
          :key="tab.value"
          :class="['filter-tab', { active: currentType === tab.value }]"
          @click="changeType(tab.value)"
        >
          {{ tab.label }}
        </view>
      </view>
      
      <view class="date-picker" @click="showDatePicker">
        <text>{{ dateRangeText }}</text>
        <text class="arrow">▼</text>
      </view>
    </view>

    <!-- 统计概览 -->
    <view class="summary-card" v-if="summary">
      <view class="summary-item">
        <view class="summary-label">收入</view>
        <view class="summary-value income">+{{ currentCurrency }}{{ formatAmount(summary.totalIncome) }}</view>
      </view>
      <view class="summary-divider"></view>
      <view class="summary-item">
        <view class="summary-label">支出</view>
        <view class="summary-value expense">-{{ currentCurrency }}{{ formatAmount(summary.totalExpense) }}</view>
      </view>
    </view>

    <!-- 交易列表 -->
    <scroll-view 
      class="transaction-scroll"
      scroll-y
      @scrolltolower="loadMore"
    >
      <view v-if="loading && transactionList.length === 0" class="loading-container">
        <uni-load-more status="loading" />
      </view>
      
      <view v-else-if="transactionList.length === 0" class="empty-state">
        <text class="empty-icon">📝</text>
        <text class="empty-text">暂无交易记录</text>
        <text class="empty-hint">点击下方按钮开始记账</text>
      </view>
      
      <view v-else class="transaction-list">
        <view 
          v-for="(group, date) in groupedTransactions" 
          :key="date"
          class="day-group"
        >
          <view class="day-header">
            <text class="day-date">{{ formatDayDate(date) }}</text>
            <view class="day-stats">
              <text class="income">+{{ currentCurrency }}{{ formatAmount(group.dayIncome) }}</text>
              <text class="expense">-{{ currentCurrency }}{{ formatAmount(group.dayExpense) }}</text>
            </view>
          </view>
          
          <view class="day-transactions">
            <view 
              v-for="item in group.items" 
              :key="item.id"
              class="transaction-item"
              @click="goToDetail(item.id)"
            >
              <view class="item-icon" :style="{ background: getCategoryBg(item.categoryId) }">
                {{ getCategoryIcon(item.categoryId) }}
              </view>
              <view class="item-info">
                <view class="item-name">{{ item.categoryName || '未分类' }}</view>
                <view class="item-account">{{ item.accountName || '未知账户' }}</view>
              </view>
              <view class="item-right">
                <view 
                  class="item-amount"
                  :class="item.type === 1 ? 'income' : 'expense'"
                >
                  {{ item.type === 1 ? '+' : '-' }}{{ currentCurrency }}{{ formatAmount(item.amount) }}
                </view>
                <view class="item-time">{{ formatTime(item.transactionTime) }}</view>
              </view>
            </view>
          </view>
        </view>
        
        <view v-if="hasMore" class="load-more">
          <uni-load-more :status="loading ? 'loading' : 'more'" />
        </view>
      </view>
    </scroll-view>

    <!-- 悬浮记账按钮 -->
    <view class="fab" @click="goToAccounting">
      <text class="fab-icon">+</text>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { useTransactionStore } from '@/stores/transaction'

const transactionStore = useTransactionStore()

const loading = ref(false)
const currentType = ref<number | null>(null)
const currentPage = ref(1)
const hasMore = ref(true)
const accountId = ref<number | null>(null)

const typeTabs = [
  { label: '全部', value: null },
  { label: '收入', value: 1 },
  { label: '支出', value: 2 }
]

const dateRangeText = ref('本月')

const transactionList = computed(() => transactionStore.transactionList.records || [])
const summary = computed(() => transactionStore.summary)

const groupedTransactions = computed(() => {
  const groups: Record<string, any> = {}
  
  transactionList.value.forEach((item: any) => {
    const date = item.transactionTime?.split('T')[0] || 'unknown'
    if (!groups[date]) {
      groups[date] = { items: [], dayIncome: 0, dayExpense: 0 }
    }
    groups[date].items.push(item)
    if (item.type === 1) {
      groups[date].dayIncome += parseFloat(item.amount) || 0
    } else {
      groups[date].dayExpense += parseFloat(item.amount) || 0
    }
  })
  
  return groups
})

const currentCurrency = '¥'

onLoad((options: any) => {
  if (options?.accountId) {
    accountId.value = parseInt(options.accountId)
  }
})

onMounted(async () => {
  await loadTransactions()
})

async function loadTransactions() {
  loading.value = true
  try {
    await transactionStore.fetchTransactions({
      current: currentPage.value,
      size: 20,
      type: currentType.value ?? undefined
    })
    hasMore.value = transactionList.value.length < (transactionStore.transactionList.total || 0)
  } catch (error) {
    // ignore
  } finally {
    loading.value = false
  }
}

async function loadMore() {
  if (!hasMore.value || loading.value) return
  currentPage.value++
  await loadTransactions()
}

function changeType(type: number | null) {
  currentType.value = type
  currentPage.value = 1
  transactionStore.transactionList.records = []
  loadTransactions()
}

function showFilter() {
  uni.showToast({ title: '筛选功能开发中', icon: 'none' })
}

function showDatePicker() {
  uni.showToast({ title: '日期选择开发中', icon: 'none' })
}

function formatAmount(amount: any): string {
  if (!amount && amount !== 0) return '0.00'
  const num = typeof amount === 'string' ? parseFloat(amount) : amount
  return num.toFixed(2).replace(/\B(?=(\d{3})+(?!\d))/g, ',')
}

function formatDayDate(dateStr: string): string {
  if (dateStr === 'unknown') return '未知日期'
  const date = new Date(dateStr)
  const today = new Date()
  const yesterday = new Date(today)
  yesterday.setDate(yesterday.getDate() - 1)
  
  if (dateStr === today.toISOString().split('T')[0]) {
    return '今天'
  } else if (dateStr === yesterday.toISOString().split('T')[0]) {
    return '昨天'
  } else {
    return `${date.getMonth() + 1}月${date.getDate()}日`
  }
}

function formatTime(timeStr: string): string {
  if (!timeStr) return ''
  const date = new Date(timeStr)
  return `${date.getHours().toString().padStart(2, '0')}:${date.getMinutes().toString().padStart(2, '0')}`
}

function getCategoryIcon(categoryId: number): string {
  // 从分类 store 获取图标
  const icons: Record<number, string> = {
    1: '🍜', 2: '🚇', 3: '🛒', 4: '🎮', 5: '💊', 6: '📚', 7: '🏠', 8: '📱'
  }
  return icons[categoryId] || '💰'
}

function getCategoryBg(categoryId: number): string {
  const bgs: Record<number, string> = {
    1: '#FFF3E0', 2: '#E3F2FD', 3: '#FCE4EC', 4: '#F3E5F5', 5: '#FFEBEE', 6: '#E8F5E9', 7: '#FFF8E1', 8: '#E0F7FA'
  }
  return bgs[categoryId] || '#F8F9FA'
}

function goBack() {
  uni.navigateBack()
}

function goToDetail(id: number) {
  uni.navigateTo({ url: `/pages/transactions/detail?id=${id}` })
}

function goToAccounting() {
  uni.switchTab({ url: '/pages/accounting/index' })
}
</script>

<style scoped lang="scss">
.transactions-page {
  min-height: 100vh;
  background-color: #F8F9FA;
  display: flex;
  flex-direction: column;
}

.filter-bar {
  background: #fff;
  padding: 16rpx 24rpx;
  display: flex;
  align-items: center;
  justify-content: space-between;
  border-bottom: 1rpx solid #F0F0F0;
}

.filter-tabs {
  display: flex;
  gap: 16rpx;
}

.filter-tab {
  padding: 12rpx 24rpx;
  border-radius: 20rpx;
  font-size: 26rpx;
  color: #666;
  background: #F8F9FA;
  
  &.active {
    background: #0F4C5C;
    color: #fff;
    font-weight: 500;
  }
}

.date-picker {
  display: flex;
  align-items: center;
  gap: 8rpx;
  font-size: 26rpx;
  color: #666;
  
  .arrow {
    font-size: 20rpx;
  }
}

.summary-card {
  background: linear-gradient(135deg, #0F4C5C 0%, #073B4C 100%);
  margin: 24rpx;
  border-radius: 16rpx;
  padding: 24rpx;
  display: flex;
  align-items: center;
  color: #fff;
}

.summary-item {
  flex: 1;
  text-align: center;
}

.summary-label {
  font-size: 24rpx;
  opacity: 0.8;
  margin-bottom: 8rpx;
}

.summary-value {
  font-size: 32rpx;
  font-weight: 700;
  
  &.income {
    color: #06D6A0;
  }
  
  &.expense {
    color: #FFD166;
  }
}

.summary-divider {
  width: 2rpx;
  height: 60rpx;
  background: rgba(255, 255, 255, 0.2);
}

.transaction-scroll {
  flex: 1;
  height: calc(100vh - 300rpx);
}

.loading-container {
  padding: 200rpx 0;
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 200rpx 0;
  
  .empty-icon {
    font-size: 120rpx;
    margin-bottom: 32rpx;
  }
  
  .empty-text {
    font-size: 32rpx;
    color: #666;
    margin-bottom: 16rpx;
  }
  
  .empty-hint {
    font-size: 26rpx;
    color: #9CA3AF;
  }
}

.transaction-list {
  padding: 0 24rpx 120rpx;
}

.day-group {
  margin-bottom: 24rpx;
}

.day-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16rpx 0;
}

.day-date {
  font-size: 26rpx;
  font-weight: 600;
  color: #666;
}

.day-stats {
  display: flex;
  gap: 16rpx;
  font-size: 24rpx;
  
  .income {
    color: #06D6A0;
  }
  
  .expense {
    color: #EF476F;
  }
}

.day-transactions {
  background: #fff;
  border-radius: 16rpx;
  overflow: hidden;
}

.transaction-item {
  display: flex;
  align-items: center;
  padding: 24rpx;
  border-bottom: 1rpx solid #F3F4F6;
  
  &:last-child {
    border-bottom: none;
  }
}

.item-icon {
  width: 80rpx;
  height: 80rpx;
  border-radius: 16rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 36rpx;
  margin-right: 16rpx;
  flex-shrink: 0;
}

.item-info {
  flex: 1;
  
  .item-name {
    font-size: 28rpx;
    font-weight: 500;
    color: #1F2937;
  }
  
  .item-account {
    font-size: 24rpx;
    color: #9CA3AF;
    margin-top: 4rpx;
  }
}

.item-right {
  text-align: right;
  
  .item-amount {
    font-size: 28rpx;
    font-weight: 600;
    
    &.income {
      color: #06D6A0;
    }
    
    &.expense {
      color: #1F2937;
    }
  }
  
  .item-time {
    font-size: 24rpx;
    color: #9CA3AF;
    margin-top: 4rpx;
  }
}

.load-more {
  padding: 32rpx 0;
}

.fab {
  position: fixed;
  right: 32rpx;
  bottom: 120rpx;
  width: 112rpx;
  height: 112rpx;
  background: #FB8B24;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 8rpx 24rpx rgba(251, 139, 36, 0.4);
  
  .fab-icon {
    font-size: 64rpx;
    color: #fff;
    font-weight: 300;
    margin-top: -4rpx;
  }
}
</style>
