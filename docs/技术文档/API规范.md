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

## 12. 预算管理模块 (`/budget`)

### GET /budget/budgets — 获取预算列表

>**描述**: 获取用户的预算列表，支持按账本和周期类型筛选

**查询参数：**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `bookId` | integer | 否 | 账本 ID |
| `periodType` | integer | 否 | 周期类型：1=月预算，2=年预算，3=周预算 |

**响应示例：**

```json
{
  "code": 0,
  "message": "获取成功",
  "data": [
    {
      "id": 1,
      "userId": 100,
      "bookId": 1,
      "categoryId": 101,
      "categoryName": "餐饮",
      "categoryIcon": "restaurant",
      "categoryColor": "#FB8B24",
      "name": "餐饮预算",
      "amount": 3000.00,
      "usedAmount": 1500.00,
      "remainingAmount": 1500.00,
      "usageRate": 50.00,
      "periodType": 1,
      "periodTypeName": "月预算",
      "periodStart": "2024-01-01T00:00:00Z",
      "periodEnd": "2024-01-31T23:59:59Z",
      "warningThreshold": 80,
      "warningEnabled": true,
      "isWarning": false,
      "remark": "每月餐饮预算",
      "createTime": "2024-01-01T00:00:00Z"
    }
  ]
}
```

---

### GET /budget/budgets/{id} — 获取预算详情

>**描述**: 获取指定预算的详细信息

**路径参数：** `id` — 预算 ID

**响应示例：**

```json
{
  "code": 0,
  "message": "获取成功",
  "data": {
    "id": 1,
    "userId": 100,
    "bookId": 1,
    "bookName": "我的账本",
    "categoryId": 101,
    "categoryName": "餐饮",
    "categoryIcon": "restaurant",
    "categoryColor": "#FB8B24",
    "name": "餐饮预算",
    "amount": 3000.00,
    "usedAmount": 1500.00,
    "remainingAmount": 1500.00,
    "usageRate": 50.00,
    "periodType": 1,
    "periodTypeName": "月预算",
    "periodStart": "2024-01-01T00:00:00Z",
    "periodEnd": "2024-01-31T23:59:59Z",
    "warningThreshold": 80,
    "warningEnabled": true,
    "isWarning": false,
    "remark": "每月餐饮预算",
    "createTime": "2024-01-01T00:00:00Z",
    "updateTime": "2024-01-15T10:30:00Z"
  }
}
```

---

### POST /budget/budgets — 创建预算

>**描述**: 创建新的预算

**请求参数：**

```json
{
  "name": "餐饮预算",           // 预算名称（必填）
  "bookId": 1,                  // 账本 ID（可选）
  "categoryId": 101,            // 分类 ID（必填）
  "amount": 3000.00,           // 预算金额（必填）
  "periodType": 1,             // 周期类型：1=月，2=年，3=周（必填）
  "periodStart": "2024-01-01", // 周期开始日期（可选）
  "periodEnd": "2024-01-31",   // 周期结束日期（可选）
  "warningThreshold": 80,       // 预警阈值百分比（可选，默认 80）
  "warningEnabled": true,       // 是否启用预警（可选，默认 true）
  "remark": "每月餐饮预算"      // 备注（可选）
}
```

**响应示例：**

```json
{
  "code": 0,
  "message": "创建成功",
  "data": {
    "id": 2,
    "name": "餐饮预算",
    "amount": 3000.00,
    "periodType": 1,
    "createTime": "2024-01-15T10:30:00Z"
  }
}
```

---

### PUT /budget/budgets/{id} — 更新预算

>**描述**: 更新指定的预算

**路径参数：** `id` — 预算 ID

**请求参数：**

```json
{
  "name": "更新后的预算",       // 预算名称（可选）
  "amount": 3500.00,           // 预算金额（可选）
  "periodStart": "2024-02-01", // 周期开始日期（可选）
  "periodEnd": "2024-02-29",   // 周期结束日期（可选）
  "warningThreshold": 90,       // 预警阈值（可选）
  "warningEnabled": false,      // 是否启用预警（可选）
  "remark": "更新后的备注"      // 备注（可选）
}
```

---

### DELETE /budget/budgets/{id} — 删除预算

>**描述**: 删除指定的预算

**路径参数：** `id` — 预算 ID

**响应示例：**

```json
{
  "code": 0,
  "message": "删除成功",
  "data": null
}
```

---

### GET /budget/budgets/warnings — 获取预警预算列表

>**描述**: 获取所有触发预警的预算

**响应示例：**

```json
{
  "code": 0,
  "message": "获取成功",
  "data": [
    {
      "id": 3,
      "name": "购物预算",
      "amount": 2000.00,
      "usedAmount": 2100.00,
      "usageRate": 105.00,
      "isWarning": true,
      "warningThreshold": 80
    }
  ]
}
```

---

### POST /budget/budgets/{id}/refresh-used — 刷新预算已用金额

>**描述**: 重新计算预算已使用金额

**路径参数：** `id` — 预算 ID

**响应示例：**

```json
{
  "code": 0,
  "message": "刷新成功",
  "data": {
    "id": 1,
    "usedAmount": 1600.00,
    "usageRate": 53.33
  }
}
```

---

## 13. 移动端专用 API (`/mobile`)

### 13.1 设备管理

#### POST /mobile/device/register — 注册设备

>**描述**: 注册移动设备，用于推送通知和多设备管理

**请求参数：**

```json
{
  "deviceId": "uuid-device-id",     // 设备唯一标识（必填）
  "deviceType": "iOS",              // 设备类型：iOS/Android（必填）
  "deviceModel": "iPhone 14 Pro",   // 设备型号（可选）
  "osVersion": "17.0",             // 系统版本（可选）
  "appVersion": "1.0.0"            // APP 版本（可选）
}
```

**响应示例：**

```json
{
  "code": 0,
  "message": "注册成功",
  "data": {
    "sessionId": "session-uuid",
    "expiresIn": 2592000
  }
}
```

---

### 13.2 语音记账

#### POST /mobile/voice/intent — 语音意图识别

>**描述**: 解析语音输入，提取记账意图

**请求参数：**

```json
{
  "audio": "base64-encoded-audio",  // 音频数据（必填）
  "format": "wav"                    // 音频格式（必填）
}
```

**响应示例：**

```json
{
  "code": 0,
  "message": "识别成功",
  "data": {
    "amount": 100.50,
    "type": 2,
    "categoryName": "餐饮",
    "categoryId": 101,
    "confidence": 0.95,
    "rawText": "花了100块5毛买午餐"
  }
}
```

---

### 13.3 OCR 识别

#### POST /mobile/ocr/receipt — 小票识别

>**描述**: 识别小票图片，提取消费信息

**请求参数：** `multipart/form-data`

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `image` | File | 必填 | 小票图片 |

**响应示例：**

```json
{
  "code": 0,
  "message": "识别成功",
  "data": {
    "merchantName": "星巴克",
    "totalAmount": 45.00,
    "items": [
      { "name": "拿铁", "price": 30.00, "quantity": 1 },
      { "name": "蛋糕", "price": 15.00, "quantity": 1 }
    ],
    "transactionTime": "2024-01-15T14:30:00Z",
    "confidence": 0.92
  }
}
```

---

## 14. SDK 使用示例

### 14.1 JavaScript SDK

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

// 获取预算列表
const getBudgets = async (params = {}) => {
  const response = await apiClient.get('/budget/budgets', { params });
  return response.data;
};

// 创建预算
const createBudget = async (budget) => {
  const response = await apiClient.post('/budget/budgets', budget);
  return response.data;
};
```

### 14.2 React Native SDK

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
  return await api.post('/mobile/ocr/receipt', formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  });
};

// 语音意图识别
const recognizeVoice = async (audioBase64: string) => {
  return await api.post('/mobile/voice/intent', {
    audio: audioBase64,
    format: 'wav'
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
| 1.2.0 | 2026-05-22 | 新增预算管理模块 API、移动端专用 API |
| 2.0.0 | 计划中 | 支持企业 ERP 集成 |

---

**文档版本**：1.2.0
**最后更新**：2026-05-22
**维护人员**：后端架构师 / API 开发团队
