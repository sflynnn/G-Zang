# G-Zang 移动端应用

基于 uni-app 开发的跨平台移动端财务管理系统。

## 项目特性

- 🚀 基于 uni-app 框架，支持多端部署（H5、小程序、App）
- 📱 Vue 3 + TypeScript + Composition API
- 🎨 现代化 UI 设计，响应式布局
- 🔄 Pinia 状态管理，支持数据持久化
- 🌐 集成 shared 包，统一 API 调用
- 📦 模块化架构，便于维护和扩展
- 🔧 完善的开发工具链（ESLint、Prettier、TypeScript）

## 技术栈

- **框架**: uni-app 3.x + Vue 3
- **语言**: TypeScript
- **状态管理**: Pinia
- **UI组件**: uni-ui
- **网络请求**: Axios (通过 shared 包)
- **样式**: SCSS + CSS Variables
- **构建工具**: Vite
- **代码规范**: ESLint + Prettier

## 项目结构

```
apps/mobile/
├── src/
│   ├── api/              # API 接口封装
│   ├── components/       # 可复用组件
│   ├── composables/      # 组合式函数
│   ├── pages/            # 页面组件
│   │   ├── home/         # 首页
│   │   ├── accounting/   # 记账页
│   │   ├── bills/        # 账单页
│   │   ├── analysis/     # 统计页
│   │   └── profile/      # 个人中心
│   ├── static/           # 静态资源
│   ├── stores/           # Pinia 状态管理
│   ├── styles/           # 全局样式
│   ├── types/            # TypeScript 类型定义
│   ├── utils/            # 工具函数
│   ├── App.vue           # 应用入口
│   └── main.ts           # 主入口文件
├── .env.example          # 环境变量示例
├── manifest.json         # uni-app 应用配置
├── pages.json           # 页面路由配置
├── tsconfig.json        # TypeScript 配置
├── vite.config.ts       # Vite 配置
├── uni.scss             # 全局样式
└── package.json         # 项目配置
```

## 开发指南

### 环境要求

- Node.js >= 18.0.0
- pnpm >= 8.0.0
- 支持的开发工具：HBuilderX、VS Code 等

### 安装依赖

```bash
# 进入移动端项目目录
cd apps/mobile

# 安装依赖
pnpm install
```

### 开发运行

```bash
# H5 开发
pnpm run dev:h5

# 微信小程序开发
pnpm run dev:mp-weixin

# App 开发
pnpm run dev:app
```

### 构建发布

```bash
# H5 构建
pnpm run build:h5

# 微信小程序构建
pnpm run build:mp-weixin

# App 构建
pnpm run build:app
```

## 页面说明

### 主要页面

1. **首页 (home)**: 资产概览、快捷操作、今日收支、最近记录
2. **记账 (accounting)**: 支持收入、支出、转账的记账功能
3. **账单 (bills)**: 交易记录列表，支持筛选和搜索
4. **统计 (analysis)**: 数据统计分析和图表展示
5. **我的 (profile)**: 个人中心和设置

### 页面路由

基于 uni-app 的 `pages.json` 配置，支持底部 Tab 导航和页面跳转。

## 状态管理

使用 Pinia 进行状态管理，主要的 store 模块：

- `auth`: 认证相关（登录、注册、用户信息）
- `user`: 用户信息管理
- `accounting`: 记账相关（分类、账户、交易）
- `bill`: 账单列表和筛选
- `analysis`: 统计数据和图表
- `app`: 应用全局状态（主题、语言等）

## API 集成

通过 shared 包统一管理 API 调用：

```typescript
import { userApi, authApi, accountingApi } from '@/api'

// 使用示例
const userInfo = await userApi.getCurrentUser()
const transactions = await accountingApi.getTransactions()
```

## 样式规范

### 设计系统

- 使用 CSS Variables 定义设计 tokens
- 支持亮色/暗色主题切换
- 响应式设计，适配不同屏幕尺寸

### 组件样式

```scss
.my-component {
  // 使用设计系统变量
  padding: var(--spacing-md);
  border-radius: var(--border-radius-md);
  background-color: var(--background-white);

  &:active {
    background-color: var(--background-light);
  }
}
```

## 工具函数

提供丰富的工具函数：

- **格式化**: `formatAmount`, `formatDateTime`, `formatRelativeTime`
- **验证**: `validatePhoneNumber`, `validatePasswordStrength`
- **存储**: `Storage`, `CacheStorage`, `PreferencesStorage`
- **权限**: `PermissionChecker`, `SystemPermission`
- **日期**: `formatDate`, `getDateRange`, `isWeekday`
- **数学**: `MoneyCalculator`, `StatisticsCalculator`
- **设备**: `DeviceUtils`, `CameraUtils`

## 开发规范

### 代码规范

- 使用 TypeScript 严格模式
- 遵循 Vue 3 Composition API 最佳实践
- 使用 ESLint 和 Prettier 保持代码风格一致
- 提交前运行代码检查

### 组件开发

```vue
<template>
  <view class="my-component">
    <!-- 模板内容 -->
  </view>
</template>

<script setup lang="ts">
// 类型定义
interface Props {
  title: string
  count?: number
}

const props = withDefaults(defineProps<Props>(), {
  count: 0
})

// 响应式数据
const count = ref(0)

// 方法
const handleClick = () => {
  count.value++
}
</script>

<style lang="scss" scoped>
.my-component {
  // 组件样式
}
</style>
```

### 状态管理

```typescript
// 定义 Store
export const useMyStore = defineStore('myStore', () => {
  const count = ref(0)

  const increment = () => {
    count.value++
  }

  return {
    count: readonly(count),
    increment
  }
})

// 使用 Store
const myStore = useMyStore()
myStore.increment()
```

## 部署说明

### H5 部署

构建后的文件部署到 Web 服务器，支持现代浏览器。

### 小程序部署

1. 构建微信小程序版本
2. 使用微信开发者工具上传代码
3. 提交审核并发布

### App 部署

1. 构建 App 版本
2. 使用 HBuilderX 或相关工具打包
3. 提交应用商店审核

## 常见问题

### 开发环境问题

1. **依赖安装失败**: 确保使用 pnpm 包管理器
2. **TypeScript 错误**: 检查 tsconfig.json 配置
3. **样式不生效**: 确认 SCSS 预处理器配置正确

### 运行时问题

1. **API 请求失败**: 检查网络连接和 API 地址配置
2. **页面跳转异常**: 确认页面路由配置正确
3. **状态不同步**: 检查 Pinia store 的使用方式

## 贡献指南

1. Fork 项目
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 创建 Pull Request

## 许可证

本项目采用 MIT 许可证 - 查看 [LICENSE](../LICENSE) 文件了解详情。

## 联系方式

- 项目主页: [G-Zang](https://github.com/your-org/g-zang)
- 问题反馈: [Issues](https://github.com/your-org/g-zang/issues)
- 邮箱: contact@g-zang.com
