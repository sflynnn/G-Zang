<template>
  <div class="home">
    <n-card title="欢迎使用 G-Zang 归藏" class="welcome-card">
      <n-space vertical size="large">
        <n-alert type="info" title="系统介绍">
          G-Zang (归藏) 是一款全场景财务管理系统，为个人用户和企业提供便捷的财务管理服务。
        </n-alert>

        <n-grid cols="1 s:2 m:3 l:4" responsive="screen" x-gap="12" y-gap="12">
          <!-- 个人记账卡片 -->
          <n-grid-item>
            <n-card
              title="个人记账"
              class="feature-card"
              hoverable
              @click="goToPersonal"
            >
              <template #header-extra>
                <n-icon size="24" color="#52c41a">
                  <Person />
                </n-icon>
              </template>
              <p>记录日常收支，分析消费习惯</p>
            </n-card>
          </n-grid-item>

          <!-- 企业管理卡片 -->
          <n-grid-item v-if="authStore.isCompanyUser">
            <n-card
              title="企业管理"
              class="feature-card"
              hoverable
              @click="goToBusiness"
            >
              <template #header-extra>
                <n-icon size="24" color="#1890ff">
                  <Business />
                </n-icon>
              </template>
              <p>企业财务核算，经营数据分析</p>
            </n-card>
          </n-grid-item>

          <!-- 系统管理卡片 -->
          <n-grid-item v-if="authStore.roleId === 1">
            <n-card
              title="系统管理"
              class="feature-card"
              hoverable
              @click="goToAdmin"
            >
              <template #header-extra>
                <n-icon size="24" color="#faad14">
                  <Settings />
                </n-icon>
              </template>
              <p>用户权限管理，系统配置</p>
            </n-card>
          </n-grid-item>

          <!-- 数据统计卡片 -->
          <n-grid-item>
            <n-card title="数据统计" class="feature-card">
              <template #header-extra>
                <n-icon size="24" color="#722ed1">
                  <BarChart />
                </n-icon>
              </template>
              <p>财务报表分析，趋势预测</p>
            </n-card>
          </n-grid-item>
        </n-grid>

        <!-- 快速操作 -->
        <n-card title="快速操作">
          <n-space>
            <n-button type="primary" @click="goToPersonal">
              开始记账
            </n-button>
            <n-button @click="showGuide">
              使用指南
            </n-button>
          </n-space>
        </n-card>
      </n-space>
    </n-card>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router';
import {
  Person,
  Business,
  Settings,
  BarChart
} from '@vicons/ionicons5';
import { useAuthStore } from '../stores/auth';

const router = useRouter();
const authStore = useAuthStore();

// 跳转到个人记账
const goToPersonal = () => {
  router.push('/personal');
};

// 跳转到企业管理
const goToBusiness = () => {
  router.push('/business');
};

// 跳转到系统管理
const goToAdmin = () => {
  router.push('/admin');
};

// 显示使用指南
const showGuide = () => {
  // 这里可以显示使用指南弹窗
  console.log('显示使用指南');
};
</script>

<style scoped>
.home {
  padding: 24px;
}

.welcome-card {
  max-width: 1200px;
  margin: 0 auto;
}

.feature-card {
  height: 120px;
  cursor: pointer;
  transition: all 0.3s;
}

.feature-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.feature-card p {
  color: #666;
  margin: 8px 0 0 0;
}
</style>
