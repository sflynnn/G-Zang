<template>
  <div class="category-list min-h-screen p-4 md:p-6 lg:p-8">
    <!-- 页面标题 -->
    <div class="flex items-center justify-between mb-6">
      <div>
        <h1 class="text-2xl font-bold text-text-primary dark:text-dark-text-primary">{{ $t('category.listTitle') }}</h1>
        <p class="text-sm text-text-secondary dark:text-dark-text-secondary mt-1">{{ $t('category.listSubtitle') }}</p>
      </div>
      <button @click="openModal(null)" class="btn-primary flex items-center gap-2">
        <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4" />
        </svg>
        {{ $t('category.add') }}
      </button>
    </div>

    <!-- 分类标签页 -->
    <div class="flex gap-2 mb-6">
      <button
        v-for="tab in tabs"
        :key="tab.value"
        @click="activeTab = tab.value"
        class="px-4 py-2 rounded-xl text-sm font-medium transition-all"
        :class="activeTab === tab.value
          ? 'bg-secondary text-white'
          : 'bg-surface dark:bg-dark-surface text-text-secondary dark:text-dark-text-secondary hover:bg-gray-50 dark:hover:bg-dark-bg'"
      >
        {{ tab.label }}
      </button>
    </div>

    <!-- 分类网格 -->
    <div class="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-4">
      <div
        v-for="cat in filteredCategories"
        :key="cat.id"
        class="bg-surface dark:bg-dark-surface rounded-2xl p-4 shadow-gzang hover:shadow-lg transition-all cursor-pointer group"
        @click="openModal(cat)"
      >
        <div class="flex items-center gap-3 mb-3">
          <div
            class="w-10 h-10 rounded-xl flex items-center justify-center text-xl"
            :style="{ backgroundColor: cat.color + '20', color: cat.color }"
          >
            {{ cat.icon || '📦' }}
          </div>
          <div class="flex-1 min-w-0">
            <p class="font-medium truncate text-text-primary dark:text-dark-text-primary">{{ cat.categoryName }}</p>
            <p class="text-xs text-text-secondary dark:text-dark-text-secondary">{{ cat.type === 1 ? $t('category.income') : $t('category.expense') }}</p>
          </div>
        </div>
        <div class="flex items-center justify-between">
          <span
            class="text-xs px-2 py-0.5 rounded-full"
            :class="cat.isSystem ? 'bg-primary/10 text-primary' : 'bg-gray-100 dark:bg-dark-bg text-text-secondary'"
          >
            {{ cat.isSystem ? $t('category.system') : $t('category.custom') }}
          </span>
          <span class="text-xs text-text-secondary opacity-0 group-hover:opacity-100 transition-opacity">
            {{ $t('category.edit') }}
          </span>
        </div>
      </div>
    </div>

    <!-- 空状态 -->
    <div v-if="filteredCategories.length === 0" class="text-center py-16">
      <div class="w-16 h-16 mx-auto mb-4 rounded-2xl bg-gray-100 dark:bg-dark-bg flex items-center justify-center">
        <svg class="w-8 h-8 text-text-secondary" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M7 7h.01M7 3h5c.512 0 1.024.195 1.414.586l7 7a2 2 0 010 2.828l-7 7a2 2 0 01-2.828 0l-7-7A2 2 0 013 12V7a4 4 0 014-4z" />
        </svg>
      </div>
      <p class="text-text-secondary dark:text-dark-text-secondary mb-4">{{ $t('category.empty') }}</p>
      <button @click="openModal(null)" class="btn-primary">{{ $t('category.addFirst') }}</button>
    </div>

    <!-- 添加/编辑弹窗 -->
    <NModal v-model:show="showModal" preset="card" :title="editingCategory ? $t('category.edit') : $t('category.add')" style="max-width: 480px; margin: 0 auto;">
      <NForm ref="formRef" :model="formData" :rules="rules" label-placement="top">
        <NFormItem :label="$t('category.name')" path="categoryName">
          <NInput v-model:value="formData.categoryName" :placeholder="$t('category.namePlaceholder')" />
        </NFormItem>
        <NFormItem :label="$t('category.type')" path="type">
          <NSelect v-model:value="formData.type" :options="typeOptions" />
        </NFormItem>
        <NFormItem :label="$t('category.icon')" path="icon">
          <NSelect v-model:value="formData.icon" :options="iconOptions" />
        </NFormItem>
        <NFormItem :label="$t('category.color')" path="color">
          <div class="flex gap-2 flex-wrap">
            <button
              v-for="c in colorOptions"
              :key="c"
              @click="formData.color = c"
              class="w-8 h-8 rounded-lg transition-transform"
              :class="formData.color === c ? 'ring-2 ring-offset-2 ring-secondary scale-110' : 'hover:scale-105'"
              :style="{ backgroundColor: c }"
            />
          </div>
        </NFormItem>
      </NForm>
      <template #footer>
        <div class="flex gap-3">
          <button @click="showModal = false" class="flex-1 py-2 rounded-xl border border-border dark:border-dark-border text-text-secondary">
            {{ $t('category.cancel') }}
          </button>
          <button @click="handleSave" class="flex-1 py-2 rounded-xl bg-secondary text-white font-medium hover:bg-secondary-dark">
            {{ $t('category.save') }}
          </button>
        </div>
      </template>
    </NModal>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue';
import { NModal, NForm, NFormItem, NInput, NSelect, useMessage } from 'naive-ui';
import { accountingApi } from '@gzang/shared';
import { usePersonalStore } from '@/stores/personal';
import type { FormRules } from 'naive-ui';
import type { Category } from '@gzang/shared';

const personalStore = usePersonalStore();
const message = useMessage();

const activeTab = ref<number | null>(null);
const showModal = ref(false);
const editingCategory = ref<Category | null>(null);
const formRef = ref<any>(null);

const formData = ref({ categoryName: '', type: 2, icon: '📦', color: '#0F4C5C' });

const tabs = [
  { label: '全部', value: null },
  { label: '支出', value: 2 },
  { label: '收入', value: 1 },
];

const typeOptions = [
  { label: '支出', value: 2 },
  { label: '收入', value: 1 },
];

const iconOptions = [
  { label: '🍔 餐饮', value: '🍔' }, { label: '🚗 交通', value: '🚗' }, { label: '🛒 购物', value: '🛒' },
  { label: '🎬 娱乐', value: '🎬' }, { label: '🏠 居住', value: '🏠' }, { label: '💊 医疗', value: '💊' },
  { label: '📚 教育', value: '📚' }, { label: '💰 工资', value: '💰' }, { label: '🎁 礼金', value: '🎁' },
  { label: '📦 其他', value: '📦' },
];

const colorOptions = ['#0F4C5C', '#FB8B24', '#06D6A0', '#EF476F', '#118AB2', '#FFD166', '#8338EC', '#3A86FF'];

const rules: FormRules = {
  categoryName: { required: true, message: '分类名称不能为空' },
  type: { required: true, type: 'number', message: '请选择分类类型' },
};

const filteredCategories = computed(() => {
  if (activeTab.value === null) return personalStore.categories;
  return personalStore.categories.filter(c => c.type === activeTab.value);
});

const openModal = (cat: Category | null) => {
  editingCategory.value = cat;
  if (cat) {
    formData.value = { categoryName: cat.categoryName, type: cat.type, icon: (cat as any).icon || '📦', color: (cat as any).color || '#0F4C5C' };
  } else {
    formData.value = { categoryName: '', type: 2, icon: '📦', color: '#0F4C5C' };
  }
  showModal.value = true;
};

const handleSave = async () => {
  try {
    if (editingCategory.value) {
      await accountingApi.updateCategory(editingCategory.value.id, { categoryName: formData.value.categoryName, type: formData.value.type, icon: formData.value.icon, color: formData.value.color });
      personalStore.updateCategory({ ...editingCategory.value, categoryName: formData.value.categoryName, type: formData.value.type, icon: formData.value.icon, color: formData.value.color } as any);
      message.success('更新成功');
    } else {
      await accountingApi.createCategory({ categoryName: formData.value.categoryName, type: formData.value.type, icon: formData.value.icon, color: formData.value.color, parentId: 0, isSystem: false } as any);
      loadCategories();
      message.success('创建成功');
    }
    showModal.value = false;
  } catch (error: any) {
    message.error(error.message || '保存失败');
  }
};

const loadCategories = async () => {
  try {
    const res = await accountingApi.getCategories();
    if (res.data) personalStore.setCategories(res.data);
  } catch (error) { console.error('加载分类失败:', error); }
};

onMounted(loadCategories);
</script>

<style scoped>
.btn-primary { @apply inline-flex items-center justify-center gap-2 px-4 py-2 bg-secondary text-white rounded-xl font-medium text-sm transition-all hover:bg-secondary-dark active:scale-95; }
.text-secondary { @apply text-[#FB8B24]; }
.bg-secondary { @apply bg-[#FB8B24]; }
.hover-bg-secondary-dark:hover { @apply bg-[#e67a1a]; }
.ring-secondary { --tw-ring-color: #FB8B24; }
</style>
