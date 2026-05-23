<!-- ================================================
     G-Zang Mobile - App Startup Loading Screen
     应用初始化全屏启动页

     品牌展示 + 初始化进度指示
     仅在应用冷启动时短暂显示（通常 800-1200ms）
     ================================================ -->
<template>
  <view class="app-startup" :class="{ 'app-startup--exiting': isExiting }">
    <!-- 背景渐变 -->
    <view class="startup-bg">
      <view class="bg-gradient"></view>
      <view class="bg-particles">
        <view
          v-for="i in 6"
          :key="i"
          class="particle"
          :class="`particle--${i}`"
        ></view>
      </view>
    </view>

    <!-- Logo 区域 -->
    <view class="startup-logo-area">
      <view class="logo-container">
        <view class="logo-ring logo-ring--outer"></view>
        <view class="logo-ring logo-ring--inner"></view>
        <view class="logo-icon">
          <text class="logo-glyph">藏</text>
        </view>
      </view>

      <!-- 品牌名 -->
      <view class="brand-name">
        <text class="brand-main">G-Zang</text>
        <text class="brand-sub">归藏财务</text>
      </view>
    </view>

    <!-- 进度指示 -->
    <view class="startup-progress">
      <view class="progress-container">
        <view class="progress-bar">
          <view class="progress-fill" :style="{ width: progress + '%' }"></view>
        </view>
        <view class="progress-steps">
          <view
            v-for="(step, index) in steps"
            :key="index"
            class="progress-step"
            :class="{
              'is-done': index < currentStep,
              'is-active': index === currentStep,
            }"
          >
            <view class="step-dot">
              <view v-if="index < currentStep" class="step-check">
                <text>✓</text>
              </view>
            </view>
            <text class="step-label">{{ step }}</text>
          </view>
        </view>
      </view>
    </view>

    <!-- 版本信息 -->
    <view class="startup-version">
      <text class="version-text">v1.0.0</text>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'

interface Props {
  visible: boolean
}

const props = defineProps<Props>()

const emit = defineEmits<{
  ready: []
  exit: []
}>()

const progress = ref(0)
const currentStep = ref(0)
const isExiting = ref(false)

const steps = ['初始化', '加载配置', '同步数据']

const targetProgress = computed(() => {
  if (currentStep.value >= steps.length) return 100
  return Math.round((currentStep.value / steps.length) * 100)
})

const animateProgress = () => {
  const target = targetProgress.value
  const step = (target - progress.value) / 10
  const timer = setInterval(() => {
    progress.value += step
    if (progress.value >= target) {
      progress.value = target
      clearInterval(timer)
      if (currentStep.value < steps.length) {
        currentStep.value++
        if (currentStep.value < steps.length) {
          setTimeout(animateProgress, 150)
        } else {
          progress.value = 100
          setTimeout(() => {
            triggerExit()
          }, 300)
        }
      }
    }
  }, 30)
}

const triggerExit = () => {
  isExiting.value = true
  setTimeout(() => {
    emit('exit')
  }, 600)
}

onMounted(() => {
  setTimeout(() => {
    emit('ready')
    currentStep.value = 1
    animateProgress()
  }, 200)
})
</script>

<style lang="scss" scoped>
.app-startup {
  position: fixed;
  inset: 0;
  z-index: 999999;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: space-between;
  padding: 120rpx 80rpx 80rpx;
  overflow: hidden;
  transition: opacity 0.6s cubic-bezier(0.16, 1, 0.3, 1),
              transform 0.6s cubic-bezier(0.16, 1, 0.3, 1);

  &--exiting {
    opacity: 0;
    transform: scale(1.05);
    pointer-events: none;
  }
}

// ================================================
// 背景
// ================================================
.startup-bg {
  position: absolute;
  inset: 0;
  overflow: hidden;
}

.bg-gradient {
  position: absolute;
  inset: 0;
  background: linear-gradient(
    160deg,
    #0F4C5C 0%,
    #186a7d 35%,
    #0a3644 70%,
    #111827 100%
  );
}

.bg-particles {
  position: absolute;
  inset: 0;
}

.particle {
  position: absolute;
  border-radius: 50%;
  background: rgba(251, 139, 36, 0.15);
  animation: particleFloat 8s ease-in-out infinite;

  &--1 { width: 300rpx; height: 300rpx; top: -100rpx; right: -80rpx; animation-delay: 0s; }
  &--2 { width: 200rpx; height: 200rpx; bottom: 20%; left: -60rpx; animation-delay: -2s; background: rgba(6, 214, 160, 0.1); }
  &--3 { width: 150rpx; height: 150rpx; top: 30%; right: 10%; animation-delay: -4s; background: rgba(251, 139, 36, 0.08); }
  &--4 { width: 100rpx; height: 100rpx; bottom: 40%; right: -20rpx; animation-delay: -6s; }
  &--5 { width: 180rpx; height: 180rpx; top: 50%; left: -40rpx; animation-delay: -1s; background: rgba(17, 138, 178, 0.1); }
  &--6 { width: 80rpx; height: 80rpx; top: 10%; left: 20%; animation-delay: -3s; }
}

// ================================================
// Logo 区域
// ================================================
.startup-logo-area {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 32rpx;
  margin-top: 80rpx;
  animation: logoAreaFadeIn 0.8s cubic-bezier(0.16, 1, 0.3, 1) 0.1s both;
}

.logo-container {
  position: relative;
  width: 160rpx;
  height: 160rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.logo-ring {
  position: absolute;
  border-radius: 50%;
  border: 2rpx solid rgba(251, 139, 36, 0.3);
  animation: ringPulse 3s ease-in-out infinite;

  &--outer {
    inset: 0;
    animation-delay: 0s;
  }

  &--inner {
    inset: 16rpx;
    border-color: rgba(251, 139, 36, 0.2);
    animation-delay: 0.5s;
  }
}

.logo-icon {
  width: 100rpx;
  height: 100rpx;
  border-radius: 24rpx;
  background: linear-gradient(145deg, #FB8B24 0%, #e67a1a 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow:
    0 8rpx 32rpx rgba(251, 139, 36, 0.4),
    0 2rpx 8rpx rgba(251, 139, 36, 0.2),
    inset 0 1rpx 0 rgba(255, 255, 255, 0.2);
  animation: logoBreathing 3s ease-in-out infinite;
}

.logo-glyph {
  font-size: 48rpx;
  font-weight: 700;
  color: white;
  line-height: 1;
}

.brand-name {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4rpx;
}

.brand-main {
  font-size: 48rpx;
  font-weight: 700;
  color: white;
  letter-spacing: 4rpx;
  font-family: -apple-system, BlinkMacSystemFont, 'SF Pro Display', sans-serif;
}

.brand-sub {
  font-size: 28rpx;
  font-weight: 400;
  color: rgba(255, 255, 255, 0.6);
  letter-spacing: 8rpx;
}

// ================================================
// 进度区域
// ================================================
.startup-progress {
  width: 100%;
  animation: progressFadeIn 0.6s ease 0.4s both;
}

.progress-container {
  display: flex;
  flex-direction: column;
  gap: 32rpx;
}

.progress-bar {
  height: 4rpx;
  background: rgba(255, 255, 255, 0.12);
  border-radius: 2rpx;
  overflow: hidden;
}

.progress-fill {
  height: 100%;
  background: linear-gradient(90deg, #FB8B24, #FFD166);
  border-radius: 2rpx;
  transition: width 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
  position: relative;

  &::after {
    content: '';
    position: absolute;
    right: 0;
    top: 50%;
    transform: translateY(-50%);
    width: 8rpx;
    height: 8rpx;
    border-radius: 50%;
    background: #FB8B24;
    box-shadow: 0 0 8rpx rgba(251, 139, 36, 0.8);
  }
}

.progress-steps {
  display: flex;
  justify-content: space-between;
}

.progress-step {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8rpx;
  flex: 1;
  transition: opacity 0.3s ease;

  &.is-done {
    .step-dot {
      background: var(--gzang-success, #06D6A0);
      border-color: var(--gzang-success, #06D6A0);
    }
    .step-label {
      color: rgba(255, 255, 255, 0.8);
    }
  }

  &.is-active {
    .step-dot {
      background: var(--gzang-secondary, #FB8B24);
      border-color: var(--gzang-secondary, #FB8B24);
      animation: stepPulse 1s ease-in-out infinite;
    }
    .step-label {
      color: white;
      font-weight: 600;
    }
  }

  &:not(.is-done):not(.is-active) {
    .step-dot {
      background: transparent;
      border-color: rgba(255, 255, 255, 0.2);
    }
    .step-label {
      color: rgba(255, 255, 255, 0.4);
    }
  }
}

.step-dot {
  width: 24rpx;
  height: 24rpx;
  border-radius: 50%;
  border: 2rpx solid rgba(255, 255, 255, 0.3);
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;
}

.step-check {
  font-size: 14rpx;
  color: white;
  line-height: 1;
}

.step-label {
  font-size: 22rpx;
  color: rgba(255, 255, 255, 0.5);
  transition: color 0.3s ease;
}

// ================================================
// 版本信息
// ================================================
.startup-version {
  animation: progressFadeIn 0.6s ease 0.5s both;
}

.version-text {
  font-size: 22rpx;
  color: rgba(255, 255, 255, 0.3);
  letter-spacing: 1rpx;
}

// ================================================
// 关键帧动画
// ================================================
@keyframes logoAreaFadeIn {
  from { opacity: 0; transform: translateY(24rpx); }
  to { opacity: 1; transform: translateY(0); }
}

@keyframes progressFadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}

@keyframes particleFloat {
  0%, 100% { transform: translate(0, 0) scale(1); }
  33% { transform: translate(20rpx, -20rpx) scale(1.05); }
  66% { transform: translate(-10rpx, 10rpx) scale(0.95); }
}

@keyframes ringPulse {
  0%, 100% { transform: scale(1); opacity: 0.3; }
  50% { transform: scale(1.08); opacity: 0.5; }
}

@keyframes logoBreathing {
  0%, 100% {
    transform: scale(1);
    box-shadow:
      0 8rpx 32rpx rgba(251, 139, 36, 0.4),
      0 2rpx 8rpx rgba(251, 139, 36, 0.2),
      inset 0 1rpx 0 rgba(255, 255, 255, 0.2);
  }
  50% {
    transform: scale(1.03);
    box-shadow:
      0 12rpx 40rpx rgba(251, 139, 36, 0.5),
      0 4rpx 12rpx rgba(251, 139, 36, 0.3),
      inset 0 1rpx 0 rgba(255, 255, 255, 0.25);
  }
}

@keyframes stepPulse {
  0%, 100% { box-shadow: 0 0 0 0 rgba(251, 139, 36, 0.4); }
  50% { box-shadow: 0 0 0 8rpx rgba(251, 139, 36, 0); }
}
</style>
