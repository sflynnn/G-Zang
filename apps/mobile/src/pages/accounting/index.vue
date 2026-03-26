<template>
  <view class="accounting-page">
    <!-- 顶部导航 -->
    <view class="nav-bar">
      <view class="nav-left" @click="goBack">
        <uni-icons type="left" size="20" color="#333"></uni-icons>
      </view>
      <view class="nav-title">记账</view>
      <view class="nav-right">
        <text class="nav-action" @click="saveAsTemplate">保存模板</text>
      </view>
    </view>

    <!-- 主要内容 -->
    <scroll-view class="main-content" scroll-y="true">
      <!-- 记账类型切换 -->
      <view class="type-selector">
        <view class="type-tabs">
          <view
            v-for="type in transactionTypes"
            :key="type.key"
            class="type-tab"
            :class="{ active: form.type === type.key }"
            @click="switchType(type.key)"
          >
            <text class="type-text">{{ type.label }}</text>
          </view>
        </view>
      </view>

      <!-- 记账表单 -->
      <view class="form-container">
        <!-- 金额输入 -->
        <view class="form-item">
          <view class="amount-input">
            <text class="currency">¥</text>
            <input
              v-model="form.amount"
              type="digit"
              placeholder="0.00"
              class="amount-field"
              @blur="validateField('amount')"
            />
          </view>
          <text v-if="errors.amount" class="error-text">{{ errors.amount }}</text>
        </view>

        <!-- 分类选择 -->
        <view class="form-item">
          <view class="field-item" @click="showCategoryPicker = true">
            <view class="field-label">
              <uni-icons type="bars" size="18" color="#666"></uni-icons>
              <text class="label-text">分类</text>
            </view>
            <view class="field-value">
              <text class="value-text" :class="{ placeholder: !selectedCategory }">
                {{ selectedCategory || '请选择分类' }}
              </text>
              <uni-icons type="right" size="16" color="#ccc"></uni-icons>
            </view>
          </view>
          <text v-if="errors.categoryId" class="error-text">{{ errors.categoryId }}</text>
        </view>

        <!-- 账户选择 -->
        <view class="form-item">
          <view class="field-item" @click="showAccountPicker = true">
            <view class="field-label">
              <uni-icons type="wallet" size="18" color="#666"></uni-icons>
              <text class="label-text">账户</text>
            </view>
            <view class="field-value">
              <text class="value-text" :class="{ placeholder: !selectedAccount }">
                {{ selectedAccount || '请选择账户' }}
              </text>
              <uni-icons type="right" size="16" color="#ccc"></uni-icons>
            </view>
          </view>
          <text v-if="errors.accountId" class="error-text">{{ errors.accountId }}</text>
        </view>

        <!-- 转账目标账户（仅转账时显示） -->
        <view v-if="form.type === 3" class="form-item">
          <view class="field-item" @click="showTargetAccountPicker = true">
            <view class="field-label">
              <uni-icons type="exchange" size="18" color="#666"></uni-icons>
              <text class="label-text">转入账户</text>
            </view>
            <view class="field-value">
              <text class="value-text" :class="{ placeholder: !selectedTargetAccount }">
                {{ selectedTargetAccount || '请选择转入账户' }}
              </text>
              <uni-icons type="right" size="16" color="#ccc"></uni-icons>
            </view>
          </view>
          <text v-if="errors.targetAccountId" class="error-text">{{ errors.targetAccountId }}</text>
        </view>

        <!-- 交易时间 -->
        <view class="form-item">
          <view class="field-item" @click="showTimePicker = true">
            <view class="field-label">
              <uni-icons type="clock" size="18" color="#666"></uni-icons>
              <text class="label-text">时间</text>
            </view>
            <view class="field-value">
              <text class="value-text">{{ form.transactionTime }}</text>
              <uni-icons type="right" size="16" color="#ccc"></uni-icons>
            </view>
          </view>
        </view>

        <!-- 备注 -->
        <view class="form-item">
          <textarea
            v-model="form.remark"
            placeholder="添加备注（可选）"
            class="remark-field"
            :maxlength="200"
            auto-height
          />
          <view class="remark-count">
            <text class="count-text">{{ form.remark.length }}/200</text>
          </view>
        </view>

        <!-- 常用金额快捷选择 -->
        <view class="quick-amounts">
          <text class="section-label">常用金额</text>
          <view class="amount-grid">
            <view
              v-for="amount in quickAmounts"
              :key="amount"
              class="amount-btn"
              @click="setAmount(amount)"
            >
              <text class="amount-text">¥{{ amount }}</text>
            </view>
          </view>
        </view>

        <!-- 最近使用的分类 -->
        <view v-if="recentCategories.length > 0" class="recent-categories">
          <text class="section-label">最近分类</text>
          <view class="category-grid">
            <view
              v-for="category in recentCategories"
              :key="category.id"
              class="category-item"
              @click="selectCategory(category)"
            >
              <view class="category-icon">
                <uni-icons :type="getCategoryIcon(category.id)" size="20" color="#666"></uni-icons>
              </view>
              <text class="category-name">{{ category.name }}</text>
            </view>
          </view>
        </view>

        <!-- 智能推荐（基于历史记录） -->
        <view v-if="smartSuggestions.length > 0" class="smart-suggestions">
          <text class="section-label">智能推荐</text>
          <view class="suggestion-list">
            <view
              v-for="suggestion in smartSuggestions"
              :key="suggestion.id"
              class="suggestion-item"
              @click="applySuggestion(suggestion)"
            >
              <view class="suggestion-icon">
                <uni-icons :type="getCategoryIcon(suggestion.categoryId)" size="18" color="#666"></uni-icons>
              </view>
              <view class="suggestion-content">
                <text class="suggestion-title">{{ suggestion.categoryName }}</text>
                <text class="suggestion-amount">¥{{ suggestion.amount }}</text>
              </view>
              <uni-icons type="right" size="14" color="#ccc"></uni-icons>
            </view>
          </view>
        </view>
      </view>

      <!-- 底部操作栏 -->
      <view class="action-bar">
        <view class="action-buttons">
          <button
            class="action-btn secondary"
            @click="resetForm"
          >
            重置
          </button>
          <button
            class="action-btn primary"
            :class="{ loading }"
            :disabled="!isFormValid || loading"
            @click="handleSubmit"
          >
            <text v-if="loading">保存中...</text>
            <text v-else>确认记账</text>
          </button>
        </view>
      </view>

      <!-- 底部安全区域 -->
      <view class="bottom-safe-area" :style="{ height: safeAreaBottom + 'px' }"></view>
    </scroll-view>

    <!-- 分类选择弹窗 -->
    <uni-popup ref="categoryPopup" type="bottom" :is-mask-click="true">
      <view class="picker-container">
        <view class="picker-header">
          <text class="picker-cancel" @click="hideCategoryPicker">取消</text>
          <text class="picker-title">选择分类</text>
          <text class="picker-confirm" @click="confirmCategory">确定</text>
        </view>
        <picker-view
          :value="categoryPickerValue"
          @change="onCategoryChange"
          class="category-picker"
        >
          <picker-view-column>
            <view
              v-for="category in availableCategories"
              :key="category.id"
              class="picker-item"
            >
              {{ category.name }}
            </view>
          </picker-view-column>
        </picker-view>
      </view>
    </uni-popup>

    <!-- 账户选择弹窗 -->
    <uni-popup ref="accountPopup" type="bottom" :is-mask-click="true">
      <view class="picker-container">
        <view class="picker-header">
          <text class="picker-cancel" @click="hideAccountPicker">取消</text>
          <text class="picker-title">选择账户</text>
          <text class="picker-confirm" @click="confirmAccount">确定</text>
        </view>
        <picker-view
          :value="accountPickerValue"
          @change="onAccountChange"
          class="account-picker"
        >
          <picker-view-column>
            <view
              v-for="account in accounts"
              :key="account.id"
              class="picker-item"
            >
              {{ account.name }}
            </view>
          </picker-view-column>
        </picker-view>
      </view>
    </uni-popup>

    <!-- 时间选择弹窗 -->
    <uni-popup ref="timePopup" type="bottom" :is-mask-click="true">
      <view class="datetime-picker">
        <view class="picker-header">
          <text class="picker-cancel" @click="hideTimePicker">取消</text>
          <text class="picker-title">选择时间</text>
          <text class="picker-confirm" @click="confirmTime">确定</text>
        </view>
        <picker
          mode="datetime"
          :value="currentTime"
          :start="minTime"
          :end="maxTime"
          @change="onTimeChange"
          class="time-picker"
        >
          <view class="picker-placeholder">请选择时间</view>
        </picker>
      </view>
    </uni-popup>
  </view>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted, watch } from 'vue'
import { useAccountingStore } from '@/stores/accounting'
import { useAppStore } from '@/stores/app'
import { formatAmount } from '@/utils/format'
import { validateTransactionForm } from '@/utils/validate'
import { DeviceUtils } from '@/utils/device'
import { MoneyCalculator } from '@/utils/math'

// 状态管理
const accountingStore = useAccountingStore()
const appStore = useAppStore()

// 响应式数据
const loading = ref(false)
const safeAreaBottom = ref(0)

// 表单数据
const form = reactive({
  type: 2, // 默认支出
  amount: '',
  categoryId: 0,
  accountId: 0,
  targetAccountId: 0,
  transactionTime: '',
  remark: ''
})

// 错误信息
const errors = reactive({
  amount: '',
  categoryId: '',
  accountId: '',
  targetAccountId: ''
})

// 弹窗状态
const showCategoryPicker = ref(false)
const showAccountPicker = ref(false)
const showTargetAccountPicker = ref(false)
const showTimePicker = ref(false)

// 选择器值
const categoryPickerValue = ref([0])
const accountPickerValue = ref([0])
const currentTime = ref('')

// 交易类型
const transactionTypes = [
  { key: 1, label: '收入' },
  { key: 2, label: '支出' },
  { key: 3, label: '转账' }
]

// 常用金额
const quickAmounts = [10, 20, 50, 100, 200, 500]

// 计算属性
const isFormValid = computed(() => {
  const validation = validateTransactionForm({
    type: form.type,
    amount: form.amount,
    categoryId: form.categoryId,
    accountId: form.accountId,
    transactionTime: form.transactionTime
  })

  // 转账时需要目标账户
  if (form.type === 3 && !form.targetAccountId) {
    return false
  }

  return validation.isValid
})

const selectedCategory = computed(() => {
  const category = availableCategories.value.find(c => c.id === form.categoryId)
  return category?.name || ''
})

const selectedAccount = computed(() => {
  const account = accountingStore.accounts.find(a => a.id === form.accountId)
  return account?.name || ''
})

const selectedTargetAccount = computed(() => {
  const account = accountingStore.accounts.find(a => a.id === form.targetAccountId)
  return account?.name || ''
})

const availableCategories = computed(() => {
  if (form.type === 1) {
    return accountingStore.incomeCategories
  } else if (form.type === 2) {
    return accountingStore.expenseCategories
  }
  return []
})

const recentCategories = computed(() => accountingStore.recentCategories.slice(0, 6))

// 智能推荐（模拟数据）
const smartSuggestions = ref([
  { id: 1, categoryId: 101, categoryName: '早餐', amount: 15 },
  { id: 2, categoryId: 102, categoryName: '地铁', amount: 3 },
  { id: 3, categoryId: 103, categoryName: '购物', amount: 120 }
])

// 账户数据（从store获取）
const accounts = computed(() => accountingStore.accounts)

// 获取分类图标
const getCategoryIcon = (categoryId: number) => {
  const iconMap: Record<number, string> = {
    101: 'restaurant', // 餐饮
    102: 'location-o', // 交通
    103: 'shop-o'      // 购物
  }
  return iconMap[categoryId] || 'circle'
}

// 切换交易类型
const switchType = (type: number) => {
  form.type = type
  // 重置分类选择
  form.categoryId = 0
  form.targetAccountId = 0
  validateForm()
}

// 设置金额
const setAmount = (amount: number) => {
  form.amount = amount.toString()
  validateField('amount')
}

// 选择分类
const selectCategory = (category: any) => {
  form.categoryId = category.id
  validateField('categoryId')
  // 更新最近使用
  accountingStore.updateRecentCategory(category.id)
}

// 验证字段
const validateField = (field: keyof typeof errors) => {
  const validation = validateTransactionForm({
    type: form.type,
    amount: form.amount,
    categoryId: form.categoryId,
    accountId: form.accountId,
    transactionTime: form.transactionTime
  })

  errors[field] = validation.errors[field] || ''
}

// 验证整个表单
const validateForm = () => {
  Object.keys(errors).forEach(key => {
    validateField(key as keyof typeof errors)
  })
}

// 分类选择器
const onCategoryChange = (e: any) => {
  categoryPickerValue.value = e.detail.value
}

const confirmCategory = () => {
  const index = categoryPickerValue.value[0]
  const category = availableCategories.value[index]
  if (category) {
    selectCategory(category)
  }
  showCategoryPicker.value = false
}

const hideCategoryPicker = () => {
  showCategoryPicker.value = false
}

// 账户选择器
const onAccountChange = (e: any) => {
  accountPickerValue.value = e.detail.value
}

const confirmAccount = () => {
  const index = accountPickerValue.value[0]
  const account = accounts.value[index]
  if (account) {
    form.accountId = account.id
    validateField('accountId')
  }
  showAccountPicker.value = false
}

const hideAccountPicker = () => {
  showAccountPicker.value = false
}

// 时间选择器
const onTimeChange = (e: any) => {
  currentTime.value = e.detail.value
}

const confirmTime = () => {
  form.transactionTime = currentTime.value
  showTimePicker.value = false
}

const hideTimePicker = () => {
  showTimePicker.value = false
}

// 应用智能推荐
const applySuggestion = (suggestion: any) => {
  form.categoryId = suggestion.categoryId
  form.amount = suggestion.amount.toString()
  validateForm()
  appStore.showSuccess('已应用智能推荐')
}

// 保存为模板
const saveAsTemplate = () => {
  if (!isFormValid.value) {
    appStore.showError('请先完善记账信息')
    return
  }

  // 这里应该保存到模板存储
  appStore.showSuccess('模板保存成功')
}

// 重置表单
const resetForm = () => {
  form.amount = ''
  form.categoryId = 0
  form.accountId = 0
  form.targetAccountId = 0
  form.remark = ''
  Object.keys(errors).forEach(key => {
    errors[key as keyof typeof errors] = ''
  })
}

// 处理提交
const handleSubmit = async () => {
  if (!isFormValid.value) return

  try {
    loading.value = true

    const transactionData = {
      ...form,
      amount: parseFloat(form.amount),
      transactionTime: form.transactionTime || new Date().toISOString()
    }

    await accountingStore.createTransaction(transactionData)

    appStore.showSuccess('记账成功')

    // 重置表单
    resetForm()

    // 返回上一页
    setTimeout(() => {
      uni.navigateBack()
    }, 1500)

  } catch (error: any) {
    console.error('记账失败:', error)
    appStore.showError(error.message || '记账失败，请重试')
  } finally {
    loading.value = false
  }
}

// 返回上一页
const goBack = () => {
  uni.navigateBack()
}

// 生命周期
onMounted(async () => {
  // 获取设备信息
  const deviceInfo = await DeviceUtils.getDeviceInfo()
  safeAreaBottom.value = deviceInfo.safeAreaInsets.bottom

  // 设置默认时间
  form.transactionTime = new Date().toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })

  currentTime.value = new Date().toISOString()

  // 加载数据
  await Promise.all([
    accountingStore.loadCategories(),
    accountingStore.loadAccounts()
  ])

  // 获取页面参数
  const pages = getCurrentPages()
  const currentPage = pages[pages.length - 1]
  const options = currentPage.options || {}

  // 如果有类型参数，设置默认类型
  if (options.type) {
    const type = parseInt(options.type as string)
    if ([1, 2, 3].includes(type)) {
      form.type = type
    }
  }
})
</script>

<style lang="scss" scoped>
.accounting-page {
  height: 100vh;
  background-color: #f5f5f5;
  display: flex;
  flex-direction: column;
}

.nav-bar {
  background-color: #fff;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  border-bottom: 1px solid #e5e5e5;
  position: relative;
  z-index: 10;
}

.nav-left, .nav-right {
  width: 60px;
  display: flex;
  align-items: center;
}

.nav-left {
  justify-content: flex-start;
}

.nav-right {
  justify-content: flex-end;
}

.nav-title {
  font-size: 18px;
  font-weight: 600;
  color: #333;
}

.nav-action {
  font-size: 14px;
  color: #1989fa;
  cursor: pointer;
}

.main-content {
  flex: 1;
  background-color: #f5f5f5;
}

.type-selector {
  background-color: #fff;
  margin: 16px;
  border-radius: 12px;
  overflow: hidden;
}

.type-tabs {
  display: flex;
}

.type-tab {
  flex: 1;
  padding: 16px;
  text-align: center;
  transition: all 0.2s;

  &.active {
    background-color: #1989fa;
  }
}

.type-text {
  font-size: 16px;
  color: #333;

  .active & {
    color: white;
    font-weight: 600;
  }
}

.form-container {
  background-color: #fff;
  margin: 0 16px 16px 16px;
  border-radius: 12px;
  padding: 24px;
}

.form-item {
  margin-bottom: 24px;

  &:last-child {
    margin-bottom: 0;
  }
}

.amount-input {
  display: flex;
  align-items: center;
  padding: 16px;
  background-color: #f8f9fa;
  border-radius: 12px;
}

.currency {
  font-size: 24px;
  font-weight: 600;
  color: #1989fa;
  margin-right: 8px;
}

.amount-field {
  flex: 1;
  font-size: 24px;
  font-weight: 600;
  color: #333;
  border: none;
  outline: none;
  background: transparent;
}

.field-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  background-color: #f8f9fa;
  border-radius: 12px;
  cursor: pointer;
  transition: background-color 0.2s;

  &:active {
    background-color: #e9ecef;
  }
}

.field-label {
  display: flex;
  align-items: center;
}

.label-text {
  font-size: 16px;
  color: #333;
  margin-left: 8px;
}

.field-value {
  display: flex;
  align-items: center;
}

.value-text {
  font-size: 16px;
  color: #333;
  margin-right: 8px;

  &.placeholder {
    color: #ccc;
  }
}

.error-text {
  font-size: 12px;
  color: #ff4d4f;
  margin-top: 4px;
}

.remark-field {
  width: 100%;
  padding: 16px;
  background-color: #f8f9fa;
  border-radius: 12px;
  border: none;
  outline: none;
  font-size: 16px;
  min-height: 80px;
}

.remark-count {
  text-align: right;
  margin-top: 4px;
}

.count-text {
  font-size: 12px;
  color: #999;
}

.section-label {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin-bottom: 12px;
  display: block;
}

.amount-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 12px;
}

.amount-btn {
  padding: 12px;
  background-color: #f8f9fa;
  border-radius: 8px;
  text-align: center;
  cursor: pointer;
  transition: background-color 0.2s;

  &:active {
    background-color: #e9ecef;
  }
}

.amount-text {
  font-size: 14px;
  color: #333;
}

.category-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 12px;
}

.category-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 16px 12px;
  background-color: #f8f9fa;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s;

  &:active {
    background-color: #e9ecef;
    transform: scale(0.95);
  }
}

.category-icon {
  width: 40px;
  height: 40px;
  border-radius: 8px;
  background-color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 8px;
}

.category-name {
  font-size: 12px;
  color: #666;
  text-align: center;
}

.suggestion-list {
  background-color: #f8f9fa;
  border-radius: 8px;
  overflow: hidden;
}

.suggestion-item {
  display: flex;
  align-items: center;
  padding: 16px;
  border-bottom: 1px solid #e5e5e5;
  cursor: pointer;
  transition: background-color 0.2s;

  &:last-child {
    border-bottom: none;
  }

  &:active {
    background-color: #e9ecef;
  }
}

.suggestion-icon {
  width: 36px;
  height: 36px;
  border-radius: 6px;
  background-color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 12px;
}

.suggestion-content {
  flex: 1;
}

.suggestion-title {
  font-size: 15px;
  color: #333;
  display: block;
  margin-bottom: 4px;
}

.suggestion-amount {
  font-size: 13px;
  color: #666;
}

.action-bar {
  background-color: #fff;
  padding: 16px;
  border-top: 1px solid #e5e5e5;
}

.action-buttons {
  display: flex;
  gap: 12px;
}

.action-btn {
  flex: 1;
  height: 44px;
  border-radius: 8px;
  border: none;
  font-size: 16px;
  font-weight: 600;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.2s;

  &.secondary {
    background-color: #f8f9fa;
    color: #666;

    &:active {
      background-color: #e9ecef;
    }
  }

  &.primary {
    background-color: #1989fa;
    color: white;

    &:disabled {
      background-color: #ccc;
      cursor: not-allowed;
    }

    &:not(:disabled):active {
      background-color: #0066cc;
    }

    &.loading {
      pointer-events: none;
    }
  }
}

.bottom-safe-area {
  background-color: #fff;
}

.picker-container {
  background-color: #fff;
  border-radius: 12px 12px 0 0;
  overflow: hidden;
}

.picker-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  border-bottom: 1px solid #e5e5e5;
}

.picker-cancel, .picker-confirm {
  font-size: 16px;
  color: #1989fa;
  cursor: pointer;
}

.picker-title {
  font-size: 16px;
  font-weight: 600;
  color: #333;
}

.category-picker, .account-picker {
  height: 200px;
}

.picker-item {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 40px;
  font-size: 16px;
  color: #333;
}

.datetime-picker {
  background-color: #fff;
  border-radius: 12px 12px 0 0;
  overflow: hidden;
}

.time-picker {
  height: 200px;
}

.picker-placeholder {
  padding: 20px;
  text-align: center;
  color: #999;
  font-size: 16px;
}
</style>
