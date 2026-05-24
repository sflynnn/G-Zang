-- ============================================================
-- V16__add_category_budget_table.sql
-- 分类月度预算额度表
-- 执行时间: 2026-05-24
-- ============================================================

USE g_zang;

-- 创建分类预算表
CREATE TABLE IF NOT EXISTS t_category_budget (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    user_id BIGINT NOT NULL COMMENT '所属用户ID',
    book_id BIGINT NULL COMMENT '所属账本ID（NULL表示通用预算）',
    category_id BIGINT NOT NULL COMMENT '分类ID',
    `year_month` VARCHAR(7) NOT NULL COMMENT '预算月份，格式：YYYY-MM',
    budget_amount DECIMAL(12, 2) NOT NULL DEFAULT 0.00 COMMENT '预算金额',
    warning_threshold DECIMAL(5, 2) NOT NULL DEFAULT 80.00 COMMENT '警告阈值百分比（0-100）',
    is_enabled TINYINT NOT NULL DEFAULT 1 COMMENT '是否启用：0-禁用，1-启用',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    -- 唯一约束：每个用户在每个账本的每个分类每月只能有一个预算
    UNIQUE KEY uk_user_book_category_month (user_id, book_id, category_id, `year_month`),
    
    -- 索引
    INDEX idx_user_id (user_id),
    INDEX idx_book_id (book_id),
    INDEX idx_category_id (category_id),
    INDEX idx_year_month (`year_month`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci 
COMMENT='分类月度预算额度表';

-- 初始化示例数据（餐饮分类默认月度预算 2000 元）
INSERT INTO t_category_budget (user_id, book_id, category_id, `year_month`, budget_amount, warning_threshold) 
SELECT 
    1 as user_id,
    1 as book_id,
    1 as category_id,  -- 餐饮
    DATE_FORMAT(CURRENT_DATE, '%Y-%m') as `year_month`,
    2000.00 as budget_amount,
    80.00 as warning_threshold
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1 FROM t_category_budget 
    WHERE user_id = 1 AND book_id = 1 AND category_id = 1 
    AND `year_month` = DATE_FORMAT(CURRENT_DATE, '%Y-%m')
);

-- 验证表结构
DESCRIBE t_category_budget;
SELECT COUNT(*) as budget_count FROM t_category_budget;

-- 输出完成信息
SELECT 'V16__add_category_budget_table.sql 执行完成' AS status;
