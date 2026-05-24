/**
 * 标签相关 API
 * MobileTagController - /api/mobile/tags
 */
import { api } from './index';

export interface TagVO {
  id: number;
  tagName: string;
  tagColor: string;
  usageCount: number;
  isFrequent: number;
}

/**
 * 获取用户标签列表
 * GET /api/mobile/tags
 */
export async function getTags(): Promise<TagVO[]> {
  return api.get('/tags');
}

/**
 * 获取常用标签
 * GET /api/mobile/tags/frequent
 */
export async function getFrequentTags(limit: number = 5): Promise<TagVO[]> {
  return api.get('/tags/frequent', { limit });
}

/**
 * 创建标签
 * POST /api/mobile/tags
 */
export async function createTag(data: {
  tagName: string;
  tagColor?: string;
  isFrequent?: number;
}): Promise<TagVO> {
  return api.post('/tags', data);
}

/**
 * 更新标签
 * PUT /api/mobile/tags/{id}
 */
export async function updateTag(
  id: number,
  data: {
    tagName?: string;
    tagColor?: string;
    isFrequent?: number;
  }
): Promise<void> {
  return api.put(`/tags/${id}`, data);
}

/**
 * 删除标签
 * DELETE /api/mobile/tags/{id}
 */
export async function deleteTag(id: number): Promise<void> {
  return api.delete(`/tags/${id}`);
}

/**
 * 使用标签（增加使用次数）
 * POST /api/mobile/tags/{id}/use
 */
export async function useTag(id: number): Promise<void> {
  return api.post(`/tags/${id}/use`);
}

export const tagApi = {
  getTags,
  getFrequentTags,
  createTag,
  updateTag,
  deleteTag,
  useTag,
};

export default tagApi;
