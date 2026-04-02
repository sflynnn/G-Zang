-- ============================================================
-- V6__add_permission_timestamp.sql
-- 描述：为 t_permission 表添加 create_time 和 update_time 字段
-- 原因：schema.sql 和迁移脚本定义了这些字段，但实际数据库中缺失
--       导致 MyBatis-Plus 查询报错 Unknown column 'create_time'
-- ============================================================
USE g_zang;

-- 添加 create_time 字段（如果不存在）
SET @exist := (SELECT COUNT(*) FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 't_permission' AND COLUMN_NAME = 'create_time');
SET @sqlstmt := IF(@exist = 0,
    'ALTER TABLE t_permission ADD COLUMN create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT ''创建时间''',
    'SELECT ''create_time column already exists'' as result');
PREPARE stmt FROM @sqlstmt;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 添加 update_time 字段（如果不存在）
SET @exist := (SELECT COUNT(*) FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 't_permission' AND COLUMN_NAME = 'update_time');
SET @sqlstmt := IF(@exist = 0,
    'ALTER TABLE t_permission ADD COLUMN update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT ''更新时间''',
    'SELECT ''update_time column already exists'' as result');
PREPARE stmt FROM @sqlstmt;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
