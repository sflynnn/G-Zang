<template>
  <view class="transactions-edit-page">
    <uni-nav-bar left-icon="back" title="编辑交易" @clickLeft="goBack" />
    
    <view v-if="loading" class="loading-container">
      <uni-load-more status="loading" />
    </view>
    
    <template v-else>
      <!-- 金额输入 -->
      <view class="amount-section">
        <view class="amount-display" :class="formData.type === 1 ? 'income' : 'expense'">
          <text class="currency">{{ currentCurrency }}</text>
          <input 
            class="amount-input" 
            v-model="formData.amount" 
            type="digit"
            placeholder="0.00"
            @blur="formatAmount"
          />
        </view>
        
        <view class="type-tabs">
          <view 
            :class="['type-tab', 'expense', { active: formData.type === 2 }]"
            @click="formData.type = 2"
          >
            支出
          </view>
          <view 
            :class="['type-tab', 'income', { active: formData.type === 1 }]"
            @click="formData.type = 1"
          >
            收入
          </view>
        </view>
      </view>

      <!-- 分类选择 -->
      <view class="category-section" @click="showCategoryPicker">
        <view class="section-row">
          <text class="row-icon">📂</text>
          <text class="row-label">分类</text>
        </view>
        <view class="section-value">
          <text class="category-badge" v-if="selectedCategory">
            {{ selectedCategory.icon }} {{ selectedCategory.name }}
          </text>
          <text class="placeholder" v-else>请选择分类</text>
          <text class="arrow">›</text>
        </view>
      </view>

      <!-- 账户选择 -->
      <view class="category-section" @click="showAccountPicker">
        <view class="section-row">
          <text class="row-icon">💳</text>
          <text class="row-label">账户</text>
        </view>
        <view class="section-value">
          <text v-if="selectedAccount">{{ selectedAccount.accountName }}</text>
          <text class="placeholder" v-else>请选择账户</text>
          <text class="arrow">›</text>
        </view>
      </view>

      <!-- 日期选择 -->
      <view class="category-section" @click="showDatePicker">
        <view class="section-row">
          <text class="row-icon">📅</text>
          <text class="row-label">日期</text>
        </view>
        <view class="section-value">
          <text>{{ formatDate(formData.transactionTime) }}</text>
          <text class="arrow">›</text>
        </view>
      </view>

      <!-- 备注 -->
      <view class="remark-section">
        <textarea 
          class="remark-input" 
          v-model="formData.remark" 
          placeholder="添加备注..."
          maxlength="200"
          rows="3"
        />
      </view>

      <!-- 保存按钮 -->
      <view class="submit-bar">
        <button class="submit-btn" @click="handleSubmit" :loading="submitting">
          {{ submitting ? '保存中...' : '保存' }}
        </button>
      </view>
    </template>
  </view>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { useTransactionStore } from '@/stores/transaction'
import { useCategoryStore } from '@/stores/category'
import { useAccountStore } from '@/stores/account'

const transactionStore = useTransactionStore()
const categoryStore = useCategoryStore()
const accountStore = useAccountStore()

const transactionId = ref<number>(0)
const loading = ref(false)
const submitting = ref(false)
const selectedCategory = ref<any>(null)
const selectedAccount = ref<any>(null)

const formData = ref({
  amount: '',
  type: 2,
  categoryId: null as number | null,
  accountId: null as number | null,
  transactionTime: new Date().toISOString(),
  remark: ''
})

const currentCurrency = '¥'

onLoad(async (options: any) => {
  if (options?.id) {
    transactionId.value = parseInt(options.id)
    await loadTransaction()
  }
  await loadCategories()
  await loadAccounts()
})

onMounted(async () => {
  if (!transactionId.value) {
    await loadCategories()
    await loadAccounts()
  }
})

async function loadTransaction() {
  loading.value = true
  try {
    const res: any = await uni.request({
      url: `/api/mobile/transactions/${transactionId.value}`
    })
    if (res.data?.data) {
      const data = res.data.data
      formData.value = {
        amount: data.amount?.toString() || '',
        type: data.type,
        categoryId: data.categoryId,
        accountId: data.accountId,
        transactionTime: data.transactionTime,
        remark: data.remark || ''
      }
      selectedCategory.value = { id: data.categoryId, name: data.categoryName, icon: '📂' }
      selectedAccount.value = { id: data.accountId, accountName: data.accountName }
    }
  } catch (error) {
    // ignore
  } finally {
    loading.value = false
  }
}

async function loadCategories() {
  try {
    await categoryStore.fetchCategories()
  } catch (error) {
    // ignore
  }
}

async function loadAccounts() {
  try {
    await accountStore.fetchAccounts()
  } catch (error) {
    // ignore
  }
}

function formatAmount() {
  if (formData.value.amount) {
    const num = parseFloat(formData.value.amount)
    if (!isNaN(num)) {
      formData.value.amount = num.toFixed(2)
    }
  }
}

function showCategoryPicker() {
  const categories = formData.value.type === 1 
    ? categoryStore.incomeCategories 
    : categoryStore.expenseCategories
  
  const items = categories.map((c: any) => ({
    label: `${c.icon || '📂'} ${c.name}`,
    value: c.id
  }))
  
  uni.showActionSheet({
    itemList: items.map(i => i.label),
    success: (res) => {
      const selected = categories[res.tapIndex]
      selectedCategory.value = selected
      formData.value.categoryId = selected.id
    }
  })
}

function showAccountPicker() {
  const items = accountStore.accountList.map((a: any) => ({
    label: a.accountName,
    value: a.id
  }))
  
  uni.showActionSheet({
    itemList: items.map(i => i.label),
    success: (res) => {
      const selected = accountStore.accountList[res.tapIndex]
      selectedAccount.value = selected
      formData.value.accountId = selected.id
    }
  })
}

function showDatePicker() {
  const current = new Date(formData.value.transactionTime)
  uni.showDatePicker({
    current: current,
    success: (res: any) => {
      formData.value.transactionTime = res.dateValue
    }
  })
}

function formatDate(dateStr: string): string {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  const year = date.getFullYear()
  const month = (date.getMonth() + 1).toString().padStart(2, '0')
  const day = date.getDate().toString().padStart(2, '0')
  return `${year}-${month}-${day}`
}

function goBack() {
  uni.navigateBack()
}

async function handleSubmit() {
  if (!formData.value.amount) {
    uni.showToast({ title: '请输入金额', icon: 'none' })
    return
  }
  if (!formData.value.categoryId) {
    uni.showToast({ title: '请选择分类', icon: 'none' })
    return
  }
  if (!formData.value.accountId) {
    uni.showToast({ title: '请选择账户', icon: 'none' })
    return
  }

  submitting.value = true
  try {
    await transactionStore.updateTransaction({
      id: transactionId.value,
      amount: parseFloat(formData.value.amount),
      type: formData.value.type,
      categoryId: formData.value.categoryId,
      accountId: formData.value.accountId,
      transactionTime: formData.value.transactionTime,
      remark: formData.value.remark
    })
    
    uni.showToast({ title: '保存成功', icon: 'success' })
    setTimeout(() => {
      goBack()
    }, 1500)
  } catch (error: any) {
    uni.showToast({ title: error.message || '保存失败', icon: 'none' })
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped lang="scss">
.transactions-edit-page {
  min-height: 100vh;
  background-color: #F8F9FA;
  padding-bottom: 140rpx;
}

.loading-container {
  padding: 200rpx 0;
}

.amount-section {
  background: #fff;
  padding: 48rpx 32rpx;
  margin-bottom: 24rpx;
}

.amount-display {
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 32rpx;
  
  .currency {
    font-size: 40rpx;
    font-weight: 600;
    margin-right: 8rpx;
  }
  
  &.income .currency {
    color: #06D6A0;
  }
  
  &.expense .currency {
    color: #EF476F;
  }
}

.amount-input {
  font-size: 64rpx;
  font-weight: 700;
  color: #1F2937;
  text-align: center;
  min-width: 200rpx;
}

.type-tabs {
  display: flex;
  background: #F8F9FA;
  border-radius: 24rpx;
  padding: 8rpx;
  gap: 8rpx;
}

.type-tab {
  flex: 1;
  padding: 20rpx;
  border-radius: 20rpx;
  text-align: center;
  font-size: 28rpx;
  font-weight: 600;
  color: #9CA3AF;
  
  &.expense.active {
    background: #EF476F;
    color: #fff;
  }
  
  &.income.active {
    background: #06D6A0;
    color: #fff;
  }
}

.category-section {
  background: #fff;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 32rpx;
  margin-bottom: 2rpx;
}

.section-row {
  display: flex;
  align-items: center;
  gap: 12rpx;
  
  .row-icon {
    font-size: 32rpx;
  }
  
  .row-label {
    font-size: 28rpx;
    color: #1F2937;
  }
}

.section-value {
  display: flex;
  align-items: center;
  gap: 8rpx;
  font-size: 28rpx;
  color: #1F2937;
  
  .placeholder {
    color: #ccc;
  }
  
  .arrow {
    color: #D1D5DB;
    font-size: 32rpx;
  }
}

.category-badge {
  background: rgba(15, 76, 92, 0.08);
  color: #0F4C5C;
  padding: 8rpx 16rpx;
  border-radius: 8rpx;
  font-weight: 500;
}

.remark-section {
  background: #fff;
  padding: 24rpx;
  margin-top: 24rpx;
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
