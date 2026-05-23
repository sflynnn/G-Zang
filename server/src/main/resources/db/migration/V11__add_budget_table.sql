-- V11__add_budget_table.sql
-- 预算表结构
-- 创建时间: 2026-05-22

-- 预算表
CREATE TABLE IF NOT EXISTS t_budget (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '预算ID',
    user_id BIGINT NOT NULL COMMENT '所属用户ID',
    company_id BIGINT DEFAULT NULL COMMENT '所属公司ID（个人预算为NULL）',
    book_id BIGINT DEFAULT NULL COMMENT '账本ID',
    category_id BIGINT DEFAULT NULL COMMENT '分类ID（NULL表示总预算）',
    amount DECIMAL(15, 2) NOT NULL DEFAULT 0.00 COMMENT '预算金额',
    used_amount DECIMAL(15, 2) NOT NULL DEFAULT 0.00 COMMENT '已使用金额',
    period_type TINYINT NOT NULL DEFAULT 1 COMMENT '预算周期类型 (1:月预算, 2:年预算, 3:周预算)',
    period_start DATETIME DEFAULT NULL COMMENT '预算周期开始日期',
    period_end DATETIME DEFAULT NULL COMMENT '预算周期结束日期',
    name VARCHAR(100) NOT NULL COMMENT '预算名称',
    warning_threshold INT DEFAULT 80 COMMENT '预警阈值（百分比）',
    warning_enabled TINYINT(1) NOT NULL DEFAULT 1 COMMENT '是否启用预警',
    remark VARCHAR(500) DEFAULT NULL COMMENT '备注',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_user_id (user_id),
    INDEX idx_company_id (company_id),
    INDEX idx_book_id (book_id),
    INDEX idx_category_id (category_id),
    INDEX idx_period_type (period_type),
    INDEX idx_user_book_period (user_id, book_id, period_start, period_end)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='预算表';
