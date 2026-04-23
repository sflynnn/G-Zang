<template>
  <view class="voice-recorder">
    <view class="recorder-container">
      <view class="waveform-area">
        <view
          v-for="i in 20"
          :key="i"
          class="wave-bar"
          :class="{ 'wave-bar--active': isRecording }"
          :style="{ animationDelay: `${i * 0.05}s`, height: isRecording ? `${getWaveHeight(i)}px` : '4px' }"
        ></view>
      </view>

      <view class="time-display">
        <text class="time-text">{{ formatDuration(recordingTime) }}</text>
        <text class="time-hint">{{ isRecording ? '点击完成录制' : '按住说话' }}</text>
      </view>

      <view class="control-area">
        <view
          class="record-btn"
          :class="{ 'record-btn--recording': isRecording }"
          @touchstart.prevent="handleTouchStart"
          @touchend.prevent="handleTouchEnd"
          @mousedown="handleTouchStart"
          @mouseup="handleTouchEnd"
        >
          <view class="btn-inner">
            <uni-icons
              :type="isRecording ? 'checkmark' : 'mic'"
              :size="32"
              color="white"
            />
          </view>
        </view>
      </view>

      <view v-if="lastResult" class="result-preview">
        <view class="result-header">
          <uni-icons type="paperplane" :size="16" color="var(--gzang-primary)" />
          <text class="result-title">识别结果</text>
        </view>
        <view class="result-content">
          <text class="result-text">{{ lastResult.rawText }}</text>
        </view>
        <view v-if="lastResult.amount" class="result-amount">
          <text class="amount-label">识别金额：</text>
          <text class="amount-value">¥{{ lastResult.amount }}</text>
        </view>
        <view class="result-actions">
          <button class="btn btn--secondary" @click="handleRetry">重新录制</button>
          <button class="btn btn--primary" @click="handleConfirm">确认记账</button>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue'

interface VoiceResult {
  filePath: string
  duration: number
}

interface Props {
  isProcessing?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  isProcessing: false
})

const emit = defineEmits<{
  'start': []
  'stop': []
  'confirm': [result: VoiceResult]
  'cancel': []
}>()

const isRecording = ref(false)
const recordingTime = ref(0)
const lastResult = ref<{ rawText: string; amount?: number } | null>(null)

let timer: ReturnType<typeof setInterval> | null = null

const handleTouchStart = () => {
  startRecording()
}

const handleTouchEnd = async () => {
  if (isRecording.value) {
    await stopRecording()
  }
}

const startRecording = () => {
  isRecording.value = true
  recordingTime.value = 0
  emit('start')

  timer = setInterval(() => {
    recordingTime.value++
    if (recordingTime.value >= 60) {
      stopRecording()
    }
  }, 1000)
}

const stopRecording = async () => {
  if (timer) {
    clearInterval(timer)
    timer = null
  }

  isRecording.value = false
  emit('stop')

  // 模拟语音识别结果
  // 实际项目中这里应该调用语音识别 API
  await new Promise(resolve => setTimeout(resolve, 500))

  // 模拟识别结果
  lastResult.value = {
    rawText: '今天午餐消费 45 元，餐饮',
    amount: 45
  }
}

const formatDuration = (seconds: number): string => {
  const mins = Math.floor(seconds / 60)
  const secs = seconds % 60
  return `${mins.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`
}

const getWaveHeight = (index: number): number => {
  // 生成随机波形高度
  const base = Math.sin(Date.now() / 200 + index) * 10
  return 8 + Math.abs(base) + Math.random() * 20
}

const handleRetry = () => {
  lastResult.value = null
  recordingTime.value = 0
}

const handleConfirm = () => {
  if (lastResult.value) {
    emit('confirm', {
      filePath: '',
      duration: recordingTime.value
    })
  }
}
</script>

<style scoped lang="scss">
.voice-recorder {
  padding: 24rpx;
}

.recorder-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 32rpx;
}

.waveform-area {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6rpx;
  height: 120rpx;
  padding: 0 24rpx;
}

.wave-bar {
  width: 6rpx;
  background-color: var(--gzang-border);
  border-radius: 3rpx;
  transition: height 0.1s ease;

  &--active {
    background-color: var(--gzang-primary);
  }
}

.time-display {
  text-align: center;
}

.time-text {
  font-size: 48rpx;
  font-family: 'Roboto Mono', monospace;
  color: var(--gzang-primary);
  font-weight: 600;
}

.time-hint {
  display: block;
  font-size: 26rpx;
  color: var(--gzang-text-secondary);
  margin-top: 8rpx;
}

.control-area {
  padding: 16rpx;
}

.record-btn {
  width: 120rpx;
  height: 120rpx;
  border-radius: 50%;
  background-color: var(--gzang-secondary);
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 8rpx 24rpx rgba(251, 139, 36, 0.3);
  transition: all 0.2s;

  &--recording {
    background-color: var(--gzang-danger);
    box-shadow: 0 8rpx 24rpx rgba(239, 71, 111, 0.3);
    transform: scale(1.1);

    .btn-inner {
      animation: pulse 1s ease-in-out infinite;
    }
  }

  &:active {
    transform: scale(0.95);
  }
}

.btn-inner {
  width: 96rpx;
  height: 96rpx;
  border-radius: 50%;
  background-color: rgba(255, 255, 255, 0.2);
  display: flex;
  align-items: center;
  justify-content: center;
}

@keyframes pulse {
  0%, 100% { transform: scale(1); }
  50% { transform: scale(1.1); }
}

.result-preview {
  width: 100%;
  background-color: var(--gzang-surface);
  border-radius: 20rpx;
  padding: 24rpx;
  box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.04);
}

.result-header {
  display: flex;
  align-items: center;
  gap: 8rpx;
  margin-bottom: 16rpx;
}

.result-title {
  font-size: 28rpx;
  font-weight: 600;
  color: var(--gzang-primary);
}

.result-content {
  background-color: #f8f8f8;
  border-radius: 12rpx;
  padding: 16rpx;
  margin-bottom: 16rpx;
}

.result-text {
  font-size: 28rpx;
  color: var(--gzang-text-primary);
  line-height: 1.5;
}

.result-amount {
  display: flex;
  align-items: center;
  gap: 8rpx;
  margin-bottom: 20rpx;
}

.amount-label {
  font-size: 26rpx;
  color: var(--gzang-text-secondary);
}

.amount-value {
  font-size: 32rpx;
  font-weight: 600;
  color: var(--gzang-danger);
  font-family: 'Roboto Mono', monospace;
}

.result-actions {
  display: flex;
  gap: 16rpx;

  .btn {
    flex: 1;
  }
}
</style>
