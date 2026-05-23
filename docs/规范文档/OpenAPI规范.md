# G-Zang OpenAPI 规范

> **文档版本**：1.2.0
> **最后更新**：2026-05-22
> **维护人员**：后端架构师 / API 开发团队
> **关联规则**：`technical-writing.mdc`

---

此文件为 OpenAPI 3.0 YAML 规范的核心引用文档。

完整的 `openapi.yaml` 文件位于 `docs/规范文档/openapi.yaml`，包含：

- 所有 API 接口的完整定义
- 请求/响应模式（Schema）
- 认证和安全配置
- 错误码定义
- 使用示例

### 快速引用

| 模块 | 路径前缀 | 描述 |
|------|---------|------|
| 认证 | `/auth` | 登录、注册、Token 刷新 |
| 用户 | `/user` | 个人信息管理 |
| 账户 | `/account` | 账户 CRUD |
| 分类 | `/category` | 分类管理 |
| 交易 | `/accounting` | 交易记录管理 |
| 报表 | `/report` | 收支汇总、趋势分析、分类统计 |
| **预算** | `/budget` | 预算管理、预警 |
| **移动端** | `/mobile` | 设备管理、语音记账、OCR 识别 |
| 企业 | `/company` | 公司管理、员工邀请 |
| 系统 | `/system` | 系统配置 |

### 新增 API 端点

#### 预算管理 (`/budget`)
- `GET /budget/budgets` - 获取预算列表
- `GET /budget/budgets/{id}` - 获取预算详情
- `POST /budget/budgets` - 创建预算
- `PUT /budget/budgets/{id}` - 更新预算
- `DELETE /budget/budgets/{id}` - 删除预算
- `GET /budget/budgets/warnings` - 获取预警预算列表
- `POST /budget/budgets/{id}/refresh-used` - 刷新已用金额

#### 移动端专用 (`/mobile`)
- `POST /mobile/device/register` - 注册设备
- `POST /mobile/voice/intent` - 语音意图识别
- `POST /mobile/ocr/receipt` - 小票识别

### 规范链接

- **完整规范**：`openapi.yaml`
- **API 使用文档**：`../技术文档/API 规范.md`
- **错误码参考**：`../技术文档/API 规范.md#3.4-错误码定义`

---

**文档版本**：1.2.0
**最后更新**：2026-05-22
**维护人员**：后端架构师 / API 开发团队
