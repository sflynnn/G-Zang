<template>
  <div class="micro-app-container">
    <!-- 加载状态 -->
    <div v-if="loading" class="loading-container">
      <n-spin size="large" description="正在加载应用..." />
    </div>

    <!-- 子应用容器 -->
    <div
      v-else
      :id="appContainerId"
      class="micro-app-viewport"
    ></div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue';
import { useRoute } from 'vue-router';
import { loadMicroApp } from 'qiankun';

const route = useRoute();

// 加载状态
const loading = ref(true);

// 应用容器ID
const appContainerId = 'subapp-viewport';

// 子应用实例
let microApp: any = null;

onMounted(async () => {
  try {
    const appName = route.meta.appName as string;

    if (!appName) {
      console.error('未找到应用名称');
      return;
    }

    // 获取应用配置（这里可以根据appName从配置中获取）
    const appConfig = getAppConfig(appName);

    if (!appConfig) {
      console.error('未找到应用配置:', appName);
      return;
    }

    // 加载微前端应用
    microApp = loadMicroApp({
      name: appConfig.name,
      entry: appConfig.entry,
      container: `#${appContainerId}`,
      props: {
        routerBase: appConfig.routerBase,
        route: route.params.pathMatch?.join('/') || '',
        // 传递全局状态
        ...getGlobalState()
      }
    });

    // 监听应用加载完成
    microApp.mountPromise.then(() => {
      loading.value = false;
    });

  } catch (error) {
    console.error('加载微前端应用失败:', error);
    loading.value = false;
  }
});

onUnmounted(() => {
  // 卸载应用
  if (microApp) {
    microApp.unmount();
  }
});

// 获取应用配置
const getAppConfig = (appName: string) => {
  const configs = {
    'pc-personal': {
      name: 'pc-personal',
      entry: '//localhost:8081',
      routerBase: '/personal'
    },
    'pc-business': {
      name: 'pc-business',
      entry: '//localhost:8082',
      routerBase: '/business'
    },
    'pc-admin': {
      name: 'pc-admin',
      entry: '//localhost:8083',
      routerBase: '/admin'
    }
  };

  return configs[appName as keyof typeof configs];
};

// 获取全局状态
const getGlobalState = () => {
  // 这里应该从store中获取全局状态
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

.micro-app-viewport {
  height: 100%;
  width: 100%;
}
</style>
