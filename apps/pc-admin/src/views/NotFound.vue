<template>
  <div class="not-found">
    <div class="not-found-content">
      <n-result
        status="404"
        :title="t('messages.notFoundTitle')"
        :description="t('messages.notFoundDesc')"
      >
        <template #footer>
          <n-space>
            <n-button @click="$router.go(-1)">
              {{ t('messages.backPrev') }}
            </n-button>
            <n-button type="primary" @click="$router.push('/')">
              {{ t('messages.backHome') }}
            </n-button>
          </n-space>
        </template>
      </n-result>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useI18n } from 'vue-i18n';
const { t } = useI18n();
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
