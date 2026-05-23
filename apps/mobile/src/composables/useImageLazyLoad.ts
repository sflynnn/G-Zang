/**
 * 图片懒加载 Composable
 * 用于优化移动端图片加载性能
 */
import { ref, onMounted, onUnmounted, watch, type Ref } from 'vue'

interface LazyLoadOptions {
  root?: HTMLElement | null
  rootMargin?: string
  threshold?: number
}

export function useImageLazyLoad(
  imageRef: Ref<HTMLElement | null>,
  options: LazyLoadOptions = {}
) {
  const isLoaded = ref(false)
  const isInView = ref(false)
  const error = ref(false)

  let observer: IntersectionObserver | null = null

  const defaultOptions: IntersectionObserverInit = {
    root: options.root || null,
    rootMargin: options.rootMargin || '50px 0px',
    threshold: options.threshold || 0.1
  }

  const handleIntersection = (entries: IntersectionObserverEntry[]) => {
    entries.forEach(entry => {
      if (entry.isIntersecting) {
        isInView.value = true
        // 图片进入视口时断开观察，避免重复触发
        if (observer && imageRef.value) {
          observer.unobserve(imageRef.value)
        }
      }
    })
  }

  const initObserver = () => {
    if (typeof IntersectionObserver !== 'undefined') {
      observer = new IntersectionObserver(handleIntersection, defaultOptions)
      if (imageRef.value) {
        observer.observe(imageRef.value)
      }
    } else {
      // 浏览器不支持时，直接显示图片
      isInView.value = true
    }
  }

  const onImageLoad = () => {
    isLoaded.value = true
    error.value = false
  }

  const onImageError = () => {
    error.value = true
    isLoaded.value = false
  }

  // 监听 ref 变化
  watch(imageRef, (newRef, oldRef) => {
    if (oldRef && observer) {
      observer.unobserve(oldRef)
    }
    if (newRef) {
      if (observer) {
        observer.observe(newRef)
      } else {
        initObserver()
      }
    }
  })

  onMounted(() => {
    initObserver()
  })

  onUnmounted(() => {
    if (observer) {
      observer.disconnect()
      observer = null
    }
  })

  return {
    isLoaded,
    isInView,
    error,
    onImageLoad,
    onImageError
  }
}

/**
 * 图片占位符组件
 */
export function useImagePlaceholder() {
  const placeholderStyle = {
    background: 'linear-gradient(90deg, #f0f0f0 25%, #e0e0e0 50%, #f0f0f0 75%)',
    backgroundSize: '200% 100%',
    animation: 'shimmer 1.5s infinite'
  }

  return {
    placeholderStyle
  }
}

export default useImageLazyLoad
