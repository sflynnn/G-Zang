<template>
  <view class="settings-page apple-style">
    <!-- Large Title Navigation -->
    <view class="nav-large-title">
      <view class="nav-header">
        <view class="nav-back" @click="goBack">
          <AppleIcon name="chevron-left" :size="24" color="var(--gzang-primary)" />
        </view>
        <text class="nav-title">{{ t('settings.title') }}</text>
        <view class="nav-spacer"></view>
      </view>
    </view>

    <scroll-view class="main-content" scroll-y="true">
      <!-- Account Settings -->
      <view class="settings-section">
        <view class="section-title">{{ t('settings.accountSettings') }}</view>
        <view class="settings-group">
          <view class="settings-item" @click="goToEditProfile">
            <view class="item-icon" style="background: rgba(17, 138, 178, 0.12)">
              <AppleIcon name="user" :size="18" color="var(--gzang-info)" />
            </view>
            <text class="item-label">{{ t('settings.editProfile') }}</text>
            <AppleIcon name="chevron-right" :size="16" color="var(--gzang-text-tertiary)" />
          </view>
          
          <view class="settings-item" @click="goToChangePassword">
            <view class="item-icon" style="background: rgba(155, 89, 182, 0.12)">
              <AppleIcon name="lock" :size="18" color="#9B59B6" />
            </view>
            <text class="item-label">{{ t('settings.changePassword') }}</text>
            <AppleIcon name="chevron-right" :size="16" color="var(--gzang-text-tertiary)" />
          </view>
        </view>
      </view>

      <!-- Data Management -->
      <view class="settings-section">
        <view class="section-title">{{ t('settings.dataManagement') }}</view>
        <view class="settings-group">
          <view class="settings-item" @click="handleExport">
            <view class="item-icon" style="background: rgba(251, 139, 36, 0.12)">
              <AppleIcon name="export" :size="18" color="var(--gzang-secondary)" />
            </view>
            <text class="item-label">{{ t('settings.dataExport') }}</text>
            <text class="item-value">JSON / Excel</text>
          </view>
          
          <view class="settings-item toggle-item">
            <view class="item-icon" style="background: rgba(6, 214, 160, 0.12)">
              <AppleIcon name="refresh" :size="18" color="var(--gzang-success)" />
            </view>
            <text class="item-label">{{ t('settings.autoBackup') }}</text>
            <view class="toggle-switch" :class="{ active: settings.autoBackup }" @click="toggleSetting('autoBackup')">
              <view class="toggle-thumb"></view>
            </view>
          </view>
        </view>
      </view>

      <!-- Notifications -->
      <view class="settings-section">
        <view class="section-title">{{ t('settings.notifications') }}</view>
        <view class="settings-group">
          <view class="settings-item toggle-item">
            <view class="item-icon" style="background: rgba(251, 139, 36, 0.12)">
              <AppleIcon name="bell" :size="18" color="var(--gzang-secondary)" />
            </view>
            <text class="item-label">{{ t('settings.billReminder') }}</text>
            <view class="toggle-switch" :class="{ active: settings.billReminder }" @click="toggleSetting('billReminder')">
              <view class="toggle-thumb"></view>
            </view>
          </view>
          
          <view class="settings-item toggle-item">
            <view class="item-icon" style="background: rgba(239, 71, 111, 0.12)">
              <AppleIcon name="warning" :size="18" color="var(--gzang-danger)" />
            </view>
            <text class="item-label">{{ t('settings.budgetAlert') }}</text>
            <view class="toggle-switch" :class="{ active: settings.budgetAlert }" @click="toggleSetting('budgetAlert')">
              <view class="toggle-thumb"></view>
            </view>
          </view>
        </view>
      </view>

      <!-- Appearance -->
      <view class="settings-section">
        <view class="section-title">{{ t('settings.appearance') }}</view>
        <view class="settings-group">
          <view class="settings-item" @click="goToTheme">
            <view class="item-icon" style="background: rgba(251, 139, 36, 0.12)">
              <AppleIcon name="settings" :size="18" color="var(--gzang-secondary)" />
            </view>
            <text class="item-label">{{ t('settings.themeSettings') }}</text>
            <text class="item-value">{{ themeLabel }}</text>
          </view>
          
          <view class="settings-item" @click="toggleLanguage">
            <view class="item-icon" style="background: rgba(6, 214, 160, 0.12)">
              <AppleIcon name="language" :size="18" color="var(--gzang-success)" />
            </view>
            <text class="item-label">{{ languageLabel }}</text>
            <AppleIcon name="chevron-right" :size="16" color="var(--gzang-text-tertiary)" />
          </view>
        </view>
      </view>

      <!-- About -->
      <view class="settings-section">
        <view class="section-title">{{ t('settings.aboutUs') }}</view>
        <view class="settings-group">
          <view class="settings-item" @click="goToHelp">
            <view class="item-icon" style="background: rgba(107, 114, 128, 0.12)">
              <AppleIcon name="help" :size="18" color="var(--gzang-text-secondary)" />
            </view>
            <text class="item-label">{{ t('settings.helpFeedback') }}</text>
            <AppleIcon name="chevron-right" :size="16" color="var(--gzang-text-tertiary)" />
          </view>
          
          <view class="settings-item" @click="goToPrivacy">
            <view class="item-icon" style="background: rgba(107, 114, 128, 0.12)">
              <AppleIcon name="lock" :size="18" color="var(--gzang-text-secondary)" />
            </view>
            <text class="item-label">{{ t('settings.privacyPolicy') }}</text>
            <AppleIcon name="chevron-right" :size="16" color="var(--gzang-text-tertiary)" />
          </view>
          
          <view class="settings-item">
            <view class="item-icon" style="background: rgba(107, 114, 128, 0.12)">
              <AppleIcon name="info" :size="18" color="var(--gzang-text-secondary)" />
            </view>
            <text class="item-label">{{ t('settings.currentVersion') }}</text>
            <text class="item-value">v1.0.0</text>
          </view>
        </view>
      </view>

      <!-- Danger Zone -->
      <view class="settings-section danger-section">
        <view class="settings-group">
          <view class="settings-item danger" @click="handleClearCache">
            <view class="item-icon" style="background: rgba(255, 209, 102, 0.12)">
              <AppleIcon name="delete" :size="18" color="var(--gzang-warning)" />
            </view>
            <text class="item-label">{{ t('settings.clearCache') }}</text>
            <text class="item-value">{{ cacheSize }}</text>
          </view>
          
          <view class="settings-item danger" @click="handleLogout">
            <view class="item-icon" style="background: rgba(239, 71, 111, 0.12)">
              <AppleIcon name="logout" :size="18" color="var(--gzang-danger)" />
            </view>
            <text class="item-label">{{ t('settings.logout') }}</text>
            <AppleIcon name="chevron-right" :size="16" color="var(--gzang-danger)" />
          </view>
        </view>
      </view>

      <view class="bottom-safe-area"></view>
    </scroll-view>
  </view>
</template>

<script setup lang="ts">
import { reactive, computed } from 'vue'
import { useI18n } from 'vue-i18n'
import { useAuthStore } from '@/stores/auth'
import { useAppStore } from '@/stores/app'
import AppleIcon from '@/components/common/AppleIcon/index.vue'

const { t } = useI18n()
const authStore = useAuthStore()
const appStore = useAppStore()

const settings = reactive({
  autoBackup: true,
  billReminder: true,
  budgetAlert: true,
})

const cacheSize = '12.5 MB'
const version = '1.0.0'

const currentTheme = computed(() => appStore.theme)
const currentLanguage = computed(() => appStore.language)

const themeLabel = computed(() => {
  const map: Record<string, string> = {
    light: t('settings.themeModes.light'),
    dark: t('settings.themeModes.dark'),
    auto: t('settings.themeModes.auto'),
  }
  return map[currentTheme.value] || t('settings.themeModes.auto')
})

const languageLabel = computed(() => {
  const map: Record<string, string> = {
    'zh-CN': '简体中文',
    'en-US': 'English',
  }
  return map[currentLanguage.value] || '简体中文'
})

const toggleLanguage = () => {
  const newLang = currentLanguage.value === 'zh-CN' ? 'en-US' : 'zh-CN'
  appStore.setLanguage(newLang)
}

const toggleSetting = (key: keyof typeof settings) => {
  settings[key] = !settings[key]
}

const goBack = () => uni.navigateBack()
const goToEditProfile = () => uni.navigateTo({ url: '/pages/profile/edit' })
const goToChangePassword = () => uni.showToast({ title: '功能开发中', icon: 'none' })
const goToTheme = () => uni.navigateTo({ url: '/pages/settings/theme' })
const goToHelp = () => uni.showToast({ title: '功能开发中', icon: 'none' })
const goToPrivacy = () => uni.navigateTo({ url: '/pages/privacy/index' })

const handleExport = () => {
  uni.showActionSheet({
    itemList: ['JSON', 'Excel'],
    success: (res) => {
      if (res.tapIndex === 0) {
        appStore.showSuccess('导出 JSON 成功')
      } else if (res.tapIndex === 1) {
        appStore.showSuccess('导出 Excel 成功')
      }
    }
  })
}

const handleClearCache = () => {
  uni.showModal({
    title: t('settings.clearCache'),
    content: t('common.confirmAction'),
    success: (res) => {
      if (res.confirm) {
        appStore.showSuccess(t('settings.cacheCleared'))
      }
    }
  })
}

const handleLogout = () => {
  uni.showModal({
    title: t('auth.logout'),
    content: t('auth.logoutConfirm'),
    success: async (res) => {
      if (res.confirm) {
        await authStore.logout()
        uni.reLaunch({ url: '/pages/login/index' })
      }
    }
  })
}
</script>

<style lang="scss" scoped>
.settings-page {
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
  align-items: center;
  margin-bottom: var(--apple-space-3);
}

.nav-back {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background: var(--gzang-surface);
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: var(--apple-shadow-xs);
}

.nav-title {
  flex: 1;
  text-align: center;
  font-size: var(--apple-text-lg);
  font-weight: var(--apple-font-semibold);
  color: var(--gzang-text-primary);
  margin-right: 36px;
}

.nav-spacer {
  width: 36px;
}

.main-content {
  padding: 0 var(--apple-space-4);
}

.settings-section {
  margin-bottom: var(--apple-space-5);
}

.section-title {
  font-size: var(--apple-text-sm);
  font-weight: var(--apple-font-medium);
  color: var(--gzang-text-secondary);
  margin-bottom: var(--apple-space-2);
  padding: 0 var(--apple-space-2);
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
  padding: var(--apple-space-4);
  border-bottom: 0.5px solid var(--gzang-border);
  transition: background-color var(--apple-duration-fast);
  
  &:last-child { border-bottom: none; }
  &:active { background: var(--gzang-bg); }
  
  &.danger {
    .item-label { color: var(--gzang-danger); }
  }
}

.item-icon {
  width: 36px;
  height: 36px;
  border-radius: var(--apple-radius-sm);
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: var(--apple-space-3);
  flex-shrink: 0;
}

.item-label {
  flex: 1;
  font-size: var(--apple-text-base);
  color: var(--gzang-text-primary);
  font-weight: var(--apple-font-medium);
}

.item-value {
  font-size: var(--apple-text-sm);
  color: var(--gzang-text-tertiary);
}

// Toggle Switch
.toggle-switch {
  width: 51px;
  height: 31px;
  background: var(--gzang-border);
  border-radius: 16px;
  position: relative;
  transition: background-color var(--apple-duration-fast);
  
  &.active {
    background: var(--gzang-success);
    
    .toggle-thumb {
      transform: translateX(20px);
    }
  }
}

.toggle-thumb {
  position: absolute;
  top: 2px;
  left: 2px;
  width: 27px;
  height: 27px;
  background: white;
  border-radius: 50%;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.15);
  transition: transform var(--apple-duration-fast) var(--apple-ease-spring);
}

.bottom-safe-area {
  height: var(--apple-space-4);
}
</style>
