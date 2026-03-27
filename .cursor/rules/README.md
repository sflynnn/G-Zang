# G-Zang MDC 规则系统

## 概述

G-Zang 项目的 MDC（Model-Driven Code generation）规则系统采用分层架构，基于专家角色驱动的开发规范体系，为不同技术领域提供专门的开发指导。

## 规则层次

### 1. 通用规范层
- `mdc_rules.mdc` — 全项目通用规范（前端/后端/数据库基础）

### 2. 应用端专用规范层
- `admin.mdc` — 管理端 (Vue 3 + TypeScript + Naive UI)
- `pc-main.mdc` — PC主应用 (qiankun 微前端)
- `pc-personal.mdc` — PC个人记账子应用
- `pc-business.mdc` — PC企业业务子应用
- `mobile.mdc` — 移动端 (uni-app)
- `backend.mdc` — 后端服务 (Spring Boot + Java)

### 3. 专项技术规范层
| 规则文件 | 专家角色 | 描述 |
|---------|---------|------|
| `security.mdc` | Security Engineer | 认证、授权、SQL注入、XSS、OWASP Top 10 |
| `database.mdc` | Database Optimizer | 索引设计、查询优化、N+1预防、安全迁移 |
| `devops.mdc` | DevOps Automator | CI/CD流水线、Docker、Kubernetes、监控告警 |
| `git-workflow.mdc` | Git Workflow Master | 分支策略、Conventional Commits、PR流程 |
| `testing.mdc` | QA Engineer | 单元测试、集成测试、E2E测试、覆盖率目标 |
| `code-review.mdc` | Code Reviewer | 审查标准、Blocker/Suggestion/Nit分类、评论格式 |
| `ui-design.mdc` | UI Designer | 设计令牌、组件样式、WCAG无障碍标准 |
| `technical-writing.mdc` | Technical Writer | README模板、OpenAPI文档、CHANGELOG规范 |

## 路径匹配规则

| 规则文件 | 匹配路径 | 适用场景 |
|---------|---------|---------|
| `mdc_rules.mdc` | 除专用目录外的所有文件 | 通用开发规范 |
| `admin.mdc` | `apps/pc-admin/**`, `**/admin/**` | 管理端开发 |
| `pc-main.mdc` | `apps/pc-main/**` | PC主应用开发 |
| `pc-personal.mdc` | `apps/pc-personal/**` | PC个人记账开发 |
| `pc-business.mdc` | `apps/pc-business/**` | PC企业业务开发 |
| `mobile.mdc` | `apps/mobile/**` | 移动端开发 |
| `backend.mdc` | `server/**`, `**/*.java` | 后端服务开发 |
| `security.mdc` | `server/**`, `**/*.java` | 安全相关开发 |
| `database.mdc` | `**/mapper/**`, `**/*Mapper.xml` | 数据库相关开发 |
| `devops.mdc` | `.github/**`, `**/docker/**`, `**/k8s/**` | DevOps 配置 |
| `git-workflow.mdc` | `**/*` | Git 协作流程 |
| `testing.mdc` | `**/*.spec.ts`, `**/*.test.ts`, `server/src/test/**` | 测试代码 |
| `code-review.mdc` | `**/*` | 代码审查场景 |
| `ui-design.mdc` | `apps/*/**` | 前端 UI 开发 |
| `technical-writing.mdc` | `docs/**`, `**/README.md` | 文档编写 |

## 使用方法

### 自动应用
当在对应目录下开发时，Cursor 会自动应用相应的 MDC 规则：

```bash
# 在后端开发
cd server/src/main/java/
# 自动应用 backend.mdc + security.mdc + database.mdc

# 在前端开发
cd apps/pc-personal/src/components/
# 自动应用 pc-personal.mdc + ui-design.mdc

# 在 Dockerfile 开发
cd server/
# 自动应用 devops.mdc
```

### 手动指定
在代码生成时手动指定规则：

```
请根据 security.mdc 规则实现 JWT 认证功能
请根据 database.mdc 规则设计交易表结构
请根据 ui-design.mdc 规范实现 QuickRecord 组件
```

## 规则配置

### 规则文件结构

每个 MDC 规则文件包含：
- **头部配置**：alwaysApply、filePathPatterns、framework 等
- **应用概述**：定位和核心功能
- **技术栈规范**：详细的技术选型
- **项目结构**：目录组织规范
- **组件设计**：UI 组件和业务逻辑规范
- **代码生成**：具体的代码生成指导

### 配置参数说明

| 参数 | 类型 | 说明 |
|------|------|------|
| `alwaysApply` | boolean | 是否总是应用此规则 |
| `filePathPatterns` | string[] | 文件路径匹配模式 |
| `framework` | string | 框架标识 |
| `description` | string | 规则描述 |

## 添加新规则

1. 在 `.cursor/rules/` 目录创建新的 `.mdc` 文件
2. 在 `rules.json` 的 `rules` 数组中添加规则配置
3. 在 `autoApply.priority` 中设置优先级（通用规则放最后）

```yaml
---
alwaysApply: false
filePathPatterns:
  - "your/custom/path/**"
framework: "Your Framework"
description: "Your custom rule description"
---
```

## 开发规范速查

### 命名规范
- **组件**：PascalCase (`UserList.vue`)
- **文件**：kebab-case (`user-list.vue`)
- **函数**：camelCase (`getUserById`)
- **常量**：UPPER_SNAKE_CASE (`API_BASE_URL`)

### 代码结构
- **前端**：Composition API + `<script setup>`
- **后端**：分层架构 (Controller → Service → Mapper)
- **类型安全**：TypeScript + Java 强类型

### 提交规范
```
<type>(<scope>): <subject>
feat(transaction): add voice recording support
fix(auth): prevent session hijacking
docs: update API documentation
```

## 相关文档

- [技术架构设计规范](../../docs/技术文档/技术架构设计规范.md)
- [前端开发规范](../../docs/技术文档/前端开发规范.md)
- [后端开发规范](../../docs/技术文档/后端开发规范.md)
- [安全设计文档](../../docs/技术文档/安全设计文档.md)
- [API 规范](../../docs/技术文档/API 规范.md)

---

**维护人员**：开发团队
**最后更新**：2026-03-27
