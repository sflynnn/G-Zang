// G-Zang 移动端存储工具函数

import envConfig from '@/config/env'

// 存储键前缀
const PREFIX = envConfig.STORAGE_PREFIX

// 本地存储封装
export class Storage {
  // 设置存储
  static set(key: string, value: any): void {
    try {
      const serializedValue = JSON.stringify(value)
      uni.setStorageSync(`${PREFIX}${key}`, serializedValue)
    } catch (error) {
      console.error('存储数据失败:', error)
    }
  }

  // 获取存储
  static get<T = any>(key: string, defaultValue?: T): T | null {
    try {
      const value = uni.getStorageSync(`${PREFIX}${key}`)
      if (value === null || value === undefined || value === '') {
        return defaultValue || null
      }
      return JSON.parse(value)
    } catch (error) {
      console.error('获取存储数据失败:', error)
      return defaultValue || null
    }
  }

  // 删除存储
  static remove(key: string): void {
    try {
      uni.removeStorageSync(`${PREFIX}${key}`)
    } catch (error) {
      console.error('删除存储数据失败:', error)
    }
  }

  // 清空所有存储
  static clear(): void {
    try {
      const info = uni.getStorageInfoSync()
      const keys = info.keys.filter(key => key.startsWith(PREFIX))
      keys.forEach(key => uni.removeStorageSync(key))
    } catch (error) {
      console.error('清空存储失败:', error)
    }
  }

  // 检查存储是否存在
  static has(key: string): boolean {
    try {
      const value = uni.getStorageSync(`${PREFIX}${key}`)
      return value !== null && value !== undefined && value !== ''
    } catch (error) {
      return false
    }
  }

  // 获取存储信息
  static getInfo(): any {
    try {
      return uni.getStorageInfoSync()
    } catch (error) {
      console.error('获取存储信息失败:', error)
      return null
    }
  }
}

// 缓存存储类
export class CacheStorage {
  private static cache = new Map<string, { value: any; timestamp: number }>()

  // 设置缓存（带过期时间）
  static set(key: string, value: any, ttl = envConfig.CACHE_MAX_AGE): void {
    this.cache.set(key, {
      value,
      timestamp: Date.now() + ttl
    })
  }

  // 获取缓存
  static get<T = any>(key: string): T | null {
    const item = this.cache.get(key)
    if (!item) return null

    if (Date.now() > item.timestamp) {
      this.cache.delete(key)
      return null
    }

    return item.value
  }

  // 删除缓存
  static remove(key: string): void {
    this.cache.delete(key)
  }

  // 清空缓存
  static clear(): void {
    this.cache.clear()
  }

  // 清理过期缓存
  static cleanExpired(): void {
    const now = Date.now()
    for (const [key, item] of this.cache.entries()) {
      if (now > item.timestamp) {
        this.cache.delete(key)
      }
    }
  }
}

// 用户偏好存储
export class PreferencesStorage {
  private static readonly THEME_KEY = 'theme'
  private static readonly LANGUAGE_KEY = 'language'
  private static readonly CURRENCY_KEY = 'currency'
  private static readonly SETTINGS_KEY = 'settings'

  // 主题设置
  static getTheme(): 'light' | 'dark' | 'auto' {
    return Storage.get(this.THEME_KEY, 'light')
  }

  static setTheme(theme: 'light' | 'dark' | 'auto'): void {
    Storage.set(this.THEME_KEY, theme)
  }

  // 语言设置
  static getLanguage(): string {
    return Storage.get(this.LANGUAGE_KEY, 'zh-CN')
  }

  static setLanguage(language: string): void {
    Storage.set(this.LANGUAGE_KEY, language)
  }

  // 货币设置
  static getCurrency(): string {
    return Storage.get(this.CURRENCY_KEY, 'CNY')
  }

  static setCurrency(currency: string): void {
    Storage.set(this.CURRENCY_KEY, currency)
  }

  // 应用设置
  static getSettings(): Record<string, any> {
    return Storage.get(this.SETTINGS_KEY, {})
  }

  static setSettings(settings: Record<string, any>): void {
    Storage.set(this.SETTINGS_KEY, settings)
  }

  static updateSetting(key: string, value: any): void {
    const settings = this.getSettings()
    settings[key] = value
    this.setSettings(settings)
  }
}

// 搜索历史存储
export class SearchHistoryStorage {
  private static readonly HISTORY_KEY = 'search_history'
  private static readonly MAX_HISTORY = 20

  // 获取搜索历史
  static getHistory(): string[] {
    return Storage.get(this.HISTORY_KEY, [])
  }

  // 添加搜索记录
  static addHistory(keyword: string): void {
    if (!keyword.trim()) return

    const history = this.getHistory()
    const filtered = history.filter(item => item !== keyword)
    filtered.unshift(keyword)

    // 限制历史记录数量
    const limited = filtered.slice(0, this.MAX_HISTORY)
    Storage.set(this.HISTORY_KEY, limited)
  }

  // 删除搜索记录
  static removeHistory(keyword: string): void {
    const history = this.getHistory()
    const filtered = history.filter(item => item !== keyword)
    Storage.set(this.HISTORY_KEY, filtered)
  }

  // 清空搜索历史
  static clearHistory(): void {
    Storage.set(this.HISTORY_KEY, [])
  }
}

// 临时数据存储（页面间传递数据）
export class TempDataStorage {
  private static readonly TEMP_KEY = 'temp_data'

  // 设置临时数据
  static set(key: string, value: any): void {
    const tempData = Storage.get(this.TEMP_KEY, {})
    tempData[key] = value
    Storage.set(this.TEMP_KEY, tempData)
  }

  // 获取临时数据
  static get<T = any>(key: string): T | null {
    const tempData = Storage.get(this.TEMP_KEY, {})
    return tempData[key] || null
  }

  // 删除临时数据
  static remove(key: string): void {
    const tempData = Storage.get(this.TEMP_KEY, {})
    delete tempData[key]
    Storage.set(this.TEMP_KEY, tempData)
  }

  // 清空临时数据
  static clear(): void {
    Storage.set(this.TEMP_KEY, {})
  }
}
