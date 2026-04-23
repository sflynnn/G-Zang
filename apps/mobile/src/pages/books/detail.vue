<template>
  <view class="book-detail-page">
    <!-- 顶部导航 -->
    <view class="nav-bar">
      <view class="nav-left" @click="goBack">
        <uni-icons type="left" size="20" color="#333"></uni-icons>
      </view>
      <view class="nav-title">账本详情</view>
      <view class="nav-right" @click="goToEdit">
        <uni-icons type="edit" size="20" color="#0F4C5C"></uni-icons>
      </view>
    </view>

    <scroll-view class="main-content" scroll-y="true">
      <!-- 账本信息卡片 -->
      <view
        class="book-header"
        :style="{
          background: `linear-gradient(135deg, ${book?.color || '#0F4C5C'} 0%, ${darkenColor(book?.color || '#0F4C5C')} 100%)`
        }"
      >
        <view class="header-top">
          <view class="book-icon">{{ book?.icon }}</view>
          <view class="book-type-badge">{{ getBookTypeName(book?.type) }}</view>
        </view>
        <view class="book-name">{{ book?.name }}</view>
        <view class="book-currency">{{ book?.currencySymbol }} {{ book?.currency }}</view>
      </view>

      <!-- 统计概览 -->
      <view class="stats-section">
        <view class="stats-grid">
          <view class="stat-item">
            <text class="stat-value income">+{{ formatAmount(book?.totalIncome || 0) }}</text>
            <text class="stat-label">总收入</text>
          </view>
          <view class="stat-divider"></view>
          <view class="stat-item">
            <text class="stat-value expense">-{{ formatAmount(book?.totalExpense || 0) }}</text>
            <text class="stat-label">总支出</text>
          </view>
          <view class="stat-divider"></view>
          <view class="stat-item">
            <text class="stat-value" :class="balance >= 0 ? 'income' : 'expense'">
              {{ formatAmount(balance) }}
            </text>
            <text class="stat-label">结余</text>
          </view>
        </view>
      </view>

      <!-- 周期统计 -->
      <view v-if="statistics" class="period-section">
        <view class="section-title">本期统计</view>
        <view class="period-tabs">
          <view
            v-for="period in periodList"
            :key="period.key"
            class="period-tab"
            :class="{ active: activePeriod === period.key }"
            @click="activePeriod = period.key"
          >
            <text class="period-name">{{ period.name }}</text>
            <view class="period-stats">
              <text class="period-income">收 {{ formatAmount(getPeriodIncome(period.key)) }}</text>
              <text class="period-expense">支 {{ formatAmount(getPeriodExpense(period.key)) }}</text>
            </view>
          </view>
        </view>
      </view>

      <!-- 操作菜单 -->
      <view class="menu-section">
        <view class="menu-item" @click="goToCategories">
          <view class="menu-left">
            <uni-icons type="bars" size="20" color="#0F4C5C"></uni-icons>
            <text class="menu-text">分类管理</text>
          </view>
          <view class="menu-right">
            <text class="menu-count">{{ book?.categoryCount }} 个分类</text>
            <uni-icons type="right" size="16" color="#ccc"></uni-icons>
          </view>
        </view>
        <view class="menu-item" @click="goToTransactions">
          <view class="menu-left">
            <uni-icons type="wallet" size="20" color="#0F4C5C"></uni-icons>
            <text class="menu-text">交易记录</text>
          </view>
          <view class="menu-right">
            <text class="menu-count">{{ book?.transactionCount }} 条</text>
            <uni-icons type="right" size="16" color="#ccc"></uni-icons>
          </view>
        </view>
        <view v-if="!book?.isDefault" class="menu-item" @click="handleSetDefault">
          <view class="menu-left">
            <uni-icons type="star" size="20" color="#FB8B24"></uni-icons>
            <text class="menu-text">设为默认账本</text>
          </view>
          <uni-icons type="right" size="16" color="#ccc"></uni-icons>
        </view>
      </view>

      <!-- 删除按钮 -->
      <view v-if="!book?.isDefault" class="danger-zone">
        <button class="delete-btn" @click="handleDelete">删除账本</button>
      </view>
    </scroll-view>
  </view>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useBookStore } from '@/stores/book'
import { useAppStore } from '@/stores/app'

const bookStore = useBookStore()
const appStore = useAppStore()

const bookId = ref<number | null>(null)
const activePeriod = ref<'today' | 'thisWeek' | 'thisMonth' | 'thisYear'>('thisMonth')

const book = computed(() => bookStore.books.find(b => b.id === bookId.value))
const statistics = computed(() =>
  bookId.value ? bookStore.statistics[bookId.value] : null
)
const balance = computed(() =>
  (book.value?.totalIncome || 0) - (book.value?.totalExpense || 0)
)

const periodList = [
  { key: 'today' as const, name: '今日' },
  { key: 'thisWeek' as const, name: '本周' },
  { key: 'thisMonth' as const, name: '本月' },
  { key: 'thisYear' as const, name: '本年' },
]

const formatAmount = (amount: number) => {
  return Math.abs(amount).toLocaleString('zh-CN', {
    minimumFractionDigits: 2,
    maximumFractionDigits: 2,
  })
}

const getBookTypeName = (type?: string) => {
  if (!type) return ''
  const typeMap: Record<string, string> = {
    PERSONAL: '个人',
    FAMILY: '家庭',
    TEAM: '团队',
    BUSINESS: '业务',
    OTHER: '其他',
  }
  return typeMap[type] || type
}

const getPeriodIncome = (period: 'today' | 'thisWeek' | 'thisMonth' | 'thisYear') => {
  if (!statistics.value) return 0
  return statistics.value.periodStats[period]?.income || 0
}

const getPeriodExpense = (period: 'today' | 'thisWeek' | 'thisMonth' | 'thisYear') => {
  if (!statistics.value) return 0
  return statistics.value.periodStats[period]?.expense || 0
}

const darkenColor = (hex: string) => {
  const color = hex.replace('#', '')
  const r = Math.max(0, parseInt(color.substring(0, 2), 16) - 30)
  const g = Math.max(0, parseInt(color.substring(2, 4), 16) - 30)
  const b = Math.max(0, parseInt(color.substring(4, 6), 16) - 30)
  return `#${r.toString(16).padStart(2, '0')}${g.toString(16).padStart(2, '0')}${b.toString(16).padStart(2, '0')}`
}

const goBack = () => {
  uni.navigateBack()
}

const goToEdit = () => {
  if (bookId.value) {
    uni.navigateTo({ url: `/pages/books/create?id=${bookId.value}` })
  }
}

const goToCategories = () => {
  uni.navigateTo({ url: `/pages/categories/index?bookId=${bookId.value}` })
}

const goToTransactions = () => {
  uni.navigateTo({ url: `/pages/transactions/index?bookId=${bookId.value}` })
}

const handleSetDefault = async () => {
  if (!bookId.value) return
  try {
    await bookStore.setDefaultBook(bookId.value)
    appStore.showSuccess('已设为默认账本')
  } catch (error: any) {
    appStore.showError(error.message || '设置失败')
  }
}

const handleDelete = () => {
  uni.showModal({
    title: '确认删除',
    content: '删除后账本数据将无法恢复，确定要删除吗？',
    confirmColor: '#EF476F',
    success: async (res) => {
      if (res.confirm && bookId.value) {
        try {
          await bookStore.deleteBook(bookId.value)
          appStore.showSuccess('账本已删除')
          setTimeout(() => {
            uni.navigateBack()
          }, 1000)
        } catch (error: any) {
          appStore.showError(error.message || '删除失败')
        }
      }
    },
  })
}

onMounted(async () => {
  const pages = getCurrentPages()
  const currentPage = pages[pages.length - 1]
  const options = currentPage.options || {}

  if (options.id) {
    bookId.value = parseInt(options.id as string)
  }

  if (!bookStore.books.length) {
    await bookStore.loadBooks()
  }
})
</script>

<style lang="scss" scoped>
.book-detail-page {
  min-height: 100vh;
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
  border-bottom: 1px solid #E5E5E5;
}

.nav-left, .nav-right {
  width: 60px;
  display: flex;
  align-items: center;
}

.nav-right {
  justify-content: flex-end;
}

.nav-title {
  font-size: 18px;
  font-weight: 600;
  color: #333;
}

.main-content {
  flex: 1;
  padding-bottom: 40px;
}

// 账本头部
.book-header {
  padding: 24px 20px;
  color: #fff;
}

.header-top {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 16px;
}

.book-icon {
  width: 64px;
  height: 64px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 32px;
}

.book-type-badge {
  background: rgba(255, 255, 255, 0.2);
  padding: 6px 12px;
  border-radius: 12px;
  font-size: 13px;
}

.book-name {
  font-size: 24px;
  font-weight: 600;
  margin-bottom: 4px;
}

.book-currency {
  font-size: 14px;
  opacity: 0.8;
}

// 统计概览
.stats-section {
  background: #fff;
  margin: -20px 16px 16px;
  border-radius: 16px;
  padding: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}

.stats-grid {
  display: flex;
  align-items: center;
}

.stat-item {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.stat-value {
  font-size: 18px;
  font-weight: 600;
  margin-bottom: 4px;

  &.income {
    color: #06D6A0;
  }

  &.expense {
    color: #EF476F;
  }
}

.stat-label {
  font-size: 12px;
  color: #999;
}

.stat-divider {
  width: 1px;
  height: 40px;
  background: #E5E5E5;
}

// 周期统计
.period-section {
  background: #fff;
  margin: 0 16px 16px;
  border-radius: 16px;
  padding: 20px;
}

.section-title {
  font-size: 15px;
  font-weight: 600;
  color: #333;
  margin-bottom: 16px;
}

.period-tabs {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.period-tab {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 14px 16px;
  background: #F8F9FA;
  border-radius: 10px;
  transition: all 0.2s;

  &.active {
    background: rgba(15, 76, 92, 0.08);
    border: 1px solid #0F4C5C;
  }
}

.period-name {
  font-size: 15px;
  font-weight: 500;
  color: #333;
}

.period-stats {
  display: flex;
  gap: 16px;
}

.period-income {
  font-size: 14px;
  color: #06D6A0;
}

.period-expense {
  font-size: 14px;
  color: #EF476F;
}

// 操作菜单
.menu-section {
  background: #fff;
  margin: 0 16px 16px;
  border-radius: 16px;
  overflow: hidden;
}

.menu-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  border-bottom: 1px solid #F0F0F0;
  transition: background-color 0.2s;

  &:last-child {
    border-bottom: none;
  }

  &:active {
    background: #F8F9FA;
  }
}

.menu-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.menu-text {
  font-size: 15px;
  color: #333;
}

.menu-right {
  display: flex;
  align-items: center;
  gap: 8px;
}

.menu-count {
  font-size: 13px;
  color: #999;
}

// 危险区
.danger-zone {
  margin: 24px 16px 0;
}

.delete-btn {
  width: 100%;
  height: 48px;
  background: #fff;
  color: #EF476F;
  border: 1px solid #EF476F;
  border-radius: 10px;
  font-size: 16px;
  font-weight: 500;
  display: flex;
  align-items: center;
  justify-content: center;

  &:active {
    background: #FFF5F5;
  }
}
</style>
