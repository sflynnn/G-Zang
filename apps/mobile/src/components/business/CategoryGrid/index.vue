<template>
  <view class="category-grid">
    <!-- 顶部切换 -->
    <view class="grid-header" v-if="showHeader">
      <view class="tab-switch">
        <view
          class="tab-item"
          :class="{ 'tab-item--active': activeType === 2 }"
          @click="switchType(2)"
        >
          {{ t('accounting.expense') }}
        </view>
        <view
          class="tab-item"
          :class="{ 'tab-item--active': activeType === 1 }"
          @click="switchType(1)"
        >
          {{ t('accounting.income') }}
        </view>
      </view>
    </view>

    <scroll-view scroll-y class="grid-scroll">
      <!-- 一级分类网格 -->
      <view class="grid-content">
        <view
          v-for="category in displayCategories"
          :key="category.id"
          class="grid-item"
          :class="{ 'grid-item--selected': selectedId === category.id }"
          @click="handleSelectCategory(category)"
        >
          <view
            class="item-icon"
            :style="{ backgroundColor: getIconBg(category.icon) }"
          >
            <uni-icons
              :type="getIconType(category.icon)"
              :size="22"
              :color="getIconColor(category.icon)"
            />
          </view>
          <text class="item-name">{{ category.name }}</text>
          <text class="item-amount" :class="{ expense: activeType === 2, income: activeType === 1 }">
            {{ formatAmount(category.amount) }}
          </text>
        </view>

        <!-- 添加分类按钮 -->
        <view class="grid-item add-item" @click="handleAddCategory" v-if="!readonly">
          <view class="item-icon add-icon">
            <uni-icons type="plus" size="22" color="#999"></uni-icons>
          </view>
          <text class="item-name">{{ t('common.create') }}</text>
        </view>
      </view>

      <!-- 子分类展开面板 -->
      <view v-if="expandedCategory" class="sub-categories-panel">
        <view class="panel-header">
          <view class="panel-title">
            <uni-icons
              :type="getIconType(expandedCategory.icon)"
              :size="18"
              :color="getIconColor(expandedCategory.icon)"
            />
            <text class="panel-name">{{ expandedCategory.name }}</text>
          </view>
          <view class="panel-actions">
            <view class="action-btn" @click="handleAddSubCategory">
              <uni-icons type="plus" size="16" color="#0F4C5C"></uni-icons>
              <text>{{ t('category.addSubCategory') || '添加子类' }}</text>
            </view>
            <view class="close-btn" @click="closePanel">
              <uni-icons type="close" size="16" color="#999"></uni-icons>
            </view>
          </view>
        </view>

        <view class="sub-category-list">
          <view
            v-for="sub in expandedCategory.children"
            :key="sub.id"
            class="sub-category-item"
            :class="{ 'sub-category-item--selected': selectedId === sub.id }"
            @click="handleSelectSubCategory(sub)"
          >
            <view class="sub-info">
              <uni-icons
                v-if="sub.icon"
                :type="getIconType(sub.icon)"
                :size="16"
                :color="getIconColor(sub.icon)"
              />
              <text class="sub-name">{{ sub.name }}</text>
            </view>
            <view class="sub-amount" :class="{ expense: activeType === 2, income: activeType === 1 }">
              {{ formatAmount(sub.amount) }}
            </view>
            <view v-if="!readonly && !sub.isSystem" class="delete-btn" @click.stop="handleDeleteSubCategory(sub)">
              <uni-icons type="close" size="12" color="#999"></uni-icons>
            </view>
          </view>

          <!-- 空状态 -->
          <view v-if="!expandedCategory.children?.length" class="sub-empty">
            <text>{{ t('common.noData') }}</text>
            <text class="sub-empty-hint">{{ t('category.addSubCategoryHint') || '点击上方"添加子类"创建' }}</text>
          </view>
        </view>
      </view>
    </scroll-view>

    <!-- 添加子类弹窗 -->
    <uni-popup ref="addSubPopup" type="bottom" :is-mask-click="true">
      <view class="add-sub-popup">
        <view class="popup-header">
          <text class="popup-cancel" @click="closeAddSubPopup">{{ t('common.cancel') }}</text>
          <text class="popup-title">{{ t('category.addSubCategory') || '添加子类' }}</text>
          <text class="popup-confirm" @click="confirmAddSubCategory">{{ t('common.confirm') }}</text>
        </view>
        <view class="popup-body">
          <input
            v-model="newSubCategoryName"
            class="sub-input"
            placeholder="输入子类名称，如：早餐"
            maxlength="10"
            focus
          />
          <view class="preset-list">
            <text class="preset-label">预设选项：</text>
            <view class="preset-tags">
              <text
                v-for="preset in presets"
                :key="preset"
                class="preset-tag"
                @click="newSubCategoryName = preset"
              >{{ preset }}</text>
            </view>
          </view>
        </view>
      </view>
    </uni-popup>
  </view>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import { useI18n } from 'vue-i18n'
import type { Category } from '@/types/transaction'

const { t } = useI18n()

interface Props {
  categories: Category[]
  modelValue?: number
  activeTab?: 1 | 2
  showHeader?: boolean
  readonly?: boolean   // 只读模式（用于显示统计，不允许选择/操作）
  bookId?: number     // 账本ID（用于获取该账本的数据）
}

const props = withDefaults(defineProps<Props>(), {
  categories: () => [],
  modelValue: undefined,
  activeTab: 2,
  showHeader: false,
  readonly: false,
})

const emit = defineEmits<{
  'update:modelValue': [id: number | undefined]
  'update:activeTab': [type: 1 | 2]
  'select': [category: Category, isSubCategory: boolean]
  'addSubCategory': [parentId: number, name: string]
  'deleteSubCategory': [subCategory: Category]
}>()

const activeType = ref(props.activeTab)
const selectedId = ref<number | undefined>(props.modelValue)
const expandedCategory = ref<Category | null>(null)
const newSubCategoryName = ref('')
const addSubPopup = ref<any>(null)

// 预设子类名称
const presets = ['早餐', '午餐', '晚餐', '下午茶', '零食', '水果', '日用品', '服装', '娱乐', '通讯']

watch(() => props.modelValue, (val) => {
  selectedId.value = val
})

watch(() => props.activeTab, (val) => {
  activeType.value = val
  selectedId.value = undefined
  expandedCategory.value = null
})

watch(activeType, (val) => {
  emit('update:activeTab', val)
})

// 筛选当前类型的分类（一级）
const displayCategories = computed(() => {
  return props.categories.filter(cat => cat.type === activeType.value && !cat.parentId)
})

// 切换收支类型
const switchType = (type: 1 | 2) => {
  activeType.value = type
  selectedId.value = undefined
  expandedCategory.value = null
}

// 选择一级分类
const handleSelectCategory = (category: Category) => {
  selectedId.value = category.id

  // 如果有子类，展开子类面板
  if (category.children && category.children.length > 0) {
    expandedCategory.value = { ...category }
  }

  emit('update:modelValue', category.id)
  emit('select', category, false)
}

// 选择子类
const handleSelectSubCategory = (sub: Category) => {
  selectedId.value = sub.id
  emit('update:modelValue', sub.id)
  emit('select', sub, true)
  closePanel()
}

// 关闭子类面板
const closePanel = () => {
  expandedCategory.value = null
}

// 添加一级分类
const handleAddCategory = () => {
  emit('addSubCategory', 0, '') // 0 表示添加一级分类
}

// 添加子类
const handleAddSubCategory = () => {
  addSubPopup.value.open()
}

const closeAddSubPopup = () => {
  addSubPopup.value.close()
  newSubCategoryName.value = ''
}

const confirmAddSubCategory = () => {
  if (!newSubCategoryName.value.trim()) return
  if (expandedCategory.value) {
    emit('addSubCategory', expandedCategory.value.id, newSubCategoryName.value.trim())
  }
  closeAddSubPopup()
}

// 删除子类
const handleDeleteSubCategory = (sub: Category) => {
  uni.showModal({
    title: t('common.confirm') || '确认',
    content: `${t('common.confirmDelete') || '确定要删除'}"${sub.name}"吗？`,
    confirmColor: '#EF476F',
    success: (res) => {
      if (res.confirm) {
        emit('deleteSubCategory', sub)
      }
    },
  })
}

// 格式化金额
const formatAmount = (amount?: number) => {
  if (amount === undefined || amount === null || amount === 0) return '-'
  const prefix = amount > 0 ? '' : ''
  return prefix + Math.abs(amount).toFixed(2)
}

// 图标映射
const getIconType = (icon?: string): string => {
  const iconMap: Record<string, string> = {
    food: 'paperplane',
    transport: 'location',
    shopping: 'cart',
    entertainment: 'star',
    medical: 'first-aid-box',
    education: 'bookmark',
    housing: 'home',
    communication: 'phone',
    salary: 'wallet',
    investment: 'chartbars',
    gift: 'gift',
    other: 'circle',
    // 中文字标
    餐饮: 'paperplane',
    交通: 'location',
    购物: 'cart',
    娱乐: 'star',
    医疗: 'first-aid-box',
    教育: 'bookmark',
    居住: 'home',
    通讯: 'phone',
    工资: 'wallet',
    投资: 'chartbars',
    社交: 'gift',
  }
  return iconMap[icon || ''] || icon || 'circle'
}

const getIconBg = (icon?: string): string => {
  const colorMap: Record<string, string> = {
    food: '#FFF3E0',
    transport: '#E3F2FD',
    shopping: '#FCE4EC',
    entertainment: '#F3E5F5',
    medical: '#FFEBEE',
    education: '#E8F5E9',
    housing: '#FFF8E1',
    communication: '#E0F7FA',
    salary: '#E8F5E9',
    investment: '#FFF8E1',
    gift: '#FCE4EC',
    other: '#F5F5F5',
    餐饮: '#FFF3E0',
    交通: '#E3F2FD',
    购物: '#FCE4EC',
    娱乐: '#F3E5F5',
    医疗: '#FFEBEE',
    教育: '#E8F5E9',
    居住: '#FFF8E1',
    通讯: '#E0F7FA',
    工资: '#E8F5E9',
    投资: '#FFF8E1',
    社交: '#FCE4EC',
  }
  return colorMap[icon || ''] || '#F5F5F5'
}

const getIconColor = (icon?: string): string => {
  const colorMap: Record<string, string> = {
    food: '#FF9800',
    transport: '#2196F3',
    shopping: '#E91E63',
    entertainment: '#9C27B0',
    medical: '#F44336',
    education: '#4CAF50',
    housing: '#FFC107',
    communication: '#00BCD4',
    salary: '#4CAF50',
    investment: '#FFC107',
    gift: '#E91E63',
    other: '#9E9E9E',
    餐饮: '#FF9800',
    交通: '#2196F3',
    购物: '#E91E63',
    娱乐: '#9C27B0',
    医疗: '#F44336',
    教育: '#4CAF50',
    居住: '#FFC107',
    通讯: '#00BCD4',
    工资: '#4CAF50',
    投资: '#FFC107',
    社交: '#E91E63',
  }
  return colorMap[icon || ''] || '#9E9E9E'
}
</script>

<style scoped lang="scss">
.category-grid {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.grid-header {
  padding: 16rpx 24rpx;
  background-color: var(--gzang-surface, #fff);
  border-bottom: 1rpx solid var(--gzang-border, #E5E5E5);
}

.tab-switch {
  display: flex;
  background-color: #f5f5f5;
  border-radius: 12rpx;
  padding: 4rpx;
}

.tab-item {
  flex: 1;
  text-align: center;
  padding: 12rpx 0;
  font-size: 28rpx;
  color: var(--gzang-text-secondary, #666);
  border-radius: 8rpx;
  transition: all 0.2s;

  &--active {
    background-color: var(--gzang-surface, #fff);
    color: var(--gzang-primary, #0F4C5C);
    font-weight: 600;
    box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.06);
  }
}

.grid-scroll {
  flex: 1;
  overflow: hidden;
}

.grid-content {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16rpx;
  padding: 24rpx;
}

.grid-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 6rpx;
  padding: 16rpx 8rpx;
  border-radius: 16rpx;
  background: #fff;
  transition: all 0.2s;

  &--selected {
    background-color: rgba(15, 76, 92, 0.08);

    .item-icon {
      transform: scale(1.1);
    }

    .item-name {
      color: var(--gzang-primary, #0F4C5C);
      font-weight: 600;
    }
  }

  &:active {
    opacity: 0.7;
  }

  &.add-item {
    background: #F8F9FA;
    border: 2rpx dashed #ddd;
  }
}

.item-icon {
  width: 80rpx;
  height: 80rpx;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: transform 0.2s;

  &.add-icon {
    background: #F0F0F0;
  }
}

.item-name {
  font-size: 24rpx;
  color: var(--gzang-text-secondary, #666);
  text-align: center;
  max-width: 120rpx;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.item-amount {
  font-size: 20rpx;
  font-weight: 500;

  &.expense {
    color: #EF476F;
  }

  &.income {
    color: #06D6A0;
  }
}

// 子分类面板
.sub-categories-panel {
  background: #fff;
  border-top: 16rpx solid #F8F9FA;
  margin-top: 8rpx;
}

.panel-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20rpx 24rpx;
  border-bottom: 1rpx solid #F0F0F0;
}

.panel-title {
  display: flex;
  align-items: center;
  gap: 8rpx;
}

.panel-name {
  font-size: 28rpx;
  font-weight: 600;
  color: #333;
}

.panel-actions {
  display: flex;
  align-items: center;
  gap: 16rpx;
}

.action-btn {
  display: flex;
  align-items: center;
  gap: 4rpx;
  padding: 8rpx 16rpx;
  background: rgba(15, 76, 92, 0.08);
  border-radius: 8rpx;
  font-size: 24rpx;
  color: #0F4C5C;

  &:active {
    background: rgba(15, 76, 92, 0.15);
  }
}

.close-btn {
  width: 40rpx;
  height: 40rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #F5F5F5;
  border-radius: 50%;

  &:active {
    background: #E5E5E5;
  }
}

.sub-category-list {
  padding: 16rpx 24rpx;
  display: flex;
  flex-wrap: wrap;
  gap: 16rpx;
}

.sub-category-item {
  display: flex;
  align-items: center;
  gap: 8rpx;
  padding: 12rpx 20rpx;
  background: #F8F9FA;
  border-radius: 24rpx;
  border: 2rpx solid transparent;
  transition: all 0.2s;

  &--selected {
    background: rgba(15, 76, 92, 0.1);
    border-color: #0F4C5C;
  }

  &:active {
    opacity: 0.7;
  }
}

.sub-info {
  display: flex;
  align-items: center;
  gap: 6rpx;
}

.sub-name {
  font-size: 26rpx;
  color: #333;
}

.sub-amount {
  font-size: 22rpx;
  font-weight: 500;
  margin-left: 8rpx;

  &.expense {
    color: #EF476F;
  }

  &.income {
    color: #06D6A0;
  }
}

.delete-btn {
  width: 28rpx;
  height: 28rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #E5E5E5;
  border-radius: 50%;
  margin-left: 4rpx;

  &:active {
    background: #ccc;
  }
}

.sub-empty {
  width: 100%;
  text-align: center;
  padding: 40rpx 0;
  font-size: 26rpx;
  color: #999;
}

.sub-empty-hint {
  display: block;
  font-size: 24rpx;
  color: #ccc;
  margin-top: 8rpx;
}

// 添加子类弹窗
.add-sub-popup {
  background: #fff;
  border-radius: 24rpx 24rpx 0 0;
  overflow: hidden;
}

.popup-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20rpx 32rpx;
  border-bottom: 1rpx solid #F0F0F0;
}

.popup-cancel {
  font-size: 28rpx;
  color: #999;
}

.popup-title {
  font-size: 30rpx;
  font-weight: 600;
  color: #333;
}

.popup-confirm {
  font-size: 28rpx;
  color: #0F4C5C;
  font-weight: 500;
}

.popup-body {
  padding: 32rpx;
}

.sub-input {
  width: 100%;
  padding: 24rpx;
  background: #F8F9FA;
  border-radius: 12rpx;
  font-size: 30rpx;
  border: 1rpx solid transparent;

  &:focus {
    border-color: #0F4C5C;
    background: #fff;
  }
}

.preset-list {
  margin-top: 24rpx;
}

.preset-label {
  font-size: 26rpx;
  color: #999;
  margin-bottom: 16rpx;
  display: block;
}

.preset-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 12rpx;
}

.preset-tag {
  padding: 10rpx 20rpx;
  background: #F0F0F0;
  border-radius: 20rpx;
  font-size: 24rpx;
  color: #666;

  &:active {
    background: #E5E5E5;
  }
}
</style>
