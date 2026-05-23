<!-- ================================================
     G-Zang Mobile - Page Transition Wrapper
     页面内容容器，统一控制页面入场动画
     必须放在每个页面的根 view 外层使用
     ================================================ -->
<template>
  <Transition :name="transitionName" @after-enter="onEnter" @after-leave="onLeave">
    <view :key="pageKey" class="page-container">
      <slot />
    </view>
  </Transition>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue'
import { onShow } from '@dcloudio/uni-app'

// 动画类型
export type PageTransitionName =
  | 'slide-left'    // push：从右滑入（iOS 默认）
  | 'slide-right'   // pop：从左滑入
  | 'fade'          // 淡入淡出
  | 'slide-up'      // 从底部滑入
  | 'none'          // 无动画

interface Props {
  transition?: PageTransitionName
}

const props = withDefaults(defineProps<Props>(), {
  transition: 'fade',
})

const emit = defineEmits<{
  enter: []
  leave: []
}>()

const pageKey = ref('page-' + Date.now())
const transitionName = ref(props.transition)

// 每次页面显示时（路由到达）更新 key 触发动画
onShow(() => {
  pageKey.value = 'page-' + Date.now()
  transitionName.value = props.transition
})

const onEnter = () => {
  emit('enter')
}

const onLeave = () => {
  emit('leave')
}

// 支持运行时动态切换动画
watch(() => props.transition, (v) => {
  transitionName.value = v
})
</script>

<style lang="scss" scoped>
// ================================================
// 页面容器基础样式
// ================================================
.page-container {
  width: 100%;
  min-height: 100vh;
  background-color: var(--gzang-bg);
  position: relative;
}

// ================================================
// 统一转场动画
// ================================================

// ---- slide-left (push) ----
.slide-left-enter-active {
  transition: opacity 280ms cubic-bezier(0.16, 1, 0.3, 1),
              transform 280ms cubic-bezier(0.16, 1, 0.3, 1);
}
.slide-left-leave-active {
  transition: opacity 200ms cubic-bezier(0.4, 0, 1, 1),
              transform 200ms cubic-bezier(0.4, 0, 1, 1);
  position: absolute;
  width: 100%;
}
.slide-left-enter-from {
  opacity: 0;
  transform: translateX(30px);
}
.slide-left-leave-to {
  opacity: 0;
  transform: translateX(-20px);
}

// ---- slide-right (pop) ----
.slide-right-enter-active {
  transition: opacity 280ms cubic-bezier(0.16, 1, 0.3, 1),
              transform 280ms cubic-bezier(0.16, 1, 0.3, 1);
}
.slide-right-leave-active {
  transition: opacity 200ms cubic-bezier(0.4, 0, 1, 1),
              transform 200ms cubic-bezier(0.4, 0, 1, 1);
  position: absolute;
  width: 100%;
}
.slide-right-enter-from {
  opacity: 0;
  transform: translateX(-30px);
}
.slide-right-leave-to {
  opacity: 0;
  transform: translateX(20px);
}

// ---- fade ----
.fade-enter-active {
  transition: opacity 280ms cubic-bezier(0.16, 1, 0.3, 1),
              transform 280ms cubic-bezier(0.16, 1, 0.3, 1);
}
.fade-leave-active {
  transition: opacity 200ms cubic-bezier(0.4, 0, 1, 1),
              transform 200ms cubic-bezier(0.4, 0, 1, 1);
  position: absolute;
  width: 100%;
}
.fade-enter-from {
  opacity: 0;
  transform: translateY(12px) scale(0.98);
}
.fade-leave-to {
  opacity: 0;
  transform: translateY(-8px) scale(0.98);
}

// ---- slide-up ----
.slide-up-enter-active {
  transition: opacity 320ms cubic-bezier(0.16, 1, 0.3, 1),
              transform 320ms cubic-bezier(0.34, 1.2, 0.64, 1);
}
.slide-up-leave-active {
  transition: opacity 200ms cubic-bezier(0.4, 0, 1, 1),
              transform 200ms cubic-bezier(0.4, 0, 1, 1);
  position: absolute;
  width: 100%;
}
.slide-up-enter-from {
  opacity: 0;
  transform: translateY(100%);
}
.slide-up-leave-to {
  opacity: 0;
  transform: translateY(20px);
}

// ---- none ----
.none-enter-active,
.none-leave-active {
  transition: none;
}
</style>
