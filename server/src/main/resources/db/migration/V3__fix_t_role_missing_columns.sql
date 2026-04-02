-- ============================================================
-- G-Zang 修复 t_role 表缺失字段
-- 版本：V3
-- 日期：2026-03-28
-- 描述：Role 实体继承了 BaseEntity（含 createTime/updateTime），
--       但历史环境的 t_role 表没有 create_time/update_time 列，
--       导致 MyBatis Plus 自动生成的 SELECT 报 SQL 语法错误。
-- 兼容：MySQL 8.0
-- ============================================================

USE g_zang;

SET @exist := (SELECT COUNT(*) FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 't_role' AND COLUMN_NAME = 'create_time');
SET @sql := IF(@exist = 0,
    'ALTER TABLE t_role ADD COLUMN create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT ''创建时间'' AFTER is_default',
    'SELECT ''create_time column already exists'' as result');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @exist := (SELECT COUNT(*) FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 't_role' AND COLUMN_NAME = 'update_time');
SET @sql := IF(@exist = 0,
    'ALTER TABLE t_role ADD COLUMN update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT ''更新时间'' AFTER create_time',
    'SELECT ''update_time column already exists'' as result');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;
