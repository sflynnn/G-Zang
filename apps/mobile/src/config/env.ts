// G-Zang 移动端环境配置

interface EnvConfig {
  // API配置
  API_BASE_URL: string
  API_TIMEOUT: number

  // 应用配置
  APP_NAME: string
  APP_VERSION: string
  APP_ENV: 'development' | 'production' | 'test'

  // 第三方服务配置
  WECHAT_APPID?: string
  ALIPAY_APPID?: string

  // 功能开关
  ENABLE_VOICE_RECORDING: boolean
  ENABLE_OCR: boolean
  ENABLE_OFFLINE_MODE: boolean

  // 缓存配置
  CACHE_MAX_AGE: number
  STORAGE_PREFIX: string
}

// 环境配置
const envConfig: EnvConfig = {
  // API配置
  API_BASE_URL: (import.meta.env as any).VITE_API_BASE_URL || 'http://localhost:8080/api/v1',
  API_TIMEOUT: 10000,

  // 应用配置
  APP_NAME: 'G-Zang',
  APP_VERSION: '1.0.0',
  APP_ENV: ((import.meta.env as any).MODE || 'development') as 'development' | 'production' | 'test',

  // 第三方服务配置
  WECHAT_APPID: (import.meta.env as any).VITE_WECHAT_APPID,
  ALIPAY_APPID: (import.meta.env as any).VITE_ALIPAY_APPID,

  // 功能开关
  ENABLE_VOICE_RECORDING: true,
  ENABLE_OCR: true,
  ENABLE_OFFLINE_MODE: true,

  // 缓存配置
  CACHE_MAX_AGE: 24 * 60 * 60 * 1000, // 24小时
  STORAGE_PREFIX: 'gzang_mobile_'
}

// 开发环境特殊配置
if (envConfig.APP_ENV === 'development') {
}

// 生产环境特殊配置
if (envConfig.APP_ENV === 'production') {
  // 生产环境下禁用console.log
  console.log = () => {}
  console.warn = () => {}
  console.error = () => {}
}

export default envConfig

// 导出类型
export type { EnvConfig }
