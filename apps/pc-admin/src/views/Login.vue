<template>
  <div class="min-h-screen bg-background flex">
    <!-- 左侧品牌区域 -->
    <div class="hidden lg:flex lg:w-1/2 bg-primary flex-col justify-center items-center p-8 relative overflow-hidden">
      <!-- 背景装饰图案 -->
      <div class="absolute inset-0 opacity-10">
        <div class="absolute top-10 left-10 w-32 h-32 bg-secondary/20 rounded-full"></div>
        <div class="absolute bottom-20 right-20 w-24 h-24 bg-success/20 rounded-full"></div>
        <div class="absolute top-1/2 left-1/4 w-16 h-16 bg-white/10 rounded-full"></div>
      </div>

      <!-- 品牌内容 -->
      <div class="relative z-10 text-center">
        <!-- Logo -->
        <div class="mx-auto mb-8 w-20 h-20 bg-white rounded-xl flex items-center justify-center shadow-admin-lg">
          <img src="/logo.png" alt="G-Zang Logo" class="w-16 h-16 object-contain" />
        </div>

        <!-- 品牌标题 -->
        <h1 class="text-4xl font-bold text-white mb-4 font-chinese tracking-tight">
          {{ $t('brand.title') }}
        </h1>

        <!-- 副标题 -->
        <p class="text-lg text-white/80 font-chinese">
          {{ $t('brand.subtitle') }}
        </p>

        <!-- 标语 -->
        <p class="mt-6 text-sm text-white/60 font-sans">
          {{ $t('brand.slogan') }}
        </p>
      </div>
    </div>

    <!-- 右侧登录表单 -->
    <div :class="['login-right-section', { 'theme-dark': appStore.isDark }]">
      <!-- 背景装饰 -->
      <div class="login-bg-gradient"></div>
      <div class="login-bg-overlay"></div>

      <!-- 装饰元素 -->
      <div class="login-decoration login-decoration-1"></div>
      <div class="login-decoration login-decoration-2"></div>

      <!-- 右上角工具栏 -->
      <div class="login-toolbar">
        <!-- 语言切换 -->
        <n-dropdown
          :options="languageOptions"
          @select="handleLanguageChange"
          trigger="click"
        >
          <button
            class="flex items-center justify-center gap-1 px-2 h-9 rounded-lg transition-colors border"
            :class="[
              appStore.language === 'zh-CN'
                ? isDark ? 'border-dark-border bg-dark-surface-elevated text-dark-text-primary' : 'border-gray-200 bg-gray-50 text-text-primary font-medium'
                : isDark ? 'hover:bg-dark-surface-elevated text-dark-text-secondary hover:text-dark-text-primary border-transparent' : 'hover:bg-gray-100 text-text-secondary hover:text-text-primary border-transparent'
            ]"
            :title="$t('common.language')"
          >
            <n-icon :component="LanguageOutline" :size="16" />
            <span class="text-xs font-semibold">{{ appStore.language === 'zh-CN' ? '中' : 'EN' }}</span>
          </button>
        </n-dropdown>

        <!-- 主题切换 -->
        <button
          @click="handleThemeToggle"
          class="p-2 rounded-lg transition-colors"
          :class="isDark ? 'hover:bg-dark-surface-elevated text-dark-text-secondary hover:text-dark-text-primary' : 'hover:bg-gray-100 text-text-secondary hover:text-text-primary'"
          :title="$t('common.toggleTheme')"
        >
          <n-icon :component="isDark ? SunnyOutline : MoonOutline" :size="18" />
        </button>
      </div>

      <!-- 表单容器 -->
      <div class="login-form-wrapper">
        <!-- 表单卡片容器 -->
        <div :class="['login-form-card', { 'bg-dark': appStore.isDark }]">
          <!-- 表单头部 -->
          <div class="login-form-header">
            <h2 class="login-form-title">
              {{ $t('login.title') }}
            </h2>
            <p class="login-form-subtitle">
              {{ $t('login.subtitle') }}
            </p>
          </div>

          <!-- 登录表单 -->
          <n-form ref="formRef" :model="formData" :rules="rules" label-placement="top" @submit.prevent="handleSubmit"
            class="space-y-4">
            <!-- 用户名输入框 -->
            <n-form-item path="username">
              <n-input v-model:value="formData.username" :placeholder="$t('login.username')" size="large"
                :disabled="loading" clearable autofocus class="rounded-lg">
                <template #prefix>
                  <svg class="w-5 h-5 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                      d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z"></path>
                  </svg>
                </template>
              </n-input>
            </n-form-item>

            <!-- 密码输入框 -->
            <n-form-item path="password">
              <n-input v-model:value="formData.password" type="password" :placeholder="$t('login.password')"
                size="large" :disabled="loading" show-password-on="click" @keydown.enter="handleSubmit"
                class="rounded-lg">
                <template #prefix>
                  <svg class="w-5 h-5 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                      d="M12 15v2m-6 4h12a2 2 0 002-2v-6a2 2 0 00-2-2H6a2 2 0 00-2 2v6a2 2 0 002 2zm10-10V7a4 4 0 00-8 0v4h8z">
                    </path>
                  </svg>
                </template>
              </n-input>
            </n-form-item>

            <!-- 滑块验证 -->
            <div class="captcha-container">
              <SliderCaptcha ref="sliderCaptchaRef" v-model="captchaVerified" />
            </div>

            <!-- 记住我和忘记密码 -->
            <div class="flex items-center justify-between mt-4 mb-2">
              <n-checkbox v-model:checked="rememberMe" size="small" class="text-sm">
                <span class="font-sans text-gray-600">{{ $t('login.rememberMe') }}</span>
              </n-checkbox>
              <n-button text size="small" class="text-secondary hover:text-secondary/80 font-sans">
                {{ $t('login.forgotPassword') }}
              </n-button>
            </div>

            <!-- 登录按钮 -->
            <n-form-item>
              <button type="submit" :disabled="loading" class="login-btn-primary">
                <span v-if="loading">{{ $t('login.loggingIn') }}</span>
                <span v-else>{{ $t('login.loginButton') }}</span>
              </button>
            </n-form-item>
          </n-form>

          <!-- 底部信息 -->
          <div class="text-center">
            <p class="text-xs text-gray-500 dark:text-gray-400 font-sans">
              {{ $t('login.copyright') }}
            </p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, watch, nextTick, computed, h } from 'vue';
import { useRouter } from 'vue-router';
import { useI18n } from 'vue-i18n';
import { NIcon } from 'naive-ui';
// Icons are no longer needed in the simplified Apple-style design
import { LanguageOutline, SunnyOutline, MoonOutline, CheckmarkCircleSharp } from '@vicons/ionicons5';
import { useAppStore } from '@/stores/app';
import { login } from '@/api/auth';
import SliderCaptcha from '@/components/SliderCaptcha.vue';

const router = useRouter();
const appStore = useAppStore();
const { t, locale } = useI18n();

const isDark = computed(() => appStore.isDark);

// 语言选项（动态计算，当前语言显示勾选）
const languageOptions = computed(() => [
  {
    label: t('common.chinese'),
    key: 'zh-CN',
    icon: appStore.language === 'zh-CN'
      ? () => h(NIcon, { component: CheckmarkCircleSharp, size: 16, class: 'text-secondary' })
      : undefined,
  },
  {
    label: t('common.english'),
    key: 'en-US',
    icon: appStore.language === 'en-US'
      ? () => h(NIcon, { component: CheckmarkCircleSharp, size: 16, class: 'text-secondary' })
      : undefined,
  }
]);

// 表单引用
const formRef = ref();

// 滑块验证引用
const sliderCaptchaRef = ref();

// 表单数据
const formData = reactive({
  username: '',
  password: ''
});

// 记住我
const rememberMe = ref(false);

// 滑块验证状态
const captchaVerified = ref(false);

// 表单验证规则
const rules = {
  username: [
    { required: true, message: () => t('login.validation.usernameRequired'), trigger: 'blur' }
  ],
  password: [
    { required: true, message: () => t('login.validation.passwordRequired'), trigger: 'blur' },
    { min: 6, message: () => t('login.validation.passwordMinLength'), trigger: 'blur' }
  ]
};

// 加载状态
const loading = ref(false);

// 页面加载时检查是否记住登录信息
onMounted(() => {
  const remembered = localStorage.getItem('admin_rememberMe');
  if (remembered === 'true') {
    rememberMe.value = true;
    const savedUsername = localStorage.getItem('admin_savedUsername');
    if (savedUsername) {
      formData.username = savedUsername;
    }
  }
});

// 语言切换处理
const handleLanguageChange = (lang: string) => {
  appStore.setLanguage(lang);
  locale.value = lang;
};

// 监听语言变化，清除表单验证错误
watch(locale, async () => {
  await nextTick();
  try {
    // 清除验证状态
    if (formRef.value && typeof formRef.value.resetValidation === 'function') {
      formRef.value.resetValidation();
    }
  } catch (error) {
    // 如果方法不存在，静默处理
    console.warn('Form validation reset method not available:', error);
  }
});

// 主题切换处理
const handleThemeToggle = () => {
  appStore.setDarkTheme(!appStore.isDark);
};

// 提交表单
const handleSubmit = async () => {
  try {
    // 检查滑块验证
    if (!captchaVerified.value) {
      window.$message?.warning(t('login.captchaRequired'));
      return;
    }

    await formRef.value?.validate();
    loading.value = true;
    appStore.setLoading(true); // 显示全局loading

    // 调用真实API进行登录
    const response = await login(formData);

    if (response.code === 0) {
      // 保存token和用户信息（含角色和权限）
      const loginData = response.data;
      const userInfo = loginData.user;
      appStore.setToken(loginData.token || '');
      appStore.setUser({
        userId: userInfo.userId,
        username: userInfo.username,
        nickname: userInfo.nickname || userInfo.username,
        avatar: userInfo.avatar,
        roleId: userInfo.roleId,
        roleCode: userInfo.roleCode || '',
        roleName: userInfo.roleName || '',
        companyId: userInfo.companyId,
        companyName: userInfo.companyName,
        status: userInfo.status,
        permissions: userInfo.permissions || loginData.permissions || []
      });

      // 如果选择记住我，保存用户名
      if (rememberMe.value) {
        localStorage.setItem('admin_rememberMe', 'true');
        localStorage.setItem('admin_savedUsername', formData.username);
      } else {
        localStorage.removeItem('admin_rememberMe');
        localStorage.removeItem('admin_savedUsername');
      }

      // 重置滑块验证
      captchaVerified.value = false;
      sliderCaptchaRef.value?.reset();

      // 显示成功消息
      window.$message?.success(t('login.success'));

      // 跳转到首页
      router.push('/dashboard');
    } else {
      // 处理登录失败的情况
      const errorMsg = response.message || t('login.loginFailed');
      console.error('Login failed:', errorMsg);
      window.$message?.error(errorMsg);
      // 重置滑块验证
      captchaVerified.value = false;
      sliderCaptchaRef.value?.reset();
    }
  } catch (error: any) {
    console.error('Login failed:', error);
    window.$message?.error(t('login.networkError'));

    // 处理表单验证错误或其他错误
    if (error.message) {
      console.error('错误信息:', error.message);
    }
  } finally {
    loading.value = false;
    appStore.setLoading(false); // 隐藏全局loading
  }
};
</script>

<style scoped>
/* 自定义Naive UI组件样式以匹配全局主题 */
:deep(.n-form-item-blank) {
  background: rgba(255, 255, 255, 0.6);
  backdrop-filter: blur(8px);
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: var(--border-radius-xl);
  transition: all var(--transition-normal);
  box-shadow: var(--shadow-sm);
}

@media (prefers-color-scheme: dark) {
  :deep(.n-form-item-blank) {
    background: rgba(31, 41, 55, 0.6);
    border-color: rgba(75, 85, 99, 0.3);
  }
}

:deep(.n-form-item-blank:hover) {
  background: rgba(255, 255, 255, 0.8);
  border-color: rgba(251, 139, 36, 0.3);
  transform: translateY(-1px);
  box-shadow: var(--shadow-md);
}

@media (prefers-color-scheme: dark) {
  :deep(.n-form-item-blank:hover) {
    background: rgba(55, 65, 81, 0.8);
    border-color: rgba(251, 139, 36, 0.4);
  }
}

:deep(.n-form-item-blank:focus-within) {
  background: rgba(255, 255, 255, 0.95);
  border-color: var(--color-secondary);
  box-shadow: 0 0 0 3px rgba(251, 139, 36, 0.15), var(--shadow-lg);
  transform: translateY(-1px);
}

@media (prefers-color-scheme: dark) {
  :deep(.n-form-item-blank:focus-within) {
    background: rgba(17, 24, 39, 0.95);
  }
}

:deep(.n-input__input) {
  color: var(--color-gray-900);
  font-family: var(--font-family-sans);
}

@media (prefers-color-scheme: dark) {
  :deep(.n-input__input) {
    color: var(--color-gray-100);
  }
}

:deep(.n-input__placeholder) {
  color: var(--color-gray-400);
}

@media (prefers-color-scheme: dark) {
  :deep(.n-input__placeholder) {
    color: var(--color-gray-500);
  }
}

:deep(.n-checkbox__label) {
  color: var(--color-gray-600);
  font-family: var(--font-family-sans);
}

@media (prefers-color-scheme: dark) {
  :deep(.n-checkbox__label) {
    color: var(--color-gray-400);
  }
}

:deep(.n-button--text) {
  color: var(--color-secondary);
  font-family: var(--font-family-sans);
}

:deep(.n-button--text:hover) {
  color: var(--color-secondary);
  opacity: 0.8;
}

@media (prefers-color-scheme: dark) {

  /* 登录页深色背景下标题使用浅色字体 */
  .login-form-title {
    color: #FFFFFF;
  }

  /* 登录页中的次要文本（例如"记住我"）使用浅色但保留层次感 */
  .font-sans.text-gray-600 {
    color: var(--color-gray-400);
  }
}

/* 登录页工具栏样式 */
.login-toolbar {
  position: absolute;
  top: 1rem;
  right: 1rem;
  z-index: 10;
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

/* 深色主题下文字颜色 */
.text-text-primary {
  color: var(--color-gray-900);
}

.text-text-secondary {
  color: var(--color-gray-500);
}

.bg-dark-surface-elevated {
  background-color: #374151;
}

.border-dark-border {
  border-color: #374151;
}

.text-dark-text-primary {
  color: #F9FAFB;
}

.text-dark-text-secondary {
  color: #9CA3AF;
}
</style>
