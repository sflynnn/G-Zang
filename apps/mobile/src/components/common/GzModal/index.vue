<script setup lang="ts">
import { onMounted, onUnmounted } from 'vue'
import { useModal } from '@/composables/useModal'
import AppleIcon from '@/components/common/AppleIcon/index.vue'

const { state, debug, confirm, cancel } = useModal()

onMounted(() => {
  debug.mounted = true
  debug.lastAction = 'component-mounted'
  debug.lastUpdatedAt = new Date().toISOString()
})

onUnmounted(() => {
  debug.mounted = false
  debug.lastAction = 'component-unmounted'
  debug.lastUpdatedAt = new Date().toISOString()
})

const handleConfirm = () => {
  uni.removeStorageSync('__auth_modal_showing__')
  uni.reLaunch({ url: '/pages/login/index' })
  confirm()
}

const handleCancel = () => {
  uni.removeStorageSync('__auth_modal_showing__')
  setTimeout(() => {
    uni.reLaunch({ url: '/pages/login/index' })
  }, 2000)
  cancel()
}
</script>

<template>
  <view class="gz-modal-overlay" v-if="state.visible" catchtouchmove="true" @click.self="handleCancel">
      <view class="gz-modal-box">
        <view class="gz-modal-icon">
          <AppleIcon name="alert-circle" :size="40" color="#FB8B24" />
        </view>
        <text class="gz-modal-title">{{ state.title }}</text>
        <text class="gz-modal-message">{{ state.message }}</text>
        <text class="gz-modal-debug">debug: visible={{ String(state.visible) }} | mounted={{ String(debug.mounted) }} | showCalls={{ debug.showCalls }}</text>
        <view class="gz-modal-actions">
          <view
            v-if="state.showCancel"
            class="gz-modal-btn gz-modal-btn-cancel"
            @click="handleCancel"
          >
            {{ state.cancelText }}
          </view>
          <view
            class="gz-modal-btn gz-modal-btn-confirm"
            @click="handleConfirm"
          >
            {{ state.confirmText }}
          </view>
        </view>
      </view>
    </view>
</template>
<style lang="scss" scoped>
.gz-modal-overlay {
  position: fixed;
  inset: 0;
  z-index: 99999;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0 80rpx;
  animation: modalOverlayIn 0.2s ease-out;
}

.gz-modal-box {
  width: 100%;
  max-width: 560rpx;
  background: rgba(255, 255, 255, 0.98);
  border-radius: 32rpx;
  padding: 48rpx 40rpx 40rpx;
  display: flex;
  flex-direction: column;
  align-items: center;
  animation: modalBoxIn 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
}

.gz-modal-icon {
  margin-bottom: 24rpx;
}

.gz-modal-title {
  font-size: 32rpx;
  font-weight: 600;
  color: #1F2937;
  margin-bottom: 16rpx;
  text-align: center;
}

.gz-modal-message {
  font-size: 28rpx;
  color: #6B7280;
  line-height: 1.5;
  text-align: center;
  margin-bottom: 20rpx;
}

.gz-modal-debug {
  font-size: 20rpx;
  color: #9CA3AF;
  line-height: 1.3;
  text-align: center;
  margin-bottom: 20rpx;
  word-break: break-all;
}

.gz-modal-actions {
  display: flex;
  width: 100%;
  gap: 24rpx;
}

.gz-modal-btn {
  flex: 1;
  height: 88rpx;
  border-radius: 44rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 30rpx;
  font-weight: 600;

  &.gz-modal-btn-cancel {
    background: #F3F4F6;
    color: #6B7280;
  }

  &.gz-modal-btn-confirm {
    background: linear-gradient(135deg, #FB8B24, #FF6B35);
    color: #FFFFFF;
    box-shadow: 0 8rpx 24rpx rgba(251, 139, 36, 0.3);
  }
}

@keyframes modalOverlayIn {
  from { opacity: 0; }
  to { opacity: 1; }
}

@keyframes modalBoxIn {
  from {
    opacity: 0;
    transform: scale(0.85);
  }
  to {
    opacity: 1;
    transform: scale(1);
  }
}
</style>
