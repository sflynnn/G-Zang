<template>
  <view class="app">
    <!-- 状态栏占位 -->
    <view class="status-bar-placeholder" v-if="showStatusBar"></view>

    <!-- 页面容器 -->
    <view class="page-container">
      <router-view />
    </view>

    <!-- 底部Tab栏 -->
    <uni-tabbar
      v-if="showTabBar"
      :current="currentTab"
      @change="switchTab"
      :list="tabBarList"
    />
  </view>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useAuthStore } from '@/stores/auth'

// 状态管理
const authStore = useAuthStore()

// 响应式数据
const currentTab = ref(0)
const showStatusBar = ref(true)
const showTabBar = ref(true)

// Tab栏配置
const tabBarList = [
  {
    pagePath: '/pages/home/index',
    iconPath: '/static/tabbar/home.png',
    selectedIconPath: '/static/tabbar/home-active.png',
    text: '首页'
  },
  {
    pagePath: '/pages/accounting/index',
    iconPath: '/static/tabbar/accounting.png',
    selectedIconPath: '/static/tabbar/accounting-active.png',
    text: '记账'
  },
  {
    pagePath: '/pages/bills/index',
    iconPath: '/static/tabbar/bills.png',
    selectedIconPath: '/static/tabbar/bills-active.png',
    text: '账单'
  },
  {
    pagePath: '/pages/analysis/index',
    iconPath: '/static/tabbar/analysis.png',
    selectedIconPath: '/static/tabbar/analysis-active.png',
    text: '统计'
  },
  {
    pagePath: '/pages/profile/index',
    iconPath: '/static/tabbar/profile.png',
    selectedIconPath: '/static/tabbar/profile-active.png',
    text: '我的'
  }
]

// 计算属性
const currentRoute = computed(() => {
  const pages = getCurrentPages()
  return pages[pages.length - 1]?.route || ''
})

// 切换Tab
const switchTab = (e: any) => {
  const { index } = e.detail
  const tab = tabBarList[index]
  if (tab) {
    uni.switchTab({
      url: tab.pagePath
    })
  }
}

// 检查是否需要显示Tab栏
const checkTabBarVisibility = () => {
  const noTabPages = ['pages/login/index']
  showTabBar.value = !noTabPages.includes(currentRoute.value)
}

// 检查是否需要显示状态栏占位
const checkStatusBarVisibility = () => {
  const customNavPages = ['pages/login/index']
  showStatusBar.value = !customNavPages.includes(currentRoute.value)
}

// 生命周期
onMounted(() => {
  checkTabBarVisibility()
  checkStatusBarVisibility()

  // 监听页面变化
  uni.onShow = () => {
    checkTabBarVisibility()
    checkStatusBarVisibility()
  }
})
</script>

<style lang="scss">
.app {
  height: 100vh;
  display: flex;
  flex-direction: column;
}

.status-bar-placeholder {
  height: var(--status-bar-height, 0px);
  background-color: var(--background-white);
}

.page-container {
  flex: 1;
  overflow: hidden;
}

// 自定义Tab栏样式
.uni-tabbar {
  background-color: var(--background-white);
  border-top: 1px solid var(--border-color);
  padding-bottom: env(safe-area-inset-bottom);

  .uni-tabbar__item {
    color: var(--text-secondary);

    &.uni-tabbar__item--active {
      color: var(--primary-color);
    }
  }
}
</style>
