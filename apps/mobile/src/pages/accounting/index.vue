<template>
  <PageTransition>
  <view class="accounting-page apple-style">
    <!-- Header -->
    <view class="page-header">
      <!-- Header Nav -->
      <view class="header-nav">
        <view class="nav-left">
          <view class="book-selector" @click="showBookPicker">
            <text class="book-name">{{ currentBook?.name || t('accounting.selectBook') }}</text>
            <AppleIcon name="chevron-down" :size="12" color="var(--gzang-text-secondary)" />
          </view>
        </view>
      </view>

      <!-- Transaction Type Selector -->
      <TransactionTypeSelector v-model="form.type" @change="onTypeChange" />
    </view>

    <!-- Main Content -->
    <scroll-view class="main-content" scroll-y="true" :scroll-into-view="scrollIntoView" scroll-with-animation>

      <!-- Amount Section -->
      <view id="amount-section" class="amount-section">
        <view class="amount-display">
          <text class="currency">{{ currentCurrencySymbol }}</text>
          <input
            v-model="form.amount"
            type="digit"
            placeholder="0.00"
            class="amount-input"
            @focus="onAmountFocus"
            @blur="validateField('amount')"
          />
        </view>
        <text v-if="errors.amount" class="error-text">{{ errors.amount }}</text>
      </view>

      <!-- Quick Amounts -->
      <view class="quick-amounts">
        <view 
          v-for="amount in quickAmounts" 
          :key="amount"
          class="quick-amount-btn"
          :class="{ active: form.amount === String(amount) }"
          @click="setAmount(amount)"
        >
          {{ currentCurrencySymbol }}{{ amount }}
        </view>
        <view class="quick-amount-btn custom" @click="showKeyboard = true">
          <AppleIcon name="edit" :size="14" color="var(--gzang-text-secondary)" />
          <text>{{ t('accounting.custom') }}</text>
        </view>
      </view>

      <!-- Info Row: Date + Account -->
      <view class="info-row apple-list">
        <!-- Date -->
        <view class="info-item" @click="showDatePicker">
          <view class="info-left">
            <view class="info-icon" style="background: rgba(15, 76, 92, 0.1)">
              <AppleIcon name="calendar" :size="18" color="var(--gzang-primary)" />
            </view>
            <text class="info-label">{{ t('accounting.date') }}</text>
          </view>
          <view class="info-right">
            <text class="info-value">{{ formatDisplayDate(form.transactionDate) }}</text>
            <AppleIcon name="chevron-right" :size="14" color="var(--gzang-text-tertiary)" />
          </view>
        </view>

        <!-- Account -->
        <view class="info-item" @click="showAccountPicker = true">
          <view class="info-left">
            <view class="info-icon" style="background: rgba(6, 214, 160, 0.1)">
              <AppleIcon name="wallet" :size="18" color="var(--gzang-success)" />
            </view>
            <text class="info-label">{{ t('accounting.account') }}</text>
          </view>
          <view class="info-right">
            <text class="info-value" :class="{ placeholder: !selectedAccount }">
              {{ selectedAccount || t('accounting.selectAccount') }}
            </text>
            <AppleIcon name="chevron-right" :size="14" color="var(--gzang-text-tertiary)" />
          </view>
        </view>

        <!-- Target Account (Transfer only) -->
        <view v-if="form.type === 3" class="info-item" @click="showTargetAccountPicker = true">
          <view class="info-left">
            <view class="info-icon" style="background: rgba(251, 139, 36, 0.1)">
              <AppleIcon name="transfer" :size="18" color="var(--gzang-secondary)" />
            </view>
            <text class="info-label">{{ t('accounting.targetAccount') }}</text>
          </view>
          <view class="info-right">
            <text class="info-value" :class="{ placeholder: !selectedTargetAccount }">
              {{ selectedTargetAccount || t('accounting.selectTargetAccount') }}
            </text>
            <AppleIcon name="chevron-right" :size="14" color="var(--gzang-text-tertiary)" />
          </view>
        </view>
      </view>

      <!-- Category Selection -->
      <view class="category-section">
        <text class="section-title">{{ t('accounting.category') }}</text>
        <CategoryGrid
          v-model="form.categoryId"
          :expense-categories="expenseCategories"
          :income-categories="incomeCategories"
          :currency-symbol="currentCurrencySymbol"
          :show-tabs="form.type !== 3"
          :show-amount="true"
          :error="errors.categoryId"
          @change="onCategoryChange"
        />
      </view>

      <!-- Remark -->
      <view class="remark-section apple-list">
        <view class="remark-item">
          <view class="remark-icon">
            <AppleIcon name="edit" :size="18" color="var(--gzang-text-secondary)" />
          </view>
          <textarea
            v-model="form.remark"
            :placeholder="t('accounting.notePlaceholder')"
            class="remark-input"
            :maxlength="200"
            auto-height
          />
        </view>
        <view class="remark-count">
          <text>{{ form.remark.length }}/200</text>
        </view>
      </view>

      <!-- Bottom Safe Area Spacer -->
      <view class="bottom-spacer"></view>
    </scroll-view>

    <!-- Fixed Action Bar -->
    <view class="action-bar">
      <button 
        class="action-btn secondary" 
        @click="resetForm"
      >
        <AppleIcon name="refresh" :size="16" color="var(--gzang-text-secondary)" />
        <text>{{ t('common.reset') }}</text>
      </button>
      <button 
        class="action-btn primary"
        :class="{ loading: submitting, disabled: !isFormValid }"
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
          <text class="picker-cancel" @click="hideAccountPicker">{{ t('common.cancel') }}</text>
          <text class="picker-title">{{ t('accounting.selectAccount') }}</text>
          <text class="picker-confirm" @click="confirmAccount">{{ t('common.confirm') }}</text>
        </view>
        <picker-view :value="accountPickerValue" @change="onAccountChange" class="account-picker">
          <picker-view-column>
            <view v-for="account in accounts" :key="account.id" class="picker-item">
              {{ account.name }} ({{ currentCurrencySymbol }}{{ (account.balance || 0).toFixed(2) }})
            </view>
          </picker-view-column>
        </picker-view>
      </view>
    </uni-popup>

    <!-- Target Account Picker Popup -->
    <uni-popup ref="targetAccountPopup" type="bottom" :is-mask-click="true">
      <view class="picker-container">
        <view class="picker-header">
          <text class="picker-cancel" @click="hideTargetAccountPicker">{{ t('common.cancel') }}</text>
          <text class="picker-title">{{ t('accounting.selectTargetAccount') }}</text>
          <text class="picker-confirm" @click="confirmTargetAccount">{{ t('common.confirm') }}</text>
        </view>
        <picker-view :value="targetAccountPickerValue" @change="onTargetAccountChange" class="account-picker">
          <picker-view-column>
            <view 
              v-for="account in targetAccounts" 
              :key="account.id" 
              class="picker-item"
              :class="{ disabled: account.id === form.accountId }"
            >
              {{ account.name }} ({{ currentCurrencySymbol }}{{ (account.balance || 0).toFixed(2) }})
            </view>
          </picker-view-column>
        </picker-view>
      </view>
    </uni-popup>

    <!-- Date Picker Popup -->
    <uni-popup ref="datePopup" type="bottom" :is-mask-click="true">
      <view class="date-picker-container">
        <view class="picker-header">
          <text class="picker-cancel" @click="hideDatePicker">{{ t('common.cancel') }}</text>
          <text class="picker-title">{{ t('accounting.selectDate') }}</text>
          <text class="picker-confirm" @click="confirmDatePicker">{{ t('common.confirm') }}</text>
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
          <picker-view :value="datePickerValue" @change="onDateChange" class="date-picker">
            <picker-view-column>
              <view v-for="year in years" :key="year" class="picker-item">{{ year }}</view>
            </picker-view-column>
            <picker-view-column>
              <view v-for="month in months" :key="month" class="picker-item">{{ String(month).padStart(2, '0') }}</view>
            </picker-view-column>
            <picker-view-column>
              <view v-for="day in days" :key="day" class="picker-item">{{ String(day).padStart(2, '0') }}</view>
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
          <text class="picker-cancel" @click="hideBookPicker">{{ t('common.cancel') }}</text>
          <text class="picker-title">{{ t('book.switchBook') }}</text>
          <text class="picker-confirm" @click="confirmBookPicker">{{ t('common.confirm') }}</text>
        </view>
        <picker-view :value="bookPickerValue" @change="onBookChange" class="book-picker">
          <picker-view-column>
            <view v-for="book in books" :key="book.id" class="picker-item">
              {{ book.icon || '📒' }} {{ book.name }}
            </view>
          </picker-view-column>
        </picker-view>
      </view>
    </uni-popup>

    <!-- Amount Keyboard Popup -->
    <uni-popup ref="keyboardPopup" type="bottom" :is-mask-click="true">
      <view class="keyboard-popup-content">
        <AmountKeyboard
          v-model="form.amount"
          :currency-symbol="currentCurrencySymbol"
          :quick-amounts="quickAmounts"
          @confirm="hideKeyboard"
          @cancel="hideKeyboard"
        />
      </view>
    </uni-popup>

    <!-- Custom TabBar -->
    <CustomTabBar />
  </view>
  </PageTransition>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { useI18n } from 'vue-i18n'
import { useAccountingStore } from '@/stores/accounting'
import { useBookStore } from '@/stores/book'
import { useAppStore } from '@/stores/app'
import PageTransition from '@/components/common/PageTransition/index.vue'
import AppleIcon from '@/components/common/AppleIcon/index.vue'
import TransactionTypeSelector from '@/components/business/TransactionTypeSelector/index.vue'
import CategoryGrid from '@/components/business/CategoryGrid/index.vue'
import AmountKeyboard from '@/components/common/AmountKeyboard/index.vue'
import CustomTabBar from '@/components/CustomTabBar/index.vue'
import type { Category } from '@/components/business/CategoryGrid/index.vue'

const { t } = useI18n()

// Stores
const accountingStore = useAccountingStore()
const bookStore = useBookStore()
const appStore = useAppStore()

// Refs
const submitting = ref(false)
const showAccountPicker = ref(false)
const showTargetAccountPicker = ref(false)
const showKeyboard = ref(false)
const bookPickerValue = ref([0])
const accountPickerValue = ref([0])
const targetAccountPickerValue = ref([0])
const datePickerValue = ref([0, 0, 0])
const scrollIntoView = ref('')

// Popup refs
const accountPopup = ref<any>(null)
const targetAccountPopup = ref<any>(null)
const bookPopup = ref<any>(null)
const datePopup = ref<any>(null)
const keyboardPopup = ref<any>(null)

// Form data
const form = reactive({
  type: 2 as 1 | 2 | 3, // 2: expense, 1: income, 3: transfer
  amount: '',
  categoryId: undefined as number | undefined,
  accountId: 0,
  targetAccountId: 0,
  transactionDate: '',
  transactionTime: '',
  remark: ''
})

// Errors
const errors = reactive({
  amount: '',
  categoryId: '',
  accountId: '',
  targetAccountId: ''
})

// Quick amounts
const quickAmounts = [10, 50, 100, 200, 500]

// Computed
const currentBook = computed(() => bookStore.currentBook)
const books = computed(() => bookStore.books)
const currentCurrencySymbol = computed(() => bookStore.currentCurrencySymbol)
const accounts = computed(() => accountingStore.accounts)

// Filter out the source account for target account selection
const targetAccounts = computed(() => {
  return accounts.value.filter(a => a.id !== form.accountId)
})

const selectedAccount = computed(() => {
  const account = accounts.value.find(a => a.id === form.accountId)
  return account?.name || ''
})

const selectedTargetAccount = computed(() => {
  const account = accounts.value.find(a => a.id === form.targetAccountId)
  return account?.name || ''
})

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
  const hasAccount = form.accountId && form.accountId > 0
  const hasTarget = form.type !== 3 || (form.targetAccountId && form.targetAccountId > 0)
  return hasAmount && hasCategory && hasAccount && hasTarget
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

// Date picker data
const years = computed(() => {
  const current = new Date().getFullYear()
  return Array.from({ length: 10 }, (_, i) => current - 5 + i)
})

const months = computed(() => Array.from({ length: 12 }, (_, i) => i + 1))

// 修复：当月份切换时，确保天数不超过该月最大天数
const getDaysInMonth = (year: number, month: number) => {
  return new Date(year, month, 0).getDate()
}

const days = computed(() => {
  const year = years.value[datePickerValue.value[0]] || new Date().getFullYear()
  const month = months.value[datePickerValue.value[1]] || 1
  return Array.from({ length: getDaysInMonth(year, month) }, (_, i) => i + 1)
})

// Methods
const formatDate = (date: Date) => {
  const y = date.getFullYear()
  const m = String(date.getMonth() + 1).padStart(2, '0')
  const d = String(date.getDate()).padStart(2, '0')
  return `${y}-${m}-${d}`
}

const formatDisplayDate = (dateStr: string) => {
  if (!dateStr) return t('accounting.selectDate')
  const today = formatDate(new Date())
  const yesterday = formatDate(new Date(Date.now() - 86400000))
  if (dateStr === today) return t('datetime.today')
  if (dateStr === yesterday) return t('datetime.yesterday')
  return dateStr.replace(/-/g, '/')
}

// Event handlers
const onTypeChange = (type: 1 | 2 | 3) => {
  form.type = type
  form.categoryId = undefined
  form.targetAccountId = 0
  errors.categoryId = ''
  errors.targetAccountId = ''
}

const onCategoryChange = (category: Category) => {
  form.categoryId = category.id
  errors.categoryId = ''
}

const onAmountFocus = () => {
  scrollIntoView.value = 'amount-section'
}

const setAmount = (amount: number) => {
  form.amount = String(amount)
  validateField('amount')
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
    validateField('accountId')
  }
  hideAccountPicker()
}

const hideAccountPicker = () => {
  accountPopup.value?.close()
}

// Target Account picker
const onTargetAccountChange = (e: any) => {
  targetAccountPickerValue.value = e.detail.value
}

const confirmTargetAccount = () => {
  const index = targetAccountPickerValue.value[0]
  const account = targetAccounts.value[index]
  if (account) {
    form.targetAccountId = account.id
    validateField('targetAccountId')
  }
  hideTargetAccountPicker()
}

const hideTargetAccountPicker = () => {
  targetAccountPopup.value?.close()
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

const confirmBookPicker = () => {
  const index = bookPickerValue.value[0]
  const book = books.value[index]
  if (book) {
    bookStore.switchBook(book.id)
  }
  hideBookPicker()
}

const hideBookPicker = () => {
  bookPopup.value?.close()
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

const selectQuickDate = (date: string) => {
  form.transactionDate = date
}

const confirmDatePicker = () => {
  const year = years.value[datePickerValue.value[0]] || new Date().getFullYear()
  const month = months.value[datePickerValue.value[1]] || 1
  const day = days.value[datePickerValue.value[2]] || 1
  form.transactionDate = `${year}-${String(month).padStart(2, '0')}-${String(day).padStart(2, '0')}`
  form.transactionTime = `${form.transactionDate} ${String(new Date().getHours()).padStart(2, '0')}:${String(new Date().getMinutes()).padStart(2, '0')}:00`
  hideDatePicker()
}

const hideDatePicker = () => {
  datePopup.value?.close()
}

// Keyboard
const showAmountKeyboard = () => {
  showKeyboard.value = true
  keyboardPopup.value?.open()
}

const hideKeyboard = () => {
  showKeyboard.value = false
  keyboardPopup.value?.close()
}

// Validation
const validateField = (field: keyof typeof errors) => {
  switch (field) {
    case 'amount':
      if (!form.amount) errors.amount = t('accounting.pleaseEnterAmount')
      else if (parseFloat(form.amount) <= 0) errors.amount = '金额必须大于0'
      else errors.amount = ''
      break
    case 'categoryId':
      if (!form.categoryId) errors.categoryId = t('accounting.pleaseSelectCategory')
      else errors.categoryId = ''
      break
    case 'accountId':
      if (!form.accountId) errors.accountId = t('accounting.pleaseSelectAccount')
      else errors.accountId = ''
      break
    case 'targetAccountId':
      if (form.type === 3 && !form.targetAccountId) {
        errors.targetAccountId = t('accounting.pleaseSelectTargetAccount')
      } else {
        errors.targetAccountId = ''
      }
      break
  }
}

const validateForm = () => {
  validateField('amount')
  validateField('categoryId')
  validateField('accountId')
  validateField('targetAccountId')
  return !errors.amount && !errors.categoryId && !errors.accountId && !errors.targetAccountId
}

// Reset
const resetForm = () => {
  form.amount = ''
  form.categoryId = undefined
  form.accountId = 0
  form.targetAccountId = 0
  form.remark = ''
  Object.keys(errors).forEach(key => {
    errors[key as keyof typeof errors] = ''
  })
}

// Submit
const handleSubmit = async () => {
  if (!validateForm()) {
    // Scroll to first error
    if (errors.amount) {
      scrollIntoView.value = 'amount-section'
    }
    return
  }

  try {
    submitting.value = true
    const transactionData = {
      type: form.type,
      amount: parseFloat(form.amount),
      categoryId: form.categoryId!,
      accountId: form.accountId,
      targetAccountId: form.type === 3 ? form.targetAccountId : undefined,
      transactionTime: form.transactionTime || new Date().toISOString(),
      remark: form.remark,
      bookId: currentBook.value?.id
    }
    await accountingStore.createTransaction(transactionData as any)
    appStore.showSuccess(t('accounting.recordSuccess'))
    resetForm()
    setTimeout(() => {
      uni.navigateBack()
    }, 1500)
  } catch (error: any) {
    appStore.showError(error.message || t('accounting.recordFailed'))
  } finally {
    submitting.value = false
  }
}

// Lifecycle
onMounted(async () => {
  const today = new Date()
  form.transactionDate = formatDate(today)
  form.transactionTime = today.toISOString()

  await Promise.all([
    accountingStore.loadCategories(),
    accountingStore.loadAccounts(),
    bookStore.loadBooks()
  ])
})
</script>

<style lang="scss" scoped>
.accounting-page {
  min-height: 100vh;
  background: var(--gzang-bg);
  display: flex;
  flex-direction: column;
}

// ================== Header ==================
.page-header {
  background: var(--gzang-bg);
  padding: var(--apple-space-4);
  padding-top: calc(constant(safe-area-inset-top) + var(--apple-space-3));
}

.header-nav {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--apple-space-4);
}

.nav-left {
  flex: 1;
}

.book-selector {
  display: inline-flex;
  align-items: center;
  gap: var(--apple-space-2);
  padding: var(--apple-space-2) var(--apple-space-3);
  background: var(--gzang-surface);
  border-radius: var(--apple-radius-full);
  box-shadow: var(--apple-shadow-xs);
  
  .book-name {
    font-size: var(--apple-text-sm);
    font-weight: var(--apple-font-medium);
    color: var(--gzang-text-primary);
  }
}

// ================== Main Content ==================
.main-content {
  flex: 1;
  padding: 0 var(--apple-space-4);
}

// ================== Amount Section ==================
.amount-section {
  text-align: center;
  padding: var(--apple-space-6) 0;
}

.amount-display {
  display: flex;
  align-items: baseline;
  justify-content: center;
}

.currency {
  font-size: var(--apple-text-2xl);
  font-weight: var(--apple-font-semibold);
  color: var(--gzang-primary);
  margin-right: var(--apple-space-1);
}

.amount-input {
  font-size: 56px;
  font-weight: var(--apple-font-bold);
  color: var(--gzang-text-primary);
  text-align: center;
  background: transparent;
  border: none;
  outline: none;
  font-family: var(--font-mono);
  font-variant-numeric: tabular-nums;
  letter-spacing: -1px;
  min-width: 200px;
  
  &::placeholder {
    color: var(--gzang-text-tertiary);
  }
}

// ================== Quick Amounts ==================
.quick-amounts {
  display: flex;
  gap: var(--apple-space-2);
  margin-bottom: var(--apple-space-5);
  overflow-x: auto;
  -webkit-overflow-scrolling: touch;
  
  &::-webkit-scrollbar {
    display: none;
  }
}

.quick-amount-btn {
  flex-shrink: 0;
  padding: var(--apple-space-2) var(--apple-space-4);
  background: var(--gzang-surface);
  border-radius: var(--apple-radius-full);
  font-size: var(--apple-text-sm);
  font-weight: var(--apple-font-medium);
  color: var(--gzang-text-secondary);
  box-shadow: var(--apple-shadow-xs);
  transition: all var(--apple-duration-fast) var(--apple-ease-out);
  
  &.active {
    background: var(--gzang-secondary);
    color: white;
  }
  
  &.custom {
    display: flex;
    align-items: center;
    gap: var(--apple-space-2);
    background: var(--gzang-surface);
    border: 1.5rpx dashed var(--gzang-border);
  }
  
  &:active {
    transform: scale(0.95);
  }
}

// ================== Info Row (Apple List Style) ==================
.info-row {
  margin-bottom: var(--apple-space-4);
  border-radius: var(--apple-radius-xl);
  overflow: hidden;
}

.info-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: var(--apple-space-4);
  background: var(--gzang-surface);
  transition: background-color var(--apple-duration-fast) var(--apple-ease-out);
  
  &:not(:last-child) {
    border-bottom: 0.5px solid var(--gzang-border);
  }
  
  &:active {
    background: var(--gzang-bg);
  }
}

.info-left {
  display: flex;
  align-items: center;
  gap: var(--apple-space-3);
}

.info-icon {
  width: 36px;
  height: 36px;
  border-radius: var(--apple-radius-sm);
  display: flex;
  align-items: center;
  justify-content: center;
}

.info-label {
  font-size: var(--apple-text-base);
  color: var(--gzang-text-primary);
  font-weight: var(--apple-font-medium);
}

.info-right {
  display: flex;
  align-items: center;
  gap: var(--apple-space-2);
}

.info-value {
  font-size: var(--apple-text-sm);
  color: var(--gzang-text-secondary);
  
  &.placeholder {
    color: var(--gzang-text-tertiary);
  }
}

// ================== Category Section ==================
.category-section {
  margin-bottom: var(--apple-space-5);
}

.section-title {
  font-size: var(--apple-text-lg);
  font-weight: var(--apple-font-semibold);
  color: var(--gzang-text-primary);
  display: block;
  margin-bottom: var(--apple-space-3);
}

// ================== Remark Section ==================
.remark-section {
  border-radius: var(--apple-radius-xl);
  overflow: hidden;
  margin-bottom: var(--apple-space-5);
}

.remark-item {
  display: flex;
  padding: var(--apple-space-4);
  background: var(--gzang-surface);
}

.remark-icon {
  margin-right: var(--apple-space-3);
  margin-top: 2px;
}

.remark-input {
  flex: 1;
  font-size: var(--apple-text-base);
  color: var(--gzang-text-primary);
  background: transparent;
  border: none;
  outline: none;
  min-height: 60px;
  
  &::placeholder {
    color: var(--gzang-text-tertiary);
  }
}

.remark-count {
  padding: 0 var(--apple-space-4) var(--apple-space-3);
  background: var(--gzang-surface);
  text-align: right;
  
  text {
    font-size: var(--apple-text-xs);
    color: var(--gzang-text-tertiary);
  }
}

// ================== Bottom Spacer ==================
.bottom-spacer {
  height: 140px;
}

// ================== Action Bar ==================
.action-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  display: flex;
  gap: var(--apple-space-3);
  padding: var(--apple-space-4);
  padding-bottom: calc(var(--apple-space-4) + env(safe-area-inset-bottom) + 84px);
  background: var(--gzang-surface);
  border-top: 0.5px solid var(--gzang-border);
}

.action-btn {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: var(--apple-space-2);
  height: 52px;
  border: none;
  border-radius: var(--apple-radius-lg);
  font-size: var(--apple-text-base);
  font-weight: var(--apple-font-semibold);
  transition: all var(--apple-duration-fast) var(--apple-ease-out);
  
  &.secondary {
    background: var(--gzang-bg);
    color: var(--gzang-text-secondary);
    
    &:active {
      background: var(--gzang-border);
    }
  }
  
  &.primary {
    background: var(--gzang-secondary);
    color: white;
    box-shadow: 0 4px 12px rgba(251, 139, 36, 0.3);
    
    &.disabled {
      background: var(--gzang-border);
      box-shadow: none;
    }
    
    &:active:not(.disabled) {
      transform: scale(0.98);
    }
  }
  
  .spin {
    animation: apple-spin 1s linear infinite;
  }
}

.btn-amount {
  margin-left: var(--apple-space-2);
  font-family: var(--font-mono);
  font-variant-numeric: tabular-nums;
}

@keyframes apple-spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

// ================== Error Text ==================
.error-text {
  font-size: var(--apple-text-xs);
  color: var(--gzang-danger);
  margin-top: var(--apple-space-2);
  display: block;
  text-align: center;
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

// ================== Keyboard Popup ==================
.keyboard-popup-content {
  background: var(--gzang-surface);
  border-radius: var(--apple-radius-xl) var(--apple-radius-xl) 0 0;
}

// ================== Dark Mode ==================
@media (prefers-color-scheme: dark) {
  .page-header,
  .main-content {
    background: var(--gzang-bg, #000000);
  }
  
  .amount-input {
    color: var(--gzang-text-primary, #FFFFFF);
  }
  
  .info-item,
  .remark-item,
  .remark-count,
  .quick-amount-btn {
    background: var(--gzang-surface, #1C1C1E);
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
