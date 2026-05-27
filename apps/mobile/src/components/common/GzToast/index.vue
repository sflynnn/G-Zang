<script setup lang="ts">
import { useToast } from '@/composables/useToast'
import AppleIcon from '@/components/common/AppleIcon/index.vue'

const { list, getIconConfig } = useToast()
</script>

<template>
  <view class="gz-toast-container" v-if="list.length > 0">
    <view
      v-for="item in list"
      :key="item.id"
      class="gz-toast-item"
      :class="`gz-toast-${item.type}`"
    >
      <view class="gz-toast-icon">
        <AppleIcon
          :name="getIconConfig(item.type).icon"
          :size="22"
          :color="getIconConfig(item.type).color"
        />
      </view>
      <text class="gz-toast-message">{{ item.message }}</text>
    </view>
  </view>
</template>
<style lang="scss" scoped>
.gz-toast-container {
  position: fixed;
  top: 120rpx;
  left: 0;
  right: 0;
  z-index: 99999;
  display: flex;
  flex-direction: column;
  align-items: center;
  pointer-events: none;
  padding: 0 32rpx;
}

.gz-toast-item {
  display: flex;
  align-items: center;
  gap: 16rpx;
  padding: 24rpx 32rpx;
  background: rgba(255, 255, 255, 0.98);
  border-radius: 24rpx;
  box-shadow: 0 8rpx 32rpx rgba(0, 0, 0, 0.12);
  margin-bottom: 16rpx;
  max-width: 100%;
  animation: toastIn 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);

  &.gz-toast-success {
    border-left: 8rpx solid #06D6A0;
  }

  &.gz-toast-error {
    border-left: 8rpx solid #EF476F;
  }

  &.gz-toast-warning {
    border-left: 8rpx solid #FB8B24;
  }

  &.gz-toast-info {
    border-left: 8rpx solid #0F4C5C;
  }
}

.gz-toast-icon {
  flex-shrink: 0;
  display: flex;
  align-items: center;
  justify-content: center;
}

.gz-toast-message {
  font-size: 28rpx;
  color: #1F2937;
  font-weight: 500;
  line-height: 1.4;
}

@keyframes toastIn {
  from {
    opacity: 0;
    transform: translateY(-20rpx) scale(0.9);
  }
  to {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}
</style>
