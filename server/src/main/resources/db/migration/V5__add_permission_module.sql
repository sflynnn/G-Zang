-- ============================================================
-- G-Zang 权限模块字段迁移脚本
-- 日期：2026-03-28
-- 描述：为 t_permission 表添加 permission_module 字段
--       用于支持按模块分组的权限树形结构
-- ============================================================

USE g_zang;

-- 添加 permission_module 字段（如果不存在）
SET @exist := (SELECT COUNT(*) FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 't_permission' AND COLUMN_NAME = 'permission_module');
SET @sqlstmt := IF(@exist = 0,
    'ALTER TABLE t_permission ADD COLUMN permission_module VARCHAR(50) DEFAULT NULL COMMENT "权限所属模块（树形结构用）" AFTER permission_group',
    'SELECT ''permission_module column already exists'' as result');
PREPARE stmt FROM @sqlstmt;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 为现有权限填充 permission_module（基于 permission_code 的前缀约定）
UPDATE t_permission SET permission_module = 'USER'
    WHERE permission_code IN ('USER_MANAGE', 'USER_ADD', 'USER_EDIT', 'USER_DELETE', 'USER_VIEW');
UPDATE t_permission SET permission_module = 'ROLE'
    WHERE permission_code IN ('ROLE_MANAGE', 'ROLE_ADD', 'ROLE_EDIT', 'ROLE_DELETE', 'ROLE_VIEW');
UPDATE t_permission SET permission_module = 'PERMISSION'
    WHERE permission_code IN ('PERMISSION_VIEW', 'PERMISSION_ASSIGN');
UPDATE t_permission SET permission_module = 'COMPANY'
    WHERE permission_code IN ('COMPANY_MANAGE', 'COMPANY_ADD', 'COMPANY_EDIT', 'COMPANY_DELETE', 'COMPANY_VIEW');
UPDATE t_permission SET permission_module = 'ACCOUNT'
    WHERE permission_code IN ('ACCOUNT_MANAGE', 'ACCOUNT_ADD', 'ACCOUNT_EDIT', 'ACCOUNT_DELETE', 'ACCOUNT_VIEW');
UPDATE t_permission SET permission_module = 'CATEGORY'
    WHERE permission_code IN ('CATEGORY_MANAGE', 'CATEGORY_ADD', 'CATEGORY_EDIT', 'CATEGORY_DELETE', 'CATEGORY_VIEW');
UPDATE t_permission SET permission_module = 'TRANSACTION'
    WHERE permission_code IN ('TRANSACTION_MANAGE', 'TRANSACTION_ADD', 'TRANSACTION_EDIT', 'TRANSACTION_DELETE', 'TRANSACTION_VIEW');
UPDATE t_permission SET permission_module = 'REPORT'
    WHERE permission_code IN ('REPORT_VIEW', 'REPORT_EXPORT', 'REPORT_ANALYSIS');
UPDATE t_permission SET permission_module = 'AUDIT'
    WHERE permission_code IN ('SYSTEM_AUDIT', 'OPERATION_LOG');
UPDATE t_permission SET permission_module = 'SYSTEM'
    WHERE permission_code IN ('SYSTEM_CONFIG', 'DICTIONARY_MANAGE');

-- 为 null 的设置为 OTHER
UPDATE t_permission SET permission_module = 'OTHER' WHERE permission_module IS NULL;

-- 添加索引
SET @idx_exist := (SELECT COUNT(*) FROM information_schema.STATISTICS
    WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 't_permission' AND INDEX_NAME = 'idx_permission_module');
SET @sqlstmt2 := IF(@idx_exist = 0,
    'ALTER TABLE t_permission ADD INDEX idx_permission_module (permission_module)',
    'SELECT ''idx_permission_module already exists'' as result');
PREPARE stmt2 FROM @sqlstmt2;
EXECUTE stmt2;
DEALLOCATE PREPARE stmt2;
