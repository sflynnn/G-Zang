-- G-Zang 菜单管理数据库迁移脚本
-- 迁移版本：V7__add_menu_management.sql
-- 创建日期：2026-04-15

-- 使用数据库
USE g_zang;

-- =============================================
-- 1. 创建菜单表
-- =============================================
CREATE TABLE IF NOT EXISTS t_menu (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键，菜单ID',
    menu_name VARCHAR(64) NOT NULL COMMENT '菜单名称',
    menu_code VARCHAR(128) NOT NULL COMMENT '菜单编码（唯一标识）',
    menu_type TINYINT NOT NULL DEFAULT 2 COMMENT '菜单类型：1=目录, 2=菜单, 3=按钮, 4=外链',
    parent_id BIGINT NOT NULL DEFAULT 0 COMMENT '父菜单ID（0为一级菜单）',
    icon VARCHAR(128) NULL COMMENT '菜单图标',
    path VARCHAR(255) NULL COMMENT '路由路径',
    component VARCHAR(255) NULL COMMENT '前端组件路径',
    redirect VARCHAR(255) NULL COMMENT '重定向路径',
    sort_order INT NOT NULL DEFAULT 0 COMMENT '排序号',
    visible TINYINT NOT NULL DEFAULT 1 COMMENT '是否显示：0=隐藏, 1=显示',
    cache TINYINT NOT NULL DEFAULT 0 COMMENT '是否缓存：0=否, 1=是',
    affix TINYINT NOT NULL DEFAULT 0 COMMENT '是否固定：0=否, 1=是',
    keep_alive TINYINT NOT NULL DEFAULT 0 COMMENT '是否保活：0=否, 1=是',
    permission_code VARCHAR(64) NULL COMMENT '关联权限码（按钮类型时使用）',
    menu_level TINYINT NOT NULL DEFAULT 1 COMMENT '菜单级别：1=系统级, 2=企业级',
    company_id BIGINT NULL COMMENT '企业ID（系统级菜单为NULL）',
    description VARCHAR(255) NULL COMMENT '菜单描述',
    is_system TINYINT NOT NULL DEFAULT 0 COMMENT '是否为系统内置：0=否, 1=是',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_menu_code (menu_code),
    INDEX idx_parent_id (parent_id),
    INDEX idx_menu_level (menu_level),
    INDEX idx_company_id (company_id),
    INDEX idx_visible (visible),
    INDEX idx_sort_order (sort_order)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='菜单表';

-- =============================================
-- 2. 创建菜单权限关联表
-- =============================================
CREATE TABLE IF NOT EXISTS t_menu_permission (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    menu_id BIGINT NOT NULL COMMENT '菜单ID',
    permission_id BIGINT NOT NULL COMMENT '权限ID',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY uk_menu_permission (menu_id, permission_id),
    INDEX idx_menu_id (menu_id),
    INDEX idx_permission_id (permission_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='菜单权限关联表';

-- =============================================
-- 3. 添加菜单管理权限
-- =============================================
INSERT INTO t_permission (permission_name, permission_code, permission_group, permission_level) VALUES
('菜单管理', 'MENU_MANAGE', 'SYSTEM', 1),
('查看菜单', 'MENU_VIEW', 'SYSTEM', 1),
('添加菜单', 'MENU_ADD', 'SYSTEM', 1),
('编辑菜单', 'MENU_EDIT', 'SYSTEM', 1),
('删除菜单', 'MENU_DELETE', 'SYSTEM', 1)
ON DUPLICATE KEY UPDATE permission_name = VALUES(permission_name);

-- =============================================
-- 4. 初始化默认菜单数据
-- =============================================

-- 系统管理模块
INSERT INTO t_menu (menu_name, menu_code, menu_type, parent_id, icon, path, component, sort_order, menu_level, is_system) VALUES
('系统管理', 'system', 1, 0, 'SettingsOutline', '/system', NULL, 1, 1, 1),
('用户管理', 'system:user', 2, 1, 'PeopleOutline', '/users', 'UserManagement.vue', 1, 1, 1),
('角色管理', 'system:role', 2, 1, 'ShieldOutline', '/roles', 'RoleManagement.vue', 2, 1, 1),
('菜单管理', 'system:menu', 2, 1, 'MenuOutline', '/menus', 'MenuManagement.vue', 3, 1, 1),
('权限管理', 'system:permission', 2, 1, 'KeyOutline', '/permissions', 'PermissionManagement.vue', 4, 1, 1),
('公司管理', 'system:company', 2, 1, 'BusinessOutline', '/companies', 'CompanyManagement.vue', 5, 1, 1),
('系统设置', 'system:settings', 2, 1, 'ConstructOutline', '/system/settings', 'SystemSettings.vue', 6, 1, 1),
('审计日志', 'system:audit', 2, 1, 'DocumentTextOutline', '/audit-logs', 'AuditLog.vue', 7, 1, 1)
ON DUPLICATE KEY UPDATE menu_name = VALUES(menu_name);

-- 财务管理模块
INSERT INTO t_menu (menu_name, menu_code, menu_type, parent_id, icon, path, component, sort_order, menu_level, is_system) VALUES
('财务管理', 'finance', 1, 0, 'WalletOutline', '/finance', NULL, 2, 1, 1),
('账户管理', 'finance:account', 2, 9, 'CardOutline', '/accounts', 'AccountManagement.vue', 1, 1, 1),
('分类管理', 'finance:category', 2, 9, 'GridOutline', '/categories', 'CategoryManagement.vue', 2, 1, 1),
('交易记录', 'finance:transaction', 2, 9, 'SwapHorizontalOutline', '/transactions', 'TransactionList.vue', 3, 1, 1),
('报表中心', 'finance:report', 2, 9, 'BarChartOutline', '/reports', 'ReportCenter.vue', 4, 1, 1)
ON DUPLICATE KEY UPDATE menu_name = VALUES(menu_name);

-- 快捷入口模块
INSERT INTO t_menu (menu_name, menu_code, menu_type, parent_id, icon, path, component, sort_order, menu_level, is_system) VALUES
('快捷入口', 'shortcut', 1, 0, 'FlashOutline', '/shortcut', NULL, 99, 1, 1),
('仪表盘', 'shortcut:dashboard', 2, 14, 'HomeOutline', '/dashboard', 'Dashboard.vue', 1, 1, 1),
('个人中心', 'shortcut:profile', 2, 14, 'PersonOutline', '/profile', 'Profile.vue', 2, 1, 1)
ON DUPLICATE KEY UPDATE menu_name = VALUES(menu_name);

-- =============================================
-- 5. 设置菜单关联权限
-- =============================================

-- 为菜单管理菜单关联权限
UPDATE t_menu SET permission_code = 'USER_MANAGE' WHERE menu_code = 'system:user';
UPDATE t_menu SET permission_code = 'ROLE_MANAGE' WHERE menu_code = 'system:role';
UPDATE t_menu SET permission_code = 'MENU_MANAGE' WHERE menu_code = 'system:menu';
UPDATE t_menu SET permission_code = 'PERMISSION_MANAGE' WHERE menu_code = 'system:permission';
UPDATE t_menu SET permission_code = 'COMPANY_MANAGE' WHERE menu_code = 'system:company';
UPDATE t_menu SET permission_code = 'SYSTEM_CONFIG' WHERE menu_code = 'system:settings';
UPDATE t_menu SET permission_code = 'SYSTEM_AUDIT' WHERE menu_code = 'system:audit';

-- 为财务管理菜单关联权限
UPDATE t_menu SET permission_code = 'ACCOUNT_VIEW' WHERE menu_code = 'finance:account';
UPDATE t_menu SET permission_code = 'CATEGORY_VIEW' WHERE menu_code = 'finance:category';
UPDATE t_menu SET permission_code = 'TRANSACTION_VIEW' WHERE menu_code = 'finance:transaction';
UPDATE t_menu SET permission_code = 'REPORT_VIEW' WHERE menu_code = 'finance:report';

-- =============================================
-- 6. 给超级管理员角色分配菜单管理权限
-- =============================================
INSERT INTO t_role_permission (role_id, permission_id)
SELECT r.id, p.id FROM t_role r, t_permission p
WHERE r.role_code = 'SUPER_ADMIN' AND p.permission_code LIKE 'MENU%'
ON DUPLICATE KEY UPDATE role_id = VALUES(role_id);

-- =============================================
-- 7. 给管理员角色分配菜单管理权限
-- =============================================
INSERT INTO t_role_permission (role_id, permission_id)
SELECT r.id, p.id FROM t_role r, t_permission p
WHERE r.role_code = 'ADMIN' AND p.permission_code LIKE 'MENU%'
ON DUPLICATE KEY UPDATE role_id = VALUES(role_id);

-- =============================================
-- 完成
-- =============================================
