/**
 * Book Store - 账本状态管理
 */
import { defineStore } from 'pinia';
import { ref, computed } from 'vue';
import type { Book, BookForm, BookStatistics, BookFilters } from '@/types/book';
import { PRESET_CURRENCIES } from '@/types/book';
import * as bookApi from '@/api/book';

export interface BookState {
  currentBook: Book | null;
  bookList: Book[];
  loading: boolean;
  statistics: Record<number, BookStatistics>;
}

export const useBookStore = defineStore('book', () => {
  // 状态
  const currentBook = ref<Book | null>(null);
  const bookList = ref<Book[]>([]);
  const loading = ref(false);
  const statistics = ref<Record<number, BookStatistics>>({});

  // 计算属性
  const defaultBook = computed(() =>
    bookList.value.find((b) => b.isDefault) || bookList.value[0] || null
  );

  const personalBooks = computed(() =>
    bookList.value.filter((b) => b.type === 'PERSONAL')
  );

  const familyBooks = computed(() =>
    bookList.value.filter((b) => b.type === 'FAMILY')
  );

  const businessBooks = computed(() =>
    bookList.value.filter((b) => b.type === 'BUSINESS' || b.type === 'TEAM')
  );

  const currentCurrencySymbol = computed(() => {
    const book = currentBook.value || defaultBook.value;
    if (book) return book.currencySymbol;
    return '¥';
  });

  const currentCurrency = computed(() => {
    const book = currentBook.value || defaultBook.value;
    if (book) {
      return PRESET_CURRENCIES.find((c) => c.code === book.currency) || PRESET_CURRENCIES[0];
    }
    return PRESET_CURRENCIES[0];
  });

  const currentStatistics = computed(() => {
    if (!currentBook.value) return null;
    return statistics.value[currentBook.value.id] || null;
  });

  // 获取账本列表
  const fetchBooks = async (filters?: BookFilters) => {
    const data = await bookApi.getBooks();
    bookList.value = data;

    // 如果没有设置当前账本，默认选择第一个
    if (!currentBook.value && bookList.value.length > 0) {
      currentBook.value = defaultBook.value || bookList.value[0];
    }

    return data;
  };

  // 切换账本
  const switchBook = async (bookId: number) => {
    const book = bookList.value.find((b) => b.id === bookId);
    if (book) {
      currentBook.value = book;
      uni.setStorageSync('currentBookId', bookId);
      // 加载新账本统计
      await fetchStatistics(bookId);
    }
  };

  // 创建账本
  const createBook = async (form: BookForm) => {
    const newBook = await bookApi.createBook({
      name: form.name,
      icon: form.icon,
      color: form.color,
      currency: form.currency,
      currencySymbol: form.currencySymbol,
      type: form.type,
      isDefault: form.isDefault,
    });

    bookList.value.push(newBook);

    // 如果设为默认账本
    if (newBook.isDefault) {
      bookList.value.forEach((b) => {
        if (b.id !== newBook.id) {
          b.isDefault = false;
        }
      });
    }

    return newBook;
  };

  // 删除账本
  const deleteBook = async (id: number) => {
    await bookApi.deleteBook(id);

    bookList.value = bookList.value.filter((b) => b.id !== id);
    delete statistics.value[id];

    // 如果删除的是当前账本，切换到默认账本
    if (currentBook.value?.id === id) {
      currentBook.value = defaultBook.value || bookList.value[0] || null;
      if (currentBook.value) {
        uni.setStorageSync('currentBookId', currentBook.value.id);
      }
    }

    return true;
  };

  // 获取账本统计
  const fetchStatistics = async (
    bookId: number,
    params?: { startDate?: string; endDate?: string }
  ) => {
    try {
      const stats = await bookApi.getBookStatistics(bookId, params);
      statistics.value[bookId] = {
        bookId,
        totalIncome: stats.totalIncome || 0,
        totalExpense: stats.totalExpense || 0,
        balance: stats.balance || 0,
        transactionCount: stats.transactionCount || 0,
        periodStats: stats.periodStats || {
          today: { income: 0, expense: 0 },
          thisWeek: { income: 0, expense: 0 },
          thisMonth: { income: 0, expense: 0 },
          thisYear: { income: 0, expense: 0 },
        },
      };
      return stats;
    } catch (error) {
      throw error;
    }
  };

  // 更新账本
  const updateBook = async (id: number, form: Partial<BookForm>) => {
    const updatedBook = await bookApi.updateBook(id, form);

    const index = bookList.value.findIndex((b) => b.id === id);
    if (index !== -1) {
      bookList.value[index] = updatedBook;
    }

    // 如果更新的是当前账本
    if (currentBook.value?.id === id) {
      currentBook.value = updatedBook;
    }

    return updatedBook;
  };

  // 设置默认账本
  const setDefaultBook = async (bookId: number) => {
    try {
      await bookApi.setDefaultBook(bookId);
      bookList.value.forEach((b) => {
        b.isDefault = b.id === bookId;
      });
    } catch (error) {
      throw error;
    }
  };

  // 初始化
  const initialize = () => {
    const savedBookId = uni.getStorageSync('currentBookId');
    if (savedBookId && typeof savedBookId === 'number') {
      const book = bookList.value.find((b) => b.id === savedBookId);
      if (book) {
        currentBook.value = book;
      }
    }
  };

  // 清空状态
  const clearState = () => {
    currentBook.value = null;
    bookList.value = [];
    statistics.value = {};
  };

  return {
    // 状态
    currentBook: computed(() => currentBook.value),
    currentBookId: computed(() => currentBook.value?.id),
    books: computed(() => bookList.value),
    bookList: computed(() => bookList.value),
    loading: computed(() => loading.value),
    statistics: computed(() => statistics.value),

    // 计算属性
    defaultBook,
    personalBooks,
    familyBooks,
    businessBooks,
    currentCurrencySymbol,
    currentCurrency,
    currentStatistics,

    // 方法
    fetchBooks,
    loadBooks: fetchBooks, // 别名，保持向后兼容
    switchBook,
    createBook,
    deleteBook,
    fetchStatistics,
    updateBook,
    setDefaultBook,
    initialize,
    clearState,
  };
});
