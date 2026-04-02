<template>
  <div class="profile min-h-screen bg-background dark:bg-background-dark p-4 md:p-6 lg:p-8">
    <!-- 页面标题 -->
    <div class="mb-6 animate-fade-in">
      <h1 class="text-2xl font-bold text-text-primary dark:text-text-primary-dark">{{ $t('profile.title') }}</h1>
      <p class="text-text-secondary dark:text-text-secondary-dark mt-1">{{ $t('profile.subtitle') }}</p>
    </div>

    <div class="grid grid-cols-1 lg:grid-cols-3 gap-6">
      <!-- 左侧：用户信息卡片 -->
      <div class="lg:col-span-1">
        <div
          class="bg-surface dark:bg-surface-dark rounded-2xl shadow-gzang dark:shadow-lg p-6 animate-slide-up"
        >
          <!-- 头像区域 -->
          <div class="flex flex-col items-center pb-6 border-b border-border dark:border-border-dark">
            <div class="relative group">
              <n-avatar
                :size="96"
                round
                :src="user?.avatar || 'https://gw.alipayobjects.com/zos/rmsportal/BiazfanxmamNRoxxVxka.png'"
                class="ring-4 ring-primary/10"
              />
              <button
                class="absolute inset-0 flex items-center justify-center rounded-full bg-black/50 opacity-0 group-hover:opacity-100 transition-opacity"
                @click="triggerAvatarUpload"
              >
                <n-icon :component="CameraOutline" :size="24" class="text-white" />
              </button>
              <input
                ref="avatarInputRef"
                type="file"
                accept="image/*"
                class="hidden"
                @change="handleAvatarChange"
              />
            </div>
            <h2 class="mt-4 text-lg font-semibold text-text-primary dark:text-text-primary-dark">
              {{ user?.nickname || user?.username || t('profile.admin') }}
            </h2>
            <p class="text-sm text-text-secondary dark:text-text-secondary-dark">
              {{ user?.roleName || t('profile.systemAdmin') }}
            </p>
            <div class="mt-3 flex items-center gap-2">
              <span
                class="inline-flex items-center gap-1 px-2.5 py-1 rounded-full text-xs font-medium bg-success/10 text-success"
              >
                <span class="w-1.5 h-1.5 rounded-full bg-success"></span>
                {{ t('profile.online') }}
              </span>
              <span
                class="px-2.5 py-1 rounded-full text-xs font-medium bg-primary/10 text-primary"
              >
                {{ user?.companyName || t('profile.systemLevel') }}
              </span>
            </div>
          </div>

          <!-- 统计信息 -->
          <div class="py-4 border-b border-border dark:border-border-dark">
            <div class="grid grid-cols-3 gap-2 text-center">
              <div>
                <p class="text-xl font-bold font-mono text-text-primary dark:text-text-primary-dark">12</p>
                <p class="text-xs text-text-secondary dark:text-text-secondary-dark mt-0.5">
                  {{ t('profile.actions') }}
                </p>
              </div>
              <div>
                <p class="text-xl font-bold font-mono text-text-primary dark:text-text-primary-dark">5</p>
                <p class="text-xs text-text-secondary dark:text-text-secondary-dark mt-0.5">
                  {{ t('profile.logins') }}
                </p>
              </div>
              <div>
                <p class="text-xl font-bold font-mono text-text-primary dark:text-text-primary-dark">{{ accountAge }}</p>
                <p class="text-xs text-text-secondary dark:text-text-secondary-dark mt-0.5">
                  {{ t('profile.accountDays') }}
                </p>
              </div>
            </div>
          </div>

          <!-- 安全等级 -->
          <div class="pt-4">
            <div class="flex items-center justify-between mb-2">
              <span class="text-sm font-medium text-text-primary dark:text-text-primary-dark">
                {{ t('profile.securityLevel') }}
              </span>
              <span class="text-sm font-semibold text-success">{{ securityLevel }}</span>
            </div>
            <div class="h-2 bg-gray-100 dark:bg-gray-700 rounded-full overflow-hidden">
              <div
                class="h-full rounded-full transition-all duration-500"
                :class="securityColor"
                :style="{ width: securityPercent + '%' }"
              ></div>
            </div>
            <p class="text-xs text-text-secondary dark:text-text-secondary-dark mt-1.5">
              {{ securityTip }}
            </p>
          </div>
        </div>
      </div>

      <!-- 右侧：设置表单 -->
      <div class="lg:col-span-2 space-y-6">
        <!-- 基本信息 -->
        <div
          class="bg-surface dark:bg-surface-dark rounded-2xl shadow-gzang dark:shadow-lg p-6 animate-slide-up"
          style="animation-delay: 100ms"
        >
          <div class="flex items-center gap-3 mb-6">
            <div class="w-10 h-10 rounded-xl bg-primary/10 flex items-center justify-center">
              <n-icon :component="PersonOutline" :size="20" class="text-primary" />
            </div>
            <div>
              <h3 class="text-base font-semibold text-text-primary dark:text-text-primary-dark">
                {{ t('profile.basicInfo') }}
              </h3>
              <p class="text-xs text-text-secondary dark:text-text-secondary-dark">
                {{ t('profile.updateInfo') }}
              </p>
            </div>
          </div>

          <div class="grid grid-cols-1 md:grid-cols-2 gap-x-6 gap-y-5">
            <!-- 用户名 -->
            <div class="md:col-span-1">
              <label class="block text-sm font-medium mb-1.5" :class="isDark ? 'text-gray-300' : 'text-gray-700'">
                {{ t('profile.username') }}
              </label>
              <n-input
                v-model:value="form.username"
                :disabled="true"
                :placeholder="t('profile.username')"
                size="large"
              />
            </div>

            <!-- 昵称 -->
            <div class="md:col-span-1">
              <label class="block text-sm font-medium mb-1.5" :class="isDark ? 'text-gray-300' : 'text-gray-700'">
                {{ t('profile.nickname') }}
              </label>
              <n-input
                v-model:value="form.nickname"
                :placeholder="t('profile.nicknamePlaceholder')"
                size="large"
              />
            </div>

            <!-- 手机号 -->
            <div class="md:col-span-1">
              <label class="block text-sm font-medium mb-1.5" :class="isDark ? 'text-gray-300' : 'text-gray-700'">
                {{ t('profile.phone') }}
              </label>
              <n-input
                v-model:value="form.phone"
                :placeholder="t('profile.phonePlaceholder')"
                size="large"
              >
                <template #prefix>
                  <span class="text-text-secondary dark:text-text-secondary-dark">+86</span>
                </template>
              </n-input>
            </div>

            <!-- 邮箱 -->
            <div class="md:col-span-1">
              <label class="block text-sm font-medium mb-1.5" :class="isDark ? 'text-gray-300' : 'text-gray-700'">
                {{ t('profile.email') }}
              </label>
              <n-input
                v-model:value="form.email"
                :placeholder="t('profile.emailPlaceholder')"
                size="large"
              />
            </div>

            <!-- 公司 -->
            <div class="md:col-span-1">
              <label class="block text-sm font-medium mb-1.5" :class="isDark ? 'text-gray-300' : 'text-gray-700'">
                {{ t('profile.company') }}
              </label>
              <n-input
                :value="user?.companyName || t('profile.systemAccount')"
                :disabled="true"
                size="large"
              />
            </div>

            <!-- 角色 -->
            <div class="md:col-span-1">
              <label class="block text-sm font-medium mb-1.5" :class="isDark ? 'text-gray-300' : 'text-gray-700'">
                {{ t('profile.role') }}
              </label>
              <n-input
                :value="user?.roleName || t('profile.systemAdmin')"
                :disabled="true"
                size="large"
              />
            </div>
          </div>

          <div class="mt-6 flex justify-end">
            <n-button type="primary" size="large" :loading="saving" @click="saveBasicInfo">
              {{ t('profile.saveChanges') }}
            </n-button>
          </div>
        </div>

        <!-- 安全设置 -->
        <div
          class="bg-surface dark:bg-surface-dark rounded-2xl shadow-gzang dark:shadow-lg p-6 animate-slide-up"
          style="animation-delay: 200ms"
        >
          <div class="flex items-center gap-3 mb-6">
            <div class="w-10 h-10 rounded-xl bg-secondary/10 flex items-center justify-center">
              <n-icon :component="ShieldOutline" :size="20" class="text-secondary" />
            </div>
            <div>
              <h3 class="text-base font-semibold text-text-primary dark:text-text-primary-dark">
                {{ t('profile.security') }}
              </h3>
              <p class="text-xs text-text-secondary dark:text-text-secondary-dark">
                {{ t('profile.managePassword') }}
              </p>
            </div>
          </div>

          <div class="space-y-4">
            <!-- 修改密码 -->
            <div
              class="flex items-center justify-between p-4 rounded-xl border transition-colors"
              :class="isDark ? 'border-[#374151] hover:border-secondary/40' : 'border-gray-200 hover:border-primary/40'"
            >
              <div class="flex items-center gap-3">
                <div class="w-10 h-10 rounded-lg bg-gray-100 dark:bg-gray-700 flex items-center justify-center">
                  <n-icon :component="KeyOutline" :size="18" class="text-text-secondary dark:text-text-secondary-dark" />
                </div>
                <div>
                  <p class="text-sm font-medium text-text-primary dark:text-text-primary-dark">
                    {{ t('profile.loginPassword') }}
                  </p>
                  <p class="text-xs text-text-secondary dark:text-text-secondary-dark mt-0.5">
                    {{ t('profile.lastChanged') }}
                  </p>
                </div>
              </div>
              <n-button size="small" secondary type="default" @click="showPasswordModal = true">
                {{ t('profile.change') }}
              </n-button>
            </div>

            <!-- 两步验证 -->
            <div
              class="flex items-center justify-between p-4 rounded-xl border transition-colors"
              :class="isDark ? 'border-[#374151] hover:border-secondary/40' : 'border-gray-200 hover:border-primary/40'"
            >
              <div class="flex items-center gap-3">
                <div class="w-10 h-10 rounded-lg bg-gray-100 dark:bg-gray-700 flex items-center justify-center">
                  <n-icon :component="PhonePortraitOutline" :size="18" class="text-text-secondary dark:text-text-secondary-dark" />
                </div>
                <div>
                  <p class="text-sm font-medium text-text-primary dark:text-text-primary-dark">
                    {{ t('profile.twoFactor') }}
                  </p>
                  <p class="text-xs mt-0.5" :class="twoFactorEnabled ? 'text-success' : 'text-text-secondary dark:text-text-secondary-dark'">
                    {{ twoFactorEnabled ? t('profile.twoFactorEnabled') : t('profile.twoFactorDisabled') }}
                  </p>
                </div>
              </div>
              <n-switch :value="twoFactorEnabled" @update:value="toggleTwoFactor" />
            </div>

            <!-- 登录设备管理 -->
            <div
              class="flex items-center justify-between p-4 rounded-xl border transition-colors"
              :class="isDark ? 'border-[#374151] hover:border-secondary/40' : 'border-gray-200 hover:border-primary/40'"
            >
              <div class="flex items-center gap-3">
                <div class="w-10 h-10 rounded-lg bg-gray-100 dark:bg-gray-700 flex items-center justify-center">
                  <n-icon :component="LaptopOutline" :size="18" class="text-text-secondary dark:text-text-secondary-dark" />
                </div>
                <div>
                  <p class="text-sm font-medium text-text-primary dark:text-text-primary-dark">
                    {{ t('profile.loginDevices') }}
                  </p>
                  <p class="text-xs text-text-secondary dark:text-text-secondary-dark mt-0.5">
                    {{ t('profile.currentDevices') }}
                  </p>
                </div>
              </div>
              <n-button size="small" secondary type="default" @click="showDevicesModal = true">
                {{ t('profile.manage') }}
              </n-button>
            </div>

            <!-- 账户注销 -->
            <div
              class="flex items-center justify-between p-4 rounded-xl border transition-colors"
              :class="isDark ? 'border-[#374151]/50 hover:border-danger/30' : 'border-gray-200/50 hover:border-danger/30'"
            >
              <div class="flex items-center gap-3">
                <div class="w-10 h-10 rounded-lg bg-danger/5 flex items-center justify-center">
                  <n-icon :component="TrashOutline" :size="18" class="text-danger/60" />
                </div>
                <div>
                  <p class="text-sm font-medium text-danger/80">
                    {{ t('profile.deleteAccount') }}
                  </p>
                  <p class="text-xs text-text-secondary dark:text-text-secondary-dark mt-0.5">
                    {{ t('profile.deleteWarning') }}
                  </p>
                </div>
              </div>
              <n-button size="small" type="error" tertiary>
                {{ t('profile.delete') }}
              </n-button>
            </div>
          </div>
        </div>

        <!-- 偏好设置 -->
        <div
          class="bg-surface dark:bg-surface-dark rounded-2xl shadow-gzang dark:shadow-lg p-6 animate-slide-up"
          style="animation-delay: 300ms"
        >
          <div class="flex items-center gap-3 mb-6">
            <div class="w-10 h-10 rounded-xl bg-success/10 flex items-center justify-center">
              <n-icon :component="SettingsOutline" :size="20" class="text-success" />
            </div>
            <div>
              <h3 class="text-base font-semibold text-text-primary dark:text-text-primary-dark">
                {{ t('profile.preferences') }}
              </h3>
              <p class="text-xs text-text-secondary dark:text-text-secondary-dark">
                {{ t('profile.customizePrefs') }}
              </p>
            </div>
          </div>

          <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
            <!-- 语言 -->
            <div>
              <label class="block text-sm font-medium mb-1.5" :class="isDark ? 'text-gray-300' : 'text-gray-700'">
                {{ t('profile.interfaceLanguage') }}
              </label>
              <n-select
                v-model:value="form.language"
                :options="languageOptions"
                size="large"
                @update:value="handleLanguageChange"
              />
            </div>

            <!-- 主题 -->
            <div>
              <label class="block text-sm font-medium mb-1.5" :class="isDark ? 'text-gray-300' : 'text-gray-700'">
                {{ t('profile.themeModeLabel') }}
              </label>
              <n-select
                v-model:value="form.theme"
                :options="themeOptions"
                size="large"
                @update:value="handleThemeChange"
              />
            </div>

            <!-- 通知偏好 -->
            <div class="md:col-span-2">
              <label class="block text-sm font-medium mb-3" :class="isDark ? 'text-gray-300' : 'text-gray-700'">
                {{ t('profile.notificationPrefs') }}
              </label>
              <div class="grid grid-cols-1 sm:grid-cols-2 gap-3">
                <div
                  v-for="pref in notificationPrefs"
                  :key="pref.key"
                  class="flex items-center justify-between p-3 rounded-xl border"
                  :class="isDark ? 'border-[#374151]' : 'border-gray-200'"
                >
                  <div class="flex items-center gap-2">
                    <n-icon :component="pref.icon" :size="16" class="text-text-secondary dark:text-text-secondary-dark" />
                    <span class="text-sm" :class="isDark ? 'text-gray-300' : 'text-gray-700'">{{ getNotificationLabel(pref.key) }}</span>
                  </div>
                  <n-switch :value="pref.enabled" size="small" />
                </div>
              </div>
            </div>
          </div>

          <div class="mt-6 flex justify-end">
            <n-button type="primary" size="large" :loading="savingPrefs" @click="savePreferences">
              {{ t('profile.savePrefs') }}
            </n-button>
          </div>
        </div>
      </div>
    </div>

    <!-- 修改密码弹窗 -->
    <n-modal
      v-model:show="showPasswordModal"
      preset="card"
      :title="t('profile.changePassword')"
      :class="isDark ? 'password-modal-dark' : 'password-modal-light'"
      style="width: 28rem; max-width: 90vw;"
    >
      <div class="space-y-4">
          <div>
          <label class="block text-sm font-medium mb-1.5" :class="isDark ? 'text-gray-300' : 'text-gray-700'">
            {{ t('profile.currentPassword') }}
          </label>
          <n-input
            v-model:value="passwordForm.oldPassword"
            type="password"
            show-password-on="click"
            :placeholder="t('profile.currentPasswordPlaceholder')"
            size="large"
          />
        </div>
        <div>
          <label class="block text-sm font-medium mb-1.5" :class="isDark ? 'text-gray-300' : 'text-gray-700'">
            {{ t('profile.newPassword') }}
          </label>
          <n-input
            v-model:value="passwordForm.newPassword"
            type="password"
            show-password-on="click"
            :placeholder="t('profile.newPasswordPlaceholder')"
            size="large"
          />
        </div>
        <div>
          <label class="block text-sm font-medium mb-1.5" :class="isDark ? 'text-gray-300' : 'text-gray-700'">
            {{ t('profile.confirmPassword') }}
          </label>
          <n-input
            v-model:value="passwordForm.confirmPassword"
            type="password"
            show-password-on="click"
            :placeholder="t('profile.confirmPasswordPlaceholder')"
            size="large"
          />
        </div>
      </div>
      <template #footer>
        <div class="flex justify-end gap-3">
          <n-button @click="showPasswordModal = false">
            {{ t('profile.cancel') }}
          </n-button>
          <n-button type="primary" :loading="changingPassword" @click="changePassword">
            {{ t('profile.confirmChange') }}
          </n-button>
        </div>
      </template>
    </n-modal>

    <!-- 登录设备弹窗 -->
    <n-modal
      v-model:show="showDevicesModal"
      preset="card"
      :title="t('profile.loginDeviceManagement')"
      :class="isDark ? 'devices-modal-dark' : 'devices-modal-light'"
      style="width: 32rem; max-width: 90vw;"
    >
      <div class="space-y-3">
        <div
          v-for="device in devices"
          :key="device.id"
          class="flex items-center justify-between p-4 rounded-xl border"
          :class="[
            isDark ? 'border-[#374151]' : 'border-gray-200',
            device.current ? 'border-success/30 bg-success/5' : ''
          ]"
        >
          <div class="flex items-center gap-3">
            <div class="w-10 h-10 rounded-lg bg-gray-100 dark:bg-gray-700 flex items-center justify-center">
              <n-icon :component="device.type === 'mobile' ? PhonePortraitOutline : LaptopOutline" :size="18" class="text-text-secondary dark:text-text-secondary-dark" />
            </div>
            <div>
              <div class="flex items-center gap-2">
                <p class="text-sm font-medium text-text-primary dark:text-text-primary-dark">{{ device.name }}</p>
                <span v-if="device.current" class="px-1.5 py-0.5 rounded text-[10px] font-medium bg-success/10 text-success">
                  {{ t('profile.current') }}
                </span>
              </div>
              <p class="text-xs text-text-secondary dark:text-text-secondary-dark mt-0.5">
                {{ device.location }} · {{ device.time }}
              </p>
            </div>
          </div>
          <n-button
            v-if="!device.current"
            size="tiny"
            type="error"
            tertiary
            @click="logoutDevice(device.id)"
          >
            {{ t('profile.remove') }}
          </n-button>
        </div>
      </div>
    </n-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRouter } from 'vue-router';
import { useAppStore } from '@/stores/app';
import {
  PersonOutline,
  KeyOutline,
  ShieldOutline,
  CameraOutline,
  PhonePortraitOutline,
  LaptopOutline,
  TrashOutline,
  SettingsOutline,
  MailOutline,
  NotificationsOutline,
  BusinessOutline
} from '@vicons/ionicons5';
import { markRaw } from 'vue';
import { useMessage } from 'naive-ui';

const { t, locale } = useI18n();
const router = useRouter();
const appStore = useAppStore();
const message = useMessage();

const user = computed(() => appStore.user);
const isDark = computed(() => appStore.isDark);

const avatarInputRef = ref<HTMLInputElement | null>(null);
const saving = ref(false);
const savingPrefs = ref(false);
const changingPassword = ref(false);
const showPasswordModal = ref(false);
const showDevicesModal = ref(false);
const twoFactorEnabled = ref(false);

// 表单数据
const form = ref({
  username: user.value?.username || 'admin',
  nickname: user.value?.nickname || '',
  phone: '138****8888',
  email: 'admin@example.com',
  language: appStore.language,
  theme: isDark.value ? 'dark' : 'light'
});

// 密码表单
const passwordForm = ref({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
});

// 登录设备
const devices = ref([
  { id: 1, name: 'Windows Chrome', type: 'desktop', location: '广东省深圳市', time: '2024-01-15 09:30', current: true },
  { id: 2, name: 'iPhone Safari', type: 'mobile', location: '广东省深圳市', time: '2024-01-14 21:15', current: false },
]);

// 语言选项
const languageOptions = computed(() => [
  { label: t('common.chinese'), value: 'zh-CN' },
  { label: t('common.english'), value: 'en-US' }
]);

// 主题选项
const themeOptions = computed(() => [
  { label: t('profile.light'), value: 'light' },
  { label: t('profile.dark'), value: 'dark' },
  { label: t('profile.followSystem'), value: 'system' }
]);

// 通知偏好标签
const getNotificationLabel = (key: string) => {
  const labels: Record<string, string> = {
    security: t('messages.securityAlerts') || t('profile.securityAlerts'),
    email: t('messages.emailNotification') || t('profile.emailNotifications'),
    system: t('system.systemAnnouncements') || t('profile.systemAnnouncements'),
    activity: t('messages.activityAlerts') || t('profile.activityAlerts')
  };
  return labels[key] || key;
};

const notificationPrefs = ref([
  { key: 'security', icon: markRaw(ShieldOutline), enabled: true },
  { key: 'email', icon: markRaw(MailOutline), enabled: true },
  { key: 'system', icon: markRaw(SettingsOutline), enabled: false },
  { key: 'activity', icon: markRaw(NotificationsOutline), enabled: false },
]);

// 账户天数
const accountAge = computed(() => {
  return Math.floor(Math.random() * 200) + 30;
});

// 安全等级
const securityLevel = computed(() => {
  return twoFactorEnabled.value ? t('profile.high') : t('profile.medium');
});

const securityPercent = computed(() => {
  return twoFactorEnabled.value ? 90 : 60;
});

const securityColor = computed(() => {
  return twoFactorEnabled.value ? 'bg-success' : 'bg-secondary';
});

const securityTip = computed(() => {
  return twoFactorEnabled.value
    ? t('profile.securityExcellent')
    : t('profile.securityTip');
});

// 触发头像上传
const triggerAvatarUpload = () => {
  avatarInputRef.value?.click();
};

// 处理头像变更
const handleAvatarChange = (e: Event) => {
  const file = (e.target as HTMLInputElement).files?.[0];
  if (file) {
    const url = URL.createObjectURL(file);
    if (user.value) {
      appStore.setUser({ ...user.value, avatar: url });
    }
    message.success(t('profile.avatarUpdated'));
  }
};

// 保存基本信息
const saveBasicInfo = async () => {
  saving.value = true;
  try {
    // TODO: 调用API保存
    await new Promise(resolve => setTimeout(resolve, 800));
    if (user.value) {
      appStore.setUser({ ...user.value, nickname: form.value.nickname });
    }
    message.success(t('profile.basicInfoSaved'));
  } finally {
    saving.value = false;
  }
};

// 保存偏好设置
const savePreferences = async () => {
  savingPrefs.value = true;
  try {
    await new Promise(resolve => setTimeout(resolve, 600));
    message.success(t('profile.prefsSaved'));
  } finally {
    savingPrefs.value = false;
  }
};

// 语言切换
const handleLanguageChange = (lang: string) => {
  appStore.setLanguage(lang);
  locale.value = lang;
};

// 主题切换
const handleThemeChange = (theme: string) => {
  if (theme === 'dark') {
    appStore.setDarkTheme(true);
  } else if (theme === 'light') {
    appStore.setDarkTheme(false);
  } else {
    appStore.setDarkTheme(window.matchMedia('(prefers-color-scheme: dark)').matches);
  }
};

// 切换两步验证
const toggleTwoFactor = (enabled: boolean) => {
  twoFactorEnabled.value = enabled;
  if (enabled) {
    message.success(t('profile.twoFactorOn'));
  } else {
    message.warning(t('profile.twoFactorOff'));
  }
};

// 修改密码
const changePassword = async () => {
  if (!passwordForm.value.oldPassword) {
    message.error(t('profile.enterCurrentPassword'));
    return;
  }
  if (passwordForm.value.newPassword.length < 8) {
    message.error(t('profile.passwordMinLength'));
    return;
  }
  if (passwordForm.value.newPassword !== passwordForm.value.confirmPassword) {
    message.error(t('profile.passwordsNotMatch'));
    return;
  }
  changingPassword.value = true;
  try {
    await new Promise(resolve => setTimeout(resolve, 1000));
    showPasswordModal.value = false;
    passwordForm.value = { oldPassword: '', newPassword: '', confirmPassword: '' };
    message.success(t('profile.passwordChanged'));
  } finally {
    changingPassword.value = false;
  }
};

// 登出设备
const logoutDevice = (id: number) => {
  devices.value = devices.value.filter(d => d.id !== id);
  message.success(t('profile.deviceRemoved'));
};

onMounted(() => {
  if (user.value) {
    form.value.username = user.value.username;
    form.value.nickname = user.value.nickname || '';
  }
});
</script>

<style scoped>
@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}

@keyframes slideUp {
  from { opacity: 0; transform: translateY(20px); }
  to { opacity: 1; transform: translateY(0); }
}

.animate-fade-in {
  animation: fadeIn 0.5s ease-out;
}

.animate-slide-up {
  animation: slideUp 0.5s ease-out both;
}

:deep(.password-modal-light .n-card),
:deep(.devices-modal-light .n-card) {
  border-radius: 1rem;
}

:deep(.password-modal-dark .n-card),
:deep(.devices-modal-dark .n-card) {
  border-radius: 1rem;
}
</style>
