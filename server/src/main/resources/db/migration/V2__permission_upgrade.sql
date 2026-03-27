-- ============================================================
-- G-Zang 权限体系升级脚本
-- 版本：V2
-- 日期：2026-03-27
-- 描述：扩展角色表、新增用户角色关联表、角色数据范围表、
--       字段权限表（预留）、操作日志表
-- 兼容：MySQL 8.0
-- ============================================================

USE g_zang;

-- --------------------------------------------------
-- 1. 扩展 t_role 表
--    注意：MySQL 不支持 IF NOT EXISTS，需手动判断
-- --------------------------------------------------
SET @exist := (SELECT COUNT(*) FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 't_role' AND COLUMN_NAME = 'role_type');
SET @sql := IF(@exist = 0, 'ALTER TABLE t_role ADD COLUMN role_type TINYINT NOT NULL DEFAULT 1 COMMENT ''角色类型：1=系统内置, 2=企业自定义'' AFTER description', 'SELECT ''role_type column already exists'' as result');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @exist := (SELECT COUNT(*) FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 't_role' AND COLUMN_NAME = 'data_scope');
SET @sql := IF(@exist = 0, 'ALTER TABLE t_role ADD COLUMN data_scope VARCHAR(32) NOT NULL DEFAULT ''OWN'' COMMENT ''数据范围：OWN/DEPARTMENT/COMPANY/ALL'' AFTER role_type', 'SELECT ''data_scope column already exists'' as result');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @exist := (SELECT COUNT(*) FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 't_role' AND COLUMN_NAME = 'company_id');
SET @sql := IF(@exist = 0, 'ALTER TABLE t_role ADD COLUMN company_id BIGINT NULL COMMENT ''自定义角色所属公司'' AFTER data_scope', 'SELECT ''company_id column already exists'' as result');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @exist := (SELECT COUNT(*) FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 't_role' AND COLUMN_NAME = 'is_default');
SET @sql := IF(@exist = 0, 'ALTER TABLE t_role ADD COLUMN is_default TINYINT NOT NULL DEFAULT 0 COMMENT ''是否为默认角色（预留）'' AFTER company_id', 'SELECT ''is_default column already exists'' as result');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- 添加索引（忽略已存在报错）
-- role_type 索引
SET @exist := (SELECT COUNT(*) FROM information_schema.STATISTICS
    WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 't_role' AND INDEX_NAME = 'idx_role_type');
SET @sql := IF(@exist = 0, 'ALTER TABLE t_role ADD INDEX idx_role_type (role_type)', 'SELECT ''idx_role_type already exists'' as result');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- data_scope 索引
SET @exist := (SELECT COUNT(*) FROM information_schema.STATISTICS
    WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 't_role' AND INDEX_NAME = 'idx_data_scope');
SET @sql := IF(@exist = 0, 'ALTER TABLE t_role ADD INDEX idx_data_scope (data_scope)', 'SELECT ''idx_data_scope already exists'' as result');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- company_id 索引
SET @exist := (SELECT COUNT(*) FROM information_schema.STATISTICS
    WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 't_role' AND INDEX_NAME = 'idx_role_company_id');
SET @sql := IF(@exist = 0, 'ALTER TABLE t_role ADD INDEX idx_role_company_id (company_id)', 'SELECT ''idx_role_company_id already exists'' as result');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- 更新现有内置角色的 data_scope
UPDATE t_role SET data_scope = 'ALL'     WHERE role_code = 'SUPER_ADMIN';
UPDATE t_role SET data_scope = 'COMPANY' WHERE role_code = 'ADMIN';
UPDATE t_role SET data_scope = 'COMPANY' WHERE role_code = 'FINANCE';
UPDATE t_role SET data_scope = 'OWN'     WHERE role_code = 'USER';

-- --------------------------------------------------
-- 2. 新增用户-角色关联表（多角色预留）
-- --------------------------------------------------
CREATE TABLE IF NOT EXISTS t_user_role (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    role_id BIGINT NOT NULL COMMENT '角色ID',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY uk_user_role (user_id, role_id),
    INDEX idx_user_role_user_id (user_id),
    INDEX idx_user_role_role_id (role_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户角色关联表（多角色预留）';

-- 迁移现有用户的单角色数据到关联表（忽略已存在的记录）
INSERT IGNORE INTO t_user_role (user_id, role_id)
SELECT id, role_id FROM t_user WHERE role_id IS NOT NULL;

-- --------------------------------------------------
-- 3. 新增角色数据范围关联表（行级权限预留）
-- --------------------------------------------------
CREATE TABLE IF NOT EXISTS t_role_data_scope (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
    role_id BIGINT NOT NULL COMMENT '角色ID',
    scope_type VARCHAR(32) NOT NULL COMMENT '范围类型：DEPARTMENT/COST_CENTER/AREA',
    scope_value VARCHAR(128) NOT NULL COMMENT '范围值：如部门ID、成本中心ID',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_rds_role_id (role_id),
    INDEX idx_rds_scope_type (scope_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色数据范围关联表（行级权限预留）';

-- --------------------------------------------------
-- 4. 新增字段权限表（预留扩展）
-- --------------------------------------------------
CREATE TABLE IF NOT EXISTS t_field_permission (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
    role_id BIGINT NOT NULL COMMENT '角色ID',
    entity_type VARCHAR(64) NOT NULL COMMENT '实体类型：如Transaction, Account',
    field_name VARCHAR(64) NOT NULL COMMENT '字段名',
    field_visible TINYINT NOT NULL DEFAULT 1 COMMENT '是否可见：0=不可见, 1=可见',
    field_editable TINYINT NOT NULL DEFAULT 1 COMMENT '是否可编辑：0=只读, 1=可编辑',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_fp_role_id (role_id),
    INDEX idx_fp_entity_type (entity_type),
    UNIQUE KEY uk_role_entity_field (role_id, entity_type, field_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='字段权限表（预留扩展）';

-- --------------------------------------------------
-- 5. 新增操作日志表
-- --------------------------------------------------
CREATE TABLE IF NOT EXISTS t_operation_log (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
    user_id BIGINT NOT NULL COMMENT '操作用户ID',
    username VARCHAR(64) NOT NULL COMMENT '操作用户名',
    company_id BIGINT NULL COMMENT '所属公司ID',
    action VARCHAR(64) NOT NULL COMMENT '操作类型：CREATE/UPDATE/DELETE/VIEW/EXPORT/LOGIN/LOGOUT/PERMISSION_CHANGE',
    target_type VARCHAR(64) NOT NULL COMMENT '操作对象类型：如Transaction, Account',
    target_id VARCHAR(128) NULL COMMENT '操作对象ID',
    target_name VARCHAR(255) NULL COMMENT '操作对象名称',
    detail TEXT NULL COMMENT '变更详情（JSON格式）',
    ip_address VARCHAR(64) NULL COMMENT 'IP地址',
    user_agent VARCHAR(512) NULL COMMENT 'User-Agent',
    request_uri VARCHAR(255) NULL COMMENT '请求URI',
    request_method VARCHAR(16) NULL COMMENT '请求方法',
    request_params TEXT NULL COMMENT '请求参数（脱敏后）',
    response_status INT NULL COMMENT '响应状态码',
    error_message TEXT NULL COMMENT '错误信息（失败时）',
    duration_ms INT NULL COMMENT '请求耗时（毫秒）',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
    INDEX idx_ol_user_id (user_id),
    INDEX idx_ol_company_id (company_id),
    INDEX idx_ol_target_type (target_type),
    INDEX idx_ol_action (action),
    INDEX idx_ol_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='操作日志表';

-- --------------------------------------------------
-- 6. 扩展 t_permission 表（新增分组字段）
-- --------------------------------------------------
SET @exist := (SELECT COUNT(*) FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 't_permission' AND COLUMN_NAME = 'permission_group');
SET @sql := IF(@exist = 0,
    'ALTER TABLE t_permission
        ADD COLUMN permission_group VARCHAR(32) NULL COMMENT ''权限分组：SYSTEM/ORGANIZATION/FINANCE/BUSINESS'' AFTER permission_code,
        ADD COLUMN permission_level TINYINT NOT NULL DEFAULT 2 COMMENT ''权限级别：1=系统级, 2=企业级, 3=个人级'' AFTER permission_group,
        ADD COLUMN sort_order INT NOT NULL DEFAULT 0 COMMENT ''排序号'' AFTER permission_level,
        ADD COLUMN description VARCHAR(255) NULL COMMENT ''权限描述'' AFTER sort_order',
    'SELECT ''permission_group column already exists'' as result');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @exist := (SELECT COUNT(*) FROM information_schema.STATISTICS
    WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 't_permission' AND INDEX_NAME = 'idx_permission_group');
SET @sql := IF(@exist = 0, 'ALTER TABLE t_permission ADD INDEX idx_permission_group (permission_group)', 'SELECT ''idx_permission_group already exists'' as result');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- 更新现有权限的分组和级别
UPDATE t_permission SET permission_group = 'SYSTEM',      permission_level = 1, sort_order = 10 WHERE permission_code = 'SYSTEM_CONFIG';
UPDATE t_permission SET permission_group = 'SYSTEM',      permission_level = 1, sort_order = 11, description = '查看系统操作审计日志' WHERE permission_code = 'SYSTEM_AUDIT';
UPDATE t_permission SET permission_group = 'ORGANIZATION', permission_level = 2, sort_order = 20, description = '企业用户管理（含增删改查）' WHERE permission_code = 'USER_MANAGE';
UPDATE t_permission SET permission_group = 'ORGANIZATION', permission_level = 2, sort_order = 21, description = '添加企业用户' WHERE permission_code = 'USER_ADD';
UPDATE t_permission SET permission_group = 'ORGANIZATION', permission_level = 2, sort_order = 22, description = '查看用户列表和详情' WHERE permission_code = 'USER_VIEW';
UPDATE t_permission SET permission_group = 'ORGANIZATION', permission_level = 2, sort_order = 23, description = '编辑用户信息' WHERE permission_code = 'USER_EDIT';
UPDATE t_permission SET permission_group = 'ORGANIZATION', permission_level = 2, sort_order = 24, description = '删除企业用户' WHERE permission_code = 'USER_DELETE';
UPDATE t_permission SET permission_group = 'ORGANIZATION', permission_level = 2, sort_order = 25, description = '企业管理（公司信息配置）' WHERE permission_code = 'COMPANY_MANAGE';
UPDATE t_permission SET permission_group = 'ORGANIZATION', permission_level = 2, sort_order = 26, description = '企业角色管理（含增删改查）' WHERE permission_code = 'ROLE_MANAGE';
UPDATE t_permission SET permission_group = 'ORGANIZATION', permission_level = 2, sort_order = 27, description = '创建自定义角色' WHERE permission_code = 'ROLE_ADD';
UPDATE t_permission SET permission_group = 'ORGANIZATION', permission_level = 2, sort_order = 28, description = '查看角色列表和详情' WHERE permission_code = 'ROLE_VIEW';
UPDATE t_permission SET permission_group = 'ORGANIZATION', permission_level = 2, sort_order = 29, description = '编辑角色信息' WHERE permission_code = 'ROLE_EDIT';
UPDATE t_permission SET permission_group = 'ORGANIZATION', permission_level = 2, sort_order = 30, description = '删除自定义角色' WHERE permission_code = 'ROLE_DELETE';
UPDATE t_permission SET permission_group = 'FINANCE',      permission_level = 2, sort_order = 40, description = '录入交易记录' WHERE permission_code = 'TRANSACTION_ADD';
UPDATE t_permission SET permission_group = 'FINANCE',      permission_level = 2, sort_order = 41, description = '查看交易记录' WHERE permission_code = 'TRANSACTION_VIEW';
UPDATE t_permission SET permission_group = 'FINANCE',      permission_level = 2, sort_order = 42, description = '编辑交易记录' WHERE permission_code = 'TRANSACTION_EDIT';
UPDATE t_permission SET permission_group = 'FINANCE',      permission_level = 2, sort_order = 43, description = '删除交易记录' WHERE permission_code = 'TRANSACTION_DELETE';
UPDATE t_permission SET permission_group = 'FINANCE',      permission_level = 2, sort_order = 50, description = '添加账户' WHERE permission_code = 'ACCOUNT_ADD';
UPDATE t_permission SET permission_group = 'FINANCE',      permission_level = 2, sort_order = 51, description = '查看账户' WHERE permission_code = 'ACCOUNT_VIEW';
UPDATE t_permission SET permission_group = 'FINANCE',      permission_level = 2, sort_order = 52, description = '编辑账户' WHERE permission_code = 'ACCOUNT_EDIT';
UPDATE t_permission SET permission_group = 'FINANCE',      permission_level = 2, sort_order = 53, description = '删除账户' WHERE permission_code = 'ACCOUNT_DELETE';
UPDATE t_permission SET permission_group = 'FINANCE',      permission_level = 2, sort_order = 60, description = '添加分类' WHERE permission_code = 'CATEGORY_ADD';
UPDATE t_permission SET permission_group = 'FINANCE',      permission_level = 2, sort_order = 61, description = '查看分类' WHERE permission_code = 'CATEGORY_VIEW';
UPDATE t_permission SET permission_group = 'FINANCE',      permission_level = 2, sort_order = 62, description = '编辑分类' WHERE permission_code = 'CATEGORY_EDIT';
UPDATE t_permission SET permission_group = 'FINANCE',      permission_level = 2, sort_order = 63, description = '删除分类' WHERE permission_code = 'CATEGORY_DELETE';
UPDATE t_permission SET permission_group = 'FINANCE',      permission_level = 2, sort_order = 70, description = '查看报表' WHERE permission_code = 'REPORT_VIEW';
UPDATE t_permission SET permission_group = 'FINANCE',      permission_level = 2, sort_order = 80, description = '成本中心增删改查' WHERE permission_code = 'COST_CENTER_MANAGE';
UPDATE t_permission SET permission_group = 'BUSINESS',    permission_level = 2, sort_order = 90, description = '创建业务订单' WHERE permission_code = 'BUSINESS_ORDER_ADD';
UPDATE t_permission SET permission_group = 'BUSINESS',    permission_level = 2, sort_order = 91, description = '查看业务订单' WHERE permission_code = 'BUSINESS_ORDER_VIEW';
UPDATE t_permission SET permission_group = 'BUSINESS',    permission_level = 2, sort_order = 92, description = '编辑业务订单' WHERE permission_code = 'BUSINESS_ORDER_EDIT';
UPDATE t_permission SET permission_group = 'BUSINESS',    permission_level = 2, sort_order = 93, description = '删除业务订单' WHERE permission_code = 'BUSINESS_ORDER_DELETE';
UPDATE t_permission SET permission_group = 'BUSINESS',    permission_level = 2, sort_order = 94, description = '审批业务订单' WHERE permission_code = 'BUSINESS_ORDER_APPROVE';

-- --------------------------------------------------
-- 7. 补充缺失的权限数据
-- --------------------------------------------------
INSERT INTO t_permission (permission_name, permission_code, permission_group, permission_level, sort_order, description) VALUES
('系统配置', 'SYSTEM_CONFIG', 'SYSTEM', 1, 10, '系统配置管理权限（仅超级管理员）'),
('审计日志查看', 'SYSTEM_AUDIT', 'SYSTEM', 1, 11, '查看系统操作审计日志'),
('用户管理', 'USER_MANAGE', 'ORGANIZATION', 2, 20, '企业用户管理（含增删改查）'),
('添加用户', 'USER_ADD', 'ORGANIZATION', 2, 21, '添加企业用户'),
('查看用户', 'USER_VIEW', 'ORGANIZATION', 2, 22, '查看用户列表和详情'),
('编辑用户', 'USER_EDIT', 'ORGANIZATION', 2, 23, '编辑用户信息'),
('删除用户', 'USER_DELETE', 'ORGANIZATION', 2, 24, '删除企业用户'),
('公司管理', 'COMPANY_MANAGE', 'ORGANIZATION', 2, 25, '企业管理（公司信息配置）'),
('角色管理', 'ROLE_MANAGE', 'ORGANIZATION', 2, 26, '企业角色管理（含增删改查）'),
('添加角色', 'ROLE_ADD', 'ORGANIZATION', 2, 27, '创建自定义角色'),
('查看角色', 'ROLE_VIEW', 'ORGANIZATION', 2, 28, '查看角色列表和详情'),
('编辑角色', 'ROLE_EDIT', 'ORGANIZATION', 2, 29, '编辑角色信息'),
('删除角色', 'ROLE_DELETE', 'ORGANIZATION', 2, 30, '删除自定义角色'),
('交易录入', 'TRANSACTION_ADD', 'FINANCE', 2, 40, '录入交易记录'),
('交易查看', 'TRANSACTION_VIEW', 'FINANCE', 2, 41, '查看交易记录'),
('交易编辑', 'TRANSACTION_EDIT', 'FINANCE', 2, 42, '编辑交易记录'),
('交易删除', 'TRANSACTION_DELETE', 'FINANCE', 2, 43, '删除交易记录'),
('交易导出', 'TRANSACTION_EXPORT', 'FINANCE', 2, 44, '导出交易记录'),
('添加账户', 'ACCOUNT_ADD', 'FINANCE', 2, 50, '添加账户'),
('查看账户', 'ACCOUNT_VIEW', 'FINANCE', 2, 51, '查看账户'),
('编辑账户', 'ACCOUNT_EDIT', 'FINANCE', 2, 52, '编辑账户'),
('删除账户', 'ACCOUNT_DELETE', 'FINANCE', 2, 53, '删除账户'),
('添加分类', 'CATEGORY_ADD', 'FINANCE', 2, 60, '添加分类'),
('查看分类', 'CATEGORY_VIEW', 'FINANCE', 2, 61, '查看分类'),
('编辑分类', 'CATEGORY_EDIT', 'FINANCE', 2, 62, '编辑分类'),
('删除分类', 'CATEGORY_DELETE', 'FINANCE', 2, 63, '删除分类'),
('查看报表', 'REPORT_VIEW', 'FINANCE', 2, 70, '查看报表'),
('导出报表', 'REPORT_EXPORT', 'FINANCE', 2, 71, '导出报表数据'),
('成本中心管理', 'COST_CENTER_MANAGE', 'BUSINESS', 2, 80, '成本中心增删改查'),
('添加业务订单', 'BUSINESS_ORDER_ADD', 'BUSINESS', 2, 90, '创建业务订单'),
('查看业务订单', 'BUSINESS_ORDER_VIEW', 'BUSINESS', 2, 91, '查看业务订单'),
('编辑业务订单', 'BUSINESS_ORDER_EDIT', 'BUSINESS', 2, 92, '编辑业务订单'),
('删除业务订单', 'BUSINESS_ORDER_DELETE', 'BUSINESS', 2, 93, '删除业务订单'),
('审批业务订单', 'BUSINESS_ORDER_APPROVE', 'BUSINESS', 2, 94, '审批业务订单')
ON DUPLICATE KEY UPDATE
    permission_name = VALUES(permission_name),
    permission_group = IF(VALUES(permission_group) IS NOT NULL, VALUES(permission_group), permission_group),
    permission_level = VALUES(permission_level),
    sort_order = VALUES(sort_order),
    description = VALUES(description);

-- --------------------------------------------------
-- 8. 为 SUPER_ADMIN 补充系统级权限关联
-- --------------------------------------------------
INSERT IGNORE INTO t_role_permission (role_id, permission_id)
SELECT r.id, p.id FROM t_role r, t_permission p
WHERE r.role_code = 'SUPER_ADMIN'
  AND p.permission_code IN ('SYSTEM_CONFIG', 'SYSTEM_AUDIT', 'USER_MANAGE', 'USER_ADD', 'USER_VIEW',
      'USER_EDIT', 'USER_DELETE', 'COMPANY_MANAGE', 'ROLE_MANAGE', 'ROLE_ADD', 'ROLE_VIEW',
      'ROLE_EDIT', 'ROLE_DELETE', 'TRANSACTION_ADD', 'TRANSACTION_VIEW', 'TRANSACTION_EDIT',
      'TRANSACTION_DELETE', 'TRANSACTION_EXPORT', 'ACCOUNT_ADD', 'ACCOUNT_VIEW', 'ACCOUNT_EDIT',
      'ACCOUNT_DELETE', 'CATEGORY_ADD', 'CATEGORY_VIEW', 'CATEGORY_EDIT', 'CATEGORY_DELETE',
      'REPORT_VIEW', 'REPORT_EXPORT', 'COST_CENTER_MANAGE', 'BUSINESS_ORDER_ADD',
      'BUSINESS_ORDER_VIEW', 'BUSINESS_ORDER_EDIT', 'BUSINESS_ORDER_DELETE', 'BUSINESS_ORDER_APPROVE');
