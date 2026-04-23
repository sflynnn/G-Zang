<template>
  <view class="loading-spinner" :class="[`spinner--${size}`, `spinner--${type}`]">
    <view v-if="type === 'spinner'" class="spinner-circle"></view>
    <view v-else-if="type === 'dots'" class="spinner-dots">
      <view class="dot"></view>
      <view class="dot"></view>
      <view class="dot"></view>
    </view>
    <view v-else-if="type === 'wave'" class="spinner-wave">
      <view class="wave-bar"></view>
      <view class="wave-bar"></view>
      <view class="wave-bar"></view>
      <view class="wave-bar"></view>
      <view class="wave-bar"></view>
    </view>
    <text v-if="text" class="spinner-text">{{ text }}</text>
  </view>
</template>

<script setup lang="ts">
interface Props {
  size?: 'sm' | 'md' | 'lg'
  type?: 'spinner' | 'dots' | 'wave'
  text?: string
  color?: string
}

withDefaults(defineProps<Props>(), {
  size: 'md',
  type: 'spinner',
  text: ''
})
</script>

<style scoped lang="scss">
.loading-spinner {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 16rpx;

  &--sm {
    .spinner-circle { width: 32rpx; height: 32rpx; border-width: 3rpx; }
    .dot { width: 8rpx; height: 8rpx; }
    .wave-bar { width: 4rpx; }
    .spinner-text { font-size: 22rpx; }
  }

  &--md {
    .spinner-circle { width: 48rpx; height: 48rpx; border-width: 4rpx; }
    .dot { width: 12rpx; height: 12rpx; }
    .wave-bar { width: 6rpx; }
    .spinner-text { font-size: 26rpx; }
  }

  &--lg {
    .spinner-circle { width: 72rpx; height: 72rpx; border-width: 5rpx; }
    .dot { width: 16rpx; height: 16rpx; }
    .wave-bar { width: 8rpx; }
    .spinner-text { font-size: 28rpx; }
  }
}

.spinner-circle {
  border-style: solid;
  border-color: var(--gzang-border);
  border-top-color: var(--gzang-primary);
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}

.spinner-dots {
  display: flex;
  gap: 8rpx;
}

.dot {
  background-color: var(--gzang-primary);
  border-radius: 50%;
  animation: bounce 1.4s ease-in-out infinite;

  &:nth-child(1) { animation-delay: 0s; }
  &:nth-child(2) { animation-delay: 0.16s; }
  &:nth-child(3) { animation-delay: 0.32s; }
}

@keyframes bounce {
  0%, 80%, 100% { transform: scale(0); opacity: 0.5; }
  40% { transform: scale(1); opacity: 1; }
}

.spinner-wave {
  display: flex;
  gap: 6rpx;
  align-items: flex-end;
}

.wave-bar {
  background-color: var(--gzang-primary);
  border-radius: 4rpx;
  animation: wave 1.2s ease-in-out infinite;

  &:nth-child(1) { animation-delay: 0s; height: 60%; }
  &:nth-child(2) { animation-delay: 0.1s; height: 80%; }
  &:nth-child(3) { animation-delay: 0.2s; height: 100%; }
  &:nth-child(4) { animation-delay: 0.3s; height: 80%; }
  &:nth-child(5) { animation-delay: 0.4s; height: 60%; }
}

@keyframes wave {
  0%, 100% { transform: scaleY(0.5); }
  50% { transform: scaleY(1); }
}

.spinner-text {
  color: var(--gzang-text-secondary);
}

@keyframes spin {
  to { transform: rotate(360deg); }
}
</style>
