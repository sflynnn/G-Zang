-- G-Zang (归藏) 真实账号数据初始化脚本
-- 此脚本包含真实的用户账号、公司、账户和交易记录数据
-- 执行前请确保基础表结构已创建（schema.sql）

USE g_zang;

-- 初始化真实账号数据
-- 注意：密码使用bcrypt加密，示例密码为"123456"
-- 生产环境请使用更强的密码，并考虑动态生成

-- 创建测试公司
INSERT INTO t_company (company_name, admin_user_id) VALUES
('G-Zang测试公司', 2)
ON DUPLICATE KEY UPDATE company_name = VALUES(company_name);

-- 创建用户账号
-- 密码：123456 (bcrypt加密后的哈希值)
INSERT INTO t_user (username, password, nickname, role_id, company_id, status) VALUES
-- 超级管理员账号
('admin@gzang.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lbdxp7KdHtKJSLBZi', '超级管理员', 1, NULL, 1),
-- 管理员账号
('manager@gzang.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lbdxp7KdHtKJSLBZi', '管理员', 2, 1, 1),
-- 财务账号
('finance@gzang.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lbdxp7KdHtKJSLBZi', '财务', 3, 1, 1),
-- 普通用户账号
('user@gzang.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lbdxp7KdHtKJSLBZi', '普通用户', 4, NULL, 1)
ON DUPLICATE KEY UPDATE
  nickname = VALUES(nickname),
  role_id = VALUES(role_id),
  company_id = VALUES(company_id),
  status = VALUES(status);

-- 创建用户账户
-- 为每个用户创建默认现金账户
INSERT INTO t_account (user_id, company_id, account_name, account_type, balance) VALUES
(1, NULL, '现金', 1, 10000.00),
(2, 1, '现金', 1, 5000.00),
(3, 1, '现金', 1, 3000.00),
(4, NULL, '现金', 1, 2000.00),
(2, 1, '公司银行卡', 2, 50000.00),
(3, 1, '财务支付宝', 3, 15000.00)
ON DUPLICATE KEY UPDATE account_name = VALUES(account_name);

-- 创建一些示例交易记录
INSERT INTO t_transaction (user_id, company_id, amount, type, category_id, account_id, transaction_time, remark) VALUES
-- 超级管理员的交易
(1, NULL, 5000.00, 1, 11, 1, '2024-01-15 09:00:00', '系统初始化收入'),
(1, NULL, 800.00, 2, 1, 1, '2024-01-15 12:30:00', '午餐'),
(1, NULL, 50.00, 2, 2, 1, '2024-01-15 18:00:00', '地铁'),

-- 管理员的交易
(2, 1, 10000.00, 1, 11, 2, '2024-01-10 09:00:00', '公司收入'),
(2, 1, 2000.00, 2, 9, 5, '2024-01-12 14:00:00', '办公用品采购'),
(2, 1, 500.00, 2, 1, 2, '2024-01-13 12:00:00', '商务午餐'),

-- 财务的交易
(3, 1, 3000.00, 1, 12, 3, '2024-01-08 09:00:00', '奖金收入'),
(3, 1, 1000.00, 2, 3, 6, '2024-01-10 15:30:00', '办公设备'),
(3, 1, 200.00, 2, 1, 3, '2024-01-11 11:45:00', '客户招待'),

-- 普通用户的交易
(4, NULL, 3000.00, 1, 11, 4, '2024-01-05 09:00:00', '工资收入'),
(4, NULL, 150.00, 2, 1, 4, '2024-01-07 19:00:00', '晚餐'),
(4, NULL, 100.00, 2, 3, 4, '2024-01-08 14:20:00', '日用品')
ON DUPLICATE KEY UPDATE remark = VALUES(remark);

-- 查询初始化结果
SELECT '用户账号初始化完成' as message, COUNT(*) as user_count FROM t_user;
SELECT '公司初始化完成' as message, COUNT(*) as company_count FROM t_company;
SELECT '账户初始化完成' as message, COUNT(*) as account_count FROM t_account;
SELECT '交易记录初始化完成' as message, COUNT(*) as transaction_count FROM t_transaction;



