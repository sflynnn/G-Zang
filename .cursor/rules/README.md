# G-Zang MDC 规则系统

## 📋 概述

G-Zang项目的MDC（Model-Driven Code generation）规则系统采用了分层架构，为不同应用端提供专门的开发规范和代码生成规则。

## 🏗️ 架构设计

### 规则层次
1. **通用规范** (`mdc_rules.mdc`) - 全项目通用规范
2. **端专用规范** - 各应用端的特定规范
   - `admin.mdc` - 管理端
   - `pc-main.mdc` - PC主应用
   - `pc-personal.mdc` - PC个人记账
   - `pc-business.mdc` - PC企业业务
   - `mobile.mdc` - 移动端
   - `backend.mdc` - 后端服务

### 自动应用机制
- **路径匹配**：根据文件路径自动选择对应的规则
- **优先级排序**：端专用规则优先于通用规则
- **智能识别**：支持多路径模式匹配

## 📁 路径匹配规则

| 规则文件 | 匹配路径 | 适用场景 |
|---------|---------|---------|
| `mdc_rules.mdc` | 除专用目录外的所有文件 | 通用开发规范 |
| `admin.mdc` | `apps/pc-admin/**`, `**/admin/**` | 管理端开发 |
| `pc-main.mdc` | `apps/pc-main/**` | PC主应用开发 |
| `pc-personal.mdc` | `apps/pc-personal/**` | PC个人记账开发 |
| `pc-business.mdc` | `apps/pc-business/**` | PC企业业务开发 |
| `mobile.mdc` | `apps/mobile/**` | 移动端开发 |
| `backend.mdc` | `server/**`, `**/*.java` | 后端服务开发 |

## 🚀 使用方法

### 自动应用
当你在对应目录下开发时，Cursor会自动应用相应的MDC规则：

```bash
# 在管理端目录开发
cd apps/pc-admin/src/components/
# 自动应用 admin.mdc 规则

# 在后端开发
cd server/src/main/java/
# 自动应用 backend.mdc 规则
```

### 手动指定
也可以在代码生成时手动指定规则：

```
请根据admin.mdc规则生成用户管理页面
请根据backend.mdc规则生成交易服务
```

## 📋 规则配置

### 规则文件结构
每个MDC规则文件包含：
- **头部配置**：alwaysApply、filePathPatterns、framework等
- **应用概述**：定位和核心功能
- **技术栈规范**：详细的技术选型
- **项目结构**：目录组织规范
- **组件设计**：UI组件和业务逻辑规范
- **代码生成**：具体的代码生成指导

### 配置参数说明

| 参数 | 类型 | 说明 |
|------|------|------|
| `alwaysApply` | boolean | 是否总是应用此规则 |
| `filePathPatterns` | string[] | 文件路径匹配模式 |
| `framework` | string | 框架标识 |
| `description` | string | 规则描述 |

## 🔧 自定义配置

### 添加新规则
1. 在 `.cursor/rules/` 目录创建新的 `.mdc` 文件
2. 在 `rules.json` 中添加规则配置
3. 设置适当的路径匹配模式

### 修改匹配规则
编辑对应规则文件的头部配置：
```yaml
---
alwaysApply: false
filePathPatterns:
  - "your/custom/path/**"
  - "**/another/pattern/**"
framework: "Your Framework"
description: "Your custom rule description"
---
```

## 📚 开发规范

### 命名规范
- **组件**：PascalCase (UserList.vue)
- **文件**：kebab-case (user-list.vue)
- **函数**：camelCase (getUserById)
- **常量**：UPPER_SNAKE_CASE (API_BASE_URL)

### 代码结构
- **前端**：Composition API + `<script setup>`
- **后端**：分层架构 (Controller → Service → Mapper)
- **类型安全**：TypeScript + Java强类型

### 提交规范
```
<type>(<scope>): <subject>

<body>

<footer>
```

## 🔍 故障排除

### 规则未自动应用
1. 检查文件路径是否匹配规则模式
2. 确认规则文件存在且格式正确
3. 查看Cursor日志中的规则加载信息

### 规则冲突
1. 检查多个规则的路径匹配是否重叠
2. 调整规则优先级顺序
3. 使用更具体的路径模式

## 📞 支持

如有问题，请参考：
- [技术架构设计规范](../../docs/技术文档/技术架构设计规范.md)
- [前端开发规范](../../docs/技术文档/前端开发规范.md)
- [后端开发规范](../../docs/技术文档/后端开发规范.md)

---

**维护人员**：开发团队
**最后更新**：2026-01-17
