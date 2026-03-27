# G-Zang (归藏) API 规范

> **文档版本**：1.0.0
> **最后更新**：2026-03-27
> **维护人员**：后端架构师 / API 开发团队
> **关联规则**：`backend.mdc`、`security.mdc`、`technical-writing.mdc`

---

## 1. 概述

所有 API 接口均采用 RESTful 风格，基于 HTTPS 协议，返回 JSON 格式数据。统一前缀为 `/api/v1/`。

**基础信息：**

| 配置项 | 值 |
|--------|-----|
| 基础 URL | `https://api.gzang.com/v1` |
| 认证方式 | Bearer Token (JWT) |
| 数据格式 | JSON |
| 字符编码 | UTF-8 |
| 限流策略 | 1000 次/分钟/IP |
| 错误格式 | 统一错误码 + 友好提示 |

---

## 2. 认证与授权

### 2.1 认证方式

基于 JWT (JSON Web Token)。用户登录成功后，后端返回 JWT Token，前端在后续请求的 `Authorization` 头中携带 `Bearer <token>`。

```
Authorization: Bearer eyJhbGciOiJIUzI1NiIs...
```

**Token 规范：**

| 类型 | 有效期 | 存储位置 | 说明 |
|------|--------|---------|------|
| Access Token | ≤ 2 小时 | 内存（不持久化） | 携带在 Authorization Header |
| Refresh Token | ≤ 7 天 | HttpOnly Cookie 或安全存储 | 用于刷新 Access Token |

### 2.2 授权方式

基于 RBAC (Role-Based Access Control)。后端通过 Spring Security 结合 JWT 解析出的用户角色和权限，对请求进行授权。

---

## 3. 通用响应结构

### 3.1 成功响应

```json
{
  "code": 0,
  "message": "操作成功",
  "data": {},
  "timestamp": 1711545600000
}
```

### 3.2 分页响应

```json
{
  "code": 0,
  "message": "查询成功",
  "data": {
    "total": 156,
    "pages": 8,
    "current": 1,
    "size": 20,
    "records": []
  },
  "timestamp": 1711545600000
}
```

### 3.3 错误响应

```json
{
  "code": 400,
  "message": "参数错误：金额不能为空",
  "data": null,
  "errors": [
    {
      "field": "amount",
      "message": "金额不能为空"
    }
  ],
  "timestamp": 1711545600000
}
```

### 3.4 错误码定义

| 错误码 | 说明 | HTTP 状态码 |
|--------|------|------------|
| 0 | 成功 | 200 |
| 400 | 参数错误 / 参数校验失败 | 400 |
| 401 | 未认证 / Token 无效或过期 | 401 |
| 403 | 无权限访问 | 403 |
| 404 | 资源不存在 | 404 |
| 409 | 资源冲突（如重复创建） | 409 |
| 422 | 业务规则校验失败 | 422 |
| 429 | 请求过于频繁（限流） | 429 |
| 500 | 服务器内部错误 | 500 |
| 503 | 服务不可用 | 503 |

---

## 4. 认证模块 (`/auth`)

### POST /auth/login — 用户登录

**请求参数：**

```json
{
  "username": "string",  // 用户名（手机号/邮箱）
  "password": "string"   // 密码
}
```

**响应示例：**

```json
{
  "code": 0,
  "message": "登录成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIs...",
    "refreshToken": "refresh_token_here",
    "expiresIn": 7200,
    "user": {
      "id": 1,
      "username": "user@example.com",
      "nickname": "用户昵称",
      "avatar": "https://cdn.gzang.com/avatar/1.jpg",
      "roleId": 1,
      "companyId": null,
      "status": 1
    }
  }
}
```

**错误码：**
- `400`: 参数错误
- `401`: 用户名或密码错误
- `403`: 账户被禁用

---

### POST /auth/register — 用户注册

**请求参数：**

```json
{
  "username": "string",       // 用户名（手机号/邮箱）
  "password": "string",       // 密码
  "nickname": "string",       // 用户昵称
  "companyName": "string"     // 公司名称（企业用户可选）
}
```

---

### POST /auth/refresh — 刷新访问令牌

**请求头：**

```
Authorization: Bearer <refresh_token>
```

---

## 5. 用户模块 (`/user`)

### GET /user/info — 获取当前用户信息

**响应示例：**

```json
{
  "code": 0,
  "message": "获取成功",
  "data": {
    "id": 1,
    "username": "user@example.com",
    "nickname": "用户昵称",
    "avatar": "https://cdn.gzang.com/avatar/1.jpg",
    "roleId": 1,
    "companyId": null,
    "status": 1,
    "createTime": "2024-01-01T00:00:00Z"
  }
}
```

---

### PUT /user/profile — 更新用户信息

**请求参数：**

```json
{
  "nickname": "string",
  "avatar": "string"
}
```

---

## 6. 记账模块 (`/accounting`)

### POST /accounting/transaction — 新增交易记录

> 新增一条交易记录（收入、支出或转账）。
> - 支出交易会从账户余额中扣除金额
> - 转账交易会同时创建两笔关联记录

**请求头：**

```
Authorization: Bearer <access_token>
```

**请求参数：**

```json
{
  "amount": 100.50,           // 交易金额（必填，DECIMAL(19,4)）
  "type": 1,                // 交易类型：1=收入，2=支出，3=转账（必填）
  "categoryId": 101,          // 分类ID（必填）
  "accountId": 201,           // 账户ID（必填）
  "transactionTime": "2024-01-14T10:30:00Z",  // 发生时间（必填，ISO 8601）
  "remark": "午餐费",          // 备注（可选，最大500字符）
  "relatedBusinessId": "ORD001"  // 关联业务ID（企业用户可选）
}
```

**响应示例：**

```json
{
  "code": 0,
  "message": "记账成功",
  "data": {
    "id": 1001,
    "amount": 100.50,
    "type": 2,
    "categoryId": 101,
    "categoryName": "餐饮",
    "accountId": 201,
    "accountName": "现金",
    "transactionTime": "2024-01-14T10:30:00Z",
    "remark": "午餐费",
    "createTime": "2024-01-14T10:35:00Z"
  }
}
```

---

### GET /accounting/transactions — 查询交易记录列表

**查询参数：**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `startDate` | string | 否 | 开始日期 (YYYY-MM-DD) |
| `endDate` | string | 否 | 结束日期 (YYYY-MM-DD) |
| `type` | integer | 否 | 交易类型：1=收入，2=支出 |
| `categoryId` | integer | 否 | 分类 ID |
| `accountId` | integer | 否 | 账户 ID |
| `page` | integer | 否 | 页码，默认 1 |
| `size` | integer | 否 | 每页数量，默认 20 |

**响应示例：**

```json
{
  "code": 0,
  "message": "查询成功",
  "data": {
    "total": 156,
    "pages": 8,
    "current": 1,
    "size": 20,
    "records": [
      {
        "id": 1001,
        "amount": 100.50,
        "type": 2,
        "categoryName": "餐饮",
        "accountName": "现金",
        "transactionTime": "2024-01-14T10:30:00Z",
        "remark": "午餐费",
        "createTime": "2024-01-14T10:35:00Z"
      }
    ]
  }
}
```

---

### PUT /accounting/transaction/{id} — 更新交易记录

**路径参数：** `id` — 交易记录 ID

**请求参数：** 同新增接口

---

### DELETE /accounting/transaction/{id} — 删除交易记录

**路径参数：** `id` — 交易记录 ID

---

## 7. 账户模块 (`/account`)

### GET /account/accounts — 获取账户列表

**响应示例：**

```json
{
  "code": 0,
  "message": "获取成功",
  "data": [
    {
      "id": 201,
      "accountName": "现金",
      "accountType": 1,
      "balance": 1250.50,
      "currency": "CNY",
      "createTime": "2024-01-01T00:00:00Z"
    },
    {
      "id": 202,
      "accountName": "招商银行",
      "accountType": 2,
      "balance": 5000.00,
      "currency": "CNY",
      "createTime": "2024-01-01T00:00:00Z"
    }
  ]
}
```

### POST /account/account — 创建新账户

**请求参数：**

```json
{
  "accountName": "string",    // 账户名称（必填）
  "accountType": 1,          // 账户类型：1=现金，2=银行卡，3=电子支付（必填）
  "initialBalance": 0.00,    // 初始余额（可选，默认 0）
  "currency": "CNY"          // 币种（可选，默认 CNY）
}
```

---

## 8. 分类模块 (`/category`)

### GET /category/categories — 获取分类列表

**查询参数：**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `type` | integer | 否 | 分类类型：1=收入，2=支出 |

**响应示例：**

```json
{
  "code": 0,
  "message": "获取成功",
  "data": [
    {
      "id": 101,
      "categoryName": "餐饮",
      "parentId": 0,
      "type": 2,
      "icon": "restaurant",
      "isSystem": true
    }
  ]
}
```

### POST /category/category — 创建新分类

**请求参数：**

```json
{
  "categoryName": "string",   // 分类名称（必填）
  "parentId": 0,              // 父分类ID（必填，0 为一级分类）
  "type": 1,                  // 分类类型：1=收入，2=支出（必填）
  "icon": "string"            // 图标名称（可选）
}
```

---

## 9. 报表分析模块 (`/report`)

### GET /report/summary — 获取收支汇总

**查询参数：**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `startDate` | string | 是 | 开始日期 (YYYY-MM-DD) |
| `endDate` | string | 是 | 结束日期 (YYYY-MM-DD) |

**响应示例：**

```json
{
  "code": 0,
  "message": "获取成功",
  "data": {
    "totalIncome": 15000.00,
    "totalExpense": 8500.00,
    "netIncome": 6500.00,
    "transactionCount": 156
  }
}
```

---

### GET /report/trend — 获取收支趋势分析

**查询参数：**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `period` | string | 是 | 周期：day / week / month / year |
| `startDate` | string | 是 | 开始日期 |
| `endDate` | string | 是 | 结束日期 |

**响应示例：**

```json
{
  "code": 0,
  "message": "获取成功",
  "data": {
    "period": "month",
    "data": [
      {
        "date": "2024-01",
        "income": 15000.00,
        "expense": 8500.00,
        "net": 6500.00
      }
    ]
  }
}
```

---

### GET /report/category-summary — 分类统计分析

**查询参数：**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `startDate` | string | 是 | 开始日期 |
| `endDate` | string | 是 | 结束日期 |
| `type` | integer | 否 | 分类类型：1=收入，2=支出 |

**响应示例：**

```json
{
  "code": 0,
  "message": "获取成功",
  "data": [
    {
      "categoryId": 101,
      "categoryName": "餐饮",
      "amount": 1200.00,
      "percentage": 14.12,
      "transactionCount": 24
    }
  ]
}
```

---

## 10. 企业管理模块 (`/company`)

### POST /company/company — 创建公司

**请求参数：**

```json
{
  "companyName": "string",    // 公司名称（必填）
  "description": "string"     // 公司描述（可选）
}
```

---

### GET /company/employees — 获取公司员工列表

**响应示例：**

```json
{
  "code": 0,
  "message": "获取成功",
  "data": [
    {
      "id": 1,
      "username": "admin@gzang.com",
      "nickname": "管理员",
      "roleId": 1,
      "roleName": "管理员",
      "status": 1,
      "joinTime": "2024-01-01T00:00:00Z"
    }
  ]
}
```

---

### POST /company/invite — 邀请员工加入公司

**请求参数：**

```json
{
  "email": "string",      // 员工邮箱（必填）
  "roleId": 2            // 角色ID（必填）
}
```

---

## 11. 系统管理模块 (`/system`)

### GET /system/config — 获取系统配置

**响应示例：**

```json
{
  "code": 0,
  "message": "获取成功",
  "data": {
    "siteName": "G-Zang 财务管理系统",
    "maxLoginAttempts": "5",
    "sessionTimeout": "7200"
  }
}
```

---

### PUT /system/config — 更新系统配置

**请求参数：**

```json
{
  "configKey": "string",
  "configValue": "string"
}
```

---

## 12. SDK 使用示例

### 12.1 JavaScript SDK

```javascript
// 初始化 API 客户端
const apiClient = new GZangAPI({
  baseURL: 'https://api.gzang.com/v1',
  timeout: 10000
});

// 设置认证令牌
apiClient.setToken(localStorage.getItem('token'));

// 用户登录
const login = async (username, password) => {
  const response = await apiClient.post('/auth/login', { username, password });
  return response.data;
};

// 获取交易记录
const getTransactions = async (params = {}) => {
  const response = await apiClient.get('/accounting/transactions', { params });
  return response.data;
};

// 新增交易
const createTransaction = async (transaction) => {
  const response = await apiClient.post('/accounting/transaction', transaction);
  return response.data;
};
```

### 12.2 React Native SDK

```typescript
import GZangAPI from 'gzang-api-react-native';

const api = new GZangAPI({
  baseURL: 'https://api.gzang.com/v1'
});

api.setToken(token);

// 上传图片识别小票
const recognizeReceipt = async (imageUri: string) => {
  const formData = new FormData();
  formData.append('image', {
    uri: imageUri,
    type: 'image/jpeg',
    name: 'receipt.jpg'
  });
  return await api.post('/ocr/recognize', formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  });
};
```

---

## 附录 A. OpenAPI 规范

完整的 OpenAPI 3.0 定义请参考 `docs/规范文档/openapi.yaml`（由 `technical-writing.mdc` 规范自动生成）。

---

## 附录 B. 版本历史

| 版本 | 更新日期 | 更新内容 |
|------|----------|----------|
| 1.0.0 | 2024-01-14 | 初始版本发布 |
| 1.1.0 | 2026-03-27 | 规范化响应结构，新增字段级错误详情 |
| 2.0.0 | 计划中 | 支持企业 ERP 集成 |

---

**文档版本**：1.0.0
**最后更新**：2026-03-27
**维护人员**：后端架构师 / API 开发团队
