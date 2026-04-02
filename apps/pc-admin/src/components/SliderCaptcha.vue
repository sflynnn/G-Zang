<template>
  <div class="slider-captcha">
    <div class="slider-track" :class="{ 'completed': isCompleted }">
      <div class="slider-bg"></div>
      <div class="slider-progress" :style="{ width: `${progress}%` }"></div>
      <div
        class="slider-thumb"
        :class="{ 'completed': isCompleted }"
        @mousedown="startDrag"
        @touchstart="startTouch"
        :style="{ left: `${progress}%` }"
      >
        <div class="thumb-icon">
          <svg v-if="!isCompleted" width="16" height="16" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M9 5L16 12L9 19" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
          <svg v-else width="16" height="16" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M20 6L9 17L4 12" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
        </div>
      </div>
      <div class="slider-text">
        <span v-if="!isCompleted">{{ $t('login.sliderCaptcha.hint') }}</span>
        <span v-else class="success-text">{{ $t('login.sliderCaptcha.success') }}</span>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue';

// Props
interface Props {
  modelValue?: boolean;
  threshold?: number; // 完成阈值，默认90%
}

const props = withDefaults(defineProps<Props>(), {
  modelValue: false,
  threshold: 90
});

// Emits
const emit = defineEmits<{
  'update:modelValue': [value: boolean];
}>();

// Reactive data
const progress = ref(0);
const isDragging = ref(false);
const startX = ref(0);

// Computed
const isCompleted = computed(() => progress.value >= props.threshold);

// Methods
const startDrag = (e: MouseEvent) => {
  e.preventDefault();
  isDragging.value = true;
  startX.value = e.clientX;
  document.addEventListener('mousemove', onDrag);
  document.addEventListener('mouseup', stopDrag);
};

const startTouch = (e: TouchEvent) => {
  e.preventDefault();
  isDragging.value = true;
  startX.value = e.touches[0].clientX;
  document.addEventListener('touchmove', onTouch);
  document.addEventListener('touchend', stopTouch);
};

const onDrag = (e: MouseEvent) => {
  if (!isDragging.value) return;
  updateProgress(e.clientX);
};

const onTouch = (e: TouchEvent) => {
  if (!isDragging.value) return;
  updateProgress(e.touches[0].clientX);
};

const updateProgress = (clientX: number) => {
  const trackElement = document.querySelector('.slider-track') as HTMLElement;
  if (!trackElement) return;

  const rect = trackElement.getBoundingClientRect();
  const newProgress = Math.max(0, Math.min(100, ((clientX - rect.left) / rect.width) * 100));

  progress.value = newProgress;

  if (newProgress >= props.threshold && !props.modelValue) {
    emit('update:modelValue', true);
  } else if (newProgress < props.threshold && props.modelValue) {
    emit('update:modelValue', false);
  }
};

const stopDrag = () => {
  isDragging.value = false;
  document.removeEventListener('mousemove', onDrag);
  document.removeEventListener('mouseup', stopDrag);

  // 如果没有完成，重置进度
  if (!isCompleted.value) {
    progress.value = 0;
    emit('update:modelValue', false);
  }
};

const stopTouch = () => {
  isDragging.value = false;
  document.removeEventListener('touchmove', onTouch);
  document.removeEventListener('touchend', stopTouch);

  // 如果没有完成，重置进度
  if (!isCompleted.value) {
    progress.value = 0;
    emit('update:modelValue', false);
  }
};

// Reset method
const reset = () => {
  progress.value = 0;
  emit('update:modelValue', false);
};

// Expose reset method
defineExpose({
  reset
});
</script>

<style scoped>
.slider-captcha {
  width: 100%;
  margin: 0.5rem 0;
}

.slider-track {
  position: relative;
  width: 100%;
  height: 48px;
  background: rgba(255, 255, 255, 0.6);
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 8px;
  overflow: hidden;
  cursor: pointer;
  backdrop-filter: blur(8px);
  transition: all 0.3s ease;
}

.slider-track:hover {
  border-color: rgba(15, 76, 92, 0.3);
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(15, 76, 92, 0.15);
}

.slider-track.completed {
  border-color: rgba(34, 197, 94, 0.5);
  background: rgba(34, 197, 94, 0.1);
}

.slider-bg {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, rgba(15, 76, 92, 0.08) 0%, rgba(15, 76, 92, 0.04) 100%);
}

.slider-progress {
  position: absolute;
  top: 0;
  left: 0;
  height: 100%;
  background: linear-gradient(90deg, rgba(15, 76, 92, 0.6) 0%, rgba(15, 76, 92, 0.4) 100%);
  transition: width 0.1s ease;
}

.slider-track.completed .slider-progress {
  background: linear-gradient(90deg, rgba(34, 197, 94, 0.6) 0%, rgba(34, 197, 94, 0.4) 100%);
}

.slider-thumb {
  position: absolute;
  top: 50%;
  transform: translate(-50%, -50%);
  width: 40px;
  height: 40px;
  background: white;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
  cursor: grab;
  transition: all 0.2s ease;
  z-index: 2;
}

.slider-thumb:active {
  cursor: grabbing;
  transform: translate(-50%, -50%) scale(1.1);
}

.slider-thumb.completed {
  background: rgba(34, 197, 94, 1);
  color: white;
}

.thumb-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  color: #6b7280;
  transition: color 0.2s ease;
}

.slider-thumb.completed .thumb-icon {
  color: white;
}

.slider-text {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  font-size: 14px;
  font-weight: 500;
  color: #374151;
  pointer-events: none;
  transition: all 0.3s ease;
  z-index: 1;
}

.slider-track.completed .slider-text {
  color: rgba(34, 197, 94, 1);
}

.success-text {
  color: rgba(34, 197, 94, 1) !important;
  font-weight: 600;
}

/* 深色主题适配 */
@media (prefers-color-scheme: dark) {
  .slider-track {
    background: rgba(31, 41, 55, 0.6);
    border-color: rgba(75, 85, 99, 0.3);
  }

  .slider-track:hover {
    border-color: rgba(15, 76, 92, 0.4);
  }

  .slider-track.completed {
    border-color: rgba(34, 197, 94, 0.6);
    background: rgba(34, 197, 94, 0.1);
  }

  .slider-bg {
    background: linear-gradient(90deg, rgba(15, 76, 92, 0.12) 0%, rgba(15, 76, 92, 0.06) 100%);
  }

  .slider-thumb {
    background: #374151;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.3);
  }

  .slider-thumb.completed {
    background: rgba(34, 197, 94, 1);
  }

  .thumb-icon {
    color: #9ca3af;
  }

  .slider-text {
    color: #d1d5db;
  }

  .slider-track.completed .slider-text {
    color: rgba(34, 197, 94, 1);
  }
}

/* 响应式设计 */
@media (max-width: 640px) {
  .slider-track {
    height: 44px;
  }

  .slider-thumb {
    width: 36px;
    height: 36px;
  }

  .slider-text {
    font-size: 13px;
  }
}
</style>
