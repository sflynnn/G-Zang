<template>
  <PageTransition>
    <view class="change-password-page apple-style">
      <view class="nav-large-title">
        <view class="nav-header">
          <view class="nav-back" @click="goBack">
            <AppleIcon name="left" :size="20" color="var(--gzang-text-primary)" />
          </view>
          <text class="nav-title">{{ t('settings.changePassword') }}</text>
          <view style="width: 36px;"></view>
        </view>
      </view>

      <view class="main-content">
        <view class="form-card">
          <view class="form-item">
            <text class="form-label">当前密码</text>
            <view class="input-row">
              <input
                v-model="form.oldPassword"
                type="password"
                placeholder="请输入当前密码"
                class="form-input"
              />
              <text class="toggle-btn" @click="showOld = !showOld">{{ showOld ? '隐藏' : '显示' }}</text>
            </view>
          </view>

          <view class="form-divider"></view>

          <view class="form-item">
            <text class="form-label">新密码</text>
            <view class="input-row">
              <input
                v-model="form.newPassword"
                type="password"
                placeholder="请输入新密码（6-20位）"
                class="form-input"
              />
              <text class="toggle-btn" @click="showNew = !showNew">{{ showNew ? '隐藏' : '显示' }}</text>
            </view>
          </view>

          <view class="form-divider"></view>

          <view class="form-item">
            <text class="form-label">确认密码</text>
            <view class="input-row">
              <input
                v-model="form.confirmPassword"
                type="password"
                placeholder="请再次输入新密码"
                class="form-input"
              />
              <text class="toggle-btn" @click="showConfirm = !showConfirm">{{ showConfirm ? '隐藏' : '显示' }}</text>
            </view>
          </view>
        </view>

        <view class="tips-card">
          <text class="tips-title">密码要求</text>
          <view class="tips-row">
            <text :class="['tips-dot', { ok: form.newPassword.length >= 6 }]">-</text>
            <text>6-20位字符</text>
          </view>
          <view class="tips-row">
            <text :class="['tips-dot', { ok: hasLetterAndNumber }">-</text>
            <text>字母和数字组合</text>
          </view>
          <view class="tips-row">
            <text :class="['tips-dot', { ok: passwordsMatch }">-</text>
            <text>两次密码一致</text>
          </view>
        </view>

        <button
          class="submit-btn"
          :disabled="!isFormValid || submitting"
          @click="handleSubmit"
        >
          <text v-if="submitting">提交中...</text>
          <text v-else>确认修改</text>
        </button>
      </view>
    </view>
  </PageTransition>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useI18n } from 'vue-i18n'
import { changePassword } from '@/api/user'
import PageTransition from '@/components/common/PageTransition/index.vue'
import AppleIcon from '@/components/common/AppleIcon/index.vue'

const { t } = useI18n()

const submitting = ref(false)
const showOld = ref(false)
const showNew = ref(false)
const showConfirm = ref(false)

const form = ref({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const hasLetterAndNumber = computed(() =>
  /[A-Za-z]/.test(form.value.newPassword) && /\d/.test(form.value.newPassword)
)
const passwordsMatch = computed(() =>
  form.value.newPassword === form.value.confirmPassword && form.value.confirmPassword.length > 0
)

const isFormValid = computed(() =>
  form.value.oldPassword.length >= 6 &&
  form.value.newPassword.length >= 6 &&
  form.value.newPassword.length <= 20 &&
  form.value.newPassword === form.value.confirmPassword
)

function goBack() { uni.navigateBack() }

async function handleSubmit() {
  if (form.value.newPassword !== form.value.confirmPassword) {
    uni.showToast({ title: '两次密码输入不一致', icon: 'none' })
    return
  }
  submitting.value = true
  try {
    await changePassword(form.value.oldPassword, form.value.newPassword)
    uni.showToast({ title: '密码修改成功', icon: 'success' })
    setTimeout(() => uni.navigateBack(), 1500)
  } catch (e: any) {
    uni.showToast({ title: e.message || '修改失败', icon: 'none' })
  } finally {
    submitting.value = false
  }
}
</script>

<style lang="scss" scoped>
.change-password-page {
  min-height: 100vh;
  background: var(--gzang-bg);
}

.nav-large-title {
  background: var(--gzang-bg);
  padding: 0 var(--apple-space-4);
  padding-top: calc(constant(safe-area-inset-top) + var(--apple-space-3));
}

.nav-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: var(--apple-space-3);
}

.nav-back {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background: var(--gzang-surface);
  display: flex;
  align-items: center;
  justify-content: center;
}

.nav-title {
  font-size: var(--apple-text-lg);
  font-weight: var(--apple-font-semibold);
  color: var(--gzang-text-primary);
}

.main-content {
  padding: 0 var(--apple-space-4);
}

.form-card {
  background: var(--gzang-surface);
  border-radius: var(--apple-radius-xl);
  padding: var(--apple-space-4);
  margin-bottom: var(--apple-space-4);
  box-shadow: var(--apple-shadow-sm);
}

.form-item {
  padding: var(--apple-space-3) 0;
}

.form-label {
  display: block;
  font-size: var(--apple-text-sm);
  color: var(--gzang-text-secondary);
  margin-bottom: var(--apple-space-2);
}

.input-row {
  display: flex;
  align-items: center;
  gap: var(--apple-space-2);
}

.form-input {
  flex: 1;
  font-size: var(--apple-text-base);
  color: var(--gzang-text-primary);
  padding: var(--apple-space-2) 0;
  border: none;
  outline: none;
  background: transparent;
}

.toggle-btn {
  font-size: var(--apple-text-sm);
  color: var(--gzang-primary);
  padding: var(--apple-space-1) var(--apple-space-2);
}

.form-divider {
  height: 1px;
  background: var(--gzang-border);
}

.tips-card {
  background: var(--gzang-surface);
  border-radius: var(--apple-radius-xl);
  padding: var(--apple-space-4);
  margin-bottom: var(--apple-space-5);
  box-shadow: var(--apple-shadow-sm);
}

.tips-title {
  display: block;
  font-size: var(--apple-text-sm);
  font-weight: var(--apple-font-semibold);
  color: var(--gzang-text-primary);
  margin-bottom: var(--apple-space-3);
}

.tips-row {
  display: flex;
  align-items: center;
  gap: var(--apple-space-2);
  padding: var(--apple-space-2) 0;
  font-size: var(--apple-text-sm);
  color: var(--gzang-text-tertiary);
}

.tips-dot {
  &.ok { color: var(--gzang-success); }
}

.submit-btn {
  width: 100%;
  padding: var(--apple-space-4);
  background: var(--gzang-primary);
  color: white;
  border: none;
  border-radius: var(--apple-radius-xl);
  font-size: var(--apple-text-base);
  font-weight: var(--apple-font-semibold);
  box-shadow: 0 4px 12px rgba(15, 76, 92, 0.3);
  text-align: center;

  &[disabled] {
    background: var(--gzang-border);
    box-shadow: none;
  }
}
</style>
