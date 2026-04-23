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
  type: number;
  parentId?: number;
  children?: CategoryVO[];
  amount?: number;
  sortOrder?: number;
}

/**
 * 获取分类列表
 * GET /api/mobile/categories?type=1|2
 */
export async function getCategories(type?: TransactionType): Promise<CategoryVO[]> {
  return api.get('/categories', type !== undefined ? { type } : undefined);
}

/**
 * 获取分类详情
 * GET /api/mobile/categories/{id}
 */
export async function getCategory(id: number): Promise<CategoryVO> {
  return api.get(`/categories/${id}`);
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
  getCategory,
  createCategory,
  updateCategory,
  deleteCategory,
  getSystemCategories,
  initUserCategories,
};

export default categoryApi;
