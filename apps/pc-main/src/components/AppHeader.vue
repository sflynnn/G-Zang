<template>
  <n-layout-header class="app-header">
    <div class="header-content">
      <!-- Logo -->
      <div class="logo">
        <n-icon size="32" color="#1890ff">
          <Wallet />
        </n-icon>
        <span class="logo-text">G-Zang 归藏</span>
      </div>

      <!-- 用户信息 -->
      <div class="user-info" v-if="authStore.isAuthenticated">
        <n-dropdown :render-option="renderDropdownOption" :options="userMenuOptions" @select="handleMenuSelect">
          <n-button text>
            <template #icon>
              <n-icon>
                <Person />
              </n-icon>
            </template>
            {{ authStore.nickname || authStore.username }}
          </n-button>
        </n-dropdown>
      </div>
    </div>
  </n-layout-header>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import { useRouter } from 'vue-router';
import {
  Wallet,
  Person
} from '@vicons/ionicons5';
import { useAuthStore } from '../stores/auth';

const router = useRouter();
const authStore = useAuthStore();

// 用户菜单选项
const userMenuOptions = [
  {
    label: '个人中心',
    key: 'profile'
  },
  {
    label: '设置',
    key: 'settings'
  },
  {
    type: 'divider'
  },
  {
    label: '退出登录',
    key: 'logout'
  }
];

// 渲染下拉选项
const renderDropdownOption = ({ node, option }: any) => {
  if (option.type === 'divider') {
    return <n-divider />;
  }
  return node;
};

// 处理菜单选择
const handleMenuSelect = (key: string) => {
  switch (key) {
    case 'profile':
      router.push('/profile');
      break;
    case 'settings':
      router.push('/settings');
      break;
    case 'logout':
      authStore.logout();
      router.push('/login');
      break;
  }
};
</script>

<style scoped>
.app-header {
  background-color: #fff;
  border-bottom: 1px solid #e8e8e8;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 64px;
  padding: 0 24px;
}

.logo {
  display: flex;
  align-items: center;
  gap: 12px;
}

.logo-text {
  font-size: 18px;
  font-weight: 600;
  color: #1890ff;
}

.user-info {
  display: flex;
  align-items: center;
}
</style>
