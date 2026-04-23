/**
 * 账本相关 API
 * 注意: 后端 Mobile 服务暂无账本 (Book) 相关接口
 * 所有账本相关接口均为占位实现，待后端补充接口后启用
 */
import { api } from './index';
import type { Book } from '@/types/book';

/**
 * 获取账本列表
 * 状态: 未实现 (后端无对应接口)
 */
export async function getBooks(options?: { skipLoading?: boolean }): Promise<Book[]> {
  return api.get<Book[]>('/books', undefined, options);
}

export async function getBook(id: number, options?: { skipLoading?: boolean }): Promise<Book> {
  return api.get<Book>(`/books/${id}`, undefined, options);
}

export async function createBook(data: {
  name: string;
  icon?: string;
  color?: string;
  currency?: string;
  currencySymbol?: string;
  type?: string;
  isDefault?: boolean;
}): Promise<Book> {
  return api.post<Book>('/books', data);
}

export async function updateBook(data: { id: number; [key: string]: any }): Promise<Book> {
  return api.put<Book>(`/books/${data.id}`, data);
}

export async function deleteBook(id: number): Promise<void> {
  return api.delete(`/books/${id}`);
}

export async function getBookStatistics(
  bookId: number,
  params?: { startDate?: string; endDate?: string },
  options?: { skipLoading?: boolean }
): Promise<{
  totalIncome: number;
  totalExpense: number;
  balance: number;
  transactionCount: number;
}> {
  return api.get(`/books/${bookId}/statistics`, params, options);
}

export async function setDefaultBook(id: number): Promise<void> {
  return api.put(`/books/${id}/default`);
}

export const bookApi = {
  getBooks,
  getBook,
  createBook,
  updateBook,
  deleteBook,
  getBookStatistics,
  setDefaultBook,
};

export default bookApi;
