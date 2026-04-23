<template>
  <view class="home-page apple-style">
    <!-- Large Title Navigation -->
    <view class="nav-large-title">
      <view class="nav-header">
        <view class="nav-titles">
          <text class="nav-title">{{ t('home.today') }}</text>
          <text class="nav-subtitle">{{ currentDate }}</text>
        </view>
        <view class="nav-actions">
          <view class="nav-avatar" @click="goToProfile">
            <text class="avatar-text">{{ userInitials }}</text>
          </view>
        </view>
      </view>
    </view>

    <!-- Main Content -->
    <view class="main-content">
      <scroll-view
        class="content-scroll"
        scroll-y="true"
        :scroll-top="scrollTop"
        @scrolltolower="onScrollToLower"
      >
      <!-- Asset Card - Gradient Style -->
      <view class="asset-card-wrapper">
        <view class="asset-card" @click="goToAccounts">
          <view class="asset-card-header">
            <view class="book-badge">
              <AppleIcon name="book" :size="14" color="white" />
              <text class="book-name">{{ currentBook?.name || t('book.defaultBook') }}</text>
            </view>
            <AppleIcon name="chevron-right" :size="16" color="rgba(255,255,255,0.6)" />
          </view>
          
          <view class="asset-main">
            <text class="asset-label">{{ t('home.totalAssets') }}</text>
            <text class="asset-amount amount">{{ formatAmount(totalAssets) }}</text>
          </view>
          
          <view class="asset-stats">
            <view class="stat-item">
              <view class="stat-icon">
                <AppleIcon name="income" :size="14" color="rgba(255,255,255,0.6)" />
              </view>
              <view class="stat-content">
                <text class="stat-label">{{ t('home.monthlyIncome') }}</text>
                <text class="stat-value income amount">+{{ formatAmount(monthlyIncome) }}</text>
              </view>
            </view>
            <view class="stat-divider"></view>
            <view class="stat-item">
              <view class="stat-icon">
                <AppleIcon name="expense" :size="14" color="rgba(255,255,255,0.6)" />
              </view>
              <view class="stat-content">
                <text class="stat-label">{{ t('home.monthlyExpense') }}</text>
                <text class="stat-value expense amount">-{{ formatAmount(monthlyExpense) }}</text>
              </view>
            </view>
          </view>
        </view>
      </view>

      <!-- Book Switcher -->
      <view class="section-book-switcher" v-if="books.length > 1">
        <scroll-view class="book-scroll" scroll-x="true" enhanced="true" show-scrollbar="false">
          <view 
            v-for="book in books" 
            :key="book.id"
            class="book-chip"
            :class="{ active: currentBookId === book.id }"
            @click="switchBook(book)"
          >
            <AppleIcon name="book" :size="12" :color="currentBookId === book.id ? 'white' : 'text-secondary'" />
            <text class="book-chip-name">{{ book.name }}</text>
          </view>
        </scroll-view>
      </view>

      <!-- Quick Actions - Apple Grid Style -->
      <view class="section-quick-actions">
        <text class="section-title">{{ t('home.quickActions') }}</text>
        <view class="quick-actions-grid">
          <view 
            v-for="(action, index) in quickActions" 
            :key="action.key"
            class="quick-action-item"
            :class="'stagger-' + (index + 1)"
            @click="handleAction(action)"
          >
            <view class="action-icon" :style="{ background: action.bgColor }">
              <AppleIcon :name="action.icon" :size="20" :color="action.color" />
            </view>
            <text class="action-label">{{ action.label }}</text>
          </view>
        </view>
      </view>

      <!-- Recent Transactions - Apple List Style -->
      <view class="section-recent">
        <view class="section-header">
          <text class="section-title">{{ t('home.recentTransactions') }}</text>
          <view class="section-more" @click="goToTransactions">
            <text class="more-text">{{ t('home.viewAll') }}</text>
            <AppleIcon name="chevron-right" :size="14" color="var(--gzang-secondary)" />
          </view>
        </view>
        
        <view class="transaction-list apple-list">
          <template v-if="recentTransactions.length > 0">
            <view 
              v-for="(transaction, index) in recentTransactions" 
              :key="transaction.id"
              class="transaction-item apple-list-item"
              @click="goToTransactionDetail(transaction)"
            >
              <view class="transaction-icon" :style="{ background: transaction.categoryColor + '20' }">
                <AppleIcon :name="transaction.categoryIcon || 'shopping'" :size="18" :color="transaction.categoryColor" />
              </view>
              <view class="transaction-info">
                <text class="transaction-name">{{ transaction.categoryName }}</text>
                <text class="transaction-account">{{ transaction.accountName }}</text>
              </view>
              <text 
                class="transaction-amount amount"
                :class="transaction.type === 1 ? 'income' : 'expense'"
              >
                {{ transaction.type === 1 ? '+' : '-' }}{{ formatAmount(transaction.amount) }}
              </text>
            </view>
          </template>
          
          <!-- Empty State -->
          <view v-else class="empty-state">
            <view class="empty-icon-wrapper">
              <AppleIcon name="list" :size="48" color="var(--gzang-text-tertiary)" />
            </view>
            <text class="empty-title">{{ t('home.noTransactions') }}</text>
            <text class="empty-desc">{{ t('home.noTransactionsDesc') }}</text>
            <button class="empty-action" @click="goToAccounting">
              <AppleIcon name="plus" :size="16" color="white" />
              <text>{{ t('accounting.quickRecord') }}</text>
            </button>
          </view>
        </view>
      </view>

      <!-- Bottom Safe Area -->
      <view class="bottom-safe-area"></view>
    </scroll-view>
    </view>

    <!-- 自定义 TabBar -->
    <CustomTabBar />
  </view>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { useI18n } from 'vue-i18n'
import { useAuthStore } from '@/stores/auth'
import { useBookStore } from '@/stores/book'
import { useAccountStore } from '@/stores/account'
import AppleIcon from '@/components/common/AppleIcon/index.vue'
import CustomTabBar from '@/components/CustomTabBar/index.vue'

const { t } = useI18n()

// Stores
const authStore = useAuthStore()
const bookStore = useBookStore()
const accountStore = useAccountStore()

// Refs
const isLoading = ref(false)
const scrollTop = ref(0)

// User Info
const userInitials = computed(() => {
  const name = authStore.user?.nickname || authStore.user?.username || ''
  return name.slice(0, 1).toUpperCase() || 'U'
})

// Date
const currentDate = computed(() => {
  const now = new Date()
  const weekdays = ['星期日', '星期一', '星期二', '星期三', '星期四', '星期五', '星期六']
  const month = now.getMonth() + 1
  const day = now.getDate()
  const weekday = weekdays[now.getDay()]
  return `${month}月${day}日 ${weekday}`
})

// Greeting based on time
const greeting = computed(() => {
  const hour = new Date().getHours()
  if (hour < 6) return t('home.greeting.night')
  if (hour < 9) return t('home.greeting.morning')
  if (hour < 14) return t('home.greeting.morning')
  if (hour < 18) return t('home.greeting.afternoon')
  return t('home.greeting.evening')
})

// Book Info
const books = computed(() => bookStore.bookList || [])
const currentBookId = computed(() => bookStore.currentBook?.id)
const currentBook = computed(() => bookStore.currentBook)

// Account Stats
const totalAssets = computed(() => accountStore.netAssets || 0)
const monthlyIncome = computed(() => {
  const stats = bookStore.currentStatistics
  return stats?.periodStats?.thisMonth?.income || 0
})
const monthlyExpense = computed(() => {
  const stats = bookStore.currentStatistics
  return stats?.periodStats?.thisMonth?.expense || 0
})

// Quick Actions
const quickActions = computed(() => [
  { 
    key: 'accounting', 
    icon: 'accounting', 
    label: t('accounting.quickRecord'), 
    bgColor: 'rgba(251, 139, 36, 0.12)', 
    color: 'var(--gzang-secondary)',
    path: '/pages/accounting/index'
  },
  { 
    key: 'voice', 
    icon: 'microphone', 
    label: t('accounting.voiceRecord'), 
    bgColor: 'rgba(6, 214, 160, 0.12)', 
    color: 'var(--gzang-success)',
    path: '/pages/accounting/voice'
  },
  { 
    key: 'camera', 
    icon: 'camera', 
    label: t('accounting.cameraRecord'), 
    bgColor: 'rgba(239, 71, 111, 0.12)', 
    color: 'var(--gzang-danger)',
    path: '/pages/accounting/camera'
  },
  { 
    key: 'bills', 
    icon: 'bills', 
    label: t('bills.title'), 
    bgColor: 'rgba(15, 76, 92, 0.12)', 
    color: 'var(--gzang-primary)',
    path: '/pages/bills/index'
  },
  { 
    key: 'analysis', 
    icon: 'analysis', 
    label: t('analysis.title'), 
    bgColor: 'rgba(17, 138, 178, 0.12)', 
    color: 'var(--gzang-info)',
    path: '/pages/analysis/index'
  },
  { 
    key: 'budget', 
    icon: 'chart', 
    label: t('budget.title'), 
    bgColor: 'rgba(255, 209, 102, 0.12)', 
    color: 'var(--gzang-warning)',
    path: '/pages/budget/index'
  },
])

// Recent Transactions (mock data)
const recentTransactions = ref([
  {
    id: 1,
    categoryName: '餐饮',
    categoryIcon: 'food',
    categoryColor: '#FB8B24',
    accountName: '交通银行',
    amount: 12.50,
    type: 2
  },
  {
    id: 2,
    categoryName: '地铁',
    categoryIcon: 'transport',
    categoryColor: '#0F4C5C',
    accountName: '交通银行',
    amount: 4.00,
    type: 2
  },
  {
    id: 3,
    categoryName: '工资',
    categoryIcon: 'income',
    categoryColor: '#06D6A0',
    accountName: '建设银行',
    amount: 15000.00,
    type: 1
  },
  {
    id: 4,
    categoryName: '网购',
    categoryIcon: 'shopping',
    categoryColor: '#EF476F',
    accountName: '支付宝',
    amount: 199.00,
    type: 2
  }
])

// Format Amount
const formatAmount = (amount: number) => {
  if (amount === undefined || amount === null) return '0.00'
  return Math.abs(amount).toLocaleString('zh-CN', {
    minimumFractionDigits: 2,
    maximumFractionDigits: 2
  })
}

// Actions
const switchBook = async (book: any) => {
  try {
    await bookStore.switchBook(book.id)
    await loadData()
  } catch (error) {
    // handle error
  }
}

const handleAction = (action: typeof quickActions[0]) => {
  const tabBarPages = ['/pages/home/index', '/pages/accounting/index', '/pages/bills/index', '/pages/analysis/index', '/pages/profile/index']
  if (tabBarPages.includes(action.path)) {
    uni.switchTab({ url: action.path })
  } else {
    uni.navigateTo({ url: action.path })
  }
}

const goToProfile = () => {
  uni.switchTab({ url: '/pages/profile/index' })
}

const goToAccounts = () => {
  uni.navigateTo({ url: '/pages/accounts/index' })
}

const goToTransactions = () => {
  uni.navigateTo({ url: '/pages/transactions/index' })
}

const goToTransactionDetail = (transaction: any) => {
  uni.navigateTo({ url: `/pages/bills/detail?id=${transaction.id}` })
}

const goToAccounting = () => {
  uni.switchTab({ url: '/pages/accounting/index' })
}

// Load Data
const loadData = async () => {
  if (isLoading.value) return

  try {
    isLoading.value = true
    if (books.value.length === 0) {
      await bookStore.fetchBooks().catch(() => {})
    }
    await accountStore.fetchAccounts().catch(() => {})
    if (currentBook.value) {
      await bookStore.fetchStatistics(currentBook.value.id).catch(() => {})
    }
  } finally {
    isLoading.value = false
  }
}

const onScrollToLower = () => {
  // Load more if needed
}

// Lifecycle
let isRedirectingToLogin = false

onMounted(async () => {
  if (!authStore.isAuthenticated && !isRedirectingToLogin) {
    isRedirectingToLogin = true
    await new Promise(resolve => setTimeout(resolve, 150))
    uni.reLaunch({ url: '/pages/login/index' })
    return
  }
  await loadData()
})

onShow(() => {
  if (authStore.isAuthenticated) {
    loadData()
  } else if (!isRedirectingToLogin) {
    isRedirectingToLogin = true
    uni.reLaunch({ url: '/pages/login/index' })
  }
})
</script>

<style lang="scss" scoped>
.home-page {
  min-height: 100vh;
  background: var(--gzang-bg);
  position: relative;
}

// ================== Large Title Navigation ==================
.nav-large-title {
  background: var(--gzang-bg);
  padding: 0 var(--apple-space-4);
  padding-top: calc(constant(safe-area-inset-top) + var(--apple-space-3));
  padding-top: calc(env(safe-area-inset-top) + var(--apple-space-3));
}

.nav-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
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

.nav-subtitle {
  font-size: var(--apple-text-sm);
  color: var(--gzang-text-secondary);
  margin-top: var(--apple-space-1);
  font-weight: var(--apple-font-medium);
}

.nav-actions {
  display: flex;
  align-items: center;
  gap: var(--apple-space-3);
}

.nav-avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background: linear-gradient(135deg, var(--gzang-secondary) 0%, var(--gzang-secondary-dark) 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 2px 8px rgba(251, 139, 36, 0.3);
}

.avatar-text {
  font-size: var(--apple-text-sm);
  font-weight: var(--apple-font-bold);
  color: white;
}

// ================== Main Content ==================
.main-content {
  flex: 1;
}

// ================== Asset Card ==================
.asset-card-wrapper {
  padding: var(--apple-space-4);
}

.asset-card {
  background: linear-gradient(145deg, var(--gzang-primary) 0%, var(--gzang-primary-light) 50%, var(--gzang-secondary) 100%);
  border-radius: var(--apple-radius-2xl);
  padding: var(--apple-space-5);
  position: relative;
  overflow: hidden;
  box-shadow: 0 8px 32px rgba(15, 76, 92, 0.3);
  
  // Decorative circle
  &::before {
    content: '';
    position: absolute;
    top: -50px;
    right: -50px;
    width: 200px;
    height: 200px;
    border-radius: 50%;
    background: rgba(255, 255, 255, 0.05);
  }
  
  &::after {
    content: '';
    position: absolute;
    bottom: -80px;
    left: -20px;
    width: 160px;
    height: 160px;
    border-radius: 50%;
    background: rgba(255, 255, 255, 0.03);
  }
}

.asset-card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--apple-space-4);
  position: relative;
  z-index: 1;
}

.book-badge {
  display: flex;
  align-items: center;
  gap: var(--apple-space-2);
  background: rgba(255, 255, 255, 0.15);
  padding: var(--apple-space-1) var(--apple-space-3);
  border-radius: var(--apple-radius-full);
  
  .book-name {
    font-size: var(--apple-text-xs);
    color: white;
    font-weight: var(--apple-font-medium);
  }
}

.asset-main {
  position: relative;
  z-index: 1;
  margin-bottom: var(--apple-space-5);
}

.asset-label {
  font-size: var(--apple-text-sm);
  color: rgba(255, 255, 255, 0.7);
  display: block;
  margin-bottom: var(--apple-space-1);
}

.asset-amount {
  font-size: 36px;
  font-weight: var(--apple-font-bold);
  color: white;
  letter-spacing: -1px;
}

.asset-stats {
  display: flex;
  align-items: center;
  gap: var(--apple-space-4);
  position: relative;
  z-index: 1;
}

.stat-item {
  flex: 1;
  display: flex;
  align-items: center;
  gap: var(--apple-space-2);
}

.stat-icon {
  width: 28px;
  height: 28px;
  border-radius: var(--apple-radius-sm);
  background: rgba(255, 255, 255, 0.1);
  display: flex;
  align-items: center;
  justify-content: center;
}

.stat-content {
  display: flex;
  flex-direction: column;
}

.stat-label {
  font-size: 10px;
  color: rgba(255, 255, 255, 0.6);
}

.stat-value {
  font-size: var(--apple-text-sm);
  font-weight: var(--apple-font-semibold);
  
  &.income {
    color: rgba(6, 214, 160, 1);
  }
  
  &.expense {
    color: rgba(255, 209, 102, 1);
  }
}

.stat-divider {
  width: 1px;
  height: 32px;
  background: rgba(255, 255, 255, 0.2);
}

// ================== Book Switcher ==================
.section-book-switcher {
  padding: 0 var(--apple-space-4);
  margin-bottom: var(--apple-space-4);
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
}

.book-chip-name {
  font-size: var(--apple-text-xs);
  color: var(--gzang-text-secondary);
  font-weight: var(--apple-font-medium);
}

// ================== Quick Actions ==================
.section-quick-actions {
  padding: 0 var(--apple-space-4);
  margin-bottom: var(--apple-space-5);
}

.section-title {
  font-size: var(--apple-text-lg);
  font-weight: var(--apple-font-semibold);
  color: var(--gzang-text-primary);
  display: block;
  margin-bottom: var(--apple-space-4);
}

.quick-actions-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: var(--apple-space-3);
}

.quick-action-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: var(--apple-space-4) var(--apple-space-2);
  background: var(--gzang-surface);
  border-radius: var(--apple-radius-lg);
  box-shadow: var(--apple-shadow-xs);
  transition: all var(--apple-duration-fast) var(--apple-ease-out);
  
  &:active {
    transform: scale(0.96);
    box-shadow: none;
  }
  
  &.stagger-1 { animation: apple-fade-in-up 0.3s 0.05s both; }
  &.stagger-2 { animation: apple-fade-in-up 0.3s 0.1s both; }
  &.stagger-3 { animation: apple-fade-in-up 0.3s 0.15s both; }
  &.stagger-4 { animation: apple-fade-in-up 0.3s 0.2s both; }
  &.stagger-5 { animation: apple-fade-in-up 0.3s 0.25s both; }
  &.stagger-6 { animation: apple-fade-in-up 0.3s 0.3s both; }
}

.action-icon {
  width: 44px;
  height: 44px;
  border-radius: var(--apple-radius-md);
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: var(--apple-space-2);
}

.action-label {
  font-size: var(--apple-text-xs);
  color: var(--gzang-text-secondary);
  font-weight: var(--apple-font-medium);
  text-align: center;
}

// ================== Recent Transactions ==================
.section-recent {
  padding: 0 var(--apple-space-4);
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--apple-space-3);
}

.section-more {
  display: flex;
  align-items: center;
  gap: 4px;
  
  .more-text {
    font-size: var(--apple-text-sm);
    color: var(--gzang-secondary);
    font-weight: var(--apple-font-medium);
  }
}

.transaction-list {
  background: var(--gzang-surface);
  border-radius: var(--apple-radius-xl);
  overflow: hidden;
  box-shadow: var(--apple-shadow-xs);
}

.transaction-item {
  display: flex;
  align-items: center;
  padding: var(--apple-space-4);
  transition: background-color var(--apple-duration-fast) var(--apple-ease-out);
  
  &:not(:last-child) {
    border-bottom: 0.5px solid var(--gzang-border);
  }
  
  &:active {
    background: var(--gzang-bg);
  }
}

.transaction-icon {
  width: 44px;
  height: 44px;
  border-radius: var(--apple-radius-md);
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: var(--apple-space-3);
  flex-shrink: 0;
}

.transaction-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-width: 0;
}

.transaction-name {
  font-size: var(--apple-text-base);
  font-weight: var(--apple-font-medium);
  color: var(--gzang-text-primary);
}

.transaction-account {
  font-size: var(--apple-text-xs);
  color: var(--gzang-text-secondary);
  margin-top: 2px;
}

.transaction-amount {
  font-size: var(--apple-text-base);
  font-weight: var(--apple-font-semibold);
  flex-shrink: 0;
  
  &.income {
    color: var(--gzang-success);
  }
  
  &.expense {
    color: var(--gzang-text-primary);
  }
}

// ================== Empty State ==================
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
  margin-bottom: var(--apple-space-5);
}

.empty-action {
  display: flex;
  align-items: center;
  gap: var(--apple-space-2);
  padding: var(--apple-space-3) var(--apple-space-5);
  background: var(--gzang-secondary);
  color: white;
  border: none;
  border-radius: var(--apple-radius-full);
  font-size: var(--apple-text-sm);
  font-weight: var(--apple-font-semibold);
  box-shadow: 0 4px 12px rgba(251, 139, 36, 0.3);
  
  &:active {
    transform: scale(0.97);
  }
}

// ================== Bottom Safe Area ==================
.bottom-safe-area {
  height: calc(var(--apple-space-4) + 84px);
}

// ================== Animations ==================
@keyframes apple-fade-in-up {
  from {
    opacity: 0;
    transform: translateY(16px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

// ================== Dark Mode ==================
@media (prefers-color-scheme: dark) {
  .nav-large-title {
    background: var(--gzang-bg, #000000);
  }
  
  .nav-title,
  .section-title,
  .transaction-name,
  .empty-title {
    color: var(--gzang-text-primary, #FFFFFF);
  }
  
  .asset-card-wrapper,
  .section-book-switcher,
  .section-quick-actions,
  .section-recent {
    .book-chip,
    .quick-action-item,
    .transaction-list {
      background: var(--gzang-surface, #1C1C1E);
    }
  }
}

[data-theme="dark"] {
  .nav-large-title {
    background: var(--gzang-bg, #000000);
  }
  
  .nav-title,
  .section-title,
  .transaction-name,
  .empty-title {
    color: var(--gzang-text-primary, #FFFFFF);
  }
  
  .asset-card {
    background: linear-gradient(145deg, #0a3644 0%, #0F4C5C 50%, #e67a1a 100%);
  }
}
</style>
