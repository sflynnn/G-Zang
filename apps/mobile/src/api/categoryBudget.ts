/**
 * 分类预算相关 API
 * MobileCategoryBudgetController - /api/mobile/category-budgets
 */
import { api } from './index';

export interface CategoryBudgetVO {
  budget: number;
  spent: number;
  remaining: number;
  percentUsed: number;
  warningThreshold: number;
}

export interface CategoryBudgetItemVO {
  id: number;
  userId: number;
  bookId?: number;
  categoryId: number;
  yearMonth: string;
  budgetAmount: number;
  warningThreshold: number;
  isEnabled: number;
}

/**
 * 获取分类预算
 * GET /api/mobile/category-budgets?categoryId={categoryId}&bookId={bookId}&month={YYYY-MM}
 */
export async function getCategoryBudget(
  categoryId: number,
  bookId?: number,
  month?: string
): Promise<CategoryBudgetVO> {
  const params: Record<string, any> = { categoryId };
  if (bookId) params.bookId = bookId;
  if (month) params.month = month;
  return api.get('/category-budgets', params);
}

/**
 * 获取预算列表
 * GET /api/mobile/category-budgets/list?bookId={bookId}&month={YYYY-MM}
 */
export async function getBudgetList(
  bookId?: number,
  month?: string
): Promise<CategoryBudgetItemVO[]> {
  const params: Record<string, any> = {};
  if (bookId) params.bookId = bookId;
  if (month) params.month = month;
  return api.get('/category-budgets/list', params);
}

/**
 * 保存分类预算（创建或更新）
 * POST /api/mobile/category-budgets
 */
export async function saveBudget(data: {
  categoryId: number;
  bookId?: number;
  yearMonth: string;
  budgetAmount: number;
  warningThreshold?: number;
}): Promise<CategoryBudgetItemVO> {
  return api.post('/category-budgets', data);
}

/**
 * 删除分类预算
 * DELETE /api/mobile/category-budgets/{id}
 */
export async function deleteBudget(id: number): Promise<void> {
  return api.delete(`/category-budgets/${id}`);
}

export const categoryBudgetApi = {
  getCategoryBudget,
  getBudgetList,
  saveBudget,
  deleteBudget,
};

export default categoryBudgetApi;
