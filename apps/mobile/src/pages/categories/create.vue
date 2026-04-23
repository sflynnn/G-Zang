<template>
  <view class="create-category-page">
    <uni-nav-bar left-icon="back" :title="isEdit ? '编辑分类' : '创建分类'" @clickLeft="goBack" />
    
    <view class="form-container">
      <!-- 分类类型 -->
      <view class="form-section">
        <view class="section-title">分类类型</view>
        <view class="type-grid">
          <view 
            :class="['type-cell', { active: formData.type === 2 }]"
            @click="formData.type = 2"
          >
            <view class="type-icon">📉</view>
            <view class="type-label">支出</view>
          </view>
          <view 
            :class="['type-cell', { active: formData.type === 1 }]"
            @click="formData.type = 1"
          >
            <view class="type-icon">📈</view>
            <view class="type-label">收入</view>
          </view>
        </view>
      </view>

      <!-- 图标选择 -->
      <view class="form-section">
        <view class="section-title">选择图标</view>
        <view class="icon-grid">
          <view 
            v-for="icon in availableIcons" 
            :key="icon"
            :class="['icon-cell', { active: selectedIcon === icon }]"
            @click="selectedIcon = icon"
          >
            {{ icon }}
          </view>
        </view>
      </view>

      <!-- 分类名称 -->
      <view class="form-section">
        <view class="section-title">分类名称</view>
        <input 
          class="text-input" 
          v-model="formData.name" 
          placeholder="请输入分类名称"
          maxlength="10"
        />
      </view>

      <!-- 颜色选择 -->
      <view class="form-section">
        <view class="section-title">选择颜色</view>
        <view class="color-grid">
          <view 
            v-for="color in colors" 
            :key="color.value"
            :class="['color-cell', { active: selectedColor === color.value }]"
            :style="{ background: color.value }"
            @click="selectedColor = color.value"
          >
            <text v-if="selectedColor === color.value" class="color-check">✓</text>
          </view>
        </view>
      </view>
    </view>

    <!-- 提交按钮 -->
    <view class="submit-bar">
      <button class="submit-btn" @click="handleSubmit" :loading="submitting">
        {{ submitting ? '保存中...' : '保存' }}
      </button>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { useCategoryStore } from '@/stores/category'

const categoryStore = useCategoryStore()

const submitting = ref(false)
const isEdit = ref(false)
const categoryId = ref<number | null>(null)
const selectedIcon = ref('📂')
const selectedColor = ref('#0F4C5C')

const formData = ref({
  type: 2,
  name: ''
})

const expenseIcons = ['🍜', '🚇', '🛒', '🎮', '💊', '📚', '🏠', '📱', '👔', '🎁', '☕', '🏋️', '✂️', '🎬', '🐱', '💇', '📦', '🎓']
const incomeIcons = ['💰', '💵', '🏦', '📈', '🎁', '💸', '🧧', '💼', '📊', '🔖']

const colors = [
  { value: '#0F4C5C' },
  { value: '#FB8B24' },
  { value: '#06D6A0' },
  { value: '#EF476F' },
  { value: '#FFD166' },
  { value: '#3A86FF' },
  { value: '#8338EC' },
  { value: '#118AB2' },
]

const availableIcons = computed(() => {
  return formData.value.type === 1 ? incomeIcons : expenseIcons
})

onLoad((options: any) => {
  if (options?.id) {
    isEdit.value = true
    categoryId.value = parseInt(options.id)
    // 加载分类数据
  }
  if (options?.type) {
    formData.value.type = parseInt(options.type)
    selectedIcon.value = formData.value.type === 1 ? '💰' : '🍜'
  }
})

function goBack() {
  uni.navigateBack()
}

async function handleSubmit() {
  if (!formData.value.name.trim()) {
    uni.showToast({ title: '请输入分类名称', icon: 'none' })
    return
  }

  submitting.value = true
  try {
    await categoryStore.createCategory({
      name: formData.value.name,
      type: formData.value.type,
      icon: selectedIcon.value
    })
    
    uni.showToast({ title: '保存成功', icon: 'success' })
    setTimeout(() => {
      goBack()
    }, 1500)
  } catch (error: any) {
    uni.showToast({ title: error.message || '保存失败', icon: 'none' })
  } finally {
    submitting.value = false
  }
}

function getIconBg(color: string): string {
  const bgMap: Record<string, string> = {
    '#0F4C5C': '#E3F2FD',
    '#FB8B24': '#FFF3E0',
    '#06D6A0': '#E8F5E9',
    '#EF476F': '#FFEBEE',
    '#FFD166': '#FFF8E1',
    '#3A86FF': '#E3F2FD',
    '#8338EC': '#F3E5F5',
    '#118AB2': '#E0F7FA',
  }
  return bgMap[color] || '#F8F9FA'
}
</script>

<style scoped lang="scss">
.create-category-page {
  min-height: 100vh;
  background-color: #F8F9FA;
  padding-bottom: 140rpx;
}

.form-container {
  padding: 24rpx;
}

.form-section {
  background: #fff;
  border-radius: 16rpx;
  padding: 24rpx;
  margin-bottom: 24rpx;
}

.section-title {
  font-size: 28rpx;
  font-weight: 600;
  color: #1F2937;
  margin-bottom: 20rpx;
}

.type-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16rpx;
}

.type-cell {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8rpx;
  padding: 32rpx;
  background: #F8F9FA;
  border-radius: 12rpx;
  border: 4rpx solid transparent;
  transition: all 0.2s;
  
  &.active {
    border-color: #0F4C5C;
    background: rgba(15, 76, 92, 0.08);
  }
  
  .type-icon {
    font-size: 48rpx;
  }
  
  .type-label {
    font-size: 28rpx;
    color: #666;
  }
  
  &.active .type-label {
    color: #0F4C5C;
    font-weight: 600;
  }
}

.icon-grid {
  display: grid;
  grid-template-columns: repeat(6, 1fr);
  gap: 16rpx;
}

.icon-cell {
  aspect-ratio: 1;
  background: #F8F9FA;
  border-radius: 16rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 40rpx;
  border: 4rpx solid transparent;
  transition: all 0.2s;
  
  &.active {
    border-color: #0F4C5C;
    background: rgba(15, 76, 92, 0.08);
  }
}

.text-input {
  width: 100%;
  padding: 24rpx 28rpx;
  background: #F8F9FA;
  border-radius: 12rpx;
  font-size: 28rpx;
  color: #333;
  border: 2rpx solid transparent;
  
  &:focus {
    border-color: #0F4C5C;
  }
}

.color-grid {
  display: flex;
  gap: 20rpx;
  flex-wrap: wrap;
}

.color-cell {
  width: 72rpx;
  height: 72rpx;
  border-radius: 50%;
  border: 4rpx solid transparent;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s;
  
  &.active {
    border-color: #333;
    transform: scale(1.1);
  }
  
  .color-check {
    color: #fff;
    font-size: 28rpx;
    font-weight: 700;
  }
}

.submit-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 32rpx;
  background: #fff;
  border-top: 1rpx solid #E5E5E5;
}

.submit-btn {
  width: 100%;
  height: 96rpx;
  background: #0F4C5C;
  border-radius: 24rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 32rpx;
  font-weight: 600;
  border: none;
  
  &[loading] {
    opacity: 0.7;
  }
}
</style>
