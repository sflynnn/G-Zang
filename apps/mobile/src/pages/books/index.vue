<template>
  <view class="books-page apple-style">
    <!-- Large Title Navigation -->
    <view class="nav-large-title">
      <view class="nav-header">
        <text class="nav-title">{{ t('book.title') }}</text>
        <view class="nav-add" @click="goToCreate">
          <AppleIcon name="plus" :size="20" color="var(--gzang-primary)" />
        </view>
      </view>
    </view>

    <scroll-view class="main-content" scroll-y="true">
      <!-- Default Book Card -->
      <view v-if="defaultBook" class="default-book-section">
        <view class="section-label">默认账本</view>
        <view class="book-card default-card" :style="{ '--book-color': defaultBook.color || 'var(--gzang-primary)' }">
          <view class="book-card-header">
            <view class="book-icon">
              <AppleIcon name="book" :size="24" color="white" />
            </view>
            <view class="book-info">
              <text class="book-name">{{ defaultBook.name }}</text>
              <text class="book-meta">{{ getBookTypeName(defaultBook.type) }}</text>
            </view>
            <view class="book-badge">默认</view>
          </view>
          <view class="book-card-body">
            <view class="book-stat">
              <text class="stat-label">{{ t('analysis.income') }}</text>
              <text class="stat-value income">+{{ formatAmount(defaultBook.totalIncome || 0) }}</text>
            </view>
            <view class="book-stat">
              <text class="stat-label">{{ t('analysis.expense') }}</text>
              <text class="stat-value expense">-{{ formatAmount(defaultBook.totalExpense || 0) }}</text>
            </view>
            <view class="book-stat">
              <text class="stat-label">{{ t('analysis.balance') }}</text>
              <text class="stat-value balance">{{ formatAmount((defaultBook.totalIncome || 0) - (defaultBook.totalExpense || 0)) }}</text>
            </view>
          </view>
          <view class="book-card-footer" @click="goToDetail(defaultBook)">
            <text>查看详情</text>
            <AppleIcon name="chevron-right" :size="14" color="rgba(255,255,255,0.6)" />
          </view>
        </view>
      </view>

      <!-- Other Books -->
      <view v-if="otherBooks.length > 0" class="other-books-section">
        <view class="section-label">其他账本</view>
        <view class="books-list">
          <view 
            v-for="book in otherBooks" 
            :key="book.id"
            class="book-card simple-card"
            @click="goToDetail(book)"
          >
            <view class="book-card-header">
              <view class="book-icon simple">
                <AppleIcon name="book" :size="20" color="var(--gzang-primary)" />
              </view>
              <view class="book-info">
                <text class="book-name">{{ book.name }}</text>
                <text class="book-meta">{{ getBookTypeName(book.type) }}</text>
              </view>
              <AppleIcon name="chevron-right" :size="18" color="var(--gzang-text-tertiary)" />
            </view>
          </view>
        </view>
      </view>

      <!-- Empty State -->
      <view v-if="books.length === 0 && !loading" class="empty-state">
        <view class="empty-icon-wrapper">
          <AppleIcon name="book" :size="48" color="var(--gzang-text-tertiary)" />
        </view>
        <text class="empty-title">还没有账本</text>
        <text class="empty-desc">创建你的第一个账本开始记账吧</text>
        <button class="create-btn" @click="goToCreate">
          <AppleIcon name="plus" :size="16" color="white" />
          <text>创建账本</text>
        </button>
      </view>

      <!-- Add Book Button -->
      <view v-if="books.length > 0" class="add-book-btn" @click="goToCreate">
        <AppleIcon name="plus" :size="18" color="var(--gzang-primary)" />
        <text class="btn-text">添加新账本</text>
      </view>

      <view class="bottom-safe-area"></view>
    </scroll-view>
  </view>
</template>

<script setup lang="ts">
import { computed, onMounted } from 'vue'
import { useI18n } from 'vue-i18n'
import { useBookStore } from '@/stores/book'
import type { Book } from '@/types/book'
import AppleIcon from '@/components/common/AppleIcon/index.vue'

const { t } = useI18n()
const bookStore = useBookStore()

const books = computed(() => bookStore.books || [])
const defaultBook = computed(() => books.value.find(b => b.isDefault) || books.value[0])
const otherBooks = computed(() => books.value.filter(b => !b.isDefault))
const loading = computed(() => bookStore.loading)

const formatAmount = (amount: number) => {
  return Math.abs(amount).toLocaleString('zh-CN', {
    minimumFractionDigits: 2,
    maximumFractionDigits: 2
  })
}

const getBookTypeName = (type: string) => {
  const typeMap: Record<string, string> = {
    PERSONAL: '个人',
    FAMILY: '家庭',
    TEAM: '团队',
    BUSINESS: '业务',
    OTHER: '其他',
  }
  return typeMap[type] || type
}

const goToCreate = () => uni.navigateTo({ url: '/pages/books/create' })
const goToDetail = (book: Book) => uni.navigateTo({ url: `/pages/books/detail?id=${book.id}` })

onMounted(async () => {
  await bookStore.loadBooks()
})
</script>

<style lang="scss" scoped>
.books-page {
  min-height: 100vh;
  background: var(--gzang-bg);
}

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

.nav-title {
  font-size: var(--apple-text-4xl);
  font-weight: var(--apple-font-bold);
  color: var(--gzang-text-primary);
}

.nav-add {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background: var(--gzang-surface);
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: var(--apple-shadow-xs);
}

.main-content {
  padding: 0 var(--apple-space-4);
}

.section-label {
  font-size: var(--apple-text-sm);
  font-weight: var(--apple-font-medium);
  color: var(--gzang-text-secondary);
  margin-bottom: var(--apple-space-3);
  padding: 0 var(--apple-space-2);
}

.default-book-section {
  margin-bottom: var(--apple-space-5);
}

.book-card {
  border-radius: var(--apple-radius-xl);
  padding: var(--apple-space-5);
  color: white;
  position: relative;
  overflow: hidden;
  
  &.default-card {
    background: linear-gradient(145deg, var(--book-color, var(--gzang-primary)) 0%, var(--book-color, var(--gzang-primary)) 100%);
    box-shadow: 0 8px 32px rgba(15, 76, 92, 0.25);
    
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
  
  &.simple-card {
    background: var(--gzang-surface);
    box-shadow: var(--apple-shadow-sm);
    padding: var(--apple-space-4);
    
    &:active { background: var(--gzang-bg); }
  }
}

.book-card-header {
  display: flex;
  align-items: center;
  margin-bottom: var(--apple-space-4);
  position: relative;
  z-index: 1;
}

.book-icon {
  width: 48px;
  height: 48px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: var(--apple-radius-md);
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: var(--apple-space-3);
  
  &.simple {
    background: rgba(15, 76, 92, 0.1);
  }
}

.book-info {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.book-name {
  font-size: var(--apple-text-lg);
  font-weight: var(--apple-font-semibold);
  margin-bottom: 2px;
  
  .simple-card & { color: var(--gzang-text-primary); }
}

.book-meta {
  font-size: var(--apple-text-xs);
  opacity: 0.7;
  
  .simple-card & { color: var(--gzang-text-secondary); opacity: 1; }
}

.book-badge {
  background: rgba(255, 255, 255, 0.2);
  padding: 4px 10px;
  border-radius: var(--apple-radius-full);
  font-size: var(--apple-text-xs);
  font-weight: var(--apple-font-medium);
  
  .simple-card & {
    background: rgba(15, 76, 92, 0.1);
    color: var(--gzang-primary);
  }
}

.book-card-body {
  display: flex;
  justify-content: space-between;
  margin-bottom: var(--apple-space-4);
  position: relative;
  z-index: 1;
}

.book-stat {
  display: flex;
  flex-direction: column;
}

.stat-label {
  font-size: var(--apple-text-xs);
  opacity: 0.7;
  margin-bottom: 4px;
}

.stat-value {
  font-size: var(--apple-text-base);
  font-weight: var(--apple-font-semibold);
  
  &.income { color: var(--gzang-success); }
  &.expense { color: rgba(255, 209, 102, 1); }
  &.balance { color: white; }
}

.book-card-footer {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 4px;
  padding-top: var(--apple-space-3);
  border-top: 1px solid rgba(255, 255, 255, 0.2);
  font-size: var(--apple-text-sm);
  opacity: 0.8;
}

.other-books-section {
  margin-bottom: var(--apple-space-5);
}

.books-list {
  display: flex;
  flex-direction: column;
  gap: var(--apple-space-3);
}

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
  margin-bottom: var(--apple-space-5);
}

.create-btn {
  display: flex;
  align-items: center;
  gap: var(--apple-space-2);
  padding: var(--apple-space-3) var(--apple-space-5);
  background: var(--gzang-primary);
  color: white;
  border: none;
  border-radius: var(--apple-radius-full);
  font-size: var(--apple-text-sm);
  font-weight: var(--apple-font-semibold);
  box-shadow: 0 4px 12px rgba(15, 76, 92, 0.3);
}

.add-book-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: var(--apple-space-2);
  padding: var(--apple-space-4);
  background: var(--gzang-surface);
  border: 1.5px dashed var(--gzang-primary);
  border-radius: var(--apple-radius-xl);
  margin-top: var(--apple-space-4);
}

.btn-text {
  font-size: var(--apple-text-base);
  font-weight: var(--apple-font-medium);
  color: var(--gzang-primary);
}

.bottom-safe-area {
  height: var(--apple-space-4);
}
</style>
