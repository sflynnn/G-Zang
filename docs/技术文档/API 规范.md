# G-Zang (归藏) API 规范

## 1. 概述

所有 API 接口均采用 RESTful 风格，基于 HTTPS 协议，返回 JSON 格式数据。统一前缀为 `/api/v1/`。

## 2. 认证与授权

- **认证方式**：基于 JWT (JSON Web Token)。用户登录成功后，后端返回 JWT Token，前端在后续请求的 `Authorization` 头中携带 `Bearer <token>`。
- **授权方式**：基于 RBAC (Role-Based Access Control)。后端通过 Spring Security 结合 JWT 解析出的用户角色和权限，对请求进行授权。

## 3. 通用响应结构

所有 API 响应均采用以下统一结构：

```json
{
  "code": 0,           // 业务状态码，0 表示成功，非 0 表示失败
  "message": "操作成功", // 提示信息
  "data": {}           // 业务数据，成功时返回，失败时可能为 null
}
```

### 3.1 常见业务状态码

| Code | Message | 描述 |
| :--- | :--- | :--- |
| 0 | 操作成功 | 请求成功 |
| 400 | 参数错误 | 请求参数不合法 |
| 401 | 未认证 | 用户未登录或 Token 无效 |
| 403 | 无权限 | 用户无权访问该资源 |
| 500 | 服务器内部错误 | 服务器处理请求时发生未知错误 |

## 4. API 接口列表

### 4.1 认证模块 (`/api/v1/auth`)

#### 4.1.1 用户登录
- **URL**：`/api/v1/auth/login`
- **方法**：`POST`
- **请求体**：
  ```json
  {
    "username": "string", // 用户名（手机号/邮箱）
    "password": "string"  // 密码
  }
  ```
- **响应体**：
  ```json
  {
    "code": 0,
    "message": "登录成功",
    "data": {
      "token": "string", // JWT Token
      "refreshToken": "string",
      "user": { /* 用户基本信息 */ }
    }
  }
  ```

#### 4.1.2 用户注册
- **URL**：`/api/v1/auth/register`
- **方法**：`POST`
- **请求体**：
  ```json
  {
    "username": "string",
    "password": "string",
    "nickname": "string"
  }
  ```
- **响应体**：同通用响应结构

### 4.2 用户模块 (`/api/v1/user`)

#### 4.2.1 获取当前用户信息
- **URL**：`/api/v1/user/info`
- **方法**：`GET`
- **响应体**：
  ```json
  {
    "code": 0,
    "message": "获取成功",
    "data": {
      "id": 1,
      "username": "testuser",
      "nickname": "测试用户",
      "roleId": 1, // 角色ID
      "companyId": null // 所属公司ID，个人用户为null
    }
  }
  ```

### 4.3 记账模块 (`/api/v1/accounting`)

#### 4.3.1 新增交易记录
- **URL**：`/api/v1/accounting/transaction`
- **方法**：`POST`
- **请求体**：
  ```json
  {
    "amount": 100.50,       // 交易金额
    "type": 1,              // 交易类型 (1:收入, 2:支出)
    "categoryId": 101,      // 分类ID
    "accountId": 201,       // 账户ID
    "transactionTime": "2026-01-07 10:30:00", // 交易时间
    "remark": "午餐",       // 备注
    "relatedBusinessId": "string" // 关联业务ID，公司记账时使用
  }
  ```
- **响应体**：同通用响应结构

#### 4.3.2 查询交易记录列表
- **URL**：`/api/v1/accounting/transactions`
- **方法**：`GET`
- **查询参数**：
  - `startDate`: 开始日期 (YYYY-MM-DD)
  - `endDate`: 结束日期 (YYYY-MM-DD)
  - `type`: 交易类型 (1:收入, 2:支出)
  - `categoryId`: 分类ID
  - `accountId`: 账户ID
  - `page`: 页码
  - `size`: 每页数量
- **响应体**：
  ```json
  {
    "code": 0,
    "message": "查询成功",
    "data": {
      "total": 100,
      "records": [
        { /* Transaction Object */ },
        { /* Transaction Object */ }
      ]
    }
  }
  ```

### 4.4 报表模块 (`/api/v1/report`)

#### 4.4.1 获取收支汇总
- **URL**：`/api/v1/report/summary`
- **方法**：`GET`
- **查询参数**：
  - `startDate`: 开始日期 (YYYY-MM-DD)
  - `endDate`: 结束日期 (YYYY-MM-DD)
- **响应体**：
  ```json
  {
    "code": 0,
    "message": "获取成功",
    "data": {
      "totalIncome": 5000.00,
      "totalExpense": 2000.00,
      "netIncome": 3000.00
    }
  }
  ```

## 5. 错误处理

后端将通过统一的异常处理器捕获所有异常，并返回上述通用响应结构，`code` 为非 0 值，`message` 包含错误详情。
