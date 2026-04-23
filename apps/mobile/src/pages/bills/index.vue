<template>
  <view class="bills-page apple-style">
    <!-- Large Title Navigation -->
    <view class="nav-large-title">
      <view class="nav-header">
        <view class="nav-titles">
          <text class="nav-title">{{ t('bills.title') }}</text>
        </view>
        <view class="nav-actions">
          <view class="nav-action" @click="goToSearch">
            <AppleIcon name="search" :size="20" color="var(--gzang-text-secondary)" />
          </view>
          <view class="nav-action" @click="goToFilter">
            <AppleIcon name="filter" :size="20" color="var(--gzang-text-secondary)" />
          </view>
        </view>
      </view>
    </view>

    <!-- Book Switcher -->
    <view class="book-switcher" v-if="books.length > 0">
      <scroll-view class="book-scroll" scroll-x="true" enhanced="true" show-scrollbar="false">
        <view 
          v-for="book in books" 
          :key="book.id"
          class="book-chip"
          :class="{ active: currentBookId === book.id }"
          @click="switchBook(book.id)"
        >
          <AppleIcon name="book" :size="12" :color="currentBookId === book.id ? 'white' : 'text-secondary'" />
          <text class="book-chip-name">{{ book.name }}</text>
        </view>
        <view class="book-chip add-book" @click="goToBooks">
          <AppleIcon name="plus" :size="12" color="var(--gzang-text-tertiary)" />
        </view>
      </scroll-view>
    </view>

    <scroll-view class="main-content" scroll-y="true" @scrolltolower="loadMore">
      <!-- Calendar Card - Apple Style -->
      <view class="calendar-card">
        <!-- Month Navigation -->
        <view class="calendar-header">
          <view class="month-nav" @click="prevMonth">
            <AppleIcon name="chevron-left" :size="18" color="var(--gzang-text-secondary)" />
          </view>
          <text class="month-title">{{ currentMonthTitle }}</text>
          <view class="month-nav" @click="nextMonth">
            <AppleIcon name="chevron-right" :size="18" color="var(--gzang-text-secondary)" />
          </view>
        </view>

        <!-- Weekday Header -->
        <view class="weekday-row">
          <text v-for="day in weekdays" :key="day" class="weekday">{{ day }}</text>
        </view>

        <!-- Calendar Grid -->
        <view class="days-grid">
          <view
            v-for="(day, index) in calendarDays"
            :key="index"
            class="day-cell"
            :class="{
              'is-empty': !day.date,
              'is-today': day.isToday,
              'has-data': day.hasData,
              'is-selected': selectedDate === day.dateStr
            }"
            @click="selectDate(day)"
          >
            <text v-if="day.date" class="day-text">{{ day.date }}</text>
            <view v-if="day.hasData && day.date" class="day-indicator"></view>
          </view>
        </view>

        <!-- Period Tabs -->
        <view class="period-tabs">
          <view 
            v-for="period in periods" 
            :key="period.key"
            class="period-tab"
            :class="{ active: activePeriod === period.key }"
            @click="switchPeriod(period.key)"
          >
            {{ period.label }}
          </view>
        </view>
      </view>

      <!-- Stats Card -->
      <view class="stats-card" v-if="periodStats">
        <view class="stats-header">
          <text class="stats-period">{{ getPeriodLabel() }}</text>
          <view class="stats-income">
            <AppleIcon name="income" :size="12" color="var(--gzang-success)" />
            <text>{{ formatAmount(periodStats.income) }}</text>
          </view>
        </view>
        <view class="stats-body">
          <view class="stats-main">
            <view class="stat-item">
              <text class="stat-label">{{ t('analysis.expense') }}</text>
              <text class="stat-value expense amount">{{ formatAmount(periodStats.expense) }}</text>
            </view>
            <view class="stat-divider"></view>
            <view class="stat-item">
              <text class="stat-label">{{ t('analysis.balance') }}</text>
              <text class="stat-value" :class="periodStats.balance >= 0 ? 'income' : 'expense'">
                {{ formatAmount(periodStats.balance) }}
              </text>
            </view>
          </view>
        </view>
      </view>

      <!-- Transaction List -->
      <view class="section-transactions">
        <view class="section-header">
          <text class="section-title">{{ t('bills.title') }}</text>
          <text class="section-count">{{ filteredTransactions.length }} {{ t('analysis.total') }}</text>
        </view>

        <view class="transaction-list" v-if="groupedTransactions.length > 0">
          <view 
            v-for="group in groupedTransactions" 
            :key="group.date"
            class="transaction-group"
          >
            <!-- Group Header -->
            <view class="group-header">
              <text class="group-date">{{ formatGroupDate(group.date) }}</text>
              <view class="group-stats">
                <text class="group-income" v-if="group.income > 0">+{{ formatAmount(group.income) }}</text>
                <text class="group-expense">-{{ formatAmount(group.expense) }}</text>
              </view>
            </view>

            <!-- Group Items -->
            <view class="group-items">
              <view 
                v-for="item in group.transactions" 
                :key="item.id"
                class="transaction-item"
                @click="goToDetail(item)"
              >
                <view class="item-icon" :style="{ background: getCategoryBg(item.categoryIcon) + '20' }">
                  <AppleIcon :name="item.categoryIcon || 'shopping'" :size="18" :color="getCategoryColor(item.categoryIcon)" />
                </view>
                <view class="item-info">
                  <text class="item-category">{{ item.categoryName }}</text>
                  <text class="item-account">{{ item.accountName }}</text>
                </view>
                <view class="item-right">
                  <text class="item-amount amount" :class="item.type === 1 ? 'income' : 'expense'">
                    {{ item.type === 1 ? '+' : '-' }}{{ formatAmount(item.amount) }}
                  </text>
                  <text class="item-time">{{ formatTime(item.transactionTime) }}</text>
                </view>
              </view>
            </view>
          </view>
        </view>

        <!-- Empty State -->
        <view v-else class="empty-state">
          <view class="empty-icon-wrapper">
            <AppleIcon name="list" :size="48" color="var(--gzang-text-tertiary)" />
          </view>
          <text class="empty-title">{{ t('bills.noBills') }}</text>
          <text class="empty-desc">{{ t('bills.noBillsDesc') }}</text>
        </view>

        <!-- Loading / No More -->
        <view v-if="loading" class="loading-more">
          <view class="loading-spinner"></view>
        </view>
        <view v-if="finished && filteredTransactions.length > 0" class="no-more">
          <text>— 没有更多了 —</text>
        </view>
      </view>

      <view class="bottom-safe-area"></view>
    </scroll-view>

    <!-- 自定义 TabBar -->
    <CustomTabBar />
  </view>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useI18n } from 'vue-i18n'
import { useBookStore } from '@/stores/book'
import type { Transaction } from '@/types/transaction'
import CustomTabBar from '@/components/CustomTabBar/index.vue'
import AppleIcon from '@/components/common/AppleIcon/index.vue'

const { t } = useI18n()

// Store
const bookStore = useBookStore()

// State
const loading = ref(false)
const finished = ref(false)
const currentBookId = ref<number | null>(null)
const currentMonth = ref(new Date())
const activePeriod = ref<'day' | 'week' | 'month' | 'year' | 'all'>('month')
const selectedDate = ref<string | null>(null)

// Weekdays
const weekdays = ['日', '一', '二', '三', '四', '五', '六']

// Periods
const periods = computed(() => [
  { key: 'day' as const, label: t('datetime.today') },
  { key: 'week' as const, label: t('datetime.thisWeek') },
  { key: 'month' as const, label: t('datetime.thisMonth') },
  { key: 'year' as const, label: t('datetime.thisYear') },
])

// Computed
const books = computed(() => bookStore.books || [])

// Current month title
const currentMonthTitle = computed(() => {
  const year = currentMonth.value.getFullYear()
  const month = currentMonth.value.getMonth() + 1
  return `${year}年${month}月`
})

// Calendar days
const calendarDays = computed(() => {
  const year = currentMonth.value.getFullYear()
  const month = currentMonth.value.getMonth()
  const firstDay = new Date(year, month, 1)
  const lastDay = new Date(year, month + 1, 0)
  const startWeek = firstDay.getDay()
  const daysInMonth = lastDay.getDate()

  const today = new Date()
  const todayStr = formatDateStr(today)
  const todayYear = today.getFullYear()
  const todayMonth = today.getMonth()
  const todayDate = today.getDate()

  const days: Array<{
    date: number | null
    dateStr: string | null
    isToday: boolean
    hasData: boolean
  }> = []

  // Empty cells
  for (let i = 0; i < startWeek; i++) {
    days.push({ date: null, dateStr: null, isToday: false, hasData: false })
  }

  // Date cells
  for (let d = 1; d <= daysInMonth; d++) {
    const dateStr = `${year}-${String(month + 1).padStart(2, '0')}-${String(d).padStart(2, '0')}`
    const isToday = year === todayYear && month === todayMonth && d === todayDate
    const hasData = Math.random() > 0.6

    days.push({ date: d, dateStr, isToday, hasData })
  }

  return days
})

// Mock transactions
const mockTransactions = computed((): Transaction[] => {
  const year = currentMonth.value.getFullYear()
  const month = currentMonth.value.getMonth() + 1
  const transactions: Transaction[] = []

  const categories = [
    { name: '餐饮', icon: 'food', color: '#FB8B24' },
    { name: '交通', icon: 'transport', color: '#0F4C5C' },
    { name: '购物', icon: 'shopping', color: '#EF476F' },
    { name: '娱乐', icon: 'entertainment', color: '#9B59B6' },
    { name: '工资', icon: 'income', color: '#06D6A0' },
  ]
  const accounts = ['支付宝', '微信钱包', '建设银行', '招商银行']

  for (let d = 1; d <= 28; d++) {
    if (Math.random() > 0.5) {
      const day = String(d).padStart(2, '0')
      const monthStr = String(month).padStart(2, '0')
      const dateStr = `${year}-${monthStr}-${day}`

      for (let i = 0; i < Math.floor(Math.random() * 3) + 1; i++) {
        const cat = categories[Math.floor(Math.random() * categories.length)]
        const acc = accounts[Math.floor(Math.random() * accounts.length)]
        const type = cat.name === '工资' ? 1 : 2

        transactions.push({
          id: Date.now() + d * 100 + i,
          type,
          amount: Math.floor(Math.random() * 500) + 10,
          categoryId: Math.floor(Math.random() * 100) + 1,
          categoryName: cat.name,
          categoryIcon: cat.icon,
          accountId: Math.floor(Math.random() * 4) + 1,
          accountName: acc,
          transactionTime: `${dateStr} ${String(Math.floor(Math.random() * 12) + 8).padStart(2, '0')}:${String(Math.floor(Math.random() * 60)).padStart(2, '0')}:00`,
          createTime: `${dateStr} ${String(Math.floor(Math.random() * 12) + 8).padStart(2, '0')}:${String(Math.floor(Math.random() * 60)).padStart(2, '0')}:00`,
        } as Transaction)
      }
    }
  }

  return transactions.sort((a, b) =>
    new Date(b.transactionTime).getTime() - new Date(a.transactionTime).getTime()
  )
})

// Filtered transactions
const filteredTransactions = computed(() => {
  let result = [...mockTransactions.value]

  if (activePeriod.value === 'day' && selectedDate.value) {
    result = result.filter(t => t.transactionTime.startsWith(selectedDate.value!))
  } else if (activePeriod.value === 'month') {
    const year = currentMonth.value.getFullYear()
    const month = currentMonth.value.getMonth() + 1
    const monthStr = `${year}-${String(month).padStart(2, '0')}`
    result = result.filter(t => t.transactionTime.startsWith(monthStr))
  }

  return result
})

// Grouped transactions
const groupedTransactions = computed(() => {
  const groups: Record<string, { date: string; income: number; expense: number; transactions: Transaction[] }> = {}

  filteredTransactions.value.forEach(t => {
    const dateStr = t.transactionTime.split(' ')[0]
    if (!groups[dateStr]) {
      groups[dateStr] = { date: dateStr, income: 0, expense: 0, transactions: [] }
    }
    groups[dateStr].transactions.push(t)
    if (t.type === 1) {
      groups[dateStr].income += t.amount
    } else {
      groups[dateStr].expense += t.amount
    }
  })

  return Object.values(groups).sort((a, b) =>
    new Date(b.date).getTime() - new Date(a.date).getTime()
  )
})

// Period stats
const periodStats = computed(() => {
  const income = filteredTransactions.value
    .filter(t => t.type === 1)
    .reduce((sum, t) => sum + t.amount, 0)
  const expense = filteredTransactions.value
    .filter(t => t.type === 2)
    .reduce((sum, t) => sum + t.amount, 0)

  return { income, expense, balance: income - expense }
})

// Methods
const formatDateStr = (date: Date) => {
  const y = date.getFullYear()
  const m = String(date.getMonth() + 1).padStart(2, '0')
  const d = String(date.getDate()).padStart(2, '0')
  return `${y}-${m}-${d}`
}

const formatAmount = (amount: number) => {
  return Math.abs(amount).toLocaleString('zh-CN', {
    minimumFractionDigits: 2,
    maximumFractionDigits: 2
  })
}

const formatGroupDate = (dateStr: string) => {
  const today = formatDateStr(new Date())
  const yesterday = formatDateStr(new Date(Date.now() - 86400000))

  if (dateStr === today) return '今天'
  if (dateStr === yesterday) return '昨天'

  const date = new Date(dateStr)
  const month = date.getMonth() + 1
  const day = date.getDate()
  const weekday = ['周日', '周一', '周二', '周三', '周四', '周五', '周六'][date.getDay()]

  return `${month}月${day}日 ${weekday}`
}

const formatTime = (timeStr: string) => {
  const time = timeStr.split(' ')[1]
  return time.substring(0, 5)
}

const getCategoryBg = (icon?: string) => {
  const bgMap: Record<string, string> = {
    food: '#FB8B24',
    transport: '#0F4C5C',
    shopping: '#EF476F',
    entertainment: '#9B59B6',
    income: '#06D6A0',
  }
  return bgMap[icon || ''] || '#6B7280'
}

const getCategoryColor = (icon?: string) => {
  const colorMap: Record<string, string> = {
    food: '#FB8B24',
    transport: '#0F4C5C',
    shopping: '#EF476F',
    entertainment: '#9B59B6',
    income: '#06D6A0',
  }
  return colorMap[icon || ''] || '#6B7280'
}

const getPeriodLabel = () => {
  const labels: Record<string, string> = {
    day: t('datetime.today'),
    week: t('datetime.thisWeek'),
    month: t('datetime.thisMonth'),
    year: t('datetime.thisYear'),
    all: t('common.all'),
  }
  return labels[activePeriod.value]
}

// Actions
const switchBook = (bookId: number) => {
  currentBookId.value = bookId
  bookStore.switchBook(bookId)
  selectedDate.value = null
}

const prevMonth = () => {
  const date = new Date(currentMonth.value)
  date.setMonth(date.getMonth() - 1)
  currentMonth.value = date
}

const nextMonth = () => {
  const date = new Date(currentMonth.value)
  date.setMonth(date.getMonth() + 1)
  currentMonth.value = date
}

const selectDate = (day: { date: number | null; dateStr: string | null }) => {
  if (!day.date || !day.dateStr) return
  selectedDate.value = day.dateStr
  activePeriod.value = 'day'
}

const switchPeriod = (period: typeof activePeriod.value) => {
  activePeriod.value = period
  if (period !== 'day') {
    selectedDate.value = null
  }
}

const loadMore = () => {
  if (loading.value || finished.value) return
  finished.value = true
}

// Navigation
const goToSearch = () => {
  uni.navigateTo({ url: '/pages/transactions/index?type=search' })
}

const goToFilter = () => {
  uni.navigateTo({ url: '/pages/transactions/index?type=filter' })
}

const goToBooks = () => {
  uni.navigateTo({ url: '/pages/books/index' })
}

const goToDetail = (item: Transaction) => {
  uni.navigateTo({ url: `/pages/transactions/detail?id=${item.id}` })
}

onMounted(async () => {
  await bookStore.loadBooks()

  if (bookStore.currentBookId) {
    currentBookId.value = bookStore.currentBookId
  } else if (bookStore.books.length > 0) {
    currentBookId.value = bookStore.books[0].id
  }
})
</script>

<style lang="scss" scoped>
.bills-page {
  min-height: 100vh;
  background: var(--gzang-bg);
  position: relative;
}

// ================== Large Title Navigation ==================
.nav-large-title {
  background: var(--gzang-bg);
  padding: 0 var(--apple-space-4);
  padding-top: calc(constant(safe-area-inset-top) + var(--apple-space-3));
}

.nav-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--apple-space-3);
}

.nav-titles {
  display: flex;
  flex-direction: column;
}

.nav-title {
  font-size: var(--apple-text-4xl);
  font-weight: var(--apple-font-bold);
  color: var(--gzang-text-primary);
  letter-spacing: -0.5px;
}

.nav-actions {
  display: flex;
  align-items: center;
  gap: var(--apple-space-3);
}

.nav-action {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background: var(--gzang-surface);
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: var(--apple-shadow-xs);
  transition: all var(--apple-duration-fast) var(--apple-ease-out);
  
  &:active {
    transform: scale(0.95);
    background: var(--gzang-bg);
  }
}

// ================== Book Switcher ==================
.book-switcher {
  padding: var(--apple-space-3) var(--apple-space-4);
}

.book-scroll {
  display: flex;
  white-space: nowrap;
}

.book-chip {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 8px 14px;
  background: var(--gzang-surface);
  border-radius: var(--apple-radius-full);
  margin-right: var(--apple-space-2);
  transition: all var(--apple-duration-fast) var(--apple-ease-out);
  box-shadow: var(--apple-shadow-xs);
  
  &.active {
    background: var(--gzang-primary);
    
    .book-chip-name {
      color: white;
    }
  }
  
  &.add-book {
    background: transparent;
    border: 1.5px dashed var(--gzang-border);
    padding: 8px 12px;
  }
}

.book-chip-name {
  font-size: var(--apple-text-xs);
  color: var(--gzang-text-secondary);
  font-weight: var(--apple-font-medium);
}

// ================== Main Content ==================
.main-content {
  padding: 0 var(--apple-space-4);
}

// ================== Calendar Card ==================
.calendar-card {
  background: var(--gzang-surface);
  border-radius: var(--apple-radius-xl);
  padding: var(--apple-space-4);
  margin-bottom: var(--apple-space-4);
  box-shadow: var(--apple-shadow-sm);
}

.calendar-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--apple-space-4);
}

.month-nav {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background: var(--gzang-bg);
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all var(--apple-duration-fast) var(--apple-ease-out);
  
  &:active {
    background: var(--gzang-border);
  }
}

.month-title {
  font-size: var(--apple-text-lg);
  font-weight: var(--apple-font-semibold);
  color: var(--gzang-text-primary);
}

.weekday-row {
  display: flex;
  margin-bottom: var(--apple-space-2);
}

.weekday {
  flex: 1;
  text-align: center;
  font-size: var(--apple-text-xs);
  color: var(--gzang-text-tertiary);
  font-weight: var(--apple-font-medium);
}

.days-grid {
  display: flex;
  flex-wrap: wrap;
}

.day-cell {
  width: calc(100% / 7);
  aspect-ratio: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  position: relative;
  
  &.is-empty {
    pointer-events: none;
  }
  
  &.is-today {
    .day-text {
      background: var(--gzang-primary);
      color: white;
      border-radius: 50%;
      width: 32px;
      height: 32px;
      display: flex;
      align-items: center;
      justify-content: center;
    }
  }
  
  &.is-selected {
    .day-text {
      background: rgba(251, 139, 36, 0.15);
      color: var(--gzang-secondary);
      border-radius: 50%;
      width: 32px;
      height: 32px;
      display: flex;
      align-items: center;
      justify-content: center;
      font-weight: var(--apple-font-semibold);
    }
  }
}

.day-text {
  font-size: var(--apple-text-sm);
  color: var(--gzang-text-primary);
}

.day-indicator {
  position: absolute;
  bottom: 4px;
  width: 4px;
  height: 4px;
  border-radius: 50%;
  background: var(--gzang-secondary);
}

// Period Tabs
.period-tabs {
  display: flex;
  background: var(--gzang-bg);
  border-radius: var(--apple-radius-sm);
  padding: 2px;
  margin-top: var(--apple-space-3);
}

.period-tab {
  flex: 1;
  padding: var(--apple-space-2) 0;
  text-align: center;
  font-size: var(--apple-text-xs);
  color: var(--gzang-text-secondary);
  border-radius: calc(var(--apple-radius-sm) - 2px);
  transition: all var(--apple-duration-fast) var(--apple-ease-out);
  
  &.active {
    background: var(--gzang-surface);
    color: var(--gzang-text-primary);
    font-weight: var(--apple-font-semibold);
    box-shadow: var(--apple-shadow-xs);
  }
}

// ================== Stats Card ==================
.stats-card {
  background: linear-gradient(145deg, var(--gzang-primary) 0%, var(--gzang-primary-light) 100%);
  border-radius: var(--apple-radius-xl);
  padding: var(--apple-space-5);
  margin-bottom: var(--apple-space-4);
  color: white;
  position: relative;
  overflow: hidden;
  
  &::before {
    content: '';
    position: absolute;
    top: -40px;
    right: -40px;
    width: 150px;
    height: 150px;
    border-radius: 50%;
    background: rgba(255, 255, 255, 0.05);
  }
}

.stats-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--apple-space-4);
}

.stats-period {
  font-size: var(--apple-text-sm);
  opacity: 0.8;
}

.stats-income {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: var(--apple-text-sm);
  color: var(--gzang-success);
  font-weight: var(--apple-font-medium);
}

.stats-body {
  position: relative;
  z-index: 1;
}

.stats-main {
  display: flex;
  align-items: center;
}

.stat-item {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.stat-label {
  font-size: var(--apple-text-xs);
  opacity: 0.7;
  margin-bottom: var(--apple-space-1);
}

.stat-value {
  font-size: var(--apple-text-2xl);
  font-weight: var(--apple-font-bold);
  
  &.income {
    color: var(--gzang-success);
  }
  
  &.expense {
    color: rgba(255, 209, 102, 1);
  }
}

.stat-divider {
  width: 1px;
  height: 48px;
  background: rgba(255, 255, 255, 0.2);
  margin: 0 var(--apple-space-5);
}

// ================== Transaction Section ==================
.section-transactions {
  background: var(--gzang-surface);
  border-radius: var(--apple-radius-xl);
  padding: var(--apple-space-4);
  box-shadow: var(--apple-shadow-sm);
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--apple-space-4);
}

.section-title {
  font-size: var(--apple-text-lg);
  font-weight: var(--apple-font-semibold);
  color: var(--gzang-text-primary);
}

.section-count {
  font-size: var(--apple-text-sm);
  color: var(--gzang-text-tertiary);
}

// Transaction Groups
.transaction-group {
  margin-bottom: var(--apple-space-5);
  
  &:last-child {
    margin-bottom: 0;
  }
}

.group-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--apple-space-3);
}

.group-date {
  font-size: var(--apple-text-sm);
  font-weight: var(--apple-font-semibold);
  color: var(--gzang-text-primary);
}

.group-stats {
  display: flex;
  gap: var(--apple-space-3);
}

.group-income {
  font-size: var(--apple-text-xs);
  color: var(--gzang-success);
  font-weight: var(--apple-font-medium);
}

.group-expense {
  font-size: var(--apple-text-xs);
  color: var(--gzang-text-secondary);
}

.group-items {
  display: flex;
  flex-direction: column;
  gap: var(--apple-space-2);
}

.transaction-item {
  display: flex;
  align-items: center;
  padding: var(--apple-space-3);
  background: var(--gzang-bg);
  border-radius: var(--apple-radius-md);
  transition: all var(--apple-duration-fast) var(--apple-ease-out);
  
  &:active {
    background: var(--gzang-border);
  }
}

.item-icon {
  width: 40px;
  height: 40px;
  border-radius: var(--apple-radius-sm);
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: var(--apple-space-3);
  flex-shrink: 0;
}

.item-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-width: 0;
}

.item-category {
  font-size: var(--apple-text-sm);
  font-weight: var(--apple-font-medium);
  color: var(--gzang-text-primary);
}

.item-account {
  font-size: var(--apple-text-xs);
  color: var(--gzang-text-tertiary);
  margin-top: 2px;
}

.item-right {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  flex-shrink: 0;
}

.item-amount {
  font-size: var(--apple-text-sm);
  font-weight: var(--apple-font-semibold);
  
  &.income {
    color: var(--gzang-success);
  }
  
  &.expense {
    color: var(--gzang-text-primary);
  }
}

.item-time {
  font-size: var(--apple-text-xs);
  color: var(--gzang-text-tertiary);
  margin-top: 2px;
}

// Empty State
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: var(--apple-space-10) var(--apple-space-4);
}

.empty-icon-wrapper {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  background: var(--gzang-bg);
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: var(--apple-space-4);
}

.empty-title {
  font-size: var(--apple-text-lg);
  font-weight: var(--apple-font-semibold);
  color: var(--gzang-text-primary);
  margin-bottom: var(--apple-space-2);
}

.empty-desc {
  font-size: var(--apple-text-sm);
  color: var(--gzang-text-secondary);
  text-align: center;
}

// Loading
.loading-more {
  display: flex;
  justify-content: center;
  padding: var(--apple-space-4);
}

.loading-spinner {
  width: 24px;
  height: 24px;
  border: 2px solid var(--gzang-border);
  border-top-color: var(--gzang-primary);
  border-radius: 50%;
  animation: apple-spin 1s linear infinite;
}

@keyframes apple-spin {
  to { transform: rotate(360deg); }
}

.no-more {
  text-align: center;
  padding: var(--apple-space-4);
  font-size: var(--apple-text-xs);
  color: var(--gzang-text-tertiary);
}

// Bottom Safe Area
.bottom-safe-area {
  height: calc(var(--apple-space-4) + 84px);
}

// ================== Dark Mode ==================
@media (prefers-color-scheme: dark) {
  .nav-large-title {
    background: var(--gzang-bg, #000000);
  }
  
  .nav-title {
    color: var(--gzang-text-primary, #FFFFFF);
  }
  
  .calendar-card,
  .section-transactions {
    background: var(--gzang-surface, #1C1C1E);
  }
  
  .day-text {
    color: var(--gzang-text-primary, #FFFFFF);
  }
  
  .group-date,
  .item-category {
    color: var(--gzang-text-primary, #FFFFFF);
  }
}

[data-theme="dark"] {
  .nav-large-title {
    background: var(--gzang-bg, #000000);
  }
  
  .nav-title {
    color: var(--gzang-text-primary, #FFFFFF);
  }
}
</style>
