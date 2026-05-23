-- V8: 创建分类图标颜色配置表
-- 为每个系统预设分类关联图标和颜色

CREATE TABLE IF NOT EXISTS t_category_icon (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    category_id BIGINT NOT NULL COMMENT '分类ID',
    icon VARCHAR(64) NOT NULL DEFAULT 'circle' COMMENT '图标名称',
    color VARCHAR(16) NOT NULL DEFAULT '#6B7280' COMMENT '主题色十六进制',
    sort_order INT NOT NULL DEFAULT 0 COMMENT '排序',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_category_id (category_id),
    KEY idx_category_id (category_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='分类图标颜色配置表';

-- 插入系统分类图标配置（支出分类）
INSERT IGNORE INTO t_category_icon (id, category_id, icon, color, sort_order) VALUES
(1, 1, 'food', '#FB8B24', 1),           -- 餐饮 - 橙色
(2, 2, 'transport', '#0F4C5C', 2),    -- 交通 - 深青
(3, 3, 'shopping', '#EF476F', 3),     -- 购物 - 玫红
(4, 4, 'entertainment', '#9B59B6', 4), -- 娱乐 - 紫色
(5, 5, 'medical', '#EF476F', 5),     -- 医疗 - 玫红
(6, 6, 'education', '#118AB2', 6),   -- 教育 - 蓝色
(7, 7, 'housing', '#06D6A0', 7),      -- 居家 - 绿色
(8, 8, 'communication', '#FFD166', 8), -- 通讯 - 黄色
(9, 9, 'shopping', '#6B7280', 9);    -- 其他支出 - 灰色

-- 插入收入分类图标配置
INSERT IGNORE INTO t_category_icon (id, category_id, icon, color, sort_order) VALUES
(10, 10, 'income', '#06D6A0', 1),      -- 工资 - 绿色
(11, 11, 'star', '#FFD166', 2),       -- 奖金 - 黄色
(12, 12, 'chart', '#3A86FF', 3),      -- 投资 - 天蓝
(13, 13, 'clock', '#118AB2', 4),      -- 兼职 - 深蓝
(14, 14, 'heart', '#EF476F', 5),      -- 礼金 - 玫红
(15, 15, 'refresh', '#9B59B6', 6);   -- 其他收入 - 紫色
