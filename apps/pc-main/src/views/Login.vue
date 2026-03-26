<template>
  <div class="login-container">
    <n-card class="login-card" title="登录 G-Zang 归藏">
      <n-form
        ref="formRef"
        :model="formData"
        :rules="rules"
        label-placement="top"
        @submit.prevent="handleSubmit"
      >
        <n-form-item label="用户名" path="username">
          <n-input
            v-model:value="formData.username"
            placeholder="请输入用户名或邮箱"
            :disabled="loading"
          >
            <template #prefix>
              <n-icon>
                <Person />
              </n-icon>
            </template>
          </n-input>
        </n-form-item>

        <n-form-item label="密码" path="password">
          <n-input
            v-model:value="formData.password"
            type="password"
            placeholder="请输入密码"
            show-password-on="click"
            :disabled="loading"
          >
            <template #prefix>
              <n-icon>
                <LockClosed />
              </n-icon>
            </template>
          </n-input>
        </n-form-item>

        <n-form-item>
          <n-space justify="space-between">
            <n-checkbox v-model:checked="rememberMe">
              记住我
            </n-checkbox>
            <n-button text type="primary" size="small">
              忘记密码？
            </n-button>
          </n-space>
        </n-form-item>

        <n-form-item>
          <n-space vertical>
            <n-button
              type="primary"
              attr-type="submit"
              block
              :loading="loading"
              size="large"
            >
              登录
            </n-button>
            <n-button
              block
              secondary
              size="large"
              @click="goToRegister"
            >
              注册新账户
            </n-button>
          </n-space>
        </n-form-item>
      </n-form>
    </n-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue';
import { useRouter } from 'vue-router';
import { Person, LockClosed } from '@vicons/ionicons5';
import { useAuthStore } from '../stores/auth';

const router = useRouter();
const authStore = useAuthStore();

// 表单引用
const formRef = ref();

// 表单数据
const formData = reactive({
  username: '',
  password: ''
});

// 记住我
const rememberMe = ref(false);

// 加载状态
const loading = ref(false);

// 表单验证规则
const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' }
  ]
};

// 处理登录
const handleSubmit = async () => {
  try {
    await formRef.value?.validate();

    loading.value = true;

    const result = await authStore.login(formData.username, formData.password);

    if (result.success) {
      // 登录成功，跳转到首页
      router.push('/home');
    } else {
      // 登录失败，显示错误信息
      window.$message?.error(result.message || '登录失败');
    }
  } catch (error) {
    console.error('表单验证失败:', error);
  } finally {
    loading.value = false;
  }
};

// 跳转到注册页面
const goToRegister = () => {
  router.push('/register');
};
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
}

.login-card {
  width: 100%;
  max-width: 400px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
}
</style>
