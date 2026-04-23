// G-Zang 移动端状态管理导出文件

export { useAuthStore } from './auth'
export { useUserStore } from './user'
export { useAccountingStore } from './accounting'
export { useBillStore } from './bill'
export { useAnalysisStore } from './analysis'
export { useAppStore } from './app'
export { useBookStore } from './book'
export { useAccountStore } from './account'
export { useTransactionStore } from './transaction'
export { useCategoryStore } from './category'
export { useReportStore } from './report'
export { useLoadingStore } from './loading'

// 类型导出
export type { AuthState, LoginParams, UserInfo } from './auth'
export type { UserState } from './user'
export type { AccountingState, TransactionForm, Category, Account } from './accounting'
export type { BillState, Transaction } from './bill'
export type { AnalysisState, ChartData } from './analysis'
export type { AppState } from './app'
export type { BookState } from './book'
export type { AccountState } from './account'
export type { TransactionState } from './transaction'
export type { CategoryState } from './category'
export type { ReportState, SummaryResult, MonthlyTrendItem, CategoryReportItem, AccountBalanceItem } from './report'

// 从 types/book 导出账本相关类型
export type { BookStatistics, BookFilters } from '@/types/book'
