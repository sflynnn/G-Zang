import dayjs from 'dayjs'
import Decimal from 'decimal.js'

// 金额格式化
export const formatAmount = (amount: number | string, currency = 'CNY'): string => {
  const num = new Decimal(amount || 0)
  const formatted = num.toFixed(2)

  // 根据货币类型返回不同格式
  switch (currency) {
    case 'CNY':
      return `¥${formatted}`
    case 'USD':
      return `$${formatted}`
    case 'EUR':
      return `€${formatted}`
    case 'JPY':
      return `¥${formatted}`
    default:
      return `${formatted} ${currency}`
  }
}

// 百分比格式化
export const formatPercentage = (value: number, decimals = 1): string => {
  return `${(value * 100).toFixed(decimals)}%`
}

// 数字格式化（千分位分隔）
export const formatNumber = (num: number): string => {
  return new Intl.NumberFormat('zh-CN').format(num)
}

// 日期时间格式化
export const formatDateTime = (date: string | Date, format = 'YYYY-MM-DD HH:mm'): string => {
  return dayjs(date).format(format)
}

// 相对时间格式化
export const formatRelativeTime = (date: string | Date): string => {
  const now = dayjs()
  const target = dayjs(date)
  const diff = now.diff(target, 'minute')

  if (diff < 1) return '刚刚'
  if (diff < 60) return `${diff}分钟前`

  const hours = Math.floor(diff / 60)
  if (hours < 24) return `${hours}小时前`

  const days = Math.floor(hours / 24)
  if (days < 30) return `${days}天前`

  const months = Math.floor(days / 30)
  if (months < 12) return `${months}个月前`

  const years = Math.floor(months / 12)
  return `${years}年前`
}

// 文件大小格式化
export const formatFileSize = (bytes: number): string => {
  if (bytes === 0) return '0 B'

  const k = 1024
  const sizes = ['B', 'KB', 'MB', 'GB', 'TB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))

  return `${(bytes / Math.pow(k, i)).toFixed(1)} ${sizes[i]}`
}

// 手机号格式化
export const formatPhoneNumber = (phone: string): string => {
  // 中国手机号格式化
  if (/^1[3-9]\d{9}$/.test(phone)) {
    return phone.replace(/(\d{3})(\d{4})(\d{4})/, '$1 $2 $3')
  }
  return phone
}

// 银行卡号格式化
export const formatBankCard = (cardNumber: string): string => {
  return cardNumber.replace(/\s/g, '').replace(/(.{4})/g, '$1 ')
}

// 文本截断
export const truncateText = (text: string, maxLength: number, suffix = '...'): string => {
  if (text.length <= maxLength) return text
  return text.substring(0, maxLength - suffix.length) + suffix
}

// 颜色格式化
export const formatColor = (color: string): string => {
  // 确保颜色格式正确
  if (color.startsWith('#')) {
    return color
  }
  return `#${color}`
}

// 交易类型格式化
export const formatTransactionType = (type: number): string => {
  switch (type) {
    case 1:
      return '收入'
    case 2:
      return '支出'
    case 3:
      return '转账'
    default:
      return '未知'
  }
}

// 分类名称格式化
export const formatCategoryName = (name: string): string => {
  // 可以在这里添加分类名称的美化逻辑
  return name
}

// 账户名称格式化
export const formatAccountName = (name: string): string => {
  // 可以在这里添加账户名称的美化逻辑
  return name
}
