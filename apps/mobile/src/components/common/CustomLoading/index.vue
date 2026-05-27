<script setup lang="ts">
import { loadingManager } from '@/composables/loadingManager'

// 直接使用，loadingManager.state 本身已是 reactive 对象
const state = loadingManager.state
</script>

<template>
  <view class="gz-loading-overlay" v-if="state.visible" @click.stop>
    <view class="gz-loading-content">
      <!-- 品牌渐变圆环 -->
      <view class="gz-loading-ring">
        <view class="gz-loading-arc gz-loading-arc-1"></view>
        <view class="gz-loading-arc gz-loading-arc-2"></view>
        <view class="gz-loading-brand">G</view>
      </view>
      <text class="gz-loading-text">{{ state.text }}</text>
    </view>
  </view>
</template>

<style lang="scss" scoped>
.gz-loading-overlay {
  position: fixed;
  inset: 0;
  z-index: 99998;
  background: rgba(15, 76, 92, 0.25);
  backdrop-filter: blur(5rpx);
  -webkit-backdrop-filter: blur(5rpx);
  display: flex;
  align-items: center;
  justify-content: center;
}

.gz-loading-content {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.gz-loading-ring {
  width: 100rpx;
  height: 100rpx;
  position: relative;
  margin-bottom: 24rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.gz-loading-arc {
  position: absolute;
  border-radius: 50%;
  border-style: solid;
  border-color: transparent;
}

.gz-loading-arc-1 {
  width: 100%;
  height: 100%;
  border-width: 5rpx;
  border-top-color: #FB8B24;
  animation: spin 1s linear infinite;
}

.gz-loading-arc-2 {
  width: 70%;
  height: 70%;
  border-width: 5rpx;
  border-bottom-color: rgba(255, 255, 255, 0.5);
  animation: spin 1.5s linear infinite reverse;
}

.gz-loading-brand {
  font-size: 36rpx;
  font-weight: 800;
  font-style: italic;
  color: #FFFFFF;
  z-index: 1;
}

.gz-loading-text {
  font-size: 26rpx;
  color: rgba(255, 255, 255, 0.9);
  font-weight: 500;
  letter-spacing: 2rpx;
}

@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}
</style>
