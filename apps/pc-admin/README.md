# G-Zang PC Admin

G-Zang (归藏) 财务管理系统 - 管理端

## 技术栈

- **Vue 3** - 渐进式 JavaScript 框架
- **TypeScript** - 类型安全的 JavaScript
- **Vite** - 快速的构建工具
- **Tailwind CSS** - 实用优先的 CSS 框架
- **Naive UI** - Vue 3 组件库
- **Vue Router** - 官方路由管理器
- **Pinia** - 状态管理库
- **Axios** - HTTP 客户端

## 功能特性

### 🎯 核心功能
- ✅ 用户管理 - 用户的增删改查、角色分配
- ✅ 角色管理 - 角色创建、权限分配
- ✅ 公司管理 - 多租户公司组织管理
- ✅ 权限管理 - 系统权限的配置管理
- ✅ 系统设置 - 系统配置、邮件设置、安全设置
- ✅ 数据统计 - 仪表板数据展示

### 🎨 界面特性
- 📱 响应式设计，支持多种屏幕尺寸
- 🌙 深色/浅色主题切换
- 🧭 侧边栏导航，折叠展开
- 📊 数据表格，支持排序、筛选、分页
- 🔍 搜索和筛选功能
- 📝 表单验证和错误提示
- 🎯 企业级管理后台设计，遵循"智简·归藏"设计理念
- 🎨 管理端专用颜色调色板：归藏青(primary)、琉璃金(secondary)
- 🔐 专业的管理员登录界面，支持记住密码功能

## 项目结构

```
src/
├── api/           # API 接口定义
├── components/    # 通用组件
├── layouts/       # 布局组件
├── router/        # 路由配置
├── stores/        # 状态管理
├── styles/        # 样式文件
├── utils/         # 工具函数
└── views/         # 页面组件
    ├── Dashboard.vue          # 仪表板
    ├── Login.vue              # 登录页
    ├── UserManagement.vue     # 用户管理
    ├── RoleManagement.vue     # 角色管理
    ├── CompanyManagement.vue  # 公司管理
    ├── PermissionManagement.vue # 权限管理
    ├── SystemSettings.vue     # 系统设置
    └── NotFound.vue           # 404页面
```

## 环境要求

- Node.js >= 18.0.0
- npm >= 8.0.0 或 pnpm >= 7.0.0

## 安装依赖

```bash
# 使用 npm
npm install

# 使用 pnpm
pnpm install
```

## 环境配置

复制环境变量文件：

```bash
cp .env.example .env
```

编辑 `.env` 文件，配置以下变量：

```env
# API 基础地址
VITE_API_BASE_URL=http://localhost:8081

# 应用端口
VITE_PORT=8082

# 是否启用开发模式
VITE_DEV_MODE=true

# 应用标题
VITE_APP_TITLE=G-Zang 管理端
```

## 开发运行

```bash
# 启动开发服务器
npm run dev

# 或使用 pnpm
pnpm dev
```

应用将在 `http://localhost:8082` 上运行。

## 构建生产版本

```bash
# 构建生产版本
npm run build

# 预览构建结果
npm run preview
```

## 代码检查

```bash
# TypeScript 类型检查
npm run type-check
```

## API 接口

管理端通过 RESTful API 与后端服务通信。主要接口包括：

### 认证接口
- `POST /api/v1/auth/login` - 用户登录
- `POST /api/v1/auth/register` - 用户注册

### 用户管理
- `GET /api/v1/users` - 获取用户列表
- `POST /api/v1/users` - 创建用户
- `PUT /api/v1/users/{id}` - 更新用户
- `DELETE /api/v1/users/{id}` - 删除用户

### 其他接口
- 角色管理、公司管理、权限管理等

完整的 API 文档请参考项目根目录下的 `api-docs.json` 文件。

## 开发指南

### 组件开发
- 使用 Vue 3 Composition API
- 组件应为 TypeScript 文件 (`.vue`)
- 使用 `<script setup lang="ts">` 语法

### 状态管理
- 使用 Pinia 进行状态管理
- 按功能模块组织 store

### API 调用
- 统一在 `src/api/` 目录下定义接口
- 使用 axios 进行 HTTP 请求
- 请求/响应拦截器处理认证和错误

### 样式规范
- 使用 CSS Variables 支持主题切换
- 遵循 Naive UI 设计规范
- 响应式设计，移动端优先

## 部署说明

1. 构建生产版本：`npm run build`
2. 将 `dist/` 目录部署到 Web 服务器
3. 配置反向代理，将 API 请求转发到后端服务
4. 配置 HTTPS (推荐)

## 浏览器支持

- Chrome >= 90
- Firefox >= 88
- Safari >= 14
- Edge >= 90

## 许可证

MIT License
