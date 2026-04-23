<template>
  <view class="register-page">
    <!-- 动态渐变背景 -->
    <view class="gradient-bg">
      <view class="gradient-orb orb-1"></view>
      <view class="gradient-orb orb-2"></view>
      <view class="gradient-orb orb-3"></view>
      <view class="mesh-overlay"></view>
    </view>

    <!-- 内容区域 -->
    <view class="content-wrapper">
      <!-- 返回 & 主题/语言切换 -->
      <view class="top-bar">
        <view class="back-btn" @click="handleBack">
          <uni-icons type="left" size="18" :color="'var(--gzang-text-primary, #1F2937)'"></uni-icons>
        </view>
        <view class="settings-row">
          <view class="setting-item" @click="toggleTheme">
            <text class="setting-icon">{{ isDark ? '🌙' : '☀️' }}</text>
          </view>
          <view class="setting-item" @click="toggleLanguage">
            <text class="setting-text">{{ currentLangLabel }}</text>
          </view>
        </view>
      </view>

      <!-- Logo 区域 -->
      <view class="hero-section" :class="{ 'animate-in': mounted }">
        <view class="logo-container">
          <image
            class="brand-logo"
            src="/static/logo.png"
            mode="aspectFit"
          />
        </view>
        <view class="brand-info">
          <text class="brand-name">G-Zang</text>
          <text class="brand-tagline">{{ $t('brand.tagline') }}</text>
        </view>
      </view>

      <!-- 注册表单 -->
      <view class="form-section" :class="{ 'animate-in': mounted }">
        <view class="glass-card">
          <!-- 欢迎语 -->
          <view class="welcome-header">
            <text class="welcome-title">{{ $t('register.welcomeTitle') }}</text>
            <text class="welcome-subtitle">{{ $t('register.welcomeSubtitle') }}</text>
          </view>

          <!-- 用户名 -->
          <view class="input-group" :class="{ 'focused': focusedField === 'username', 'has-value': form.username }">
            <view class="input-prefix">
              <text class="prefix-icon">👤</text>
            </view>
            <view class="input-wrapper">
              <input
                v-model="form.username"
                type="text"
                class="modern-input"
                placeholder=" "
                autocomplete="off"
                autocorrect="off"
                autocapitalize="off"
                spellcheck="false"
                :disabled="!usernameReady"
                @focus="focusedField = 'username'"
                @blur="focusedField = null"
              />
              <text class="floating-label">{{ $t('auth.usernamePlaceholder') }}</text>
            </view>
            <view v-if="form.username" class="input-action" @click="form.username = ''">
              <text class="action-icon">✕</text>
            </view>
          </view>

          <!-- 昵称 -->
          <view class="input-group" :class="{ 'focused': focusedField === 'nickname', 'has-value': form.nickname }">
            <view class="input-prefix">
              <text class="prefix-icon">🏷️</text>
            </view>
            <view class="input-wrapper">
              <input
                v-model="form.nickname"
                type="text"
                class="modern-input"
                placeholder=" "
                autocomplete="off"
                autocorrect="off"
                autocapitalize="off"
                spellcheck="false"
                :disabled="!nicknameReady"
                @focus="focusedField = 'nickname'"
                @blur="focusedField = null"
              />
              <text class="floating-label">{{ $t('register.nicknamePlaceholder') }}</text>
            </view>
            <view v-if="form.nickname" class="input-action" @click="form.nickname = ''">
              <text class="action-icon">✕</text>
            </view>
          </view>

          <!-- 密码 -->
          <view class="input-group" :class="{ 'focused': focusedField === 'password', 'has-value': form.password }">
            <view class="input-prefix">
              <text class="prefix-icon">🔐</text>
            </view>
            <view class="input-wrapper">
              <input
                v-model="form.password"
                :type="showPassword ? 'text' : 'password'"
                class="modern-input"
                placeholder=" "
                autocomplete="new-password"
                autocorrect="off"
                autocapitalize="off"
                spellcheck="false"
                :disabled="!passwordReady"
                @focus="focusedField = 'password'"
                @blur="focusedField = null"
              />
              <text class="floating-label">{{ $t('auth.passwordPlaceholder') }}</text>
            </view>
            <view class="input-action" @click="showPassword = !showPassword">
              <text class="action-icon">{{ showPassword ? '🙈' : '👁️' }}</text>
            </view>
          </view>

          <!-- 确认密码 -->
          <view class="input-group" :class="{ 'focused': focusedField === 'confirmPassword', 'has-value': form.confirmPassword }">
            <view class="input-prefix">
              <text class="prefix-icon">🔐</text>
            </view>
            <view class="input-wrapper">
              <input
                v-model="form.confirmPassword"
                :type="showConfirmPassword ? 'text' : 'password'"
                class="modern-input"
                placeholder=" "
                autocomplete="new-password"
                autocorrect="off"
                autocapitalize="off"
                spellcheck="false"
                :disabled="!confirmPasswordReady"
                @focus="focusedField = 'confirmPassword'"
                @blur="focusedField = null"
              />
              <text class="floating-label">{{ $t('register.confirmPasswordPlaceholder') }}</text>
            </view>
            <view class="input-action" @click="showConfirmPassword = !showConfirmPassword">
              <text class="action-icon">{{ showConfirmPassword ? '🙈' : '👁️' }}</text>
            </view>
          </view>

          <!-- 用户协议 -->
          <view class="agreement-row">
            <label class="agreement-option" @click="form.agreed = !form.agreed">
              <view class="checkbox" :class="{ checked: form.agreed }">
                <text v-if="form.agreed" class="check-icon">✓</text>
              </view>
              <text class="agreement-text">
                {{ $t('register.agreePrefix') }}
                <text class="agreement-link" @click.stop="handleTerms">{{ $t('brand.userAgreement') }}</text>
                {{ $t('register.and') }}
                <text class="agreement-link" @click.stop="handlePrivacy">{{ $t('brand.privacyPolicy') }}</text>
              </text>
            </label>
          </view>

          <!-- 注册按钮 -->
          <button
            class="register-button"
            :class="{ 'loading': loading, 'disabled': !isFormValid }"
            :disabled="loading || !isFormValid"
            @click="handleRegister"
          >
            <view v-if="loading" class="button-loader">
              <view class="spinner"></view>
            </view>
            <text v-else class="button-text">{{ $t('register.registerButton') }}</text>
          </button>

          <!-- 登录提示 -->
          <view class="login-prompt-inline">
            <text class="prompt-text">{{ $t('register.hasAccount') }}</text>
            <text class="prompt-link" @click="handleBackToLogin">{{ $t('register.loginNow') }}</text>
          </view>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted, watch, nextTick } from 'vue'
import { useAppStore } from '@/stores/app'
import { register as registerApi } from '@/api/auth'
import { i18n } from '@/i18n'

const appStore = useAppStore()

const form = reactive({
  username: '',
  nickname: '',
  password: '',
  confirmPassword: '',
  agreed: false
})

const usernameReady = ref(false)
const nicknameReady = ref(false)
const passwordReady = ref(false)
const confirmPasswordReady = ref(false)

const focusedField = ref<string | null>(null)
const showPassword = ref(false)
const showConfirmPassword = ref(false)
const loading = ref(false)
const mounted = ref(false)

const isFormValid = computed(() => {
  const usernameOk = form.username.trim().length >= 3
  const passwordOk = form.password.length >= 6
  const confirmOk = form.confirmPassword === form.password
  const agreedOk = form.agreed
  return usernameOk && passwordOk && confirmOk && agreedOk
})

const isDark = computed(() => appStore.isDark)

const toggleTheme = () => {
  appStore.setTheme(isDark.value ? 'light' : 'dark')
}

const currentLangLabel = computed(() => {
  return appStore.language === 'en-US' ? 'EN' : '中文'
})

const toggleLanguage = () => {
  const next = appStore.language === 'en-US' ? 'zh-CN' : 'en-US'
  appStore.setLanguage(next)
}

watch(() => appStore.isDark, (isDark) => {
  if (typeof document !== 'undefined') {
    if (isDark) {
      document.documentElement.setAttribute('data-theme', 'dark')
    } else {
      document.documentElement.removeAttribute('data-theme')
    }
  }
}, { immediate: true })

const handleBack = () => {
  uni.navigateBack()
}

const handleBackToLogin = () => {
  uni.navigateBack()
}

const handleTerms = () => {
  uni.navigateTo({ url: '/pages/terms/index' })
}

const handlePrivacy = () => {
  uni.navigateTo({ url: '/pages/privacy/index' })
}

const handleRegister = async () => {
  if (!isFormValid.value || loading.value) return

  if (form.password !== form.confirmPassword) {
    uni.showToast({ title: i18n.global.t('auth.passwordsNotMatch'), icon: 'none' })
    return
  }

  try {
    loading.value = true
    await registerApi({
      username: form.username.trim(),
      password: form.password,
      nickname: form.nickname.trim() || undefined
    })
    appStore.showSuccess(i18n.global.t('register.registerSuccess'))
    uni.navigateBack()
  } catch (error: any) {
    appStore.showError(error.message || i18n.global.t('register.registerFailed'))
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  mounted.value = true

  // 禁用输入框，阻止浏览器自动填充
  usernameReady.value = false
  nicknameReady.value = false
  passwordReady.value = false
  confirmPasswordReady.value = false

  // 延迟启用输入框（此时自动填充已过期）
  setTimeout(() => {
    usernameReady.value = true
    nicknameReady.value = true
    passwordReady.value = true
    confirmPasswordReady.value = true
  }, 300)
})
</script>

<style lang="scss" scoped>
.register-page {
  min-height: 100vh;
  position: relative;
  overflow: hidden;
  background: var(--gzang-bg);
}

.gradient-bg {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 0;
  background: var(--gzang-bg);
}

.gradient-orb {
  position: absolute;
  border-radius: 50%;
  filter: blur(80px);
  opacity: 0.6;
  animation: float 8s ease-in-out infinite;

  &.orb-1 {
    width: 300px;
    height: 300px;
    background: radial-gradient(circle, var(--gzang-primary) 0%, var(--gzang-primary-light) 100%);
    top: -100px;
    right: -100px;
    animation-delay: 0s;
  }

  &.orb-2 {
    width: 250px;
    height: 250px;
    background: radial-gradient(circle, var(--gzang-secondary) 0%, var(--gzang-secondary-light) 100%);
    bottom: 20%;
    left: -80px;
    animation-delay: -3s;
    opacity: 0.4;
  }

  &.orb-3 {
    width: 200px;
    height: 200px;
    background: radial-gradient(circle, var(--gzang-info) 0%, var(--gzang-success) 100%);
    bottom: -50px;
    right: 20%;
    animation-delay: -5s;
    opacity: 0.3;
  }
}

.mesh-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(180deg, rgba(248, 249, 250, 0.3) 0%, rgba(248, 249, 250, 0.8) 100%);
  pointer-events: none;
}

:deep([data-theme="dark"]) .mesh-overlay,
[data-theme="dark"] .mesh-overlay {
  background: linear-gradient(180deg, rgba(10, 10, 15, 0.3) 0%, rgba(10, 10, 15, 0.8) 100%);
}

@keyframes float {
  0%, 100% {
    transform: translate(0, 0) scale(1);
  }
  33% {
    transform: translate(30px, -30px) scale(1.05);
  }
  66% {
    transform: translate(-20px, 20px) scale(0.95);
  }
}

.content-wrapper {
  position: relative;
  z-index: 1;
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  padding: 0 24px;
}

.top-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 12px;
}

.back-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 40px;
  height: 40px;
  border-radius: 12px;
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(8px);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  cursor: pointer;
  transition: all 0.2s ease;

  &:active {
    transform: scale(0.94);
    box-shadow: 0 1px 4px rgba(0, 0, 0, 0.06);
  }
}

.settings-row {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  gap: 8px;
}

.setting-item {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 6px 12px;
  border-radius: 20px;
  background: var(--gzang-bg);
  border: 1px solid var(--gzang-border);
  cursor: pointer;
  transition: all 0.2s ease;

  &:active {
    transform: scale(0.96);
    opacity: 0.8;
  }
}

.setting-icon {
  font-size: 16px;
  line-height: 1;
}

.setting-text {
  font-size: 12px;
  font-weight: 500;
  color: var(--gzang-text-secondary);
  letter-spacing: 0.5px;
}

.hero-section {
  flex: 0 0 auto;
  display: flex;
  flex-direction: column;
  align-items: center;
  padding-top: 24px;
  padding-bottom: 24px;
  opacity: 0;
  transform: translateY(-20px);
  transition: all 0.8s cubic-bezier(0.16, 1, 0.3, 1);

  &.animate-in {
    opacity: 1;
    transform: translateY(0);
  }
}

.logo-container {
  width: 64px;
  height: 64px;
  margin-bottom: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.brand-logo {
  width: 100%;
  height: 100%;
  object-fit: contain;
}

.brand-info {
  text-align: center;
}

.brand-name {
  display: block;
  font-size: 24px;
  font-weight: 700;
  color: var(--gzang-text-primary);
  letter-spacing: 2px;
  margin-bottom: 4px;
}

.brand-tagline {
  display: block;
  font-size: 12px;
  color: var(--gzang-text-secondary);
  letter-spacing: 3px;
}

.form-section {
  flex: 1;
  display: flex;
  flex-direction: column;
  opacity: 0;
  transform: translateY(30px);
  transition: all 0.8s cubic-bezier(0.16, 1, 0.3, 1) 0.15s;

  &.animate-in {
    opacity: 1;
    transform: translateY(0);
  }
}

.glass-card {
  background: var(--gzang-surface);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border: 1px solid var(--gzang-border);
  border-radius: 20px;
  padding: 24px 20px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
}

.welcome-header {
  text-align: center;
  margin-bottom: 20px;
}

.welcome-title {
  display: block;
  font-size: 22px;
  font-weight: 600;
  color: var(--gzang-text-primary);
  margin-bottom: 4px;
}

.welcome-subtitle {
  display: block;
  font-size: 13px;
  color: var(--gzang-text-secondary);
}

.input-group {
  position: relative;
  display: flex;
  align-items: center;
  background: var(--gzang-bg);
  border: 1px solid var(--gzang-border);
  border-radius: 12px;
  padding: 0 14px;
  margin-bottom: 12px;
  transition: all 0.3s ease;
  min-height: 50px;

  &.focused {
    background: var(--gzang-bg);
    border-color: var(--gzang-secondary);
    box-shadow: 0 0 0 3px rgba(251, 139, 36, 0.1);
  }
}

.input-prefix {
  flex-shrink: 0;
  padding-right: 12px;
  display: flex;
  align-items: center;
}

.prefix-icon {
  font-size: 18px;
}

.input-wrapper {
  flex: 1;
  position: relative;
  display: flex;
  align-items: center;
  min-height: 50px;
}

.floating-label {
  position: absolute;
  left: 0;
  font-size: 15px;
  color: var(--gzang-text-secondary);
  transition: all 0.3s ease;
  pointer-events: none;
  top: 50%;
  transform: translateY(-50%);
  opacity: 0.6;

  .input-group.focused &,
  .input-group.has-value & {
    font-size: 11px;
    top: 2px;
    transform: translateY(0);
    padding-left: 2px;
    color: var(--gzang-secondary);
    opacity: 1;
  }

  .input-group.has-value & {
    color: var(--gzang-text-secondary);
  }
}

.modern-input {
  width: 100%;
  height: 50px;
  border: none;
  outline: none;
  background: transparent;
  font-size: 16px;
  color: var(--gzang-text-primary);
  caret-color: var(--gzang-secondary);
  padding: 0;
  position: absolute;
  top: 0;
  left: 0;

  &::placeholder {
    color: transparent;
  }
}

.modern-input:-webkit-autofill,
.modern-input:-webkit-autofill:hover,
.modern-input:-webkit-autofill:focus,
.modern-input:-webkit-autofill:active {
  -webkit-box-shadow: 0 0 0 1000px var(--gzang-surface) inset !important;
  -webkit-text-fill-color: var(--gzang-text-primary) !important;
  transition: background-color 5000s ease-in-out 0s;
  background-color: var(--gzang-surface) !important;
  color: var(--gzang-text-primary) !important;
}

/* 隐藏自动填充的下拉建议框 */
input:-webkit-autofill-preview,
input:-webkit-autofill-capture-button {
  display: none !important;
}

.input-action {
  flex-shrink: 0;
  padding: 8px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0.6;
  transition: opacity 0.2s;

  &:hover {
    opacity: 1;
  }
}

.action-icon {
  font-size: 14px;
}

.agreement-row {
  margin-bottom: 16px;
}

.agreement-option {
  display: flex;
  align-items: flex-start;
  cursor: pointer;
}

.checkbox {
  width: 18px;
  height: 18px;
  min-width: 18px;
  border: 2px solid var(--gzang-border);
  border-radius: 5px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 8px;
  margin-top: 1px;
  transition: all 0.2s ease;

  &.checked {
    background: var(--gzang-secondary);
    border-color: var(--gzang-secondary);
  }
}

.check-icon {
  color: var(--gzang-surface);
  font-size: 11px;
  font-weight: bold;
}

.agreement-text {
  font-size: 12px;
  color: var(--gzang-text-secondary);
  line-height: 1.5;
}

.agreement-link {
  color: var(--gzang-secondary);
  cursor: pointer;
}

.register-button {
  width: 100%;
  height: 50px;
  background: linear-gradient(135deg, var(--gzang-secondary) 0%, var(--gzang-secondary-light) 100%);
  border: none;
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 4px 16px rgba(251, 139, 36, 0.3);

  &.disabled {
    opacity: 0.5;
    cursor: not-allowed;
  }

  &:active:not(.disabled) {
    transform: scale(0.98);
  }
}

.button-text {
  font-size: 16px;
  font-weight: 600;
  color: var(--gzang-surface);
  letter-spacing: 4px;
}

.button-loader {
  .spinner {
    width: 22px;
    height: 22px;
    border: 2px solid rgba(255, 255, 255, 0.3);
    border-top-color: var(--gzang-surface);
    border-radius: 50%;
    animation: spin 0.8s linear infinite;
  }
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

.login-prompt-inline {
  display: flex;
  justify-content: center;
  align-items: center;
  margin-top: 16px;
  gap: 4px;
}

.prompt-text {
  font-size: 13px;
  color: var(--gzang-text-secondary);
}

.prompt-link {
  font-size: 13px;
  color: var(--gzang-secondary);
  font-weight: 600;
  cursor: pointer;
}
</style>
