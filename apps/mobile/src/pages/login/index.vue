<template>
  <view class="login-page">
    <!-- 背景装饰 -->
    <view class="background">
      <view class="circle circle-1"></view>
      <view class="circle circle-2"></view>
      <view class="circle circle-3"></view>
    </view>

    <!-- 顶部Logo区域 -->
    <view class="header">
      <view class="logo-container">
        <view class="logo">
          <text class="logo-text">G-Zang</text>
        </view>
        <text class="subtitle">财务管理系统</text>
      </view>
    </view>

    <!-- 登录表单 -->
    <view class="form-container">
      <view class="form-card">
        <text class="form-title">欢迎回来</text>
        <text class="form-subtitle">请登录您的账户</text>

        <view class="form">
          <!-- 用户名输入 -->
          <view class="form-item">
            <view class="input-container">
              <view class="input-icon">
                <uni-icons type="person" size="20" color="#666"></uni-icons>
              </view>
              <input
                v-model="form.username"
                type="text"
                placeholder="请输入用户名"
                class="input"
                @blur="validateField('username')"
              />
            </view>
            <text v-if="errors.username" class="error-text">{{ errors.username }}</text>
          </view>

          <!-- 密码输入 -->
          <view class="form-item">
            <view class="input-container">
              <view class="input-icon">
                <uni-icons type="locked" size="20" color="#666"></uni-icons>
              </view>
              <input
                v-model="form.password"
                :type="showPassword ? 'text' : 'password'"
                placeholder="请输入密码"
                class="input"
                @blur="validateField('password')"
              />
              <view class="input-action" @click="togglePasswordVisibility">
                <uni-icons
                  :type="showPassword ? 'eye' : 'eye-slash'"
                  size="20"
                  color="#666"
                ></uni-icons>
              </view>
            </view>
            <text v-if="errors.password" class="error-text">{{ errors.password }}</text>
          </view>

          <!-- 记住密码 -->
          <view class="form-item remember">
            <label class="checkbox-container">
              <checkbox
                v-model="form.remember"
                color="#1989fa"
              />
              <text class="checkbox-label">记住密码</text>
            </label>
            <text class="forgot-password" @click="goToForgotPassword">忘记密码？</text>
          </view>

          <!-- 登录按钮 -->
          <view class="form-item">
            <button
              class="login-btn"
              :class="{ loading }"
              :disabled="loading || !isFormValid"
              @click="handleLogin"
            >
              <text v-if="loading" class="loading-text">登录中...</text>
              <text v-else>登录</text>
            </button>
          </view>

          <!-- 注册链接 -->
          <view class="form-item register-link">
            <text>还没有账户？</text>
            <text class="register-text" @click="goToRegister">立即注册</text>
          </view>
        </view>
      </view>
    </view>

    <!-- 底部信息 -->
    <view class="footer">
      <text class="footer-text">© 2024 G-Zang 财务管理系统</text>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { useAuthStore } from '@/stores/auth'
import { useAppStore } from '@/stores/app'
import { validateLoginForm } from '@/utils/validate'

// 状态管理
const authStore = useAuthStore()
const appStore = useAppStore()

// 响应式数据
const form = reactive({
  username: '',
  password: '',
  remember: false
})

const showPassword = ref(false)
const loading = ref(false)
const errors = reactive({
  username: '',
  password: ''
})

// 计算属性
const isFormValid = computed(() => {
  return form.username.trim() && form.password.trim() && !errors.username && !errors.password
})

// 验证字段
const validateField = (field: keyof typeof errors) => {
  const validation = validateLoginForm({
    username: form.username,
    password: form.password
  })

  errors[field] = validation.errors[field] || ''
}

// 切换密码可见性
const togglePasswordVisibility = () => {
  showPassword.value = !showPassword.value
}

// 处理登录
const handleLogin = async () => {
  if (!isFormValid.value) return

  try {
    loading.value = true

    await authStore.login({
      username: form.username,
      password: form.password,
      remember: form.remember
    })

    appStore.showSuccess('登录成功')

    // 跳转到首页
    uni.switchTab({
      url: '/pages/home/index'
    })

  } catch (error: any) {
    console.error('登录失败:', error)
    appStore.showError(error.message || '登录失败，请重试')
  } finally {
    loading.value = false
  }
}

// 跳转到忘记密码
const goToForgotPassword = () => {
  uni.showToast({
    title: '忘记密码功能开发中',
    icon: 'none'
  })
}

// 跳转到注册页面
const goToRegister = () => {
  uni.navigateTo({
    url: '/pages/register/index'
  })
}

// 生命周期
onMounted(() => {
  // 检查是否已登录
  if (authStore.isAuthenticated) {
    uni.switchTab({
      url: '/pages/home/index'
    })
  }

  // 初始化表单验证
  Object.keys(form).forEach(key => {
    if (key !== 'remember') {
      validateField(key as keyof typeof errors)
    }
  })
})
</script>

<style lang="scss" scoped>
.login-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  position: relative;
  overflow: hidden;
  padding: 0 24px;
  display: flex;
  flex-direction: column;
}

.background {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  overflow: hidden;
}

.circle {
  position: absolute;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.1);
  animation: float 6s ease-in-out infinite;

  &.circle-1 {
    width: 200px;
    height: 200px;
    top: -100px;
    right: -100px;
    animation-delay: 0s;
  }

  &.circle-2 {
    width: 150px;
    height: 150px;
    top: 200px;
    left: -75px;
    animation-delay: 2s;
  }

  &.circle-3 {
    width: 100px;
    height: 100px;
    bottom: 100px;
    right: 50px;
    animation-delay: 4s;
  }
}

@keyframes float {
  0%, 100% {
    transform: translateY(0px);
  }
  50% {
    transform: translateY(-20px);
  }
}

.header {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
}

.logo-container {
  text-align: center;
  z-index: 1;
}

.logo {
  width: 80px;
  height: 80px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 16px;
  backdrop-filter: blur(10px);
}

.logo-text {
  font-size: 24px;
  font-weight: bold;
  color: white;
}

.subtitle {
  font-size: 16px;
  color: rgba(255, 255, 255, 0.8);
}

.form-container {
  flex: 2;
  display: flex;
  align-items: flex-start;
  justify-content: center;
  z-index: 1;
}

.form-card {
  background: rgba(255, 255, 255, 0.95);
  border-radius: 20px;
  padding: 32px 24px;
  width: 100%;
  max-width: 400px;
  backdrop-filter: blur(10px);
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
}

.form-title {
  font-size: 24px;
  font-weight: bold;
  color: #333;
  text-align: center;
  display: block;
  margin-bottom: 8px;
}

.form-subtitle {
  font-size: 14px;
  color: #666;
  text-align: center;
  display: block;
  margin-bottom: 32px;
}

.form-item {
  margin-bottom: 24px;

  &.remember {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 16px;
  }

  &.register-link {
    text-align: center;
    margin-top: 24px;
    margin-bottom: 0;
  }
}

.input-container {
  display: flex;
  align-items: center;
  border: 1px solid #e0e0e0;
  border-radius: 12px;
  background: white;
  transition: border-color 0.2s;

  &:focus-within {
    border-color: #1989fa;
  }
}

.input-icon {
  padding: 0 16px;
  display: flex;
  align-items: center;
}

.input {
  flex: 1;
  padding: 16px 0;
  border: none;
  outline: none;
  font-size: 16px;
  background: transparent;
}

.input-action {
  padding: 0 16px;
  display: flex;
  align-items: center;
  cursor: pointer;
}

.error-text {
  font-size: 12px;
  color: #ff4d4f;
  margin-top: 4px;
  display: block;
}

.checkbox-container {
  display: flex;
  align-items: center;
  cursor: pointer;
}

.checkbox-label {
  margin-left: 8px;
  font-size: 14px;
  color: #666;
}

.forgot-password {
  font-size: 14px;
  color: #1989fa;
  cursor: pointer;
}

.login-btn {
  width: 100%;
  height: 50px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  border-radius: 12px;
  color: white;
  font-size: 16px;
  font-weight: 600;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.2s;

  &:disabled {
    opacity: 0.6;
    cursor: not-allowed;
  }

  &:not(:disabled):active {
    transform: scale(0.98);
  }

  &.loading {
    pointer-events: none;
  }
}

.register-text {
  color: #1989fa;
  cursor: pointer;
  margin-left: 4px;
}

.footer {
  padding: 24px 0;
  text-align: center;
  z-index: 1;
}

.footer-text {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.6);
}
</style>
