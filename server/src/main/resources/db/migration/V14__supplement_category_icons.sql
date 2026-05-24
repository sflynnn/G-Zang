-- ============================================================
-- V14: 补充缺失的分类图标配置
-- 确保所有系统预设分类都有图标配置
-- ============================================================

USE g_zang;

-- 支出分类补充图标
INSERT INTO t_category_icon (category_id, icon, color, sort_order) VALUES
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

-- 收入分类补充图标
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

-- 验证：检查哪些分类缺少图标
SELECT '缺少图标的支出分类:' as '';
SELECT c.id, c.category_name, ci.icon
FROM t_category c
LEFT JOIN t_category_icon ci ON c.id = ci.category_id
WHERE c.type = 2 AND ci.icon IS NULL
ORDER BY c.id;

SELECT '缺少图标的收入分类:' as '';
SELECT c.id, c.category_name, ci.icon
FROM t_category c
LEFT JOIN t_category_icon ci ON c.id = ci.category_id
WHERE c.type = 1 AND ci.icon IS NULL
ORDER BY c.id;

-- 显示所有图标的分类
SELECT '所有有图标的分类:' as '';
SELECT c.id, c.category_name, c.type, ci.icon, ci.color
FROM t_category c
INNER JOIN t_category_icon ci ON c.id = ci.category_id
ORDER BY c.type, c.parent_id, c.id;
