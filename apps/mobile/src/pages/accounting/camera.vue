<template>
  <view class="camera-page">
    <!-- 顶部导航 -->
    <view class="nav-bar">
      <view class="nav-left" @click="goBack">
        <uni-icons type="left" size="20" color="#fff"></uni-icons>
      </view>
      <view class="nav-title">拍照记账</view>
      <view class="nav-right"></view>
    </view>

    <!-- 相机区域 -->
    <view class="camera-section" :class="{ 'has-preview': capturedImage }">
      <!-- 相机预览 -->
      <view v-if="!capturedImage" class="camera-preview">
        <camera
          v-if="cameraAvailable"
          class="camera"
          :device-position="cameraFacing"
          flash="auto"
          @error="onCameraError"
        ></camera>
        <view v-else class="camera-placeholder">
          <text class="placeholder-icon">📷</text>
          <text class="placeholder-text">相机不可用</text>
          <text class="placeholder-hint">请在手机端使用此功能</text>
        </view>

        <!-- 拍摄框引导 -->
        <view class="scan-frame">
          <view class="frame-corner top-left"></view>
          <view class="frame-corner top-right"></view>
          <view class="frame-corner bottom-left"></view>
          <view class="frame-corner bottom-right"></view>
        </view>

        <!-- 提示文字 -->
        <view class="scan-hint">
          <text>将小票/发票放入框内</text>
        </view>

        <!-- 切换摄像头按钮 -->
        <view class="camera-controls">
          <view class="control-btn" @click="toggleCamera">
            <text>🔄</text>
          </view>
        </view>
      </view>

      <!-- 预览已拍摄图片 -->
      <view v-else class="preview-section">
        <image
          class="captured-image"
          :src="capturedImage"
          mode="aspectFit"
        ></image>

        <!-- 重新拍摄按钮 -->
        <view class="retake-btn" @click="retakePhoto">
          <text>🔄 重新拍摄</text>
        </view>
      </view>
    </view>

    <!-- OCR 结果区域 -->
    <view class="result-section">
      <!-- 处理中状态 -->
      <view v-if="isProcessing" class="processing-card">
        <view class="processing-content">
          <view class="spinner-large"></view>
          <text class="processing-text">正在识别中...</text>
          <text class="processing-sub">请稍候</text>
        </view>
      </view>

      <!-- OCR 结果卡片 -->
      <view v-else-if="ocrResult" class="ocr-result-card">
        <view class="result-header">
          <text class="result-title">识别结果</text>
          <text class="result-confidence">
            置信度: {{ Math.round(ocrResult.confidence * 100) }}%
          </text>
        </view>

        <!-- 原始文本 -->
        <view class="raw-text-section">
          <text class="section-label">原始文字</text>
          <view class="raw-text-box">
            <text class="raw-text">{{ ocrResult.rawText || '未识别到文字' }}</text>
          </view>
        </view>

        <!-- 解析出的信息 -->
        <view class="parsed-info-section">
          <text class="section-label">解析信息</text>

          <view class="info-grid">
            <!-- 金额 -->
            <view class="info-item" :class="{ empty: !ocrResult.amount }">
              <text class="info-icon">💰</text>
              <text class="info-label">金额</text>
              <view class="info-value-wrapper">
                <text v-if="ocrResult.amount" class="info-value amount">
                  {{ currentCurrencySymbol }}{{ ocrResult.amount.toFixed(2) }}
                </text>
                <text v-else class="info-value empty">-</text>
              </view>
            </view>

            <!-- 分类 -->
            <view class="info-item" :class="{ empty: !ocrResult.category }">
              <text class="info-icon">📂</text>
              <text class="info-label">分类</text>
              <view class="info-value-wrapper">
                <text v-if="ocrResult.category" class="info-value">
                  {{ ocrResult.category }}
                </text>
                <text v-else class="info-value empty">-</text>
              </view>
            </view>

            <!-- 商家 -->
            <view class="info-item" :class="{ empty: !ocrResult.merchant }">
              <text class="info-icon">🏪</text>
              <text class="info-label">商家</text>
              <view class="info-value-wrapper">
                <text v-if="ocrResult.merchant" class="info-value">
                  {{ ocrResult.merchant }}
                </text>
                <text v-else class="info-value empty">-</text>
              </view>
            </view>

            <!-- 日期 -->
            <view class="info-item" :class="{ empty: !ocrResult.date }">
              <text class="info-icon">📅</text>
              <text class="info-label">日期</text>
              <view class="info-value-wrapper">
                <text v-if="ocrResult.date" class="info-value">
                  {{ ocrResult.date }}
                </text>
                <text v-else class="info-value empty">-</text>
              </view>
            </view>
          </view>
        </view>

        <!-- 编辑选项 -->
        <view class="edit-section">
          <view class="edit-row" @click="showAmountEdit = true">
            <view class="edit-label">
              <text class="edit-icon">💰</text>
              <text>金额</text>
            </view>
            <view class="edit-value">
              <text class="amount-value">{{ currentCurrencySymbol }}{{ formData.amount.toFixed(2) }}</text>
              <text class="edit-arrow">›</text>
            </view>
          </view>

          <view class="edit-row" @click="showCategoryEdit = true">
            <view class="edit-label">
              <text class="edit-icon">📂</text>
              <text>分类</text>
            </view>
            <view class="edit-value">
              <text>{{ formData.categoryName || '选择分类' }}</text>
              <text class="edit-arrow">›</text>
            </view>
          </view>

          <view class="edit-row" @click="showAccountEdit = true">
            <view class="edit-label">
              <text class="edit-icon">💳</text>
              <text>账户</text>
            </view>
            <view class="edit-value">
              <text>{{ formData.accountName || '选择账户' }}</text>
              <text class="edit-arrow">›</text>
            </view>
          </view>

          <view class="edit-row" @click="showDateEdit = true">
            <view class="edit-label">
              <text class="edit-icon">📅</text>
              <text>日期</text>
            </view>
            <view class="edit-value">
              <text>{{ formatDisplayDate }}</text>
              <text class="edit-arrow">›</text>
            </view>
          </view>
        </view>

        <!-- 备注 -->
        <view class="remark-section">
          <textarea
            v-model="formData.remark"
            placeholder="添加备注（可选）"
            class="remark-input"
            :maxlength="200"
          ></textarea>
        </view>
      </view>

      <!-- 空状态 -->
      <view v-else class="empty-state">
        <text class="empty-icon">🧾</text>
        <text class="empty-title">拍摄小票自动识别</text>
        <text class="empty-desc">
          支持识别发票、小票、收据等，自动提取金额、商家和日期信息
        </text>
      </view>
    </view>

    <!-- 底部操作栏 -->
    <view class="action-bar" v-if="capturedImage || ocrResult">
      <view class="action-hint" v-if="!ocrResult">
        <text>点击识别按钮开始 OCR 识别</text>
      </view>

      <view class="action-buttons">
        <button
          v-if="!ocrResult"
          class="action-btn primary"
          :class="{ loading: isProcessing }"
          :disabled="!capturedImage || isProcessing"
          @click="startOCR"
        >
          <text v-if="isProcessing">识别中...</text>
          <text v-else>🔍 开始识别</text>
        </button>

        <button
          v-if="ocrResult"
          class="action-btn secondary"
          @click="resetForm"
        >
          <text>重置</text>
        </button>

        <button
          v-if="ocrResult"
          class="action-btn primary"
          :disabled="!canSubmit || submitting"
          @click="handleSubmit"
        >
          <text v-if="submitting">保存中...</text>
          <text v-else>✅ 确认记账</text>
        </button>
      </view>
    </view>

    <!-- 金额编辑弹窗 -->
    <uni-popup ref="amountPopup" type="bottom" :is-mask-click="true">
      <view class="edit-popup">
        <view class="popup-header">
          <text class="popup-cancel" @click="closeAmountPopup">取消</text>
          <text class="popup-title">修改金额</text>
          <text class="popup-confirm" @click="confirmAmount">确定</text>
        </view>
        <view class="amount-edit-content">
          <view class="amount-edit-row">
            <text class="amount-currency">{{ currentCurrencySymbol }}</text>
            <input
              v-model="tempAmount"
              type="digit"
              placeholder="0.00"
              class="amount-edit-input"
            />
          </view>
        </view>
      </view>
    </uni-popup>

    <!-- 分类选择弹窗 -->
    <uni-popup ref="categoryPopup" type="bottom" :is-mask-click="true">
      <view class="edit-popup">
        <view class="popup-header">
          <text class="popup-cancel" @click="closeCategoryPopup">取消</text>
          <text class="popup-title">选择分类</text>
          <text class="popup-confirm" @click="confirmCategory">确定</text>
        </view>
        <picker-view
          :value="categoryPickerValue"
          @change="onCategoryChange"
          class="category-picker"
        >
          <picker-view-column>
            <view v-for="cat in allCategories" :key="cat.id" class="picker-item">
              {{ cat.icon || '📂' }} {{ cat.name }}
            </view>
          </picker-view-column>
        </picker-view>
      </view>
    </uni-popup>

    <!-- 账户选择弹窗 -->
    <uni-popup ref="accountPopup" type="bottom" :is-mask-click="true">
      <view class="edit-popup">
        <view class="popup-header">
          <text class="popup-cancel" @click="closeAccountPopup">取消</text>
          <text class="popup-title">选择账户</text>
          <text class="popup-confirm" @click="confirmAccount">确定</text>
        </view>
        <picker-view
          :value="accountPickerValue"
          @change="onAccountChange"
          class="account-picker"
        >
          <picker-view-column>
            <view v-for="account in accounts" :key="account.id" class="picker-item">
              {{ getAccountIcon(account) }} {{ account.name }}
            </view>
          </picker-view-column>
        </picker-view>
      </view>
    </uni-popup>

    <!-- 日期选择弹窗 -->
    <uni-popup ref="datePopup" type="bottom" :is-mask-click="true">
      <view class="edit-popup">
        <view class="popup-header">
          <text class="popup-cancel" @click="closeDatePopup">取消</text>
          <text class="popup-title">选择日期</text>
          <text class="popup-confirm" @click="confirmDate">确定</text>
        </view>
        <picker-view
          :value="datePickerValue"
          @change="onDateChange"
          class="date-picker"
        >
          <picker-view-column>
            <view v-for="year in years" :key="year" class="picker-item">
              {{ year }}年
            </view>
          </picker-view-column>
          <picker-view-column>
            <view v-for="month in months" :key="month" class="picker-item">
              {{ month }}月
            </view>
          </picker-view-column>
          <picker-view-column>
            <view v-for="day in days" :key="day" class="picker-item">
              {{ day }}日
            </view>
          </picker-view-column>
        </picker-view>
      </view>
    </uni-popup>
  </view>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted, onUnmounted } from 'vue'
import { useOCR, type OCRResult } from '@/composables/useOCR'
import { useBookStore } from '@/stores/book'
import { useTransactionStore } from '@/stores/transaction'
import { useCategoryStore } from '@/stores/category'
import { useAccountStore } from '@/stores/account'
import { formatDate as formatDateStr } from '@/utils/date'

// Store
const bookStore = useBookStore()
const transactionStore = useTransactionStore()
const categoryStore = useCategoryStore()
const accountStore = useAccountStore()

// OCR composable
const { isProcessing, lastResult, recognizeImage } = useOCR()

// 响应式数据
const cameraAvailable = ref(false)
const cameraFacing = ref<'front' | 'back'>('back')
const capturedImage = ref('')
const ocrResult = ref<OCRResult | null>(null)
const submitting = ref(false)

const formData = reactive({
  amount: 0,
  categoryId: 0,
  categoryName: '',
  accountId: 0,
  accountName: '',
  date: formatDateStr(new Date()),
  remark: ''
})

const tempAmount = ref('')

// 弹窗 refs
const amountPopup = ref<any>(null)
const categoryPopup = ref<any>(null)
const accountPopup = ref<any>(null)
const datePopup = ref<any>(null)

// 选择器数据
const categoryPickerValue = ref([0])
const accountPickerValue = ref([0])
const datePickerValue = ref([0, 0, 0])

// 日期选择器数据
const years = computed(() => {
  const current = new Date().getFullYear()
  return Array.from({ length: 10 }, (_, i) => current - 5 + i)
})
const months = computed(() => Array.from({ length: 12 }, (_, i) => i + 1))
const days = computed(() => {
  const year = years.value[datePickerValue.value[0]] || new Date().getFullYear()
  const month = months.value[datePickerValue.value[1]] || new Date().getMonth() + 1
  return Array.from({ length: getDaysInMonth(year, month) }, (_, i) => i + 1)
})

// 计算属性
const currentCurrencySymbol = computed(() => bookStore.currentCurrencySymbol)
const accounts = computed(() => accountStore.accountList)
const allCategories = computed(() => categoryStore.categoryList)

const canSubmit = computed(() => {
  return formData.amount > 0 && formData.categoryId > 0 && formData.accountId > 0
})

const formatDisplayDate = computed(() => {
  const today = formatDateStr(new Date())
  const yesterday = formatDateStr(new Date(Date.now() - 86400000))
  if (formData.date === today) return '今天'
  if (formData.date === yesterday) return '昨天'
  return formData.date.replace(/-/g, '/')
})

// 方法

const getDaysInMonth = (year: number, month: number) => {
  return new Date(year, month, 0).getDate()
}

const getAccountIcon = (account: any) => {
  const typeIcons: Record<string, string> = {
    CASH: '💵',
    BANK_CARD: '🏦',
    CREDIT_CARD: '💳',
    E_WALLET: '📱',
    INVESTMENT: '📈',
    DEBT: '📋',
    OTHER: '💰',
  }
  return typeIcons[account.type] || '💰'
}

// 相机操作
const toggleCamera = () => {
  cameraFacing.value = cameraFacing.value === 'back' ? 'front' : 'back'
}

const onCameraError = (e: any) => {
  cameraAvailable.value = false
}

const retakePhoto = () => {
  capturedImage.value = ''
  ocrResult.value = null
}

// OCR 识别
const startOCR = async () => {
  if (!capturedImage.value) return

  try {
    const result = await recognizeImage(capturedImage.value)

    if (result) {
      ocrResult.value = result

      // 填充表单数据
      formData.amount = result.amount || 0

      if (result.category) {
        const category = allCategories.value.find(c => c.name === result.category)
        if (category) {
          formData.categoryId = category.id
          formData.categoryName = category.name || ''
        }
      }

      // 设置默认账户
      if (accounts.value.length > 0) {
        formData.accountId = accounts.value[0].id
        formData.accountName = accounts.value[0].name || ''
      }
    } else {
      uni.showToast({
        title: '未识别到有效信息',
        icon: 'none'
      })
    }
  } catch (error) {
    uni.showToast({
      title: '识别失败，请重试',
      icon: 'none'
    })
  }
}

// 金额编辑
const showAmountEdit = computed(() => false)
const showCategoryEdit = computed(() => false)
const showAccountEdit = computed(() => false)
const showDateEdit = computed(() => false)

const closeAmountPopup = () => {
  amountPopup.value.close()
}

const confirmAmount = () => {
  const amount = parseFloat(tempAmount.value)
  if (!isNaN(amount) && amount > 0) {
    formData.amount = amount
  }
  closeAmountPopup()
}

// 分类选择
const closeCategoryPopup = () => {
  categoryPopup.value.close()
}

const onCategoryChange = (e: any) => {
  categoryPickerValue.value = e.detail.value
}

const confirmCategory = () => {
  const index = categoryPickerValue.value[0]
  const category = allCategories.value[index]
  if (category) {
    formData.categoryId = category.id
    formData.categoryName = category.name || ''
  }
  closeCategoryPopup()
}

// 账户选择
const closeAccountPopup = () => {
  accountPopup.value.close()
}

const onAccountChange = (e: any) => {
  accountPickerValue.value = e.detail.value
}

const confirmAccount = () => {
  const index = accountPickerValue.value[0]
  const account = accounts.value[index]
  if (account) {
    formData.accountId = account.id
    formData.accountName = account.name || ''
  }
  closeAccountPopup()
}

// 日期选择
const showDatePicker = () => {
  const date = formData.date ? new Date(formData.date) : new Date()
  const yearIndex = years.value.indexOf(date.getFullYear())
  const monthIndex = date.getMonth()
  const dayIndex = date.getDate() - 1
  datePickerValue.value = [
    yearIndex >= 0 ? yearIndex : 0,
    monthIndex,
    dayIndex >= 0 ? dayIndex : 0
  ]
  datePopup.value.open()
}

const onDateChange = (e: any) => {
  datePickerValue.value = e.detail.value
}

const confirmDate = () => {
  const year = years.value[datePickerValue.value[0]] || new Date().getFullYear()
  const month = months.value[datePickerValue.value[1]] || 1
  const day = days.value[datePickerValue.value[2]] || 1
  formData.date = `${year}-${String(month).padStart(2, '0')}-${String(day).padStart(2, '0')}`
  closeDatePopup()
}

const closeDatePopup = () => {
  datePopup.value.close()
}

// 提交表单
const handleSubmit = async () => {
  if (!canSubmit.value) {
    uni.showToast({
      title: '请完善记账信息',
      icon: 'none'
    })
    return
  }

  try {
    submitting.value = true

    await transactionStore.createTransaction({
      type: 2, // 支出
      amount: formData.amount,
      categoryId: formData.categoryId,
      accountId: formData.accountId,
      transactionTime: `${formData.date} ${new Date().toTimeString().slice(0, 8)}`,
      remark: formData.remark || `拍照记账: ${ocrResult.value?.merchant || ''}`
    })

    uni.showToast({
      title: '记账成功',
      icon: 'success'
    })

    setTimeout(() => {
      uni.navigateBack()
    }, 1500)

  } catch (error: any) {
    // ignore
  } finally {
    submitting.value = false
  }
}

// 重置表单
const resetForm = () => {
  capturedImage.value = ''
  ocrResult.value = null
  formData.amount = 0
  formData.categoryId = 0
  formData.categoryName = ''
  formData.accountId = 0
  formData.accountName = ''
  formData.date = formatDateStr(new Date())
  formData.remark = ''
}

// 返回
const goBack = () => {
  uni.navigateBack()
}

// 生命周期
onMounted(async () => {
  // 检查相机是否可用
  try {
    const cameraInfo = uni.createCameraContext()
    cameraAvailable.value = !!cameraInfo
  } catch (e) {
    cameraAvailable.value = false
  }

  // 加载数据
  await Promise.all([
    bookStore.fetchBooks(),
    categoryStore.fetchCategories(),
    accountStore.fetchAccounts()
  ])

  // 设置默认账户
  if (accounts.value.length > 0) {
    formData.accountId = accounts.value[0].id
    formData.accountName = accounts.value[0].name || ''
  }
})
</script>

<style lang="scss" scoped>
.camera-page {
  height: 100vh;
  background-color: #111;
  display: flex;
  flex-direction: column;
}

.nav-bar {
  background-color: rgba(0, 0, 0, 0.8);
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  padding-top: calc(12px + env(safe-area-inset-top));
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  z-index: 10;
}

.nav-left, .nav-right {
  width: 80px;
  display: flex;
  align-items: center;
}

.nav-left {
  justify-content: flex-start;
}

.nav-right {
  justify-content: flex-end;
}

.nav-title {
  font-size: 18px;
  font-weight: 600;
  color: #fff;
}

// 相机区域
.camera-section {
  flex: 0 0 45vh;
  position: relative;

  &.has-preview {
    flex: 0 0 auto;
    height: 280px;
  }
}

.camera-preview {
  width: 100%;
  height: 100%;
  position: relative;
  background: #000;
}

.camera {
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
  background: #1a1a1a;

  .placeholder-icon {
    font-size: 60px;
    margin-bottom: 16px;
  }

  .placeholder-text {
    font-size: 16px;
    color: #fff;
    margin-bottom: 8px;
  }

  .placeholder-hint {
    font-size: 12px;
    color: rgba(255, 255, 255, 0.5);
  }
}

// 扫描框
.scan-frame {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 280px;
  height: 200px;
  pointer-events: none;
}

.frame-corner {
  position: absolute;
  width: 24px;
  height: 24px;
  border-color: #FB8B24;
  border-style: solid;
  border-width: 0;

  &.top-left {
    top: 0;
    left: 0;
    border-top-width: 3px;
    border-left-width: 3px;
    border-top-left-radius: 8px;
  }

  &.top-right {
    top: 0;
    right: 0;
    border-top-width: 3px;
    border-right-width: 3px;
    border-top-right-radius: 8px;
  }

  &.bottom-left {
    bottom: 0;
    left: 0;
    border-bottom-width: 3px;
    border-left-width: 3px;
    border-bottom-left-radius: 8px;
  }

  &.bottom-right {
    bottom: 0;
    right: 0;
    border-bottom-width: 3px;
    border-right-width: 3px;
    border-bottom-right-radius: 8px;
  }
}

.scan-hint {
  position: absolute;
  bottom: 40px;
  left: 50%;
  transform: translateX(-50%);
  background: rgba(0, 0, 0, 0.6);
  padding: 8px 16px;
  border-radius: 20px;

  text {
    font-size: 12px;
    color: #fff;
  }
}

.camera-controls {
  position: absolute;
  bottom: 20px;
  left: 0;
  right: 0;
  display: flex;
  justify-content: center;
  gap: 20px;
}

.control-btn {
  width: 44px;
  height: 44px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.2);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;

  &:active {
    background: rgba(255, 255, 255, 0.3);
  }
}

// 预览区域
.preview-section {
  width: 100%;
  height: 100%;
  position: relative;
  background: #000;
}

.captured-image {
  width: 100%;
  height: 100%;
}

.retake-btn {
  position: absolute;
  bottom: 20px;
  left: 50%;
  transform: translateX(-50%);
  background: rgba(0, 0, 0, 0.6);
  padding: 10px 20px;
  border-radius: 24px;
  display: flex;
  align-items: center;
  gap: 6px;

  text {
    font-size: 13px;
    color: #fff;
  }
}

// 结果区域
.result-section {
  flex: 1;
  background: #F8F9FA;
  border-radius: 24px 24px 0 0;
  margin-top: -24px;
  padding: 20px 16px;
  overflow-y: auto;
}

// 处理中状态
.processing-card {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
}

.processing-content {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.spinner-large {
  width: 48px;
  height: 48px;
  border: 4px solid #E5E5E5;
  border-top-color: #0F4C5C;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
  margin-bottom: 16px;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

.processing-text {
  font-size: 16px;
  color: #333;
  font-weight: 500;
}

.processing-sub {
  font-size: 12px;
  color: #999;
  margin-top: 4px;
}

// OCR 结果卡片
.ocr-result-card {
  background: #fff;
  border-radius: 16px;
  padding: 16px;
}

.result-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.result-title {
  font-size: 16px;
  font-weight: 600;
  color: #333;
}

.result-confidence {
  font-size: 12px;
  color: #06D6A0;
}

.raw-text-section {
  margin-bottom: 16px;
}

.section-label {
  font-size: 12px;
  color: #9CA3AF;
  margin-bottom: 8px;
  display: block;
}

.raw-text-box {
  background: #F8F9FA;
  border-radius: 8px;
  padding: 12px;
}

.raw-text {
  font-size: 13px;
  color: #666;
  line-height: 1.5;
  word-break: break-all;
}

.parsed-info-section {
  margin-bottom: 16px;
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
}

.info-item {
  background: #F8F9FA;
  border-radius: 12px;
  padding: 12px;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;

  &.empty {
    opacity: 0.5;
  }
}

.info-icon {
  font-size: 20px;
}

.info-label {
  font-size: 11px;
  color: #999;
}

.info-value-wrapper {
  display: flex;
  align-items: center;
}

.info-value {
  font-size: 14px;
  font-weight: 600;
  color: #333;

  &.amount {
    color: #EF476F;
    font-size: 16px;
  }

  &.empty {
    color: #ccc;
  }
}

// 编辑区域
.edit-section {
  margin-bottom: 16px;
}

.edit-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 14px 0;
  border-bottom: 1px solid #F3F4F6;

  &:last-child {
    border-bottom: none;
  }
}

.edit-label {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  color: #333;
}

.edit-icon {
  font-size: 16px;
}

.edit-value {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
  color: #666;
}

.amount-value {
  font-weight: 600;
  color: #EF476F;
}

.edit-arrow {
  color: #D1D5DB;
  font-size: 16px;
}

// 备注
.remark-section {
  margin-top: 12px;
}

.remark-input {
  width: 100%;
  min-height: 60px;
  background: #F8F9FA;
  border-radius: 10px;
  padding: 12px;
  font-size: 14px;
  color: #333;
  border: none;
  outline: none;
  resize: none;
}

// 空状态
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
  text-align: center;
}

.empty-icon {
  font-size: 60px;
  margin-bottom: 16px;
}

.empty-title {
  font-size: 18px;
  font-weight: 600;
  color: #333;
  margin-bottom: 8px;
}

.empty-desc {
  font-size: 13px;
  color: #9CA3AF;
  line-height: 1.5;
}

// 底部操作栏
.action-bar {
  background: #fff;
  padding: 16px;
  padding-bottom: calc(16px + env(safe-area-inset-bottom));
  border-top: 1px solid #E5E5E5;
}

.action-hint {
  text-align: center;
  font-size: 12px;
  color: #9CA3AF;
  margin-bottom: 12px;
}

.action-buttons {
  display: flex;
  gap: 12px;
}

.action-btn {
  flex: 1;
  height: 48px;
  border-radius: 12px;
  font-size: 15px;
  font-weight: 600;
  display: flex;
  align-items: center;
  justify-content: center;
  border: none;

  &.secondary {
    background: #F8F9FA;
    color: #666;
  }

  &.primary {
    background: #0F4C5C;
    color: #fff;
  }

  &:disabled {
    background: #ccc;
    color: #fff;
  }
}

// 编辑弹窗
.edit-popup {
  background: #fff;
  border-radius: 16px 16px 0 0;
  overflow: hidden;
}

.popup-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  border-bottom: 1px solid #E5E5E5;
}

.popup-cancel {
  font-size: 15px;
  color: #999;
}

.popup-title {
  font-size: 16px;
  font-weight: 600;
  color: #333;
}

.popup-confirm {
  font-size: 15px;
  color: #0F4C5C;
  font-weight: 500;
}

.amount-edit-content {
  padding: 20px;
}

.amount-edit-row {
  display: flex;
  align-items: center;
  background: #F8F9FA;
  border-radius: 12px;
  padding: 16px;
}

.amount-currency {
  font-size: 24px;
  font-weight: 600;
  color: #0F4C5C;
  margin-right: 8px;
}

.amount-edit-input {
  flex: 1;
  font-size: 28px;
  font-weight: 600;
  color: #333;
  background: transparent;
  border: none;
  outline: none;
}

.category-picker, .account-picker, .date-picker {
  height: 200px;
}

.picker-item {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 44px;
  font-size: 15px;
  color: #333;
}
</style>
