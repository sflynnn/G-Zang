/**
 * 账本相关 API
 * 后端接口: /api/mobile/books
 */
import { api } from './index';
import type { Book, BookForm, BookStatistics } from '@/types/book';

/**
 * 获取账本列表
 */
export async function getBooks(options?: { skipLoading?: boolean }): Promise<Book[]> {
  return api.get<Book[]>('/books', undefined, options);
}

/**
 * 获取单个账本
 */
export async function getBook(id: number): Promise<Book> {
  return api.get<Book>(`/books/${id}`);
}

/**
 * 创建账本
 */
export async function createBook(data: BookForm): Promise<Book> {
  return api.post<Book>('/books', data);
}

/**
 * 更新账本
 */
export async function updateBook(id: number, data: Partial<BookForm>): Promise<Book> {
  return api.put<Book>(`/books/${id}`, data);
}

/**
 * 删除账本
 */
export async function deleteBook(id: number): Promise<void> {
  return api.delete(`/books/${id}`);
}

/**
 * 获取账本统计
 */
export async function getBookStatistics(
  bookId: number,
  params?: { startDate?: string; endDate?: string }
): Promise<BookStatistics> {
  const data = await api.get<any>(`/books/${bookId}/statistics`, params);
  return {
    bookId,
    totalIncome: data.totalIncome || 0,
    totalExpense: data.totalExpense || 0,
    balance: data.balance || 0,
    transactionCount: data.transactionCount || 0,
    periodStats: data.periodStats || {
      today: { income: 0, expense: 0 },
      thisWeek: { income: 0, expense: 0 },
      thisMonth: { income: 0, expense: 0 },
      thisYear: { income: 0, expense: 0 },
    },
  };
}

/**
 * 设置默认账本
 */
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
