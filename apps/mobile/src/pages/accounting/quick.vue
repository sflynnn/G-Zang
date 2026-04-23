<template>
  <view class="quick-record-page">
    <!-- 顶部导航 -->
    <view class="nav-bar">
      <view class="nav-left" @click="goBack">
        <uni-icons type="left" size="20" color="#333"></uni-icons>
      </view>
      <view class="nav-title">快速记账</view>
      <view class="nav-right">
        <view class="book-selector" @click="showBookPicker">
          <text class="book-name">{{ currentBook?.name || '选择账本' }}</text>
          <uni-icons type="bottom" size="12" color="#666"></uni-icons>
        </view>
      </view>
    </view>

    <view class="page-content">
      <!-- 金额显示区域 -->
      <view class="amount-display">
        <text class="currency-symbol">{{ currentCurrencySymbol }}</text>
        <input
          v-model="form.amount"
          type="digit"
          placeholder="0.00"
          class="amount-input"
          @input="onAmountInput"
        />
      </view>

      <!-- 交易类型切换 -->
      <view class="type-selector">
        <view class="type-tabs">
          <view
            class="type-tab expense"
            :class="{ active: form.type === 2 }"
            @click="switchType(2)"
          >
            <text>支出</text>
          </view>
          <view
            class="type-tab income"
            :class="{ active: form.type === 1 }"
            @click="switchType(1)"
          >
            <text>收入</text>
          </view>
        </view>
      </view>

      <!-- 分类网格 -->
      <view class="category-section">
        <view class="category-grid">
          <view
            v-for="category in currentCategories"
            :key="category.id"
            class="category-item"
            :class="{ selected: form.categoryId === category.id }"
            @click="selectCategory(category)"
          >
            <view
              class="category-icon"
              :style="{ background: category.bgColor || '#F8F9FA' }"
            >
              <text class="category-emoji">{{ category.icon || '📂' }}</text>
            </view>
            <text class="category-name">{{ category.name }}</text>
            <text
              v-if="category.amount"
              class="category-amount"
              :class="form.type === 1 ? 'income' : 'expense'"
            >
              {{ form.type === 1 ? '+' : '-' }}{{ formatAmount(category.amount) }}
            </text>
          </view>
        </view>
      </view>

      <!-- 子分类展开面板 -->
      <view v-if="selectedCategory && selectedCategory.children?.length" class="sub-panel">
        <view class="sub-panel-header">
          <view class="sub-panel-title">
            <view
              class="sub-icon"
              :style="{ background: selectedCategory.bgColor || '#F8F9FA' }"
            >
              <text class="sub-emoji">{{ selectedCategory.icon || '📂' }}</text>
            </view>
            <text class="sub-name">{{ selectedCategory.name }}</text>
          </view>
        </view>
        <view class="sub-tags">
          <view
            v-for="sub in selectedCategory.children"
            :key="sub.id"
            class="sub-tag"
            :class="{ selected: form.subCategoryId === sub.id }"
            @click="selectSubCategory(sub)"
          >
            <text>{{ sub.name }}</text>
            <text v-if="sub.amount" class="sub-amount" :class="form.type === 1 ? 'income' : 'expense'">
              {{ form.type === 1 ? '+' : '-' }}{{ formatAmount(sub.amount) }}
            </text>
          </view>
        </view>
      </view>

      <!-- 账户选择 -->
      <view class="form-row" @click="showAccountPicker = true">
        <view class="row-icon">💳</view>
        <text class="row-label">账户</text>
        <text class="row-value" :class="{ placeholder: !selectedAccountName }">
          {{ selectedAccountName || '请选择账户' }}
        </text>
        <text class="row-arrow">›</text>
      </view>

      <!-- 日期选择 -->
      <view class="form-row" @click="showDatePicker">
        <view class="row-icon">📅</view>
        <text class="row-label">日期</text>
        <text class="row-value">{{ formatDisplayDate }}</text>
        <text class="row-arrow">›</text>
      </view>

      <!-- 快捷金额 -->
      <view class="quick-amounts">
        <view
          v-for="amount in quickAmounts"
          :key="amount"
          class="qa-btn"
          @click="setQuickAmount(amount)"
        >
          <text>{{ currentCurrencySymbol }}{{ amount }}</text>
        </view>
      </view>
    </view>

    <!-- 底部提交按钮 -->
    <view class="submit-bar">
      <button
        class="submit-btn"
        :class="{ disabled: !isFormValid }"
        :disabled="!isFormValid || submitting"
        @click="handleSubmit"
      >
        <text v-if="submitting">保存中...</text>
        <text v-else>确认记账</text>
      </button>
    </view>

    <!-- 账户选择弹窗 -->
    <uni-popup ref="accountPopup" type="bottom" :is-mask-click="true">
      <view class="picker-container">
        <view class="picker-header">
          <text class="picker-cancel" @click="closeAccountPicker">取消</text>
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
              {{ getAccountIcon(account) }} {{ account.name }} ({{ currentCurrencySymbol }}{{ account.balance.toFixed(2) }})
            </view>
          </picker-view-column>
        </picker-view>
      </view>
    </uni-popup>

    <!-- 日期选择弹窗 -->
    <uni-popup ref="datePopup" type="bottom" :is-mask-click="true">
      <view class="picker-container">
        <view class="picker-header">
          <text class="picker-cancel" @click="closeDatePicker">取消</text>
          <text class="picker-title">选择日期</text>
          <text class="picker-confirm" @click="confirmDate">确定</text>
        </view>
        <picker-view
          :value="datePickerValue"
          @change="onDateChange"
          class="date-picker"
        >
          <picker-view-column>
            <view v-for="year in years" :key="year" class="picker-item">
              {{ year }}年
            </view>
          </picker-view-column>
          <picker-view-column>
            <view v-for="month in months" :key="month" class="picker-item">
              {{ month }}月
            </view>
          </picker-view-column>
          <picker-view-column>
            <view v-for="day in days" :key="day" class="picker-item">
              {{ day }}日
            </view>
          </picker-view-column>
        </picker-view>
      </view>
    </uni-popup>

    <!-- 账本选择弹窗 -->
    <uni-popup ref="bookPopup" type="bottom" :is-mask-click="true">
      <view class="picker-container">
        <view class="picker-header">
          <text class="picker-cancel" @click="closeBookPicker">取消</text>
          <text class="picker-title">选择账本</text>
          <text class="picker-confirm" @click="confirmBook">确定</text>
        </view>
        <picker-view
          :value="bookPickerValue"
          @change="onBookChange"
          class="book-picker"
        >
          <picker-view-column>
            <view v-for="book in books" :key="book.id" class="picker-item">
              {{ book.icon || '📒' }} {{ book.name }}
            </view>
          </picker-view-column>
        </picker-view>
      </view>
    </uni-popup>
  </view>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { useAccountingStore } from '@/stores/accounting'
import { useBookStore } from '@/stores/book'
import { useTransactionStore } from '@/stores/transaction'
import { useCategoryStore } from '@/stores/category'
import { useAccountStore } from '@/stores/account'

// 分类图标背景色映射
const CATEGORY_COLORS: Record<string, string> = {
  '🍜': '#FFF3E0',
  '🚇': '#E3F2FD',
  '🛒': '#FCE4EC',
  '🎮': '#F3E5F5',
  '💊': '#FFEBEE',
  '📚': '#E8F5E9',
  '🏠': '#FFF8E1',
  '📱': '#E0F7FA',
  '💰': '#E8F5E9',
  '🎁': '#FCE4EC',
  '✈️': '#E3F2FD',
  '🏥': '#FFEBEE',
  '👔': '#E3F2FD',
  '🎨': '#F3E5F5',
  '☔': '#E0F7FA',
  '📂': '#F8F9FA',
}

// Store
const accountingStore = useAccountingStore()
const bookStore = useBookStore()
const transactionStore = useTransactionStore()
const categoryStore = useCategoryStore()
const accountStore = useAccountStore()

// 响应式数据
const submitting = ref(false)
const showAccountPicker = ref(false)
const accountPickerValue = ref([0])
const bookPickerValue = ref([0])
const datePickerValue = ref([0, 0, 0])

// 弹窗 refs
const accountPopup = ref<any>(null)
const datePopup = ref<any>(null)
const bookPopup = ref<any>(null)

// 表单数据
const form = reactive({
  type: 2 as 1 | 2, // 1: 收入, 2: 支出
  amount: '',
  categoryId: 0 as number,
  subCategoryId: 0 as number,
  accountId: 0 as number,
  transactionDate: ''
})

// 日期选择器数据
const years = computed(() => {
  const current = new Date().getFullYear()
  return Array.from({ length: 10 }, (_, i) => current - 5 + i)
})
const months = computed(() => Array.from({ length: 12 }, (_, i) => i + 1))
const days = computed(() => {
  const year = years.value[datePickerValue.value[0]] || new Date().getFullYear()
  const month = months.value[datePickerValue.value[1]] || new Date().getMonth() + 1
  return Array.from({ length: getDaysInMonth(year, month) }, (_, i) => i + 1)
})

// 常用金额
const quickAmounts = [10, 50, 100, 200, 500]

// 计算属性
const currentBook = computed(() => bookStore.currentBook)
const books = computed(() => bookStore.bookList)
const currentCurrencySymbol = computed(() => bookStore.currentCurrencySymbol)

const accounts = computed(() => accountStore.accountList)

const currentCategories = computed(() => {
  const type = form.type
  const categories = type === 1 ? categoryStore.incomeCategories : categoryStore.expenseCategories
  return categories.map(cat => ({
    ...cat,
    bgColor: CATEGORY_COLORS[cat.icon] || '#F8F9FA'
  }))
})

const selectedCategory = computed(() => {
  if (!form.categoryId) return null
  return currentCategories.value.find(c => c.id === form.categoryId)
})

const selectedAccountName = computed(() => {
  if (!form.accountId) return ''
  const account = accounts.value.find(a => a.id === form.accountId)
  return account?.name || ''
})

const formatDisplayDate = computed(() => {
  if (!form.transactionDate) return '今天'
  const today = formatDate(new Date())
  const yesterday = formatDate(new Date(Date.now() - 86400000))
  if (form.transactionDate === today) return '今天'
  if (form.transactionDate === yesterday) return '昨天'
  return form.transactionDate.replace(/-/g, '/')
})

const isFormValid = computed(() => {
  const hasAmount = form.amount && parseFloat(form.amount) > 0
  const hasCategory = form.categoryId > 0
  const hasAccount = form.accountId > 0
  return hasAmount && hasCategory && hasAccount
})

// 方法
const formatDate = (date: Date) => {
  const y = date.getFullYear()
  const m = String(date.getMonth() + 1).padStart(2, '0')
  const d = String(date.getDate()).padStart(2, '0')
  return `${y}-${m}-${d}`
}

const formatAmount = (amount: number) => {
  if (amount >= 1000) {
    return (amount / 1000).toFixed(1) + 'k'
  }
  return amount.toFixed(0)
}

const getDaysInMonth = (year: number, month: number) => {
  return new Date(year, month, 0).getDate()
}

const getAccountIcon = (account: any) => {
  const typeIcons: Record<string, string> = {
    CASH: '💵',
    BANK_CARD: '🏦',
    CREDIT_CARD: '💳',
    E_WALLET: '📱',
    INVESTMENT: '📈',
    DEBT: '📋',
    OTHER: '💰',
  }
  return typeIcons[account.type] || '💰'
}

const switchType = (type: 1 | 2) => {
  form.type = type
  form.categoryId = 0
  form.subCategoryId = 0
}

const selectCategory = (category: any) => {
  form.categoryId = category.id
  form.subCategoryId = 0
}

const selectSubCategory = (sub: any) => {
  form.subCategoryId = sub.id
  form.categoryId = sub.id
}

const setQuickAmount = (amount: number) => {
  form.amount = amount.toString()
}

const onAmountInput = () => {
  // 过滤非数字输入
  form.amount = form.amount.replace(/[^\d.]/g, '')
  // 确保只有一个小数点
  const parts = form.amount.split('.')
  if (parts.length > 2) {
    form.amount = parts[0] + '.' + parts.slice(1).join('')
  }
  // 限制小数位数为2位
  if (parts[1] && parts[1].length > 2) {
    form.amount = parts[0] + '.' + parts[1].slice(0, 2)
  }
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
  }
  closeAccountPicker()
}

const closeAccountPicker = () => {
  accountPopup.value.close()
}

// 日期选择器
const showDatePicker = () => {
  const date = form.transactionDate ? new Date(form.transactionDate) : new Date()
  const yearIndex = years.value.indexOf(date.getFullYear())
  const monthIndex = date.getMonth()
  const dayIndex = date.getDate() - 1
  datePickerValue.value = [
    yearIndex >= 0 ? yearIndex : 0,
    monthIndex,
    dayIndex >= 0 ? dayIndex : 0
  ]
  datePopup.value.open()
}

const onDateChange = (e: any) => {
  datePickerValue.value = e.detail.value
}

const confirmDate = () => {
  const year = years.value[datePickerValue.value[0]] || new Date().getFullYear()
  const month = months.value[datePickerValue.value[1]] || 1
  const day = days.value[datePickerValue.value[2]] || 1
  form.transactionDate = `${year}-${String(month).padStart(2, '0')}-${String(day).padStart(2, '0')}`
  closeDatePicker()
}

const closeDatePicker = () => {
  datePopup.value.close()
}

// 账本选择器
const showBookPicker = () => {
  const currentIndex = books.value.findIndex(b => b.id === currentBook.value?.id)
  bookPickerValue.value = currentIndex >= 0 ? [currentIndex] : [0]
  bookPopup.value.open()
}

const onBookChange = (e: any) => {
  bookPickerValue.value = e.detail.value
}

const confirmBook = () => {
  const index = bookPickerValue.value[0]
  const book = books.value[index]
  if (book) {
    bookStore.switchBook(book.id)
  }
  closeBookPicker()
}

const closeBookPicker = () => {
  bookPopup.value.close()
}

// 返回
const goBack = () => {
  uni.navigateBack()
}

// 提交
const handleSubmit = async () => {
  if (!isFormValid.value) return

  try {
    submitting.value = true

    const transactionData = {
      type: form.type as 1 | 2,
      amount: parseFloat(form.amount),
      categoryId: form.categoryId,
      accountId: form.accountId,
      transactionTime: form.transactionDate ? `${form.transactionDate} ${new Date().toTimeString().slice(0, 8)}` : new Date().toISOString(),
      remark: ''
    }

    await transactionStore.createTransaction(transactionData)

    uni.showToast({
      title: '记账成功',
      icon: 'success'
    })

    // 重置表单
    form.amount = ''
    form.categoryId = 0
    form.subCategoryId = 0

    setTimeout(() => {
      uni.navigateBack()
    }, 1500)

  } catch (error: any) {
    // ignore
  } finally {
    submitting.value = false
  }
}

// 生命周期
onMounted(async () => {
  // 设置默认日期
  form.transactionDate = formatDate(new Date())

  // 加载数据
  await Promise.all([
    bookStore.fetchBooks(),
    categoryStore.fetchCategories(),
    accountStore.fetchAccounts()
  ])

  // 获取页面参数
  const pages = getCurrentPages()
  const currentPage = pages[pages.length - 1] as any
  const options = currentPage?.options || {}

  // 如果有类型参数
  if (options.type) {
    const type = parseInt(options.type as string) as 1 | 2
    if ([1, 2].includes(type)) {
      form.type = type
    }
  }

  // 如果有账本参数
  if (options.bookId) {
    const bookId = parseInt(options.bookId as string)
    bookStore.switchBook(bookId)
  }
})
</script>

<style lang="scss" scoped>
.quick-record-page {
  height: 100vh;
  background-color: #fff;
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
}

.nav-left, .nav-right {
  width: 80px;
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

.book-selector {
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 6px 12px;
  background: #F8F9FA;
  border-radius: 16px;
  max-width: 120px;
}

.book-name {
  font-size: 13px;
  color: #333;
  max-width: 80px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.page-content {
  flex: 1;
  overflow-y: auto;
  padding-bottom: 80px;
}

// 金额显示
.amount-display {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 32px 20px;
  background: linear-gradient(135deg, #0F4C5C 0%, #1a6b7a 100%);
}

.currency-symbol {
  font-size: 32px;
  font-weight: 600;
  color: rgba(255, 255, 255, 0.9);
  margin-right: 4px;
}

.amount-input {
  font-size: 48px;
  font-weight: 700;
  color: #fff;
  text-align: center;
  background: transparent;
  border: none;
  outline: none;
  min-width: 200px;

  &::placeholder {
    color: rgba(255, 255, 255, 0.4);
  }
}

// 类型切换
.type-selector {
  margin: 16px;
}

.type-tabs {
  display: flex;
  background: #F8F9FA;
  border-radius: 12px;
  padding: 5px;
  gap: 4px;
}

.type-tab {
  flex: 1;
  padding: 12px;
  text-align: center;
  border-radius: 10px;
  font-size: 14px;
  font-weight: 600;
  transition: all 0.2s;

  &.expense {
    color: #EF476F;
    &.active {
      background: #EF476F;
      color: #fff;
    }
  }

  &.income {
    color: #06D6A0;
    &.active {
      background: #06D6A0;
      color: #fff;
    }
  }
}

// 分类网格
.category-section {
  margin: 0 16px 16px;
}

.category-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 12px;
}

.category-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 6px;
  padding: 12px 4px;
  background: #F8F9FA;
  border-radius: 14px;
  transition: all 0.2s;

  &.selected {
    background: rgba(15, 76, 92, 0.08);
    border: 1px solid #0F4C5C;
  }

  &:active {
    transform: scale(0.96);
  }
}

.category-icon {
  width: 44px;
  height: 44px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.category-emoji {
  font-size: 20px;
}

.category-name {
  font-size: 11px;
  color: #374151;
  text-align: center;
}

.category-amount {
  font-size: 10px;
  font-weight: 600;

  &.expense {
    color: #EF476F;
  }

  &.income {
    color: #06D6A0;
  }
}

// 子分类面板
.sub-panel {
  margin: 0 16px 16px;
  background: #fff;
  border-radius: 16px;
  padding: 14px;
  border: 1px solid #E5E5E5;
}

.sub-panel-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 12px;
}

.sub-panel-title {
  display: flex;
  align-items: center;
  gap: 8px;
}

.sub-icon {
  width: 28px;
  height: 28px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.sub-emoji {
  font-size: 14px;
}

.sub-name {
  font-size: 14px;
  font-weight: 600;
  color: #1F2937;
}

.sub-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.sub-tag {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 14px;
  background: #F8F9FA;
  border-radius: 20px;
  font-size: 12px;
  color: #333;
  transition: all 0.2s;

  &.selected {
    background: rgba(15, 76, 92, 0.12);
    border: 1px solid #0F4C5C;
    color: #0F4C5C;
    font-weight: 600;
  }
}

.sub-amount {
  font-size: 11px;

  &.expense {
    color: #EF476F;
  }

  &.income {
    color: #06D6A0;
  }
}

// 表单行
.form-row {
  display: flex;
  align-items: center;
  margin: 0 16px 12px;
  height: 46px;
  background: #F8F9FA;
  border-radius: 12px;
  padding: 0 16px;
  gap: 8px;

  &:active {
    background: #E9ECEF;
  }
}

.row-icon {
  font-size: 16px;
}

.row-label {
  font-size: 14px;
  color: #666;
}

.row-value {
  flex: 1;
  font-size: 14px;
  color: #1F2937;
  text-align: right;

  &.placeholder {
    color: #ccc;
  }
}

.row-arrow {
  color: #D1D5DB;
  font-size: 16px;
}

// 快捷金额
.quick-amounts {
  display: flex;
  gap: 10px;
  padding: 0 16px;
  margin-top: 8px;
}

.qa-btn {
  flex: 1;
  padding: 12px 0;
  background: #F8F9FA;
  border-radius: 10px;
  text-align: center;
  font-size: 13px;
  color: #333;
  font-weight: 500;

  &:active {
    background: #E9ECEF;
  }
}

// 底部提交
.submit-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 16px;
  padding-bottom: calc(16px + env(safe-area-inset-bottom));
  background: #fff;
  border-top: 1px solid #E5E5E5;
}

.submit-btn {
  width: 100%;
  height: 48px;
  background: #0F4C5C;
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 16px;
  font-weight: 600;
  border: none;

  &.disabled {
    background: #ccc;
  }

  &:active:not(.disabled) {
    opacity: 0.9;
  }
}

// 选择器弹窗
.picker-container {
  background-color: #fff;
  border-radius: 16px 16px 0 0;
  overflow: hidden;
}

.picker-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  border-bottom: 1px solid #E5E5E5;
}

.picker-cancel, .picker-confirm {
  font-size: 15px;
}

.picker-cancel {
  color: #999;
}

.picker-confirm {
  color: #0F4C5C;
  font-weight: 500;
}

.picker-title {
  font-size: 16px;
  font-weight: 600;
  color: #333;
}

.account-picker, .book-picker, .date-picker {
  height: 200px;
}

.picker-item {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 44px;
  font-size: 15px;
  color: #333;
}
</style>
