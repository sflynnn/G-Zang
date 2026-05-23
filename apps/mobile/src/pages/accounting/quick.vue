<template>
  <view class="quick-record-page apple-style">
    <!-- Top Navigation -->
    <view class="nav-bar">
      <view class="nav-left" @click="goBack">
        <AppleIcon name="chevron-left" :size="20" color="var(--gzang-text-primary)" />
      </view>
      <view class="nav-title">{{ t('accounting.quickRecord') }}</view>
      <view class="nav-right">
        <view class="book-selector" @click="showBookPicker">
          <text class="book-name">{{ currentBook?.name || t('accounting.selectBook') }}</text>
          <AppleIcon name="chevron-down" :size="12" color="var(--gzang-text-secondary)" />
        </view>
      </view>
    </view>

    <!-- Main Content -->
    <view class="page-content">
      <!-- Amount Display -->
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

      <!-- Transaction Type Selector -->
      <TransactionTypeSelector v-model="form.type" @change="onTypeChange" />

      <!-- Category Grid -->
      <view class="category-section">
        <CategoryGrid
          v-model="form.categoryId"
          :expense-categories="expenseCategories"
          :income-categories="incomeCategories"
          :currency-symbol="currentCurrencySymbol"
          :show-tabs="false"
          :show-amount="true"
          :compact="true"
          @change="onCategoryChange"
        />
      </view>

      <!-- Quick Amounts -->
      <view class="quick-amounts">
        <view
          v-for="amount in quickAmounts"
          :key="amount"
          class="qa-btn"
          :class="{ active: form.amount === String(amount) }"
          @click="setQuickAmount(amount)"
        >
          <text>{{ currentCurrencySymbol }}{{ amount }}</text>
        </view>
      </view>

      <!-- Form Rows -->
      <view class="form-rows">
        <!-- Account Selection -->
        <view class="form-row" @click="showAccountPicker = true">
          <view class="row-icon" style="background: rgba(6, 214, 160, 0.1)">
            <AppleIcon name="wallet" :size="18" color="var(--gzang-success)" />
          </view>
          <text class="row-label">{{ t('accounting.account') }}</text>
          <text class="row-value" :class="{ placeholder: !selectedAccountName }">
            {{ selectedAccountName || t('accounting.selectAccount') }}
          </text>
          <AppleIcon name="chevron-right" :size="14" color="var(--gzang-text-tertiary)" />
        </view>

        <!-- Date Selection -->
        <view class="form-row" @click="showDatePicker">
          <view class="row-icon" style="background: rgba(15, 76, 92, 0.1)">
            <AppleIcon name="calendar" :size="18" color="var(--gzang-primary)" />
          </view>
          <text class="row-label">{{ t('accounting.date') }}</text>
          <text class="row-value">{{ formatDisplayDate }}</text>
          <AppleIcon name="chevron-right" :size="14" color="var(--gzang-text-tertiary)" />
        </view>
      </view>
    </view>

    <!-- Submit Bar -->
    <view class="submit-bar">
      <button
        class="submit-btn"
        :class="{ disabled: !isFormValid }"
        :disabled="!isFormValid || submitting"
        @click="handleSubmit"
      >
        <AppleIcon v-if="submitting" name="refresh" :size="16" color="white" class="spin" />
        <text v-else>{{ t('accounting.confirmRecord') }}</text>
        <text v-if="isFormValid && form.amount" class="btn-amount">
          {{ currentCurrencySymbol }}{{ parseFloat(form.amount).toFixed(2) }}
        </text>
      </button>
    </view>

    <!-- Account Picker Popup -->
    <uni-popup ref="accountPopup" type="bottom" :is-mask-click="true">
      <view class="picker-container">
        <view class="picker-header">
          <text class="picker-cancel" @click="closeAccountPicker">{{ t('common.cancel') }}</text>
          <text class="picker-title">{{ t('accounting.selectAccount') }}</text>
          <text class="picker-confirm" @click="confirmAccount">{{ t('common.confirm') }}</text>
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
              {{ account.name }} ({{ currentCurrencySymbol }}{{ (Number(account.balance) || 0).toFixed(2) }})
            </view>
          </picker-view-column>
        </picker-view>
      </view>
    </uni-popup>

    <!-- Date Picker Popup -->
    <uni-popup ref="datePopup" type="bottom" :is-mask-click="true">
      <view class="date-picker-container">
        <view class="picker-header">
          <text class="picker-cancel" @click="closeDatePicker">{{ t('common.cancel') }}</text>
          <text class="picker-title">{{ t('accounting.selectDate') }}</text>
          <text class="picker-confirm" @click="confirmDate">{{ t('common.confirm') }}</text>
        </view>
        <view class="quick-date-btns">
          <view 
            v-for="quick in quickDates" 
            :key="quick.key"
            class="quick-date-btn"
            :class="{ active: form.transactionDate === quick.date }"
            @click="selectQuickDate(quick.date)"
          >
            {{ quick.label }}
          </view>
        </view>
        <view class="date-picker-wrapper">
          <picker-view
            :value="datePickerValue"
            @change="onDateChange"
            class="date-picker"
          >
            <picker-view-column>
              <view v-for="year in years" :key="year" class="picker-item">
                {{ year }}
              </view>
            </picker-view-column>
            <picker-view-column>
              <view v-for="month in months" :key="month" class="picker-item">
                {{ String(month).padStart(2, '0') }}
              </view>
            </picker-view-column>
            <picker-view-column>
              <view v-for="day in days" :key="day" class="picker-item">
                {{ String(day).padStart(2, '0') }}
              </view>
            </picker-view-column>
          </picker-view>
        </view>
        <view class="picker-labels">
          <text class="picker-label">年</text>
          <text class="picker-label">月</text>
          <text class="picker-label">日</text>
        </view>
      </view>
    </uni-popup>

    <!-- Book Picker Popup -->
    <uni-popup ref="bookPopup" type="bottom" :is-mask-click="true">
      <view class="picker-container">
        <view class="picker-header">
          <text class="picker-cancel" @click="closeBookPicker">{{ t('common.cancel') }}</text>
          <text class="picker-title">{{ t('book.switchBook') }}</text>
          <text class="picker-confirm" @click="confirmBook">{{ t('common.confirm') }}</text>
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
import { useI18n } from 'vue-i18n'
import { useAccountingStore } from '@/stores/accounting'
import { useBookStore } from '@/stores/book'
import AppleIcon from '@/components/common/AppleIcon/index.vue'
import TransactionTypeSelector from '@/components/business/TransactionTypeSelector/index.vue'
import CategoryGrid from '@/components/business/CategoryGrid/index.vue'
import type { Category } from '@/components/business/CategoryGrid/index.vue'

const { t } = useI18n()

// Stores
const accountingStore = useAccountingStore()
const bookStore = useBookStore()

// Refs
const submitting = ref(false)
const showAccountPicker = ref(false)
const accountPickerValue = ref([0])
const bookPickerValue = ref([0])
const datePickerValue = ref([0, 0, 0])

// Popup refs
const accountPopup = ref<any>(null)
const datePopup = ref<any>(null)
const bookPopup = ref<any>(null)

// Form data
const form = reactive({
  type: 2 as 1 | 2, // 2: expense, 1: income
  amount: '',
  categoryId: undefined as number | undefined,
  accountId: 0,
  transactionDate: ''
})

// Date picker data
const years = computed(() => {
  const current = new Date().getFullYear()
  return Array.from({ length: 10 }, (_, i) => current - 5 + i)
})
const months = computed(() => Array.from({ length: 12 }, (_, i) => i + 1))

const getDaysInMonth = (year: number, month: number) => {
  return new Date(year, month, 0).getDate()
}

const days = computed(() => {
  const year = years.value[datePickerValue.value[0]] || new Date().getFullYear()
  const month = months.value[datePickerValue.value[1]] || 1
  return Array.from({ length: getDaysInMonth(year, month) }, (_, i) => i + 1)
})

// Quick amounts
const quickAmounts = [10, 50, 100, 200, 500]

// Computed
const currentBook = computed(() => bookStore.currentBook)
const books = computed(() => bookStore.books)
const currentCurrencySymbol = computed(() => bookStore.currentCurrencySymbol)
const accounts = computed(() => accountingStore.accounts)

const selectedAccountName = computed(() => {
  if (!form.accountId) return ''
  const account = accounts.value.find(a => a.id === form.accountId)
  return account?.name || ''
})

const formatDisplayDate = computed(() => {
  if (!form.transactionDate) return t('datetime.today')
  const today = formatDate(new Date())
  const yesterday = formatDate(new Date(Date.now() - 86400000))
  if (form.transactionDate === today) return t('datetime.today')
  if (form.transactionDate === yesterday) return t('datetime.yesterday')
  return form.transactionDate.replace(/-/g, '/')
})

// Quick dates
const quickDates = computed(() => {
  const today = new Date()
  const yesterday = new Date(today)
  yesterday.setDate(yesterday.getDate() - 1)
  return [
    { key: 'today', label: t('datetime.today'), date: formatDate(today) },
    { key: 'yesterday', label: t('datetime.yesterday'), date: formatDate(yesterday) },
  ]
})

const selectQuickDate = (date: string) => {
  form.transactionDate = date
  closeDatePicker()
}

// Categories (from API)
const expenseCategories = computed((): Category[] =>
  accountingStore.categories.filter(c => c.type === 2).map(c => ({
    id: c.id,
    name: c.name || c.categoryName || '',
    icon: c.icon || 'circle',
    color: c.color || '#6B7280',
    type: 2,
  }))
)

const incomeCategories = computed((): Category[] =>
  accountingStore.categories.filter(c => c.type === 1).map(c => ({
    id: c.id,
    name: c.name || c.categoryName || '',
    icon: c.icon || 'circle',
    color: c.color || '#6B7280',
    type: 1,
  }))
)

const isFormValid = computed(() => {
  const hasAmount = form.amount && parseFloat(form.amount) > 0
  const hasCategory = form.categoryId && form.categoryId > 0
  const hasAccount = form.accountId > 0
  return hasAmount && hasCategory && hasAccount
})

// Methods
const formatDate = (date: Date) => {
  const y = date.getFullYear()
  const m = String(date.getMonth() + 1).padStart(2, '0')
  const d = String(date.getDate()).padStart(2, '0')
  return `${y}-${m}-${d}`
}

const onTypeChange = (type: 1 | 2 | 3) => {
  form.type = type as 1 | 2
  form.categoryId = undefined
}

const onCategoryChange = (category: Category) => {
  form.categoryId = category.id
}

const onAmountInput = () => {
  // Filter non-numeric input
  form.amount = form.amount.replace(/[^\d.]/g, '')
  // Ensure only one decimal point
  const parts = form.amount.split('.')
  if (parts.length > 2) {
    form.amount = parts[0] + '.' + parts.slice(1).join('')
  }
  // Limit decimal places to 2
  if (parts[1] && parts[1].length > 2) {
    form.amount = parts[0] + '.' + parts[1].slice(0, 2)
  }
}

const setQuickAmount = (amount: number) => {
  form.amount = String(amount)
}

// Account picker
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
  accountPopup.value?.close()
}

// Date picker
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
  datePopup.value?.open()
}

const onDateChange = (e: any) => {
  const newValue = e.detail.value
  const oldYear = datePickerValue.value[0]
  const oldMonth = datePickerValue.value[1]
  const oldDay = datePickerValue.value[2]

  // 检查月份或年份是否改变
  if (newValue[0] !== oldYear || newValue[1] !== oldMonth) {
    // 月份或年份改变，重新计算天数
    const year = years.value[newValue[0]] || new Date().getFullYear()
    const month = months.value[newValue[1]] || 1
    const maxDays = getDaysInMonth(year, month)
    const currentDay = newValue[2]
    // 确保天数索引不超过最大值
    newValue[2] = Math.min(currentDay, maxDays - 1)
  }

  datePickerValue.value = newValue
}

const confirmDate = () => {
  const year = years.value[datePickerValue.value[0]] || new Date().getFullYear()
  const month = months.value[datePickerValue.value[1]] || 1
  const day = days.value[datePickerValue.value[2]] || 1
  form.transactionDate = `${year}-${String(month).padStart(2, '0')}-${String(day).padStart(2, '0')}`
  closeDatePicker()
}

const closeDatePicker = () => {
  datePopup.value?.close()
}

// Book picker
const showBookPicker = () => {
  const currentIndex = books.value.findIndex(b => b.id === currentBook.value?.id)
  bookPickerValue.value = currentIndex >= 0 ? [currentIndex] : [0]
  bookPopup.value?.open()
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
  bookPopup.value?.close()
}

// Go back
const goBack = () => {
  uni.navigateBack()
}

// Submit
const handleSubmit = async () => {
  if (!isFormValid.value) return

  try {
    submitting.value = true

    const transactionData = {
      type: form.type as 1 | 2,
      amount: parseFloat(form.amount),
      categoryId: form.categoryId!,
      accountId: form.accountId,
      transactionTime: form.transactionDate 
        ? `${form.transactionDate} ${new Date().toTimeString().slice(0, 8)}` 
        : new Date().toISOString(),
      remark: ''
    }

    await accountingStore.createTransaction(transactionData as any)

    uni.showToast({
      title: t('accounting.recordSuccess'),
      icon: 'success'
    })

    // Reset form
    form.amount = ''
    form.categoryId = undefined

    setTimeout(() => {
      uni.navigateBack()
    }, 1500)

  } catch (error: any) {
    // ignore
  } finally {
    submitting.value = false
  }
}

// Lifecycle
onMounted(async () => {
  // Set default date
  form.transactionDate = formatDate(new Date())

  // Load data
  await Promise.all([
    bookStore.loadBooks(),
    accountingStore.loadCategories(),
    accountingStore.loadAccounts()
  ])

  // Get page options
  const pages = getCurrentPages()
  const currentPage = pages[pages.length - 1] as any
  const options = currentPage?.options || {}

  // If type parameter exists
  if (options.type) {
    const type = parseInt(options.type as string) as 1 | 2
    if ([1, 2].includes(type)) {
      form.type = type
    }
  }

  // If book parameter exists
  if (options.bookId) {
    const bookId = parseInt(options.bookId as string)
    bookStore.switchBook(bookId)
  }
})
</script>

<style lang="scss" scoped>
.quick-record-page {
  min-height: 100vh;
  background: var(--gzang-bg);
  display: flex;
  flex-direction: column;
}

// ================== Navigation Bar ==================
.nav-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: var(--apple-space-4);
  padding-top: calc(constant(safe-area-inset-top) + var(--apple-space-3));
  background: var(--gzang-bg);
}

.nav-left,
.nav-right {
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
  font-size: var(--apple-text-lg);
  font-weight: var(--apple-font-semibold);
  color: var(--gzang-text-primary);
}

.book-selector {
  display: flex;
  align-items: center;
  gap: 8rpx;
  padding: 12rpx 16rpx;
  background: var(--gzang-surface);
  border-radius: var(--apple-radius-full);
  box-shadow: var(--apple-shadow-xs);
}

.book-name {
  font-size: var(--apple-text-sm);
  color: var(--gzang-text-primary);
  max-width: 80px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

// ================== Page Content ==================
.page-content {
  flex: 1;
  padding: 0 var(--apple-space-4);
}

// ================== Amount Display ==================
.amount-display {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: var(--apple-space-8) var(--apple-space-4);
  background: linear-gradient(135deg, var(--gzang-primary) 0%, var(--gzang-primary-light, #1a6b7a) 100%);
  border-radius: var(--apple-radius-2xl);
  margin-bottom: var(--apple-space-4);
}

.currency-symbol {
  font-size: var(--apple-text-3xl);
  font-weight: var(--apple-font-semibold);
  color: rgba(255, 255, 255, 0.9);
  margin-right: var(--apple-space-2);
}

.amount-input {
  font-size: 48px;
  font-weight: var(--apple-font-bold);
  color: white;
  text-align: center;
  background: transparent;
  border: none;
  outline: none;
  min-width: 200px;
  font-family: var(--font-mono);
  font-variant-numeric: tabular-nums;

  &::placeholder {
    color: rgba(255, 255, 255, 0.4);
  }
}

// ================== Category Section ==================
.category-section {
  margin-bottom: var(--apple-space-4);
}

// ================== Quick Amounts ==================
.quick-amounts {
  display: flex;
  gap: var(--apple-space-2);
  margin-bottom: var(--apple-space-4);
}

.qa-btn {
  flex: 1;
  padding: var(--apple-space-3);
  background: var(--gzang-surface);
  border-radius: var(--apple-radius-lg);
  text-align: center;
  font-size: var(--apple-text-sm);
  font-weight: var(--apple-font-medium);
  color: var(--gzang-text-secondary);
  box-shadow: var(--apple-shadow-xs);
  transition: all var(--apple-duration-fast) var(--apple-ease-out);
  
  &.active {
    background: var(--gzang-secondary);
    color: white;
  }
  
  &:active {
    transform: scale(0.95);
  }
}

// ================== Form Rows ==================
.form-rows {
  background: var(--gzang-surface);
  border-radius: var(--apple-radius-xl);
  overflow: hidden;
  box-shadow: var(--apple-shadow-sm);
}

.form-row {
  display: flex;
  align-items: center;
  padding: var(--apple-space-4);
  gap: var(--apple-space-3);
  transition: background-color var(--apple-duration-fast) var(--apple-ease-out);
  
  &:not(:last-child) {
    border-bottom: 0.5px solid var(--gzang-border);
  }
  
  &:active {
    background: var(--gzang-bg);
  }
}

.row-icon {
  width: 36px;
  height: 36px;
  border-radius: var(--apple-radius-sm);
  display: flex;
  align-items: center;
  justify-content: center;
}

.row-label {
  font-size: var(--apple-text-base);
  font-weight: var(--apple-font-medium);
  color: var(--gzang-text-primary);
  min-width: 60px;
}

.row-value {
  flex: 1;
  font-size: var(--apple-text-sm);
  color: var(--gzang-text-secondary);
  text-align: right;
  
  &.placeholder {
    color: var(--gzang-text-tertiary);
  }
}

// ================== Submit Bar ==================
.submit-bar {
  padding: var(--apple-space-4);
  padding-bottom: calc(var(--apple-space-4) + env(safe-area-inset-bottom));
  background: var(--gzang-surface);
  border-top: 0.5px solid var(--gzang-border);
}

.submit-btn {
  width: 100%;
  height: 52px;
  background: var(--gzang-secondary);
  border-radius: var(--apple-radius-lg);
  display: flex;
  align-items: center;
  justify-content: center;
  gap: var(--apple-space-2);
  color: white;
  font-size: var(--apple-text-base);
  font-weight: var(--apple-font-semibold);
  border: none;
  box-shadow: 0 4px 12px rgba(251, 139, 36, 0.3);
  transition: all var(--apple-duration-fast) var(--apple-ease-out);
  
  &.disabled {
    background: var(--gzang-border);
    box-shadow: none;
  }
  
  &:active:not(.disabled) {
    transform: scale(0.98);
  }
  
  .spin {
    animation: spin 1s linear infinite;
  }
}

.btn-amount {
  font-family: var(--font-mono);
  font-variant-numeric: tabular-nums;
  margin-left: var(--apple-space-2);
}

@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

// ================== Picker ==================
.picker-container {
  background: var(--gzang-surface);
  border-radius: var(--apple-radius-xl) var(--apple-radius-xl) 0 0;
  overflow: hidden;
}

.picker-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: var(--apple-space-4);
  border-bottom: 0.5px solid var(--gzang-border);
}

.picker-cancel,
.picker-confirm {
  font-size: var(--apple-text-base);
  font-weight: var(--apple-font-medium);
}

.picker-cancel {
  color: var(--gzang-text-secondary);
}

.picker-confirm {
  color: var(--gzang-secondary);
}

.picker-title {
  font-size: var(--apple-text-lg);
  font-weight: var(--apple-font-semibold);
  color: var(--gzang-text-primary);
}

.account-picker,
.book-picker {
  height: 200px;
}

// ================== Date Picker ==================
.date-picker-container {
  background: var(--gzang-surface);
  border-radius: var(--apple-radius-xl) var(--apple-radius-xl) 0 0;
  overflow: hidden;
  padding-bottom: env(safe-area-inset-bottom);
}

.date-picker-wrapper {
  position: relative;
  background: var(--gzang-surface);
}

.date-picker {
  height: 216px;
  width: 100%;
}

.date-picker picker-view-column {
  height: 216px;
}

.picker-item {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 36px;
  font-size: var(--apple-text-lg);
  color: var(--gzang-text-primary);
}

.picker-labels {
  display: flex;
  justify-content: space-around;
  padding: var(--apple-space-3) var(--apple-space-4);
  padding-bottom: calc(var(--apple-space-3) + env(safe-area-inset-bottom));
  background: var(--gzang-surface);
  border-top: 1px solid var(--gzang-border);
}

.picker-label {
  flex: 1;
  text-align: center;
  font-size: var(--apple-text-sm);
  color: var(--gzang-text-tertiary);
  font-weight: var(--apple-font-medium);
}

.quick-date-btns {
  display: flex;
  gap: var(--apple-space-3);
  padding: var(--apple-space-3) var(--apple-space-4);
  border-bottom: 0.5px solid var(--gzang-border);
}

.quick-date-btn {
  flex: 1;
  padding: var(--apple-space-2) var(--apple-space-3);
  text-align: center;
  background: var(--gzang-bg);
  border-radius: var(--apple-radius-sm);
  font-size: var(--apple-text-sm);
  font-weight: var(--apple-font-medium);
  color: var(--gzang-text-secondary);
  transition: all var(--apple-duration-fast) var(--apple-ease-out);

  &.active {
    background: var(--gzang-secondary);
    color: white;
  }
  
  &:active {
    opacity: 0.7;
  }
}

// ================== Dark Mode ==================
@media (prefers-color-scheme: dark) {
  .nav-bar,
  .page-content {
    background: var(--gzang-bg, #111827);
  }
  
  .amount-display {
    background: linear-gradient(135deg, var(--gzang-primary-dark, #0a3644) 0%, var(--gzang-primary, #0F4C5C) 100%);
  }
  
  .form-rows,
  .qa-btn,
  .book-selector {
    background: var(--gzang-surface, #1F2937);
  }
  
  .submit-bar {
    background: var(--gzang-surface, #1F2937);
    border-top-color: var(--gzang-border, #374151);
  }
  
  .date-picker-container {
    background: var(--gzang-surface, #1C1C1E);
  }
  
  .date-picker-wrapper {
    background: var(--gzang-surface, #1C1C1E);
  }
  
  .picker-labels {
    background: var(--gzang-surface, #1C1C1E);
    border-top-color: var(--gzang-border, #374151);
  }
  
  .picker-label {
    color: var(--gzang-text-tertiary, #8E8E93);
  }
  
  .quick-date-btn {
    background: var(--gzang-bg, #2C2C2E);
  }
}
</style>
