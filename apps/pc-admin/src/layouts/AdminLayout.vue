<template>
  <n-layout style="height: 100vh" has-sider>
    <!-- 侧边栏 -->
    <n-layout-sider
      :collapsed="collapsed"
      :collapsed-width="64"
      :width="240"
      :show-trigger="false"
      collapse-mode="width"
      bordered
    >
      <!-- Logo区域 -->
      <div class="logo">
        <n-avatar
          size="small"
          src="/logo.png"
        />
        <span v-if="!collapsed" class="logo-text">{{ $t('common.adminPanel') }}</span>
      </div>

      <!-- 菜单 -->
      <n-menu
        :collapsed="collapsed"
        :collapsed-icon-size="22"
        :options="menuOptions"
        :value="activeKey"
        @update:value="handleMenuClick"
      />
    </n-layout-sider>

    <!-- 主内容区域 -->
    <n-layout>
      <!-- 头部 -->
      <n-layout-header bordered>
        <div class="header">
          <div class="header-left">
            <n-button
              text
              @click="toggleSidebar"
              :title="$t('common.toggleSidebar')"
              style="font-size: 18px"
            >
              <template #icon>
                <n-icon :component="MenuOutline" />
              </template>
            </n-button>
          </div>

          <div class="header-right">
            <n-space>
              <!-- 语言切换 -->
              <n-dropdown
                :options="languageOptions"
                @select="handleLanguageChange"
                trigger="click"
              >
                <n-button text :title="$t('common.language')">
                  <template #icon>
                    <n-icon :component="LanguageOutline" />
                  </template>
                </n-button>
              </n-dropdown>

              <!-- 主题切换 -->
              <n-button
                text
                @click="toggleTheme"
                :title="$t('common.toggleTheme')"
              >
                <template #icon>
                  <n-icon :component="isDark ? SunnyOutline : MoonOutline" />
                </template>
              </n-button>

              <!-- 用户菜单 -->
              <n-dropdown
                :options="userMenuOptions"
                @select="handleUserMenuClick"
              >
                <n-button text>
                  <n-avatar
                    size="small"
                    src="https://gw.alipayobjects.com/zos/rmsportal/BiazfanxmamNRoxxVxka.png"
                  />
                  <span v-if="!collapsed">{{ (user as any)?.nickname || (user as any)?.username }}</span>
                  <template #icon>
                    <n-icon :component="ChevronDownOutline" />
                  </template>
                </n-button>
              </n-dropdown>
            </n-space>
          </div>
        </div>
      </n-layout-header>

      <!-- 内容区域 -->
      <n-layout-content>
        <div class="content">
          <router-view />
        </div>
      </n-layout-content>
    </n-layout>
  </n-layout>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { useI18n } from 'vue-i18n';
import { MenuOutline, ChevronDownOutline, SunnyOutline, MoonOutline, LanguageOutline } from '@vicons/ionicons5';
import { useAppStore } from '@/stores/app';

const router = useRouter();
const route = useRoute();
const appStore = useAppStore();
const { t, locale } = useI18n();

// 响应式数据
const collapsed = computed(() => appStore.collapsed);
const isDark = computed(() => appStore.isDark);
const language = computed(() => appStore.language);
const user = computed(() => appStore.user);

// 当前激活的菜单项
const activeKey = ref(route.name as string);

  // 完整的菜单配置
  const allMenuOptions = [
    {
      label: t('common.dashboard'),
      key: 'Dashboard'
    },
    {
      label: t('common.userManagement'),
      key: 'UserManagement',
      permission: 'USER_MANAGE'
    },
    {
      label: t('common.roleManagement'),
      key: 'RoleManagement',
      permission: 'ROLE_MANAGE'
    },
    {
      label: t('common.companyManagement'),
      key: 'CompanyManagement',
      permission: 'COMPANY_MANAGE'
    },
    {
      label: t('common.permissionManagement'),
      key: 'PermissionManagement',
      role: 'SUPER_ADMIN'
    },
    {
      label: t('common.systemSettings'),
      key: 'SystemSettings',
      role: 'SUPER_ADMIN'
    }
  ];

  // 菜单选项 - 根据权限动态过滤
  const menuOptions = computed(() => {
    return allMenuOptions.filter(menu => {
      // 如果菜单需要权限，检查用户是否有该权限
      if (menu.permission) {
        return appStore.checkPermission(menu.permission);
      }

      // 如果菜单需要角色，检查用户是否有该角色
      if (menu.role) {
        return appStore.checkRole(menu.role);
      }

      // 默认显示
      return true;
    });
  });

// 用户菜单选项
const userMenuOptions = computed(() => [
  {
    label: t('common.profile'),
    key: 'profile'
  },
  {
    label: t('common.logout'),
    key: 'logout'
  }
]);

// 语言选项
const languageOptions = [
  {
    label: '中文',
    key: 'zh'
  },
  {
    label: 'English',
    key: 'en'
  }
];

// 方法
const toggleSidebar = () => {
  appStore.setCollapsed(!collapsed.value);
};

const toggleTheme = () => {
  appStore.setDarkTheme(!isDark.value);
};

const handleLanguageChange = (lang: string) => {
  appStore.setLanguage(lang);
  locale.value = lang;
};

const handleMenuClick = (key: string) => {
  console.log('点击菜单:', key);
  const routeMap: Record<string, string> = {
    Dashboard: '/dashboard',
    UserManagement: '/users',
    RoleManagement: '/roles',
    CompanyManagement: '/companies',
    PermissionManagement: '/permissions',
    SystemSettings: '/system'
  };

  if (routeMap[key]) {
    router.push(routeMap[key]);
  }
};

const handleUserMenuClick = (key: string) => {
  console.log('点击用户菜单:', key);
  if (key === 'logout') {
    appStore.logout();
    router.push('/login');
  }
};

// 监听路由变化
router.afterEach((to) => {
  activeKey.value = to.name as string;
});
</script>

<style scoped>
.logo {
  display: flex;
  align-items: center;
  padding: 1rem;
  border-bottom: 1px solid var(--n-border-color);
  margin-bottom: 1rem;
}

.logo-text {
  margin-left: 0.5rem;
  font-size: 1.1rem;
  font-weight: 600;
  color: var(--n-text-color);
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 100%;
  padding: 0 1rem;
}

.header-left {
  display: flex;
  align-items: center;
}

.header-right {
  display: flex;
  align-items: center;
}

.content {
  padding: 1rem;
  height: calc(100vh - 64px);
  overflow-y: auto;
}
</style>
