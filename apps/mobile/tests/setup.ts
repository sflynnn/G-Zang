import { beforeAll, afterEach, afterAll, vi } from 'vitest'

// 全局测试设置
beforeAll(() => {
  // 全局前置设置
})

afterEach(() => {
  // 每个测试后清理
})

afterAll(() => {
  // 全局后置设置
})

// Mock uni 对象 (uni-app API)
vi.mock('vue', async () => {
  const actual = await vi.importActual('vue')
  return {
    ...actual as any,
  }
})
