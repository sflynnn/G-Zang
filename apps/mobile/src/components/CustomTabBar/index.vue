<template>
  <view class="tabbar-container">
    <!-- 背景层 -->
    <view class="tabbar-bg" />

    <!-- TabBar 内容 -->
    <view class="tabbar-content">
      <view
        v-for="(tab, index) in tabs"
        :key="tab.pagePath"
        class="tabbar-item"
        :class="{ active: activeIndex === index }"
        @click.stop="switchTab(index)"
      >
        <!-- 中间魔法按钮 -->
        <view v-if="index === 2" class="magic-wrapper">
          <view class="magic-button">
            <text class="magic-plus">+</text>
          </view>
        </view>

        <!-- 普通 Tab -->
        <template v-else>
          <!-- SVG 图标 -->
          <view class="icon-wrapper">
            <svg v-if="tab.iconKey === 'home'" class="tab-svg-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round">
              <path d="M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z"/>
              <polyline points="9 22 9 12 15 12 15 22"/>
            </svg>
            <svg v-else-if="tab.iconKey === 'accounting'" class="tab-svg-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round">
              <line x1="12" y1="1" x2="12" y2="23"/>
              <path d="M17 5H9.5a3.5 3.5 0 0 0 0 7h5a3.5 3.5 0 0 1 0 7H6"/>
            </svg>
            <svg v-else-if="tab.iconKey === 'bills'" class="tab-svg-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round">
              <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/>
              <polyline points="14 2 14 8 20 8"/>
              <line x1="16" y1="13" x2="8" y2="13"/>
              <line x1="16" y1="17" x2="8" y2="17"/>
              <polyline points="10 9 9 9 8 9"/>
            </svg>
            <svg v-else-if="tab.iconKey === 'analysis'" class="tab-svg-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round">
              <line x1="18" y1="20" x2="18" y2="10"/>
              <line x1="12" y1="20" x2="12" y2="4"/>
              <line x1="6" y1="20" x2="6" y2="14"/>
            </svg>
            <svg v-else-if="tab.iconKey === 'profile'" class="tab-svg-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round">
              <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/>
              <circle cx="12" cy="7" r="4"/>
            </svg>
          </view>

          <!-- 标签文字 -->
          <text class="tab-label">{{ tab.text }}</text>
        </template>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'

// ========== TabBar 配置 ==========
interface TabItem {
  pagePath: string
  text: string
  iconKey: string
}

const tabs: TabItem[] = [
  { pagePath: '/pages/home/index', text: '首页', iconKey: 'home' },
  { pagePath: '/pages/accounting/index', text: '记账', iconKey: 'accounting' },
  { pagePath: '/pages/bills/index', text: '账单', iconKey: 'bills' },
  { pagePath: '/pages/analysis/index', text: '统计', iconKey: 'analysis' },
  { pagePath: '/pages/profile/index', text: '我的', iconKey: 'profile' }
]

const tabBarPaths = tabs.map(t => t.pagePath)
const activeIndex = ref(0)

// ========== 路由工具 ==========
const getCurrentPagePath = (): string => {
  try {
    const hash = window.location.hash
    if (hash) {
      const match = hash.match(/#(\/[^?]+)/)
      if (match) return match[1]
    }
  } catch (e) { /* H5 only */ }

  try {
    const pages = getCurrentPages()
    if (pages && pages.length > 0) {
      const currentPage = pages[pages.length - 1] as any
      if (currentPage.route) return '/' + currentPage.route
      if (currentPage.$page?.fullPath) {
        const m = currentPage.$page.fullPath.match(/^\/([^?]+)/)
        if (m) return '/' + m[1]
      }
    }
  } catch (e) { /* Mini-program only */ }

  return ''
}

// ========== 生命周期 ==========
onMounted(() => {
  updateActiveTab()
  if (typeof window !== 'undefined') {
    window.addEventListener('hashchange', updateActiveTab)
  }
})

onUnmounted(() => {
  if (typeof window !== 'undefined') {
    window.removeEventListener('hashchange', updateActiveTab)
  }
})

// ========== 逻辑 ==========
const updateActiveTab = () => {
  const path = getCurrentPagePath()
  const idx = tabBarPaths.indexOf(path)
  if (idx !== -1) activeIndex.value = idx
}

const switchTab = (index: number) => {
  if (index === 2) {
    uni.switchTab({ url: '/pages/accounting/index' })
    return
  }

  if (index === activeIndex.value) return

  const tab = tabs[index]
  if (!tab) return

  activeIndex.value = index
  uni.switchTab({ url: tab.pagePath })
}
</script>

<style lang="scss" scoped>
// ================================================
// G-Zang Custom TabBar
// Apple HIG + G-Zang Brand Design
// ================================================

.tabbar-container {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  z-index: 9999;
}

// ================================================
// 背景层 - 毛玻璃效果
// ================================================
.tabbar-bg {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  height: calc(84px + env(safe-area-inset-bottom));
  padding-bottom: env(safe-area-inset-bottom);

  // 毛玻璃背景（浅色模式）
  background: rgba(255, 255, 255, 0.82);
  backdrop-filter: saturate(180%) blur(20px);
  -webkit-backdrop-filter: saturate(180%) blur(20px);

  // Apple 风格边框
  border-top: 0.5px solid rgba(0, 0, 0, 0.08);

  // 顶部微妙阴影
  box-shadow:
    0 -0.5px 1px rgba(0, 0, 0, 0.04),
    0 -2px 8px rgba(0, 0, 0, 0.03);
}

// ================================================
// 内容层
// ================================================
.tabbar-content {
  position: relative;
  display: flex;
  align-items: flex-start;
  justify-content: space-around;
  height: 84px;
  padding-top: 8px;
  padding-bottom: env(safe-area-inset-bottom);
}

// ================================================
// Tab 项目
// ================================================
.tabbar-item {
  position: relative;
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: flex-start;
  padding: 4px 0;
  cursor: pointer;
  -webkit-tap-highlight-color: transparent;

  // 点击缩放反馈
  &:active {
    transform: scale(0.94);
    transition: transform 80ms ease-out;
  }

  &:not(:active) {
    transition: transform 200ms cubic-bezier(0.34, 1.56, 0.64, 1);
  }

  // 选中状态
  &.active {
    .tab-label {
      color: var(--gzang-primary, #0F4C5C);
      font-weight: 600;
    }

    .tab-svg-icon {
      transform: scale(1.1);
      color: var(--gzang-primary, #0F4C5C);
    }
  }
}

// ================================================
// SVG 图标
// ================================================
.icon-wrapper {
  position: relative;
  width: 28px;
  height: 28px;
  margin-bottom: 3px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.tab-svg-icon {
  width: 24px;
  height: 24px;
  color: var(--gzang-text-secondary, #8E8E93);
  transition: color 200ms ease, transform 250ms cubic-bezier(0.34, 1.56, 0.64, 1);
}

// ================================================
// 标签文字
// ================================================
.tab-label {
  font-family: var(--apple-font-family, -apple-system, BlinkMacSystemFont, 'SF Pro Text', sans-serif);
  font-size: 10px;
  font-weight: 500;
  letter-spacing: 0.2px;
  color: var(--gzang-text-secondary, #8E8E93);
  transition: color 200ms ease, font-weight 200ms ease;
  line-height: 1;
}

// ================================================
// 魔法按钮 - 居中凸起设计
// ================================================
.magic-wrapper {
  position: relative;
  width: 52px;
  height: 64px;
  display: flex;
  align-items: flex-start;
  justify-content: center;
}

.magic-button {
  width: 52px;
  height: 52px;
  border-radius: 50%;
  margin-top: -4px;

  // G-Zang 品牌渐变
  background: linear-gradient(
    135deg,
    var(--gzang-primary, #0F4C5C) 0%,
    var(--gzang-primary-light, #186a7d) 50%,
    var(--gzang-secondary, #FB8B24) 100%
  );

  display: flex;
  align-items: center;
  justify-content: center;

  // 柔和阴影
  box-shadow:
    0 4px 12px rgba(15, 76, 92, 0.3),
    0 2px 4px rgba(15, 76, 92, 0.2),
    inset 0 1px 0 rgba(255, 255, 255, 0.15);

  // 微妙的脉冲动画
  animation: magicBreathing 3s ease-in-out infinite;

  &:active {
    animation: none;
    transform: scale(0.92);
    box-shadow:
      0 2px 6px rgba(15, 76, 92, 0.25),
      0 1px 2px rgba(15, 76, 92, 0.15),
      inset 0 1px 0 rgba(255, 255, 255, 0.1);
    transition: transform 80ms ease-out, box-shadow 80ms ease-out;
  }

  &:not(:active) {
    transition: transform 300ms cubic-bezier(0.34, 1.56, 0.64, 1), box-shadow 300ms ease;
  }
}

@keyframes magicBreathing {
  0%, 100% {
    box-shadow:
      0 4px 12px rgba(15, 76, 92, 0.3),
      0 2px 4px rgba(15, 76, 92, 0.2),
      inset 0 1px 0 rgba(255, 255, 255, 0.15);
  }
  50% {
    box-shadow:
      0 6px 20px rgba(224, 122, 63, 0.35),
      0 3px 8px rgba(15, 76, 92, 0.2),
      inset 0 1px 0 rgba(255, 255, 255, 0.2);
  }
}

.magic-plus {
  font-family: var(--apple-font-family, -apple-system, BlinkMacSystemFont, sans-serif);
  font-size: 26px;
  font-weight: 300;
  color: white;
  line-height: 1;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.1);
}

// ================================================
// 深色模式
// ================================================
[data-theme="dark"] {
  .tabbar-bg {
    background: rgba(30, 30, 30, 0.85);
    border-top-color: rgba(255, 255, 255, 0.08);
    box-shadow:
      0 -0.5px 1px rgba(0, 0, 0, 0.2),
      0 -2px 8px rgba(0, 0, 0, 0.15);
  }

  .tab-label {
    color: rgba(255, 255, 255, 0.5);
  }

  .tabbar-item.active .tab-label {
    color: var(--gzang-secondary, #FB8B24);
  }

  .tab-svg-icon {
    color: rgba(255, 255, 255, 0.5);
  }

  .tabbar-item.active .tab-svg-icon {
    color: var(--gzang-secondary, #FB8B24);
  }

  .magic-button {
    background: linear-gradient(
      135deg,
      var(--gzang-primary-light, #186a7d) 0%,
      var(--gzang-secondary, #FB8B24) 100%
    );

    box-shadow:
      0 4px 12px rgba(224, 122, 63, 0.3),
      0 2px 4px rgba(0, 0, 0, 0.3),
      inset 0 1px 0 rgba(255, 255, 255, 0.1);
  }
}

// ================================================
// 入场动画
// ================================================
.tabbar-container {
  animation: tabbarEnter 350ms cubic-bezier(0.16, 1, 0.3, 1) forwards;
}

@keyframes tabbarEnter {
  from {
    opacity: 0;
    transform: translateY(100%);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}
</style>
