<template>
  <view v-if="isOffline" class="offline-indicator">
    <uni-icons type="wifi-off" :size="14" color="white" />
    <text class="indicator-text">当前处于离线状态</text>
    <text v-if="pendingCount > 0" class="pending-badge">{{ pendingCount }}条待同步</text>
  </view>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'

const isOffline = ref(false)
const pendingCount = ref(0)

let unsubscribe: (() => void) | null = null

const checkNetworkStatus = () => {
  uni.getNetworkType({
    success: (res) => {
      isOffline.value = res.networkType === 'none'
    }
  })
}

onMounted(() => {
  checkNetworkStatus()
  uni.onNetworkStatusChange((res) => {
    isOffline.value = !res.isConnected
  })

  // 监听待同步数量变化
  try {
    const data = uni.getStorageSync('offline_records')
    if (data) {
      pendingCount.value = JSON.parse(data).length || 0
    }
  } catch {
    pendingCount.value = 0
  }
})

onUnmounted(() => {
  unsubscribe?.()
})
</script>

<style scoped lang="scss">
.offline-indicator {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 9999;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8rpx;
  padding: 8rpx;
  background-color: var(--gzang-warning);
  background-color: #F59E0B;
}

.indicator-text {
  font-size: 24rpx;
  color: white;
}

.pending-badge {
  font-size: 22rpx;
  color: rgba(255, 255, 255, 0.9);
  background-color: rgba(255, 255, 255, 0.2);
  padding: 2rpx 12rpx;
  border-radius: 20rpx;
}
</style>
