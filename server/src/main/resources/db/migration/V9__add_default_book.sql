-- V9: 为测试用户创建默认账本和账户种子数据

-- 为 userId=1 的用户创建默认账本
INSERT INTO t_book (user_id, name, icon, color, currency, currency_symbol, type, is_default) VALUES
(1, '我的账本', '📒', '#0F4C5C', 'CNY', '¥', 'PERSONAL', TRUE)
ON DUPLICATE KEY UPDATE name=VALUES(name);

-- 为 userId=1 的用户创建默认账户（如果还没有的话）
INSERT INTO t_account (user_id, account_name, account_type, balance) VALUES
(1, '现金', 1, 0.00),
(1, '支付宝', 3, 0.00),
(1, '银行卡', 2, 0.00)
ON DUPLICATE KEY UPDATE account_name=VALUES(account_name);
