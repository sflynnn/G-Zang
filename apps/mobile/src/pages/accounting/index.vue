<template>
  <view class="accounting-page">
    <!-- 顶部导航 -->
    <view class="page-header">
      <!-- 账本选择 + 月份选择器 -->
      <view class="header-row">
        <view class="book-selector" @click="showBookPicker">
          <text class="book-icon">{{ currentBook?.icon || '📒' }}</text>
          <text class="book-name">{{ currentBook?.name || '选择账本' }}</text>
          <text class="book-arrow">›</text>
        </view>
        <MonthPicker
          :currentMonth="currentMonth"
          @prev="onMonthChange('prev')"
          @next="onMonthChange('next')"
          @change="onMonthSelect"
        />
      </view>
    </view>

    <!-- 主内容区域 -->
    <scroll-view class="main-content" scroll-y="true" refresher-enabled>
      <!-- 支出分类区域 -->
      <view class="category-section expense-section">
        <view class="section-header">
          <text class="section-title">支出</text>
          <text class="section-stat">{{ currentMonthExpense.toFixed(2) }}</text>
        </view>
        <view class="category-grid">
          <view
            v-for="category in expenseCategories"
            :key="category.id"
            class="category-card"
            :style="{ borderLeftColor: category.color }"
            @click="selectCategory(category)"
          >
            <view class="card-content">
              <text class="category-icon">{{ category.icon }}</text>
              <view class="category-info">
                <text class="category-name">{{ category.name }}</text>
                <text class="category-count">{{ category.children?.length || 0 }}个子分类</text>
              </view>
            </view>
            <view v-if="category.budget" class="budget-badge" :class="getBudgetClass(category.budget)">
              <text class="budget-text">{{ getBudgetPercent(category.budget) }}%</text>
            </view>
          </view>
        </view>
      </view>

      <!-- 收入分类区域 -->
      <view class="category-section income-section">
        <view class="section-header">
          <text class="section-title">收入</text>
          <text class="section-stat income">{{ currentMonthIncome.toFixed(2) }}</text>
        </view>
        <view class="category-grid">
          <view
            v-for="category in incomeCategories"
            :key="category.id"
            class="category-card income"
            :style="{ borderLeftColor: category.color }"
            @click="selectCategory(category)"
          >
            <view class="card-content">
              <text class="category-icon">{{ category.icon }}</text>
              <view class="category-info">
                <text class="category-name">{{ category.name }}</text>
                <text class="category-count">{{ category.children?.length || 0 }}个子分类</text>
              </view>
            </view>
          </view>
        </view>
      </view>

      <!-- 底部占位 -->
      <view class="bottom-spacer"></view>
    </scroll-view>

    <!-- 账本选择弹窗 -->
    <uni-popup ref="bookPopup" type="bottom">
      <view class="picker-container">
        <view class="picker-header">
          <text class="picker-cancel" @click="closeBookPicker">取消</text>
          <text class="picker-title">选择账本</text>
          <text class="picker-confirm" @click="confirmBook">确定</text>
        </view>
        <picker-view :value="bookPickerValue" @change="onBookChange" class="picker-view">
          <picker-view-column>
            <view v-for="book in books" :key="book.id" class="picker-item">
              {{ book.icon || '📒' }} {{ book.name }}
            </view>
          </picker-view-column>
        </picker-view>
      </view>
    </uni-popup>

    <!-- 自定义TabBar -->
    <CustomTabBar />
  </view>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted, watch } from 'vue'
import { useBookStore } from '@/stores/book'
import { useAccountingStore } from '@/stores/accounting'
import MonthPicker from '@/components/business/MonthPicker/index.vue'
import CustomTabBar from '@/components/CustomTabBar/index.vue'

// 类型定义
interface Category {
  id: number
  name: string
  categoryName?: string
  icon: string
  color: string
  type: number
  parentId?: number
  children?: Category[]
  budget?: {
    budget: number
    spent: number
    remaining: number
    percentUsed?: number
  }
}

// Store
const bookStore = useBookStore()
const accountingStore = useAccountingStore()

// 响应式数据
const currentMonth = ref(getCurrentYearMonth())
const bookPickerValue = ref([0])

// 弹窗ref
const bookPopup = ref<any>(null)

// 计算属性
const currentBook = computed(() => bookStore.currentBook)
const books = computed(() => bookStore.books)

const categories = computed((): Category[] => {
  return (accountingStore.categoriesWithChildren as unknown as Category[]) || []
})

const expenseCategories = computed((): Category[] => {
  return categories.value.filter(c => c.type === 2)
})

const incomeCategories = computed((): Category[] => {
  return categories.value.filter(c => c.type === 1)
})

// 当月统计
const currentMonthExpense = computed(() => {
  return accountingStore.currentMonthStats?.expense || 0
})

const currentMonthIncome = computed(() => {
  return accountingStore.currentMonthStats?.income || 0
})

// 获取当前年月字符串
function getCurrentYearMonth(): string {
  const now = new Date()
  return `${now.getFullYear()}-${String(now.getMonth() + 1).padStart(2, '0')}`
}

// 格式化年月显示
const formatMonthDisplay = (month: string) => {
  const [year, m] = month.split('-')
  return `${parseInt(year)}年${parseInt(m)}月`
}

// 月份切换
const onMonthChange = (direction: 'prev' | 'next') => {
  const [year, month] = currentMonth.value.split('-').map(Number)
  let newYear = year
  let newMonth = month

  if (direction === 'prev') {
    newMonth--
    if (newMonth < 1) {
      newMonth = 12
      newYear--
    }
  } else {
    newMonth++
    if (newMonth > 12) {
      newMonth = 1
      newYear++
    }
  }

  currentMonth.value = `${newYear}-${String(newMonth).padStart(2, '0')}`
  loadData()
}

const onMonthSelect = (month: string) => {
  currentMonth.value = month
  loadData()
}

// 选择分类
const selectCategory = (category: Category) => {
  const type = category.type === 2 ? 'expense' : 'income'
  uni.navigateTo({
    url: `/pages/accounting/category-record?categoryId=${category.id}&type=${category.type}`
  })
}

// 预算相关
const getBudgetPercent = (budget?: { budget: number; spent: number }) => {
  if (!budget || budget.budget <= 0) return 0
  return Math.round((budget.spent / budget.budget) * 100)
}

const getBudgetClass = (budget?: { budget: number; spent: number }) => {
  if (!budget) return ''
  const percent = getBudgetPercent(budget)
  if (percent >= 100) return 'danger'
  if (percent >= 80) return 'warning'
  return 'normal'
}

// 账本选择
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

// 加载数据
const loadData = async () => {
  const bookId = currentBook.value?.id
  
  await Promise.all([
    accountingStore.loadCategoriesWithChildren(bookId, currentMonth.value),
    accountingStore.loadMonthStats(bookId, currentMonth.value)
  ])
}

// 监听账本切换
watch(currentBook, () => {
  loadData()
})

// 生命周期
onMounted(async () => {
  await bookStore.loadBooks()
  await loadData()
})
</script>

<style lang="scss" scoped>
.accounting-page {
  min-height: 100vh;
  background: var(--gzang-bg);
  display: flex;
  flex-direction: column;
}

// 顶部导航
.page-header {
  background: var(--gzang-surface);
  padding: 16rpx 24rpx;
  padding-top: calc(env(safe-area-inset-top) + 12rpx);
}

.header-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.book-selector {
  display: flex;
  align-items: center;
  gap: 8rpx;
  padding: 12rpx 20rpx;
  background: var(--gzang-bg);
  border-radius: var(--apple-radius-full);

  &:active {
    opacity: 0.8;
  }
}

.book-icon {
  font-size: 24rpx;
}

.book-name {
  font-size: var(--apple-text-sm);
  font-weight: var(--apple-font-medium);
  color: var(--gzang-text-primary);
}

.book-arrow {
  font-size: 20rpx;
  color: var(--gzang-text-tertiary);
}

// 主内容区域
.main-content {
  flex: 1;
  padding: 16rpx 24rpx;
}

// 分类区域
.category-section {
  margin-bottom: 32rpx;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20rpx;
}

.section-title {
  font-size: var(--apple-text-lg);
  font-weight: var(--apple-font-semibold);
  color: var(--gzang-text-primary);
}

.section-stat {
  font-size: var(--apple-text-base);
  font-weight: var(--apple-font-semibold);
  font-family: var(--font-mono);
  color: var(--gzang-danger);

  &.income {
    color: var(--gzang-success);
  }
}

// 分类网格
.category-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16rpx;
}

.category-card {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 20rpx;
  background: var(--gzang-surface);
  border-radius: var(--apple-radius-lg);
  border-left: 6rpx solid;
  box-shadow: var(--apple-shadow-xs);
  transition: all var(--apple-duration-fast) var(--apple-ease-out);

  &:active {
    transform: scale(0.98);
    box-shadow: var(--apple-shadow-sm);
  }

  &.income {
    // 收入卡片样式
  }
}

.card-content {
  display: flex;
  align-items: center;
  gap: 16rpx;
}

.category-icon {
  font-size: 40rpx;
  line-height: 1;
}

.category-info {
  display: flex;
  flex-direction: column;
  gap: 4rpx;
}

.category-name {
  font-size: var(--apple-text-base);
  font-weight: var(--apple-font-medium);
  color: var(--gzang-text-primary);
}

.category-count {
  font-size: var(--apple-text-xs);
  color: var(--gzang-text-tertiary);
}

.budget-badge {
  position: absolute;
  top: 12rpx;
  right: 12rpx;
  padding: 4rpx 12rpx;
  border-radius: var(--apple-radius-full);
  font-size: 20rpx;
  font-weight: var(--apple-font-medium);

  &.normal {
    background: rgba(6, 214, 160, 0.1);
    color: var(--gzang-success);
  }

  &.warning {
    background: rgba(255, 193, 7, 0.1);
    color: #ffc107;
  }

  &.danger {
    background: rgba(239, 71, 111, 0.1);
    color: var(--gzang-danger);
  }
}

.budget-text {
  font-family: var(--font-mono);
}

.bottom-spacer {
  height: 120rpx;
}

// 账本选择弹窗
.picker-container {
  background: var(--gzang-surface);
  border-radius: var(--apple-radius-xl) var(--apple-radius-xl) 0 0;
  overflow: hidden;
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
</style>
