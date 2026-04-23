<template>
  <view class="categories-page apple-style">
    <!-- Large Title Navigation -->
    <view class="nav-large-title">
      <view class="nav-header">
        <text class="nav-title">{{ t('category.title') }}</text>
        <view class="nav-add" @click="goToCreate">
          <AppleIcon name="plus" :size="20" color="var(--gzang-primary)" />
        </view>
      </view>
    </view>

    <!-- Type Tabs -->
    <view class="type-tabs">
      <view 
        class="type-tab"
        :class="{ active: currentType === 2 }"
        @click="currentType = 2"
      >
        <AppleIcon name="expense" :size="14" :color="currentType === 2 ? 'white' : 'var(--gzang-text-secondary)'" />
        <text>{{ t('accounting.expense') }}</text>
      </view>
      <view 
        class="type-tab"
        :class="{ active: currentType === 1 }"
        @click="currentType = 1"
      >
        <AppleIcon name="income" :size="14" :color="currentType === 1 ? 'white' : 'var(--gzang-text-secondary)'" />
        <text>{{ t('accounting.income') }}</text>
      </view>
    </view>

    <scroll-view class="main-content" scroll-y="true">
      <!-- Category Grid -->
      <view class="category-grid">
        <view 
          v-for="cat in currentCategories" 
          :key="cat.id"
          class="category-item"
          @click="goToEdit(cat)"
        >
          <view class="category-icon" :style="{ background: cat.color + '20' }">
            <AppleIcon :name="cat.icon || 'shopping'" :size="24" :color="cat.color" />
          </view>
          <text class="category-name">{{ cat.name }}</text>
          <text class="category-count">{{ cat.transactionCount || 0 }}笔</text>
        </view>
      </view>

      <!-- Empty State -->
      <view v-if="currentCategories.length === 0 && !loading" class="empty-state">
        <view class="empty-icon-wrapper">
          <AppleIcon name="list" :size="48" color="var(--gzang-text-tertiary)" />
        </view>
        <text class="empty-title">暂无分类</text>
        <button class="create-btn" @click="goToCreate">
          <AppleIcon name="plus" :size="16" color="white" />
          <text>添加分类</text>
        </button>
      </view>

      <view class="bottom-safe-area"></view>
    </scroll-view>
  </view>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useI18n } from 'vue-i18n'
import { useCategoryStore } from '@/stores/category'
import AppleIcon from '@/components/common/AppleIcon/index.vue'

const { t } = useI18n()
const categoryStore = useCategoryStore()

const loading = ref(false)
const currentType = ref(2)

const currentCategories = computed(() => {
  return currentType.value === 1 
    ? categoryStore.incomeCategories 
    : categoryStore.expenseCategories
})

const mockCategories = computed(() => {
  if (currentType.value === 1) {
    return [
      { id: 101, name: '工资', icon: 'income', color: '#06D6A0', transactionCount: 12 },
      { id: 102, name: '奖金', icon: 'star', color: '#FFD166', transactionCount: 3 },
      { id: 103, name: '兼职', icon: 'clock', color: '#118AB2', transactionCount: 5 },
      { id: 104, name: '理财', icon: 'chart', color: '#3A86FF', transactionCount: 8 },
    ]
  }
  return [
    { id: 201, name: '餐饮', icon: 'food', color: '#FB8B24', transactionCount: 45 },
    { id: 202, name: '交通', icon: 'transport', color: '#0F4C5C', transactionCount: 30 },
    { id: 203, name: '购物', icon: 'shopping', color: '#EF476F', transactionCount: 22 },
    { id: 204, name: '娱乐', icon: 'entertainment', color: '#9B59B6', transactionCount: 15 },
    { id: 205, name: '居住', icon: 'housing', color: '#06D6A0', transactionCount: 6 },
    { id: 206, name: '医疗', icon: 'medical', color: '#EF476F', transactionCount: 3 },
    { id: 207, name: '教育', icon: 'education', color: '#118AB2', transactionCount: 8 },
    { id: 208, name: '通讯', icon: 'communication', color: '#FFD166', transactionCount: 4 },
  ]
})

const goBack = () => uni.navigateBack()
const goToCreate = () => uni.navigateTo({ url: `/pages/categories/create?type=${currentType.value}` })
const goToEdit = (cat: any) => uni.showToast({ title: '编辑功能开发中', icon: 'none' })

onMounted(async () => {
  loading.value = true
  await categoryStore.fetchCategories()
  loading.value = false
})
</script>

<style lang="scss" scoped>
.categories-page {
  min-height: 100vh;
  background: var(--gzang-bg);
}

.nav-large-title {
  background: var(--gzang-bg);
  padding: 0 var(--apple-space-4);
  padding-top: calc(constant(safe-area-inset-top) + var(--apple-space-3));
}

.nav-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--apple-space-3);
}

.nav-title {
  font-size: var(--apple-text-4xl);
  font-weight: var(--apple-font-bold);
  color: var(--gzang-text-primary);
}

.nav-add {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background: var(--gzang-surface);
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: var(--apple-shadow-xs);
}

.type-tabs {
  display: flex;
  padding: var(--apple-space-3) var(--apple-space-4);
  gap: var(--apple-space-2);
}

.type-tab {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: var(--apple-space-2);
  padding: var(--apple-space-3);
  background: var(--gzang-surface);
  border-radius: var(--apple-radius-lg);
  font-size: var(--apple-text-sm);
  font-weight: var(--apple-font-medium);
  color: var(--gzang-text-secondary);
  transition: all var(--apple-duration-fast);
  
  &.active {
    background: var(--gzang-primary);
    color: white;
    box-shadow: var(--apple-shadow-sm);
  }
}

.main-content {
  padding: 0 var(--apple-space-4);
}

.category-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: var(--apple-space-3);
}

.category-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: var(--apple-space-4);
  background: var(--gzang-surface);
  border-radius: var(--apple-radius-xl);
  box-shadow: var(--apple-shadow-xs);
  transition: all var(--apple-duration-fast);
  
  &:active {
    transform: scale(0.95);
    background: var(--gzang-bg);
  }
}

.category-icon {
  width: 56px;
  height: 56px;
  border-radius: var(--apple-radius-lg);
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: var(--apple-space-3);
}

.category-name {
  font-size: var(--apple-text-sm);
  font-weight: var(--apple-font-medium);
  color: var(--gzang-text-primary);
  margin-bottom: 4px;
}

.category-count {
  font-size: var(--apple-text-xs);
  color: var(--gzang-text-tertiary);
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: var(--apple-space-10) var(--apple-space-4);
}

.empty-icon-wrapper {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  background: var(--gzang-bg);
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: var(--apple-space-4);
}

.empty-title {
  font-size: var(--apple-text-lg);
  font-weight: var(--apple-font-semibold);
  color: var(--gzang-text-primary);
  margin-bottom: var(--apple-space-5);
}

.create-btn {
  display: flex;
  align-items: center;
  gap: var(--apple-space-2);
  padding: var(--apple-space-3) var(--apple-space-5);
  background: var(--gzang-primary);
  color: white;
  border: none;
  border-radius: var(--apple-radius-full);
  font-size: var(--apple-text-sm);
  font-weight: var(--apple-font-semibold);
}

.bottom-safe-area {
  height: var(--apple-space-4);
}
</style>
