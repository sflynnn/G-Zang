<template>
  <div class="system-settings">
    <div class="page-header">
      <div class="page-title">
        <h2>系统设置</h2>
        <p>管理系统配置和参数</p>
      </div>
    </div>

    <n-space vertical size="large">
      <!-- 基础设置 -->
      <n-card title="基础设置">
        <n-form :model="basicSettings" label-placement="left" label-width="120">
          <n-form-item label="系统名称">
            <n-input v-model:value="basicSettings.systemName" />
          </n-form-item>

          <n-form-item label="系统描述">
            <n-input
              v-model:value="basicSettings.systemDescription"
              type="textarea"
              :autosize="{ minRows: 3, maxRows: 6 }"
            />
          </n-form-item>

          <n-form-item label="默认语言">
            <n-select
              v-model:value="basicSettings.defaultLanguage"
              :options="languageOptions"
            />
          </n-form-item>

          <n-form-item label="时区">
            <n-select
              v-model:value="basicSettings.timezone"
              :options="timezoneOptions"
            />
          </n-form-item>

          <n-form-item>
            <n-space>
              <n-button type="primary" @click="saveBasicSettings">
                保存设置
              </n-button>
              <n-button @click="resetBasicSettings">
                重置
              </n-button>
            </n-space>
          </n-form-item>
        </n-form>
      </n-card>

      <!-- 安全设置 -->
      <n-card title="安全设置">
        <n-form :model="securitySettings" label-placement="left" label-width="120">
          <n-form-item label="密码最小长度">
            <n-input-number
              v-model:value="securitySettings.minPasswordLength"
              :min="6"
              :max="32"
            />
          </n-form-item>

          <n-form-item label="密码复杂度要求">
            <n-space>
              <n-checkbox v-model:checked="securitySettings.requireUppercase">
                大写字母
              </n-checkbox>
              <n-checkbox v-model:checked="securitySettings.requireLowercase">
                小写字母
              </n-checkbox>
              <n-checkbox v-model:checked="securitySettings.requireNumbers">
                数字
              </n-checkbox>
              <n-checkbox v-model:checked="securitySettings.requireSymbols">
                特殊字符
              </n-checkbox>
            </n-space>
          </n-form-item>

          <n-form-item label="登录失败锁定">
            <n-space>
              <n-input-number
                v-model:value="securitySettings.maxLoginAttempts"
                :min="1"
                :max="10"
                style="width: 100px"
              />
              <span>次失败后锁定</span>
              <n-input-number
                v-model:value="securitySettings.lockDuration"
                :min="1"
                :max="1440"
                style="width: 100px"
              />
              <span>分钟</span>
            </n-space>
          </n-form-item>

          <n-form-item label="会话超时时间">
            <n-space>
              <n-input-number
                v-model:value="securitySettings.sessionTimeout"
                :min="15"
                :max="480"
                style="width: 100px"
              />
              <span>分钟</span>
            </n-space>
          </n-form-item>

          <n-form-item>
            <n-space>
              <n-button type="primary" @click="saveSecuritySettings">
                保存设置
              </n-button>
              <n-button @click="resetSecuritySettings">
                重置
              </n-button>
            </n-space>
          </n-form-item>
        </n-form>
      </n-card>

      <!-- 邮件设置 -->
      <n-card title="邮件设置">
        <n-form :model="emailSettings" label-placement="left" label-width="120">
          <n-form-item label="SMTP服务器">
            <n-input v-model:value="emailSettings.smtpHost" />
          </n-form-item>

          <n-form-item label="SMTP端口">
            <n-input-number
              v-model:value="emailSettings.smtpPort"
              :min="1"
              :max="65535"
            />
          </n-form-item>

          <n-form-item label="用户名">
            <n-input v-model:value="emailSettings.username" />
          </n-form-item>

          <n-form-item label="密码">
            <n-input
              v-model:value="emailSettings.password"
              type="password"
            />
          </n-form-item>

          <n-form-item label="发件人邮箱">
            <n-input v-model:value="emailSettings.fromEmail" />
          </n-form-item>

          <n-form-item label="启用SSL">
            <n-switch v-model:value="emailSettings.enableSSL" />
          </n-form-item>

          <n-form-item>
            <n-space>
              <n-button type="primary" @click="saveEmailSettings">
                保存设置
              </n-button>
              <n-button @click="resetEmailSettings">
                重置
              </n-button>
              <n-button @click="testEmailSettings">
                测试连接
              </n-button>
            </n-space>
          </n-form-item>
        </n-form>
      </n-card>

      <!-- 系统维护 -->
      <n-card title="系统维护">
        <n-space vertical>
          <n-alert type="warning">
            <template #header>
              注意：以下操作可能会影响系统正常运行，请谨慎操作！
            </template>
          </n-alert>

          <n-space>
            <n-button type="warning" @click="clearCache">
              清理缓存
            </n-button>
            <n-button type="error" @click="backupDatabase">
              备份数据库
            </n-button>
            <n-button type="info" @click="exportLogs">
              导出日志
            </n-button>
          </n-space>
        </n-space>
      </n-card>
    </n-space>
  </div>
</template>

<script setup lang="ts">
import { reactive } from 'vue';

// 基础设置
const basicSettings = reactive({
  systemName: 'G-Zang 管理系统',
  systemDescription: 'G-Zang (归藏) 财务管理系统',
  defaultLanguage: 'zh-CN',
  timezone: 'Asia/Shanghai'
});

// 安全设置
const securitySettings = reactive({
  minPasswordLength: 8,
  requireUppercase: true,
  requireLowercase: true,
  requireNumbers: true,
  requireSymbols: false,
  maxLoginAttempts: 5,
  lockDuration: 30,
  sessionTimeout: 120
});

// 邮件设置
const emailSettings = reactive({
  smtpHost: '',
  smtpPort: 587,
  username: '',
  password: '',
  fromEmail: '',
  enableSSL: true
});

// 语言选项
const languageOptions = [
  { label: '中文(简体)', value: 'zh-CN' },
  { label: 'English', value: 'en-US' }
];

// 时区选项
const timezoneOptions = [
  { label: '东八区 (Asia/Shanghai)', value: 'Asia/Shanghai' },
  { label: '北京时间 (Asia/Beijing)', value: 'Asia/Beijing' }
];

// 方法
const saveBasicSettings = () => {
  try {
    console.log('保存基础设置:', basicSettings);
    // TODO: 调用API保存设置
    window.$message?.success('基础设置保存成功');
  } catch (error) {
    console.error('保存基础设置失败:', error);
    window.$message?.error('保存基础设置失败');
  }
};

const resetBasicSettings = () => {
  Object.assign(basicSettings, {
    systemName: 'G-Zang 管理系统',
    systemDescription: 'G-Zang (归藏) 财务管理系统',
    defaultLanguage: 'zh-CN',
    timezone: 'Asia/Shanghai'
  });
};

const saveSecuritySettings = () => {
  try {
    console.log('保存安全设置:', securitySettings);
    // TODO: 调用API保存设置
    window.$message?.success('安全设置保存成功');
  } catch (error) {
    console.error('保存安全设置失败:', error);
    window.$message?.error('保存安全设置失败');
  }
};

const resetSecuritySettings = () => {
  Object.assign(securitySettings, {
    minPasswordLength: 8,
    requireUppercase: true,
    requireLowercase: true,
    requireNumbers: true,
    requireSymbols: false,
    maxLoginAttempts: 5,
    lockDuration: 30,
    sessionTimeout: 120
  });
};

const saveEmailSettings = () => {
  try {
    console.log('保存邮件设置:', emailSettings);
    // TODO: 调用API保存设置
    window.$message?.success('邮件设置保存成功');
  } catch (error) {
    console.error('保存邮件设置失败:', error);
    window.$message?.error('保存邮件设置失败');
  }
};

const resetEmailSettings = () => {
  Object.assign(emailSettings, {
    smtpHost: '',
    smtpPort: 587,
    username: '',
    password: '',
    fromEmail: '',
    enableSSL: true
  });
};

const testEmailSettings = () => {
  try {
    console.log('测试邮件连接');
    // TODO: 调用API测试邮件连接
    window.$message?.success('邮件连接测试成功');
  } catch (error) {
    console.error('邮件连接测试失败:', error);
    window.$message?.error('邮件连接测试失败');
  }
};

const clearCache = () => {
  try {
    console.log('清理系统缓存');
    // TODO: 调用API清理缓存
    window.$message?.success('系统缓存清理成功');
  } catch (error) {
    console.error('清理系统缓存失败:', error);
    window.$message?.error('清理系统缓存失败');
  }
};

const backupDatabase = () => {
  try {
    console.log('备份数据库');
    // TODO: 调用API备份数据库
    window.$message?.success('数据库备份成功');
  } catch (error) {
    console.error('数据库备份失败:', error);
    window.$message?.error('数据库备份失败');
  }
};

const exportLogs = () => {
  try {
    console.log('导出系统日志');
    // TODO: 调用API导出日志
    window.$message?.success('系统日志导出成功');
  } catch (error) {
    console.error('导出系统日志失败:', error);
    window.$message?.error('导出系统日志失败');
  }
};
</script>

<style scoped>
.system-settings {
  padding: 1rem;
}

.page-header {
  margin-bottom: 2rem;
}

.page-title h2 {
  font-size: 1.5rem;
  font-weight: 600;
  margin-bottom: 0.5rem;
  color: var(--n-text-color);
}

.page-title p {
  color: var(--n-text-color-3);
  font-size: 0.9rem;
}
</style>
