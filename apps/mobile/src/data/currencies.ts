/**
 * Currency data based on ISO 4217
 * Covers 20+ commonly used currencies
 */

export interface CurrencyItem {
  /** ISO 4217 code */
  code: string
  /** Chinese name */
  name: string
  /** Currency symbol */
  symbol: string
  /** English name (for search) */
  nameEn: string
  /** Flag emoji for display */
  flag?: string
}

export const currencies: CurrencyItem[] = [
  { code: 'CNY', name: '人民币', symbol: '¥', nameEn: 'Chinese Yuan' },
  { code: 'USD', name: '美元', symbol: '$', nameEn: 'US Dollar' },
  { code: 'EUR', name: '欧元', symbol: '€', nameEn: 'Euro' },
  { code: 'GBP', name: '英镑', symbol: '£', nameEn: 'British Pound' },
  { code: 'JPY', name: '日元', symbol: '¥', nameEn: 'Japanese Yen' },
  { code: 'HKD', name: '港币', symbol: 'HK$', nameEn: 'Hong Kong Dollar' },
  { code: 'TWD', name: '新台币', symbol: 'NT$', nameEn: 'New Taiwan Dollar' },
  { code: 'KRW', name: '韩元', symbol: '₩', nameEn: 'South Korean Won' },
  { code: 'SGD', name: '新加坡元', symbol: 'S$', nameEn: 'Singapore Dollar' },
  { code: 'MYR', name: '马来西亚林吉特', symbol: 'RM', nameEn: 'Malaysian Ringgit' },
  { code: 'THB', name: '泰铢', symbol: '฿', nameEn: 'Thai Baht' },
  { code: 'AUD', name: '澳大利亚元', symbol: 'A$', nameEn: 'Australian Dollar' },
  { code: 'CAD', name: '加拿大元', symbol: 'C$', nameEn: 'Canadian Dollar' },
  { code: 'CHF', name: '瑞士法郎', symbol: 'CHF', nameEn: 'Swiss Franc' },
  { code: 'INR', name: '印度卢比', symbol: '₹', nameEn: 'Indian Rupee' },
  { code: 'AED', name: '阿联酋迪拉姆', symbol: 'د.إ', nameEn: 'UAE Dirham' },
  { code: 'NZD', name: '新西兰元', symbol: 'NZ$', nameEn: 'New Zealand Dollar' },
  { code: 'SEK', name: '瑞典克朗', symbol: 'kr', nameEn: 'Swedish Krona' },
  { code: 'NOK', name: '挪威克朗', symbol: 'kr', nameEn: 'Norwegian Krone' },
  { code: 'DKK', name: '丹麦克朗', symbol: 'kr', nameEn: 'Danish Krone' },
  { code: 'MXN', name: '墨西哥比索', symbol: '$', nameEn: 'Mexican Peso' },
  { code: 'BRL', name: '巴西雷亚尔', symbol: 'R$', nameEn: 'Brazilian Real' },
  { code: 'RUB', name: '俄罗斯卢布', symbol: '₽', nameEn: 'Russian Ruble' },
  { code: 'ZAR', name: '南非兰特', symbol: 'R', nameEn: 'South African Rand' },
  { code: 'PHP', name: '菲律宾比索', symbol: '₱', nameEn: 'Philippine Peso' },
  { code: 'IDR', name: '印尼盾', symbol: 'Rp', nameEn: 'Indonesian Rupiah' },
  { code: 'VND', name: '越南盾', symbol: '₫', nameEn: 'Vietnamese Dong' },
  { code: 'CNY_HKD', name: '离岸人民币', symbol: 'CNH', nameEn: 'Offshore RMB' },
  { code: 'XAU', name: '黄金(盎司)', symbol: 'XAU', nameEn: 'Gold (oz)' },
  { code: 'XAG', name: '白银(盎司)', symbol: 'XAG', nameEn: 'Silver (oz)' },
]

export default currencies
