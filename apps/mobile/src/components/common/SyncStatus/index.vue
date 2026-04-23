<template>
  <view v-if="syncStatus.syncing || syncStatus.pending > 0" class="sync-status">
    <view v-if="syncStatus.syncing" class="status-syncing">
      <view class="sync-spinner"></view>
      <text class="status-text">同步中...</text>
    </view>
    <view v-else class="status-pending">
      <uni-icons type="cloud-upload" :size="14" color="var(--gzang-warning)" />
      <text class="status-text">{{ syncStatus.pending }} 条待同步</text>
    </view>
    <text v-if="syncStatus.lastSyncTime" class="sync-time">
      上次同步: {{ formatTime(syncStatus.lastSyncTime) }}
    </text>
  </view>
</template>

<script setup lang="ts">
import dayjs from 'dayjs'
import type { SyncStatus } from '@/types/device'

interface Props {
  syncStatus: SyncStatus
}

defineProps<Props>()

const formatTime = (time: string): string => {
  return dayjs(time).format('HH:mm:ss')
}
</script>

<style scoped lang="scss">
.sync-status {
  display: flex;
  align-items: center;
  gap: 12rpx;
  padding: 8rpx 16rpx;
  background-color: var(--gzang-surface);
  border-radius: 20rpx;
  box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.04);
}

.status-syncing {
  display: flex;
  align-items: center;
  gap: 8rpx;
}

.sync-spinner {
  width: 24rpx;
  height: 24rpx;
  border: 3rpx solid var(--gzang-border);
  border-top-color: var(--gzang-primary);
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.status-text {
  font-size: 24rpx;
  color: var(--gzang-text-secondary);
}

.status-pending {
  display: flex;
  align-items: center;
  gap: 6rpx;
}

.sync-time {
  font-size: 22rpx;
  color: var(--gzang-text-secondary);
  opacity: 0.7;
}
</style>
