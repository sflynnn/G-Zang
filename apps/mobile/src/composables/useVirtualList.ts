/**
 * 虚拟列表 Composable
 * 用于优化大数据量列表的渲染性能
 */
import { ref, computed, onMounted, onUnmounted, type Ref } from 'vue'

interface VirtualListOptions {
  itemHeight: number
  buffer?: number
}

interface VirtualListResult {
  visibleItems: any[]
  totalHeight: number
  offsetY: number
  startIndex: number
  endIndex: number
}

export function useVirtualList<T>(
  items: Ref<T[]>,
  options: VirtualListOptions
): VirtualListResult & {
  containerRef: Ref<HTMLElement | null>
  handleScroll: () => void
  scrollTo: (index: number) => void
} {
  const containerRef = ref<HTMLElement | null>(null)
  const scrollTop = ref(0)
  const containerHeight = ref(0)

  const { itemHeight, buffer = 5 } = options

  // 计算可见区域的起止索引
  const startIndex = computed(() => {
    const start = Math.floor(scrollTop.value / itemHeight) - buffer
    return Math.max(0, start)
  })

  const endIndex = computed(() => {
    const visibleCount = Math.ceil(containerHeight.value / itemHeight)
    const end = Math.floor(scrollTop.value / itemHeight) + visibleCount + buffer
    return Math.min(items.value.length, end)
  })

  // 获取可见项
  const visibleItems = computed(() => {
    return items.value.slice(startIndex.value, endIndex.value)
  })

  // 计算偏移量（用于绝对定位）
  const offsetY = computed(() => {
    return startIndex.value * itemHeight
  })

  // 总高度
  const totalHeight = computed(() => {
    return items.value.length * itemHeight
  })

  // 处理滚动
  const handleScroll = () => {
    if (containerRef.value) {
      scrollTop.value = containerRef.value.scrollTop
    }
  }

  // 滚动到指定索引
  const scrollTo = (index: number) => {
    if (containerRef.value) {
      containerRef.value.scrollTop = index * itemHeight
    }
  }

  // 更新容器高度
  const updateContainerHeight = () => {
    if (containerRef.value) {
      containerHeight.value = containerRef.value.clientHeight
    }
  }

  onMounted(() => {
    updateContainerHeight()
    window.addEventListener('resize', updateContainerHeight)
  })

  onUnmounted(() => {
    window.removeEventListener('resize', updateContainerHeight)
  })

  return {
    visibleItems: visibleItems.value,
    totalHeight: totalHeight.value,
    offsetY: offsetY.value,
    startIndex: startIndex.value,
    endIndex: endIndex.value,
    containerRef,
    handleScroll,
    scrollTo
  }
}

/**
 * 防抖 Composable
 */
export function useDebounce<T extends (...args: any[]) => any>(
  fn: T,
  delay: number = 300
) {
  let timeout: ReturnType<typeof setTimeout> | null = null

  const debouncedFn = (...args: Parameters<T>) => {
    if (timeout) {
      clearTimeout(timeout)
    }
    timeout = setTimeout(() => {
      fn(...args)
    }, delay)
  }

  const cancel = () => {
    if (timeout) {
      clearTimeout(timeout)
      timeout = null
    }
  }

  return {
    debouncedFn,
    cancel
  }
}

/**
 * 节流 Composable
 */
export function useThrottle<T extends (...args: any[]) => any>(
  fn: T,
  delay: number = 300
) {
  let lastTime = 0
  let timeout: ReturnType<typeof setTimeout> | null = null

  const throttledFn = (...args: Parameters<T>) => {
    const now = Date.now()
    const remaining = delay - (now - lastTime)

    if (remaining <= 0 || remaining > delay) {
      if (timeout) {
        clearTimeout(timeout)
        timeout = null
      }
      lastTime = now
      fn(...args)
    } else if (!timeout) {
      timeout = setTimeout(() => {
        lastTime = Date.now()
        timeout = null
        fn(...args)
      }, remaining)
    }
  }

  const cancel = () => {
    if (timeout) {
      clearTimeout(timeout)
      timeout = null
    }
    lastTime = 0
  }

  return {
    throttledFn,
    cancel
  }
}

export default useVirtualList
