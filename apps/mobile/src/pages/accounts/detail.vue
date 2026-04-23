<template>
  <view class="account-detail-page">
    <uni-nav-bar left-icon="back" title="账户详情" @clickLeft="goBack" />
    
    <view v-if="loading" class="loading-container">
      <uni-load-more status="loading" />
    </view>
    
    <template v-else-if="account">
      <!-- 账户卡片 -->
      <view class="account-card" :style="{ background: gradientBackground }">
        <view class="card-header">
          <view class="account-icon">{{ account.icon || '💰' }}</view>
          <view class="account-name">{{ account.accountName }}</view>
        </view>
        
        <view class="balance-section">
          <view class="balance-label">账户余额</view>
          <view class="balance-amount">
            <text class="currency">{{ currentCurrency }}</text>
            <text class="amount">{{ formatAmount(account.balance) }}</text>
          </view>
        </view>
        
        <view class="card-footer">
          <view class="account-type-badge">
            {{ getTypeLabel(account.accountType) }}
          </view>
        </view>
      </view>

      <!-- 操作按钮 -->
      <view class="action-buttons">
        <view class="action-btn edit" @click="goToEdit">
          <text class="icon">✏️</text>
          <text class="label">编辑账户</text>
        </view>
        <view class="action-btn delete" @click="confirmDelete">
          <text class="icon">🗑️</text>
          <text class="label">删除账户</text>
        </view>
      </view>

      <!-- 最近交易 -->
      <view class="recent-transactions">
        <view class="section-header">
          <text class="section-title">最近交易</text>
          <text class="see-more" @click="goToTransactions">查看全部 ›</text>
        </view>
        
        <view v-if="recentTransactions.length > 0" class="transaction-list">
          <view 
            v-for="item in recentTransactions" 
            :key="item.id"
            class="transaction-item"
            @click="goToTransactionDetail(item.id)"
          >
            <view class="item-left">
              <view class="item-icon" :style="{ background: item.iconBg }">
                {{ item.categoryIcon || '💰' }}
              </view>
              <view class="item-info">
                <view class="item-name">{{ item.categoryName }}</view>
                <view class="item-date">{{ formatDate(item.transactionTime) }}</view>
              </view>
            </view>
            <view 
              class="item-amount"
              :class="item.type === 1 ? 'income' : 'expense'"
            >
              {{ item.type === 1 ? '+' : '-' }}{{ currentCurrency }}{{ formatAmount(item.amount) }}
            </view>
          </view>
        </view>
        
        <view v-else class="empty-state">
          <text>暂无交易记录</text>
        </view>
      </view>
    </template>
    
    <view v-else class="error-state">
      <text>账户不存在</text>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { useAccountStore } from '@/stores/account'

const accountStore = useAccountStore()

const accountId = ref<number>(0)
const loading = ref(true)
const account = ref<any>(null)
const recentTransactions = ref<any[]>([])

const gradientBackground = 'linear-gradient(135deg, #0F4C5C 0%, #1a6b7a 100%)'

const currentCurrency = '¥'

onLoad((options: any) => {
  if (options?.id) {
    accountId.value = parseInt(options.id)
  }
})

onMounted(async () => {
  await loadAccount()
})

async function loadAccount() {
  loading.value = true
  try {
    account.value = await accountStore.getAccountById(accountId.value)
    // 加载最近交易记录
  } catch (error) {
    // ignore
  } finally {
    loading.value = false
  }
}

function getTypeLabel(type: number): string {
  const types: Record<number, string> = {
    1: '银行卡',
    2: '电子钱包',
    3: '现金',
    4: '信用卡',
    5: '投资账户'
  }
  return types[type] || '其他'
}

function formatAmount(amount: any): string {
  if (!amount && amount !== 0) return '0.00'
  const num = typeof amount === 'string' ? parseFloat(amount) : amount
  return num.toFixed(2).replace(/\B(?=(\d{3})+(?!\d))/g, ',')
}

function formatDate(dateStr: string): string {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return `${date.getMonth() + 1}月${date.getDate()}日 ${date.getHours().toString().padStart(2, '0')}:${date.getMinutes().toString().padStart(2, '0')}`
}

function goBack() {
  uni.navigateBack()
}

function goToEdit() {
  uni.navigateTo({
    url: `/pages/accounts/edit?id=${accountId.value}`
  })
}

function goToTransactions() {
  uni.navigateTo({
    url: `/pages/transactions/index?accountId=${accountId.value}`
  })
}

function goToTransactionDetail(id: number) {
  uni.navigateTo({
    url: `/pages/transactions/detail?id=${id}`
  })
}

function confirmDelete() {
  uni.showModal({
    title: '确认删除',
    content: `确定要删除账户"${account.value?.accountName}"吗？删除后无法恢复。`,
    confirmColor: '#EF476F',
    success: async (res) => {
      if (res.confirm) {
        await deleteAccount()
      }
    }
  })
}

async function deleteAccount() {
  try {
    await accountStore.deleteAccount(accountId.value)
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
.account-detail-page {
  min-height: 100vh;
  background-color: #F8F9FA;
}

.loading-container {
  padding: 200rpx 0;
}

.account-card {
  margin: 24rpx;
  border-radius: 24rpx;
  padding: 32rpx;
  color: #fff;
}

.card-header {
  display: flex;
  align-items: center;
  margin-bottom: 32rpx;
}

.account-icon {
  width: 80rpx;
  height: 80rpx;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 20rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 40rpx;
  margin-right: 16rpx;
}

.account-name {
  font-size: 36rpx;
  font-weight: 600;
}

.balance-section {
  margin-bottom: 24rpx;
}

.balance-label {
  font-size: 24rpx;
  opacity: 0.8;
  margin-bottom: 8rpx;
}

.balance-amount {
  display: flex;
  align-items: baseline;
}

.currency {
  font-size: 32rpx;
  font-weight: 600;
  margin-right: 4rpx;
}

.amount {
  font-size: 56rpx;
  font-weight: 700;
  letter-spacing: -2rpx;
}

.card-footer {
  display: flex;
  justify-content: flex-end;
}

.account-type-badge {
  background: rgba(255, 255, 255, 0.2);
  padding: 8rpx 20rpx;
  border-radius: 20rpx;
  font-size: 24rpx;
}

.action-buttons {
  display: flex;
  gap: 24rpx;
  padding: 0 24rpx;
  margin-bottom: 32rpx;
}

.action-btn {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8rpx;
  padding: 24rpx;
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

.recent-transactions {
  background: #fff;
  margin: 0 24rpx;
  border-radius: 16rpx;
  padding: 24rpx;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20rpx;
}

.section-title {
  font-size: 32rpx;
  font-weight: 600;
  color: #1F2937;
}

.see-more {
  font-size: 26rpx;
  color: #9CA3AF;
}

.transaction-list {
  .transaction-item {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 20rpx 0;
    border-bottom: 1rpx solid #F3F4F6;
    
    &:last-child {
      border-bottom: none;
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
    border-radius: 16rpx;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 32rpx;
  }
  
  .item-info {
    .item-name {
      font-size: 28rpx;
      font-weight: 500;
      color: #1F2937;
    }
    
    .item-date {
      font-size: 24rpx;
      color: #9CA3AF;
      margin-top: 4rpx;
    }
  }
  
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
}

.empty-state {
  text-align: center;
  padding: 60rpx 0;
  color: #9CA3AF;
  font-size: 28rpx;
}

.error-state {
  padding: 200rpx 0;
  text-align: center;
  color: #9CA3AF;
  font-size: 28rpx;
}
</style>
