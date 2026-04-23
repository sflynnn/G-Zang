<template>
  <view class="profile-page apple-style">
    <!-- Large Title Navigation -->
    <view class="nav-large-title">
      <view class="nav-header">
        <view class="nav-titles">
          <text class="nav-title">{{ t('profile.title') }}</text>
        </view>
        <view class="nav-actions">
          <view class="nav-action" @click="goToSettings">
            <AppleIcon name="settings" :size="20" color="var(--gzang-text-secondary)" />
          </view>
        </view>
      </view>
    </view>

    <scroll-view class="main-content" scroll-y="true">
      <!-- Profile Header Card -->
      <view class="profile-card" @click="goToEdit">
        <view class="profile-avatar" :style="{ background: avatarGradient }">
          <text class="avatar-text">{{ userInitials }}</text>
        </view>
        <view class="profile-info">
          <text class="profile-name">{{ userName }}</text>
          <text class="profile-id">ID: {{ userId }}</text>
        </view>
        <view class="profile-edit">
          <AppleIcon name="chevron-right" :size="18" color="var(--gzang-text-tertiary)" />
        </view>
      </view>

      <!-- Asset Summary Card -->
      <view class="asset-card" @click="goToAccounts">
        <view class="asset-item">
          <text class="asset-label">{{ t('profile.totalAssets') }}</text>
          <text class="asset-value amount">{{ formatAmount(totalAssets) }}</text>
        </view>
        <view class="asset-divider"></view>
        <view class="asset-item">
          <text class="asset-label">{{ t('profile.monthlyIncome') }}</text>
          <text class="asset-value income amount">+{{ formatAmount(monthlyIncome) }}</text>
        </view>
        <view class="asset-divider"></view>
        <view class="asset-item">
          <text class="asset-label">{{ t('profile.monthlyExpense') }}</text>
          <text class="asset-value expense amount">-{{ formatAmount(monthlyExpense) }}</text>
        </view>
      </view>

      <!-- Management Section (iOS Settings Style) -->
      <view class="settings-section">
        <view class="section-header">
          <text class="section-title">{{ t('settings.accountSettings') }}</text>
        </view>
        <view class="settings-group">
          <view class="settings-item" @click="goToBooks">
            <view class="item-left">
              <view class="item-icon" style="background: rgba(15, 76, 92, 0.12)">
                <AppleIcon name="book" :size="18" color="var(--gzang-primary)" />
              </view>
              <text class="item-label">{{ t('book.title') }}</text>
            </view>
            <view class="item-right">
              <text class="item-badge" v-if="bookCount > 0">{{ bookCount }}</text>
              <AppleIcon name="chevron-right" :size="16" color="var(--gzang-text-tertiary)" />
            </view>
          </view>
          
          <view class="settings-item" @click="goToAccounts">
            <view class="item-left">
              <view class="item-icon" style="background: rgba(6, 214, 160, 0.12)">
                <AppleIcon name="wallet" :size="18" color="var(--gzang-success)" />
              </view>
              <text class="item-label">{{ t('account.title') }}</text>
            </view>
            <view class="item-right">
              <AppleIcon name="chevron-right" :size="16" color="var(--gzang-text-tertiary)" />
            </view>
          </view>
          
          <view class="settings-item" @click="goToBudgets">
            <view class="item-left">
              <view class="item-icon" style="background: rgba(255, 209, 102, 0.12)">
                <AppleIcon name="chart" :size="18" color="var(--gzang-warning)" />
              </view>
              <text class="item-label">{{ t('budget.title') }}</text>
            </view>
            <view class="item-right">
              <AppleIcon name="chevron-right" :size="16" color="var(--gzang-text-tertiary)" />
            </view>
          </view>
          
          <view class="settings-item no-border" @click="goToCategories">
            <view class="item-left">
              <view class="item-icon" style="background: rgba(155, 89, 182, 0.12)">
                <AppleIcon name="list" :size="18" color="#9B59B6" />
              </view>
              <text class="item-label">{{ t('category.title') }}</text>
            </view>
            <view class="item-right">
              <AppleIcon name="chevron-right" :size="16" color="var(--gzang-text-tertiary)" />
            </view>
          </view>
        </view>
      </view>

      <!-- Data & Settings Section -->
      <view class="settings-section">
        <view class="section-header">
          <text class="section-title">{{ t('settings.dataManagement') }}</text>
        </view>
        <view class="settings-group">
          <view class="settings-item" @click="goToExport">
            <view class="item-left">
              <view class="item-icon" style="background: rgba(17, 138, 178, 0.12)">
                <AppleIcon name="export" :size="18" color="var(--gzang-info)" />
              </view>
              <text class="item-label">{{ t('settings.dataExport') }}</text>
            </view>
            <view class="item-right">
              <AppleIcon name="chevron-right" :size="16" color="var(--gzang-text-tertiary)" />
            </view>
          </view>
          
          <view class="settings-item" @click="goToNotifications">
            <view class="item-left">
              <view class="item-icon" style="background: rgba(239, 71, 111, 0.12)">
                <AppleIcon name="bell" :size="18" color="var(--gzang-danger)" />
              </view>
              <text class="item-label">{{ t('settings.notifications') }}</text>
            </view>
            <view class="item-right">
              <AppleIcon name="chevron-right" :size="16" color="var(--gzang-text-tertiary)" />
            </view>
          </view>
          
          <view class="settings-item no-border" @click="goToTheme">
            <view class="item-left">
              <view class="item-icon" style="background: rgba(251, 139, 36, 0.12)">
                <AppleIcon name="settings" :size="18" color="var(--gzang-secondary)" />
              </view>
              <text class="item-label">{{ t('settings.themeSettings') }}</text>
            </view>
            <view class="item-right">
              <AppleIcon name="chevron-right" :size="16" color="var(--gzang-text-tertiary)" />
            </view>
          </view>
        </view>
      </view>

      <!-- About Section -->
      <view class="settings-section">
        <view class="section-header">
          <text class="section-title">{{ t('settings.aboutUs') }}</text>
        </view>
        <view class="settings-group">
          <view class="settings-item" @click="goToHelp">
            <view class="item-left">
              <view class="item-icon" style="background: rgba(107, 114, 128, 0.12)">
                <AppleIcon name="help" :size="18" color="var(--gzang-text-secondary)" />
              </view>
              <text class="item-label">{{ t('settings.helpFeedback') }}</text>
            </view>
            <view class="item-right">
              <AppleIcon name="chevron-right" :size="16" color="var(--gzang-text-tertiary)" />
            </view>
          </view>
          
          <view class="settings-item" @click="goToPrivacy">
            <view class="item-left">
              <view class="item-icon" style="background: rgba(107, 114, 128, 0.12)">
                <AppleIcon name="lock" :size="18" color="var(--gzang-text-secondary)" />
              </view>
              <text class="item-label">{{ t('settings.privacyPolicy') }}</text>
            </view>
            <view class="item-right">
              <AppleIcon name="chevron-right" :size="16" color="var(--gzang-text-tertiary)" />
            </view>
          </view>
          
          <view class="settings-item no-border" @click="showVersion">
            <view class="item-left">
              <view class="item-icon" style="background: rgba(107, 114, 128, 0.12)">
                <AppleIcon name="info" :size="18" color="var(--gzang-text-secondary)" />
              </view>
              <text class="item-label">{{ t('settings.currentVersion') }}</text>
            </view>
            <view class="item-right">
              <text class="item-value">v1.0.0</text>
              <AppleIcon name="chevron-right" :size="16" color="var(--gzang-text-tertiary)" />
            </view>
          </view>
        </view>
      </view>

      <!-- Logout Button -->
      <view class="logout-section">
        <button class="logout-btn" @click="handleLogout">
          <AppleIcon name="logout" :size="18" color="var(--gzang-danger)" />
          <text>{{ t('settings.logout') }}</text>
        </button>
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
import { useAuthStore } from '@/stores/auth'
import { useBookStore } from '@/stores/book'
import CustomTabBar from '@/components/CustomTabBar/index.vue'
import AppleIcon from '@/components/common/AppleIcon/index.vue'

const { t } = useI18n()

// Stores
const authStore = useAuthStore()
const bookStore = useBookStore()

// User Info
const userName = computed(() => authStore.user?.nickname || authStore.user?.username || t('profile.notSet'))

const userInitials = computed(() => {
  const name = userName.value
  return name.slice(0, 1).toUpperCase() || 'U'
})

const userId = computed(() => {
  const phone = authStore.user?.phone || ''
  if (phone) {
    return phone.replace(/(\d{3})\d{4}(\d{4})/, '$1****$2')
  }
  return '****'
})

const avatarGradient = computed(() => {
  return 'linear-gradient(135deg, var(--gzang-secondary) 0%, var(--gzang-secondary-dark) 100%)'
})

// Stats
const totalAssets = computed(() => bookStore.currentStatistics?.balance || 0)
const monthlyIncome = computed(() => bookStore.currentStatistics?.periodStats?.thisMonth?.income || 0)
const monthlyExpense = computed(() => bookStore.currentStatistics?.periodStats?.thisMonth?.expense || 0)
const bookCount = computed(() => bookStore.books?.length || 0)

// Format Amount
const formatAmount = (amount: number) => {
  if (!amount) return '0.00'
  return Math.abs(amount).toLocaleString('zh-CN', {
    minimumFractionDigits: 2,
    maximumFractionDigits: 2
  })
}

// Navigation
const goToEdit = () => {
  uni.navigateTo({ url: '/pages/profile/edit' })
}

const goToSettings = () => {
  uni.navigateTo({ url: '/pages/settings/index' })
}

const goToBooks = () => {
  uni.navigateTo({ url: '/pages/books/index' })
}

const goToAccounts = () => {
  uni.navigateTo({ url: '/pages/accounts/index' })
}

const goToBudgets = () => {
  uni.navigateTo({ url: '/pages/budget/index' })
}

const goToCategories = () => {
  uni.navigateTo({ url: '/pages/categories/index' })
}

const goToExport = () => {
  uni.navigateTo({ url: '/pages/settings/index' })
}

const goToNotifications = () => {
  uni.navigateTo({ url: '/pages/settings/index' })
}

const goToTheme = () => {
  uni.navigateTo({ url: '/pages/settings/theme' })
}

const goToHelp = () => {
  uni.navigateTo({ url: '/pages/settings/index' })
}

const goToPrivacy = () => {
  uni.navigateTo({ url: '/pages/privacy/index' })
}

const showVersion = () => {
  uni.showModal({
    title: t('settings.currentVersion'),
    content: 'G-Zang v1.0.0',
    showCancel: false
  })
}

const handleLogout = () => {
  uni.showModal({
    title: t('settings.logout'),
    content: t('auth.logoutConfirm'),
    success: async (res) => {
      if (res.confirm) {
        await authStore.logout()
        uni.reLaunch({ url: '/pages/login/index' })
      }
    }
  })
}

onMounted(async () => {
  await bookStore.fetchBooks()
  if (bookStore.currentBook) {
    await bookStore.fetchStatistics(bookStore.currentBook.id)
  }
})
</script>

<style lang="scss" scoped>
.profile-page {
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

// ================== Main Content ==================
.main-content {
  padding: 0 var(--apple-space-4);
}

// ================== Profile Card ==================
.profile-card {
  display: flex;
  align-items: center;
  background: var(--gzang-surface);
  border-radius: var(--apple-radius-xl);
  padding: var(--apple-space-4);
  margin-bottom: var(--apple-space-4);
  box-shadow: var(--apple-shadow-sm);
  transition: all var(--apple-duration-fast) var(--apple-ease-out);
  
  &:active {
    background: var(--gzang-bg);
  }
}

.profile-avatar {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: var(--apple-space-4);
  box-shadow: 0 4px 12px rgba(251, 139, 36, 0.3);
}

.avatar-text {
  font-size: 24px;
  font-weight: var(--apple-font-bold);
  color: white;
}

.profile-info {
  flex: 1;
}

.profile-name {
  font-size: var(--apple-text-xl);
  font-weight: var(--apple-font-semibold);
  color: var(--gzang-text-primary);
  display: block;
  margin-bottom: 4px;
}

.profile-id {
  font-size: var(--apple-text-sm);
  color: var(--gzang-text-secondary);
}

.profile-edit {
  flex-shrink: 0;
}

// ================== Asset Card ==================
.asset-card {
  display: flex;
  align-items: center;
  background: linear-gradient(145deg, var(--gzang-primary) 0%, var(--gzang-primary-light) 100%);
  border-radius: var(--apple-radius-xl);
  padding: var(--apple-space-5);
  margin-bottom: var(--apple-space-5);
  color: white;
  box-shadow: 0 4px 16px rgba(15, 76, 92, 0.25);
}

.asset-item {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.asset-label {
  font-size: var(--apple-text-xs);
  opacity: 0.7;
  margin-bottom: 4px;
}

.asset-value {
  font-size: var(--apple-text-lg);
  font-weight: var(--apple-font-bold);
  
  &.income {
    color: var(--gzang-success);
  }
  
  &.expense {
    color: rgba(255, 209, 102, 1);
  }
}

.asset-divider {
  width: 1px;
  height: 40px;
  background: rgba(255, 255, 255, 0.2);
}

// ================== Settings Section ==================
.settings-section {
  margin-bottom: var(--apple-space-4);
}

.section-header {
  padding: 0 var(--apple-space-2);
  margin-bottom: var(--apple-space-2);
}

.section-title {
  font-size: var(--apple-text-sm);
  font-weight: var(--apple-font-medium);
  color: var(--gzang-text-secondary);
}

.settings-group {
  background: var(--gzang-surface);
  border-radius: var(--apple-radius-xl);
  overflow: hidden;
  box-shadow: var(--apple-shadow-sm);
}

.settings-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: var(--apple-space-4);
  border-bottom: 0.5px solid var(--gzang-border);
  transition: all var(--apple-duration-fast) var(--apple-ease-out);
  
  &.no-border {
    border-bottom: none;
  }
  
  &:active {
    background: var(--gzang-bg);
  }
}

.item-left {
  display: flex;
  align-items: center;
}

.item-icon {
  width: 36px;
  height: 36px;
  border-radius: var(--apple-radius-sm);
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: var(--apple-space-3);
}

.item-label {
  font-size: var(--apple-text-base);
  color: var(--gzang-text-primary);
  font-weight: var(--apple-font-medium);
}

.item-right {
  display: flex;
  align-items: center;
  gap: var(--apple-space-2);
}

.item-badge {
  background: var(--gzang-danger);
  color: white;
  font-size: 10px;
  font-weight: var(--apple-font-semibold);
  padding: 2px 8px;
  border-radius: var(--apple-radius-full);
}

.item-value {
  font-size: var(--apple-text-sm);
  color: var(--gzang-text-tertiary);
}

// ================== Logout Section ==================
.logout-section {
  margin-top: var(--apple-space-5);
  padding: 0 var(--apple-space-2);
}

.logout-btn {
  width: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: var(--apple-space-2);
  padding: var(--apple-space-4);
  background: var(--gzang-surface);
  border: none;
  border-radius: var(--apple-radius-xl);
  font-size: var(--apple-text-base);
  font-weight: var(--apple-font-medium);
  color: var(--gzang-danger);
  box-shadow: var(--apple-shadow-sm);
  transition: all var(--apple-duration-fast) var(--apple-ease-out);
  
  &:active {
    background: rgba(239, 71, 111, 0.1);
    transform: scale(0.98);
  }
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
  
  .profile-card,
  .settings-group {
    background: var(--gzang-surface, #1C1C1E);
  }
  
  .profile-name,
  .item-label {
    color: var(--gzang-text-primary, #FFFFFF);
  }
  
  .logout-btn {
    background: var(--gzang-surface, #1C1C1E);
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
