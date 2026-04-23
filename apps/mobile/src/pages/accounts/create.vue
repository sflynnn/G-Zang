<template>
  <view class="create-account-page">
    <uni-nav-bar left-icon="back" title="创建账户" @clickLeft="goBack" />
    
    <view class="form-container">
      <!-- 账户图标选择 -->
      <view class="form-section">
        <view class="section-title">选择图标</view>
        <view class="icon-grid">
          <view 
            v-for="icon in accountIcons" 
            :key="icon"
            :class="['icon-cell', { active: selectedIcon === icon }]"
            @click="selectedIcon = icon"
          >
            {{ icon }}
          </view>
        </view>
      </view>

      <!-- 账户名称 -->
      <view class="form-section">
        <view class="section-title">账户名称</view>
        <input 
          class="text-input" 
          v-model="formData.accountName" 
          placeholder="请输入账户名称"
          maxlength="20"
        />
      </view>

      <!-- 账户类型 -->
      <view class="form-section">
        <view class="section-title">账户类型</view>
        <view class="type-grid">
          <view 
            v-for="type in accountTypes" 
            :key="type.value"
            :class="['type-cell', { active: formData.accountType === type.value }]"
            @click="formData.accountType = type.value"
          >
            <view class="type-icon">{{ type.icon }}</view>
            <view class="type-name">{{ type.label }}</view>
          </view>
        </view>
      </view>

      <!-- 初始余额 -->
      <view class="form-section">
        <view class="section-title">初始余额</view>
        <view class="amount-input-wrapper">
          <text class="currency-symbol">{{ currentCurrency }}</text>
          <input 
            class="amount-input" 
            v-model="formData.balance" 
            type="digit"
            placeholder="0.00"
            @blur="formatBalance"
          />
        </view>
      </view>

      <!-- 备注 -->
      <view class="form-section">
        <view class="section-title">备注（可选）</view>
        <textarea 
          class="remark-input" 
          v-model="formData.remark" 
          placeholder="添加备注信息"
          maxlength="200"
          rows="3"
        />
      </view>
    </view>

    <!-- 提交按钮 -->
    <view class="submit-bar">
      <button class="submit-btn" @click="handleSubmit" :loading="submitting">
        {{ submitting ? '创建中...' : '创建账户' }}
      </button>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { useAccountStore } from '@/stores/account'
import { useBookStore } from '@/stores/book'

const accountStore = useAccountStore()
const bookStore = useBookStore()

const submitting = ref(false)
const selectedIcon = ref('💰')

const accountIcons = ['💰', '🏦', '💳', '💵', '📱', '🎯', '💼', '🏠', '🚗', '✈️', '🛒', '📈']

const accountTypes = [
  { value: 1, label: '银行卡', icon: '🏦' },
  { value: 2, label: '电子钱包', icon: '📱' },
  { value: 3, label: '现金', icon: '💵' },
  { value: 4, label: '信用卡', icon: '💳' },
  { value: 5, label: '投资账户', icon: '📈' },
]

const formData = ref({
  accountName: '',
  accountType: 1,
  balance: '',
  remark: ''
})

const currentCurrency = computed(() => bookStore.currentCurrency || '¥')

function goBack() {
  uni.navigateBack()
}

function formatBalance() {
  if (formData.value.balance) {
    const num = parseFloat(formData.value.balance)
    if (!isNaN(num)) {
      formData.value.balance = num.toFixed(2)
    }
  }
}

async function handleSubmit() {
  if (!formData.value.accountName.trim()) {
    uni.showToast({ title: '请输入账户名称', icon: 'none' })
    return
  }

  submitting.value = true
  try {
    await accountStore.createAccount({
      name: formData.value.accountName,
      type: formData.value.accountType,
      initialBalance: formData.value.balance ? parseFloat(formData.value.balance) : 0
    })
    
    uni.showToast({ title: '创建成功', icon: 'success' })
    setTimeout(() => {
      goBack()
    }, 1500)
  } catch (error: any) {
    uni.showToast({ title: error.message || '创建失败', icon: 'none' })
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped lang="scss">
.create-account-page {
  min-height: 100vh;
  background-color: #F8F9FA;
  padding-bottom: 120rpx;
}

.form-container {
  padding: 24rpx;
}

.form-section {
  background: #fff;
  border-radius: 16rpx;
  padding: 24rpx;
  margin-bottom: 24rpx;
}

.section-title {
  font-size: 28rpx;
  font-weight: 600;
  color: #1F2937;
  margin-bottom: 20rpx;
}

.icon-grid {
  display: grid;
  grid-template-columns: repeat(6, 1fr);
  gap: 16rpx;
}

.icon-cell {
  aspect-ratio: 1;
  background: #F8F9FA;
  border-radius: 16rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 40rpx;
  border: 4rpx solid transparent;
  transition: all 0.2s;
  
  &.active {
    border-color: #0F4C5C;
    background: rgba(15, 76, 92, 0.08);
  }
}

.text-input {
  width: 100%;
  padding: 24rpx 28rpx;
  background: #F8F9FA;
  border-radius: 12rpx;
  font-size: 28rpx;
  color: #333;
  border: 2rpx solid transparent;
  
  &:focus {
    border-color: #0F4C5C;
  }
}

.type-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16rpx;
}

.type-cell {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8rpx;
  padding: 20rpx 8rpx;
  background: #F8F9FA;
  border-radius: 12rpx;
  border: 4rpx solid transparent;
  transition: all 0.2s;
  
  &.active {
    border-color: #0F4C5C;
    background: rgba(15, 76, 92, 0.08);
  }
  
  .type-icon {
    font-size: 36rpx;
  }
  
  .type-name {
    font-size: 22rpx;
    color: #666;
  }
  
  &.active .type-name {
    color: #0F4C5C;
    font-weight: 600;
  }
}

.amount-input-wrapper {
  display: flex;
  align-items: center;
  background: #F8F9FA;
  border-radius: 12rpx;
  padding: 0 24rpx;
  border: 2rpx solid transparent;
  
  &:focus-within {
    border-color: #0F4C5C;
  }
}

.currency-symbol {
  font-size: 32rpx;
  font-weight: 700;
  color: #0F4C5C;
  margin-right: 8rpx;
}

.amount-input {
  flex: 1;
  padding: 24rpx 0;
  font-size: 32rpx;
  font-weight: 600;
  color: #333;
}

.remark-input {
  width: 100%;
  padding: 24rpx;
  background: #F8F9FA;
  border-radius: 12rpx;
  font-size: 28rpx;
  color: #333;
  border: 2rpx solid transparent;
  resize: none;
  
  &:focus {
    border-color: #0F4C5C;
  }
}

.submit-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 32rpx;
  background: #fff;
  border-top: 1rpx solid #E5E5E5;
}

.submit-btn {
  width: 100%;
  height: 96rpx;
  background: #0F4C5C;
  border-radius: 24rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 32rpx;
  font-weight: 600;
  border: none;
  
  &[loading] {
    opacity: 0.7;
  }
}
</style>
