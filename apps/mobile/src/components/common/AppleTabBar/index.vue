<script setup lang="ts">
/**
 * AppleTabBar - Apple Style TabBar Component
 * 
 * Features:
 * - Frosted glass background
 * - Center magic button for accounting
 * - Smooth transition animations
 * - SVG icons (AppleIcon)
 */

import { ref, computed, watch } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import AppleIcon from '@/components/common/AppleIcon/index.vue'

interface TabItem {
  pagePath: string
  text: string
  icon: string
  selectedIcon: string
}

interface Props {
  current?: number
}

const props = withDefaults(defineProps<Props>(), {
  current: 0
})

const emit = defineEmits<{
  (e: 'change', index: number): void
}>()

const tabs: TabItem[] = [
  {
    pagePath: '/pages/home/index',
    text: 'home',
    icon: 'tab-home',
    selectedIcon: 'tab-home'
  },
  {
    pagePath: '/pages/accounting/index',
    text: 'accounting',
    icon: 'tab-accounting',
    selectedIcon: 'tab-accounting'
  },
  {
    pagePath: '/pages/bills/index',
    text: 'bills',
    icon: 'tab-bills',
    selectedIcon: 'tab-bills'
  },
  {
    pagePath: '/pages/analysis/index',
    text: 'analysis',
    icon: 'tab-analysis',
    selectedIcon: 'tab-analysis'
  },
  {
    pagePath: '/pages/profile/index',
    text: 'profile',
    icon: 'tab-profile',
    selectedIcon: 'tab-profile'
  }
]

const activeIndex = ref(props.current)
const isPressed = ref<number | null>(null)

const showMiddleButton = true // Always show magic button in center

const updateActiveTab = () => {
  const pages = getCurrentPages()
  if (pages.length > 0) {
    const currentPage = pages[pages.length - 1]
    const path = '/' + currentPage.route
    const index = tabs.findIndex(tab => tab.pagePath === path)
    if (index !== -1) {
      activeIndex.value = index
    }
  }
}

// Update active tab based on current page
onShow(() => {
  updateActiveTab()
})

watch(() => props.current, (val) => {
  activeIndex.value = val
})

const handleTabClick = (index: number) => {
  if (index === activeIndex.value) return
  
  isPressed.value = index
  setTimeout(() => {
    isPressed.value = null
  }, 150)
  
  activeIndex.value = index
  emit('change', index)
  
  const tab = tabs[index]
  if (tab) {
    uni.switchTab({
      url: tab.pagePath,
      fail: () => {
        // If switchTab fails, try navigateTo
        uni.navigateTo({
          url: tab.pagePath
        })
      }
    })
  }
}

const handleMagicClick = () => {
  uni.switchTab({
    url: '/pages/accounting/index'
  })
}

const isActive = (index: number) => {
  return activeIndex.value === index
}

const getTabText = (tab: TabItem) => {
  return tab.text
}
</script>

<template>
  <view class="apple-tabbar">
    <!-- Tab Items -->
    <view 
      v-for="(tab, index) in tabs" 
      :key="tab.pagePath"
      class="tabbar-item"
      :class="{ 
        active: isActive(index), 
        pressed: isPressed === index,
        'is-middle': index === 2
      }"
      @click="handleTabClick(index)"
    >
      <!-- Middle button (magic button) -->
      <template v-if="index === 2">
        <view class="magic-button" @click.stop="handleMagicClick">
          <AppleIcon 
            name="plus" 
            :size="'2xl'" 
            color="white"
            :strokeWidth="2.5"
          />
        </view>
      </template>
      
      <!-- Normal tabs -->
      <template v-else>
        <view class="tabbar-icon">
          <AppleIcon 
            :name="isActive(index) ? tab.selectedIcon : tab.icon"
            :size="'md'"
            :color="isActive(index) ? 'secondary' : 'text-tertiary'"
          />
        </view>
        <text class="tabbar-label">
          {{ $t('app.tabBar.' + getTabText(tab)) }}
        </text>
      </template>
    </view>
  </view>
</template>

<style lang="scss" scoped>
.apple-tabbar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  height: var(--apple-tabbar-height, 84px);
  background: var(--apple-glass-bg-strong, rgba(255, 255, 255, 0.85));
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border-top: 0.5px solid rgba(0, 0, 0, 0.08);
  display: flex;
  align-items: flex-start;
  justify-content: space-around;
  padding-top: 8px;
  padding-bottom: constant(safe-area-inset-bottom);
  padding-bottom: env(safe-area-inset-bottom);
  z-index: 100;
  box-shadow: 0 -4px 20px rgba(0, 0, 0, 0.05);
}

.tabbar-item {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: flex-start;
  padding: 4px 0;
  transition: all 150ms cubic-bezier(0.25, 0.1, 0.25, 1);
  
  &.pressed {
    transform: scale(0.92);
    opacity: 0.7;
  }
  
  &.is-middle {
    position: relative;
    z-index: 10;
  }
}

.tabbar-icon {
  width: 28px;
  height: 28px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 2px;
  transition: transform 200ms cubic-bezier(0.175, 0.885, 0.32, 1.275);
}

.tabbar-label {
  font-size: 10px;
  font-weight: 500;
  color: var(--gzang-text-secondary, #6B7280);
  transition: all 150ms ease;
}

.apple-tabbar .tabbar-item.active .tabbar-label {
  color: var(--gzang-secondary, #FB8B24);
  font-weight: 600;
}

.apple-tabbar .tabbar-item.active .tabbar-icon {
  transform: scale(1.1);
}

// Magic Button (Center Button)
.magic-button {
  width: 52px;
  height: 52px;
  border-radius: 50%;
  background: linear-gradient(135deg, var(--gzang-secondary, #FB8B24) 0%, var(--gzang-secondary-dark, #e67a1a) 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4px 16px rgba(251, 139, 36, 0.4), 0 2px 6px rgba(0, 0, 0, 0.1);
  transition: all 200ms cubic-bezier(0.175, 0.885, 0.32, 1.275);
  position: relative;
  top: -16px;
  margin-bottom: -12px;
  
  &:active {
    transform: scale(0.95);
    box-shadow: 0 2px 8px rgba(251, 139, 36, 0.3), 0 1px 4px rgba(0, 0, 0, 0.1);
  }
}

// Dark Mode
@media (prefers-color-scheme: dark) {
  .apple-tabbar {
    background: var(--apple-glass-bg-strong, rgba(30, 30, 30, 0.85));
    border-top-color: rgba(255, 255, 255, 0.08);
  }
  
  .tabbar-label {
    color: rgba(255, 255, 255, 0.6);
  }
  
  .apple-tabbar .tabbar-item.active .tabbar-label {
    color: var(--gzang-secondary-light, #fca03d);
  }
}

[data-theme="dark"] {
  .apple-tabbar {
    background: var(--apple-glass-bg-strong, rgba(30, 30, 30, 0.85));
    border-top-color: rgba(255, 255, 255, 0.08);
  }
  
  .tabbar-label {
    color: rgba(255, 255, 255, 0.6);
  }
  
  .apple-tabbar .tabbar-item.active .tabbar-label {
    color: var(--gzang-secondary-light, #fca03d);
  }
}
</style>
