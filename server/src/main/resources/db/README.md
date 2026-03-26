# G-Zang 数据库初始化说明

## 文件说明

### 1. schema.sql
数据库基础表结构和基础数据初始化脚本，包含：
- 所有表结构定义
- 基础角色数据（超级管理员、管理员、财务、普通用户）
- 基础权限数据
- 角色权限关联
- 系统预设分类

### 2. init_real_accounts.sql
真实账号数据初始化脚本，包含：
- 测试公司数据
- 真实用户账号（超级管理员、管理员、财务、普通用户）
- 用户账户数据
- 示例交易记录

## 账号信息

| 账号邮箱 | 密码 | 角色 | 权限说明 |
|---------|------|------|----------|
| admin@gzang.com | 123456 | 超级管理员 | 所有权限 |
| manager@gzang.com | 123456 | 管理员 | 用户管理、公司管理、记账相关权限 |
| finance@gzang.com | 123456 | 财务 | 记账管理、报表查看权限 |
| user@gzang.com | 123456 | 普通用户 | 基本记账功能 |

## 执行顺序

1. **首次初始化**：执行 `schema.sql`
2. **导入账号数据**：执行 `init_real_accounts.sql`

## 执行方法

### 方法1：命令行执行
```bash
# 连接到MySQL数据库
mysql -u username -p database_name

# 执行schema.sql
mysql -u username -p database_name < schema.sql

# 执行账号初始化
mysql -u username -p database_name < init_real_accounts.sql
```

### 方法2：MySQL Workbench或其他GUI工具
1. 打开MySQL Workbench
2. 连接到目标数据库
3. 执行SQL文件

## 注意事项

1. **密码加密**：当前使用bcrypt加密，密码均为"123456"
2. **生产环境**：请修改默认密码，使用更强的密码策略
3. **数据重复**：脚本使用`ON DUPLICATE KEY UPDATE`避免重复插入
4. **依赖关系**：确保按正确的顺序执行脚本

## 验证初始化结果

执行完 `init_real_accounts.sql` 后，可以看到以下统计信息：
- 用户账号数量
- 公司数量
- 账户数量
- 交易记录数量



