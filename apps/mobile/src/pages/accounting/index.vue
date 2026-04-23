<template>
  <view class="accounting-page apple-style">
    <!-- Header with Segmented Control -->
    <view class="page-header">
      <view class="header-nav">
        <view class="nav-left">
          <view class="book-selector" @click="showBookPicker">
            <text class="book-name">{{ currentBook?.name || t('accounting.selectCategory') }}</text>
            <AppleIcon name="chevron-down" :size="12" color="var(--gzang-text-secondary)" />
          </view>
        </view>
      </view>
      
      <!-- Segmented Control - Type Selector -->
      <view class="segmented-control">
        <view 
          v-for="type in transactionTypes" 
          :key="type.key"
          class="segment-item"
          :class="{ active: form.type === type.key }"
          @click="switchType(type.key)"
        >
          <AppleIcon 
            :name="type.icon" 
            :size="16" 
            :color="form.type === type.key ? 'white' : 'text-secondary'" 
          />
          <text class="segment-label">{{ t('accounting.' + type.i18nKey) }}</text>
        </view>
      </view>
    </view>

    <!-- Main Content -->
    <scroll-view class="main-content" scroll-y="true">
      <!-- Amount Display - Large & Center -->
      <view class="amount-section">
        <view class="amount-display">
          <text class="currency">{{ currentCurrencySymbol }}</text>
          <input
            v-model="form.amount"
            type="digit"
            placeholder="0.00"
            class="amount-input"
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
          @click="setAmount(amount)"
        >
          {{ currentCurrencySymbol }}{{ amount }}
        </view>
      </view>

      <!-- Info Row -->
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
            <text class="info-label">{{ t('accounting.selectTargetAccount') }}</text>
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
        <view class="category-tabs">
          <view 
            class="category-tab"
            :class="{ active: activeTab === 1 }"
            @click="activeTab = 1"
          >
            <AppleIcon name="income" :size="14" :color="activeTab === 1 ? 'var(--gzang-secondary)' : 'var(--gzang-text-secondary)'" />
            <text>{{ t('accounting.income') }}</text>
          </view>
          <view 
            class="category-tab"
            :class="{ active: activeTab === 2 }"
            @click="activeTab = 2"
          >
            <AppleIcon name="expense" :size="14" :color="activeTab === 2 ? 'var(--gzang-secondary)' : 'var(--gzang-text-secondary)'" />
            <text>{{ t('accounting.expense') }}</text>
          </view>
        </view>
        
        <view class="category-grid">
          <view 
            v-for="category in currentCategories"
            :key="category.id"
            class="category-item"
            :class="{ selected: form.categoryId === category.id }"
            @click="selectCategory(category)"
          >
            <view class="category-icon" :style="{ background: category.color + '20' }">
              <AppleIcon :name="category.icon" :size="20" :color="category.color" />
            </view>
            <text class="category-name">{{ category.name }}</text>
            <text class="category-amount" v-if="category.amount">{{ formatAmount(category.amount) }}</text>
          </view>
        </view>
        <text v-if="errors.categoryId" class="error-text">{{ errors.categoryId }}</text>
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
      <!-- Bottom Action Bar -->
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
          :class="{ loading: submitting }"
          :disabled="!isFormValid || submitting"
          @click="handleSubmit"
        >
          <AppleIcon v-if="submitting" name="refresh" :size="16" color="white" class="spin" />
          <text v-else>{{ t('common.confirm') }}</text>
        </button>
      </view>
      <!-- Bottom Safe Area -->
      <view class="bottom-safe-area"></view>
    </scroll-view>

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

    <!-- Date Picker Popup -->
    <uni-popup ref="datePopup" type="bottom" :is-mask-click="true">
      <view class="picker-container">
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
        <picker-view :value="datePickerValue" @change="onDateChange" class="date-picker">
          <picker-view-column>
            <view v-for="year in years" :key="year" class="picker-item">{{ year }}年</view>
          </picker-view-column>
          <picker-view-column>
            <view v-for="month in months" :key="month" class="picker-item">{{ month }}月</view>
          </picker-view-column>
          <picker-view-column>
            <view v-for="day in days" :key="day" class="picker-item">{{ day }}日</view>
          </picker-view-column>
        </picker-view>
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
              {{ book.icon }} {{ book.name }}
            </view>
          </picker-view-column>
        </picker-view>
      </view>
    </uni-popup>

    <!-- 自定义 TabBar -->
    <CustomTabBar />
  </view>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { useI18n } from 'vue-i18n'
import { useAccountingStore } from '@/stores/accounting'
import { useBookStore } from '@/stores/book'
import { useAppStore } from '@/stores/app'
import { DeviceUtils } from '@/utils/device'
import AppleIcon from '@/components/common/AppleIcon/index.vue'
import CustomTabBar from '@/components/CustomTabBar/index.vue'

const { t } = useI18n()

// Stores
const accountingStore = useAccountingStore()
const bookStore = useBookStore()
const appStore = useAppStore()

// Refs
const submitting = ref(false)
const activeTab = ref<1 | 2>(2)
const showAccountPicker = ref(false)
const showTargetAccountPicker = ref(false)
const bookPickerValue = ref([0])
const accountPickerValue = ref([0])
const datePickerValue = ref([0, 0, 0])

// Popup refs
const accountPopup = ref<any>(null)
const bookPopup = ref<any>(null)
const datePopup = ref<any>(null)

// Form data
const form = reactive({
  type: 2 as 1 | 2 | 3,
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

// Transaction types
const transactionTypes = [
  { key: 1 as const, i18nKey: 'income', icon: 'income' },
  { key: 2 as const, i18nKey: 'expense', icon: 'expense' },
  { key: 3 as const, i18nKey: 'transfer', icon: 'transfer' }
]

// Quick amounts
const quickAmounts = [10, 50, 100, 200, 500, 1000]

// Computed
const currentBook = computed(() => bookStore.currentBook)
const books = computed(() => bookStore.books)
const currentCurrencySymbol = computed(() => bookStore.currentCurrencySymbol)
const accounts = computed(() => accountingStore.accounts)

const selectedAccount = computed(() => {
  const account = accounts.value.find(a => a.id === form.accountId)
  return account?.name || ''
})

const selectedTargetAccount = computed(() => {
  const account = accounts.value.find(a => a.id === form.targetAccountId)
  return account?.name || ''
})

const currentCategories = computed(() => {
  return getCategories(activeTab.value)
})

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

const days = computed(() => {
  const year = years.value[datePickerValue.value[0]] || new Date().getFullYear()
  const month = months.value[datePickerValue.value[1]] || new Date().getMonth() + 1
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

const getDaysInMonth = (year: number, month: number) => {
  return new Date(year, month, 0).getDate()
}

const switchType = (type: 1 | 2 | 3) => {
  form.type = type
  form.categoryId = undefined
  form.targetAccountId = 0
  if (type === 1) activeTab.value = 1
  else activeTab.value = 2
  validateForm()
}

const setAmount = (amount: number) => {
  form.amount = amount.toString()
  validateField('amount')
}

const selectCategory = (category: any) => {
  form.categoryId = category.id
  validateField('categoryId')
}

const formatAmount = (amount: number) => {
  return currentCurrencySymbol.value + amount.toFixed(0)
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
  datePickerValue.value = e.detail.value
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
  }
}

const validateForm = () => {
  validateField('amount')
  validateField('categoryId')
  validateField('accountId')
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
  if (!isFormValid.value) return

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

// Mock categories
const getCategories = (type: 1 | 2) => {
  if (type === 1) {
    return [
      { id: 101, name: '工资', icon: 'income', color: '#06D6A0', amount: 15000 },
      { id: 102, name: '奖金', icon: 'star', color: '#FFD166', amount: 3000 },
      { id: 103, name: '兼职', icon: 'clock', color: '#118AB2', amount: 2000 },
      { id: 104, name: '理财', icon: 'chart', color: '#3A86FF', amount: 500 },
      { id: 105, name: '礼金', icon: 'heart', color: '#EF476F', amount: 1000 },
      { id: 106, name: '退款', icon: 'refresh', color: '#9B59B6', amount: 0 },
    ]
  }
  return [
    { id: 201, name: '餐饮', icon: 'food', color: '#FB8B24', amount: 1234.5 },
    { id: 202, name: '交通', icon: 'transport', color: '#0F4C5C', amount: 456 },
    { id: 203, name: '购物', icon: 'shopping', color: '#EF476F', amount: 789 },
    { id: 204, name: '娱乐', icon: 'entertainment', color: '#9B59B6', amount: 300 },
    { id: 205, name: '居住', icon: 'housing', color: '#06D6A0', amount: 2500 },
    { id: 206, name: '医疗', icon: 'medical', color: '#EF476F', amount: 0 },
    { id: 207, name: '教育', icon: 'education', color: '#118AB2', amount: 500 },
    { id: 208, name: '通讯', icon: 'communication', color: '#FFD166', amount: 100 },
    { id: 209, name: '服饰', icon: 'shopping', color: '#3A86FF', amount: 0 },
    { id: 210, name: '日用', icon: 'list', color: '#6B7280', amount: 0 },
  ]
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

// ================== Segmented Control ==================
.segmented-control {
  display: flex;
  background: var(--gzang-surface);
  border-radius: var(--apple-radius-lg);
  padding: 4px;
  box-shadow: var(--apple-shadow-xs);
}

.segment-item {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: var(--apple-space-2);
  padding: var(--apple-space-3);
  border-radius: calc(var(--apple-radius-lg) - 4px);
  transition: all var(--apple-duration-fast) var(--apple-ease-out);
  
  .segment-label {
    font-size: var(--apple-text-sm);
    font-weight: var(--apple-font-medium);
    color: var(--gzang-text-secondary);
  }
  
  &.active {
    background: var(--gzang-secondary);
    box-shadow: 0 2px 8px rgba(251, 139, 36, 0.3);
    
    .segment-label {
      color: white;
    }
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
  letter-spacing: -1px;
  min-width: 200px;
  
  &::placeholder {
    color: var(--gzang-text-tertiary);
  }
}

// ================== Quick Amounts ==================
.quick-amounts {
  display: flex;
  flex-wrap: wrap;
  gap: var(--apple-space-2);
  justify-content: center;
  margin-bottom: var(--apple-space-5);
}

.quick-amount-btn {
  padding: var(--apple-space-2) var(--apple-space-4);
  background: var(--gzang-surface);
  border-radius: var(--apple-radius-full);
  font-size: var(--apple-text-sm);
  font-weight: var(--apple-font-medium);
  color: var(--gzang-text-secondary);
  box-shadow: var(--apple-shadow-xs);
  transition: all var(--apple-duration-fast) var(--apple-ease-out);
  
  &:active {
    transform: scale(0.95);
    background: var(--gzang-secondary);
    color: white;
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

.category-tabs {
  display: flex;
  gap: var(--apple-space-2);
  margin-bottom: var(--apple-space-4);
}

.category-tab {
  display: flex;
  align-items: center;
  gap: var(--apple-space-2);
  padding: var(--apple-space-2) var(--apple-space-4);
  background: var(--gzang-surface);
  border-radius: var(--apple-radius-full);
  font-size: var(--apple-text-sm);
  color: var(--gzang-text-secondary);
  box-shadow: var(--apple-shadow-xs);
  transition: all var(--apple-duration-fast) var(--apple-ease-out);
  
  &.active {
    background: var(--gzang-secondary);
    color: white;
  }
}

.category-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: var(--apple-space-3);
}

.category-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: var(--apple-space-3);
  background: var(--gzang-surface);
  border-radius: var(--apple-radius-lg);
  box-shadow: var(--apple-shadow-xs);
  transition: all var(--apple-duration-fast) var(--apple-ease-out);
  
  &.selected {
    background: var(--gzang-secondary);
    box-shadow: 0 4px 12px rgba(251, 139, 36, 0.3);
    
    .category-name,
    .category-amount {
      color: white;
    }
    
    .category-icon {
      background: rgba(255, 255, 255, 0.2) !important;
    }
  }
  
  &:active {
    transform: scale(0.95);
  }
}

.category-icon {
  width: 48px;
  height: 48px;
  border-radius: var(--apple-radius-md);
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: var(--apple-space-2);
}

.category-name {
  font-size: var(--apple-text-xs);
  color: var(--gzang-text-primary);
  font-weight: var(--apple-font-medium);
  text-align: center;
}

.category-amount {
  font-size: 10px;
  color: var(--gzang-text-secondary);
  font-family: var(--font-mono);
  margin-top: 2px;
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

// ================== Action Bar ==================
.action-bar {
  display: flex;
  gap: var(--apple-space-3);
  padding: var(--apple-space-4);
  padding-bottom: calc(var(--apple-space-4) + env(safe-area-inset-bottom));
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
    
    &:disabled {
      background: var(--gzang-border);
      box-shadow: none;
    }
    
    &:active:not(:disabled) {
      transform: scale(0.98);
    }
  }
  
  .spin {
    animation: apple-spin 1s linear infinite;
  }
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

.picker-item {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 44px;
  font-size: var(--apple-text-base);
  color: var(--gzang-text-primary);
}

.account-picker,
.book-picker,
.date-picker {
  height: 200px;
}

.quick-date-btns {
  display: flex;
  gap: var(--apple-space-3);
  padding: var(--apple-space-3) var(--apple-space-4);
  border-bottom: 0.5px solid var(--gzang-border);
}

.quick-date-btn {
  flex: 1;
  padding: var(--apple-space-2);
  text-align: center;
  background: var(--gzang-bg);
  border-radius: var(--apple-radius-sm);
  font-size: var(--apple-text-sm);
  color: var(--gzang-text-secondary);
  transition: all var(--apple-duration-fast) var(--apple-ease-out);
  
  &.active {
    background: var(--gzang-secondary);
    color: white;
  }
}

// ================== Bottom Safe Area ==================
.bottom-safe-area {
  height: calc(var(--apple-space-4) + 84px);
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
  .category-item,
  .remark-item,
  .remark-count,
  .segment-item:not(.active),
  .book-selector,
  .quick-amount-btn {
    background: var(--gzang-surface, #1C1C1E);
  }
}

[data-theme="dark"] {
  .page-header,
  .main-content {
    background: var(--gzang-bg, #000000);
  }
  
  .amount-input {
    color: var(--gzang-text-primary, #FFFFFF);
  }
}
</style>
