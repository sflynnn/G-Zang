<template>
  <PageTransition>
    <view class="forgot-password-page apple-style">
      <view class="nav-large-title">
        <view class="nav-header">
          <view class="nav-back" @click="goBack">
            <AppleIcon name="left" :size="20" color="var(--gzang-text-primary)" />
          </view>
          <text class="nav-title">忘记密码</text>
          <view style="width: 36px;"></view>
        </view>
      </view>

      <view class="main-content">
        <!-- Step Indicator -->
        <view class="step-indicator">
          <view class="step" :class="{ active: step >= 1, done: step > 1 }">
            <view class="step-circle">
              <text v-if="step <= 1">1</text>
              <text v-else>✓</text>
            </view>
            <text class="step-label">验证手机</text>
          </view>
          <view class="step-line" :class="{ active: step > 1 }"></view>
          <view class="step" :class="{ active: step >= 2, done: step > 2 }">
            <view class="step-circle">
              <text v-if="step <= 2">2</text>
              <text v-else>✓</text>
            </view>
            <text class="step-label">重置密码</text>
          </view>
        </view>

        <!-- Step 1: Phone + SMS -->
        <view v-if="step === 1" class="form-card">
          <view class="form-item">
            <text class="form-label">手机号</text>
            <view class="phone-row">
              <input
                v-model="form.phone"
                type="number"
                placeholder="请输入注册手机号"
                class="form-input"
                maxlength="11"
              />
            </view>
          </view>

          <view class="form-item">
            <text class="form-label">验证码</text>
            <view class="code-row">
              <input
                v-model="form.code"
                type="number"
                placeholder="请输入验证码"
                class="form-input"
                maxlength="6"
              />
              <button
                class="send-btn"
                :disabled="countdown > 0 || sendingCode"
                @click="sendCode"
              >
                <text v-if="sendingCode">发送中...</text>
                <text v-else-if="countdown > 0">{{ countdown }}s</text>
                <text v-else>获取验证码</text>
              </button>
            </view>
          </view>

          <button class="submit-btn" :disabled="!canNextStep1" @click="verifyCode">
            <text>下一步</text>
          </button>
        </view>

        <!-- Step 2: New Password -->
        <view v-else-if="step === 2" class="form-card">
          <view class="form-item">
            <text class="form-label">新密码</text>
            <view class="input-row">
              <input
                v-model="form.newPassword"
                type="password"
                placeholder="请输入新密码（6-20位）"
                class="form-input"
              />
            </view>
          </view>

          <view class="form-item">
            <text class="form-label">确认密码</text>
            <view class="input-row">
              <input
                v-model="form.confirmPassword"
                type="password"
                placeholder="请再次输入新密码"
                class="form-input"
              />
            </view>
          </view>

          <view class="tips-card">
            <view class="tips-row">
              <text :class="['tips-dot', { ok: form.newPassword.length >= 6 }]">-</text>
              <text>6-20位字符</text>
            </view>
            <view class="tips-row">
              <text :class="['tips-dot', { ok: passwordsMatch }]">-</text>
              <text>两次密码一致</text>
            </view>
          </view>

          <button class="submit-btn" :disabled="!canSubmitStep2 || resetting" @click="resetPwd">
            <text v-if="resetting">重置中...</text>
            <text v-else>确认重置</text>
          </button>
        </view>
      </view>
    </view>
  </PageTransition>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { sendResetCode, verifyResetCode, resetPassword } from '@/api/auth'
import PageTransition from '@/components/common/PageTransition/index.vue'
import AppleIcon from '@/components/common/AppleIcon/index.vue'

const step = ref(1)
const sendingCode = ref(false)
const resetting = ref(false)
const countdown = ref(0)
const verifiedPhone = ref('')

const form = ref({
  phone: '',
  code: '',
  newPassword: '',
  confirmPassword: ''
})

let countdownTimer: ReturnType<typeof setInterval> | null = null

const passwordsMatch = computed(() =>
  form.value.newPassword === form.value.confirmPassword && form.value.confirmPassword.length > 0
)

const canNextStep1 = computed(() =>
  form.value.phone.length === 11 && form.value.code.length >= 4
)

const canSubmitStep2 = computed(() =>
  form.value.newPassword.length >= 6 &&
  form.value.newPassword.length <= 20 &&
  passwordsMatch.value
)

function goBack() { uni.navigateBack() }

async function sendCode() {
  if (!/^1\d{10}$/.test(form.value.phone)) {
    uni.showToast({ title: '请输入正确的手机号', icon: 'none' })
    return
  }
  sendingCode.value = true
  try {
    await sendResetCode(form.value.phone)
    uni.showToast({ title: '验证码已发送', icon: 'success' })
    countdown.value = 60
    if (countdownTimer) clearInterval(countdownTimer)
    countdownTimer = setInterval(() => {
      countdown.value--
      if (countdown.value <= 0) {
        if (countdownTimer) clearInterval(countdownTimer)
      }
    }, 1000)
  } catch (e: any) {
    uni.showToast({ title: e.message || '发送失败', icon: 'none' })
  } finally {
    sendingCode.value = false
  }
}

async function verifyCode() {
  if (form.value.code.length < 4) {
    uni.showToast({ title: '请输入完整验证码', icon: 'none' })
    return
  }
  try {
    await verifyResetCode(form.value.phone, form.value.code)
    verifiedPhone.value = form.value.phone
    step.value = 2
  } catch (e: any) {
    uni.showToast({ title: e.message || '验证码错误', icon: 'none' })
  }
}

async function resetPwd() {
  if (form.value.newPassword !== form.value.confirmPassword) {
    uni.showToast({ title: '两次密码不一致', icon: 'none' })
    return
  }
  resetting.value = true
  try {
    await resetPassword(verifiedPhone.value, form.value.code, form.value.newPassword)
    uni.showToast({ title: '密码重置成功', icon: 'success' })
    setTimeout(() => {
      uni.navigateBack()
    }, 1500)
  } catch (e: any) {
    uni.showToast({ title: e.message || '重置失败', icon: 'none' })
  } finally {
    resetting.value = false
  }
}
</script>

<style lang="scss" scoped>
.forgot-password-page {
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

.step-indicator {
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: var(--apple-space-6);
  padding: var(--apple-space-4) 0;
}

.step {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: var(--apple-space-2);
}

.step-circle {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--gzang-border);
  color: var(--gzang-text-tertiary);
  font-size: var(--apple-text-sm);
  font-weight: var(--apple-font-semibold);
  transition: all var(--apple-duration-normal);

  .step.active & {
    background: var(--gzang-primary);
    color: white;
  }

  .step.done & {
    background: var(--gzang-success);
    color: white;
  }
}

.step-label {
  font-size: var(--apple-text-xs);
  color: var(--gzang-text-tertiary);

  .step.active & {
    color: var(--gzang-primary);
    font-weight: var(--apple-font-semibold);
  }
}

.step-line {
  width: 60px;
  height: 2px;
  background: var(--gzang-border);
  margin: 0 var(--apple-space-3);
  margin-bottom: 22px;
  transition: background var(--apple-duration-normal);

  &.active {
    background: var(--gzang-primary);
  }
}

.form-card {
  background: var(--gzang-surface);
  border-radius: var(--apple-radius-xl);
  padding: var(--apple-space-4);
  margin-bottom: var(--apple-space-4);
  box-shadow: var(--apple-shadow-sm);
}

.form-item {
  margin-bottom: var(--apple-space-4);
}

.form-label {
  display: block;
  font-size: var(--apple-text-sm);
  color: var(--gzang-text-secondary);
  margin-bottom: var(--apple-space-2);
}

.phone-row {
  display: flex;
  align-items: center;
}

.input-row {
  display: flex;
  align-items: center;
}

.form-input {
  flex: 1;
  font-size: var(--apple-text-base);
  color: var(--gzang-text-primary);
  padding: var(--apple-space-3) 0;
  border: none;
  outline: none;
  background: transparent;
  border-bottom: 1px solid var(--gzang-border);

  &:focus {
    border-bottom-color: var(--gzang-primary);
  }
}

.code-row {
  display: flex;
  align-items: center;
  gap: var(--apple-space-3);
  border-bottom: 1px solid var(--gzang-border);
}

.send-btn {
  padding: var(--apple-space-2) var(--apple-space-3);
  background: var(--gzang-primary);
  color: white;
  border: none;
  border-radius: var(--apple-radius-md);
  font-size: var(--apple-text-sm);
  flex-shrink: 0;

  &[disabled] {
    background: var(--gzang-border);
  }
}

.tips-card {
  background: var(--gzang-bg);
  border-radius: var(--apple-radius-lg);
  padding: var(--apple-space-3);
  margin-bottom: var(--apple-space-5);
}

.tips-row {
  display: flex;
  align-items: center;
  gap: var(--apple-space-2);
  padding: var(--apple-space-1) 0;
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

  &[disabled] {
    background: var(--gzang-border);
    box-shadow: none;
  }
}
</style>
