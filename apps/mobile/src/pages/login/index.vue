<template>
  <view class="login-page">
    <!-- 动态渐变背景 -->
    <view class="gradient-bg">
      <view class="gradient-orb orb-1"></view>
      <view class="gradient-orb orb-2"></view>
      <view class="gradient-orb orb-3"></view>
      <view class="mesh-overlay"></view>
    </view>

    <!-- 内容区域 -->
    <view class="content-wrapper">
      <!-- 主题/语言切换 -->
      <view class="settings-row">
        <!-- 主题切换 -->
        <view class="setting-item" @click="toggleTheme">
          <text class="setting-icon">{{ isDark ? '🌙' : '☀️' }}</text>
        </view>
        <!-- 语言切换 -->
        <view class="setting-item" @click="toggleLanguage">
          <text class="setting-text">{{ currentLangLabel }}</text>
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

      <!-- 登录表单 -->
      <view class="form-section" :class="{ 'animate-in': mounted }">
        <view class="glass-card">
          <!-- 欢迎语 -->
          <view class="welcome-header">
            <text class="welcome-title">{{ $t('brand.welcomeBack') }}</text>
            <text class="welcome-subtitle">{{ $t('brand.loginToContinue') }}</text>
          </view>

          <!-- 手机号/用户名输入 -->
          <view class="input-group" :class="{ 'focused': focusedField === 'username', 'has-value': form.username }">
            <view class="input-prefix">
              <text class="prefix-icon">📱</text>
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
              <text class="floating-label">{{ $t('auth.phoneOrUsername') }}</text>
            </view>
            <view v-if="form.username" class="input-action" @click="form.username = ''">
              <text class="action-icon">✕</text>
            </view>
          </view>

          <!-- 密码输入 -->
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
              <text class="floating-label">{{ $t('auth.password') }}</text>
            </view>
            <view class="input-action" @click="showPassword = !showPassword">
              <text class="action-icon">{{ showPassword ? '🙈' : '👁️' }}</text>
            </view>
          </view>

          <!-- 记住我 & 忘记密码 -->
          <view class="form-options">
            <label class="remember-option" @click="form.remember = !form.remember">
              <view class="checkbox" :class="{ checked: form.remember }">
                <text v-if="form.remember" class="check-icon">✓</text>
              </view>
              <text class="option-label">{{ $t('auth.rememberMe') }}</text>
            </label>
            <text class="forgot-link" @click="handleForgot">{{ $t('auth.forgotPassword') }}</text>
          </view>

          <!-- 登录按钮 -->
          <button
            class="login-button"
            :class="{ 'loading': loading, 'disabled': !isFormValid }"
            :disabled="loading || !isFormValid"
            @click="handleLogin"
          >
            <view v-if="loading" class="button-loader">
              <view class="spinner"></view>
            </view>
            <text v-else class="button-text">{{ $t('auth.loginButton') }}</text>
          </button>

          <!-- 注册提示 -->
          <view class="register-prompt-inline">
            <text class="prompt-text">{{ $t('brand.noAccount') }}</text>
            <text class="prompt-link" @click="handleRegister">{{ $t('brand.registerNow') }}</text>
          </view>
        </view>
      </view>

      <!-- 底部协议 -->
      <view class="terms-section">
        <text class="terms-text">{{ $t('brand.loginAgreement') }}</text>
        <text class="terms-link" @click="handleTerms">{{ $t('brand.userAgreement') }}</text>
        <text class="terms-text">{{ $t('brand.and') }}</text>
        <text class="terms-link" @click="handlePrivacy">{{ $t('brand.privacyPolicy') }}</text>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { useAuthStore } from '@/stores/auth'
import { useAppStore } from '@/stores/app'
import { Storage } from '@/utils/storage'

// 存储键
const SAVED_CREDENTIALS_KEY = 'saved_credentials'

// 状态管理
const authStore = useAuthStore()
const appStore = useAppStore()

// 响应式数据
const form = reactive({
  username: '',
  password: '',
  remember: false
})

const usernameReady = ref(false)
const passwordReady = ref(false)

const focusedField = ref<string | null>(null)
const showPassword = ref(false)
const loading = ref(false)
const mounted = ref(false)
const isNavigating = ref(false) // 防止导航时触发 reactive 更新

// 计算属性
const isFormValid = computed(() => {
  return form.username.trim().length >= 3 && form.password.length >= 6
})

// 主题
const isDark = computed(() => appStore.isDark)

const toggleTheme = () => {
  const next = isDark.value ? 'light' : 'dark'
  appStore.setTheme(next)
}

// 语言
const currentLangLabel = computed(() => {
  return appStore.language === 'en-US' ? 'EN' : '中文'
})

const toggleLanguage = () => {
  const next = appStore.language === 'en-US' ? 'zh-CN' : 'en-US'
  appStore.setLanguage(next)
}

// 处理登录
const handleLogin = async () => {
  if (!isFormValid.value || loading.value) {
    return
  }

  try {
    loading.value = true

    await authStore.login({
      username: form.username,
      password: form.password,
      remember: form.remember
    })

    // 根据是否记住我，保存或清除凭证
    if (form.remember) {
      Storage.set(SAVED_CREDENTIALS_KEY, {
        username: form.username,
        password: form.password
      })
    } else {
      Storage.remove(SAVED_CREDENTIALS_KEY)
    }

    appStore.showSuccess('登录成功')

    isNavigating.value = true // 标记正在导航，防止 onShow 触发
    uni.switchTab({
      url: '/pages/home/index'
    })

  } catch (error: any) {
    isNavigating.value = false // 登录失败，重置导航状态
    appStore.showError(error.message || '登录失败，请重试')
  } finally {
    loading.value = false
  }
}

// 忘记密码
const handleForgot = () => {
  uni.showToast({
    title: '忘记密码功能开发中',
    icon: 'none'
  })
}

// 注册
const handleRegister = () => {
  uni.navigateTo({
    url: '/pages/register/index'
  })
}

// 用户协议
const handleTerms = () => {
  uni.navigateTo({
    url: '/pages/terms/index'
  })
}

// 隐私政策
const handlePrivacy = () => {
  uni.navigateTo({
    url: '/pages/privacy/index'
  })
}

// 生命周期
onMounted(async () => {
  // 检查是否已登录 -> 延迟执行，等待路由稳定
  if (authStore.isAuthenticated) {
    await new Promise(resolve => setTimeout(resolve, 150))
    uni.reLaunch({ url: '/pages/home/index' })
  }
})

// 页面每次显示时：清空表单、禁止输入，根据是否记住过决定是否回填
onShow(() => {
  // 如果正在导航中，跳过表单重置逻辑
  if (isNavigating.value) {
    return
  }
  
  form.username = ''
  form.password = ''
  form.remember = false

  // 禁用输入框，阻止浏览器自动填充
  usernameReady.value = false
  passwordReady.value = false

  // 触发入场动画
  mounted.value = false
  setTimeout(() => {
    mounted.value = true
  }, 50)

  // 延迟填充凭证（使用单向标记防止重复执行）
  let fillExecuted = false
  const fillCredentials = () => {
    if (fillExecuted) return
    fillExecuted = true
    const saved = Storage.get(SAVED_CREDENTIALS_KEY)
    if (saved && saved.username) {
      form.username = saved.username
      form.remember = true
    }
    usernameReady.value = true
    passwordReady.value = true
  }
  
  setTimeout(fillCredentials, 300)
})
</script>

<style lang="scss" scoped>
.login-page {
  min-height: 100vh;
  position: relative;
  overflow: hidden;
  background: var(--gzang-bg);
}

// 动态渐变背景
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
  background: linear-gradient(180deg, rgba(var(--gzang-overlay-dark), 0.3) 0%, rgba(var(--gzang-overlay-dark), 0.8) 100%);
  pointer-events: none;
}

// 深色模式下的 mesh-overlay 覆盖
:deep([data-theme="dark"]) .mesh-overlay,
[data-theme="dark"] .mesh-overlay {
  background: linear-gradient(180deg, rgba(10, 10, 15, 0.3) 0%, rgba(10, 10, 15, 0.8) 100%);
}

// 浅色模式下的 mesh-overlay 覆盖
:deep(:root:not([data-theme="dark"])) .mesh-overlay,
:root:not([data-theme="dark"]) .mesh-overlay {
  background: linear-gradient(180deg, rgba(248, 249, 250, 0.3) 0%, rgba(248, 249, 250, 0.8) 100%);
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

/* 内容包装器 */
.content-wrapper {
  position: relative;
  z-index: 1;
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  padding: 0 24px;
}

/* 主题/语言设置行 */
.settings-row {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  gap: 8px;
  padding-top: 12px;
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

/* Hero 区域 */
.hero-section {
  flex: 0 0 auto;
  display: flex;
  flex-direction: column;
  align-items: center;
  padding-top: 60px;
  padding-bottom: 32px;
  opacity: 0;
  transform: translateY(-20px);
  transition: all 0.8s cubic-bezier(0.16, 1, 0.3, 1);

  &.animate-in {
    opacity: 1;
    transform: translateY(0);
  }
}

.logo-container {
  width: 80px;
  height: 80px;
  margin-bottom: 16px;
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
  font-size: 28px;
  font-weight: 700;
  color: var(--gzang-text-primary);
  letter-spacing: 2px;
  margin-bottom: 4px;
}

.brand-tagline {
  display: block;
  font-size: 13px;
  color: var(--gzang-text-secondary);
  letter-spacing: 3px;
}

/* 表单区域 */
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
  padding: 28px 20px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
}

.welcome-header {
  text-align: center;
  margin-bottom: 24px;
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

/* 输入框组 */
.input-group {
  position: relative;
  display: flex;
  align-items: center;
  background: var(--gzang-bg);
  border: 1px solid var(--gzang-border);
  border-radius: 12px;
  padding: 0 14px;
  margin-bottom: 14px;
  transition: all 0.3s ease;
  min-height: 52px;

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
  min-height: 52px;
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
    color: var(--gzang-secondary);
    opacity: 1;
  }

  .input-group.has-value & {
    color: var(--gzang-text-secondary);
  }
}

.modern-input {
  width: 100%;
  height: 52px;
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

/* 禁用浏览器自动填充样式 */
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

/* 表单选项 */
.form-options {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.remember-option {
  display: flex;
  align-items: center;
  cursor: pointer;
}

.checkbox {
  width: 20px;
  height: 20px;
  border: 2px solid var(--gzang-border);
  border-radius: 6px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 10px;
  transition: all 0.2s ease;

  &.checked {
    background: var(--gzang-secondary);
    border-color: var(--gzang-secondary);
  }
}

.check-icon {
  color: var(--gzang-surface);
  font-size: 12px;
  font-weight: bold;
}

.option-label {
  font-size: 13px;
  color: var(--gzang-text-secondary);
}

.forgot-link {
  font-size: 13px;
  color: var(--gzang-secondary);
  font-weight: 500;
  cursor: pointer;
}

/* 登录按钮 */
.login-button {
  width: 100%;
  height: 52px;
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

/* 分隔线 */
.divider {
  display: flex;
  align-items: center;
  margin: 20px 0;
}

.divider-line {
  flex: 1;
  height: 1px;
  background: var(--gzang-border);
}

.divider-text {
  padding: 0 14px;
  font-size: 12px;
  color: var(--gzang-text-secondary);
}

/* 社交登录 */
.social-login {
  display: flex;
  gap: 12px;
}

.social-button {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  height: 46px;
  background: var(--gzang-bg);
  border: 1px solid var(--gzang-border);
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.2s ease;

  &:active {
    background: var(--gzang-border);
  }
}

.social-icon {
  font-size: 18px;
}

.social-text {
  font-size: 13px;
  color: var(--gzang-text-primary);
}

/* 注册提示 */
.register-prompt {
  flex-shrink: 0;
  text-align: center;
  padding: 20px 0;
  opacity: 0;
  transform: translateY(20px);
  transition: all 0.8s cubic-bezier(0.16, 1, 0.3, 1) 0.3s;

  &.animate-in {
    opacity: 1;
    transform: translateY(0);
  }
}

.register-prompt-inline {
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

/* 协议 */
.terms-section {
  flex-shrink: 0;
  text-align: center;
  padding-bottom: 20px;
}

.terms-text {
  font-size: 11px;
  color: var(--gzang-text-secondary);
}

.terms-link {
  font-size: 11px;
  color: var(--gzang-secondary);
  cursor: pointer;
}
</style>
