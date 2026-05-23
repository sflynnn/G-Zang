-- V12__add_system_config_table.sql
-- 系统配置表结构
-- 创建时间: 2026-05-22

-- 系统配置表
CREATE TABLE IF NOT EXISTS t_system_config (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '配置ID',
    config_group VARCHAR(50) NOT NULL COMMENT '配置分组',
    config_key VARCHAR(100) NOT NULL COMMENT '配置键',
    config_value TEXT COMMENT '配置值',
    config_type VARCHAR(20) DEFAULT 'string' COMMENT '配置类型 (string/number/boolean/json)',
    config_name VARCHAR(100) DEFAULT NULL COMMENT '配置名称',
    description VARCHAR(500) DEFAULT NULL COMMENT '配置描述',
    editable TINYINT(1) NOT NULL DEFAULT 1 COMMENT '是否可编辑 (1=是, 0=否)',
    sort_order INT DEFAULT 0 COMMENT '排序号',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_config_key (config_key),
    INDEX idx_config_group (config_group)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统配置表';

-- 初始化系统配置数据
INSERT INTO t_system_config (config_group, config_key, config_value, config_type, config_name, description, editable, sort_order) VALUES
-- 通用配置
('general', 'app_name', '归藏财务', 'string', '应用名称', '系统显示的应用名称', 1, 1),
('general', 'app_version', '1.0.0', 'string', '应用版本', '当前系统版本号', 0, 2),
('general', 'company_name', '归藏科技', 'string', '公司名称', '公司或组织名称', 1, 3),

-- 财务配置
('finance', 'default_currency', 'CNY', 'string', '默认货币', '系统默认使用的货币类型', 1, 10),
('finance', 'default_currency_symbol', '¥', 'string', '货币符号', '默认货币的符号', 1, 11),
('finance', 'decimal_places', '2', 'number', '小数位数', '金额计算时保留的小数位数', 1, 12),

-- 安全配置
('security', 'password_min_length', '6', 'number', '密码最小长度', '用户密码的最小长度要求', 1, 20),
('security', 'session_timeout', '7200', 'number', '会话超时时间', '会话超时时间(秒)', 1, 21),
('security', 'max_login_attempts', '5', 'number', '最大登录尝试', '密码错误最大次数', 1, 22),

-- 界面配置
('ui', 'theme', 'light', 'string', '主题', '系统主题 (light/dark/auto)', 1, 30),
('ui', 'language', 'zh-CN', 'string', '语言', '系统默认语言', 1, 31),
('ui', 'page_size', '20', 'number', '分页大小', '列表默认分页大小', 1, 32),
('ui', 'enable_animations', 'true', 'boolean', '启用动画', '是否启用界面动画效果', 1, 33);
