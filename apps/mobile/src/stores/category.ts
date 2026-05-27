/**
 * Category Store - 分类状态管理
 */
import { defineStore } from 'pinia';
import { ref, computed } from 'vue';
import { Category, TransactionType } from '@/types/transaction';
import * as categoryApi from '@/api/category';

// G-Zang 品牌色
const BRAND_COLORS = {
  primary: '#0F4C5C',
  accent: '#FB8B24',
  success: '#06D6A0',
  danger: '#EF476F',
  warning: '#FFD166',
};

export interface CategoryState {
  categoryList: Category[];
  loading: boolean;
}

// 将后端 CategoryVO 数据转换为前端 Category 格式
function transformCategoryVO(vo: any): any {
  return {
    ...vo,
    name: vo.categoryName,
    isSystem: vo.isSystem ? true : false,
    children: vo.children?.map(transformCategoryVO),
  }
}

export const useCategoryStore = defineStore('category', () => {
  // 状态
  const categoryList = ref<any[]>([]);
  const loading = ref(false);

  // 计算属性
  const incomeCategories = computed(() =>
    categoryList.value.filter((c) => c.type === TransactionType.Income)
  );

  const expenseCategories = computed(() =>
    categoryList.value.filter((c) => c.type === TransactionType.Expense)
  );

  // 按类型分组的分类
  const categoriesByType = computed(() => ({
    [TransactionType.Income]: incomeCategories.value,
    [TransactionType.Expense]: expenseCategories.value,
  }));

  // 带子分类的一级分类
  const parentCategories = computed(() =>
    categoryList.value.filter((c) => !c.parentId || c.parentId === 0)
  );

  // 获取分类颜色
  const getCategoryColor = (category: Category, type?: TransactionType) => {
    if (category.type === TransactionType.Income) {
      return BRAND_COLORS.success;
    } else if (category.type === TransactionType.Expense) {
      return BRAND_COLORS.danger;
    }
    return BRAND_COLORS.primary;
  };

  // 获取分类图标（带默认图标）
  const getCategoryIcon = (category: Category) => {
    if (category.icon) return category.icon;
    return '📂';
  };

  // 获取子分类
  const getSubCategories = (parentId: number) => {
    return categoryList.value.filter((c) => c.parentId === parentId);
  };

  // 获取分类信息
  const getCategoryInfo = (categoryId: number) => {
    return categoryList.value.find((c) => c.id === categoryId);
  };

  // 获取分类名称（后端字段为 categoryName）
  const getCategoryName = (categoryId: number) => {
    const category = getCategoryInfo(categoryId);
    return category?.categoryName || '未知分类';
  };

  // 获取分类图标
  const getCategoryIconById = (categoryId: number) => {
    const category = getCategoryInfo(categoryId);
    return category?.icon || '📂';
  };

  // 获取分类列表
  const fetchCategories = async (type?: TransactionType) => {
    const data = await categoryApi.getCategories(type);
    categoryList.value = data.map(transformCategoryVO);
    return categoryList.value;
  };

  // 创建分类（后端不支持 icon 字段）
  const createCategory = async (data: {
    name: string;
    type: TransactionType;
    parentId?: number;
  }) => {
    const newCategory = await categoryApi.createCategory({
      name: data.name,
      type: data.type,
      parentId: data.parentId,
    });
    categoryList.value.push(transformCategoryVO(newCategory));
    return categoryList.value[categoryList.value.length - 1];
  };

  // 更新分类（后端仅支持 categoryName 和 parentId）
  const updateCategory = async (
    id: number,
    data: Partial<{
      categoryName: string;
      parentId: number;
    }>
  ) => {
    const updatedCategory = await categoryApi.updateCategory({ id, ...data });

    const index = categoryList.value.findIndex((c: any) => c.id === id);
    if (index !== -1) {
      categoryList.value[index] = {
        ...categoryList.value[index],
        ...transformCategoryVO(updatedCategory),
      };
    }

    return updatedCategory;
  };

  // 删除分类
  const deleteCategory = async (id: number) => {
    await categoryApi.deleteCategory(id);
    categoryList.value = categoryList.value.filter((c) => c.id !== id);
    return true;
  };

  // 重置默认分类
  const resetDefaultCategories = async () => {
    await categoryApi.initUserCategories();
    await fetchCategories();
  };

  // 清空状态
  const clearState = () => {
    categoryList.value = [];
  };

  return {
    // 状态
    categoryList: computed(() => categoryList.value),
    loading: computed(() => loading.value),

    // 计算属性
    incomeCategories,
    expenseCategories,
    categoriesByType,
    parentCategories,

    // 方法
    fetchCategories,
    createCategory,
    updateCategory,
    deleteCategory,
    resetDefaultCategories,
    getSubCategories,
    getCategoryInfo,
    getCategoryName,
    getCategoryIcon,
    getCategoryIconById,
    getCategoryColor,
    clearState,
  };
});
