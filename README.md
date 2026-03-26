# G-Zang (归藏) - 财务管理系统

一款全场景财务管理系统，致力于通过"平台隔离、业务解耦"的架构，为个人用户提供便捷的日常记账体验，为小微企业提供专业的经营核算服务。

## 项目架构

本项目采用微前端架构，使用 pnpm workspace 进行 monorepo 管理：

```
g-zang-monorepo/
├── packages/
│   └── shared/              # 共享包 (TypeScript类型定义、API封装)
├── apps/
│   ├── pc-main/            # PC主应用 (qiankun主应用)
│   ├── pc-personal/        # PC个人用户子应用
│   ├── pc-business/        # PC企业用户子应用 (待开发)
│   └── pc-admin/           # PC管理端子应用 (待开发)
└── server/                 # Java Spring Boot后端
```

## 技术栈

### 前端
- **Vue 3** + **TypeScript** + **Composition API**
- **qiankun** - 微前端框架
- **Naive UI** - PC端UI组件库
- **Vite** - 构建工具
- **Pinia** - 状态管理
- **Vue Router** - 路由管理
- **Axios** - HTTP请求

### 后端
- **Java 17** + **Spring Boot 3**
- **MyBatis Plus** - ORM框架
- **JWT** - 身份认证
- **Redis** - 缓存
- **MySQL** - 数据库

## 快速开始

### 环境要求

- Node.js >= 18.0.0
- pnpm >= 8.0.0
- Java >= 17
- MySQL >= 8.0

### 安装依赖

```bash
# 安装所有依赖
pnpm install

# 或者分别安装
pnpm --filter shared install
pnpm --filter pc-main install
pnpm --filter pc-personal install
```

### 启动开发服务器

```bash
# 启动后端服务
cd server
./mvnw spring-boot:run

# 启动PC主应用 (端口8080)
pnpm dev:pc-main

# 启动PC个人用户子应用 (端口8081)
pnpm dev:pc-personal

# 启动PC企业用户子应用 (端口8082) - 待开发
pnpm dev:pc-business

# 启动PC管理端子应用 (端口8083) - 待开发
pnpm dev:pc-admin
```

### 访问应用

- 主应用: http://localhost:8080
- 个人记账: http://localhost:8080/personal
- 企业管理: http://localhost:8080/business (待开发)
- 系统管理: http://localhost:8080/admin (待开发)

### 数据库操作

项目提供了Python脚本来方便地查看和导出数据库数据：

```bash
# 进入数据库脚本目录
cd scripts/database

# 安装依赖（完整版本需要）
pip install -r requirements.txt

# 测试数据库连接
python test_db.py

# 查看数据库表和数据（简化版本）
python read_gzang_db_simple.py

# 查看数据库表和数据，支持导出CSV（完整版本）
python read_gzang_db.py
```

数据库脚本位置：`scripts/database/`

## 项目结构说明

### packages/shared
共享的TypeScript类型定义、API接口封装和工具函数。

- `src/types/` - TypeScript类型定义
- `src/api/` - API接口封装
- `src/utils/` - 工具函数

### apps/pc-main
qiankun主应用，负责注册和加载子应用。

- 全局状态管理
- 路由分发
- 子应用生命周期管理

### apps/pc-personal
个人用户记账子应用。

- 交易记录管理
- 账户管理
- 分类管理
- 财务报表

## 开发规范

详见 `mdc_rules.mdc` 文件。

## 构建部署

```bash
# 构建所有应用
pnpm build

# 构建共享包
pnpm --filter shared build

# 构建主应用
pnpm --filter pc-main build

# 构建个人用户应用
pnpm --filter pc-personal build
```

## 贡献指南

1. Fork 本仓库
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 创建 Pull Request

## 许可证

本项目采用 MIT 许可证 - 查看 [LICENSE](LICENSE) 文件了解详情。

## 联系我们

项目维护者：产品经理团队

---

**G-Zang (归藏)** - 让记账变得简单、有趣、高效！
