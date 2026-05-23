import { beforeAll, afterEach, afterAll } from 'vitest'
import { config } from '@vue/test-utils'

// 全局测试设置

beforeAll(() => {
  // 全局前置设置
})

afterEach(() => {
  // 每个测试后清理
  config.global.stubs = {}
})

afterAll(() => {
  // 全局后置设置
})

// Mock Naive UI 组件
config.global.stubs['n-button'] = {
  template: '<button><slot /></button>',
}
config.global.stubs['n-input'] = {
  template: '<input />',
}
config.global.stubs['n-card'] = {
  template: '<div><slot /></div>',
}
