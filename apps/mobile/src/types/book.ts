/**
 * 账本相关类型定义
 */

// 账本种类枚举
export enum BookType {
  Personal = 'PERSONAL',   // 个人
  Family = 'FAMILY',     // 家庭
  Team = 'TEAM',         // 团队
  Business = 'BUSINESS', // 业务/生意
  Other = 'OTHER'        // 其他
}

// 账本
export interface Book {
  id: number
  name: string
  icon: string            // emoji 或图标名称
  color: string           // 十六进制主题色
  currency: string        // 货币代码，如 CNY、USD
  currencySymbol: string   // 货币符号，如 ¥、$
  type: BookType | string // 账本种类
  isDefault: boolean      // 是否默认账本
  categoryCount: number   // 分类数量
  transactionCount: number // 交易记录数
  totalIncome: number     // 总收入
  totalExpense: number     // 总支出
  createTime: string
  updateTime?: string
}

// 账本表单（创建/编辑）
export interface BookForm {
  name: string
  icon: string
  color: string
  currency: string
  currencySymbol?: string
  type: BookType | string
  isDefault?: boolean
}

// 账本统计
export interface BookStatistics {
  bookId: number
  totalIncome: number
  totalExpense: number
  balance: number
  transactionCount: number
  periodStats: {
    today: { income: number; expense: number }
    thisWeek: { income: number; expense: number }
    thisMonth: { income: number; expense: number }
    thisYear: { income: number; expense: number }
  }
}

// 账本筛选条件
export interface BookFilters {
  type?: BookType | string
  keyword?: string
}

// 预设货币列表
export const PRESET_CURRENCIES = [
  { code: 'CNY', symbol: '¥', name: '人民币' },
  { code: 'USD', symbol: '$', name: '美元' },
  { code: 'EUR', symbol: '€', name: '欧元' },
  { code: 'GBP', symbol: '£', name: '英镑' },
  { code: 'JPY', symbol: '¥', name: '日元' },
  { code: 'HKD', symbol: 'HK$', name: '港币' },
  { code: 'TWD', symbol: 'NT$', name: '新台币' },
  { code: 'KRW', symbol: '₩', name: '韩元' },
  { code: 'SGD', symbol: 'S$', name: '新加坡元' },
  { code: 'MYR', symbol: 'RM', name: '马来西亚林吉特' },
  { code: 'THB', symbol: '฿', name: '泰铢' },
  { code: 'AUD', symbol: 'A$', name: '澳大利亚元' },
  { code: 'CAD', symbol: 'C$', name: '加拿大元' },
  { code: 'CHF', symbol: 'CHF', name: '瑞士法郎' },
  { code: 'INR', symbol: '₹', name: '印度卢比' },
]

// 预设账本图标
export const PRESET_BOOK_ICONS = [
  '📒', '📔', '📕', '📗', '📘', '📙',
  '💰', '💼', '🏠', '🚗', '✈️', '🎯',
  '❤️', '⭐', '🎉', '💳', '🛒', '🍔',
]

// 预设账本颜色
export const PRESET_BOOK_COLORS = [
  '#0F4C5C', // 归藏青 - 品牌色
  '#FB8B24', // 琉璃金 - 品牌色
  '#06D6A0', // 利润绿
  '#EF476F', // 支出红
  '#118AB2', // 深蓝
  '#073B4C', // 墨蓝
  '#06D6A0', // 翠绿
  '#FFD166', // 暖黄
  '#EF476F', // 玫红
  '#8338EC', // 紫色
  '#3A86FF', // 天蓝
  '#FB5607', // 橙色
]
