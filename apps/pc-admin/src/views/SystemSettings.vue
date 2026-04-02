<template>
  <div class="system-settings" :class="{ 'dark-theme': isDark }">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-content">
        <div class="header-text">
          <h1 class="page-title">{{ t('system.title') }}</h1>
          <p class="page-subtitle">{{ t('system.subtitle') }}</p>
        </div>
      </div>
    </div>

    <!-- 设置内容 -->
    <n-card class="settings-card" :bordered="false">
      <n-tabs type="line" animated placement="left" tab-style="min-width: 140px;">
        <!-- 基本设置 -->
        <n-tab-pane name="general" :tab="t('system.general')">
          <template #default>
            <div class="tab-content">
              <div class="section-header">
                <h3 class="section-title">{{ t('system.general') }}</h3>
                <p class="section-desc">{{ t('system.subtitle') }}</p>
              </div>

              <n-form
                ref="generalFormRef"
                :model="generalSettings"
                label-placement="left"
                label-width="160"
                require-mark-placement="right-hanging"
              >
                <n-form-item :label="t('system.systemName')" path="systemName">
                  <n-input 
                    v-model:value="generalSettings.systemName" 
                    :placeholder="t('system.systemName')"
                    class="setting-input"
                  />
                </n-form-item>

                <n-form-item :label="t('system.systemDescription')" path="systemDescription">
                  <n-input 
                    v-model:value="generalSettings.systemDescription" 
                    type="textarea"
                    :placeholder="t('system.systemDescription')"
                    :autosize="{ minRows: 2, maxRows: 4 }"
                    class="setting-input"
                  />
                </n-form-item>

                <n-form-item :label="t('system.copyright')" path="copyright">
                  <n-input 
                    v-model:value="generalSettings.copyright" 
                    :placeholder="t('system.copyright')"
                    class="setting-input"
                  />
                </n-form-item>

                <n-form-item>
                  <n-space>
                    <n-button type="primary" @click="saveGeneralSettings">
                      {{ t('common.save') }}
                    </n-button>
                    <n-button @click="resetGeneralSettings">
                      {{ t('common.reset') }}
                    </n-button>
                  </n-space>
                </n-form-item>
              </n-form>
            </div>
          </template>
        </n-tab-pane>

        <!-- 安全设置 -->
        <n-tab-pane name="security" :tab="t('system.security')">
          <template #default>
            <div class="tab-content">
              <div class="section-header">
                <h3 class="section-title">{{ t('system.security') }}</h3>
                <p class="section-desc">{{ t('system.passwordPolicy') }}</p>
              </div>

              <n-form
                ref="securityFormRef"
                :model="securitySettings"
                label-placement="left"
                label-width="200"
                require-mark-placement="right-hanging"
              >
                <n-form-item :label="t('system.passwordMinLength')" path="passwordMinLength">
                  <n-input-number 
                    v-model:value="securitySettings.passwordMinLength" 
                    :min="6"
                    :max="32"
                    class="setting-input-number"
                  />
                </n-form-item>

                <n-form-item :label="t('system.passwordRequireUppercase')" path="passwordRequireUppercase">
                  <n-switch v-model:value="securitySettings.passwordRequireUppercase" />
                </n-form-item>

                <n-form-item :label="t('system.passwordRequireLowercase')" path="passwordRequireLowercase">
                  <n-switch v-model:value="securitySettings.passwordRequireLowercase" />
                </n-form-item>

                <n-form-item :label="t('system.passwordRequireNumber')" path="passwordRequireNumber">
                  <n-switch v-model:value="securitySettings.passwordRequireNumber" />
                </n-form-item>

                <n-form-item :label="t('system.passwordRequireSpecial')" path="passwordRequireSpecial">
                  <n-switch v-model:value="securitySettings.passwordRequireSpecial" />
                </n-form-item>

                <n-divider />

                <n-form-item :label="t('system.sessionTimeout')" path="sessionTimeout">
                  <n-input-number 
                    v-model:value="securitySettings.sessionTimeout" 
                    :min="5"
                    :max="1440"
                    class="setting-input-number"
                  >
                    <template #suffix>min</template>
                  </n-input-number>
                </n-form-item>

                <n-form-item :label="t('system.loginRetryLimit')" path="loginRetryLimit">
                  <n-input-number 
                    v-model:value="securitySettings.loginRetryLimit" 
                    :min="1"
                    :max="10"
                    class="setting-input-number"
                  />
                </n-form-item>

                <n-form-item :label="t('system.lockDuration')" path="lockDuration">
                  <n-input-number 
                    v-model:value="securitySettings.lockDuration" 
                    :min="5"
                    :max="60"
                    class="setting-input-number"
                  >
                    <template #suffix>min</template>
                  </n-input-number>
                </n-form-item>

                <n-form-item>
                  <n-space>
                    <n-button type="primary" @click="saveSecuritySettings">
                      {{ t('common.save') }}
                    </n-button>
                    <n-button @click="resetSecuritySettings">
                      {{ t('common.reset') }}
                    </n-button>
                  </n-space>
                </n-form-item>
              </n-form>
            </div>
          </template>
        </n-tab-pane>

        <!-- 通知设置 -->
        <n-tab-pane name="notification" :tab="t('system.notification')">
          <template #default>
            <div class="tab-content">
              <div class="section-header">
                <h3 class="section-title">{{ t('system.notification') }}</h3>
                <p class="section-desc">{{ t('system.notificationTypes') }}</p>
              </div>

              <n-form
                ref="notificationFormRef"
                :model="notificationSettings"
                label-placement="left"
                label-width="160"
                require-mark-placement="right-hanging"
              >
                <n-form-item :label="t('system.emailNotification')" path="emailNotification">
                  <n-switch v-model:value="notificationSettings.emailNotification" />
                </n-form-item>

                <n-form-item :label="t('system.smsNotification')" path="smsNotification">
                  <n-switch v-model:value="notificationSettings.smsNotification" />
                </n-form-item>

                <n-form-item :label="t('system.inAppNotification')" path="inAppNotification">
                  <n-switch v-model:value="notificationSettings.inAppNotification" />
                </n-form-item>

                <n-divider />

                <n-form-item :label="'SMTP Host'" path="smtpHost">
                  <n-input 
                    v-model:value="notificationSettings.smtpHost" 
                    :placeholder="'smtp.example.com'"
                    class="setting-input"
                  />
                </n-form-item>

                <n-form-item :label="'SMTP Port'" path="smtpPort">
                  <n-input-number 
                    v-model:value="notificationSettings.smtpPort" 
                    :min="1"
                    :max="65535"
                    class="setting-input-number"
                  />
                </n-form-item>

                <n-form-item :label="'SMS Provider'" path="smsProvider">
                  <n-select 
                    v-model:value="notificationSettings.smsProvider"
                    :options="smsProviderOptions"
                    class="setting-input"
                  />
                </n-form-item>

                <n-form-item>
                  <n-space>
                    <n-button type="primary" @click="saveNotificationSettings">
                      {{ t('common.save') }}
                    </n-button>
                    <n-button @click="resetNotificationSettings">
                      {{ t('common.reset') }}
                    </n-button>
                  </n-space>
                </n-form-item>
              </n-form>
            </div>
          </template>
        </n-tab-pane>

        <!-- 主题设置 -->
        <n-tab-pane name="appearance" :tab="t('system.appearance')">
          <template #default>
            <div class="tab-content">
              <div class="section-header">
                <h3 class="section-title">{{ t('system.appearance') }}</h3>
                <p class="section-desc">{{ t('system.theme') }}</p>
              </div>

              <n-form
                ref="appearanceFormRef"
                :model="appearanceSettings"
                label-placement="left"
                label-width="120"
                require-mark-placement="right-hanging"
              >
                <n-form-item :label="t('system.theme')" path="theme">
                  <n-radio-group v-model:value="appearanceSettings.theme" name="theme" @update:value="handleThemeChange">
                    <n-space vertical>
                      <n-radio value="light">
                        <div class="theme-option">
                          <div class="theme-preview theme-preview-light">
                            <n-icon :component="SunnyOutline" :size="20" />
                          </div>
                          <span>{{ t('system.themeLight') }}</span>
                        </div>
                      </n-radio>
                      <n-radio value="dark">
                        <div class="theme-option">
                          <div class="theme-preview theme-preview-dark">
                            <n-icon :component="MoonOutline" :size="20" />
                          </div>
                          <span>{{ t('system.themeDark') }}</span>
                        </div>
                      </n-radio>
                      <n-radio value="auto">
                        <div class="theme-option">
                          <div class="theme-preview theme-preview-auto">
                            <n-icon :component="DesktopOutline" :size="20" />
                          </div>
                          <span>{{ t('system.themeAuto') }}</span>
                        </div>
                      </n-radio>
                    </n-space>
                  </n-radio-group>
                </n-form-item>

                <n-divider />

                <n-form-item :label="t('system.primaryColor')" path="primaryColor">
                  <div class="color-picker-wrapper">
                    <n-color-picker 
                      v-model:value="appearanceSettings.primaryColor"
                      :swatches="colorSwatches"
                      :show-alpha="false"
                      :modes="['hex']"
                      size="medium"
                      :show-preview="true"
                    />
                  </div>
                </n-form-item>

                <n-form-item>
                  <n-space>
                    <n-button type="primary" @click="saveAppearanceSettings">
                      {{ t('common.save') }}
                    </n-button>
                    <n-button @click="resetAppearanceSettings">
                      {{ t('common.reset') }}
                    </n-button>
                  </n-space>
                </n-form-item>
              </n-form>
            </div>
          </template>
        </n-tab-pane>

        <!-- 语言设置 -->
        <n-tab-pane name="language" :tab="t('system.language')">
          <template #default>
            <div class="tab-content">
              <div class="section-header">
                <h3 class="section-title">{{ t('system.language') }}</h3>
                <p class="section-desc">{{ t('system.defaultLanguage') }}</p>
              </div>

              <n-form
                ref="languageFormRef"
                :model="languageSettings"
                label-placement="left"
                label-width="160"
                require-mark-placement="right-hanging"
              >
                <n-form-item :label="t('system.defaultLanguage')" path="defaultLanguage">
                  <n-select 
                    v-model:value="languageSettings.defaultLanguage"
                    :options="languageOptions"
                    class="setting-input"
                  />
                </n-form-item>

                <n-form-item :label="t('system.availableLanguages')" path="availableLanguages">
                  <n-checkbox-group v-model:value="languageSettings.availableLanguages">
                    <n-space vertical>
                      <n-checkbox value="zh-CN" :label="t('system.chinese')" />
                      <n-checkbox value="en-US" :label="t('system.english')" />
                    </n-space>
                  </n-checkbox-group>
                </n-form-item>

                <n-form-item>
                  <n-space>
                    <n-button type="primary" @click="saveLanguageSettings">
                      {{ t('common.save') }}
                    </n-button>
                    <n-button @click="resetLanguageSettings">
                      {{ t('common.reset') }}
                    </n-button>
                  </n-space>
                </n-form-item>
              </n-form>
            </div>
          </template>
        </n-tab-pane>
      </n-tabs>
    </n-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, watch, h } from 'vue';
import { useI18n } from 'vue-i18n';
import {
  SunnyOutline,
  MoonOutline,
  DesktopOutline
} from '@vicons/ionicons5';
import { useAppStore } from '@/stores/app';

const { t, locale } = useI18n();
const appStore = useAppStore();

const isDark = computed(() => appStore.isDark);

// 表单引用
const generalFormRef = ref<any>(null);
const securityFormRef = ref<any>(null);
const notificationFormRef = ref<any>(null);
const appearanceFormRef = ref<any>(null);
const languageFormRef = ref<any>(null);

// 基本设置
const generalSettings = reactive({
  systemName: 'G-Zang 管理后台',
  systemDescription: '企业级智能财务管理系统',
  copyright: '© 2026 G-Zang 财务管理系统'
});

// 安全设置
const securitySettings = reactive({
  passwordMinLength: 8,
  passwordRequireUppercase: true,
  passwordRequireLowercase: true,
  passwordRequireNumber: true,
  passwordRequireSpecial: false,
  sessionTimeout: 120,
  loginRetryLimit: 5,
  lockDuration: 30
});

// 通知设置
const notificationSettings = reactive({
  emailNotification: true,
  smsNotification: false,
  inAppNotification: true,
  smtpHost: 'smtp.example.com',
  smtpPort: 587,
  smsProvider: 'aliyun'
});

// 主题设置
const appearanceSettings = reactive({
  theme: appStore.themeMode,
  primaryColor: '#0F4C5C'
});

watch(() => appStore.themeMode, (newMode) => {
  appearanceSettings.theme = newMode;
});

const handleThemeChange = (value: 'light' | 'dark' | 'auto') => {
  appStore.setThemeMode(value);
  appearanceSettings.theme = value;
};

// 语言设置
const languageSettings = reactive({
  defaultLanguage: computed({
    get: () => appStore.language || 'zh-CN',
    set: (val: string) => {
      appStore.setLanguage(val);
      locale.value = val;
    }
  }),
  availableLanguages: ['zh-CN', 'en-US']
});

const smsProviderOptions = computed(() => [
  { label: t('system.smsAliyun'), value: 'aliyun' },
  { label: t('system.smsTencent'), value: 'tencent' },
  { label: t('system.smsHuawei'), value: 'huawei' }
]);

// 语言选项
const languageOptions = computed(() => [
  { label: t('system.chinese'), value: 'zh-CN' },
  { label: t('system.english'), value: 'en-US' }
]);

// 颜色选择器预设
const colorSwatches = [
  '#0F4C5C',
  '#186a7d',
  '#FB8B24',
  '#06D6A0',
  '#EF476F',
  '#FFD166',
  '#118AB2'
];

// 保存设置
const saveGeneralSettings = () => {
  window.$message.success(t('messages.saveSuccess'));
  localStorage.setItem('settings_general', JSON.stringify(generalSettings));
};

const saveSecuritySettings = () => {
  window.$message.success(t('messages.saveSuccess'));
  localStorage.setItem('settings_security', JSON.stringify(securitySettings));
};

const saveNotificationSettings = () => {
  window.$message.success(t('messages.saveSuccess'));
  localStorage.setItem('settings_notification', JSON.stringify(notificationSettings));
};

const saveAppearanceSettings = () => {
  window.$message.success(t('messages.saveSuccess'));
  localStorage.setItem('settings_appearance', JSON.stringify({
    primaryColor: appearanceSettings.primaryColor
  }));
};

const saveLanguageSettings = () => {
  window.$message.success(t('messages.saveSuccess'));
  localStorage.setItem('settings_language', JSON.stringify({
    defaultLanguage: languageSettings.defaultLanguage,
    availableLanguages: languageSettings.availableLanguages
  }));
};

// 重置设置
const resetGeneralSettings = () => {
  generalSettings.systemName = 'G-Zang 管理后台';
  generalSettings.systemDescription = '企业级智能财务管理系统';
  generalSettings.copyright = '© 2026 G-Zang 财务管理系统';
};

const resetSecuritySettings = () => {
  securitySettings.passwordMinLength = 8;
  securitySettings.passwordRequireUppercase = true;
  securitySettings.passwordRequireLowercase = true;
  securitySettings.passwordRequireNumber = true;
  securitySettings.passwordRequireSpecial = false;
  securitySettings.sessionTimeout = 120;
  securitySettings.loginRetryLimit = 5;
  securitySettings.lockDuration = 30;
};

const resetNotificationSettings = () => {
  notificationSettings.emailNotification = true;
  notificationSettings.smsNotification = false;
  notificationSettings.inAppNotification = true;
  notificationSettings.smtpHost = 'smtp.example.com';
  notificationSettings.smtpPort = 587;
  notificationSettings.smsProvider = 'aliyun';
};

const resetAppearanceSettings = () => {
  appearanceSettings.primaryColor = '#0F4C5C';
};

const resetLanguageSettings = () => {
  languageSettings.defaultLanguage = 'zh-CN';
  languageSettings.availableLanguages = ['zh-CN', 'en-US'];
};

// 加载已保存的设置
const loadSettings = () => {
  try {
    const savedGeneral = localStorage.getItem('settings_general');
    if (savedGeneral) {
      Object.assign(generalSettings, JSON.parse(savedGeneral));
    }

    const savedSecurity = localStorage.getItem('settings_security');
    if (savedSecurity) {
      Object.assign(securitySettings, JSON.parse(savedSecurity));
    }

    const savedNotification = localStorage.getItem('settings_notification');
    if (savedNotification) {
      Object.assign(notificationSettings, JSON.parse(savedNotification));
    }

    const savedAppearance = localStorage.getItem('settings_appearance');
    if (savedAppearance) {
      const parsed = JSON.parse(savedAppearance);
      if (parsed.primaryColor) {
        appearanceSettings.primaryColor = parsed.primaryColor;
      }
    }

    const savedLanguage = localStorage.getItem('settings_language');
    if (savedLanguage) {
      const parsed = JSON.parse(savedLanguage);
      if (parsed.defaultLanguage) {
        languageSettings.defaultLanguage = parsed.defaultLanguage;
      }
      if (parsed.availableLanguages) {
        languageSettings.availableLanguages = parsed.availableLanguages;
      }
    }
  } catch (e) {
    console.error('Failed to load settings:', e);
  }
};

loadSettings();
</script>

<style scoped>
.system-settings {
  padding: 24px;
  min-height: 100%;
  background: linear-gradient(180deg, #F8F9FA 0%, #FFFFFF 100%);
}

.dark-theme {
  background: linear-gradient(180deg, #111827 0%, #1F2937 100%);
}

/* 页面头部 */
.page-header {
  margin-bottom: 24px;
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
}

.header-text {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.page-title {
  font-size: 24px;
  font-weight: 600;
  color: #1F2937;
  margin: 0;
}

.dark-theme .page-title {
  color: #F9FAFB;
}

.page-subtitle {
  font-size: 14px;
  color: #6B7280;
  margin: 0;
}

.dark-theme .page-subtitle {
  color: #9CA3AF;
}

/* 设置卡片 */
.settings-card {
  border-radius: 12px;
}

/* Tab内容 */
.tab-content {
  padding: 24px;
  max-width: 720px;
}

.section-header {
  margin-bottom: 32px;
}

.section-title {
  font-size: 20px;
  font-weight: 600;
  color: #1F2937;
  margin: 0 0 8px 0;
}

.dark-theme .section-title {
  color: #F9FAFB;
}

.section-desc {
  font-size: 14px;
  color: #6B7280;
  margin: 0;
}

.dark-theme .section-desc {
  color: #9CA3AF;
}

/* 设置输入框 */
.setting-input {
  max-width: 400px;
}

.setting-input-number {
  width: 200px;
}

/* 主题选项 */
.theme-option {
  display: flex;
  align-items: center;
  gap: 12px;
}

.theme-preview {
  width: 48px;
  height: 32px;
  border-radius: 6px;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 2px solid transparent;
  transition: all 0.2s ease;
}

.theme-preview-light {
  background: linear-gradient(135deg, #FFFFFF 0%, #F8F9FA 100%);
  border-color: #E5E7EB;
  color: #1F2937;
}

.theme-preview-dark {
  background: linear-gradient(135deg, #1F2937 0%, #111827 100%);
  border-color: #374151;
  color: #F9FAFB;
}

.theme-preview-auto {
  background: linear-gradient(135deg, #FFFFFF 50%, #1F2937 50%);
  border-color: #E5E7EB;
  color: #9CA3AF;
}

.dark-theme .theme-preview-light {
  background: linear-gradient(135deg, #374151 0%, #252D3B 100%);
  border-color: #4B5563;
}

.dark-theme .theme-preview-dark {
  background: linear-gradient(135deg, #1F2937 0%, #111827 100%);
  border-color: #4B5563;
}

.dark-theme .theme-preview-auto {
  background: linear-gradient(135deg, #374151 50%, #1F2937 50%);
  border-color: #4B5563;
}

/* 颜色选择器 */
.color-picker-wrapper {
  min-width: 280px;
}

/* Radio组样式 */
:deep(.n-radio-group) {
  width: 100%;
}

:deep(.n-radio) {
  margin-bottom: 8px;
}
</style>
