# G-Zang 数据库设计

> **文档版本**：1.0.0
> **最后更新**：2026-03-27
> **维护人员**：数据库优化工程师 / 后端架构师
> **关联规则**：`database.mdc`、`backend.mdc`

---

## 1. 数据库概览

| 配置项 | 值 |
|--------|-----|
| 数据库类型 | MySQL 8.0+ |
| 字符集 | `utf8mb4` |
| 排序规则 | `utf8mb4_unicode_ci` |
| 存储引擎 | `InnoDB` |
| 架构 | 主从复制（读写分离） |

---

## 2. 核心表结构

### 2.1 用户与权限相关表

#### `t_user` (用户表)

| 字段名 | 类型 | 默认值 | 是否为空 | 备注 |
|--------|------|--------|---------|------|
| `id` | BIGINT | | NOT NULL | 主键，用户ID |
| `username` | VARCHAR(64) | | NOT NULL | 用户名（手机号/邮箱） |
| `password` | VARCHAR(255) | | NOT NULL | 密码（BCrypt 加密存储） |
| `nickname` | VARCHAR(64) | | NULL | 用户昵称 |
| `avatar` | VARCHAR(255) | | NULL | 用户头像 URL |
| `role_id` | BIGINT | | NOT NULL | 角色ID |
| `company_id` | BIGINT | | NULL | 所属公司ID（个人用户为 NULL） |
| `status` | TINYINT(1) | 1 | NOT NULL | 状态：0=禁用，1=正常 |
| `create_time` | DATETIME | CURRENT_TIMESTAMP | NOT NULL | 创建时间 |
| `update_time` | DATETIME | ON UPDATE | NOT NULL | 更新时间 |

> **安全提醒**：`password` 字段存储 BCrypt 哈希值，永远不要存储明文密码。

#### `t_role` (角色表)

| 字段名 | 类型 | 是否为空 | 备注 |
|--------|------|---------|------|
| `id` | BIGINT | NOT NULL | 主键，角色ID |
| `role_name` | VARCHAR(64) | NOT NULL | 角色名称（如：管理员、财务、普通用户） |
| `role_code` | VARCHAR(64) | NOT NULL | 角色编码（如：SUPER_ADMIN、ADMIN） |
| `description` | VARCHAR(255) | NULL | 角色描述 |

#### `t_permission` (权限表)

| 字段名 | 类型 | 是否为空 | 备注 |
|--------|------|---------|------|
| `id` | BIGINT | NOT NULL | 主键，权限ID |
| `permission_name` | VARCHAR(64) | NOT NULL | 权限名称（如：记账录入、报表查看） |
| `permission_code` | VARCHAR(64) | NOT NULL | 权限编码（如：TRANSACTION_ADD、REPORT_VIEW） |

#### `t_role_permission` (角色权限关联表)

| 字段名 | 类型 | 是否为空 | 备注 |
|--------|------|---------|------|
| `role_id` | BIGINT | NOT NULL | 角色ID |
| `permission_id` | BIGINT | NOT NULL | 权限ID |

### 2.2 公司与账本相关表

#### `t_company` (公司/组织表)

| 字段名 | 类型 | 是否为空 | 备注 |
|--------|------|---------|------|
| `id` | BIGINT | NOT NULL | 主键，公司ID（同时作为 tenant_id） |
| `company_name` | VARCHAR(128) | NOT NULL | 公司名称 |
| `admin_user_id` | BIGINT | NOT NULL | 公司管理员用户ID |
| `create_time` | DATETIME | NOT NULL | 创建时间 |
| `update_time` | DATETIME | NOT NULL | 更新时间 |

#### `t_account` (账户表)

| 字段名 | 类型 | 默认值 | 是否为空 | 备注 |
|--------|------|--------|---------|------|
| `id` | BIGINT | | NOT NULL | 主键，账户ID |
| `user_id` | BIGINT | | NOT NULL | 所属用户ID |
| `company_id` | BIGINT | | NULL | 所属公司ID（个人账户为 NULL） |
| `account_name` | VARCHAR(64) | | NOT NULL | 账户名称（如：现金、支付宝、招商银行） |
| `account_type` | TINYINT(1) | 1 | NOT NULL | 账户类型：1=现金，2=银行卡，3=电子支付 |
| `balance` | DECIMAL(18,2) | 0.00 | NOT NULL | 当前余额 |
| `create_time` | DATETIME | CURRENT_TIMESTAMP | NOT NULL | 创建时间 |
| `update_time` | DATETIME | ON UPDATE | NOT NULL | 更新时间 |

#### `t_category` (记账分类表)

| 字段名 | 类型 | 默认值 | 是否为空 | 备注 |
|--------|------|--------|---------|------|
| `id` | BIGINT | | NOT NULL | 主键，分类ID |
| `user_id` | BIGINT | | NULL | 所属用户ID（公司分类为 NULL） |
| `company_id` | BIGINT | | NULL | 所属公司ID（个人分类为 NULL） |
| `category_name` | VARCHAR(64) | | NOT NULL | 分类名称 |
| `parent_id` | BIGINT | 0 | NOT NULL | 父分类ID（0 为一级分类） |
| `type` | TINYINT(1) | 1 | NOT NULL | 类型：1=收入，2=支出 |
| `is_system` | TINYINT(1) | 0 | NOT NULL | 是否系统预设：0=否，1=是 |

### 2.3 交易记录表

#### `t_transaction` (交易记录表)

| 字段名 | 类型 | 默认值 | 是否为空 | 备注 |
|--------|------|--------|---------|------|
| `id` | BIGINT | | NOT NULL | 主键，交易ID |
| `user_id` | BIGINT | | NOT NULL | 记录用户ID |
| `company_id` | BIGINT | | NULL | 所属公司ID（个人交易为 NULL） |
| `amount` | DECIMAL(18,2) | 0.00 | NOT NULL | 交易金额 |
| `type` | TINYINT(1) | 1 | NOT NULL | 交易类型：1=收入，2=支出，3=转账 |
| `category_id` | BIGINT | | NOT NULL | 分类ID |
| `account_id` | BIGINT | | NOT NULL | 账户ID |
| `transaction_time` | DATETIME | CURRENT_TIMESTAMP | NOT NULL | 交易发生时间 |
| `remark` | VARCHAR(500) | | NULL | 备注 |
| `related_business_id` | VARCHAR(128) | | NULL | 关联业务ID（如维修单号） |
| `create_time` | DATETIME | CURRENT_TIMESTAMP | NOT NULL | 创建时间 |
| `update_time` | DATETIME | ON UPDATE | NOT NULL | 更新时间 |

> **性能提示**：交易表是系统中数据量最大的表，需要定期归档历史数据。

### 2.4 系统管理相关表

#### `t_system_config` (系统配置表)

| 字段名 | 类型 | 是否为空 | 备注 |
|--------|------|---------|------|
| `id` | BIGINT | NOT NULL | 主键，配置ID |
| `config_key` | VARCHAR(128) | NOT NULL | 配置键 |
| `config_value` | TEXT | NULL | 配置值 |
| `config_type` | VARCHAR(32) | NOT NULL | 配置类型 |
| `description` | VARCHAR(255) | NULL | 配置描述 |
| `is_system` | TINYINT(1) | 0 | NOT NULL | 是否系统配置：0=否，1=是 |
| `create_time` | DATETIME | NOT NULL | 创建时间 |
| `update_time` | DATETIME | NOT NULL | 更新时间 |

#### `t_operation_log` (操作日志表)

| 字段名 | 类型 | 是否为空 | 备注 |
|--------|------|---------|------|
| `id` | BIGINT | NOT NULL | 主键，日志ID |
| `user_id` | BIGINT | NULL | 操作用户ID |
| `username` | VARCHAR(64) | NULL | 操作用户名 |
| `operation_type` | VARCHAR(32) | NOT NULL | 操作类型 |
| `operation_desc` | VARCHAR(255) | NULL | 操作描述 |
| `request_method` | VARCHAR(16) | NULL | 请求方法 |
| `request_url` | VARCHAR(500) | NULL | 请求 URL |
| `request_params` | TEXT | NULL | 请求参数（脱敏后） |
| `response_code` | INT | NULL | 响应状态码 |
| `response_msg` | VARCHAR(500) | NULL | 响应消息 |
| `ip_address` | VARCHAR(64) | NULL | IP 地址 |
| `user_agent` | VARCHAR(500) | NULL | 用户代理 |
| `operation_time` | DATETIME | NOT NULL | 操作时间 |
| `duration` | BIGINT | NULL | 执行时长（毫秒） |

#### `t_file_upload` (文件上传表)

| 字段名 | 类型 | 是否为空 | 备注 |
|--------|------|---------|------|
| `id` | BIGINT | NOT NULL | 主键，文件ID |
| `file_name` | VARCHAR(255) | NOT NULL | 文件原名 |
| `file_path` | VARCHAR(500) | NOT NULL | 文件路径（存储在 OSS） |
| `file_size` | BIGINT | NOT NULL | 文件大小（字节） |
| `file_type` | VARCHAR(32) | NOT NULL | 文件类型 |
| `file_md5` | VARCHAR(64) | NULL | 文件 MD5 |
| `mime_type` | VARCHAR(128) | NULL | MIME 类型 |
| `upload_user_id` | BIGINT | NULL | 上传用户ID |
| `upload_time` | DATETIME | NOT NULL | 上传时间 |

### 2.5 预算与成本管理表

#### `t_budget` (预算表)

| 字段名 | 类型 | 默认值 | 是否为空 | 备注 |
|--------|------|--------|---------|------|
| `id` | BIGINT | | NOT NULL | 主键，预算ID |
| `user_id` | BIGINT | | NULL | 所属用户ID |
| `company_id` | BIGINT | | NULL | 所属公司ID |
| `budget_name` | VARCHAR(128) | | NOT NULL | 预算名称 |
| `budget_type` | VARCHAR(32) | | NOT NULL | 预算类型：annual / monthly / category |
| `budget_year` | INT | | NOT NULL | 预算年份 |
| `budget_month` | INT | | NULL | 预算月份 |
| `category_id` | BIGINT | | NULL | 关联分类ID |
| `budget_amount` | DECIMAL(18,2) | 0.00 | NOT NULL | 预算金额 |
| `used_amount` | DECIMAL(18,2) | 0.00 | NOT NULL | 已使用金额 |
| `warning_percent` | DECIMAL(5,2) | 80.00 | NOT NULL | 预警百分比 |
| `status` | TINYINT(1) | 1 | NOT NULL | 状态：0=禁用，1=启用 |
| `create_user_id` | BIGINT | | NOT NULL | 创建用户ID |
| `create_time` | DATETIME | CURRENT_TIMESTAMP | NOT NULL | 创建时间 |
| `update_time` | DATETIME | ON UPDATE | NOT NULL | 更新时间 |

#### `t_cost_center` (成本中心表)

| 字段名 | 类型 | 是否为空 | 备注 |
|--------|------|---------|------|
| `id` | BIGINT | NOT NULL | 主键，成本中心ID |
| `company_id` | BIGINT | NOT NULL | 所属公司ID |
| `cost_center_code` | VARCHAR(64) | NOT NULL | 成本中心编码 |
| `cost_center_name` | VARCHAR(128) | NOT NULL | 成本中心名称 |
| `parent_id` | BIGINT | NOT NULL | 父级成本中心ID（默认 0） |
| `manager_user_id` | BIGINT | NULL | 负责人用户ID |
| `budget_amount` | DECIMAL(18,2) | NOT NULL | 预算金额 |
| `description` | VARCHAR(255) | NULL | 描述 |
| `status` | TINYINT(1) | NOT NULL | 状态：0=禁用，1=启用 |
| `create_time` | DATETIME | NOT NULL | 创建时间 |
| `update_time` | DATETIME | NOT NULL | 更新时间 |

### 2.6 移动端专用表

#### `t_user_device` (用户设备表)

| 字段名 | 类型 | 是否为空 | 备注 |
|--------|------|---------|------|
| `id` | BIGINT | NOT NULL | 主键，设备ID |
| `user_id` | BIGINT | NOT NULL | 用户ID |
| `device_token` | VARCHAR(255) | NOT NULL | 设备推送令牌 |
| `device_type` | VARCHAR(32) | NOT NULL | 设备类型：iOS / Android |
| `device_model` | VARCHAR(128) | NULL | 设备型号 |
| `os_version` | VARCHAR(32) | NULL | 操作系统版本 |
| `app_version` | VARCHAR(32) | NULL | 应用版本 |
| `is_active` | TINYINT(1) | NOT NULL | 是否活跃（默认 1） |
| `last_login_time` | DATETIME | NULL | 最后登录时间 |
| `create_time` | DATETIME | NOT NULL | 创建时间 |
| `update_time` | DATETIME | NOT NULL | 更新时间 |

#### `t_offline_transaction` (离线交易记录表)

| 字段名 | 类型 | 是否为空 | 备注 |
|--------|------|---------|------|
| `id` | BIGINT | NOT NULL | 主键，离线记录ID |
| `user_id` | BIGINT | NOT NULL | 用户ID |
| `device_id` | VARCHAR(64) | NOT NULL | 设备唯一标识 |
| `transaction_data` | JSON | NOT NULL | 交易数据 JSON |
| `sync_status` | TINYINT(1) | NOT NULL | 同步状态：0=未同步，1=已同步，2=同步失败 |
| `sync_time` | DATETIME | NULL | 同步时间 |
| `sync_attempts` | INT | NOT NULL | 同步尝试次数（默认 0） |
| `create_time` | DATETIME | NOT NULL | 创建时间 |
| `update_time` | DATETIME | NOT NULL | 更新时间 |

#### `t_notification_record` (通知记录表)

| 字段名 | 类型 | 是否为空 | 备注 |
|--------|------|---------|------|
| `id` | BIGINT | NOT NULL | 主键，通知ID |
| `user_id` | BIGINT | NOT NULL | 用户ID |
| `notification_type` | VARCHAR(32) | NOT NULL | 通知类型 |
| `title` | VARCHAR(128) | NOT NULL | 通知标题 |
| `content` | TEXT | NOT NULL | 通知内容 |
| `is_read` | TINYINT(1) | NOT NULL | 是否已读（默认 0） |
| `read_time` | DATETIME | NULL | 阅读时间 |
| `send_status` | TINYINT(1) | NOT NULL | 发送状态：0=待发送，1=已发送，2=发送失败 |
| `send_time` | DATETIME | NULL | 发送时间 |
| `create_time` | DATETIME | NOT NULL | 创建时间 |

### 2.7 智能识别表

#### `t_voice_recognition_log` (语音识别日志表)

| 字段名 | 类型 | 是否为空 | 备注 |
|--------|------|---------|------|
| `id` | BIGINT | NOT NULL | 主键，识别ID |
| `user_id` | BIGINT | NOT NULL | 用户ID |
| `audio_file_id` | BIGINT | NULL | 音频文件ID |
| `recognized_text` | TEXT | NULL | 识别出的文本 |
| `parsed_amount` | DECIMAL(18,2) | NULL | 解析出的金额 |
| `parsed_category` | VARCHAR(64) | NULL | 解析出的分类 |
| `parsed_type` | TINYINT(1) | NULL | 解析出的类型：1=收入，2=支出 |
| `confidence` | DECIMAL(5,2) | NULL | 识别置信度 |
| `recognition_status` | TINYINT(1) | NOT NULL | 识别状态：0=失败，1=成功 |
| `create_time` | DATETIME | NOT NULL | 创建时间 |

#### `t_ocr_recognition_log` (OCR 识别日志表)

| 字段名 | 类型 | 是否为空 | 备注 |
|--------|------|---------|------|
| `id` | BIGINT | NOT NULL | 主键，识别ID |
| `user_id` | BIGINT | NOT NULL | 用户ID |
| `image_file_id` | BIGINT | NULL | 图片文件ID |
| `recognized_text` | TEXT | NULL | 识别出的文本 |
| `parsed_amount` | DECIMAL(18,2) | NULL | 解析出的金额 |
| `parsed_merchant` | VARCHAR(128) | NULL | 解析出的商家 |
| `parsed_items` | JSON | NULL | 解析出的商品明细 |
| `confidence` | DECIMAL(5,2) | NULL | 识别置信度 |
| `recognition_status` | TINYINT(1) | NOT NULL | 识别状态：0=失败，1=成功 |
| `create_time` | DATETIME | NOT NULL | 创建时间 |

---

## 3. 索引策略

> 详细的索引设计规范请参考 `database.mdc` 规则文件。

### 3.1 核心索引

#### 用户表索引

```sql
-- 用户名唯一索引
ALTER TABLE t_user ADD UNIQUE KEY uk_username (username);

-- 用户角色索引
ALTER TABLE t_user ADD KEY idx_role_id (role_id);

-- 用户公司索引
ALTER TABLE t_user ADD KEY idx_company_id (company_id);

-- 用户状态索引
ALTER TABLE t_user ADD KEY idx_status (status);
```

#### 交易记录表索引

```sql
-- 用户交易时间复合索引（核心查询）
ALTER TABLE t_transaction ADD KEY idx_user_transaction_time (user_id, transaction_time);

-- 公司交易时间复合索引
ALTER TABLE t_transaction ADD KEY idx_company_transaction_time (company_id, transaction_time);

-- 分类索引
ALTER TABLE t_transaction ADD KEY idx_category_id (category_id);

-- 账户索引
ALTER TABLE t_transaction ADD KEY idx_account_id (account_id);

-- 业务关联索引
ALTER TABLE t_transaction ADD KEY idx_related_business_id (related_business_id);
```

#### 操作日志表索引

```sql
-- 用户操作时间索引
ALTER TABLE t_operation_log ADD KEY idx_user_operation_time (user_id, operation_time);

-- 操作类型索引
ALTER TABLE t_operation_log ADD KEY idx_operation_type (operation_type);

-- IP 地址索引
ALTER TABLE t_operation_log ADD KEY idx_ip_address (ip_address);
```

### 3.2 性能优化索引

```sql
-- 设备推送唯一索引
ALTER TABLE t_user_device ADD UNIQUE KEY uk_user_device_token (user_id, device_token);

-- 离线数据同步索引
ALTER TABLE t_offline_transaction ADD KEY idx_sync_status_user (sync_status, user_id);

-- 通知读取索引
ALTER TABLE t_notification_record ADD KEY idx_user_read_status (user_id, is_read, create_time);
```

---

## 4. 多租户实现

### 4.1 数据隔离策略

- **用户级隔离**：个人用户数据通过 `user_id` 隔离
- **公司级隔离**：企业用户数据通过 `company_id` 隔离
- **混合隔离**：某些表同时支持用户和公司隔离

### 4.2 租户上下文管理

后端通过 `ThreadLocal` 存储当前请求的 `user_id` 和 `company_id`，MyBatis Plus 拦截器自动在 SQL 查询中添加租户条件。

```sql
-- 个人用户查询
SELECT * FROM t_transaction
WHERE user_id = #{currentUserId}
  AND company_id IS NULL;

-- 企业用户查询
SELECT * FROM t_transaction
WHERE company_id = #{currentCompanyId};
```

---

## 5. 数据初始化

详细初始化脚本和数据字典请参考：

- **初始化指南**：`../数据库初始化指南.md`
- **数据字典**：`../数据字典.md`

### 5.1 基础数据

```sql
-- 插入基础角色
INSERT INTO t_role (role_name, role_code, description) VALUES
('超级管理员', 'SUPER_ADMIN', '系统超级管理员，拥有所有权限'),
('管理员', 'ADMIN', '公司管理员，管理公司事务'),
('财务', 'FINANCE', '财务人员，负责记账和报表'),
('普通用户', 'USER', '普通用户，基本记账功能');
```

### 5.2 预设分类数据

```sql
-- 支出分类
INSERT INTO t_category (category_name, parent_id, type, is_system) VALUES
('餐饮', 0, 2, 1), ('交通', 0, 2, 1), ('购物', 0, 2, 1),
('娱乐', 0, 2, 1), ('医疗', 0, 2, 1), ('教育', 0, 2, 1),
('居家', 0, 2, 1), ('通讯', 0, 2, 1), ('其他支出', 0, 2, 1);

-- 收入分类
INSERT INTO t_category (category_name, parent_id, type, is_system) VALUES
('工资', 0, 1, 1), ('奖金', 0, 1, 1), ('投资', 0, 1, 1),
('兼职', 0, 1, 1), ('其他收入', 0, 1, 1);
```

---

**文档版本**：1.0.0
**最后更新**：2026-03-27
**维护人员**：数据库优化工程师 / 后端架构师
