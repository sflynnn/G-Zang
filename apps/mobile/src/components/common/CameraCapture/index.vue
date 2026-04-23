<template>
  <view class="camera-capture">
    <view class="capture-area">
      <camera
        v-if="showCamera"
        class="camera-view"
        device-position="back"
        flash="auto"
        @error="handleCameraError"
      />

      <view v-else class="camera-placeholder">
        <uni-icons type="camera" :size="64" color="#9CA3AF" />
        <text class="placeholder-text">相机不可用</text>
      </view>

      <view class="capture-overlay">
        <view class="corner corner--tl"></view>
        <view class="corner corner--tr"></view>
        <view class="corner corner--bl"></view>
        <view class="corner corner--br"></view>
      </view>

      <view class="capture-hint">
        <text>将小票/发票放入框内</text>
      </view>
    </view>

    <view class="control-area">
      <view class="control-btn" @click="handleAlbum">
        <uni-icons type="image" :size="24" color="var(--gzang-primary)" />
        <text class="btn-label">相册</text>
      </view>

      <view class="capture-btn" @click="handleCapture">
        <view class="capture-inner"></view>
      </view>

      <view class="control-btn" @click="handleFlash">
        <uni-icons :type="flashOn ? 'flash-fill' : 'flash-off'" :size="24" :color="flashOn ? 'var(--gzang-warning)' : 'var(--gzang-text-secondary)'" />
        <text class="btn-label">闪光灯</text>
      </view>
    </view>

    <uni-popup ref="resultPopup" type="bottom">
      <view v-if="ocrResult" class="ocr-result">
        <view class="result-header">
          <text class="result-title">识别结果</text>
          <view class="result-close" @click="resultPopup?.close()">
            <uni-icons type="closeempty" :size="20" color="#666" />
          </view>
        </view>

        <view class="result-preview">
          <image
            v-if="capturedImage"
            :src="capturedImage"
            class="preview-image"
            mode="aspectFit"
          />
        </view>

        <view class="result-form">
          <view class="form-row">
            <text class="row-label">识别金额</text>
            <input
              v-model="ocrResult.amount"
              type="digit"
              class="row-input"
              placeholder="请输入金额"
            />
          </view>
          <view class="form-row">
            <text class="row-label">消费分类</text>
            <input
              v-model="ocrResult.category"
              class="row-input"
              placeholder="请输入分类"
            />
          </view>
          <view class="form-row">
            <text class="row-label">商家名称</text>
            <input
              v-model="ocrResult.merchant"
              class="row-input"
              placeholder="请输入商家"
            />
          </view>
        </view>

        <view class="result-actions">
          <button class="btn btn--secondary btn--block" @click="handleReset">重新拍摄</button>
          <button class="btn btn--primary btn--block" @click="handleConfirm">确认记账</button>
        </view>
      </view>

      <view v-else class="loading-state">
        <LoadingSpinner type="wave" size="lg" text="正在识别中..." />
      </view>
    </uni-popup>
  </view>
</template>

<script setup lang="ts">
import { ref } from 'vue'

interface OCRData {
  amount: string
  category: string
  merchant: string
}

interface Props {
  showCamera?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  showCamera: true
})

const emit = defineEmits<{
  'confirm': [data: OCRData]
  'cancel': []
}>()

import LoadingSpinner from '@/components/common/LoadingSpinner/index.vue'

const resultPopup = ref<any>(null)
const capturedImage = ref('')
const ocrResult = ref<OCRData | null>(null)
const flashOn = ref(false)
const showCamera = ref(props.showCamera)

const handleCapture = async () => {
  try {
    const ctx = uni.createCameraContext()
    ctx.takePhoto({
      quality: 'high',
      success: async (res) => {
        capturedImage.value = res.tempImagePath
        ocrResult.value = {
          amount: '',
          category: '',
          merchant: ''
        }
        resultPopup.value.open()

        // 模拟 OCR 识别
        await new Promise(resolve => setTimeout(resolve, 1500))
        ocrResult.value = {
          amount: '68.50',
          category: '餐饮',
          merchant: '某某餐厅'
        }
      }
    })
  } catch (error) {
    uni.showToast({ title: '拍照失败', icon: 'none' })
  }
}

const handleAlbum = () => {
  uni.chooseImage({
    count: 1,
    sourceType: ['album'],
    success: (res) => {
      if (res.tempFilePaths[0]) {
        capturedImage.value = res.tempFilePaths[0]
        ocrResult.value = {
          amount: '',
          category: '',
          merchant: ''
        }
        resultPopup.value.open()

        // 模拟 OCR 识别
        setTimeout(() => {
          ocrResult.value = {
            amount: '128.00',
            category: '购物',
            merchant: '某某超市'
          }
        }, 1500)
      }
    }
  })
}

const handleFlash = () => {
  flashOn.value = !flashOn.value
}

const handleCameraError = (e: any) => {
  showCamera.value = false
}

const handleReset = () => {
  resultPopup.value.close()
  capturedImage.value = ''
  ocrResult.value = null
}

const handleConfirm = () => {
  if (ocrResult.value) {
    emit('confirm', ocrResult.value)
    resultPopup.value.close()
  }
}
</script>

<style scoped lang="scss">
.camera-capture {
  height: 100%;
  display: flex;
  flex-direction: column;
  background-color: #000;
}

.capture-area {
  flex: 1;
  position: relative;
  overflow: hidden;
}

.camera-view {
  width: 100%;
  height: 100%;
}

.camera-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 16rpx;
  background-color: #1a1a1a;
}

.placeholder-text {
  font-size: 28rpx;
  color: #666;
}

.capture-overlay {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 80%;
  height: 50%;
}

.corner {
  position: absolute;
  width: 40rpx;
  height: 40rpx;
  border-color: white;
  border-style: solid;

  &--tl {
    top: 0; left: 0;
    border-width: 4rpx 0 0 4rpx;
  }

  &--tr {
    top: 0; right: 0;
    border-width: 4rpx 4rpx 0 0;
  }

  &--bl {
    bottom: 0; left: 0;
    border-width: 0 0 4rpx 4rpx;
  }

  &--br {
    bottom: 0; right: 0;
    border-width: 0 4rpx 4rpx 0;
  }
}

.capture-hint {
  position: absolute;
  bottom: 40rpx;
  left: 0;
  right: 0;
  text-align: center;

  text {
    font-size: 28rpx;
    color: white;
    background-color: rgba(0, 0, 0, 0.5);
    padding: 8rpx 24rpx;
    border-radius: 24rpx;
  }
}

.control-area {
  display: flex;
  align-items: center;
  justify-content: space-around;
  padding: 40rpx;
  background-color: #1a1a1a;
}

.control-btn {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8rpx;
  padding: 16rpx;
}

.btn-label {
  font-size: 22rpx;
  color: var(--gzang-text-secondary);
}

.capture-btn {
  width: 120rpx;
  height: 120rpx;
  border-radius: 50%;
  background-color: rgba(255, 255, 255, 0.3);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 8rpx;
}

.capture-inner {
  width: 96rpx;
  height: 96rpx;
  border-radius: 50%;
  background-color: white;
  transition: transform 0.1s;

  &:active {
    transform: scale(0.9);
  }
}

.ocr-result {
  background-color: var(--gzang-surface);
  border-radius: 24rpx 24rpx 0 0;
  padding: 24rpx;
  max-height: 80vh;
}

.result-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20rpx;
}

.result-title {
  font-size: 32rpx;
  font-weight: 600;
  color: var(--gzang-primary);
}

.result-close {
  width: 48rpx;
  height: 48rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.result-preview {
  height: 300rpx;
  background-color: #f5f5f5;
  border-radius: 16rpx;
  overflow: hidden;
  margin-bottom: 20rpx;
}

.preview-image {
  width: 100%;
  height: 100%;
}

.result-form {
  display: flex;
  flex-direction: column;
  gap: 16rpx;
  margin-bottom: 24rpx;
}

.form-row {
  display: flex;
  align-items: center;
  gap: 16rpx;
  padding: 16rpx;
  background-color: #f8f8f8;
  border-radius: 12rpx;
}

.row-label {
  font-size: 28rpx;
  color: var(--gzang-text-secondary);
  min-width: 140rpx;
}

.row-input {
  flex: 1;
  font-size: 28rpx;
  color: var(--gzang-text-primary);
  background: transparent;
}

.result-actions {
  display: flex;
  gap: 16rpx;

  .btn {
    flex: 1;
  }
}

.loading-state {
  padding: 80rpx;
  display: flex;
  justify-content: center;
}
</style>
