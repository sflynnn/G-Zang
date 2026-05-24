-- ============================================================
-- V13: 添加完整的分类数据（含 emoji 图标和颜色）
-- 为移动端应用提供丰富的分类选择
-- ============================================================

USE g_zang;

-- ============================================================
-- 0. 添加 sort_order 和 remark 字段到 t_category（如果不存在）
-- ============================================================
SET @exist_sort := (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
                    WHERE TABLE_SCHEMA = 'g_zang' AND TABLE_NAME = 't_category' AND COLUMN_NAME = 'sort_order');
SET @sqlstmt_sort := IF(@exist_sort > 0, 'SELECT "Column sort_order already exists"',
                        'ALTER TABLE t_category ADD COLUMN sort_order INT NOT NULL DEFAULT 0 COMMENT "排序顺序"');
PREPARE stmt_sort FROM @sqlstmt_sort;
EXECUTE stmt_sort;
DEALLOCATE PREPARE stmt_sort;

SET @exist_remark := (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
                     WHERE TABLE_SCHEMA = 'g_zang' AND TABLE_NAME = 't_category' AND COLUMN_NAME = 'remark');
SET @sqlstmt_remark := IF(@exist_remark > 0, 'SELECT "Column remark already exists"',
                           'ALTER TABLE t_category ADD COLUMN remark VARCHAR(255) DEFAULT NULL COMMENT "备注"');
PREPARE stmt_remark FROM @sqlstmt_remark;
EXECUTE stmt_remark;
DEALLOCATE PREPARE stmt_remark;

-- ============================================================
-- 1. 支出分类（一级分类）- 共12个
-- ============================================================
INSERT INTO t_category (id, category_name, type, parent_id, is_system, sort_order, remark) VALUES
-- 餐饮相关 (parent_id = 0 表示一级分类)
(1, '餐饮', 2, 0, TRUE, 1, '餐饮消费'),
(2, '交通', 2, 0, TRUE, 2, '出行交通'),
(3, '购物', 2, 0, TRUE, 3, '日常购物'),
(4, '娱乐', 2, 0, TRUE, 4, '休闲娱乐'),
(5, '居住', 2, 0, TRUE, 5, '房租水电'),
(6, '医疗', 2, 0, TRUE, 6, '医疗保健'),
(7, '教育', 2, 0, TRUE, 7, '教育培训'),
(8, '通讯', 2, 0, TRUE, 8, '通讯网络'),
(9, '水果', 2, 0, TRUE, 9, '水果零食'),
(10, '日用', 2, 0, TRUE, 10, '日用百货'),
(11, '运动', 2, 0, TRUE, 11, '运动健身'),
(12, '宠物', 2, 0, TRUE, 12, '宠物用品')
ON DUPLICATE KEY UPDATE category_name = VALUES(category_name);

-- ============================================================
-- 2. 支出子分类 - 餐饮 (parent_id = 1)
-- ============================================================
INSERT INTO t_category (id, category_name, type, parent_id, is_system, sort_order) VALUES
(101, '早餐', 2, 1, TRUE, 1),
(102, '午餐', 2, 1, TRUE, 2),
(103, '晚餐', 2, 1, TRUE, 3),
(104, '宵夜', 2, 1, TRUE, 4),
(105, '零食', 2, 1, TRUE, 5),
(106, '饮料', 2, 1, TRUE, 6),
(107, '水果', 2, 1, TRUE, 7),
(108, '外卖', 2, 1, TRUE, 8)
ON DUPLICATE KEY UPDATE category_name = VALUES(category_name);

-- ============================================================
-- 3. 支出子分类 - 交通 (parent_id = 2)
-- ============================================================
INSERT INTO t_category (id, category_name, type, parent_id, is_system, sort_order) VALUES
(201, '打车', 2, 2, TRUE, 1),
(202, '公交', 2, 2, TRUE, 2),
(203, '地铁', 2, 2, TRUE, 3),
(204, '火车', 2, 2, TRUE, 4),
(205, '飞机', 2, 2, TRUE, 5),
(206, '加油', 2, 2, TRUE, 6),
(207, '停车', 2, 2, TRUE, 7),
(208, '租车', 2, 2, TRUE, 8)
ON DUPLICATE KEY UPDATE category_name = VALUES(category_name);

-- ============================================================
-- 4. 支出子分类 - 购物 (parent_id = 3)
-- ============================================================
INSERT INTO t_category (id, category_name, type, parent_id, is_system, sort_order) VALUES
(301, '日用品', 2, 3, TRUE, 1),
(302, '服饰', 2, 3, TRUE, 2),
(303, '美妆', 2, 3, TRUE, 3),
(304, '数码', 2, 3, TRUE, 4),
(305, '图书', 2, 3, TRUE, 5),
(306, '母婴', 2, 3, TRUE, 6),
(307, '家电', 2, 3, TRUE, 7),
(308, '礼品', 2, 3, TRUE, 8)
ON DUPLICATE KEY UPDATE category_name = VALUES(category_name);

-- ============================================================
-- 5. 支出子分类 - 娱乐 (parent_id = 4)
-- ============================================================
INSERT INTO t_category (id, category_name, type, parent_id, is_system, sort_order) VALUES
(401, '电影', 2, 4, TRUE, 1),
(402, '游戏', 2, 4, TRUE, 2),
(403, '旅游', 2, 4, TRUE, 3),
(404, '健身', 2, 4, TRUE, 4),
(405, '聚会', 2, 4, TRUE, 5),
(406, '咖啡', 2, 4, TRUE, 6),
(407, '演唱会', 2, 4, TRUE, 7),
(408, '运动', 2, 4, TRUE, 8)
ON DUPLICATE KEY UPDATE category_name = VALUES(category_name);

-- ============================================================
-- 6. 收入分类（一级分类）- 共8个
-- ============================================================
INSERT INTO t_category (id, category_name, type, parent_id, is_system, sort_order, remark) VALUES
(1001, '工资', 1, 0, TRUE, 1, '固定工资收入'),
(1002, '奖金', 1, 0, TRUE, 2, '奖金津贴'),
(1003, '投资', 1, 0, TRUE, 3, '投资收益'),
(1004, '兼职', 1, 0, TRUE, 4, '兼职外快'),
(1005, '红包', 1, 0, TRUE, 5, '红包礼金'),
(1006, '兑换', 1, 0, TRUE, 6, '兑换收益'),
(1007, '退款', 1, 0, TRUE, 7, '退款返还'),
(1008, '理财', 1, 0, TRUE, 8, '理财产品')
ON DUPLICATE KEY UPDATE category_name = VALUES(category_name);

-- ============================================================
-- 7. 收入子分类 - 工资 (parent_id = 1001)
-- ============================================================
INSERT INTO t_category (id, category_name, type, parent_id, is_system, sort_order) VALUES
(10011, '月薪', 1, 1001, TRUE, 1),
(10012, '年终奖', 1, 1001, TRUE, 2),
(10013, '补贴', 1, 1001, TRUE, 3),
(10014, '提成', 1, 1001, TRUE, 4)
ON DUPLICATE KEY UPDATE category_name = VALUES(category_name);

-- ============================================================
-- 8. 收入子分类 - 投资 (parent_id = 1003)
-- ============================================================
INSERT INTO t_category (id, category_name, type, parent_id, is_system, sort_order) VALUES
(10031, '股票', 1, 1003, TRUE, 1),
(10032, '基金', 1, 1003, TRUE, 2),
(10033, '理财', 1, 1003, TRUE, 3),
(10034, '利息', 1, 1003, TRUE, 4)
ON DUPLICATE KEY UPDATE category_name = VALUES(category_name);

-- ============================================================
-- 9. 支出分类图标配置（更新已有的，添加缺失的）
-- ============================================================
INSERT INTO t_category_icon (category_id, icon, color, sort_order) VALUES
-- 餐饮
(1, '🍔', '#EF476F', 1),
-- 餐饮子分类
(101, '🍳', '#EF476F', 1),
(102, '🍱', '#EF476F', 2),
(103, '🍲', '#EF476F', 3),
(104, '🌙', '#EF476F', 4),
(105, '🍿', '#EF476F', 5),
(106, '🧃', '#EF476F', 6),
(107, '🍎', '#EF476F', 7),
(108, '🥡', '#EF476F', 8),

-- 交通
(2, '🚕', '#118AB2', 2),
-- 交通子分类
(201, '🚕', '#118AB2', 1),
(202, '🚌', '#118AB2', 2),
(203, '🚇', '#118AB2', 3),
(204, '🚂', '#118AB2', 4),
(205, '✈️', '#118AB2', 5),
(206, '⛽', '#118AB2', 6),
(207, '🅿️', '#118AB2', 7),
(208, '🚗', '#118AB2', 8),

-- 购物
(3, '🛒', '#E91E63', 3),
-- 购物子分类
(301, '🧴', '#E91E63', 1),
(302, '👕', '#E91E63', 2),
(303, '💄', '#E91E63', 3),
(304, '📱', '#E91E63', 4),
(305, '📚', '#E91E63', 5),
(306, '👶', '#E91E63', 6),
(307, '🏠', '#E91E63', 7),
(308, '🎁', '#E91E63', 8),

-- 娱乐
(4, '🎮', '#9C27B0', 4),
-- 娱乐子分类
(401, '🎬', '#9C27B0', 1),
(402, '🎮', '#9C27B0', 2),
(403, '🌴', '#9C27B0', 3),
(404, '💪', '#9C27B0', 4),
(405, '🎉', '#9C27B0', 5),
(406, '☕', '#9C27B0', 6),
(407, '🎤', '#9C27B0', 7),
(408, '⚽', '#9C27B0', 8),

-- 居住
(5, '🏠', '#FF9800', 5),

-- 医疗
(6, '💊', '#F44336', 6),

-- 教育
(7, '📚', '#3F51B5', 7),

-- 通讯
(8, '📱', '#00BCD4', 8),

-- 水果
(9, '🍎', '#4CAF50', 9),

-- 日用
(10, '🧴', '#795548', 10),

-- 运动
(11, '⚽', '#607D8B', 11),

-- 宠物
(12, '🐱', '#FF5722', 12)
ON DUPLICATE KEY UPDATE icon = VALUES(icon), color = VALUES(color);

-- ============================================================
-- 10. 收入分类图标配置
-- ============================================================
INSERT INTO t_category_icon (category_id, icon, color, sort_order) VALUES
-- 工资
(1001, '💼', '#06D6A0', 1),
(10011, '💰', '#06D6A0', 1),
(10012, '🎊', '#06D6A0', 2),
(10013, '💵', '#06D6A0', 3),
(10014, '📈', '#06D6A0', 4),

-- 奖金
(1002, '🎁', '#06D6A0', 2),

-- 投资
(1003, '📈', '#06D6A0', 3),
(10031, '📊', '#06D6A0', 1),
(10032, '📈', '#06D6A0', 2),
(10033, '💹', '#06D6A0', 3),
(10034, '🏦', '#06D6A0', 4),

-- 兼职
(1004, '💻', '#06D6A0', 4),

-- 红包
(1005, '🧧', '#06D6A0', 5),

-- 兑换
(1006, '🎰', '#06D6A0', 6),

-- 退款
(1007, '🔙', '#06D6A0', 7),

-- 理财
(1008, '💹', '#06D6A0', 8)
ON DUPLICATE KEY UPDATE icon = VALUES(icon), color = VALUES(color);

-- ============================================================
-- 11. 验证数据
-- ============================================================
SELECT '=== 支出一级分类 ===' as '';
SELECT c.id, c.category_name, ci.icon, ci.color 
FROM t_category c 
LEFT JOIN t_category_icon ci ON c.id = ci.category_id
WHERE c.type = 2 AND (c.parent_id = 0 OR c.parent_id IS NULL)
ORDER BY c.sort_order;

SELECT '=== 支出子分类(餐饮) ===' as '';
SELECT c.id, c.category_name, ci.icon, ci.color 
FROM t_category c 
LEFT JOIN t_category_icon ci ON c.id = ci.category_id
WHERE c.parent_id = 1
ORDER BY c.sort_order;

SELECT '=== 收入一级分类 ===' as '';
SELECT c.id, c.category_name, ci.icon, ci.color 
FROM t_category c 
LEFT JOIN t_category_icon ci ON c.id = ci.category_id
WHERE c.type = 1 AND (c.parent_id = 0 OR c.parent_id IS NULL)
ORDER BY c.sort_order;

SELECT CONCAT('分类数据插入完成！支出分类: ', 
  (SELECT COUNT(*) FROM t_category WHERE type = 2), 
  ' 个，收入分类: ',
  (SELECT COUNT(*) FROM t_category WHERE type = 1),
  ' 个') as status;
