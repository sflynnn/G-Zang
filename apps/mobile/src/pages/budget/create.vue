<template>
  <view class="create-budget-page">
    <uni-nav-bar left-icon="back" title="创建预算" @clickLeft="goBack" />
    
    <view class="form-container">
      <!-- 周期选择 -->
      <view class="form-section">
        <view class="section-title">预算周期</view>
        <view class="period-grid">
          <view 
            v-for="option in periodOptions" 
            :key="option.value"
            :class="['period-cell', { active: formData.period === option.value }]"
            @click="formData.period = option.value"
          >
            <view class="period-icon">{{ option.icon }}</view>
            <view class="period-label">{{ option.label }}</view>
          </view>
        </view>
      </view>

      <!-- 分类选择 -->
      <view class="form-section">
        <view class="section-title">预算分类</view>
        <view class="category-grid">
          <view 
            v-for="cat in categories" 
            :key="cat.id"
            :class="['category-cell', { active: formData.categoryId === cat.id }]"
            @click="formData.categoryId = cat.id"
          >
            <view class="category-icon" :style="{ background: cat.iconBg }">
              {{ cat.icon }}
            </view>
            <view class="category-name">{{ cat.name }}</view>
          </view>
        </view>
      </view>

      <!-- 预算金额 -->
      <view class="form-section">
        <view class="section-title">预算金额</view>
        <view class="amount-input-wrapper">
          <text class="currency-symbol">{{ currentCurrency }}</text>
          <input 
            class="amount-input" 
            v-model="formData.amount" 
            type="digit"
            placeholder="0.00"
            @blur="formatAmount"
          />
        </view>
        
        <!-- 快捷金额 -->
        <view class="quick-amounts">
          <view 
            v-for="amt in quickAmounts" 
            :key="amt"
            class="quick-btn"
            @click="formData.amount = amt.toString()"
          >
            {{ currentCurrency }}{{ amt }}
          </view>
        </view>
      </view>

      <!-- 预警设置 -->
      <view class="form-section">
        <view class="section-title">预警设置</view>
        <view class="warning-row">
          <text class="warning-label">预警阈值</text>
          <view class="warning-input-wrapper">
            <input 
              v-model="formData.warningPercent" 
              type="number"
              class="warning-input"
              @blur="clampWarning"
            />
            <text class="warning-suffix">%</text>
          </view>
        </view>
        <view class="warning-hint">
          当预算使用超过此比例时发送提醒
        </view>
      </view>
    </view>

    <!-- 提交按钮 -->
    <view class="submit-bar">
      <button class="submit-btn" @click="handleSubmit" :loading="submitting">
        {{ submitting ? '创建中...' : '创建预算' }}
      </button>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useBookStore } from '@/stores/book'

const bookStore = useBookStore()

const submitting = ref(false)

const periodOptions = [
  { value: 'week', label: '周', icon: '📅' },
  { value: 'month', label: '月', icon: '📆' },
  { value: 'year', label: '年', icon: '📇' }
]

const categories = [
  { id: 1, name: '餐饮', icon: '🍜', iconBg: '#FFF3E0' },
  { id: 2, name: '交通', icon: '🚇', iconBg: '#E3F2FD' },
  { id: 3, name: '购物', icon: '🛒', iconBg: '#FCE4EC' },
  { id: 4, name: '娱乐', icon: '🎮', iconBg: '#F3E5F5' },
  { id: 5, name: '医疗', icon: '💊', iconBg: '#FFEBEE' },
  { id: 6, name: '教育', icon: '📚', iconBg: '#E8F5E9' },
]

const quickAmounts = [500, 1000, 2000, 3000, 5000]

const formData = ref({
  period: 'month',
  categoryId: null as number | null,
  amount: '',
  warningPercent: 80
})

const currentCurrency = '¥'

function goBack() {
  uni.navigateBack()
}

function formatAmount() {
  if (formData.value.amount) {
    const num = parseFloat(formData.value.amount)
    if (!isNaN(num)) {
      formData.value.amount = num.toFixed(2)
    }
  }
}

function clampWarning() {
  let val = parseInt(formData.value.warningPercent)
  if (isNaN(val)) val = 80
  if (val < 0) val = 0
  if (val > 100) val = 100
  formData.value.warningPercent = val
}

async function handleSubmit() {
  if (!formData.value.categoryId) {
    uni.showToast({ title: '请选择分类', icon: 'none' })
    return
  }
  
  if (!formData.value.amount) {
    uni.showToast({ title: '请输入预算金额', icon: 'none' })
    return
  }

  submitting.value = true
  try {
    // 调用预算 API
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
.create-budget-page {
  min-height: 100vh;
  background-color: #F8F9FA;
  padding-bottom: 140rpx;
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

.period-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16rpx;
}

.period-cell {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8rpx;
  padding: 24rpx 8rpx;
  background: #F8F9FA;
  border-radius: 12rpx;
  border: 4rpx solid transparent;
  transition: all 0.2s;
  
  &.active {
    border-color: #0F4C5C;
    background: rgba(15, 76, 92, 0.08);
  }
  
  .period-icon {
    font-size: 36rpx;
  }
  
  .period-label {
    font-size: 24rpx;
    color: #666;
  }
  
  &.active .period-label {
    color: #0F4C5C;
    font-weight: 600;
  }
}

.category-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16rpx;
}

.category-cell {
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
  
  .category-icon {
    width: 64rpx;
    height: 64rpx;
    border-radius: 16rpx;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 32rpx;
  }
  
  .category-name {
    font-size: 22rpx;
    color: #666;
  }
  
  &.active .category-name {
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
  font-size: 36rpx;
  font-weight: 700;
  color: #0F4C5C;
  margin-right: 8rpx;
}

.amount-input {
  flex: 1;
  padding: 24rpx 0;
  font-size: 40rpx;
  font-weight: 600;
  color: #333;
}

.quick-amounts {
  display: flex;
  gap: 12rpx;
  margin-top: 20rpx;
  flex-wrap: wrap;
}

.quick-btn {
  padding: 12rpx 24rpx;
  background: #F8F9FA;
  border-radius: 20rpx;
  font-size: 26rpx;
  color: #666;
  
  &:active {
    background: #E5E5E5;
  }
}

.warning-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.warning-label {
  font-size: 28rpx;
  color: #1F2937;
}

.warning-input-wrapper {
  display: flex;
  align-items: center;
  background: #F8F9FA;
  border-radius: 8rpx;
  padding: 0 16rpx;
}

.warning-input {
  width: 100rpx;
  padding: 12rpx 0;
  font-size: 28rpx;
  text-align: right;
}

.warning-suffix {
  font-size: 28rpx;
  color: #666;
  margin-left: 4rpx;
}

.warning-hint {
  font-size: 24rpx;
  color: #9CA3AF;
  margin-top: 12rpx;
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
