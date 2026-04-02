-- G-Zang (归藏) 数据库初始化脚本
-- 数据库版本：MySQL 8.0+
-- 字符集：utf8mb4

-- 创建数据库
CREATE DATABASE IF NOT EXISTS g_zang DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE g_zang;

-- 用户表
CREATE TABLE IF NOT EXISTS t_user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键，用户ID',
    username VARCHAR(64) NOT NULL COMMENT '用户名（手机号/邮箱）',
    password VARCHAR(255) NOT NULL COMMENT '密码（加密存储）',
    nickname VARCHAR(64) NULL COMMENT '用户昵称',
    avatar VARCHAR(255) NULL COMMENT '用户头像URL',
    role_id BIGINT NOT NULL COMMENT '角色ID',
    company_id BIGINT NULL COMMENT '所属公司ID（个人用户为NULL）',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态 (0:禁用, 1:正常)',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_username (username),
    INDEX idx_role_id (role_id),
    INDEX idx_company_id (company_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 角色表
CREATE TABLE IF NOT EXISTS t_role (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键，角色ID',
    role_name VARCHAR(64) NOT NULL COMMENT '角色名称（如：管理员、财务、普通用户）',
    role_code VARCHAR(64) NOT NULL COMMENT '角色编码',
    description VARCHAR(255) NULL COMMENT '角色描述',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_role_code (role_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

-- 权限表
CREATE TABLE IF NOT EXISTS t_permission (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键，权限ID',
    permission_name VARCHAR(64) NOT NULL COMMENT '权限名称（如：用户管理、记账录入）',
    permission_code VARCHAR(64) NOT NULL COMMENT '权限编码',
    permission_group VARCHAR(32) DEFAULT 'SYSTEM' COMMENT '权限分组：SYSTEM/ORGANIZATION/FINANCE/BUSINESS',
    permission_level TINYINT DEFAULT 2 COMMENT '权限级别：1=系统级, 2=企业级, 3=个人级',
    sort_order INT DEFAULT 0 COMMENT '排序号',
    description VARCHAR(255) DEFAULT NULL COMMENT '权限描述',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_permission_code (permission_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='权限表';

-- 角色权限关联表
CREATE TABLE IF NOT EXISTS t_role_permission (
    role_id BIGINT NOT NULL COMMENT '角色ID',
    permission_id BIGINT NOT NULL COMMENT '权限ID',
    PRIMARY KEY (role_id, permission_id),
    INDEX idx_role_id (role_id),
    INDEX idx_permission_id (permission_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色权限关联表';

-- 公司表
CREATE TABLE IF NOT EXISTS t_company (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键，公司ID (tenant_id)',
    company_name VARCHAR(128) NOT NULL COMMENT '公司名称',
    company_code VARCHAR(64) NULL COMMENT '公司编码',
    contact_name VARCHAR(64) NULL COMMENT '联系人姓名',
    contact_phone VARCHAR(32) NULL COMMENT '联系人电话',
    contact_email VARCHAR(128) NULL COMMENT '联系人邮箱',
    address VARCHAR(255) NULL COMMENT '公司地址',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0=禁用，1=正常',
    admin_user_id BIGINT NOT NULL COMMENT '公司管理员用户ID',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_admin_user_id (admin_user_id),
    INDEX idx_company_code (company_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='公司/组织表';

-- 账户表
CREATE TABLE IF NOT EXISTS t_account (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键，账户ID',
    user_id BIGINT NOT NULL COMMENT '所属用户ID',
    company_id BIGINT NULL COMMENT '所属公司ID（个人账户为NULL）',
    account_name VARCHAR(64) NOT NULL COMMENT '账户名称（如：现金、支付宝、招商银行）',
    account_type TINYINT NOT NULL DEFAULT 1 COMMENT '账户类型 (1:现金, 2:银行卡, 3:电子支付)',
    balance DECIMAL(18,2) NOT NULL DEFAULT 0.00 COMMENT '当前余额',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_user_id (user_id),
    INDEX idx_company_id (company_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='账户表';

-- 记账分类表
CREATE TABLE IF NOT EXISTS t_category (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键，分类ID',
    user_id BIGINT NULL COMMENT '所属用户ID（公司分类为NULL）',
    company_id BIGINT NULL COMMENT '所属公司ID（个人分类为NULL）',
    category_name VARCHAR(64) NOT NULL COMMENT '分类名称（如：餐饮、交通、配件采购）',
    parent_id BIGINT NOT NULL DEFAULT 0 COMMENT '父分类ID（0为一级分类）',
    type TINYINT NOT NULL DEFAULT 1 COMMENT '类型 (1:收入, 2:支出)',
    is_system TINYINT NOT NULL DEFAULT 0 COMMENT '是否系统预设 (0:否, 1:是)',
    INDEX idx_user_id (user_id),
    INDEX idx_company_id (company_id),
    INDEX idx_parent_id (parent_id),
    INDEX idx_type (type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='记账分类表';

-- 交易记录表
CREATE TABLE IF NOT EXISTS t_transaction (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键，交易ID',
    user_id BIGINT NOT NULL COMMENT '记录用户ID',
    company_id BIGINT NULL COMMENT '所属公司ID（个人交易为NULL）',
    amount DECIMAL(18,2) NOT NULL DEFAULT 0.00 COMMENT '交易金额',
    type TINYINT NOT NULL DEFAULT 1 COMMENT '交易类型 (1:收入, 2:支出)',
    category_id BIGINT NOT NULL COMMENT '分类ID',
    account_id BIGINT NOT NULL COMMENT '账户ID',
    transaction_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '交易发生时间',
    remark VARCHAR(500) NULL COMMENT '备注',
    related_business_id VARCHAR(128) NULL COMMENT '关联业务ID（如维修单号）',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_user_id (user_id),
    INDEX idx_company_id (company_id),
    INDEX idx_category_id (category_id),
    INDEX idx_account_id (account_id),
    INDEX idx_transaction_time (transaction_time),
    INDEX idx_user_company_time (user_id, company_id, transaction_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='交易记录表';

-- 插入基础数据
-- 角色数据
INSERT INTO t_role (role_name, role_code, description) VALUES
('超级管理员', 'SUPER_ADMIN', '系统超级管理员，拥有所有权限'),
('管理员', 'ADMIN', '公司管理员，管理公司事务'),
('财务', 'FINANCE', '财务人员，负责记账和报表'),
('普通用户', 'USER', '普通用户，基本记账功能')
ON DUPLICATE KEY UPDATE role_name = VALUES(role_name), description = VALUES(description);

-- 权限数据
INSERT INTO t_permission (permission_name, permission_code) VALUES
('用户管理', 'USER_MANAGE'),
('角色管理', 'ROLE_MANAGE'),
('公司管理', 'COMPANY_MANAGE'),
('记账录入', 'TRANSACTION_ADD'),
('记账查看', 'TRANSACTION_VIEW'),
('记账编辑', 'TRANSACTION_EDIT'),
('记账删除', 'TRANSACTION_DELETE'),
('账户管理', 'ACCOUNT_MANAGE'),
('分类管理', 'CATEGORY_MANAGE'),
('报表查看', 'REPORT_VIEW')
ON DUPLICATE KEY UPDATE permission_name = VALUES(permission_name);

-- 角色权限关联
-- 超级管理员拥有所有权限
INSERT INTO t_role_permission (role_id, permission_id)
SELECT r.id, p.id FROM t_role r, t_permission p WHERE r.role_code = 'SUPER_ADMIN'
ON DUPLICATE KEY UPDATE role_id = VALUES(role_id), permission_id = VALUES(permission_id);

-- 管理员权限
INSERT INTO t_role_permission (role_id, permission_id)
SELECT r.id, p.id FROM t_role r, t_permission p
WHERE r.role_code = 'ADMIN' AND p.permission_code IN ('USER_MANAGE', 'COMPANY_MANAGE', 'TRANSACTION_ADD', 'TRANSACTION_VIEW', 'TRANSACTION_EDIT', 'ACCOUNT_MANAGE', 'CATEGORY_MANAGE', 'REPORT_VIEW')
ON DUPLICATE KEY UPDATE role_id = VALUES(role_id), permission_id = VALUES(permission_id);

-- 财务权限
INSERT INTO t_role_permission (role_id, permission_id)
SELECT r.id, p.id FROM t_role r, t_permission p
WHERE r.role_code = 'FINANCE' AND p.permission_code IN ('TRANSACTION_ADD', 'TRANSACTION_VIEW', 'TRANSACTION_EDIT', 'TRANSACTION_DELETE', 'ACCOUNT_MANAGE', 'CATEGORY_MANAGE', 'REPORT_VIEW')
ON DUPLICATE KEY UPDATE role_id = VALUES(role_id), permission_id = VALUES(permission_id);

-- 普通用户权限
INSERT INTO t_role_permission (role_id, permission_id)
SELECT r.id, p.id FROM t_role r, t_permission p
WHERE r.role_code = 'USER' AND p.permission_code IN ('TRANSACTION_ADD', 'TRANSACTION_VIEW', 'ACCOUNT_MANAGE', 'CATEGORY_MANAGE')
ON DUPLICATE KEY UPDATE role_id = VALUES(role_id), permission_id = VALUES(permission_id);

-- 系统预设分类
INSERT INTO t_category (category_name, parent_id, type, is_system) VALUES
-- 支出分类
('餐饮', 0, 2, 1),
('交通', 0, 2, 1),
('购物', 0, 2, 1),
('娱乐', 0, 2, 1),
('医疗', 0, 2, 1),
('教育', 0, 2, 1),
('居家', 0, 2, 1),
('通讯', 0, 2, 1),
('其他支出', 0, 2, 1),
-- 收入分类
('工资', 0, 1, 1),
('奖金', 0, 1, 1),
('投资', 0, 1, 1),
('兼职', 0, 1, 1),
('其他收入', 0, 1, 1)
ON DUPLICATE KEY UPDATE category_name = VALUES(category_name);

-- 初始化真实账号数据
-- 注意：密码使用bcrypt加密，示例密码为"123456"
-- 生产环境请使用更强的密码，并考虑动态生成

-- 创建测试公司
INSERT INTO t_company (company_name, admin_user_id) VALUES
('G-Zang测试公司', 2)
ON DUPLICATE KEY UPDATE company_name = VALUES(company_name);

-- 创建用户账号
-- 密码：123456 (bcrypt加密后的哈希值)
-- 先删除可能存在的数据，然后重新插入以确保ID正确生成
DELETE FROM t_user WHERE username IN ('admin@gzang.com', 'manager@gzang.com', 'finance@gzang.com', 'user@gzang.com');

INSERT INTO t_user (username, password, nickname, role_id, company_id, status) VALUES
-- 超级管理员账号
('admin@gzang.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lbdxp7KdHtKJSLBZi', '超级管理员', 1, NULL, 1),
-- 管理员账号
('manager@gzang.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lbdxp7KdHtKJSLBZi', '管理员', 2, 1, 1),
-- 财务账号
('finance@gzang.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lbdxp7KdHtKJSLBZi', '财务', 3, 1, 1),
-- 普通用户账号
('user@gzang.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lbdxp7KdHtKJSLBZi', '普通用户', 4, NULL, 1);

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


