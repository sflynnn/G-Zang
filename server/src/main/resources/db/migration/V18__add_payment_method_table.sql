-- ============================================================
-- V18__add_payment_method_table.sql
-- 支付方式管理表
-- 执行时间: 2026-05-24
-- ============================================================

USE g_zang;

-- 创建支付方式表
CREATE TABLE IF NOT EXISTS t_payment_method (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    user_id BIGINT NOT NULL COMMENT '所属用户ID',
    method_code VARCHAR(50) NOT NULL COMMENT '支付方式代码：cash/card/wx/alipay/other',
    method_name VARCHAR(50) NOT NULL COMMENT '支付方式名称',
    icon VARCHAR(100) DEFAULT NULL COMMENT '图标URL或emoji',
    color VARCHAR(7) DEFAULT '#0F4C5C' COMMENT '颜色',
    sort_order INT NOT NULL DEFAULT 0 COMMENT '排序顺序',
    is_enabled TINYINT NOT NULL DEFAULT 1 COMMENT '是否启用',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    -- 唯一约束
    UNIQUE KEY uk_user_method_code (user_id, method_code),
    
    -- 索引
    INDEX idx_user_id (user_id),
    INDEX idx_sort_order (sort_order)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci 
COMMENT='支付方式管理表';

-- 初始化默认支付方式
INSERT INTO t_payment_method (user_id, method_code, method_name, icon, color, sort_order) VALUES
(1, 'cash', '现金', '💵', '#4CAF50', 1),
(1, 'wx', '微信支付', '💚', '#07C160', 2),
(1, 'alipay', '支付宝', '💙', '#1677FF', 3),
(1, 'card', '银行卡', '💳', '#6366F1', 4),
(1, 'other', '其他', '💰', '#6B7280', 5)
ON DUPLICATE KEY UPDATE method_name = VALUES(method_name);

-- 验证
SELECT * FROM t_payment_method WHERE user_id = 1 ORDER BY sort_order;

-- 输出完成信息
SELECT 'V18__add_payment_method_table.sql 执行完成' AS status;
