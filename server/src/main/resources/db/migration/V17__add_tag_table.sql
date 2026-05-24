-- ============================================================
-- V17__add_tag_table.sql
-- 用户标签管理表
-- 执行时间: 2026-05-24
-- ============================================================

USE g_zang;

-- 创建标签表
CREATE TABLE IF NOT EXISTS t_tag (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    user_id BIGINT NOT NULL COMMENT '所属用户ID',
    tag_name VARCHAR(50) NOT NULL COMMENT '标签名称',
    tag_color VARCHAR(7) DEFAULT '#0F4C5C' COMMENT '标签颜色（十六进制）',
    usage_count INT NOT NULL DEFAULT 0 COMMENT '使用次数',
    is_frequent TINYINT NOT NULL DEFAULT 0 COMMENT '是否常用：0-否，1-是',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    -- 唯一约束：每个用户标签名称唯一
    UNIQUE KEY uk_user_tag_name (user_id, tag_name),
    
    -- 索引
    INDEX idx_user_id (user_id),
    INDEX idx_usage_count (usage_count DESC),
    INDEX idx_is_frequent (is_frequent)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci 
COMMENT='用户标签管理表';

-- 初始化常用标签
INSERT INTO t_tag (user_id, tag_name, tag_color, usage_count, is_frequent) VALUES
(1, '餐饮', '#EF476F', 0, 1),
(1, '交通', '#118AB2', 0, 1),
(1, '购物', '#E91E63', 0, 1),
(1, '娱乐', '#9C27B0', 0, 1),
(1, '日常', '#795548', 0, 1),
(1, '办公', '#3F51B5', 0, 1),
(1, '医疗', '#F44336', 0, 1),
(1, '教育', '#00BCD4', 0, 1)
ON DUPLICATE KEY UPDATE tag_name = VALUES(tag_name);

-- 验证
SELECT * FROM t_tag WHERE user_id = 1 ORDER BY is_frequent DESC, usage_count DESC;

-- 输出完成信息
SELECT 'V17__add_tag_table.sql 执行完成' AS status;
