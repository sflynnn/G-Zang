<template>
  <view class="tag-selector">
    <!-- 标签标题 -->
    <view class="tag-header" v-if="showHeader">
      <text class="tag-title">标签</text>
      <view class="tag-hint" v-if="hint">
        <text class="hint-text">{{ hint }}</text>
      </view>
    </view>

    <!-- 标签网格 -->
    <view class="tag-grid">
      <!-- 已有标签 -->
      <view
        v-for="tag in displayTags"
        :key="tag"
        class="tag-item"
        :class="{ selected: isSelected(tag), custom: isCustomTag(tag) }"
        @click="toggleTag(tag)"
      >
        <text class="tag-text">{{ tag }}</text>
        <view v-if="isSelected(tag)" class="tag-check">
          <text class="check-icon">×</text>
        </view>
      </view>

      <!-- 添加自定义标签按钮 -->
      <view 
        v-if="showAddButton"
        class="tag-item add-tag"
        @click="showAddDialog"
      >
        <text class="add-icon">+</text>
        <text class="tag-text">添加</text>
      </view>
    </view>

    <!-- 已选标签展示 -->
    <view v-if="selectedTags.length > 0 && showSelectedList" class="selected-list">
      <text class="selected-label">已选：</text>
      <view 
        v-for="tag in selectedTags" 
        :key="tag"
        class="selected-tag"
      >
        <text class="selected-tag-text">{{ tag }}</text>
      </view>
    </view>

    <!-- 添加标签弹窗 -->
    <uni-popup ref="addPopup" type="bottom">
      <view class="add-dialog">
        <view class="dialog-header">
          <text class="dialog-cancel" @click="closeAddDialog">取消</text>
          <text class="dialog-title">添加标签</text>
          <text class="dialog-confirm" @click="confirmAddTag">确定</text>
        </view>
        <view class="dialog-content">
          <input
            v-model="newTagName"
            type="text"
            placeholder="输入标签名称"
            class="tag-input"
            maxlength="20"
            :focus="inputFocused"
          />
          <view class="color-picker" v-if="showColorPicker">
            <text class="color-label">选择颜色</text>
            <view class="color-list">
              <view
                v-for="color in colorOptions"
                :key="color"
                class="color-item"
                :class="{ selected: selectedColor === color }"
                :style="{ background: color }"
                @click="selectedColor = color"
              ></view>
            </view>
          </view>
        </view>
      </view>
    </uni-popup>
  </view>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import { useToast } from '@/composables/useToast'

const toast = useToast()

interface Props {
  modelValue: string[]
  suggestions?: string[]
  showHeader?: boolean
  showAddButton?: boolean
  showSelectedList?: boolean
  showColorPicker?: boolean
  hint?: string
  maxTags?: number
}

const props = withDefaults(defineProps<Props>(), {
  modelValue: () => [],
  suggestions: () => ['餐饮', '交通', '购物', '娱乐', '日常', '办公', '医疗', '教育'],
  showHeader: true,
  showAddButton: true,
  showSelectedList: false,
  showColorPicker: false,
  hint: '',
  maxTags: 5
})

const emit = defineEmits<{
  'update:modelValue': [tags: string[]]
  'change': [tags: string[]]
  'add': [tag: string]
}>()

// 弹窗ref
const addPopup = ref<any>(null)
const inputFocused = ref(false)

// 新标签
const newTagName = ref('')
const selectedColor = ref('#0F4C5C')

// 颜色选项
const colorOptions = [
  '#EF476F', '#118AB2', '#E91E63', '#9C27B0',
  '#FF9800', '#F44336', '#3F51B5', '#00BCD4',
  '#4CAF50', '#795548', '#607D8B', '#0F4C5C'
]

// 已选标签
const selectedTags = computed({
  get: () => props.modelValue,
  set: (val) => {
    emit('update:modelValue', val)
    emit('change', val)
  }
})

// 合并展示标签（已有标签 + 自定义标签）
const displayTags = computed(() => {
  const baseTags = props.suggestions
  const customTags = selectedTags.value.filter(t => !baseTags.includes(t))
  return [...baseTags, ...customTags].slice(0, 12)
})

// 判断是否已选中
const isSelected = (tag: string) => selectedTags.value.includes(tag)

// 判断是否是自定义标签
const isCustomTag = (tag: string) => {
  return !props.suggestions.includes(tag)
}

// 切换标签选中状态
const toggleTag = (tag: string) => {
  const current = [...selectedTags.value]
  const index = current.indexOf(tag)
  
  if (index > -1) {
    // 取消选中
    current.splice(index, 1)
  } else {
    // 选中（检查上限）
    if (current.length >= props.maxTags) {
      toast.warning(`最多添加${props.maxTags}个标签`)
      return
    }
    current.push(tag)
  }
  
  selectedTags.value = current
}

// 显示添加弹窗
const showAddDialog = () => {
  newTagName.value = ''
  selectedColor.value = '#0F4C5C'
  inputFocused.value = true
  addPopup.value?.open()
}

// 关闭添加弹窗
const closeAddDialog = () => {
  addPopup.value?.close()
  inputFocused.value = false
}

// 确认添加标签
const confirmAddTag = () => {
  const name = newTagName.value.trim()
  if (!name) {
    toast.warning('请输入标签名称')
    return
  }
  
  if (selectedTags.value.includes(name)) {
    toast.warning('该标签已存在')
    return
  }
  
  if (selectedTags.value.length >= props.maxTags) {
    toast.warning(`最多添加${props.maxTags}个标签`)
    return
  }
  
  emit('add', name)
  selectedTags.value = [...selectedTags.value, name]
  closeAddDialog()
}

// 移除标签
const removeTag = (tag: string) => {
  const current = selectedTags.value.filter(t => t !== tag)
  selectedTags.value = current
}
</script>

<style lang="scss" scoped>
.tag-selector {
  width: 100%;
}

.tag-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 20rpx;
}

.tag-title {
  font-size: var(--apple-text-base);
  font-weight: var(--apple-font-semibold);
  color: var(--gzang-text-primary);
}

.tag-hint {
  .hint-text {
    font-size: var(--apple-text-xs);
    color: var(--gzang-text-tertiary);
  }
}

.tag-grid {
  display: flex;
  flex-wrap: wrap;
  gap: 16rpx;
}

.tag-item {
  position: relative;
  display: flex;
  align-items: center;
  gap: 6rpx;
  padding: 12rpx 20rpx;
  background: var(--gzang-bg);
  border-radius: var(--apple-radius-full);
  font-size: var(--apple-text-sm);
  color: var(--gzang-text-secondary);
  transition: all var(--apple-duration-fast);
  
  &.selected {
    background: rgba(15, 76, 92, 0.1);
    color: var(--gzang-primary);
    font-weight: var(--apple-font-medium);
    border: 1px solid var(--gzang-primary);
  }
  
  &.custom {
    background: linear-gradient(135deg, rgba(251, 139, 36, 0.1) 0%, rgba(255, 154, 60, 0.1) 100%);
    border: 1px dashed var(--gzang-secondary);
  }
  
  &.add-tag {
    border: 1px dashed var(--gzang-border);
    color: var(--gzang-text-tertiary);
    
    &:active {
      background: var(--gzang-bg);
    }
  }
  
  &:active {
    transform: scale(0.95);
  }
}

.tag-text {
  line-height: 1;
}

.add-icon {
  font-size: 24rpx;
  font-weight: 300;
}

.tag-check {
  margin-left: 4rpx;
}

.check-icon {
  font-size: 18rpx;
  font-weight: bold;
}

.selected-list {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 12rpx;
  margin-top: 20rpx;
  padding: 16rpx;
  background: var(--gzang-bg);
  border-radius: var(--apple-radius-lg);
}

.selected-label {
  font-size: var(--apple-text-xs);
  color: var(--gzang-text-tertiary);
}

.selected-tag {
  display: flex;
  align-items: center;
  gap: 6rpx;
  padding: 8rpx 16rpx;
  background: var(--gzang-primary);
  border-radius: var(--apple-radius-full);
}

.selected-tag-text {
  font-size: var(--apple-text-xs);
  color: white;
}

// 添加弹窗
.add-dialog {
  background: var(--gzang-surface);
  border-radius: var(--apple-radius-xl) var(--apple-radius-xl) 0 0;
  overflow: hidden;
}

.dialog-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 24rpx 32rpx;
  border-bottom: 1px solid var(--gzang-border);
}

.dialog-cancel,
.dialog-confirm {
  font-size: var(--apple-text-base);
  font-weight: var(--apple-font-medium);
}

.dialog-cancel {
  color: var(--gzang-text-secondary);
}

.dialog-confirm {
  color: var(--gzang-secondary);
}

.dialog-title {
  font-size: var(--apple-text-lg);
  font-weight: var(--apple-font-semibold);
  color: var(--gzang-text-primary);
}

.dialog-content {
  padding: 32rpx;
}

.tag-input {
  width: 100%;
  height: 88rpx;
  padding: 0 24rpx;
  background: var(--gzang-bg);
  border-radius: var(--apple-radius-lg);
  font-size: var(--apple-text-base);
  color: var(--gzang-text-primary);
  border: none;
  outline: none;

  &::placeholder {
    color: var(--gzang-text-tertiary);
  }
}

.color-picker {
  margin-top: 32rpx;
}

.color-label {
  display: block;
  font-size: var(--apple-text-sm);
  color: var(--gzang-text-secondary);
  margin-bottom: 16rpx;
}

.color-list {
  display: flex;
  flex-wrap: wrap;
  gap: 16rpx;
}

.color-item {
  width: 56rpx;
  height: 56rpx;
  border-radius: 50%;
  border: 4rpx solid transparent;
  transition: all var(--apple-duration-fast);
  
  &.selected {
    border-color: var(--gzang-text-primary);
    transform: scale(1.1);
  }
  
  &:active {
    transform: scale(0.95);
  }
}
</style>
