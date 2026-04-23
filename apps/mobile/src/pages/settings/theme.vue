<template>
  <view class="theme-settings-page">
    <uni-nav-bar left-icon="left" :title="t('theme.title')" @clickLeft="goBack" />
    
    <!-- 主题模式 -->
    <view class="settings-section">
      <view class="section-title">{{ t('theme.appearanceMode') }}</view>
      <view class="mode-options">
        <view 
          v-for="mode in themeModes" 
          :key="mode.value"
          :class="['mode-cell', { active: currentMode === mode.value }]"
          @click="setThemeMode(mode.value)"
        >
          <view class="mode-icon">{{ mode.icon }}</view>
          <view class="mode-info">
            <view class="mode-name">{{ mode.label }}</view>
            <view class="mode-desc">{{ mode.desc }}</view>
          </view>
          <view class="mode-check" v-if="currentMode === mode.value">✓</view>
        </view>
      </view>
    </view>

    <!-- 主色调 -->
    <view class="settings-section">
      <view class="section-title">{{ t('theme.primaryColor') }}</view>
      <view class="color-options">
        <view 
          v-for="color in primaryColors" 
          :key="color.value"
          :class="['color-option', { active: currentPrimaryColor === color.value }]"
          @click="setPrimaryColor(color.value)"
        >
          <view class="color-swatch" :style="{ background: color.value }"></view>
          <view class="color-name">{{ color.name }}</view>
        </view>
      </view>
    </view>

    <!-- 强调色 -->
    <view class="settings-section">
      <view class="section-title">{{ t('theme.accentColor') }}</view>
      <view class="color-options">
        <view 
          v-for="color in accentColors" 
          :key="color.value"
          :class="['color-option', { active: currentAccentColor === color.value }]"
          @click="setAccentColor(color.value)"
        >
          <view class="color-swatch" :style="{ background: color.value }"></view>
          <view class="color-name">{{ color.name }}</view>
        </view>
      </view>
    </view>

    <!-- 圆角风格 -->
    <view class="settings-section">
      <view class="section-title">{{ t('theme.cornerStyle') }}</view>
      <view class="corner-options">
        <view 
          v-for="style in cornerStyles" 
          :key="style.value"
          :class="['corner-cell', { active: currentCornerStyle === style.value }]"
          @click="setCornerStyle(style.value)"
        >
          <view class="corner-preview" :style="{ borderRadius: style.radius }"></view>
          <view class="corner-name">{{ style.label }}</view>
        </view>
      </view>
    </view>

    <!-- 预览 -->
    <view class="preview-section">
      <view class="preview-title">{{ t('theme.preview') }}</view>
      <view class="preview-card" :style="{ '--primary': currentPrimaryColor, '--accent': currentAccentColor }">
        <view class="preview-header">G-Zang</view>
        <view class="preview-content">
          <view class="preview-stat">
            <text class="stat-label">{{ t('home.totalAssets') }}</text>
            <text class="stat-value">¥ 125,680.00</text>
          </view>
          <view class="preview-btn">{{ t('accounting.quickRecord') }}</view>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useI18n } from 'vue-i18n'
import { useAppStore } from '@/stores/app'

const { t } = useI18n()
const appStore = useAppStore()

// 从 appStore 同步状态
const currentMode = ref(appStore.theme)
const currentPrimaryColor = ref('#0F4C5C')
const currentAccentColor = ref('#FB8B24')
const currentCornerStyle = ref('medium')

// 监听 appStore 主题变化
const themeLabel = computed(() => {
  const map: Record<string, string> = {
    light: t('theme.lightMode'),
    dark: t('theme.darkMode'),
    auto: t('theme.autoMode'),
  }
  return map[currentMode.value] || t('theme.lightMode')
})

const themeModes = [
  { value: 'light', label: t('theme.lightMode'), desc: t('theme.lightModeDesc'), icon: '☀️' },
  { value: 'dark', label: t('theme.darkMode'), desc: t('theme.darkModeDesc'), icon: '🌙' },
  { value: 'auto', label: t('theme.autoMode'), desc: t('theme.autoModeDesc'), icon: '🌓' },
]

const primaryColors = [
  { value: '#0F4C5C', name: t('theme.colors.guizangCyan') },
  { value: '#3A86FF', name: t('theme.colors.techBlue') },
  { value: '#8338EC', name: t('theme.colors.electricPurple') },
  { value: '#06D6A0', name: t('theme.colors.emeraldGreen') },
  { value: '#FB8B24', name: t('theme.colors.liuliGold') },
  { value: '#EF476F', name: t('theme.colors.coralRed') },
]

const accentColors = [
  { value: '#FB8B24', name: t('theme.colors.liuliGold') },
  { value: '#FFD166', name: t('theme.colors.guizangCyan') },
  { value: '#06D6A0', name: t('theme.colors.emeraldGreen') },
  { value: '#3A86FF', name: t('theme.colors.techBlue') },
  { value: '#EF476F', name: t('theme.colors.coralRed') },
  { value: '#8338EC', name: t('theme.colors.electricPurple') },
]

const cornerStyles = [
  { value: 'small', label: t('theme.cornerStyles.small'), radius: '8rpx' },
  { value: 'medium', label: t('theme.cornerStyles.medium'), radius: '16rpx' },
  { value: 'large', label: t('theme.cornerStyles.large'), radius: '24rpx' },
  { value: 'full', label: t('theme.cornerStyles.full'), radius: '50%' },
]

function goBack() {
  uni.navigateBack()
}

function setThemeMode(mode: string) {
  currentMode.value = mode
  appStore.setTheme(mode as 'light' | 'dark' | 'auto')
  uni.showToast({ title: t('theme.themeUpdated'), icon: 'success' })
}

function setPrimaryColor(color: string) {
  currentPrimaryColor.value = color
  // 更新 CSS 变量
  uni.setStorageSync('primaryColor', color)
}

function setAccentColor(color: string) {
  currentAccentColor.value = color
  uni.setStorageSync('accentColor', color)
}

function setCornerStyle(style: string) {
  currentCornerStyle.value = style
}
</script>

<style scoped lang="scss">
.theme-settings-page {
  min-height: 100vh;
  background-color: var(--gzang-bg, #F8F9FA);
  padding-bottom: 40rpx;
}

.settings-section {
  background: var(--gzang-surface, #fff);
  margin: 24rpx;
  border-radius: 16rpx;
  padding: 24rpx;
}

.section-title {
  font-size: 28rpx;
  font-weight: 600;
  color: var(--gzang-text-primary, #1F2937);
  margin-bottom: 20rpx;
}

.mode-options {
  .mode-cell {
    display: flex;
    align-items: center;
    padding: 20rpx;
    border-radius: 12rpx;
    margin-bottom: 12rpx;
    background: var(--gzang-bg, #F8F9FA);
    border: 4rpx solid transparent;
    
    &:last-child {
      margin-bottom: 0;
    }
    
    &.active {
      border-color: var(--gzang-primary, #0F4C5C);
      background: rgba(15, 76, 92, 0.08);
    }
  }
}

.mode-icon {
  font-size: 40rpx;
  margin-right: 16rpx;
}

.mode-info {
  flex: 1;
  
  .mode-name {
    font-size: 28rpx;
    font-weight: 500;
    color: var(--gzang-text-primary, #1F2937);
  }
  
  .mode-desc {
    font-size: 22rpx;
    color: var(--gzang-text-secondary, #9CA3AF);
    margin-top: 4rpx;
  }
}

.mode-check {
  font-size: 32rpx;
  color: var(--gzang-primary, #0F4C5C);
  font-weight: 700;
}

.color-options {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20rpx;
}

.color-option {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8rpx;
  padding: 20rpx;
  border-radius: 12rpx;
  background: var(--gzang-bg, #F8F9FA);
  border: 4rpx solid transparent;
  
  &.active {
    border-color: var(--gzang-text-primary, #333);
  }
}

.color-swatch {
  width: 64rpx;
  height: 64rpx;
  border-radius: 50%;
}

.color-name {
  font-size: 22rpx;
  color: var(--gzang-text-secondary, #666);
}

.corner-options {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16rpx;
}

.corner-cell {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8rpx;
  padding: 20rpx;
  border-radius: 12rpx;
  background: var(--gzang-bg, #F8F9FA);
  border: 4rpx solid transparent;
  
  &.active {
    border-color: var(--gzang-primary, #0F4C5C);
  }
}

.corner-preview {
  width: 60rpx;
  height: 60rpx;
  background: var(--gzang-primary, #0F4C5C);
}

.corner-name {
  font-size: 22rpx;
  color: var(--gzang-text-secondary, #666);
}

.preview-section {
  background: var(--gzang-surface, #fff);
  margin: 24rpx;
  border-radius: 16rpx;
  padding: 24rpx;
}

.preview-title {
  font-size: 28rpx;
  font-weight: 600;
  color: var(--gzang-text-primary, #1F2937);
  margin-bottom: 20rpx;
}

.preview-card {
  background: linear-gradient(135deg, var(--primary, #0F4C5C) 0%, var(--primary, #0F4C5C) 100%);
  border-radius: 24rpx;
  padding: 32rpx;
  color: #fff;
}

.preview-header {
  font-size: 32rpx;
  font-weight: 700;
  margin-bottom: 24rpx;
}

.preview-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.preview-stat {
  .stat-label {
    font-size: 24rpx;
    opacity: 0.8;
    display: block;
  }
  
  .stat-value {
    font-size: 40rpx;
    font-weight: 700;
  }
}

.preview-btn {
  background: var(--accent, #FB8B24);
  padding: 16rpx 32rpx;
  border-radius: 24rpx;
  font-size: 28rpx;
  font-weight: 600;
  color: #fff;
}

// 深色模式适配
[data-theme="dark"] {
  .settings-section,
  .preview-section {
    background: var(--gzang-surface, #1F2937);
  }
  
  .mode-cell,
  .color-option,
  .corner-cell {
    background: var(--gzang-bg, #111827);
  }
  
  .mode-name,
  .preview-title {
    color: var(--gzang-text-primary, #F9FAFB);
  }
  
  .mode-desc,
  .color-name,
  .corner-name {
    color: var(--gzang-text-secondary, #9CA3AF);
  }
}
</style>
