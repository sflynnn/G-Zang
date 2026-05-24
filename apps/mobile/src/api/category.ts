/**
 * 分类相关 API
 * MobileCategoryController - /api/mobile/categories
 * 对应后端: server/gzang-mobile-api/.../MobileCategoryController.java
 */
import { api } from './index';
import type { TransactionType } from '@/types/transaction';

/** 后端 CreateCategoryDTO 字段 */
export interface CreateCategoryDTO {
  categoryName: string;
  type: number;
  parentId?: number;
}

/** 后端 UpdateCategoryDTO 字段 */
export interface UpdateCategoryDTO extends Partial<CreateCategoryDTO> {
  id: number;
}

/** 分类数据结构 */
export interface CategoryVO {
  id: number;
  categoryName: string;
  icon?: string;
  color?: string;
  type: number;
  parentId?: number;
  isSystem?: number;
  children?: CategoryVO[];
  amount?: number;
  sortOrder?: number;
}

/** 分类预算信息 */
export interface CategoryBudgetVO {
  budget: number;
  spent: number;
  remaining: number;
  percentUsed?: number;
  warningThreshold?: number;
}

/** 带子分类和额度的分类 */
export interface CategoryWithChildrenVO extends CategoryVO {
  children: CategoryVO[];
  budget?: CategoryBudgetVO;
}

/** 分类列表响应（带子分类和额度） */
export interface CategoryListWithChildrenResponse {
  expenseCategories: CategoryWithChildrenVO[];
  incomeCategories: CategoryWithChildrenVO[];
}

/**
 * 获取分类列表
 * GET /api/mobile/categories?type=1|2
 */
export async function getCategories(type?: TransactionType): Promise<CategoryVO[]> {
  return api.get('/categories', type !== undefined ? { type } : undefined);
}

/**
 * 获取分类列表（带子分类和额度）
 * GET /api/mobile/categories/with-children?bookId={bookId}&month={YYYY-MM}
 */
export async function getCategoriesWithChildren(
  bookId?: number,
  month?: string
): Promise<CategoryWithChildrenVO[]> {
  const params: Record<string, any> = {};
  if (bookId) params.bookId = bookId;
  if (month) params.month = month;
  return api.get('/categories/with-children', Object.keys(params).length > 0 ? params : undefined);
}

/**
 * 获取分类详情
 * GET /api/mobile/categories/{id}
 */
export async function getCategory(id: number): Promise<CategoryVO> {
  return api.get(`/categories/${id}`);
}

/**
 * 获取分类月度额度
 * GET /api/mobile/categories/{id}/budget?bookId={bookId}&month={YYYY-MM}
 */
export async function getCategoryBudget(
  id: number,
  bookId?: number,
  month?: string
): Promise<CategoryBudgetVO> {
  const params: Record<string, any> = {};
  if (bookId) params.bookId = bookId;
  if (month) params.month = month;
  return api.get(`/categories/${id}/budget`, Object.keys(params).length > 0 ? params : undefined);
}

/**
 * 创建分类
 * POST /api/mobile/categories
 * 字段映射: name → categoryName
 */
export async function createCategory(data: {
  name: string;
  type: TransactionType;
  parentId?: number;
}): Promise<CategoryVO> {
  return api.post('/categories', {
    categoryName: data.name,
    type: data.type,
    parentId: data.parentId,
  });
}

/**
 * 更新分类
 * PUT /api/mobile/categories/{id}
 */
export async function updateCategory(data: {
  id: number;
  categoryName?: string;
  parentId?: number;
}): Promise<CategoryVO> {
  return api.put(`/categories/${data.id}`, {
    categoryName: data.categoryName,
    parentId: data.parentId,
  });
}

/**
 * 删除分类
 * DELETE /api/mobile/categories/{id}
 */
export async function deleteCategory(id: number): Promise<void> {
  return api.delete(`/categories/${id}`);
}

/**
 * 获取系统预设分类
 * GET /api/mobile/categories/system?type=1|2
 */
export async function getSystemCategories(type?: TransactionType): Promise<CategoryVO[]> {
  return api.get('/categories/system', type !== undefined ? { type } : undefined);
}

/**
 * 初始化用户分类
 * POST /api/mobile/categories/init
 */
export async function initUserCategories(): Promise<void> {
  return api.post('/categories/init');
}

export const categoryApi = {
  getCategories,
  getCategoriesWithChildren,
  getCategory,
  getCategoryBudget,
  createCategory,
  updateCategory,
  deleteCategory,
  getSystemCategories,
  initUserCategories,
};

export default categoryApi;
