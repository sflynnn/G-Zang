<script setup lang="ts">
import { ref, watch } from 'vue'

interface Props {
  show: boolean
  title?: string
  confirmText?: string
  height?: string
  showConfirm?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  title: '',
  confirmText: '',
  height: '70vh',
  showConfirm: false,
})

const emit = defineEmits<{
  'update:show': [value: boolean]
  confirm: []
  close: []
}>()

function close() {
  emit('update:show', false)
  emit('close')
}

function confirm() {
  emit('confirm')
  emit('update:show', false)
}

function onMaskClick() {
  close()
}

watch(() => props.show, (val) => {
  if (typeof uni !== 'undefined' && uni.pageScrollTo) {
    if (val) {
      uni.pageScrollTo({ scrollTop: 0, duration: 0 })
    }
  }
})
</script>

<template>
  <view
    v-if="show"
    class="selector-mask"
    @click="onMaskClick"
  >
    <view
      class="selector-sheet"
      :class="{ 'sheet-visible': show }"
      :style="{ height }"
      @click.stop
    >
      <!-- Drag handle -->
      <view class="sheet-handle">
        <view class="handle-bar"></view>
      </view>

      <!-- Header -->
      <view class="sheet-header">
        <view class="header-left">
          <text class="close-btn" @click="close">取消</text>
        </view>
        <text v-if="title" class="header-title">{{ title }}</text>
        <view class="header-right">
          <text
            v-if="showConfirm"
            class="confirm-btn"
            @click="confirm"
          >{{ confirmText || '确认' }}</text>
          <view v-else class="header-spacer"></view>
        </view>
      </view>

      <!-- Content -->
      <scroll-view
        class="sheet-content"
        scroll-y="true"
        :enhanced="true"
        :bounces="true"
      >
        <slot></slot>
      </scroll-view>
    </view>
  </view>
</template>

<style lang="scss" scoped>
.selector-mask {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 999;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: flex-end;
  justify-content: center;
  animation: fadeIn 0.2s ease;
}

@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}

.selector-sheet {
  width: 100%;
  max-width: 100%;
  background: #fff;
  border-radius: 32rpx 32rpx 0 0;
  overflow: hidden;
  display: flex;
  flex-direction: column;
  max-height: 90vh;
  transform: translateY(100%);
  transition: transform 300ms cubic-bezier(0.34, 1.56, 0.64, 1);

  &.sheet-visible {
    transform: translateY(0);
  }
}

.sheet-handle {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 20rpx 0 12rpx;
  flex-shrink: 0;
}

.handle-bar {
  width: 80rpx;
  height: 8rpx;
  background: #D1D5DB;
  border-radius: 4rpx;
}

.sheet-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 32rpx 24rpx;
  flex-shrink: 0;
  border-bottom: 1rpx solid #F3F4F6;
}

.header-left,
.header-right {
  min-width: 100rpx;
}

.header-right {
  display: flex;
  justify-content: flex-end;
}

.header-spacer {
  width: 1rpx;
}

.header-title {
  font-size: 34rpx;
  font-weight: 600;
  color: #1F2937;
  flex: 1;
  text-align: center;
  letter-spacing: 0.5rpx;
}

.close-btn {
  font-size: 32rpx;
  color: #6B7280;
  padding: 8rpx 0;
  font-weight: 500;
}

.confirm-btn {
  font-size: 32rpx;
  color: #0F4C5C;
  font-weight: 600;
  padding: 8rpx 0;
}

.sheet-content {
  flex: 1;
  overflow-y: auto;
  -webkit-overflow-scrolling: touch;
  padding-bottom: constant(safe-area-inset-bottom);
  padding-bottom: env(safe-area-inset-bottom);
}
</style>
