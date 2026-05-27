<template>
  <!-- 自定义组件 -->
  <CustomToast />
  <CustomModal />
  <CustomLoading />

  <view class="voice-page">
    <!-- 自定义导航栏 -->
    <view class="nav-bar">
      <view class="back-btn" @click="goBack">
        <text class="back-icon">←</text>
      </view>
      <view class="nav-title">语音记账</view>
      <view class="nav-right"></view>
    </view>

    <!-- 波形动画区域 -->
    <view class="waveform-area">
      <view
        v-for="(bar, index) in waveformBars"
        :key="index"
        class="wave-bar"
        :style="{
          height: recording ? bar.currentHeight + 'px' : '8px',
          animationDelay: (index * 0.05) + 's',
          background: recording ? 'rgba(255, 255, 255, 0.7)' : 'rgba(255, 255, 255, 0.4)'
        }"
      ></view>
    </view>

    <!-- 麦克风按钮 -->
    <view class="mic-container">
      <view
        class="mic-btn"
        :class="{ recording: recording }"
        @click="toggleRecording"
      >
        <text class="mic-icon">{{ recording ? '⏹' : '🎤' }}</text>
      </view>
      <view class="mic-hint" v-if="recording">
        <text class="recording-time">{{ formatDuration(recordingDuration) }}</text>
      </view>
    </view>

    <!-- 提示文字 -->
    <view class="voice-hint">
      <text v-if="!recording && !resultText">点击麦克风，说出你的消费</text>
      <text v-else-if="recording">正在聆听...</text>
      <text v-else>{{ resultText }}</text>
    </view>

    <!-- 识别结果卡片 -->
    <view class="result-card" v-if="recognizedText || recording">
      <view class="rc-label">识别结果</view>
      <view class="rc-text" :class="{ placeholder: !recognizedText && recording }">
        {{ recognizedText || '正在聆听...' }}
      </view>
    </view>

    <!-- 快捷确认卡片 -->
    <view class="result-card" v-if="recognizedText || resultCategory">
      <view class="rc-label">快捷确认</view>
      <view class="rc-text">
        {{ resultCategory || '餐饮' }} ·
        <text style="color: #EF476F; font-weight: 600">
          ¥ {{ resultAmount > 0 ? resultAmount.toFixed(2) : '0.00' }}
        </text>
      </view>
    </view>

    <!-- 快捷金额选择 -->
    <view class="quick-amounts" v-if="recognizedText">
      <view
        v-for="amount in quickAmounts"
        :key="amount"
        class="qa-btn"
        :class="{ active: selectedQuickAmount === amount }"
        @click="selectQuickAmount(amount)"
      >
        ¥{{ amount }}
      </view>
    </view>

    <!-- 分类选择 -->
    <view class="category-section" v-if="recognizedText">
      <view class="section-title">选择分类</view>
      <view class="category-grid">
        <view
          v-for="cat in categories"
          :key="cat.id"
          class="cat-item"
          :class="{ active: selectedCategory?.id === cat.id }"
          @click="selectCategory(cat)"
        >
          <view class="cat-icon" :style="{ background: cat.bgColor }">
            <text>{{ cat.icon }}</text>
          </view>
          <view class="cat-name">{{ cat.name }}</view>
        </view>
      </view>
    </view>

    <!-- 确认按钮 -->
    <view class="submit-bar" v-if="recognizedText">
      <view class="submit-btn" :class="{ loading: submitting }" @click="confirmTransaction">
        <text v-if="submitting">保存中...</text>
        <text v-else>确认记账</text>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useAppStore } from '@/stores/app'
import { useVoiceRecord } from '@/composables/useVoiceRecord'
import { useTransactionStore } from '@/stores/transaction'
import { useBookStore } from '@/stores/book'
import { useCategoryStore } from '@/stores/category'
import { uploadVoice, type VoiceIntentResult } from '@/api/voice'
import CustomToast from '@/components/common/CustomToast/index.vue'
import CustomModal from '@/components/common/CustomModal/index.vue'
import CustomLoading from '@/components/common/CustomLoading/index.vue'

// 状态管理
const appStore = useAppStore()
const transactionStore = useTransactionStore()
const bookStore = useBookStore()
const categoryStore = useCategoryStore()
const { startRecording, stopRecording, recordResult } = useVoiceRecord()

// 录音状态
const recording = ref(false)
const recordingDuration = ref(0)
let recordingTimer: ReturnType<typeof setInterval> | null = null

// 识别状态
const recognizedText = ref('')
const resultText = computed(() => recognizedText.value || '正在聆听...')
const resultCategory = ref('')
const resultAmount = ref(0)
const submitting = ref(false)

// 解析后的语音结果
const voiceResult = ref<VoiceIntentResult | null>(null)

// 快捷金额
const quickAmounts = [10, 50, 100, 200, 500]
const selectedQuickAmount = ref<number | null>(null)

// 分类列表
const categories = [
  { id: 1, name: '餐饮', icon: '🍜', bgColor: '#FFF3E0', keywords: ['吃饭', '餐饮', '午餐', '晚餐', '早餐', '外卖'] },
  { id: 2, name: '交通', icon: '🚇', bgColor: '#E3F2FD', keywords: ['交通', '打车', '地铁', '公交', '加油'] },
  { id: 3, name: '购物', icon: '🛒', bgColor: '#FCE4EC', keywords: ['购物', '网购', '淘宝', '京东'] },
  { id: 4, name: '娱乐', icon: '🎮', bgColor: '#F3E5F5', keywords: ['娱乐', '游戏', '电影', 'K歌'] },
  { id: 5, name: '医疗', icon: '💊', bgColor: '#FFEBEE', keywords: ['医疗', '买药', '医院'] },
  { id: 6, name: '教育', icon: '📚', bgColor: '#E8F5E9', keywords: ['教育', '学费', '培训'] },
  { id: 7, name: '居住', icon: '🏠', bgColor: '#FFF8E1', keywords: ['房租', '水电', '物业', '居住'] },
  { id: 8, name: '通讯', icon: '📱', bgColor: '#E0F7FA', keywords: ['通讯', '话费', '流量'] }
]
const selectedCategory = ref<typeof categories[0] | null>(null)

// 波形条
const waveformBars = ref(
  Array.from({ length: 25 }, () => ({
    baseHeight: Math.random() * 20 + 10,
    currentHeight: 8
  }))
)

// 波形动画
let waveformAnimation: ReturnType<typeof setInterval> | null = null

// 格式化时长
const formatDuration = (seconds: number) => {
  const mins = Math.floor(seconds / 60)
  const secs = seconds % 60
  return `${mins.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`
}

// 开始/停止录音
const toggleRecording = async () => {
  if (recording.value) {
    // 停止录音
    recording.value = false
    if (recordingTimer) {
      clearInterval(recordingTimer)
      recordingTimer = null
    }
    if (waveformAnimation) {
      clearInterval(waveformAnimation)
      waveformAnimation = null
    }

    // 调用语音识别 API
    const recordRes = await stopRecording()
    recognizedText.value = '正在识别...'
    try {
      if (recordRes?.filePath) {
        // 上传语音文件进行识别
        await recognizeVoice(recordRes.filePath)
      } else {
        recognizedText.value = ''
        appStore.showWarning('未获取到录音，请重试')
      }
    } catch (error) {
      recognizedText.value = ''
      appStore.showError('识别失败，请重试')
    }
  } else {
    // 开始录音
    try {
      await startRecording()
      recording.value = true
      recognizedText.value = ''
      voiceResult.value = null
      recordingDuration.value = 0
      // 开始计时
      recordingTimer = setInterval(() => {
        recordingDuration.value++
      }, 1000)
      // 开始波形动画
      waveformAnimation = setInterval(() => {
        waveformBars.value = waveformBars.value.map(bar => ({
          ...bar,
          currentHeight: bar.baseHeight + Math.random() * 20
        }))
      }, 100)
    } catch (error) {
      appStore.showError('无法访问麦克风，请检查权限设置')
    }
  }
}

// 语音识别（调用后端 API）
const recognizeVoice = async (filePath: string) => {
  try {
    // 上传语音文件到后端进行识别
    const result: VoiceIntentResult = await uploadVoice(filePath, 'voice.mp3')
    voiceResult.value = result

    if (result.success) {
      recognizedText.value = result.originalText

      if (result.amount !== null) {
        resultAmount.value = result.amount
      }

      if (result.categoryName) {
        resultCategory.value = result.categoryName
        selectedCategory.value = categories.find(c => c.name === result.categoryName) || null
      }
    } else {
      recognizedText.value = result.originalText || '未识别到有效信息'
      appStore.showWarning(result.errorMessage || '未能识别记账意图，请手动输入')
    }
  } catch (error) {
    recognizedText.value = '识别服务暂不可用'
    appStore.showError('语音识别失败，请重试')
  }
}

// 选择快捷金额
const selectQuickAmount = (amount: number) => {
  selectedQuickAmount.value = amount
  resultAmount.value = amount
}

// 选择分类
const selectCategory = (cat: typeof categories[0]) => {
  selectedCategory.value = cat
  resultCategory.value = cat.name
}

// 确认记账
const confirmTransaction = async () => {
  if (!resultAmount.value || resultAmount.value <= 0) {
    appStore.showError('请输入正确的金额')
    return
  }

  if (!selectedCategory.value) {
    appStore.showError('请选择分类')
    return
  }

  submitting.value = true
  try {
    await transactionStore.create({
      type: 2, // 支出
      amount: resultAmount.value,
      categoryId: selectedCategory.value.id,
      accountId: 1, // 默认账户，后续优化
      transactionTime: new Date().toISOString().replace('T', ' ').slice(0, 19),
      remark: `语音记账: ${recognizedText.value}`
    })

    appStore.showSuccess('记账成功')

    // 重置状态
    resetState()

    // 返回上一页
    setTimeout(() => {
      uni.navigateBack()
    }, 1000)
  } catch (error) {
    appStore.showError('记账失败，请重试')
  } finally {
    submitting.value = false
  }
}

// 重置状态
const resetState = () => {
  recognizedText.value = ''
  resultAmount.value = 0
  selectedCategory.value = null
  selectedQuickAmount.value = null
  recordingDuration.value = 0
  voiceResult.value = null
  waveformBars.value = Array.from({ length: 25 }, () => ({
    baseHeight: Math.random() * 20 + 10,
    currentHeight: 8
  }))
}

// 返回上一页
const goBack = () => {
  if (recording.value) {
    stopRecording()
    recording.value = false
  }
  uni.navigateBack()
}

// 生命周期
onMounted(async () => {
  // 初始化波形
  waveformBars.value = Array.from({ length: 25 }, (_, i) => ({
    baseHeight: 12 + Math.sin(i * 0.5) * 8,
    currentHeight: 8
  }))

  // 预加载分类数据
  await categoryStore.fetchCategories()
  const cats = categoryStore.expenseCategories
  if (cats.length > 0) {
    categories.splice(0, categories.length, ...cats.map(c => ({
      id: c.id,
      name: c.categoryName || c.name || '未分类',
      icon: c.icon || '📂',
      bgColor: '#F8F9FA',
      keywords: []
    })))
  }
})

onUnmounted(() => {
  // 清理定时器
  if (recordingTimer) {
    clearInterval(recordingTimer)
  }
  if (waveformAnimation) {
    clearInterval(waveformAnimation)
  }
  if (recording.value) {
    stopRecording()
  }
})
</script>

<style lang="scss" scoped>
.voice-page {
  min-height: 100vh;
  background: linear-gradient(180deg, #0F4C5C 0%, #073B4C 100%);
  display: flex;
  flex-direction: column;
  padding-bottom: 120px;
}

.nav-bar {
  height: 88px;
  display: flex;
  align-items: flex-end;
  padding: 0 16px 12px;
  position: relative;
}

.back-btn {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.15);
  display: flex;
  align-items: center;
  justify-content: center;
  position: absolute;
  left: 16px;
  bottom: 8px;

  &:active {
    background: rgba(255, 255, 255, 0.25);
  }
}

.back-icon {
  font-size: 18px;
  color: #fff;
}

.nav-title {
  font-size: 18px;
  font-weight: 600;
  color: #fff;
  position: absolute;
  left: 50%;
  transform: translateX(-50%);
}

.nav-right {
  flex: 1;
}

// 波形动画区域
.waveform-area {
  height: 140px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 4px;
  padding: 20px 40px;
}

.wave-bar {
  width: 4px;
  border-radius: 2px;
  animation: wave 1s ease-in-out infinite;
  transition: height 0.1s ease;
}

@keyframes wave {
  0%, 100% {
    transform: scaleY(0.5);
  }
  50% {
    transform: scaleY(1.5);
  }
}

// 麦克风按钮
.mic-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin: 20px 0;
}

.mic-btn {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  background: #FB8B24;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 0 0 12px rgba(251, 139, 36, 0.2);
  transition: all 0.2s ease;

  &.recording {
    background: #EF476F;
    box-shadow: 0 0 0 16px rgba(239, 71, 111, 0.2);
    animation: pulse 1.5s ease-in-out infinite;
  }

  &:active {
    transform: scale(0.95);
  }
}

@keyframes pulse {
  0%, 100% {
    box-shadow: 0 0 0 12px rgba(239, 71, 111, 0.2);
  }
  50% {
    box-shadow: 0 0 0 24px rgba(239, 71, 111, 0.1);
  }
}

.mic-icon {
  font-size: 28px;
}

.mic-hint {
  margin-top: 12px;
}

.recording-time {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.7);
  font-family: 'Roboto Mono', 'JetBrains Mono', monospace;
}

// 提示文字
.voice-hint {
  text-align: center;
  color: rgba(255, 255, 255, 0.5);
  font-size: 13px;
  margin-bottom: 20px;
}

// 结果卡片
.result-card {
  margin: 0 16px 12px;
  background: #fff;
  border-radius: 16px;
  padding: 16px;
}

.rc-label {
  font-size: 12px;
  color: #9CA3AF;
  margin-bottom: 6px;
}

.rc-text {
  font-size: 16px;
  color: #1F2937;
  line-height: 1.5;

  &.placeholder {
    color: #ccc;
  }
}

// 快捷金额
.quick-amounts {
  display: flex;
  gap: 10px;
  padding: 0 16px;
  margin-bottom: 16px;
}

.qa-btn {
  flex: 1;
  padding: 10px 0;
  background: rgba(255, 255, 255, 0.15);
  border-radius: 10px;
  text-align: center;
  font-size: 13px;
  color: #fff;
  font-weight: 500;
  border: 1px solid transparent;
  transition: all 0.2s;

  &.active {
    background: rgba(255, 255, 255, 0.25);
    border-color: rgba(255, 255, 255, 0.5);
  }

  &:active {
    background: rgba(255, 255, 255, 0.25);
  }
}

// 分类选择
.category-section {
  padding: 0 16px;
}

.section-title {
  font-size: 13px;
  font-weight: 600;
  color: rgba(255, 255, 255, 0.7);
  margin-bottom: 12px;
}

.category-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 10px;
}

.cat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 10px 4px;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 12px;
  border: 1px solid transparent;
  transition: all 0.2s;

  &.active {
    background: rgba(255, 255, 255, 0.2);
    border-color: rgba(255, 255, 255, 0.5);
  }

  &:active {
    background: rgba(255, 255, 255, 0.15);
  }
}

.cat-icon {
  width: 38px;
  height: 38px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 16px;
  margin-bottom: 4px;
}

.cat-name {
  font-size: 10px;
  color: rgba(255, 255, 255, 0.8);
}

// 提交按钮
.submit-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 16px;
  background: linear-gradient(to top, #073B4C 60%, transparent);
  padding-top: 32px;
}

.submit-btn {
  height: 48px;
  background: #FB8B24;
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 15px;
  font-weight: 600;

  &:active {
    background: #e67a1a;
  }
}
</style>
