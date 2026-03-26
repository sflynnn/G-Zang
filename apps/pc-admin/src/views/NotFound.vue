<template>
  <div class="not-found">
    <div class="not-found-content">
      <n-result
        status="404"
        title="页面未找到"
        description="抱歉，您访问的页面不存在或已被删除"
      >
        <template #footer>
          <n-space>
            <n-button @click="$router.go(-1)">
              返回上一页
            </n-button>
            <n-button type="primary" @click="$router.push('/')">
              返回首页
            </n-button>
          </n-space>
        </template>
      </n-result>
    </div>
  </div>
</template>

<script setup lang="ts">
// 404页面组件
</script>

<style scoped>
.not-found {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background-color: var(--n-color);
}

.not-found-content {
  text-align: center;
}
</style>
