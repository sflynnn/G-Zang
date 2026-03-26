<template>
  <transition name="loading-fade" appear>
    <div v-if="isLoading" class="global-loading">
      <!-- 背景遮罩 -->
      <div class="loading-backdrop"></div>

      <!-- Loading内容 -->
      <div class="loading-content">
        <!-- 星系旋转系统 -->
        <div class="galaxy-system">
          <!-- 第一轨道 - 内圈小行星带 (4个) -->
          <div class="orbit orbit-1">
            <div class="asteroid asteroid-1">•</div>
            <div class="asteroid asteroid-2">•</div>
            <div class="asteroid asteroid-3">•</div>
            <div class="asteroid asteroid-4">•</div>
          </div>

          <!-- 第二轨道 - 中圈行星 (6个) -->
          <div class="orbit orbit-2">
            <div class="planet planet-1"></div>
            <div class="planet planet-2"></div>
            <div class="planet planet-3"></div>
            <div class="planet planet-4"></div>
            <div class="planet planet-5"></div>
            <div class="planet planet-6"></div>
          </div>

          <!-- 第三轨道 - 外圈气态巨行星 (3个) -->
          <div class="orbit orbit-3">
            <div class="gas-giant gas-giant-1">
              <div class="giant-body"></div>
              <div class="giant-ring"></div>
            </div>
            <div class="gas-giant gas-giant-2">
              <div class="giant-body"></div>
              <div class="giant-ring"></div>
            </div>
            <div class="gas-giant gas-giant-3">
              <div class="giant-body"></div>
              <div class="giant-ring"></div>
            </div>
          </div>

          <!-- 第四轨道 - 柯伊伯带彗星 (2个) -->
          <div class="orbit orbit-4">
            <div class="comet comet-1">☄</div>
            <div class="comet comet-2">☄</div>
          </div>

          <!-- 主恒星 - 品牌元素 -->
          <div class="star-core">
            <div class="core-glow"></div>
            <div class="core-icon">
              <svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <path d="M12 2L13.09 8.26L19 9L13.09 9.74L12 16L10.91 9.74L5 9L10.91 8.26L12 2Z" fill="currentColor"/>
              </svg>
            </div>
            <div class="core-pulse"></div>
          </div>
        </div>

        <!-- Loading文字 -->
        <div class="loading-text">
          <span class="text-main">{{ loadingText }}</span>
          <div class="loading-dots">
            <span class="dot dot-1"></span>
            <span class="dot dot-2"></span>
            <span class="dot dot-3"></span>
          </div>
        </div>
      </div>
    </div>
  </transition>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import { useLoadingStore } from '@/stores/loading';
import { useI18n } from 'vue-i18n';

const loadingStore = useLoadingStore();
const { t } = useI18n();

// 全局加载状态
const isLoading = computed(() => loadingStore.isGlobalLoading);

// 动态加载文本
const loadingText = computed(() => {
  return loadingStore.globalLoadingText || t('common.loading');
});
</script>

<style scoped>
.global-loading {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  z-index: 9999;
  display: flex;
  align-items: center;
  justify-content: center;
}

.loading-backdrop {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, rgba(0, 0, 0, 0.2) 0%, rgba(0, 0, 0, 0.4) 100%);
  backdrop-filter: blur(8px);
}

.loading-content {
  position: relative;
  z-index: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 2rem;
}

/* 星系旋转系统 */
.galaxy-system {
  position: relative;
  width: 160px;
  height: 160px;
}

/* 轨道系统 */
.orbit {
  position: absolute;
  top: 50%;
  left: 50%;
  border-radius: 50%;
}

.orbit-1 {
  width: 80px;
  height: 80px;
  margin: -40px 0 0 -40px;
  animation: orbit-rotate 8s linear infinite;
}

.orbit-2 {
  width: 120px;
  height: 120px;
  margin: -60px 0 0 -60px;
  animation: orbit-rotate 10s linear infinite reverse;
}

.orbit-3 {
  width: 160px;
  height: 160px;
  margin: -80px 0 0 -80px;
  animation: orbit-rotate 15s linear infinite;
}

.orbit-4 {
  width: 200px;
  height: 200px;
  margin: -100px 0 0 -100px;
  animation: orbit-rotate 20s linear infinite;
}

/* 内圈小行星带 */
.asteroid {
  position: absolute;
  font-size: 8px;
  color: var(--color-gray-400);
  text-shadow: 0 0 4px var(--color-secondary);
  animation: asteroid-pulse 1.5s ease-in-out infinite;
}

/* 小行星在轨道上的位置 */
.orbit-1 .asteroid:nth-child(1) { top: 0; left: 50%; transform: translateX(-50%); }
.orbit-1 .asteroid:nth-child(2) { top: 50%; right: 0; transform: translateY(-50%); }
.orbit-1 .asteroid:nth-child(3) { bottom: 0; left: 50%; transform: translateX(-50%); }
.orbit-1 .asteroid:nth-child(4) { top: 50%; left: 0; transform: translateY(-50%); }

/* 中圈行星 */
.planet {
  position: absolute;
  width: 6px;
  height: 6px;
  background: linear-gradient(45deg, var(--color-primary), var(--color-secondary));
  border-radius: 50%;
  box-shadow: 0 0 8px rgba(251, 139, 36, 0.6);
  animation: planet-glow 3s ease-in-out infinite;
}

.planet:nth-child(2) {
  background: linear-gradient(45deg, var(--color-success), var(--color-primary));
  animation-delay: 0.5s;
}

.planet:nth-child(3) {
  background: linear-gradient(45deg, var(--color-secondary), var(--color-success));
  animation-delay: 1s;
}

.planet:nth-child(4) {
  background: linear-gradient(45deg, var(--color-primary), var(--color-success));
  animation-delay: 1.5s;
}

.planet:nth-child(5) {
  background: linear-gradient(45deg, var(--color-secondary), var(--color-danger));
  animation-delay: 2s;
}

.planet:nth-child(6) {
  background: linear-gradient(45deg, var(--color-danger), var(--color-primary));
  animation-delay: 2.5s;
}

/* 行星在轨道上的位置 */
.orbit-2 .planet:nth-child(1) { top: 0; left: 60px; transform: translateX(-50%); }
.orbit-2 .planet:nth-child(2) { top: 30px; right: 16.08px; transform: translateY(-50%); }
.orbit-2 .planet:nth-child(3) { top: 60px; right: 0; transform: translateY(-50%); }
.orbit-2 .planet:nth-child(4) { bottom: 30px; right: 16.08px; transform: translateY(-50%); }
.orbit-2 .planet:nth-child(5) { bottom: 0; left: 60px; transform: translateX(-50%); }
.orbit-2 .planet:nth-child(6) { top: 30px; left: 16.08px; transform: translateY(-50%); }

/* 外圈气态巨行星 */
.gas-giant {
  position: absolute;
  width: 16px;
  height: 16px;
  animation: gas-giant-rotate 2s ease-in-out infinite;
}

/* 气态巨行星在轨道上的位置 */
.orbit-3 .gas-giant:nth-child(1) { top: 0; left: 80px; transform: translateX(-50%); }
.orbit-3 .gas-giant:nth-child(2) { top: 80px; right: 0; transform: translateY(-50%); }
.orbit-3 .gas-giant:nth-child(3) { top: 80px; left: 0; transform: translateY(-50%); }

.giant-body {
  width: 12px;
  height: 12px;
  background: linear-gradient(135deg, var(--color-primary), var(--color-secondary));
  border-radius: 50%;
  box-shadow:
    0 0 12px rgba(15, 76, 92, 0.8),
    inset 0 1px 0 rgba(255, 255, 255, 0.3);
  position: relative;
  top: 2px;
  left: 2px;
}

.giant-ring {
  position: absolute;
  top: 0;
  left: 0;
  width: 16px;
  height: 16px;
  border: 1px solid rgba(251, 139, 36, 0.6);
  border-radius: 50%;
  box-shadow: 0 0 8px rgba(251, 139, 36, 0.4);
}

/* 柯伊伯带彗星 */
.comet {
  position: absolute;
  font-size: 10px;
  color: var(--color-success);
  text-shadow: 0 0 8px var(--color-success);
  animation: comet-trail 1.5s ease-in-out infinite;
}

.comet:nth-child(2) {
  animation-delay: 0.75s;
}

/* 彗星在轨道上的位置 */
.orbit-4 .comet:nth-child(1) { top: 100px; right: 0; transform: translateY(-50%); }
.orbit-4 .comet:nth-child(2) { top: 100px; left: 0; transform: translateY(-50%); }

/* 恒星核心 */
.star-core {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 40px;
  height: 40px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.core-glow {
  position: absolute;
  top: -8px;
  left: -8px;
  width: 56px;
  height: 56px;
  background: radial-gradient(
    circle,
    rgba(251, 139, 36, 0.3) 0%,
    rgba(251, 139, 36, 0.1) 50%,
    transparent 70%
  );
  border-radius: 50%;
  animation: core-glow-pulse 4s ease-in-out infinite;
}

.core-icon {
  position: relative;
  z-index: 2;
  color: var(--color-primary);
  filter: drop-shadow(0 0 8px var(--color-secondary));
  animation: core-icon-shine 3s ease-in-out infinite;
}

.core-pulse {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 100%;
  height: 100%;
  border-radius: 50%;
  background: radial-gradient(
    circle,
    transparent 30%,
    rgba(251, 139, 36, 0.2) 50%,
    transparent 70%
  );
  animation: core-pulse-wave 2s ease-in-out infinite;
}

@keyframes orbit-rotate {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}

@keyframes asteroid-pulse {
  0%, 100% {
    opacity: 0.7;
    transform: scale(1);
  }
  50% {
    opacity: 1;
    transform: scale(1.3);
  }
}

@keyframes gas-giant-rotate {
  0%, 100% {
    transform: scale(1) rotate(0deg);
  }
  50% {
    transform: scale(1.1) rotate(180deg);
  }
}

@keyframes planet-glow {
  0%, 100% {
    box-shadow: 0 0 8px rgba(251, 139, 36, 0.6);
    transform: scale(1);
  }
  50% {
    box-shadow: 0 0 16px rgba(251, 139, 36, 0.9), 0 0 24px rgba(251, 139, 36, 0.4);
    transform: scale(1.1);
  }
}

@keyframes comet-trail {
  0%, 100% {
    opacity: 0.7;
    text-shadow: 0 0 10px var(--color-success);
  }
  50% {
    opacity: 1;
    text-shadow: 0 0 20px var(--color-success), 0 0 30px var(--color-success);
  }
}

@keyframes core-glow-pulse {
  0%, 100% {
    transform: scale(1);
    opacity: 0.8;
  }
  50% {
    transform: scale(1.2);
    opacity: 1;
  }
}

@keyframes core-icon-shine {
  0%, 100% {
    filter: drop-shadow(0 0 8px var(--color-secondary));
  }
  50% {
    filter: drop-shadow(0 0 16px var(--color-secondary)) brightness(1.2);
  }
}

@keyframes core-pulse-wave {
  0% {
    transform: translate(-50%, -50%) scale(0.8);
    opacity: 0;
  }
  50% {
    opacity: 0.6;
  }
  100% {
    transform: translate(-50%, -50%) scale(1.5);
    opacity: 0;
  }
}

/* Loading文字 */
.loading-text {
  display: flex;
  align-items: baseline;
  gap: 0.25rem;
  font-family: var(--font-family-chinese);
}

.text-main {
  font-size: 1.25rem;
  font-weight: 600;
  color: #2a6b7c;
  text-shadow: 0 0 8px rgba(42, 107, 124, 0.5);
  letter-spacing: 0.1em;
}

.loading-dots {
  display: flex;
  align-items: center;
  gap: 4px;
  margin-left: 8px;
}

.dot {
  display: inline-block;
  width: 4px;
  height: 4px;
  background: var(--color-secondary);
  border-radius: 50%;
  animation: dot-pulse 1.4s ease-in-out infinite;
}

.dot-1 {
  animation-delay: 0s;
}

.dot-2 {
  animation-delay: 0.2s;
}

.dot-3 {
  animation-delay: 0.4s;
}

@keyframes dot-pulse {
  0%, 80%, 100% {
    transform: scale(0.8);
    opacity: 0.4;
  }
  40% {
    transform: scale(1);
    opacity: 1;
  }
}

/* 过渡动画 */
.loading-fade-enter-active,
.loading-fade-leave-active {
  transition: opacity 0.5s ease;
}

.loading-fade-enter-from,
.loading-fade-leave-to {
  opacity: 0;
}

/* 深色主题适配 */
@media (prefers-color-scheme: dark) {
  .loading-backdrop {
    background: linear-gradient(135deg, rgba(0, 0, 0, 0.5) 0%, rgba(0, 0, 0, 0.8) 100%);
  }

  .text-main {
    color: #2a6b7c;
    text-shadow: 0 0 12px rgba(42, 107, 124, 0.6);
  }

  .asteroid {
    color: var(--color-gray-500);
    text-shadow: 0 0 6px var(--color-secondary);
  }

  .planet {
    background: linear-gradient(45deg, var(--color-primary), var(--color-secondary));
    box-shadow: 0 0 10px rgba(251, 139, 36, 0.8);
  }

  .giant-body {
    background: linear-gradient(135deg, var(--color-primary), var(--color-secondary));
    box-shadow:
      0 0 15px rgba(15, 76, 92, 1),
      inset 0 1px 0 rgba(255, 255, 255, 0.2);
  }

  .giant-ring {
    border-color: rgba(251, 139, 36, 0.8);
    box-shadow: 0 0 10px rgba(251, 139, 36, 0.6);
  }

  .comet {
    color: var(--color-success);
    text-shadow: 0 0 10px var(--color-success);
  }

  .core-glow {
    background: radial-gradient(
      circle,
      rgba(251, 139, 36, 0.4) 0%,
      rgba(251, 139, 36, 0.15) 50%,
      transparent 70%
    );
  }

  .core-icon {
    color: var(--color-secondary);
  }

  .core-pulse {
    background: radial-gradient(
      circle,
      transparent 30%,
      rgba(251, 139, 36, 0.3) 50%,
      transparent 70%
    );
  }
}

/* 响应式设计 */
@media (max-width: 640px) {
  .galaxy-system {
    width: 140px;
    height: 140px;
  }

  .orbit-1 {
    width: 70px;
    height: 70px;
    margin: -35px 0 0 -35px;
  }

  .orbit-2 {
    width: 100px;
    height: 100px;
    margin: -50px 0 0 -50px;
  }

  .orbit-3 {
    width: 140px;
    height: 140px;
    margin: -70px 0 0 -70px;
  }

  .orbit-4 {
    width: 160px;
    height: 160px;
    margin: -80px 0 0 -80px;
  }

  .asteroid {
    font-size: 6px;
  }

  .orbit-1 .asteroid:nth-child(1) { top: 0; left: 50%; transform: translateX(-50%); }
  .orbit-1 .asteroid:nth-child(2) { top: 50%; right: 0; transform: translateY(-50%); }
  .orbit-1 .asteroid:nth-child(3) { bottom: 0; left: 50%; transform: translateX(-50%); }
  .orbit-1 .asteroid:nth-child(4) { top: 50%; left: 0; transform: translateY(-50%); }

  .planet {
    width: 5px;
    height: 5px;
  }

  .orbit-2 .planet:nth-child(1) { top: 0; left: 50px; transform: translateX(-50%); }
  .orbit-2 .planet:nth-child(2) { top: 25px; right: 13.4px; transform: translateY(-50%); }
  .orbit-2 .planet:nth-child(3) { top: 50px; right: 0; transform: translateY(-50%); }
  .orbit-2 .planet:nth-child(4) { bottom: 25px; right: 13.4px; transform: translateY(-50%); }
  .orbit-2 .planet:nth-child(5) { bottom: 0; left: 50px; transform: translateX(-50%); }
  .orbit-2 .planet:nth-child(6) { top: 25px; left: 13.4px; transform: translateY(-50%); }

  .gas-giant {
    width: 14px;
    height: 14px;
  }

  .giant-body {
    width: 10px;
    height: 10px;
    top: 2px;
    left: 2px;
  }

  .orbit-3 .gas-giant:nth-child(1) { top: 0; left: 70px; transform: translateX(-50%); }
  .orbit-3 .gas-giant:nth-child(2) { top: 70px; right: 0; transform: translateY(-50%); }
  .orbit-3 .gas-giant:nth-child(3) { top: 70px; left: 0; transform: translateY(-50%); }

  .comet {
    font-size: 8px;
  }

  .orbit-4 .comet:nth-child(1) { top: 80px; right: 0; transform: translateY(-50%); }
  .orbit-4 .comet:nth-child(2) { top: 80px; left: 0; transform: translateY(-50%); }

  .star {
    font-size: 8px;
  }

  .planet {
    width: 5px;
    height: 5px;
  }

  .comet {
    font-size: 10px;
  }

  .star-core {
    width: 32px;
    height: 32px;
  }

  .core-glow {
    top: -6px;
    left: -6px;
    width: 44px;
    height: 44px;
  }

  .text-main {
    font-size: 1.125rem;
  }

  .dot {
    width: 3px;
    height: 3px;
  }
}
</style>
