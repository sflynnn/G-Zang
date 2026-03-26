# G-Zang (归藏) API 文档

## 概述

本文档描述了G-Zang系统的REST API接口规范。所有API均采用RESTful设计，支持JSON数据格式，使用JWT进行身份认证。

**基础信息：**
- **基础URL**: `https://api.gzang.com/v1`
- **认证方式**: Bearer Token (JWT)
- **数据格式**: JSON
- **字符编码**: UTF-8
- **限流策略**: 1000次/分钟/IP

---

## 认证接口

### POST /auth/login
用户登录接口

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
    "user": {
      "id": 1,
      "username": "user@example.com",
      "nickname": "用户昵称",
      "roleId": 1,
      "companyId": null
    }
  }
}
```

**错误码：**
- `400`: 参数错误
- `401`: 用户名或密码错误
- `403`: 账户被禁用

### POST /auth/register
用户注册接口

**请求参数：**
```json
{
  "username": "string",      // 用户名（手机号/邮箱）
  "password": "string",      // 密码
  "nickname": "string",      // 用户昵称
  "companyName": "string"    // 公司名称（企业用户可选）
}
```

### POST /auth/refresh
刷新访问令牌

**请求头：**
```
Authorization: Bearer <refresh_token>
```

---

## 用户管理接口

### GET /user/info
获取当前用户信息

**请求头：**
```
Authorization: Bearer <access_token>
```

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

### PUT /user/profile
更新用户信息

**请求头：**
```
Authorization: Bearer <access_token>
```

**请求参数：**
```json
{
  "nickname": "string",
  "avatar": "string"
}
```

---

## 记账管理接口

### POST /accounting/transaction
新增交易记录

**请求头：**
```
Authorization: Bearer <access_token>
```

**请求参数：**
```json
{
  "amount": 100.50,           // 交易金额（必填）
  "type": 1,                  // 交易类型：1-收入，2-支出（必填）
  "categoryId": 101,          // 分类ID（必填）
  "accountId": 201,           // 账户ID（必填）
  "transactionTime": "2024-01-14T10:30:00Z", // 交易时间（必填）
  "remark": "午餐费",         // 备注（可选）
  "relatedBusinessId": "ORD001" // 关联业务ID（企业用户可选）
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
    "accountId": 201,
    "transactionTime": "2024-01-14T10:30:00Z",
    "remark": "午餐费",
    "createTime": "2024-01-14T10:35:00Z"
  }
}
```

### GET /accounting/transactions
查询交易记录列表

**请求头：**
```
Authorization: Bearer <access_token>
```

**查询参数：**
```
startDate: 2024-01-01        // 开始日期 (YYYY-MM-DD)
endDate: 2024-01-31          // 结束日期 (YYYY-MM-DD)
type: 1                      // 交易类型 (1-收入, 2-支出)
categoryId: 101              // 分类ID
accountId: 201               // 账户ID
page: 1                      // 页码 (默认1)
size: 20                     // 每页数量 (默认20)
```

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

### PUT /accounting/transaction/{id}
更新交易记录

**请求头：**
```
Authorization: Bearer <access_token>
```

**路径参数：**
- `id`: 交易记录ID

**请求参数：** 同新增接口

### DELETE /accounting/transaction/{id}
删除交易记录

**请求头：**
```
Authorization: Bearer <access_token>
```

**路径参数：**
- `id`: 交易记录ID

---

## 账户管理接口

### GET /account/accounts
获取账户列表

**请求头：**
```
Authorization: Bearer <access_token>
```

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
      "createTime": "2024-01-01T00:00:00Z"
    },
    {
      "id": 202,
      "accountName": "招商银行",
      "accountType": 2,
      "balance": 5000.00,
      "createTime": "2024-01-01T00:00:00Z"
    }
  ]
}
```

### POST /account/account
创建新账户

**请求头：**
```
Authorization: Bearer <access_token>
```

**请求参数：**
```json
{
  "accountName": "string",    // 账户名称（必填）
  "accountType": 1,          // 账户类型：1-现金，2-银行卡，3-电子支付（必填）
  "initialBalance": 0.00     // 初始余额（可选，默认0）
}
```

---

## 分类管理接口

### GET /category/categories
获取分类列表

**请求头：**
```
Authorization: Bearer <access_token>
```

**查询参数：**
```
type: 1    // 分类类型：1-收入，2-支出
```

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
      "isSystem": true
    },
    {
      "id": 102,
      "categoryName": "交通",
      "parentId": 0,
      "type": 2,
      "isSystem": true
    }
  ]
}
```

### POST /category/category
创建新分类

**请求头：**
```
Authorization: Bearer <access_token>
```

**请求参数：**
```json
{
  "categoryName": "string",   // 分类名称（必填）
  "parentId": 0,             // 父分类ID（必填，0为一级分类）
  "type": 1                  // 分类类型：1-收入，2-支出（必填）
}
```

---

## 报表分析接口

### GET /report/summary
获取收支汇总

**请求头：**
```
Authorization: Bearer <access_token>
```

**查询参数：**
```
startDate: 2024-01-01    // 开始日期 (YYYY-MM-DD)
endDate: 2024-01-31      // 结束日期 (YYYY-MM-DD)
```

**响应示例：**
```json
{
  "code": 0,
  "message": "获取成功",
  "data": {
    "totalIncome": 15000.00,     // 总收入
    "totalExpense": 8500.00,     // 总支出
    "netIncome": 6500.00,        // 净收入
    "transactionCount": 156      // 交易笔数
  }
}
```

### GET /report/trend
获取收支趋势分析

**请求头：**
```
Authorization: Bearer <access_token>
```

**查询参数：**
```
period: month    // 周期：day, week, month, year
startDate: 2024-01-01
endDate: 2024-01-31
```

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
      },
      {
        "date": "2024-02",
        "income": 16000.00,
        "expense": 7800.00,
        "net": 8200.00
      }
    ]
  }
}
```

### GET /report/category-summary
分类统计分析

**请求头：**
```
Authorization: Bearer <access_token>
```

**查询参数：**
```
startDate: 2024-01-01
endDate: 2024-01-31
type: 2    // 分类类型：1-收入，2-支出
```

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
    },
    {
      "categoryId": 102,
      "categoryName": "交通",
      "amount": 800.00,
      "percentage": 9.41,
      "transactionCount": 12
    }
  ]
}
```

---

## 企业管理接口

### POST /company/company
创建公司

**请求头：**
```
Authorization: Bearer <access_token>
```

**请求参数：**
```json
{
  "companyName": "string",    // 公司名称（必填）
  "description": "string"     // 公司描述（可选）
}
```

**响应示例：**
```json
{
  "code": 0,
  "message": "创建成功",
  "data": {
    "id": 1,
    "companyName": "XX修理厂",
    "adminUserId": 1,
    "createTime": "2024-01-14T10:00:00Z"
  }
}
```

### GET /company/employees
获取公司员工列表

**请求头：**
```
Authorization: Bearer <access_token>
```

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

### POST /company/invite
邀请员工加入公司

**请求头：**
```
Authorization: Bearer <access_token>
```

**请求参数：**
```json
{
  "email": "string",      // 员工邮箱（必填）
  "roleId": 2            // 角色ID（必填）
}
```

---

## 系统管理接口

### GET /system/config
获取系统配置

**请求头：**
```
Authorization: Bearer <admin_token>
```

**响应示例：**
```json
{
  "code": 0,
  "message": "获取成功",
  "data": {
    "siteName": "G-Zang财务管理系统",
    "maxLoginAttempts": "5",
    "sessionTimeout": "7200"
  }
}
```

### PUT /system/config
更新系统配置

**请求头：**
```
Authorization: Bearer <admin_token>
```

**请求参数：**
```json
{
  "configKey": "string",      // 配置键
  "configValue": "string"     // 配置值
}
```

---

## 错误响应格式

所有API在发生错误时均返回统一的错误响应格式：

```json
{
  "code": 400,                    // 错误码
  "message": "参数错误：用户名不能为空", // 错误信息
  "data": null                    // 错误时data为null
}
```

### 常见错误码

| 错误码 | 说明 | HTTP状态码 |
|--------|------|------------|
| 0 | 成功 | 200 |
| 400 | 参数错误 | 400 |
| 401 | 未认证/认证失效 | 401 |
| 403 | 无权限访问 | 403 |
| 404 | 资源不存在 | 404 |
| 409 | 资源冲突 | 409 |
| 422 | 参数校验失败 | 422 |
| 429 | 请求过于频繁 | 429 |
| 500 | 服务器内部错误 | 500 |
| 503 | 服务不可用 | 503 |

---

## SDK使用示例

### JavaScript SDK

```javascript
// 初始化API客户端
const apiClient = new GZangAPI({
  baseURL: 'https://api.gzang.com/v1',
  timeout: 10000
});

// 设置认证令牌
apiClient.setToken(localStorage.getItem('token'));

// 用户登录
const login = async (username, password) => {
  try {
    const response = await apiClient.post('/auth/login', {
      username,
      password
    });
    return response.data;
  } catch (error) {
    console.error('登录失败:', error);
    throw error;
  }
};

// 获取交易记录
const getTransactions = async (params = {}) => {
  try {
    const response = await apiClient.get('/accounting/transactions', {
      params
    });
    return response.data;
  } catch (error) {
    console.error('获取交易记录失败:', error);
    throw error;
  }
};

// 新增交易
const createTransaction = async (transaction) => {
  try {
    const response = await apiClient.post('/accounting/transaction', transaction);
    return response.data;
  } catch (error) {
    console.error('新增交易失败:', error);
    throw error;
  }
};
```

### 移动端SDK (React Native)

```typescript
import GZangAPI from 'gzang-api-react-native';

const api = new GZangAPI({
  baseURL: 'https://api.gzang.com/v1'
});

// 设置令牌
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
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  });
};
```

---

## 版本历史

| 版本 | 更新日期 | 更新内容 |
|------|----------|----------|
| 1.0.0 | 2024-01-14 | 初始版本发布 |
| 1.1.0 | 计划中 | 新增OCR识别接口 |
| 1.2.0 | 计划中 | 新增预算提醒接口 |
| 2.0.0 | 计划中 | 支持企业ERP集成 |

---

## 支持与反馈

如有API使用问题或建议，请联系：

- **技术支持邮箱**: support@gzang.com
- **开发者论坛**: https://forum.gzang.com
- **GitHub Issues**: https://github.com/gzang/api/issues

---

**文档版本**: 1.0.0
**最后更新**: 2026-01-14
**维护人员**: API开发团队
