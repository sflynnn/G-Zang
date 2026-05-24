<template>
  <view class="category-record-page">
    <!-- 自定义导航栏 -->
    <view class="page-header">
      <view class="header-nav">
        <view class="back-btn" @click="goBack">
          <uni-icons type="left" size="24" color="var(--gzang-text-primary)" />
        </view>
        <view class="category-info">
          <text class="category-icon">{{ currentCategory?.icon || '📂' }}</text>
          <text class="category-name">{{ currentCategory?.name || '选择分类' }}</text>
        </view>
        <view class="header-right"></view>
      </view>
    </view>

    <scroll-view class="page-content" scroll-y="true">
      <!-- 金额显示区域 -->
      <view class="amount-section">
        <text class="currency-symbol">{{ currencySymbol }}</text>
        <text class="amount-value">{{ displayAmount }}</text>
      </view>

      <!-- 预算进度（如果有设置预算） -->
      <view v-if="categoryBudget" class="budget-section">
        <BudgetProgress
          :budget="categoryBudget.budget"
          :spent="categoryBudget.spent"
          :remaining="categoryBudget.remaining"
          :show-detail="true"
          :show-warning-line="true"
          :warning-threshold="categoryBudget.warningThreshold || 80"
        />
      </view>

      <!-- 二级分类选择 -->
      <view class="sub-category-section">
        <view class="section-header">
          <text class="section-title">选择子分类</text>
          <text v-if="subCategories.length > 0" class="section-hint">
            {{ selectedSubCategory ? '已选' : '请选择' }}
          </text>
        </view>
        <SubCategoryGrid
          v-model="form.subCategoryId"
          :categories="subCategories"
          @change="onSubCategoryChange"
        />
      </view>

      <!-- 表单区域 -->
      <view class="form-section">
        <!-- 标签选择 -->
        <view class="form-item">
          <TagSelector
            v-model="form.tags"
            :show-header="true"
            :show-add-button="true"
            :max-tags="5"
            hint="最多添加5个标签"
            @change="onTagsChange"
          />
        </view>

        <!-- 备注 -->
        <view class="form-item remark-item">
          <view class="form-label">
            <uni-icons type="compose" size="18" color="var(--gzang-text-secondary)" />
            <text class="label-text">备注</text>
          </view>
          <textarea
            v-model="form.remark"
            placeholder="添加备注信息..."
            class="remark-input"
            :maxlength="200"
            auto-height
          />
          <text class="remark-count">{{ form.remark.length }}/200</text>
        </view>

        <!-- 支付方式 -->
        <view class="form-item" @click="showPaymentMethodPicker">
          <view class="form-label">
            <uni-icons type="wallet" size="18" color="var(--gzang-text-secondary)" />
            <text class="label-text">支付方式</text>
          </view>
          <view class="form-value">
            <text v-if="selectedPaymentMethod" class="payment-method">
              <text>{{ selectedPaymentMethod.icon }}</text>
              <text class="payment-name">{{ selectedPaymentMethod.methodName }}</text>
            </text>
            <text v-else class="placeholder">请选择</text>
            <uni-icons type="right" size="14" color="var(--gzang-text-tertiary)" />
          </view>
        </view>

        <!-- 账户选择 -->
        <view class="form-item" @click="showAccountPicker()">
          <view class="form-label">
            <uni-icons type="wallet" size="18" color="var(--gzang-text-secondary)" />
            <text class="label-text">账户</text>
          </view>
          <view class="form-value">
            <text v-if="selectedAccount" class="account-name">{{ selectedAccount.name }}</text>
            <text v-else class="placeholder">请选择账户</text>
            <uni-icons type="right" size="14" color="var(--gzang-text-tertiary)" />
          </view>
        </view>

        <!-- 日期选择 -->
        <view class="form-item" @click="showDatePicker()">
          <view class="form-label">
            <uni-icons type="calendar" size="18" color="var(--gzang-text-secondary)" />
            <text class="label-text">日期</text>
          </view>
          <view class="form-value">
            <text class="date-text">{{ formatDisplayDate }}</text>
            <uni-icons type="right" size="14" color="var(--gzang-text-tertiary)" />
          </view>
        </view>
      </view>

      <!-- 底部占位 -->
      <view class="bottom-spacer"></view>
    </scroll-view>

    <!-- 账户选择弹窗 -->
    <uni-popup ref="accountPopup" type="bottom">
      <view class="picker-container">
        <view class="picker-header">
          <text class="picker-cancel" @click="closeAccountPicker">取消</text>
          <text class="picker-title">选择账户</text>
          <text class="picker-confirm" @click="confirmAccount">确定</text>
        </view>
        <picker-view :value="accountPickerValue" @change="onAccountChange" class="picker-view">
          <picker-view-column>
            <view v-for="account in accounts" :key="account.id" class="picker-item">
              <text>{{ account.name }}</text>
              <text class="picker-balance">{{ currencySymbol }}{{ (account.balance || 0).toFixed(2) }}</text>
            </view>
          </picker-view-column>
        </picker-view>
      </view>
    </uni-popup>

    <!-- 支付方式选择弹窗 -->
    <uni-popup ref="paymentPopup" type="bottom">
      <view class="picker-container">
        <view class="picker-header">
          <text class="picker-cancel" @click="closePaymentMethodPicker">取消</text>
          <text class="picker-title">选择支付方式</text>
          <text class="picker-confirm" @click="confirmPaymentMethod">确定</text>
        </view>
        <picker-view :value="paymentPickerValue" @change="onPaymentMethodChange" class="picker-view">
          <picker-view-column>
            <view v-for="method in paymentMethods" :key="method.id" class="picker-item">
              <text>{{ method.icon }} {{ method.methodName }}</text>
            </view>
          </picker-view-column>
        </picker-view>
      </view>
    </uni-popup>

    <!-- 日期选择弹窗 -->
    <uni-popup ref="datePopup" type="bottom">
      <view class="picker-container date-picker-container">
        <view class="picker-header">
          <text class="picker-cancel" @click="closeDatePicker">取消</text>
          <text class="picker-title">选择日期</text>
          <text class="picker-confirm" @click="confirmDate">确定</text>
        </view>
        <view class="quick-date-btns">
          <view
            v-for="quick in quickDates"
            :key="quick.key"
            class="quick-date-btn"
            :class="{ active: form.date === quick.date }"
            @click="selectQuickDate(quick.date)"
          >
            {{ quick.label }}
          </view>
        </view>
        <picker-view :value="datePickerValue" @change="onDateChange" class="picker-view">
          <picker-view-column>
            <view v-for="year in years" :key="year" class="picker-item">{{ year }}年</view>
          </picker-view-column>
          <picker-view-column>
            <view v-for="month in months" :key="month" class="picker-item">{{ String(month).padStart(2, '0') }}月</view>
          </picker-view-column>
          <picker-view-column>
            <view v-for="day in days" :key="day" class="picker-item">{{ String(day).padStart(2, '0') }}日</view>
          </picker-view-column>
        </picker-view>
      </view>
    </uni-popup>
  </view>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { useBookStore } from '@/stores/book'
import { useAccountingStore } from '@/stores/accounting'
import { useAppStore } from '@/stores/app'
import SubCategoryGrid from '@/components/business/SubCategoryGrid/index.vue'
import TagSelector from '@/components/business/TagSelector/index.vue'
import BudgetProgress from '@/components/business/BudgetProgress/index.vue'

// 接口定义
interface Category {
  id: number
  name: string
  icon: string
  color: string
  parentId?: number
  children?: Category[]
  budget?: {
    budget: number
    spent: number
    remaining: number
    warningThreshold?: number
  }
}

interface PaymentMethod {
  id: number
  methodCode: string
  methodName: string
  icon: string
  color: string
}

// Store
const bookStore = useBookStore()
const accountingStore = useAccountingStore()
const appStore = useAppStore()

// 页面参数
const categoryId = ref<number>(0)
const categoryType = ref<number>(2) // 1=收入, 2=支出

// 弹窗 refs
const accountPopup = ref<any>(null)
const paymentPopup = ref<any>(null)
const datePopup = ref<any>(null)

// 弹窗控制函数（避免模板中的直接赋值）
const showAccountPicker = () => {
  if (!form.accountId && accounts.value.length > 0) {
    form.accountId = accounts.value[0].id
  }
  const currentIndex = accounts.value.findIndex(a => a.id === form.accountId)
  accountPickerValue.value = currentIndex >= 0 ? [currentIndex] : [0]
  accountPopup.value?.open()
}

const showDatePicker = () => {
  const date = form.date ? new Date(form.date) : new Date()
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

// 选择器数据
const accountPickerValue = ref([0])
const paymentPickerValue = ref([0])
const datePickerValue = ref([0, 0, 0])

// 表单数据
const form = reactive({
  amount: '',
  subCategoryId: undefined as number | undefined,
  tags: [] as string[],
  remark: '',
  paymentMethodId: undefined as number | undefined,
  accountId: undefined as number | undefined,
  date: '',
  time: ''
})

// 常用金额
// 计算属性
const currencySymbol = computed(() => bookStore.currentCurrencySymbol)
const currentBook = computed(() => bookStore.currentBook)

const currentCategory = computed((): Category | null => {
  return (accountingStore.categoriesWithChildren.find(c => c.id === categoryId.value) as Category | undefined) || null
})

const subCategories = computed((): Category[] => {
  if (!currentCategory.value?.children) return []
  return currentCategory.value.children.map(c => ({
    id: c.id,
    name: (c as any).name || (c as any).categoryName || '',
    icon: (c as any).icon || '📂',
    color: (c as any).color || '#6B7280',
    parentId: (c as any).parentId
  }))
})

const categoryBudget = computed(() => currentCategory.value?.budget || null)

const selectedSubCategory = computed(() => {
  if (!form.subCategoryId) return null
  return subCategories.value.find(c => c.id === form.subCategoryId)
})

const accounts = computed(() => accountingStore.accounts || [])

const selectedAccount = computed(() => {
  if (!form.accountId) return null
  return accounts.value.find(a => a.id === form.accountId)
})

const paymentMethods = computed((): PaymentMethod[] => [
  { id: 1, methodCode: 'cash', methodName: '现金', icon: '💵', color: '#4CAF50' },
  { id: 2, methodCode: 'wx', methodName: '微信支付', icon: '💚', color: '#07C160' },
  { id: 3, methodCode: 'alipay', methodName: '支付宝', icon: '💙', color: '#1677FF' },
  { id: 4, methodCode: 'card', methodName: '银行卡', icon: '💳', color: '#6366F1' },
  { id: 5, methodCode: 'other', methodName: '其他', icon: '💰', color: '#6B7280' }
])

const selectedPaymentMethod = computed(() => {
  if (!form.paymentMethodId) return null
  return paymentMethods.value.find(p => p.id === form.paymentMethodId)
})

const displayAmount = computed(() => {
  if (!form.amount) return '0.00'
  return parseFloat(form.amount).toFixed(2)
})

const formatDisplayDate = computed(() => {
  if (!form.date) return '选择日期'
  const today = formatDate(new Date())
  const yesterday = formatDate(new Date(Date.now() - 86400000))
  if (form.date === today) return `今天 ${form.time}`
  if (form.date === yesterday) return `昨天 ${form.time}`
  return `${form.date.replace(/-/g, '/')} ${form.time}`
})

// 日期选择器数据
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

// 快捷日期
const quickDates = computed(() => {
  const today = new Date()
  const yesterday = new Date(today)
  yesterday.setDate(yesterday.getDate() - 1)
  return [
    { key: 'today', label: '今天', date: formatDate(today) },
    { key: 'yesterday', label: '昨天', date: formatDate(yesterday) }
  ]
})

// 表单验证
const isFormValid = computed(() => {
  const hasAmount = form.amount && parseFloat(form.amount) > 0
  const hasAccount = form.accountId && form.accountId > 0
  return hasAmount && hasAccount
})

// 方法
const formatDate = (date: Date) => {
  const y = date.getFullYear()
  const m = String(date.getMonth() + 1).padStart(2, '0')
  const d = String(date.getDate()).padStart(2, '0')
  return `${y}-${m}-${d}`
}

// 事件处理
const onSubCategoryChange = (subCategory: Category) => {
  form.subCategoryId = subCategory.id
}

const onTagsChange = (tags: string[]) => {
  form.tags = tags
}

// 返回
const goBack = () => {
  uni.navigateBack()
}

// 账户选择
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

// 支付方式选择
const showPaymentMethodPicker = () => {
  if (!form.paymentMethodId && paymentMethods.value.length > 0) {
    form.paymentMethodId = paymentMethods.value[0].id
  }
  const currentIndex = paymentMethods.value.findIndex(p => p.id === form.paymentMethodId)
  paymentPickerValue.value = currentIndex >= 0 ? [currentIndex] : [0]
  paymentPopup.value?.open()
}

const onPaymentMethodChange = (e: any) => {
  paymentPickerValue.value = e.detail.value
}

const confirmPaymentMethod = () => {
  const index = paymentPickerValue.value[0]
  const method = paymentMethods.value[index]
  if (method) {
    form.paymentMethodId = method.id
  }
  closePaymentMethodPicker()
}

const closePaymentMethodPicker = () => {
  paymentPopup.value?.close()
}

// 日期选择
const onDateChange = (e: any) => {
  datePickerValue.value = e.detail.value
}

const selectQuickDate = (date: string) => {
  form.date = date
}

const confirmDate = () => {
  const year = years.value[datePickerValue.value[0]] || new Date().getFullYear()
  const month = months.value[datePickerValue.value[1]] || 1
  const day = days.value[datePickerValue.value[2]] || 1
  form.date = `${year}-${String(month).padStart(2, '0')}-${String(day).padStart(2, '0')}`
  closeDatePicker()
}

const closeDatePicker = () => {
  datePopup.value?.close()
}

// 提交
const handleSubmit = async () => {
  if (!isFormValid.value) {
    if (!form.amount) {
      uni.showToast({ title: '请输入金额', icon: 'none' })
    } else if (!form.accountId) {
      uni.showToast({ title: '请选择账户', icon: 'none' })
    }
    return
  }

  try {
    const transactionTime = form.date 
      ? `${form.date} ${form.time || '00:00'}:00`
      : new Date().toISOString()

    const paymentMethod = selectedPaymentMethod.value?.methodCode || 'other'

    await accountingStore.createTransaction({
      type: categoryType.value,
      amount: parseFloat(form.amount),
      categoryId: form.subCategoryId || categoryId.value,
      accountId: form.accountId!,
      transactionTime,
      remark: form.remark,
      tags: form.tags,
      paymentMethod
    })

    appStore.showSuccess('记账成功')
    
    setTimeout(() => {
      uni.navigateBack()
    }, 1500)
  } catch (error: any) {
    appStore.showError(error.message || '记账失败')
  }
}

// 生命周期
onMounted(async () => {
  // 从页面参数获取分类信息
  const pages = getCurrentPages()
  const currentPage = pages[pages.length - 1] as any
  const options = currentPage?.options || {}
  
  if (options.categoryId) {
    categoryId.value = parseInt(options.categoryId)
  }
  if (options.type) {
    categoryType.value = parseInt(options.type)
  }

  // 初始化日期时间
  const now = new Date()
  form.date = formatDate(now)
  form.time = `${String(now.getHours()).padStart(2, '0')}:${String(now.getMinutes()).padStart(2, '0')}`

  // 加载数据
  await Promise.all([
    accountingStore.loadCategoriesWithChildren(currentBook.value?.id),
    accountingStore.loadAccounts()
  ])
})
</script>

<style lang="scss" scoped>
.category-record-page {
  min-height: 100vh;
  background: var(--gzang-bg);
  display: flex;
  flex-direction: column;
}

// 导航栏
.page-header {
  background: var(--gzang-surface);
  padding-top: calc(env(safe-area-inset-top) + 12rpx);
}

.header-nav {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16rpx 24rpx;
}

.back-btn {
  width: 64rpx;
  height: 64rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  
  &:active {
    opacity: 0.6;
  }
}

.category-info {
  display: flex;
  align-items: center;
  gap: 12rpx;
}

.category-icon {
  font-size: 36rpx;
}

.category-name {
  font-size: var(--apple-text-lg);
  font-weight: var(--apple-font-semibold);
  color: var(--gzang-text-primary);
}

.header-right {
  width: 64rpx;
}

// 内容区域
.page-content {
  flex: 1;
  padding: 32rpx 24rpx;
}

// 金额区域
.amount-section {
  display: flex;
  align-items: baseline;
  justify-content: center;
  padding: 48rpx 0;
}

.currency-symbol {
  font-size: 36rpx;
  font-weight: var(--apple-font-semibold);
  color: var(--gzang-primary);
  margin-right: 8rpx;
}

.amount-value {
  font-size: 72rpx;
  font-weight: var(--apple-font-bold);
  color: var(--gzang-text-primary);
  font-family: var(--font-mono);
  font-variant-numeric: tabular-nums;
  letter-spacing: -2px;
}

// 预算区域
.budget-section {
  margin-bottom: 32rpx;
}

// 子分类区域
.sub-category-section {
  margin-bottom: 32rpx;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20rpx;
}

.section-title {
  font-size: var(--apple-text-base);
  font-weight: var(--apple-font-semibold);
  color: var(--gzang-text-primary);
}

.section-hint {
  font-size: var(--apple-text-xs);
  color: var(--gzang-text-tertiary);
}

// 表单区域
.form-section {
  background: var(--gzang-surface);
  border-radius: var(--apple-radius-xl);
  overflow: hidden;
}

.form-item {
  padding: 24rpx;
  border-bottom: 1px solid var(--gzang-border);
  
  &:last-child {
    border-bottom: none;
  }
}

.form-label {
  display: flex;
  align-items: center;
  gap: 12rpx;
  margin-bottom: 12rpx;
}

.label-text {
  font-size: var(--apple-text-sm);
  color: var(--gzang-text-secondary);
}

.form-value {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 8rpx;
}

.placeholder {
  font-size: var(--apple-text-base);
  color: var(--gzang-text-tertiary);
}

.payment-method {
  display: flex;
  align-items: center;
  gap: 8rpx;
}

.payment-name {
  font-size: var(--apple-text-base);
  color: var(--gzang-text-primary);
}

.account-name {
  font-size: var(--apple-text-base);
  color: var(--gzang-text-primary);
}

.date-text {
  font-size: var(--apple-text-base);
  color: var(--gzang-text-primary);
}

.remark-item {
  position: relative;
}

.remark-input {
  width: 100%;
  min-height: 80rpx;
  font-size: var(--apple-text-base);
  color: var(--gzang-text-primary);
  background: transparent;
  border: none;
  outline: none;
  resize: none;

  &::placeholder {
    color: var(--gzang-text-tertiary);
  }
}

.remark-count {
  position: absolute;
  right: 24rpx;
  bottom: 12rpx;
  font-size: var(--apple-text-xs);
  color: var(--gzang-text-tertiary);
}

.bottom-spacer {
  height: 400rpx;
}

// 选弹窗
.picker-container {
  background: var(--gzang-surface);
  border-radius: var(--apple-radius-xl) var(--apple-radius-xl) 0 0;
  overflow: hidden;
}

.date-picker-container {
  padding-bottom: env(safe-area-inset-bottom);
}

.picker-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 24rpx 32rpx;
  border-bottom: 1px solid var(--gzang-border);
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

.picker-view {
  height: 400rpx;
}

.picker-item {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 80rpx;
  font-size: var(--apple-text-base);
  color: var(--gzang-text-primary);
}

.picker-balance {
  margin-left: 16rpx;
  font-size: var(--apple-text-xs);
  color: var(--gzang-text-tertiary);
}

.quick-date-btns {
  display: flex;
  gap: 16rpx;
  padding: 16rpx 24rpx;
  border-bottom: 1px solid var(--gzang-border);
}

.quick-date-btn {
  flex: 1;
  padding: 12rpx;
  text-align: center;
  background: var(--gzang-bg);
  border-radius: var(--apple-radius-sm);
  font-size: var(--apple-text-sm);
  color: var(--gzang-text-secondary);

  &.active {
    background: var(--gzang-secondary);
    color: white;
  }
}
</style>
