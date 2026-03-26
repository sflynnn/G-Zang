// G-Zang 移动端数学工具函数

import Decimal from 'decimal.js'

// 配置 Decimal
Decimal.set({ precision: 10, rounding: 4 })

// 金额计算类
export class MoneyCalculator {
  private static createDecimal(value: number | string): Decimal {
    return new Decimal(value || 0)
  }

  // 加法
  static add(...values: (number | string)[]): number {
    return values.reduce((sum, value) =>
      this.createDecimal(sum).add(this.createDecimal(value)).toNumber(),
      0
    )
  }

  // 减法
  static subtract(minuend: number | string, subtrahend: number | string): number {
    return this.createDecimal(minuend).sub(this.createDecimal(subtrahend)).toNumber()
  }

  // 乘法
  static multiply(...values: (number | string)[]): number {
    return values.reduce((product, value) =>
      this.createDecimal(product).mul(this.createDecimal(value)).toNumber(),
      1
    )
  }

  // 除法
  static divide(dividend: number | string, divisor: number | string): number {
    if (this.createDecimal(divisor).isZero()) {
      throw new Error('除数不能为零')
    }
    return this.createDecimal(dividend).div(this.createDecimal(divisor)).toNumber()
  }

  // 百分比计算
  static percentage(value: number | string, total: number | string): number {
    if (this.createDecimal(total).isZero()) return 0
    return this.createDecimal(value).div(this.createDecimal(total)).mul(100).toNumber()
  }

  // 四舍五入到指定位数
  static round(value: number | string, decimals = 2): number {
    return this.createDecimal(value).toDecimalPlaces(decimals, Decimal.ROUND_HALF_UP).toNumber()
  }

  // 向上取整
  static ceil(value: number | string): number {
    return this.createDecimal(value).ceil().toNumber()
  }

  // 向下取整
  static floor(value: number | string): number {
    return this.createDecimal(value).floor().toNumber()
  }

  // 绝对值
  static abs(value: number | string): number {
    return this.createDecimal(value).abs().toNumber()
  }

  // 比较大小
  static compare(value1: number | string, value2: number | string): number {
    return this.createDecimal(value1).comparedTo(this.createDecimal(value2))
  }

  // 是否相等
  static equals(value1: number | string, value2: number | string): boolean {
    return this.createDecimal(value1).equals(this.createDecimal(value2))
  }

  // 大于
  static greaterThan(value1: number | string, value2: number | string): boolean {
    return this.compare(value1, value2) > 0
  }

  // 小于
  static lessThan(value1: number | string, value2: number | string): boolean {
    return this.compare(value1, value2) < 0
  }

  // 大于等于
  static greaterThanOrEqual(value1: number | string, value2: number | string): boolean {
    return this.compare(value1, value2) >= 0
  }

  // 小于等于
  static lessThanOrEqual(value1: number | string, value2: number | string): boolean {
    return this.compare(value1, value2) <= 0
  }
}

// 统计计算类
export class StatisticsCalculator {
  // 计算平均值
  static average(values: (number | string)[]): number {
    if (values.length === 0) return 0
    const sum = values.reduce((acc, val) => MoneyCalculator.add(acc, val), 0)
    return MoneyCalculator.divide(sum, values.length)
  }

  // 计算中位数
  static median(values: (number | string)[]): number {
    if (values.length === 0) return 0

    const sorted = values.map(v => Number(v)).sort((a, b) => a - b)
    const mid = Math.floor(sorted.length / 2)

    if (sorted.length % 2 === 0) {
      return MoneyCalculator.divide(MoneyCalculator.add(sorted[mid - 1], sorted[mid]), 2)
    } else {
      return sorted[mid]
    }
  }

  // 计算标准差
  static standardDeviation(values: (number | string)[]): number {
    if (values.length < 2) return 0

    const avg = this.average(values)
    const squaredDiffs = values.map(val =>
      Math.pow(MoneyCalculator.subtract(Number(val), avg), 2)
    )
    const avgSquaredDiff = this.average(squaredDiffs.map(String))

    return Math.sqrt(avgSquaredDiff)
  }

  // 计算方差
  static variance(values: (number | string)[]): number {
    if (values.length < 2) return 0
    const std = this.standardDeviation(values)
    return Math.pow(std, 2)
  }

  // 计算最大值
  static max(values: (number | string)[]): number {
    if (values.length === 0) return 0
    return Math.max(...values.map(v => Number(v)))
  }

  // 计算最小值
  static min(values: (number | string)[]): number {
    if (values.length === 0) return 0
    return Math.min(...values.map(v => Number(v)))
  }

  // 计算范围
  static range(values: (number | string)[]): number {
    if (values.length === 0) return 0
    return this.max(values) - this.min(values)
  }

  // 计算四分位数
  static quartile(values: (number | string)[], q: 1 | 2 | 3): number {
    if (values.length === 0) return 0

    const sorted = values.map(v => Number(v)).sort((a, b) => a - b)
    const pos = (sorted.length - 1) * (q / 4)
    const base = Math.floor(pos)
    const rest = pos - base

    if (sorted[base + 1] !== undefined) {
      return sorted[base] + rest * (sorted[base + 1] - sorted[base])
    } else {
      return sorted[base]
    }
  }

  // 计算百分位数
  static percentile(values: (number | string)[], p: number): number {
    if (values.length === 0) return 0
    if (p < 0 || p > 100) throw new Error('百分位数必须在0-100之间')

    const sorted = values.map(v => Number(v)).sort((a, b) => a - b)
    const pos = (sorted.length - 1) * (p / 100)
    const base = Math.floor(pos)
    const rest = pos - base

    if (sorted[base + 1] !== undefined) {
      return sorted[base] + rest * (sorted[base + 1] - sorted[base])
    } else {
      return sorted[base]
    }
  }
}

// 财务计算类
export class FinanceCalculator {
  // 计算复利
  static compoundInterest(
    principal: number,
    rate: number,
    time: number,
    compoundsPerYear = 1
  ): number {
    const r = rate / 100 // 转换为小数
    return MoneyCalculator.multiply(
      principal,
      Math.pow(1 + r / compoundsPerYear, compoundsPerYear * time)
    )
  }

  // 计算月供（等额本息）
  static monthlyPayment(
    principal: number,
    annualRate: number,
    months: number
  ): number {
    const monthlyRate = annualRate / 100 / 12
    const payment = MoneyCalculator.multiply(
      principal,
      MoneyCalculator.multiply(monthlyRate, Math.pow(1 + monthlyRate, months))
    )
    const denominator = Math.pow(1 + monthlyRate, months) - 1

    return MoneyCalculator.divide(payment, denominator)
  }

  // 计算投资回报率
  static returnOnInvestment(initial: number, final: number): number {
    if (initial === 0) return 0
    return MoneyCalculator.percentage(MoneyCalculator.subtract(final, initial), initial)
  }

  // 计算净现值
  static netPresentValue(
    cashFlows: number[],
    discountRate: number
  ): number {
    let npv = cashFlows[0] // 初始投资（通常为负值）

    for (let i = 1; i < cashFlows.length; i++) {
      const discounted = MoneyCalculator.divide(
        cashFlows[i],
        Math.pow(1 + discountRate / 100, i)
      )
      npv = MoneyCalculator.add(npv, discounted)
    }

    return npv
  }

  // 计算内部收益率（近似值）
  static internalRateOfReturn(
    cashFlows: number[],
    guess = 0.1
  ): number {
    // 使用牛顿法近似计算
    let rate = guess
    const maxIterations = 100
    const tolerance = 0.0001

    for (let i = 0; i < maxIterations; i++) {
      let npv = 0
      let dnpv = 0

      for (let j = 0; j < cashFlows.length; j++) {
        const discountFactor = Math.pow(1 + rate, j)
        npv += cashFlows[j] / discountFactor
        if (j > 0) {
          dnpv -= j * cashFlows[j] / Math.pow(1 + rate, j + 1)
        }
      }

      if (Math.abs(npv) < tolerance) {
        return rate * 100 // 转换为百分比
      }

      if (dnpv !== 0) {
        rate = rate - npv / dnpv
      } else {
        break
      }
    }

    return rate * 100
  }
}

// 数字格式化
export const formatNumber = (value: number | string, options: {
  decimals?: number
  thousands?: boolean
  prefix?: string
  suffix?: string
} = {}): string => {
  const {
    decimals = 2,
    thousands = false,
    prefix = '',
    suffix = ''
  } = options

  let num = MoneyCalculator.round(value, decimals)

  if (thousands) {
    num = Number(num.toLocaleString('zh-CN', {
      minimumFractionDigits: decimals,
      maximumFractionDigits: decimals
    }))
  }

  return `${prefix}${num}${suffix}`
}

// 安全数学运算（避免浮点数精度问题）
export const safeMath = {
  add: MoneyCalculator.add,
  subtract: MoneyCalculator.subtract,
  multiply: MoneyCalculator.multiply,
  divide: MoneyCalculator.divide,
  round: MoneyCalculator.round,
  compare: MoneyCalculator.compare,
  equals: MoneyCalculator.equals
}
