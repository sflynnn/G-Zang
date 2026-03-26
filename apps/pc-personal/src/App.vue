<template>
  <div class="personal-app">
    <!-- 子应用头部 -->
    <div class="app-header">
      <h2>个人记账</h2>
    </div>

    <!-- 内容区域 -->
    <div class="app-content">
      <router-view />
    </div>
  </div>
</template>

<script setup lang="ts">
// 子应用组件
</script>

<style scoped>
.personal-app {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.app-header {
  padding: 16px 24px;
  background-color: #fff;
  border-bottom: 1px solid #e8e8e8;
  flex-shrink: 0;
}

.app-header h2 {
  margin: 0;
  color: #1890ff;
  font-size: 18px;
  font-weight: 600;
}

.app-content {
  flex: 1;
  overflow-y: auto;
  padding: 24px;
}
</style>
