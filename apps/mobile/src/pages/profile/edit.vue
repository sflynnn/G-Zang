<template>
  <view class="edit-profile-page">
    <!-- 顶部导航 -->
    <view class="nav-bar">
      <view class="nav-left" @click="goBack">
        <uni-icons type="left" size="20" color="#333"></uni-icons>
      </view>
      <view class="nav-title">编辑资料</view>
      <view class="nav-right">
        <view class="save-btn" :class="{ disabled: saving }" @click="handleSave">
          <text>{{ saving ? '保存中...' : '保存' }}</text>
        </view>
      </view>
    </view>

    <scroll-view class="main-content" scroll-y="true">
      <!-- 头像区域 -->
      <view class="avatar-section">
        <view class="avatar-wrapper" @click="changeAvatar">
          <view class="avatar-preview" :style="{ backgroundColor: avatarBgColor }">
            <text class="avatar-text">{{ userInitials }}</text>
          </view>
          <view class="avatar-edit-icon">
            <uni-icons type="camera" size="14" color="#fff"></uni-icons>
          </view>
        </view>
        <text class="avatar-hint">点击更换头像</text>
      </view>

      <!-- 表单区域 -->
      <view class="form-section">
        <view class="form-item">
          <view class="form-label">昵称</view>
          <input
            v-model="form.nickname"
            class="form-input"
            placeholder="请输入昵称"
            maxlength="20"
          />
        </view>

        <view class="form-item">
          <view class="form-label">手机号</view>
          <view class="form-value">
            <text class="value-text">{{ form.phone || '未绑定' }}</text>
            <view class="bind-btn" @click="bindPhone">
              <text>{{ form.phone ? '更换' : '立即绑定' }}</text>
            </view>
          </view>
        </view>

        <view class="form-item">
          <view class="form-label">邮箱</view>
          <input
            v-model="form.email"
            class="form-input"
            placeholder="请输入邮箱（选填）"
            type="text"
          />
        </view>

        <view class="form-item">
          <view class="form-label">性别</view>
          <view class="gender-selector">
            <view
              v-for="item in genderOptions"
              :key="item.value"
              class="gender-option"
              :class="{ active: form.gender === item.value }"
              @click="form.gender = item.value"
            >
              <text class="gender-icon">{{ item.icon }}</text>
              <text class="gender-label">{{ item.label }}</text>
            </view>
          </view>
        </view>

        <view class="form-item">
          <view class="form-label">生日</view>
          <view class="date-picker" @click="showDatePicker">
            <text class="picker-text" :class="{ placeholder: !form.birthday }">
              {{ form.birthday || '请选择生日' }}
            </text>
            <uni-icons type="right" size="14" color="#ccc"></uni-icons>
          </view>
        </view>
      </view>

      <!-- 个人签名 -->
      <view class="form-section">
        <view class="section-title">个人签名</view>
        <textarea
          v-model="form.signature"
          class="signature-input"
          placeholder="介绍一下自己吧..."
          maxlength="100"
          auto-height
        />
        <view class="word-count">
          <text>{{ form.signature.length }}/100</text>
        </view>
      </view>
    </scroll-view>

    <!-- 日期选择弹窗 -->
    <uni-popup ref="datePopup" type="bottom" :is-mask-click="true">
      <view class="picker-container">
        <view class="picker-header">
          <text class="picker-cancel" @click="closeDatePicker">取消</text>
          <text class="picker-title">选择生日</text>
          <text class="picker-confirm" @click="confirmDate">确定</text>
        </view>
        <picker-view
          :value="datePickerValue"
          @change="onDateChange"
          class="date-picker-view"
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
import { ref, reactive, computed } from 'vue'
import { useUserStore } from '@/stores/user'
import { useAppStore } from '@/stores/app'

const userStore = useUserStore()
const appStore = useAppStore()

// 响应式数据
const saving = ref(false)
const datePopup = ref<any>(null)
const datePickerValue = ref([0, 0, 0])

// 表单数据
const form = reactive({
  nickname: '',
  phone: '',
  email: '',
  gender: 0,
  birthday: '',
  signature: '',
  avatar: '',
})

// 性别选项
const genderOptions = [
  { value: 0, label: '保密', icon: '🔒' },
  { value: 1, label: '男', icon: '👨' },
  { value: 2, label: '女', icon: '👩' },
]

// 计算属性
const userInitials = computed(() => {
  const name = form.nickname || userStore.userDisplayName || '用户'
  return name.slice(0, 1).toUpperCase()
})

const avatarBgColor = computed(() => {
  return '#FB8B24'
})

// 日期选择器数据
const years = computed(() => {
  const current = new Date().getFullYear()
  return Array.from({ length: 100 }, (_, i) => current - 100 + i).reverse()
})

const months = computed(() => Array.from({ length: 12 }, (_, i) => i + 1))

const days = computed(() => {
  const year = years.value[datePickerValue.value[0]] || new Date().getFullYear()
  const month = months.value[datePickerValue.value[1]] || 1
  return Array.from({ length: getDaysInMonth(year, month) }, (_, i) => i + 1)
})

// 获取天数
const getDaysInMonth = (year: number, month: number) => {
  return new Date(year, month, 0).getDate()
}

// 页面导航
const goBack = () => {
  uni.navigateBack()
}

// 保存
const handleSave = async () => {
  if (saving.value) return

  if (!form.nickname.trim()) {
    appStore.showError('请输入昵称')
    return
  }

  try {
    saving.value = true
    await userStore.updateProfile({
      nickname: form.nickname,
      email: form.email,
      gender: form.gender,
      birthday: form.birthday,
      signature: form.signature,
      avatar: form.avatar,
    })
    appStore.showSuccess('保存成功')
    setTimeout(() => {
      uni.navigateBack()
    }, 1000)
  } catch (error: any) {
    appStore.showError(error.message || '保存失败')
  } finally {
    saving.value = false
  }
}

// 更换头像
const changeAvatar = () => {
  uni.chooseImage({
    count: 1,
    sizeType: ['compressed'],
    sourceType: ['album', 'camera'],
    success: (res) => {
      form.avatar = res.tempFilePaths[0]
    },
  })
}

// 绑定手机号
const bindPhone = () => {
  // 实现手机号绑定逻辑
  uni.showToast({
    title: '手机号绑定功能开发中',
    icon: 'none',
  })
}

// 日期选择器
const showDatePicker = () => {
  datePopup.value.open()
}

const closeDatePicker = () => {
  datePopup.value.close()
}

const onDateChange = (e: any) => {
  datePickerValue.value = e.detail.value
}

const confirmDate = () => {
  const year = years.value[datePickerValue.value[0]] || new Date().getFullYear()
  const month = months.value[datePickerValue.value[1]] || 1
  const day = days.value[datePickerValue.value[2]] || 1
  form.birthday = `${year}-${String(month).padStart(2, '0')}-${String(day).padStart(2, '0')}`
  closeDatePicker()
}

// 初始化数据
const initData = () => {
  const profile = userStore.profile || {}
  const user = userStore.currentUser || {}

  form.nickname = profile.nickname || user.nickname || user.username || ''
  form.phone = profile.phone || user.phone || ''
  form.email = profile.email || user.email || ''
  form.gender = profile.gender || 0
  form.birthday = profile.birthday || ''
  form.signature = profile.signature || ''
  form.avatar = profile.avatar || ''
}

initData()
</script>

<style lang="scss" scoped>
.edit-profile-page {
  min-height: 100vh;
  background-color: #F8F9FA;
  display: flex;
  flex-direction: column;
}

.nav-bar {
  background-color: #fff;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  border-bottom: 1px solid #E5E5E5;
}

.nav-left, .nav-right {
  width: 60px;
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
  color: #333;
}

.save-btn {
  padding: 6px 12px;
  background: #0F4C5C;
  border-radius: 6px;
  font-size: 14px;
  color: #fff;
  font-weight: 500;

  &.disabled {
    background: #ccc;
  }

  &:active:not(.disabled) {
    opacity: 0.9;
  }
}

.main-content {
  flex: 1;
  padding-bottom: 40px;
}

// 头像区域
.avatar-section {
  background: #fff;
  padding: 24px;
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-bottom: 12px;
}

.avatar-wrapper {
  position: relative;
  margin-bottom: 12px;
}

.avatar-preview {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.avatar-text {
  font-size: 28px;
  font-weight: 700;
  color: #fff;
}

.avatar-edit-icon {
  position: absolute;
  bottom: 0;
  right: 0;
  width: 28px;
  height: 28px;
  background: #0F4C5C;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 2px solid #fff;
}

.avatar-hint {
  font-size: 12px;
  color: #999;
}

// 表单区域
.form-section {
  background: #fff;
  margin: 0 16px 12px;
  border-radius: 12px;
  overflow: hidden;
}

.section-title {
  padding: 16px 16px 8px;
  font-size: 14px;
  font-weight: 600;
  color: #333;
}

.form-item {
  padding: 16px;
  border-bottom: 1px solid #F3F4F6;

  &:last-child {
    border-bottom: none;
  }
}

.form-label {
  font-size: 14px;
  color: #666;
  margin-bottom: 10px;
}

.form-input {
  width: 100%;
  height: 44px;
  padding: 0 14px;
  background: #F8F9FA;
  border-radius: 10px;
  font-size: 15px;
  color: #333;
}

.form-value {
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 44px;
  padding: 0 14px;
  background: #F8F9FA;
  border-radius: 10px;
}

.value-text {
  font-size: 15px;
  color: #333;
}

.bind-btn {
  padding: 6px 12px;
  background: #0F4C5C;
  border-radius: 6px;

  text {
    font-size: 12px;
    color: #fff;
    font-weight: 500;
  }

  &:active {
    opacity: 0.9;
  }
}

// 性别选择
.gender-selector {
  display: flex;
  gap: 12px;
}

.gender-option {
  flex: 1;
  height: 44px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  background: #F8F9FA;
  border-radius: 10px;
  border: 2px solid transparent;
  transition: all 0.2s;

  &.active {
    background: rgba(15, 76, 92, 0.08);
    border-color: #0F4C5C;
  }
}

.gender-icon {
  font-size: 18px;
}

.gender-label {
  font-size: 14px;
  color: #333;
}

// 日期选择
.date-picker {
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 44px;
  padding: 0 14px;
  background: #F8F9FA;
  border-radius: 10px;
}

.picker-text {
  font-size: 15px;
  color: #333;

  &.placeholder {
    color: #ccc;
  }
}

// 个人签名
.signature-input {
  width: 100%;
  min-height: 80px;
  padding: 12px 14px;
  background: #F8F9FA;
  border-radius: 10px;
  font-size: 15px;
  color: #333;
  line-height: 1.5;
  border: none;
  outline: none;
}

.word-count {
  text-align: right;
  padding: 8px 4px 0;
  font-size: 12px;
  color: #999;
}

// 日期选择器弹窗
.picker-container {
  background-color: #fff;
  border-radius: 16px 16px 0 0;
  overflow: hidden;
}

.picker-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  border-bottom: 1px solid #E5E5E5;
}

.picker-cancel, .picker-confirm {
  font-size: 15px;
  cursor: pointer;
}

.picker-cancel {
  color: #999;
}

.picker-confirm {
  color: #0F4C5C;
  font-weight: 500;
}

.picker-title {
  font-size: 16px;
  font-weight: 600;
  color: #333;
}

.date-picker-view {
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
