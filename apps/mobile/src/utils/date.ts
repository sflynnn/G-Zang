// G-Zang 移动端日期工具函数

import dayjs from 'dayjs'
import 'dayjs/locale/zh-cn'
import relativeTime from 'dayjs/plugin/relativeTime'
import isBetween from 'dayjs/plugin/isBetween'
import quarterOfYear from 'dayjs/plugin/quarterOfYear'

// 配置 dayjs
dayjs.locale('zh-cn')
dayjs.extend(relativeTime)
dayjs.extend(isBetween)
dayjs.extend(quarterOfYear)

// 日期格式常量
export const DATE_FORMATS = {
  DATE: 'YYYY-MM-DD',
  DATETIME: 'YYYY-MM-DD HH:mm:ss',
  TIME: 'HH:mm:ss',
  MONTH: 'YYYY-MM',
  YEAR: 'YYYY'
} as const

// 获取当前日期
export const now = (): dayjs.Dayjs => dayjs()

// 格式化日期
export const formatDate = (date: string | Date | dayjs.Dayjs, format = DATE_FORMATS.DATE): string => {
  return dayjs(date).format(format)
}

// 解析日期字符串
export const parseDate = (dateString: string, format = DATE_FORMATS.DATE): dayjs.Dayjs => {
  return dayjs(dateString, format)
}

// 日期计算
export const dateAdd = (date: string | Date | dayjs.Dayjs, amount: number, unit: dayjs.ManipulateType): dayjs.Dayjs => {
  return dayjs(date).add(amount, unit)
}

export const dateSubtract = (date: string | Date | dayjs.Dayjs, amount: number, unit: dayjs.ManipulateType): dayjs.Dayjs => {
  return dayjs(date).subtract(amount, unit)
}

// 日期比较
export const isBefore = (date1: string | Date | dayjs.Dayjs, date2: string | Date | dayjs.Dayjs): boolean => {
  return dayjs(date1).isBefore(dayjs(date2))
}

export const isAfter = (date1: string | Date | dayjs.Dayjs, date2: string | Date | dayjs.Dayjs): boolean => {
  return dayjs(date1).isAfter(dayjs(date2))
}

export const isSame = (date1: string | Date | dayjs.Dayjs, date2: string | Date | dayjs.Dayjs, unit: dayjs.OpUnitType = 'day'): boolean => {
  return dayjs(date1).isSame(dayjs(date2), unit)
}

// 获取日期范围
export const getDateRange = (type: 'week' | 'month' | 'year' | 'quarter'): { start: string; end: string } => {
  const now = dayjs()
  let start: dayjs.Dayjs
  let end: dayjs.Dayjs = now.endOf('day')

  switch (type) {
    case 'week':
      start = now.startOf('week')
      end = now.endOf('week')
      break
    case 'month':
      start = now.startOf('month')
      end = now.endOf('month')
      break
    case 'year':
      start = now.startOf('year')
      end = now.endOf('year')
      break
    case 'quarter':
      start = now.startOf('quarter')
      end = now.endOf('quarter')
      break
    default:
      start = now.startOf('day')
  }

  return {
    start: start.format(DATE_FORMATS.DATE),
    end: end.format(DATE_FORMATS.DATE)
  }
}

// 获取相对时间
export const getRelativeTime = (date: string | Date | dayjs.Dayjs): string => {
  return dayjs(date).fromNow()
}

// 格式化时间段
export const formatTimeRange = (start: string | Date, end: string | Date): string => {
  const startDate = dayjs(start)
  const endDate = dayjs(end)

  if (startDate.isSame(endDate, 'day')) {
    return startDate.format('MM月DD日')
  }

  if (startDate.isSame(endDate, 'month')) {
    return `${startDate.format('MM月DD日')}-${endDate.format('DD日')}`
  }

  if (startDate.isSame(endDate, 'year')) {
    return `${startDate.format('MM月DD日')}-${endDate.format('MM月DD日')}`
  }

  return `${startDate.format('YYYY年MM月DD日')}-${endDate.format('YYYY年MM月DD日')}`
}

// 获取月份的天数
export const getDaysInMonth = (date: string | Date | dayjs.Dayjs): number => {
  return dayjs(date).daysInMonth()
}

// 检查是否为工作日
export const isWeekday = (date: string | Date | dayjs.Dayjs): boolean => {
  const day = dayjs(date).day()
  return day >= 1 && day <= 5 // 1-5 表示周一到周五
}

// 获取工作日列表
export const getWeekdays = (month: string | Date | dayjs.Dayjs): dayjs.Dayjs[] => {
  const start = dayjs(month).startOf('month')
  const end = dayjs(month).endOf('month')
  const weekdays: dayjs.Dayjs[] = []

  let current = start
  while (current.isBefore(end) || current.isSame(end, 'day')) {
    if (isWeekday(current)) {
      weekdays.push(current)
    }
    current = current.add(1, 'day')
  }

  return weekdays
}

// 日期选择器选项
export const getDatePickerOptions = () => {
  const now = dayjs()

  return {
    // 年份选项（前后5年）
    years: Array.from({ length: 11 }, (_, i) => ({
      label: `${now.year() - 5 + i}年`,
      value: now.year() - 5 + i
    })),

    // 月份选项
    months: Array.from({ length: 12 }, (_, i) => ({
      label: `${i + 1}月`,
      value: i + 1
    })),

    // 日期选项（根据年月动态生成）
    getDays: (year: number, month: number) => {
      const daysInMonth = dayjs(`${year}-${month}`).daysInMonth()
      return Array.from({ length: daysInMonth }, (_, i) => ({
        label: `${i + 1}日`,
        value: i + 1
      }))
    },

    // 小时选项
    hours: Array.from({ length: 24 }, (_, i) => ({
      label: `${i.toString().padStart(2, '0')}:00`,
      value: i
    })),

    // 分钟选项
    minutes: Array.from({ length: 60 }, (_, i) => ({
      label: i.toString().padStart(2, '0'),
      value: i
    }))
  }
}

// 快捷日期选项
export const getQuickDateOptions = () => [
  {
    label: '今天',
    value: 'today',
    range: getDateRange('week') // 实际上是当天
  },
  {
    label: '昨天',
    value: 'yesterday',
    range: {
      start: dayjs().subtract(1, 'day').format(DATE_FORMATS.DATE),
      end: dayjs().subtract(1, 'day').format(DATE_FORMATS.DATE)
    }
  },
  {
    label: '本周',
    value: 'week',
    range: getDateRange('week')
  },
  {
    label: '本月',
    value: 'month',
    range: getDateRange('month')
  },
  {
    label: '本季度',
    value: 'quarter',
    range: getDateRange('quarter')
  },
  {
    label: '今年',
    value: 'year',
    range: getDateRange('year')
  }
]

// 验证日期格式
export const isValidDate = (dateString: string, format = DATE_FORMATS.DATE): boolean => {
  return dayjs(dateString, format, true).isValid()
}

// 转换日期格式
export const convertDateFormat = (dateString: string, fromFormat: string, toFormat: string): string => {
  return dayjs(dateString, fromFormat).format(toFormat)
}
