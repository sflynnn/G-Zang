<template>
  <n-layout-sider
    :collapsed="collapsed"
    :collapsed-width="64"
    :width="240"
    show-trigger="bar"
    collapse-mode="width"
    @collapse="collapsed = true"
    @expand="collapsed = false"
  >
    <n-menu
      :collapsed="collapsed"
      :collapsed-width="64"
      :collapsed-icon-size="22"
      :options="menuOptions"
      v-model:value="activeKey"
      @update:value="handleMenuUpdate"
    />
  </n-layout-sider>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import {
  HomeOutline,
  PersonOutline,
  BusinessOutline,
  SettingsOutline
} from '@vicons/ionicons5';
import { useAuthStore } from '../stores/auth';

const router = useRouter();
const route = useRoute();
const authStore = useAuthStore();

// 侧边栏折叠状态
const collapsed = ref(false);

// 当前激活的菜单项
const activeKey = ref(route.path);

// 菜单选项
const menuOptions = computed(() => [
  {
    label: '首页',
    key: '/home',
    icon: () => h(HomeOutline)
  },
  {
    label: '个人记账',
    key: '/personal',
    icon: () => h(PersonOutline),
    disabled: !authStore.isAuthenticated
  },
  {
    label: '企业管理',
    key: '/business',
    icon: () => h(BusinessOutline),
    disabled: !authStore.isAuthenticated || !authStore.isCompanyUser
  },
  {
    label: '系统管理',
    key: '/admin',
    icon: () => h(SettingsOutline),
    disabled: !authStore.isAuthenticated || authStore.roleId !== 1 // 只有管理员能访问
  }
]);

// 处理菜单更新
const handleMenuUpdate = (key: string) => {
  router.push(key);
};
</script>

<style scoped>
:deep(.n-layout-sider) {
  background-color: #fff;
  border-right: 1px solid #e8e8e8;
}
</style>
