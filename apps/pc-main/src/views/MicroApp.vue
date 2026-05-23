<template>
  <div class="micro-app-container">
    <!-- 加载状态 -->
    <div v-if="loading" class="loading-container">
      <n-spin v-if="!error" size="large" :description="loadingText" />
    </div>

    <!-- 错误状态 -->
    <div v-else-if="error" class="error-container">
      <n-result
        status="error"
        title="加载失败"
        :description="errorMessage"
      >
        <template #footer>
          <n-space justify="center">
            <n-button @click="handleRetry">重试</n-button>
            <n-button @click="handleGoBack">返回</n-button>
          </n-space>
        </template>
      </n-result>
    </div>

    <!-- 子应用容器 -->
    <div
      v-show="!loading && !error"
      :id="appContainerId"
      class="micro-app-viewport"
    ></div>

    <!-- 全屏加载遮罩 -->
    <transition name="fade">
      <div v-if="showLoadingOverlay" class="loading-overlay">
        <div class="loading-content">
          <n-spin size="large" />
          <p class="loading-text">{{ loadingText }}</p>
        </div>
      </div>
    </transition>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted, watch } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { loadMicroApp, MicroApp } from 'qiankun';

const route = useRoute();
const router = useRouter();

// 状态
const loading = ref(true);
const error = ref(false);
const errorMessage = ref('');
const showLoadingOverlay = ref(false);

// 应用容器ID
const appContainerId = 'subapp-viewport';

// 子应用实例
let microApp: MicroApp | null = null;

// 加载状态文本
const loadingText = computed(() => {
  const texts = [
    '正在加载应用...',
    '正在初始化资源...',
    '正在渲染页面...'
  ];
  return texts[Math.floor(Math.random() * texts.length)];
});

// 应用配置
const getAppConfig = (appName: string) => {
  const configs: Record<string, { name: string; entry: string; routerBase: string }> = {
    'pc-personal': {
      name: 'pc-personal',
      entry: import.meta.env.DEV ? '//localhost:8081' : '/pc-personal/',
      routerBase: '/personal'
    },
    'pc-business': {
      name: 'pc-business',
      entry: import.meta.env.DEV ? '//localhost:8082' : '/pc-business/',
      routerBase: '/business'
    },
    'pc-admin': {
      name: 'pc-admin',
      entry: import.meta.env.DEV ? '//localhost:3000' : '/pc-admin/',
      routerBase: '/admin'
    }
  };
  return configs[appName];
};

// 获取全局状态
const getGlobalState = () => {
  // 从 store 获取全局状态
  const authStore = useAuthStore();
  return {
    user: authStore.user,
    companyId: authStore.companyId,
    token: authStore.token
  };
};

// 加载微应用
const loadApp = async () => {
  loading.value = true;
  error.value = false;
  errorMessage.value = '';

  try {
    const appName = route.meta.appName as string;

    if (!appName) {
      throw new Error('未找到应用名称');
    }

    const appConfig = getAppConfig(appName);
    if (!appConfig) {
      throw new Error(`未找到应用配置: ${appName}`);
    }

    // 清理旧实例
    if (microApp) {
      microApp.unmount();
      microApp = null;
    }

    // 加载新应用
    microApp = loadMicroApp({
      name: appConfig.name,
      entry: appConfig.entry,
      container: `#${appContainerId}`,
      props: {
        routerBase: appConfig.routerBase,
        route: route.params.pathMatch?.join('/') || '',
        ...getGlobalState()
      }
    }, {
      // 沙箱配置
      sandbox: {
        strictStyleIsolation: true,
        experimentalStyleIsolation: true
      }
    });

    // 等待挂载完成
    await microApp.mountPromise;
    loading.value = false;
    console.log(`[MicroApp] ${appName} 加载成功`);

  } catch (err: any) {
    console.error('[MicroApp] 加载失败:', err);
    error.value = true;
    errorMessage.value = err?.message || '应用加载失败，请稍后重试';
    loading.value = false;
  }
};

// 重试
const handleRetry = () => {
  loadApp();
};

// 返回
const handleGoBack = () => {
  router.back();
};

// 监听路由变化
watch(() => route.fullPath, () => {
  loadApp();
});

onMounted(() => {
  loadApp();
});

onUnmounted(() => {
  // 卸载应用
  if (microApp) {
    microApp.unmount();
    microApp = null;
  }
});

// 使用 auth store
const useAuthStore = () => {
  // 这里简化处理，实际应从 @/stores/auth 导入
  return {
    user: null,
    companyId: null,
    token: null
  };
};
</script>

<style scoped>
.micro-app-container {
  height: 100%;
  position: relative;
}

.loading-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 400px;
}

.error-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 400px;
  padding: 20px;
}

.micro-app-viewport {
  height: 100%;
  width: 100%;
  min-height: 500px;
}

.loading-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(255, 255, 255, 0.9);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 9999;
}

.loading-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 16px;
}

.loading-text {
  color: var(--text-secondary);
  font-size: 14px;
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
