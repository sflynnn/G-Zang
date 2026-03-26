<template>
  <div id="app">
    <!-- 顶部导航 -->
    <AppHeader />

    <!-- 主内容区域 -->
    <div class="main-container">
      <!-- 侧边栏菜单 -->
      <AppSidebar />

      <!-- 内容区域 -->
      <div class="content-wrapper">
        <router-view />
      </div>
    </div>

    <!-- 微前端子应用容器 -->
    <div id="subapp-viewport"></div>
  </div>
</template>

<script setup lang="ts">
import AppHeader from './components/AppHeader.vue';
import AppSidebar from './components/AppSidebar.vue';
</script>

<style scoped>
#app {
  height: 100vh;
  display: flex;
  flex-direction: column;
}

.main-container {
  flex: 1;
  display: flex;
}

.content-wrapper {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
}

#subapp-viewport {
  display: none; /* 微前端子应用容器，默认为隐藏 */
}
</style>
