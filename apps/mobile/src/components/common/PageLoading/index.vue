<!-- ================================================
     G-Zang Mobile - Page Transition Loading Overlay
     路由转场时的骨架屏 + Loading 遮罩

     全局注册于 App.vue，在路由切换时自动展示
     ================================================ -->
<template>
  <Teleport to="body">
    <transition name="page-loading">
      <view
        v-if="isVisible"
        class="page-loading-overlay"
        :class="[`page-loading--${type}`, `page-loading--${theme}`]"
        @click="handleOverlayClick"
      >
        <!-- 全屏加载模式 -->
        <template v-if="type === 'fullscreen'">
          <view class="loading-splash">
            <!-- Logo -->
            <view class="splash-logo">
              <text class="logo-text">藏</text>
            </view>

            <!-- 品牌名称 -->
            <text class="splash-brand">G-Zang 归藏</text>

            <!-- 波浪进度条 -->
            <view class="splash-progress">
              <view class="progress-track">
                <view class="progress-fill"></view>
              </view>
            </view>

            <!-- 加载文案 -->
            <text v-if="text" class="splash-text">{{ text }}</text>
          </view>
        </template>

        <!-- 遮罩加载模式（页面内） -->
        <template v-else-if="type === 'overlay'">
          <view class="loading-overlay-inner">
            <view class="overlay-spinner">
              <view class="spinner-ring"></view>
              <view class="spinner-ring spinner-ring-2"></view>
            </view>
            <text v-if="text" class="overlay-text">{{ text }}</text>
          </view>
        </template>

        <!-- 行内加载模式 -->
        <template v-else>
          <view class="loading-inline-inner">
            <view class="inline-dots">
              <view class="inline-dot"></view>
              <view class="inline-dot"></view>
              <view class="inline-dot"></view>
            </view>
            <text v-if="text" class="inline-text">{{ text }}</text>
          </view>
        </template>
      </view>
    </transition>
  </Teleport>
</template>

<script setup lang="ts">
import { computed } from 'vue'

interface Props {
  isVisible: boolean
  type?: 'fullscreen' | 'overlay' | 'inline'
  text?: string
  theme?: 'primary' | 'secondary' | 'light' | 'dark'
  closable?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  type: 'fullscreen',
  text: '',
  theme: 'primary',
  closable: false,
})

const emit = defineEmits<{
  close: []
}>()

const handleOverlayClick = () => {
  if (props.closable) {
    emit('close')
  }
}
</script>

<style lang="scss" scoped>
// ================================================
// 基础遮罩层
// ================================================
.page-loading-overlay {
  position: fixed;
  inset: 0;
  z-index: 99999;
  display: flex;
  align-items: center;
  justify-content: center;

  // ---- 全屏模式 ----
  &.page-loading--fullscreen {
    background: var(--gzang-bg, #F8F9FA);

    &.page-loading--primary {
      background: var(--gzang-primary, #0F4C5C);
    }

    &.page-loading--secondary {
      background: var(--gzang-secondary, #FB8B24);
    }

    &.page-loading--dark {
      background: rgba(0, 0, 0, 0.85);
    }

    &.page-loading--light {
      background: rgba(255, 255, 255, 0.9);
    }
  }

  // ---- 遮罩模式 ----
  &.page-loading--overlay {
    background: rgba(0, 0, 0, 0.3);
    backdrop-filter: blur(4px);
    -webkit-backdrop-filter: blur(4px);
  }

  // ---- 行内模式 ----
  &.page-loading--inline {
    position: absolute;
    inset: auto;
    background: transparent;
  }
}

// ================================================
// 全屏启动页样式
// ================================================
.loading-splash {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 20rpx;
  animation: splashFadeIn 0.4s ease-out both;
}

.splash-logo {
  width: 120rpx;
  height: 120rpx;
  border-radius: 28rpx;
  background: linear-gradient(145deg, var(--gzang-secondary, #FB8B24) 0%, var(--gzang-secondary-dark, #e67a1a) 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow:
    0 8rpx 32rpx rgba(251, 139, 36, 0.4),
    0 2rpx 8rpx rgba(251, 139, 36, 0.2),
    inset 0 1rpx 0 rgba(255, 255, 255, 0.2);
  animation: logoFloat 3s ease-in-out infinite;
}

.logo-text {
  font-size: 56rpx;
  font-weight: 700;
  color: white;
  font-family: 'PingFang SC', 'Microsoft YaHei', sans-serif;
  line-height: 1;
}

.splash-brand {
  font-size: 32rpx;
  font-weight: 600;
  color: rgba(255, 255, 255, 0.9);
  letter-spacing: 2rpx;
  animation: brandFadeIn 0.5s 0.2s ease-out both;
}

.splash-progress {
  width: 240rpx;
  margin-top: 8rpx;
  animation: progressFadeIn 0.5s 0.3s ease-out both;
}

.progress-track {
  height: 4rpx;
  background: rgba(255, 255, 255, 0.15);
  border-radius: 2rpx;
  overflow: hidden;
}

.progress-fill {
  height: 100%;
  background: linear-gradient(90deg, var(--gzang-secondary, #FB8B24), rgba(255, 255, 255, 0.8));
  border-radius: 2rpx;
  animation: progressIndeterminate 1.8s ease-in-out infinite;
}

.splash-text {
  font-size: 26rpx;
  color: rgba(255, 255, 255, 0.7);
  animation: textFadeIn 0.5s 0.4s ease-out both;
}

// ================================================
// 遮罩内加载样式
// ================================================
.loading-overlay-inner {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 24rpx;
  background: rgba(255, 255, 255, 0.95);
  padding: 48rpx 64rpx;
  border-radius: 24rpx;
  box-shadow:
    0 8rpx 32rpx rgba(0, 0, 0, 0.12),
    0 2rpx 8rpx rgba(0, 0, 0, 0.06);
}

.overlay-spinner {
  position: relative;
  width: 64rpx;
  height: 64rpx;
}

.spinner-ring {
  position: absolute;
  inset: 0;
  border-radius: 50%;
  border: 4rpx solid transparent;
  border-top-color: var(--gzang-primary, #0F4C5C);
  animation: spinRing 1s linear infinite;

  &.spinner-ring-2 {
    inset: 8rpx;
    border-top-color: var(--gzang-secondary, #FB8B24);
    animation-duration: 0.75s;
    animation-direction: reverse;
  }
}

.overlay-text {
  font-size: 28rpx;
  color: var(--gzang-text-primary, #1F2937);
  font-weight: 500;
}

// ================================================
// 行内加载样式
// ================================================
.loading-inline-inner {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 16rpx;
}

.inline-dots {
  display: flex;
  gap: 8rpx;
  align-items: center;
}

.inline-dot {
  width: 10rpx;
  height: 10rpx;
  border-radius: 50%;
  background: var(--gzang-primary, #0F4C5C);
  animation: dotBounce 1.4s ease-in-out infinite;

  &:nth-child(2) { animation-delay: 0.16s; }
  &:nth-child(3) { animation-delay: 0.32s; }
}

.inline-text {
  font-size: 24rpx;
  color: var(--gzang-text-secondary, #6B7280);
}

// ================================================
// 过渡动画
// ================================================
.page-loading-enter-active {
  transition: opacity 0.2s ease;
}
.page-loading-leave-active {
  transition: opacity 0.3s ease;
}
.page-loading-enter-from,
.page-loading-leave-to {
  opacity: 0;
}

// ================================================
// 关键帧动画
// ================================================
@keyframes splashFadeIn {
  from { opacity: 0; transform: scale(0.95); }
  to { opacity: 1; transform: scale(1); }
}

@keyframes logoFloat {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-6rpx); }
}

@keyframes brandFadeIn {
  from { opacity: 0; transform: translateY(8rpx); }
  to { opacity: 1; transform: translateY(0); }
}

@keyframes progressFadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}

@keyframes textFadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}

@keyframes progressIndeterminate {
  0% { transform: translateX(-100%); width: 50%; }
  50% { width: 70%; }
  100% { transform: translateX(200%); width: 50%; }
}

@keyframes spinRing {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

@keyframes dotBounce {
  0%, 80%, 100% { transform: scale(0.6); opacity: 0.5; }
  40% { transform: scale(1); opacity: 1; }
}

// ================================================
// 深色模式
// ================================================
[data-theme="dark"] {
  .loading-overlay-inner {
    background: rgba(30, 30, 30, 0.95);
  }

  .overlay-text {
    color: rgba(255, 255, 255, 0.9);
  }

  .inline-dot {
    background: var(--gzang-secondary, #FB8B24);
  }

  .inline-text {
    color: rgba(255, 255, 255, 0.6);
  }
}
</style>
