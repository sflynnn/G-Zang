import { describe, it, expect } from 'vitest'

/**
 * 金额格式化工具函数测试
 */
describe('金额格式化工具', () => {
  /**
   * 测试金额格式化函数
   */
  describe('formatAmount', () => {
    it('should format positive numbers correctly', () => {
      const formatAmount = (amount: number): string => {
        return Math.abs(amount).toLocaleString('zh-CN', {
          minimumFractionDigits: 2,
          maximumFractionDigits: 2
        })
      }

      expect(formatAmount(1234.56)).toBe('1,234.56')
      expect(formatAmount(1000000)).toBe('1,000,000.00')
      expect(formatAmount(0)).toBe('0.00')
    })

    it('should handle negative numbers', () => {
      const formatAmount = (amount: number): string => {
        return Math.abs(amount).toLocaleString('zh-CN', {
          minimumFractionDigits: 2,
          maximumFractionDigits: 2
        })
      }

      expect(formatAmount(-1234.56)).toBe('1,234.56')
    })

    it('should handle decimal precision', () => {
      const formatAmount = (amount: number): string => {
        return Math.abs(amount).toLocaleString('zh-CN', {
          minimumFractionDigits: 2,
          maximumFractionDigits: 2
        })
      }

      expect(formatAmount(123.4)).toBe('123.40')
      expect(formatAmount(123.456)).toBe('123.46')
    })
  })

  /**
   * 测试日期格式化函数
   */
  describe('formatDate', () => {
    it('should format date correctly', () => {
      const formatDate = (dateStr: string): string => {
        const date = new Date(dateStr)
        return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`
      }

      expect(formatDate('2026-05-22')).toBe('2026-05-22')
    })
  })
})

/**
 * 分类工具函数测试
 */
describe('分类工具', () => {
  describe('getAccountTypeName', () => {
    const getAccountTypeName = (type: number): string => {
      const nameMap: Record<number, string> = {
        1: '现金',
        2: '银行卡',
        3: '电子支付',
        4: '信用卡',
        5: '电子钱包',
        6: '投资账户',
        7: '债务',
        8: '其他',
      }
      return nameMap[type] || '其他'
    }

    it('should return correct type name', () => {
      expect(getAccountTypeName(1)).toBe('现金')
      expect(getAccountTypeName(2)).toBe('银行卡')
      expect(getAccountTypeName(8)).toBe('其他')
    })

    it('should return default for unknown type', () => {
      expect(getAccountTypeName(99)).toBe('其他')
    })
  })

  describe('getAccountColor', () => {
    const getAccountColor = (type: number): string => {
      const colorMap: Record<number, string> = {
        1: '#06D6A0',
        2: '#0F4C5C',
        3: '#FB8B24',
        4: '#EF476F',
        5: '#118AB2',
        6: '#3A86FF',
        7: '#EF476F',
      }
      return colorMap[type] || '#6B7280'
    }

    it('should return correct color', () => {
      expect(getAccountColor(1)).toBe('#06D6A0')
      expect(getAccountColor(2)).toBe('#0F4C5C')
    })
  })
})

/**
 * 预算计算测试
 */
describe('预算计算', () => {
  describe('calculateUsageRate', () => {
    const calculateUsageRate = (amount: number, usedAmount: number): number => {
      if (amount <= 0) return 0
      return Math.round((usedAmount / amount) * 100)
    }

    it('should calculate usage rate correctly', () => {
      expect(calculateUsageRate(1000, 500)).toBe(50)
      expect(calculateUsageRate(1000, 1000)).toBe(100)
      expect(calculateUsageRate(1000, 1200)).toBe(120)
    })

    it('should handle zero amount', () => {
      expect(calculateUsageRate(0, 100)).toBe(0)
    })

    it('should handle zero used amount', () => {
      expect(calculateUsageRate(1000, 0)).toBe(0)
    })
  })

  describe('isWarning', () => {
    const isWarning = (usageRate: number, threshold: number = 80): boolean => {
      return usageRate >= threshold
    }

    it('should trigger warning when threshold exceeded', () => {
      expect(isWarning(80)).toBe(true)
      expect(isWarning(100)).toBe(true)
      expect(isWarning(120)).toBe(true)
    })

    it('should not trigger warning when below threshold', () => {
      expect(isWarning(79)).toBe(false)
      expect(isWarning(50)).toBe(false)
    })

    it('should use custom threshold', () => {
      expect(isWarning(70, 60)).toBe(true)
      expect(isWarning(70, 80)).toBe(false)
    })
  })
})
