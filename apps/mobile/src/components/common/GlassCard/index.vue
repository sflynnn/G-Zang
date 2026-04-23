<script setup lang="ts">
/**
 * GlassCard - Apple Style Glassmorphism Card Component
 * 
 * Features:
 * - Frosted glass effect
 * - Multiple variants (default, elevated, gradient)
 * - Gradient direction support
 */

interface Props {
  variant?: 'default' | 'elevated' | 'gradient' | 'glass'
  gradientDirection?: string
  gradientFrom?: string
  gradientTo?: string
  radius?: string
  padding?: string
}

const props = withDefaults(defineProps<Props>(), {
  variant: 'default',
  gradientDirection: '135deg',
  gradientFrom: 'var(--gzang-primary)',
  gradientTo: 'var(--gzang-primary-light)',
  radius: 'var(--apple-radius-xl)',
  padding: '20px'
})

const cardClass = computed(() => {
  return `glass-card--${props.variant}`
})
</script>

<script lang="ts">
import { computed } from 'vue'
export default {
  options: {
    styleIsolation: 'shared'
  }
}
</script>

<template>
  <view 
    class="glass-card"
    :class="cardClass"
    :style="{
      '--card-radius': radius,
      '--card-padding': padding,
      '--gradient-direction': gradientDirection,
      '--gradient-from': gradientFrom,
      '--gradient-to': gradientTo
    }"
  >
    <slot />
  </view>
</template>

<style lang="scss" scoped>
.glass-card {
  border-radius: var(--card-radius, 24px);
  padding: var(--card-padding, 20px);
  transition: all 250ms cubic-bezier(0.25, 0.1, 0.25, 1);
  
  // Default style
  &--default {
    background: var(--gzang-surface);
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  }
  
  // Elevated style
  &--elevated {
    background: var(--gzang-surface);
    box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1), 0 2px 6px rgba(0, 0, 0, 0.05);
  }
  
  // Gradient style
  &--gradient {
    background: linear-gradient(
      var(--gradient-direction, 135deg),
      var(--gradient-from, var(--gzang-primary)) 0%,
      var(--gradient-to, var(--gzang-primary-light)) 100%
    );
    color: white;
    box-shadow: 0 4px 16px rgba(0, 0, 0, 0.15);
  }
  
  // Glass style (frosted glass)
  &--glass {
    background: var(--apple-glass-bg, rgba(255, 255, 255, 0.72));
    backdrop-filter: blur(20px);
    -webkit-backdrop-filter: blur(20px);
    border: 1px solid var(--apple-glass-border, rgba(255, 255, 255, 0.5));
    box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
  }
}

// Dark mode adjustments
@media (prefers-color-scheme: dark) {
  .glass-card {
    &--default,
    &--elevated {
      background: var(--gzang-surface);
    }
    
    &--glass {
      background: var(--apple-glass-bg, rgba(30, 30, 30, 0.72));
      border-color: rgba(255, 255, 255, 0.1);
    }
  }
}

[data-theme="dark"] {
  .glass-card {
    &--default,
    &--elevated {
      background: var(--gzang-surface);
    }
    
    &--glass {
      background: var(--apple-glass-bg, rgba(30, 30, 30, 0.72));
      border-color: rgba(255, 255, 255, 0.1);
    }
  }
}
</style>
