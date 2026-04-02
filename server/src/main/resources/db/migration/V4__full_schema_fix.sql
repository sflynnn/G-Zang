-- ============================================================
-- G-Zang 完整修复脚本
-- 日期：2026-03-28
-- 描述：修复所有表结构与实体类不匹配的问题
-- 执行前请备份数据库！
-- ============================================================

USE g_zang;

-- ============================================================
-- 1. 修复 t_company 表
-- ============================================================
SET @exist := (SELECT COUNT(*) FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 't_company' AND COLUMN_NAME = 'company_code');
SET @sqlstmt := IF(@exist = 0,
    'ALTER TABLE t_company ADD COLUMN company_code VARCHAR(64) DEFAULT NULL COMMENT ''公司编码'' AFTER company_name',
    'SELECT ''company_code already exists'' as result');
PREPARE stmt FROM @sqlstmt; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @exist := (SELECT COUNT(*) FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 't_company' AND COLUMN_NAME = 'contact_name');
SET @sqlstmt := IF(@exist = 0,
    'ALTER TABLE t_company ADD COLUMN contact_name VARCHAR(64) DEFAULT NULL COMMENT ''联系人姓名'' AFTER company_code',
    'SELECT ''contact_name already exists'' as result');
PREPARE stmt FROM @sqlstmt; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @exist := (SELECT COUNT(*) FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 't_company' AND COLUMN_NAME = 'contact_phone');
SET @sqlstmt := IF(@exist = 0,
    'ALTER TABLE t_company ADD COLUMN contact_phone VARCHAR(32) DEFAULT NULL COMMENT ''联系人电话'' AFTER contact_name',
    'SELECT ''contact_phone already exists'' as result');
PREPARE stmt FROM @sqlstmt; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @exist := (SELECT COUNT(*) FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 't_company' AND COLUMN_NAME = 'contact_email');
SET @sqlstmt := IF(@exist = 0,
    'ALTER TABLE t_company ADD COLUMN contact_email VARCHAR(128) DEFAULT NULL COMMENT ''联系人邮箱'' AFTER contact_phone',
    'SELECT ''contact_email already exists'' as result');
PREPARE stmt FROM @sqlstmt; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @exist := (SELECT COUNT(*) FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 't_company' AND COLUMN_NAME = 'address');
SET @sqlstmt := IF(@exist = 0,
    'ALTER TABLE t_company ADD COLUMN address VARCHAR(255) DEFAULT NULL COMMENT ''公司地址'' AFTER contact_email',
    'SELECT ''address already exists'' as result');
PREPARE stmt FROM @sqlstmt; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @exist := (SELECT COUNT(*) FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 't_company' AND COLUMN_NAME = 'status');
SET @sqlstmt := IF(@exist = 0,
    'ALTER TABLE t_company ADD COLUMN status TINYINT NOT NULL DEFAULT 1 COMMENT ''状态：0=禁用，1=正常'' AFTER address',
    'SELECT ''status already exists'' as result');
PREPARE stmt FROM @sqlstmt; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @exist := (SELECT COUNT(*) FROM information_schema.STATISTICS
    WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 't_company' AND INDEX_NAME = 'idx_company_code');
SET @sqlstmt := IF(@exist = 0, 'ALTER TABLE t_company ADD INDEX idx_company_code (company_code)', 'SELECT ''done'' as result');
PREPARE stmt FROM @sqlstmt; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- ============================================================
-- 2. 修复 t_permission 表
-- ============================================================
SET @exist := (SELECT COUNT(*) FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 't_permission' AND COLUMN_NAME = 'permission_group');
SET @sqlstmt := IF(@exist = 0,
    'ALTER TABLE t_permission
        ADD COLUMN permission_group VARCHAR(32) DEFAULT ''SYSTEM'' COMMENT ''权限分组：SYSTEM/ORGANIZATION/FINANCE/BUSINESS'' AFTER permission_code,
        ADD COLUMN permission_level TINYINT DEFAULT 2 COMMENT ''权限级别：1=系统级, 2=企业级, 3=个人级'' AFTER permission_group,
        ADD COLUMN sort_order INT DEFAULT 0 COMMENT ''排序号'' AFTER permission_level,
        ADD COLUMN description VARCHAR(255) DEFAULT NULL COMMENT ''权限描述'' AFTER sort_order',
    'SELECT ''t_permission columns already exist'' as result');
PREPARE stmt FROM @sqlstmt; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- 添加索引
SET @exist := (SELECT COUNT(*) FROM information_schema.STATISTICS
    WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 't_permission' AND INDEX_NAME = 'idx_permission_group');
SET @sqlstmt := IF(@exist = 0, 'ALTER TABLE t_permission ADD INDEX idx_permission_group (permission_group)', 'SELECT ''done'' as result');
PREPARE stmt FROM @sqlstmt; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- ============================================================
-- 3. 修复 t_role 表
-- ============================================================
SET @exist := (SELECT COUNT(*) FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 't_role' AND COLUMN_NAME = 'role_type');
SET @sqlstmt := IF(@exist = 0,
    'ALTER TABLE t_role ADD COLUMN role_type TINYINT NOT NULL DEFAULT 1 COMMENT ''角色类型：1=系统内置, 2=企业自定义'' AFTER description',
    'SELECT ''role_type already exists'' as result');
PREPARE stmt FROM @sqlstmt; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @exist := (SELECT COUNT(*) FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 't_role' AND COLUMN_NAME = 'data_scope');
SET @sqlstmt := IF(@exist = 0,
    'ALTER TABLE t_role ADD COLUMN data_scope VARCHAR(32) NOT NULL DEFAULT ''OWN'' COMMENT ''数据范围：OWN/DEPARTMENT/COMPANY/ALL'' AFTER role_type',
    'SELECT ''data_scope already exists'' as result');
PREPARE stmt FROM @sqlstmt; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @exist := (SELECT COUNT(*) FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 't_role' AND COLUMN_NAME = 'company_id');
SET @sqlstmt := IF(@exist = 0,
    'ALTER TABLE t_role ADD COLUMN company_id BIGINT DEFAULT NULL COMMENT ''自定义角色所属公司'' AFTER data_scope',
    'SELECT ''company_id already exists'' as result');
PREPARE stmt FROM @sqlstmt; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @exist := (SELECT COUNT(*) FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 't_role' AND COLUMN_NAME = 'is_default');
SET @sqlstmt := IF(@exist = 0,
    'ALTER TABLE t_role ADD COLUMN is_default TINYINT NOT NULL DEFAULT 0 COMMENT ''是否为默认角色'' AFTER company_id',
    'SELECT ''is_default already exists'' as result');
PREPARE stmt FROM @sqlstmt; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @exist := (SELECT COUNT(*) FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 't_role' AND COLUMN_NAME = 'create_time');
SET @sqlstmt := IF(@exist = 0,
    'ALTER TABLE t_role ADD COLUMN create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT ''创建时间'' AFTER is_default',
    'SELECT ''create_time already exists'' as result');
PREPARE stmt FROM @sqlstmt; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @exist := (SELECT COUNT(*) FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 't_role' AND COLUMN_NAME = 'update_time');
SET @sqlstmt := IF(@exist = 0,
    'ALTER TABLE t_role ADD COLUMN update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT ''更新时间'' AFTER create_time',
    'SELECT ''update_time already exists'' as result');
PREPARE stmt FROM @sqlstmt; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- 更新内置角色的 data_scope
UPDATE t_role SET data_scope = 'ALL' WHERE role_code = 'SUPER_ADMIN';
UPDATE t_role SET data_scope = 'COMPANY' WHERE role_code = 'ADMIN';
UPDATE t_role SET data_scope = 'COMPANY' WHERE role_code = 'FINANCE';
UPDATE t_role SET data_scope = 'OWN' WHERE role_code = 'USER';

-- ============================================================
-- 4. 创建 t_user_role 表（如不存在）
-- ============================================================
CREATE TABLE IF NOT EXISTS t_user_role (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_user_role (user_id, role_id),
    INDEX idx_user_role_user_id (user_id),
    INDEX idx_user_role_role_id (role_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 迁移现有用户角色关系
INSERT IGNORE INTO t_user_role (user_id, role_id)
SELECT id, role_id FROM t_user WHERE role_id IS NOT NULL;

-- ============================================================
-- 5. 创建 t_role_data_scope 表（如不存在）
-- ============================================================
CREATE TABLE IF NOT EXISTS t_role_data_scope (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    role_id BIGINT NOT NULL,
    scope_type VARCHAR(32) NOT NULL,
    scope_value VARCHAR(128) NOT NULL,
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_rds_role_id (role_id),
    INDEX idx_rds_scope_type (scope_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- 6. 创建 t_field_permission 表（如不存在）
-- ============================================================
CREATE TABLE IF NOT EXISTS t_field_permission (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    role_id BIGINT NOT NULL,
    entity_type VARCHAR(64) NOT NULL,
    field_name VARCHAR(64) NOT NULL,
    field_visible TINYINT NOT NULL DEFAULT 1,
    field_editable TINYINT NOT NULL DEFAULT 1,
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_fp_role_id (role_id),
    INDEX idx_fp_entity_type (entity_type),
    UNIQUE KEY uk_role_entity_field (role_id, entity_type, field_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- 7. 创建 t_operation_log 表（如不存在）
-- ============================================================
CREATE TABLE IF NOT EXISTS t_operation_log (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    username VARCHAR(64) NOT NULL,
    company_id BIGINT DEFAULT NULL,
    action VARCHAR(64) NOT NULL,
    target_type VARCHAR(64) NOT NULL,
    target_id VARCHAR(128) DEFAULT NULL,
    target_name VARCHAR(255) DEFAULT NULL,
    detail TEXT DEFAULT NULL,
    ip_address VARCHAR(64) DEFAULT NULL,
    user_agent VARCHAR(512) DEFAULT NULL,
    request_uri VARCHAR(255) DEFAULT NULL,
    request_method VARCHAR(16) DEFAULT NULL,
    request_params TEXT DEFAULT NULL,
    response_status INT DEFAULT NULL,
    error_message TEXT DEFAULT NULL,
    duration_ms INT DEFAULT NULL,
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_ol_user_id (user_id),
    INDEX idx_ol_company_id (company_id),
    INDEX idx_ol_target_type (target_type),
    INDEX idx_ol_action (action),
    INDEX idx_ol_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- 8. 补充权限数据
-- ============================================================
INSERT INTO t_permission (permission_name, permission_code, permission_group, permission_level, sort_order, description) VALUES
('系统配置', 'SYSTEM_CONFIG', 'SYSTEM', 1, 10, '系统配置管理权限'),
('审计日志查看', 'SYSTEM_AUDIT', 'SYSTEM', 1, 11, '查看系统操作审计日志'),
('用户管理', 'USER_MANAGE', 'ORGANIZATION', 2, 20, '企业用户管理'),
('添加用户', 'USER_ADD', 'ORGANIZATION', 2, 21, '添加企业用户'),
('查看用户', 'USER_VIEW', 'ORGANIZATION', 2, 22, '查看用户列表'),
('编辑用户', 'USER_EDIT', 'ORGANIZATION', 2, 23, '编辑用户信息'),
('删除用户', 'USER_DELETE', 'ORGANIZATION', 2, 24, '删除企业用户'),
('公司管理', 'COMPANY_MANAGE', 'ORGANIZATION', 2, 25, '企业管理'),
('角色管理', 'ROLE_MANAGE', 'ORGANIZATION', 2, 26, '企业角色管理'),
('添加角色', 'ROLE_ADD', 'ORGANIZATION', 2, 27, '创建自定义角色'),
('查看角色', 'ROLE_VIEW', 'ORGANIZATION', 2, 28, '查看角色列表'),
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
('账户管理', 'ACCOUNT_MANAGE', 'FINANCE', 2, 54, '账户综合管理'),
('添加分类', 'CATEGORY_ADD', 'FINANCE', 2, 60, '添加分类'),
('查看分类', 'CATEGORY_VIEW', 'FINANCE', 2, 61, '查看分类'),
('编辑分类', 'CATEGORY_EDIT', 'FINANCE', 2, 62, '编辑分类'),
('删除分类', 'CATEGORY_DELETE', 'FINANCE', 2, 63, '删除分类'),
('分类管理', 'CATEGORY_MANAGE', 'FINANCE', 2, 64, '分类综合管理'),
('查看报表', 'REPORT_VIEW', 'FINANCE', 2, 70, '查看报表'),
('导出报表', 'REPORT_EXPORT', 'FINANCE', 2, 71, '导出报表数据'),
('成本中心管理', 'COST_CENTER_MANAGE', 'BUSINESS', 2, 80, '成本中心管理'),
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

-- ============================================================
-- 9. 修复旧权限数据的分组信息
-- ============================================================
UPDATE t_permission SET permission_group = 'ORGANIZATION', sort_order = 20 WHERE permission_code = 'USER_MANAGE' AND permission_group IS NULL;
UPDATE t_permission SET permission_group = 'ORGANIZATION', sort_order = 25 WHERE permission_code = 'COMPANY_MANAGE' AND permission_group IS NULL;
UPDATE t_permission SET permission_group = 'ORGANIZATION', sort_order = 26 WHERE permission_code = 'ROLE_MANAGE' AND permission_group IS NULL;
UPDATE t_permission SET permission_group = 'FINANCE', sort_order = 40 WHERE permission_code = 'TRANSACTION_ADD' AND permission_group IS NULL;
UPDATE t_permission SET permission_group = 'FINANCE', sort_order = 41 WHERE permission_code = 'TRANSACTION_VIEW' AND permission_group IS NULL;
UPDATE t_permission SET permission_group = 'FINANCE', sort_order = 42 WHERE permission_code = 'TRANSACTION_EDIT' AND permission_group IS NULL;
UPDATE t_permission SET permission_group = 'FINANCE', sort_order = 43 WHERE permission_code = 'TRANSACTION_DELETE' AND permission_group IS NULL;
UPDATE t_permission SET permission_group = 'FINANCE', sort_order = 50 WHERE permission_code = 'ACCOUNT_MANAGE' AND permission_group IS NULL;
UPDATE t_permission SET permission_group = 'FINANCE', sort_order = 60 WHERE permission_code = 'CATEGORY_MANAGE' AND permission_group IS NULL;
UPDATE t_permission SET permission_group = 'FINANCE', sort_order = 70 WHERE permission_code = 'REPORT_VIEW' AND permission_group IS NULL;

-- ============================================================
-- 10. 为 SUPER_ADMIN 补充所有权限
-- ============================================================
INSERT IGNORE INTO t_role_permission (role_id, permission_id)
SELECT r.id, p.id FROM t_role r, t_permission p
WHERE r.role_code = 'SUPER_ADMIN';

SELECT '修复完成！请重启后端服务。' as status;
