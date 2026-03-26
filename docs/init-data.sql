-- G-Zang 数据库初始化数据
-- 执行顺序：角色 -> 权限 -> 角色权限关联 -> 公司 -> 用户

-- ===========================================
-- 1. 插入基础角色
-- ===========================================
INSERT INTO t_role (role_name, role_code, description) VALUES
('超级管理员', 'SUPER_ADMIN', '系统超级管理员，拥有所有权限'),
('管理员', 'ADMIN', '公司管理员，管理公司事务'),
('财务', 'FINANCE', '财务人员，负责记账和报表'),
('普通用户', 'USER', '普通用户，基本记账功能');

-- ===========================================
-- 2. 插入基础权限
-- ===========================================
INSERT INTO t_permission (permission_name, permission_code) VALUES
-- 用户管理权限
('用户管理', 'USER_MANAGE'),
('用户查看', 'USER_VIEW'),
('用户添加', 'USER_ADD'),
('用户编辑', 'USER_EDIT'),
('用户删除', 'USER_DELETE'),

-- 角色管理权限
('角色管理', 'ROLE_MANAGE'),
('角色查看', 'ROLE_VIEW'),
('角色添加', 'ROLE_ADD'),
('角色编辑', 'ROLE_EDIT'),
('角色删除', 'ROLE_DELETE'),

-- 权限管理权限
('权限管理', 'PERMISSION_MANAGE'),
('权限查看', 'PERMISSION_VIEW'),
('权限添加', 'PERMISSION_ADD'),
('权限编辑', 'PERMISSION_EDIT'),
('权限删除', 'PERMISSION_DELETE'),

-- 公司管理权限
('公司管理', 'COMPANY_MANAGE'),
('公司查看', 'COMPANY_VIEW'),
('公司添加', 'COMPANY_ADD'),
('公司编辑', 'COMPANY_EDIT'),
('公司删除', 'COMPANY_DELETE'),

-- 财务相关权限
('记账录入', 'TRANSACTION_ADD'),
('记账查看', 'TRANSACTION_VIEW'),
('记账编辑', 'TRANSACTION_EDIT'),
('记账删除', 'TRANSACTION_DELETE'),
('账户管理', 'ACCOUNT_MANAGE'),
('分类管理', 'CATEGORY_MANAGE'),
('报表查看', 'REPORT_VIEW'),
('预算管理', 'BUDGET_MANAGE'),
('成本中心管理', 'COST_CENTER_MANAGE'),

-- 系统管理权限
('系统配置', 'SYSTEM_CONFIG'),
('操作日志查看', 'LOG_VIEW');

-- ===========================================
-- 3. 插入角色权限关联关系
-- ===========================================

-- 超级管理员权限（拥有所有权限）
INSERT INTO t_role_permission (role_id, permission_id)
SELECT 1, id FROM t_permission;

-- 管理员权限
INSERT INTO t_role_permission (role_id, permission_id)
SELECT 2, p.id FROM t_permission p WHERE p.permission_code IN (
    'USER_MANAGE', 'USER_VIEW', 'USER_ADD', 'USER_EDIT',
    'COMPANY_MANAGE', 'COMPANY_VIEW', 'COMPANY_ADD', 'COMPANY_EDIT', 'COMPANY_VIEW',
    'TRANSACTION_ADD', 'TRANSACTION_VIEW', 'TRANSACTION_EDIT', 'TRANSACTION_DELETE',
    'ACCOUNT_MANAGE', 'CATEGORY_MANAGE', 'REPORT_VIEW', 'BUDGET_MANAGE', 'COST_CENTER_MANAGE'
);

-- 财务权限
INSERT INTO t_role_permission (role_id, permission_id)
SELECT 3, p.id FROM t_permission p WHERE p.permission_code IN (
    'TRANSACTION_ADD', 'TRANSACTION_VIEW', 'TRANSACTION_EDIT', 'TRANSACTION_DELETE',
    'ACCOUNT_MANAGE', 'CATEGORY_MANAGE', 'REPORT_VIEW', 'BUDGET_MANAGE'
);

-- 普通用户权限
INSERT INTO t_role_permission (role_id, permission_id)
SELECT 4, p.id FROM t_permission p WHERE p.permission_code IN (
    'TRANSACTION_ADD', 'TRANSACTION_VIEW', 'TRANSACTION_EDIT',
    'ACCOUNT_MANAGE', 'CATEGORY_MANAGE', 'REPORT_VIEW'
);

-- ===========================================
-- 4. 插入测试公司
-- ===========================================
INSERT INTO t_company (company_name, admin_user_id) VALUES
('G-Zang科技有限公司', 1),
('测试修理厂', 2);

-- ===========================================
-- 5. 插入测试用户
-- ===========================================
-- 密码都是：123456 （经过BCrypt加密）
-- BCrypt加密后的密码（强度10）：
-- 123456 -> $2a$10$N.zmdr9k7uOcBvBw1WCn9u1jKzYQK9.7Q2L8Eo3JZ8.6wKd8QK8C

INSERT INTO t_user (username, password, nickname, role_id, company_id, status) VALUES
-- 超级管理员
('admin@gzang.com', '$2a$10$N.zmdr9k7uOcBvBw1WCn9u1jKzYQK9.7Q2L8Eo3JZ8.6wKd8QK8C', '系统管理员', 1, NULL, 1),

-- 管理员用户
('manager@gzang.com', '$2a$10$N.zmdr9k7uOcBvBw1WCn9u1jKzYQK9.7Q2L8Eo3JZ8.6wKd8QK8C', '管理员', 2, 2, 1),
('admin@test.com', '$2a$10$N.zmdr9k7uOcBvBw1WCn9u1jKzYQK9.7Q2L8Eo3JZ8.6wKd8QK8C', '公司管理员', 2, 1, 1),

-- 财务用户
('finance@gzang.com', '$2a$10$N.zmdr9k7uOcBvBw1WCn9u1jKzYQK9.7Q2L8Eo3JZ8.6wKd8QK8C', '财务经理', 3, 2, 1),
('accountant@test.com', '$2a$10$N.zmdr9k7uOcBvBw1WCn9u1jKzYQK9.7Q2L8Eo3JZ8.6wKd8QK8C', '会计', 3, 1, 1),

-- 普通用户
('user@gzang.com', '$2a$10$N.zmdr9k7uOcBvBw1WCn9u1jKzYQK9.7Q2L8Eo3JZ8.6wKd8QK8C', '普通用户', 4, NULL, 1),
('employee@test.com', '$2a$10$N.zmdr9k7uOcBvBw1WCn9u1jKzYQK9.7Q2L8Eo3JZ8.6wKd8QK8C', '员工', 4, 2, 1);

-- ===========================================
-- 6. 插入系统配置
-- ===========================================
INSERT INTO t_system_config (config_key, config_value, config_type, description, is_system) VALUES
('system.site_name', 'G-Zang财务管理系统', 'system', '系统名称', 1),
('system.max_login_attempts', '5', 'security', '最大登录尝试次数', 1),
('system.session_timeout', '7200', 'security', '会话超时时间(秒)', 1),
('file.upload.max_size', '10MB', 'file', '文件上传最大大小', 1),
('file.upload.allowed_types', 'jpg,jpeg,png,pdf,doc,docx,xlsx', 'file', '允许上传的文件类型', 1),
('system.version', '1.0.0', 'system', '系统版本', 1);

-- ===========================================
-- 7. 插入预设分类数据
-- ===========================================

-- 支出分类
INSERT INTO t_category (category_name, parent_id, type, is_system) VALUES
('餐饮', 0, 2, 1),
('交通', 0, 2, 1),
('购物', 0, 2, 1),
('娱乐', 0, 2, 1),
('医疗', 0, 2, 1),
('教育', 0, 2, 1),
('居家', 0, 2, 1),
('通讯', 0, 2, 1),
('其他支出', 0, 2, 1);

-- 收入分类
INSERT INTO t_category (category_name, parent_id, type, is_system) VALUES
('工资', 0, 1, 1),
('奖金', 0, 1, 1),
('投资', 0, 1, 1),
('兼职', 0, 1, 1),
('其他收入', 0, 1, 1);

-- ===========================================
-- 8. 插入测试账户
-- ===========================================
INSERT INTO t_account (user_id, company_id, account_name, account_type, balance) VALUES
(1, NULL, '现金', 1, 1000.00),
(1, NULL, '招商银行', 2, 50000.00),
(2, 2, '现金', 1, 500.00),
(2, 2, '工商银行', 2, 20000.00);

-- ===========================================
-- 执行结果验证
-- ===========================================

-- 验证数据插入是否成功
SELECT '角色数量:' as info, COUNT(*) as count FROM t_role
UNION ALL
SELECT '权限数量:', COUNT(*) FROM t_permission
UNION ALL
SELECT '角色权限关联数量:', COUNT(*) FROM t_role_permission
UNION ALL
SELECT '用户数量:', COUNT(*) FROM t_user
UNION ALL
SELECT '公司数量:', COUNT(*) FROM t_company
UNION ALL
SELECT '分类数量:', COUNT(*) FROM t_category
UNION ALL
SELECT '账户数量:', COUNT(*) FROM t_account;

-- 验证超级管理员权限
SELECT r.role_name, COUNT(rp.permission_id) as permission_count
FROM t_role r
LEFT JOIN t_role_permission rp ON r.id = rp.role_id
WHERE r.role_code = 'SUPER_ADMIN'
GROUP BY r.id, r.role_name;

-- 验证用户角色分配
SELECT u.username, u.nickname, r.role_name, r.role_code
FROM t_user u
JOIN t_role r ON u.role_id = r.id
ORDER BY u.id;
