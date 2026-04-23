<template>
  <view class="bills-detail-page">
    <uni-nav-bar left-icon="back" title="账单详情" @clickLeft="goBack" />
    
    <view v-if="loading" class="loading-container">
      <uni-load-more status="loading" />
    </view>
    
    <template v-else-if="transaction">
      <!-- 金额显示 -->
      <view class="amount-section">
        <view class="amount-type" :class="transaction.type === 1 ? 'income' : 'expense'">
          {{ transaction.type === 1 ? '收入' : '支出' }}
        </view>
        <view class="amount-value" :class="transaction.type === 1 ? 'income' : 'expense'">
          <text class="currency">{{ currentCurrency }}</text>
          <text class="amount">{{ formatAmount(transaction.amount) }}</text>
        </view>
      </view>

      <!-- 详情卡片 -->
      <view class="detail-card">
        <!-- 分类 -->
        <view class="detail-row">
          <view class="row-left">
            <text class="row-icon">📂</text>
            <text class="row-label">分类</text>
          </view>
          <view class="row-right">
            <text class="category-badge">{{ transaction.categoryName || '未分类' }}</text>
          </view>
        </view>

        <!-- 账户 -->
        <view class="detail-row">
          <view class="row-left">
            <text class="row-icon">💳</text>
            <text class="row-label">账户</text>
          </view>
          <view class="row-right">
            <text>{{ transaction.accountName || '未知账户' }}</text>
          </view>
        </view>

        <!-- 时间 -->
        <view class="detail-row">
          <view class="row-left">
            <text class="row-icon">📅</text>
            <text class="row-label">时间</text>
          </view>
          <view class="row-right">
            <text>{{ formatDateTime(transaction.transactionTime) }}</text>
          </view>
        </view>

        <!-- 账本 -->
        <view class="detail-row">
          <view class="row-left">
            <text class="row-icon">📒</text>
            <text class="row-label">账本</text>
          </view>
          <view class="row-right">
            <text>{{ transaction.bookName || '默认账本' }}</text>
          </view>
        </view>

        <!-- 备注 -->
        <view v-if="transaction.remark" class="detail-row remark-row">
          <view class="row-left">
            <text class="row-icon">📝</text>
            <text class="row-label">备注</text>
          </view>
          <view class="row-right remark-content">
            <text>{{ transaction.remark }}</text>
          </view>
        </view>
      </view>

      <!-- 操作按钮 -->
      <view class="action-buttons">
        <view class="action-btn edit" @click="goToEdit">
          <text class="icon">✏️</text>
          <text class="label">编辑</text>
        </view>
        <view class="action-btn delete" @click="confirmDelete">
          <text class="icon">🗑️</text>
          <text class="label">删除</text>
        </view>
      </view>
    </template>
    
    <view v-else class="error-state">
      <text>交易记录不存在</text>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { useTransactionStore } from '@/stores/transaction'

const transactionStore = useTransactionStore()

const transactionId = ref<number>(0)
const loading = ref(true)
const transaction = ref<any>(null)

const currentCurrency = '¥'

onLoad((options: any) => {
  if (options?.id) {
    transactionId.value = parseInt(options.id)
  }
})

onMounted(async () => {
  await loadTransaction()
})

async function loadTransaction() {
  loading.value = true
  try {
    const res = await uni.request({
      url: `/api/mobile/transactions/${transactionId.value}`
    })
    if (res.data?.data) {
      transaction.value = res.data.data
    }
  } catch (error) {
    // ignore
  } finally {
    loading.value = false
  }
}

function formatAmount(amount: any): string {
  if (!amount && amount !== 0) return '0.00'
  const num = typeof amount === 'string' ? parseFloat(amount) : amount
  return num.toFixed(2).replace(/\B(?=(\d{3})+(?!\d))/g, ',')
}

function formatDateTime(dateStr: string): string {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  const year = date.getFullYear()
  const month = (date.getMonth() + 1).toString().padStart(2, '0')
  const day = date.getDate().toString().padStart(2, '0')
  const hours = date.getHours().toString().padStart(2, '0')
  const minutes = date.getMinutes().toString().padStart(2, '0')
  return `${year}-${month}-${day} ${hours}:${minutes}`
}

function goBack() {
  uni.navigateBack()
}

function goToEdit() {
  uni.navigateTo({
    url: `/pages/transactions/edit?id=${transactionId.value}`
  })
}

function confirmDelete() {
  uni.showModal({
    title: '确认删除',
    content: '确定要删除这条交易记录吗？删除后无法恢复。',
    confirmColor: '#EF476F',
    success: async (res) => {
      if (res.confirm) {
        await deleteTransaction()
      }
    }
  })
}

async function deleteTransaction() {
  try {
    await transactionStore.deleteTransaction(transactionId.value)
    uni.showToast({ title: '删除成功', icon: 'success' })
    setTimeout(() => {
      uni.navigateBack()
    }, 1500)
  } catch (error: any) {
    uni.showToast({ title: error.message || '删除失败', icon: 'none' })
  }
}
</script>

<style scoped lang="scss">
.bills-detail-page {
  min-height: 100vh;
  background-color: #F8F9FA;
}

.loading-container {
  padding: 200rpx 0;
}

.amount-section {
  background: #fff;
  padding: 48rpx 32rpx;
  text-align: center;
  margin-bottom: 24rpx;
}

.amount-type {
  font-size: 28rpx;
  font-weight: 500;
  margin-bottom: 16rpx;
  
  &.income {
    color: #06D6A0;
  }
  
  &.expense {
    color: #EF476F;
  }
}

.amount-value {
  display: flex;
  align-items: baseline;
  justify-content: center;
  
  .currency {
    font-size: 36rpx;
    font-weight: 600;
    margin-right: 4rpx;
  }
  
  .amount {
    font-size: 64rpx;
    font-weight: 700;
    letter-spacing: -2rpx;
  }
  
  &.income {
    color: #06D6A0;
  }
  
  &.expense {
    color: #EF476F;
  }
}

.detail-card {
  background: #fff;
  margin: 0 24rpx;
  border-radius: 16rpx;
  padding: 8rpx 0;
}

.detail-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 28rpx 32rpx;
  border-bottom: 1rpx solid #F3F4F6;
  
  &:last-child {
    border-bottom: none;
  }
}

.row-left {
  display: flex;
  align-items: center;
  gap: 12rpx;
  
  .row-icon {
    font-size: 32rpx;
  }
  
  .row-label {
    font-size: 28rpx;
    color: #666;
  }
}

.row-right {
  font-size: 28rpx;
  color: #1F2937;
}

.category-badge {
  background: rgba(15, 76, 92, 0.08);
  color: #0F4C5C;
  padding: 8rpx 16rpx;
  border-radius: 8rpx;
  font-weight: 500;
}

.remark-row {
  align-items: flex-start;
}

.remark-content {
  flex: 1;
  text-align: right;
  color: #666;
  line-height: 1.5;
}

.action-buttons {
  display: flex;
  gap: 24rpx;
  padding: 32rpx 24rpx;
}

.action-btn {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8rpx;
  padding: 28rpx;
  background: #fff;
  border-radius: 16rpx;
  
  .icon {
    font-size: 32rpx;
  }
  
  .label {
    font-size: 28rpx;
    font-weight: 500;
  }
  
  &.edit {
    color: #0F4C5C;
  }
  
  &.delete {
    color: #EF476F;
  }
}

.error-state {
  padding: 200rpx 0;
  text-align: center;
  color: #9CA3AF;
  font-size: 28rpx;
}
</style>
