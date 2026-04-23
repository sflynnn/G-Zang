<template>
  <view class="manual-page">
    <!-- 顶部导航 -->
    <view class="nav-bar">
      <view class="nav-left" @click="goBack">
        <uni-icons type="left" size="20" color="#333"></uni-icons>
      </view>
      <view class="nav-title">手动记账</view>
      <view class="nav-right"></view>
    </view>

    <scroll-view class="page-content" scroll-y="true">
      <!-- 交易类型 -->
      <view class="section">
        <text class="section-title">交易类型</text>
        <view class="type-selector">
          <view
            class="type-item expense"
            :class="{ active: form.type === 2 }"
            @click="form.type = 2"
          >
            <text class="type-icon">💸</text>
            <text class="type-name">支出</text>
          </view>
          <view
            class="type-item income"
            :class="{ active: form.type === 1 }"
            @click="form.type = 1"
          >
            <text class="type-icon">💰</text>
            <text class="type-name">收入</text>
          </view>
          <view
            class="type-item transfer"
            :class="{ active: form.type === 3 }"
            @click="form.type = 3"
          >
            <text class="type-icon">🔄</text>
            <text class="type-name">转账</text>
          </view>
        </view>
      </view>

      <!-- 金额输入 -->
      <view class="section">
        <text class="section-title">金额</text>
        <view class="amount-input-card">
          <view class="amount-input-row">
            <text class="currency-symbol">{{ currentCurrencySymbol }}</text>
            <input
              v-model="form.amount"
              type="digit"
              placeholder="0.00"
              class="amount-input"
              @input="onAmountInput"
            />
          </view>
          <view class="quick-amounts">
            <view
              v-for="amount in quickAmounts"
              :key="amount"
              class="qa-btn"
              @click="setQuickAmount(amount)"
            >
              {{ currentCurrencySymbol }}{{ amount }}
            </view>
          </view>
        </view>
      </view>

      <!-- 分类选择 -->
      <view class="section">
        <text class="section-title">分类</text>
        <view class="category-selector" @click="showCategoryPicker">
          <view v-if="selectedCategory" class="selected-category">
            <view
              class="cat-icon"
              :style="{ background: categoryBgColor }"
            >
              <text class="cat-emoji">{{ selectedCategory.icon || '📂' }}</text>
            </view>
            <text class="cat-name">{{ selectedCategory.name }}</text>
          </view>
          <view v-else class="select-placeholder">
            <text>请选择分类</text>
          </view>
          <text class="select-arrow">›</text>
        </view>
      </view>

      <!-- 账户选择 -->
      <view class="section">
        <text class="section-title">{{ form.type === 3 ? '转出账户' : '支付账户' }}</text>
        <view class="account-selector" @click="showAccountPicker()">
          <view v-if="selectedAccount" class="selected-account">
            <text class="account-icon">{{ getAccountIcon(selectedAccount) }}</text>
            <view class="account-info">
              <text class="account-name">{{ selectedAccount.name }}</text>
              <text class="account-balance">
                {{ currentCurrencySymbol }}{{ selectedAccount.balance?.toFixed(2) }}
              </text>
            </view>
          </view>
          <view v-else class="select-placeholder">
            <text>请选择账户</text>
          </view>
          <text class="select-arrow">›</text>
        </view>
      </view>

      <!-- 转入账户（仅转账时显示） -->
      <view v-if="form.type === 3" class="section">
        <text class="section-title">转入账户</text>
        <view class="account-selector" @click="showTargetAccountPicker()">
          <view v-if="selectedTargetAccount" class="selected-account">
            <text class="account-icon">{{ getAccountIcon(selectedTargetAccount) }}</text>
            <view class="account-info">
              <text class="account-name">{{ selectedTargetAccount.name }}</text>
              <text class="account-balance">
                {{ currentCurrencySymbol }}{{ selectedTargetAccount.balance?.toFixed(2) }}
              </text>
            </view>
          </view>
          <view v-else class="select-placeholder">
            <text>请选择转入账户</text>
          </view>
          <text class="select-arrow">›</text>
        </view>
      </view>

      <!-- 日期时间 -->
      <view class="section">
        <text class="section-title">日期时间</text>
        <view class="datetime-selector" @click="showDatePicker">
          <view class="datetime-icon">📅</view>
          <view class="datetime-value">
            <text class="date-text">{{ formatDisplayDate }}</text>
            <text class="time-text">{{ form.time }}</text>
          </view>
          <text class="select-arrow">›</text>
        </view>
      </view>

      <!-- 备注 -->
      <view class="section">
        <text class="section-title">备注</text>
        <view class="remark-card">
          <textarea
            v-model="form.remark"
            placeholder="添加备注信息（可选）"
            class="remark-input"
            :maxlength="200"
            auto-height
          ></textarea>
          <view class="remark-count">
            <text>{{ form.remark.length }}/200</text>
          </view>
        </view>
      </view>

      <!-- 标签 -->
      <view class="section">
        <text class="section-title">标签</text>
        <view class="tags-section">
          <view class="tags-grid">
            <view
              v-for="tag in availableTags"
              :key="tag"
              class="tag-item"
              :class="{ selected: form.tags.includes(tag) }"
              @click="toggleTag(tag)"
            >
              <text>{{ tag }}</text>
            </view>
          </view>
          <view class="add-tag-btn" @click="showTagInput = true">
            <text>+ 添加标签</text>
          </view>
        </view>
      </view>

      <!-- 附件 -->
      <view class="section">
        <text class="section-title">附件</text>
        <view class="attachment-section">
          <view class="attachment-list">
            <view
              v-for="(file, index) in form.attachments"
              :key="index"
              class="attachment-item"
            >
              <image
                v-if="isImage(file)"
                :src="file"
                class="attachment-image"
                mode="aspectFill"
              ></image>
              <view class="attachment-remove" @click="removeAttachment(index)">
                <text>×</text>
              </view>
            </view>
          </view>
          <view class="add-attachment-btn" @click="chooseAttachment">
            <text class="add-icon">+</text>
            <text class="add-text">添加图片</text>
          </view>
        </view>
      </view>

      <!-- 底部安全区域 -->
      <view class="bottom-safe"></view>
    </scroll-view>

    <!-- 底部提交栏 -->
    <view class="submit-bar">
      <view class="submit-info">
        <text class="info-label">共 {{ form.type === 3 ? '转账' : (form.type === 1 ? '收入' : '支出') }}</text>
        <text class="info-amount" :class="amountColorClass">
          {{ currentCurrencySymbol }}{{ form.amount || '0.00' }}
        </text>
      </view>
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

    <!-- 分类选择弹窗 -->
    <uni-popup ref="categoryPopup" type="bottom" :is-mask-click="true">
      <view class="picker-container">
        <view class="picker-header">
          <text class="picker-cancel" @click="closeCategoryPicker">取消</text>
          <text class="picker-title">选择分类</text>
          <text class="picker-confirm" @click="confirmCategory">确定</text>
        </view>

        <!-- 类型切换 -->
        <view class="category-type-tabs">
          <view
            class="ct-tab"
            :class="{ active: categoryType === 2 }"
            @click="categoryType = 2"
          >
            <text>支出</text>
          </view>
          <view
            class="ct-tab"
            :class="{ active: categoryType === 1 }"
            @click="categoryType = 1"
          >
            <text>收入</text>
          </view>
        </view>

        <picker-view
          :value="categoryPickerValue"
          @change="onCategoryChange"
          class="category-picker"
        >
          <picker-view-column>
            <view v-for="cat in displayCategories" :key="cat.id" class="picker-item">
              {{ cat.icon || '📂' }} {{ cat.name }}
            </view>
          </picker-view-column>
        </picker-view>
      </view>
    </uni-popup>

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
            <view v-for="account in accounts" :key="account.id" class="picker-item">
              {{ getAccountIcon(account) }} {{ account.name }}
            </view>
          </picker-view-column>
        </picker-view>
      </view>
    </uni-popup>

    <!-- 目标账户选择弹窗 -->
    <uni-popup ref="targetAccountPopup" type="bottom" :is-mask-click="true">
      <view class="picker-container">
        <view class="picker-header">
          <text class="picker-cancel" @click="closeTargetAccountPicker">取消</text>
          <text class="picker-title">选择转入账户</text>
          <text class="picker-confirm" @click="confirmTargetAccount">确定</text>
        </view>
        <picker-view
          :value="targetAccountPickerValue"
          @change="onTargetAccountChange"
          class="account-picker"
        >
          <picker-view-column>
            <view
              v-for="account in targetAccounts"
              :key="account.id"
              class="picker-item"
            >
              {{ getAccountIcon(account) }} {{ account.name }}
            </view>
          </picker-view-column>
        </picker-view>
      </view>
    </uni-popup>

    <!-- 日期时间选择弹窗 -->
    <uni-popup ref="datePopup" type="bottom" :is-mask-click="true">
      <view class="picker-container">
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

    <!-- 添加标签弹窗 -->
    <uni-popup ref="tagPopup" type="bottom" :is-mask-click="true">
      <view class="tag-input-popup">
        <view class="popup-header">
          <text class="popup-cancel" @click="closeTagPopup">取消</text>
          <text class="popup-title">添加标签</text>
          <text class="popup-confirm" @click="confirmTag">确定</text>
        </view>
        <view class="tag-input-section">
          <input
            v-model="newTag"
            type="text"
            placeholder="输入标签名称"
            class="tag-input"
            maxlength="20"
          />
        </view>
      </view>
    </uni-popup>
  </view>
</template>

<script setup lang="ts">
import { ref, reactive, computed, watch, onMounted } from 'vue'
import { useBookStore } from '@/stores/book'
import { useTransactionStore } from '@/stores/transaction'
import { useCategoryStore } from '@/stores/category'
import { useAccountStore } from '@/stores/account'
import { formatDate as formatDateStr } from '@/utils/date'

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
const bookStore = useBookStore()
const transactionStore = useTransactionStore()
const categoryStore = useCategoryStore()
const accountStore = useAccountStore()

// 响应式数据
const submitting = ref(false)
const showTagInput = ref(false)
const newTag = ref('')
const categoryType = ref(2)

// 弹窗 refs
const categoryPopup = ref<any>(null)
const accountPopup = ref<any>(null)
const targetAccountPopup = ref<any>(null)
const datePopup = ref<any>(null)
const tagPopup = ref<any>(null)

// 选择器数据
const categoryPickerValue = ref([0])
const accountPickerValue = ref([0])
const targetAccountPickerValue = ref([0])
const datePickerValue = ref([0, 0, 0])

// 表单数据
const form = reactive({
  type: 2 as 1 | 2 | 3,
  amount: '',
  categoryId: 0,
  accountId: 0,
  targetAccountId: 0,
  date: formatDateStr(new Date()),
  time: new Date().toTimeString().slice(0, 5),
  remark: '',
  tags: [] as string[],
  attachments: [] as string[]
})

// 可用标签
const availableTags = ['餐饮', '交通', '购物', '娱乐', '日常', '办公', '医疗', '教育']

// 常用金额
const quickAmounts = [10, 50, 100, 200, 500, 1000]

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

// 快捷日期
const quickDates = computed(() => {
  const today = new Date()
  const yesterday = new Date(today)
  yesterday.setDate(yesterday.getDate() - 1)
  const weekStart = new Date(today)
  weekStart.setDate(today.getDate() - today.getDay())

  return [
    { key: 'today', label: '今天', date: formatDateStr(today) },
    { key: 'yesterday', label: '昨天', date: formatDateStr(yesterday) },
    { key: 'week', label: '本周初', date: formatDateStr(weekStart) },
  ]
})

// 计算属性
const currentCurrencySymbol = computed(() => bookStore.currentCurrencySymbol)
const accounts = computed(() => accountStore.accountList)
const allCategories = computed(() => categoryStore.categoryList)

const displayCategories = computed(() => {
  return allCategories.value.filter(c => c.type === categoryType.value)
})

const selectedCategory = computed(() => {
  if (!form.categoryId) return null
  return allCategories.value.find(c => c.id === form.categoryId)
})

const selectedAccount = computed(() => {
  if (!form.accountId) return null
  return accounts.value.find(a => a.id === form.accountId)
})

const selectedTargetAccount = computed(() => {
  if (!form.targetAccountId) return null
  return accounts.value.find(a => a.id === form.targetAccountId)
})

const targetAccounts = computed(() => {
  return accounts.value.filter(a => a.id !== form.accountId)
})

const categoryBgColor = computed(() => {
  const icon = selectedCategory.value?.icon
  return icon ? (CATEGORY_COLORS[icon] || '#F8F9FA') : '#F8F9FA'
})

const formatDisplayDate = computed(() => {
  const today = formatDateStr(new Date())
  const yesterday = formatDateStr(new Date(Date.now() - 86400000))
  if (form.date === today) return '今天'
  if (form.date === yesterday) return '昨天'
  return form.date.replace(/-/g, '/')
})

const amountColorClass = computed(() => {
  if (form.type === 1) return 'income'
  if (form.type === 2) return 'expense'
  return ''
})

const isFormValid = computed(() => {
  const hasAmount = form.amount && parseFloat(form.amount) > 0
  const hasCategory = form.categoryId > 0
  const hasAccount = form.accountId > 0
  const hasTarget = form.type !== 3 || (form.targetAccountId > 0 && form.targetAccountId !== form.accountId)
  return hasAmount && hasCategory && hasAccount && hasTarget
})

// 方法

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

const onAmountInput = () => {
  form.amount = form.amount.replace(/[^\d.]/g, '')
  const parts = form.amount.split('.')
  if (parts.length > 2) {
    form.amount = parts[0] + '.' + parts.slice(1).join('')
  }
  if (parts[1] && parts[1].length > 2) {
    form.amount = parts[0] + '.' + parts[1].slice(0, 2)
  }
}

const setQuickAmount = (amount: number) => {
  form.amount = amount.toString()
}

// 监听交易类型变化，重置分类
watch(() => form.type, (newType) => {
  form.categoryId = 0
  categoryType.value = newType === 3 ? 2 : (newType as 1 | 2)
  form.targetAccountId = 0
})

// 分类选择
const showCategoryPicker = () => {
  // 设置当前类型对应的分类
  categoryType.value = form.type === 3 ? 2 : (form.type as 1 | 2)
  const currentIndex = displayCategories.value.findIndex(c => c.id === form.categoryId)
  categoryPickerValue.value = currentIndex >= 0 ? [currentIndex] : [0]
  categoryPopup.value.open()
}

const onCategoryChange = (e: any) => {
  categoryPickerValue.value = e.detail.value
}

const confirmCategory = () => {
  const index = categoryPickerValue.value[0]
  const category = displayCategories.value[index]
  if (category) {
    form.categoryId = category.id
  }
  closeCategoryPicker()
}

const closeCategoryPicker = () => {
  categoryPopup.value.close()
}

// 账户选择
const showAccountPicker = () => {
  const currentIndex = accounts.value.findIndex(a => a.id === form.accountId)
  accountPickerValue.value = currentIndex >= 0 ? [currentIndex] : [0]
  accountPopup.value.open()
}

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

// 目标账户选择
const showTargetAccountPicker = () => {
  const currentIndex = targetAccounts.value.findIndex(a => a.id === form.targetAccountId)
  targetAccountPickerValue.value = currentIndex >= 0 ? [currentIndex] : [0]
  targetAccountPopup.value.open()
}

const onTargetAccountChange = (e: any) => {
  targetAccountPickerValue.value = e.detail.value
}

const confirmTargetAccount = () => {
  const index = targetAccountPickerValue.value[0]
  const account = targetAccounts.value[index]
  if (account) {
    form.targetAccountId = account.id
  }
  closeTargetAccountPicker()
}

const closeTargetAccountPicker = () => {
  targetAccountPopup.value.close()
}

// 日期选择
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
  datePopup.value.open()
}

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
  datePopup.value.close()
}

// 标签操作
const toggleTag = (tag: string) => {
  const index = form.tags.indexOf(tag)
  if (index > -1) {
    form.tags.splice(index, 1)
  } else {
    if (form.tags.length < 5) {
      form.tags.push(tag)
    } else {
      uni.showToast({ title: '最多添加5个标签', icon: 'none' })
    }
  }
}

const closeTagPopup = () => {
  tagPopup.value.close()
  newTag.value = ''
}

const confirmTag = () => {
  if (newTag.value.trim()) {
    if (form.tags.length < 5) {
      if (!form.tags.includes(newTag.value.trim())) {
        form.tags.push(newTag.value.trim())
      }
    } else {
      uni.showToast({ title: '最多添加5个标签', icon: 'none' })
    }
  }
  closeTagPopup()
}

// 附件操作
const isImage = (file: string) => {
  return /\.(jpg|jpeg|png|gif|webp)$/i.test(file)
}

const chooseAttachment = () => {
  uni.chooseImage({
    count: 3 - form.attachments.length,
    success: (res) => {
      form.attachments.push(...res.tempFilePaths)
    }
  })
}

const removeAttachment = (index: number) => {
  form.attachments.splice(index, 1)
}

// 提交表单
const handleSubmit = async () => {
  if (!isFormValid.value) return

  try {
    submitting.value = true

    const transactionTime = `${form.date} ${form.time}:00`

    await transactionStore.createTransaction({
      type: form.type as 1 | 2,
      amount: parseFloat(form.amount),
      categoryId: form.categoryId,
      accountId: form.accountId,
      targetAccountId: form.type === 3 ? form.targetAccountId : undefined,
      transactionTime,
      remark: form.remark,
      tags: form.tags.length > 0 ? form.tags : undefined
    })

    uni.showToast({
      title: '记账成功',
      icon: 'success'
    })

    setTimeout(() => {
      uni.navigateBack()
    }, 1500)

  } catch (error: any) {
    // ignore
  } finally {
    submitting.value = false
  }
}

// 返回
const goBack = () => {
  uni.navigateBack()
}

// 生命周期
onMounted(async () => {
  // 加载数据
  await Promise.all([
    bookStore.fetchBooks(),
    categoryStore.fetchCategories(),
    accountStore.fetchAccounts()
  ])

  // 设置默认账户
  if (accounts.value.length > 0) {
    form.accountId = accounts.value[0].id
  }
})
</script>

<style lang="scss" scoped>
.manual-page {
  height: 100vh;
  background-color: #F8F9FA;
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

.page-content {
  flex: 1;
  padding: 16px;
}

// 区块
.section {
  margin-bottom: 20px;
}

.section-title {
  font-size: 13px;
  font-weight: 600;
  color: #666;
  margin-bottom: 10px;
  display: block;
}

// 类型选择器
.type-selector {
  display: flex;
  gap: 12px;
}

.type-item {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 6px;
  padding: 16px 12px;
  background: #fff;
  border-radius: 14px;
  border: 2px solid transparent;
  transition: all 0.2s;

  &.active {
    border-color: currentColor;
    background: currentColor;

    .type-icon, .type-name {
      color: #fff;
    }
  }

  &.expense {
    color: #EF476F;
    &.active {
      background: #EF476F;
    }
  }

  &.income {
    color: #06D6A0;
    &.active {
      background: #06D6A0;
    }
  }

  &.transfer {
    color: #0F4C5C;
    &.active {
      background: #0F4C5C;
    }
  }
}

.type-icon {
  font-size: 24px;
}

.type-name {
  font-size: 13px;
  font-weight: 500;
}

// 金额输入
.amount-input-card {
  background: #fff;
  border-radius: 14px;
  padding: 16px;
}

.amount-input-row {
  display: flex;
  align-items: center;
  margin-bottom: 16px;
}

.currency-symbol {
  font-size: 32px;
  font-weight: 600;
  color: #0F4C5C;
  margin-right: 8px;
}

.amount-input {
  flex: 1;
  font-size: 36px;
  font-weight: 700;
  color: #333;
  background: transparent;
  border: none;
  outline: none;

  &::placeholder {
    color: #ccc;
  }
}

.quick-amounts {
  display: flex;
  gap: 10px;
}

.qa-btn {
  flex: 1;
  padding: 10px 0;
  background: #F8F9FA;
  border-radius: 10px;
  text-align: center;
  font-size: 13px;
  color: #666;
  font-weight: 500;

  &:active {
    background: #E9ECEF;
  }
}

// 分类选择器
.category-selector, .account-selector {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: #fff;
  border-radius: 14px;
  padding: 14px 16px;
  min-height: 60px;

  &:active {
    background: #F3F4F6;
  }
}

.selected-category {
  display: flex;
  align-items: center;
  gap: 12px;
}

.cat-icon {
  width: 40px;
  height: 40px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.cat-emoji {
  font-size: 20px;
}

.cat-name {
  font-size: 15px;
  color: #333;
  font-weight: 500;
}

.selected-account {
  display: flex;
  align-items: center;
  gap: 12px;
}

.account-icon {
  font-size: 24px;
}

.account-info {
  display: flex;
  flex-direction: column;
}

.account-name {
  font-size: 15px;
  color: #333;
  font-weight: 500;
}

.account-balance {
  font-size: 12px;
  color: #999;
}

.select-placeholder {
  font-size: 15px;
  color: #ccc;
}

.select-arrow {
  font-size: 18px;
  color: #D1D5DB;
}

// 日期时间选择器
.datetime-selector {
  display: flex;
  align-items: center;
  background: #fff;
  border-radius: 14px;
  padding: 14px 16px;
  gap: 12px;

  &:active {
    background: #F3F4F6;
  }
}

.datetime-icon {
  font-size: 20px;
}

.datetime-value {
  flex: 1;
  display: flex;
  align-items: center;
  gap: 12px;
}

.date-text {
  font-size: 15px;
  color: #333;
}

.time-text {
  font-size: 14px;
  color: #999;
  background: #F8F9FA;
  padding: 4px 10px;
  border-radius: 6px;
}

// 备注
.remark-card {
  background: #fff;
  border-radius: 14px;
  padding: 14px 16px;
}

.remark-input {
  width: 100%;
  min-height: 80px;
  font-size: 15px;
  color: #333;
  background: transparent;
  border: none;
  outline: none;
  resize: none;

  &::placeholder {
    color: #ccc;
  }
}

.remark-count {
  text-align: right;
  margin-top: 8px;
  font-size: 12px;
  color: #ccc;
}

// 标签
.tags-section {
  background: #fff;
  border-radius: 14px;
  padding: 14px 16px;
}

.tags-grid {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 12px;
}

.tag-item {
  padding: 8px 14px;
  background: #F8F9FA;
  border-radius: 20px;
  font-size: 13px;
  color: #666;

  &.selected {
    background: rgba(15, 76, 92, 0.1);
    color: #0F4C5C;
    font-weight: 500;
  }

  &:active:not(.selected) {
    background: #E9ECEF;
  }
}

.add-tag-btn {
  display: inline-flex;
  align-items: center;
  padding: 8px 14px;
  border: 1px dashed #ddd;
  border-radius: 20px;
  font-size: 13px;
  color: #999;

  &:active {
    background: #F8F9FA;
  }
}

// 附件
.attachment-section {
  background: #fff;
  border-radius: 14px;
  padding: 14px 16px;
}

.attachment-list {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-bottom: 12px;
}

.attachment-item {
  position: relative;
  width: 70px;
  height: 70px;
  border-radius: 8px;
  overflow: hidden;
}

.attachment-image {
  width: 100%;
  height: 100%;
}

.attachment-remove {
  position: absolute;
  top: 4px;
  right: 4px;
  width: 20px;
  height: 20px;
  border-radius: 50%;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;

  text {
    font-size: 14px;
    color: #fff;
    line-height: 1;
  }
}

.add-attachment-btn {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 8px 14px;
  border: 1px dashed #ddd;
  border-radius: 8px;
  font-size: 13px;
  color: #999;

  .add-icon {
    font-size: 16px;
  }
}

.bottom-safe {
  height: 100px;
}

// 底部提交栏
.submit-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  background: #fff;
  padding: 12px 16px;
  padding-bottom: calc(12px + env(safe-area-inset-bottom));
  border-top: 1px solid #E5E5E5;
  display: flex;
  align-items: center;
  gap: 16px;
}

.submit-info {
  display: flex;
  flex-direction: column;

  .info-label {
    font-size: 12px;
    color: #999;
  }

  .info-amount {
    font-size: 18px;
    font-weight: 700;

    &.income {
      color: #06D6A0;
    }

    &.expense {
      color: #EF476F;
    }
  }
}

.submit-btn {
  flex: 1;
  height: 48px;
  background: #0F4C5C;
  border-radius: 12px;
  color: #fff;
  font-size: 16px;
  font-weight: 600;
  border: none;
  display: flex;
  align-items: center;
  justify-content: center;

  &.disabled {
    background: #ccc;
  }

  &:active:not(.disabled) {
    opacity: 0.9;
  }
}

// 选择器弹窗
.picker-container {
  background: #fff;
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

.picker-cancel {
  font-size: 15px;
  color: #999;
}

.picker-title {
  font-size: 16px;
  font-weight: 600;
  color: #333;
}

.picker-confirm {
  font-size: 15px;
  color: #0F4C5C;
  font-weight: 500;
}

.category-type-tabs {
  display: flex;
  padding: 12px 20px;
  gap: 12px;
  border-bottom: 1px solid #F3F4F6;
}

.ct-tab {
  flex: 1;
  padding: 10px;
  text-align: center;
  background: #F8F9FA;
  border-radius: 8px;
  font-size: 14px;
  color: #666;

  &.active {
    background: #0F4C5C;
    color: #fff;
    font-weight: 500;
  }
}

.category-picker, .account-picker, .date-picker {
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

// 快捷日期按钮
.quick-date-btns {
  display: flex;
  gap: 12px;
  padding: 12px 20px;
  border-bottom: 1px solid #F0F0F0;
}

.quick-date-btn {
  flex: 1;
  padding: 10px;
  text-align: center;
  background: #F8F9FA;
  border-radius: 8px;
  font-size: 13px;
  color: #666;

  &.active {
    background: rgba(15, 76, 92, 0.1);
    color: #0F4C5C;
    font-weight: 500;
  }
}

// 标签输入弹窗
.tag-input-popup {
  background: #fff;
  border-radius: 16px 16px 0 0;
  overflow: hidden;
}

.tag-input-section {
  padding: 20px;
}

.tag-input {
  width: 100%;
  height: 44px;
  background: #F8F9FA;
  border-radius: 10px;
  padding: 0 14px;
  font-size: 15px;
  color: #333;
  border: none;
  outline: none;
}
</style>
