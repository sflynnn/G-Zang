-- G-Zang 数据库建表脚本
-- 数据库：MySQL 8.0+
-- 字符集：utf8mb4
-- 存储引擎：InnoDB

-- 创建数据库（如果不存在）
CREATE DATABASE IF NOT EXISTS g_zang CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE g_zang;

-- ===========================================
-- 1. 用户与权限相关表
-- ===========================================

-- 用户表
CREATE TABLE IF NOT EXISTS t_user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键，用户ID',
    username VARCHAR(64) NOT NULL COMMENT '用户名（手机号/邮箱）',
    password VARCHAR(255) NOT NULL COMMENT '密码（加密存储）',
    nickname VARCHAR(64) NULL COMMENT '用户昵称',
    avatar VARCHAR(255) NULL COMMENT '用户头像URL',
    role_id BIGINT NOT NULL COMMENT '角色ID',
    company_id BIGINT NULL COMMENT '所属公司ID（个人用户为NULL）',
    status TINYINT(1) NOT NULL DEFAULT 1 COMMENT '状态 (0:禁用, 1:正常)',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 角色表
CREATE TABLE IF NOT EXISTS t_role (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键，角色ID',
    role_name VARCHAR(64) NOT NULL COMMENT '角色名称（如：管理员、财务、普通用户）',
    role_code VARCHAR(64) NOT NULL UNIQUE COMMENT '角色编码',
    description VARCHAR(255) NULL COMMENT '角色描述'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色表';

-- 权限表
CREATE TABLE IF NOT EXISTS t_permission (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键，权限ID',
    permission_name VARCHAR(64) NOT NULL COMMENT '权限名称（如：用户管理、记账录入）',
    permission_code VARCHAR(64) NOT NULL UNIQUE COMMENT '权限编码'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='权限表';

-- 角色权限关联表
CREATE TABLE IF NOT EXISTS t_role_permission (
    role_id BIGINT NOT NULL COMMENT '角色ID',
    permission_id BIGINT NOT NULL COMMENT '权限ID',
    PRIMARY KEY (role_id, permission_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色权限关联表';

-- ===========================================
-- 2. 公司与账本相关表
-- ===========================================

-- 公司表
CREATE TABLE IF NOT EXISTS t_company (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键，公司ID',
    company_name VARCHAR(128) NOT NULL COMMENT '公司名称',
    admin_user_id BIGINT NOT NULL COMMENT '公司管理员用户ID',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='公司表';

-- 账户表
CREATE TABLE IF NOT EXISTS t_account (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键，账户ID',
    user_id BIGINT NOT NULL COMMENT '所属用户ID',
    company_id BIGINT NULL COMMENT '所属公司ID（个人账户为NULL）',
    account_name VARCHAR(64) NOT NULL COMMENT '账户名称（如：现金、支付宝、招商银行）',
    account_type TINYINT(1) NOT NULL DEFAULT 1 COMMENT '账户类型 (1:现金, 2:银行卡, 3:电子支付)',
    balance DECIMAL(18,2) NOT NULL DEFAULT 0.00 COMMENT '当前余额',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='账户表';

-- 记账分类表
CREATE TABLE IF NOT EXISTS t_category (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键，分类ID',
    user_id BIGINT NULL COMMENT '所属用户ID（公司分类为NULL）',
    company_id BIGINT NULL COMMENT '所属公司ID（个人分类为NULL）',
    category_name VARCHAR(64) NOT NULL COMMENT '分类名称（如：餐饮、交通、配件采购）',
    parent_id BIGINT NOT NULL DEFAULT 0 COMMENT '父分类ID（0为一级分类）',
    type TINYINT(1) NOT NULL DEFAULT 1 COMMENT '类型 (1:收入, 2:支出)',
    is_system TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否系统预设 (0:否, 1:是)'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='记账分类表';

-- ===========================================
-- 3. 交易记录相关表
-- ===========================================

-- 交易记录表
CREATE TABLE IF NOT EXISTS t_transaction (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键，交易ID',
    user_id BIGINT NOT NULL COMMENT '记录用户ID',
    company_id BIGINT NULL COMMENT '所属公司ID（个人交易为NULL）',
    amount DECIMAL(18,2) NOT NULL DEFAULT 0.00 COMMENT '交易金额',
    type TINYINT(1) NOT NULL DEFAULT 1 COMMENT '交易类型 (1:收入, 2:支出)',
    category_id BIGINT NOT NULL COMMENT '分类ID',
    account_id BIGINT NOT NULL COMMENT '账户ID',
    transaction_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '交易发生时间',
    remark VARCHAR(500) NULL COMMENT '备注',
    related_business_id VARCHAR(128) NULL COMMENT '关联业务ID（如维修单号）',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='交易记录表';

-- ===========================================
-- 4. 系统管理相关表
-- ===========================================

-- 系统配置表
CREATE TABLE IF NOT EXISTS t_system_config (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键，配置ID',
    config_key VARCHAR(128) NOT NULL UNIQUE COMMENT '配置键',
    config_value TEXT NULL COMMENT '配置值',
    config_type VARCHAR(32) NOT NULL COMMENT '配置类型',
    description VARCHAR(255) NULL COMMENT '配置描述',
    is_system TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否系统配置',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统配置表';

-- 操作日志表
CREATE TABLE IF NOT EXISTS t_operation_log (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键，日志ID',
    user_id BIGINT NULL COMMENT '操作用户ID',
    username VARCHAR(64) NULL COMMENT '操作用户名',
    operation_type VARCHAR(32) NOT NULL COMMENT '操作类型',
    operation_desc VARCHAR(255) NULL COMMENT '操作描述',
    request_method VARCHAR(16) NULL COMMENT '请求方法',
    request_url VARCHAR(500) NULL COMMENT '请求URL',
    request_params TEXT NULL COMMENT '请求参数',
    response_code INT NULL COMMENT '响应状态码',
    response_msg VARCHAR(500) NULL COMMENT '响应消息',
    ip_address VARCHAR(64) NULL COMMENT 'IP地址',
    user_agent VARCHAR(500) NULL COMMENT '用户代理',
    operation_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
    duration BIGINT NULL COMMENT '执行时长(毫秒)'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='操作日志表';

-- 文件上传表
CREATE TABLE IF NOT EXISTS t_file_upload (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键，文件ID',
    file_name VARCHAR(255) NOT NULL COMMENT '文件原名',
    file_path VARCHAR(500) NOT NULL COMMENT '文件路径',
    file_size BIGINT NOT NULL COMMENT '文件大小(字节)',
    file_type VARCHAR(32) NOT NULL COMMENT '文件类型',
    file_md5 VARCHAR(64) NULL COMMENT '文件MD5',
    mime_type VARCHAR(128) NULL COMMENT 'MIME类型',
    upload_user_id BIGINT NULL COMMENT '上传用户ID',
    upload_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '上传时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文件上传表';

-- ===========================================
-- 5. 预算与成本管理表
-- ===========================================

-- 预算表
CREATE TABLE IF NOT EXISTS t_budget (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键，预算ID',
    user_id BIGINT NULL COMMENT '所属用户ID',
    company_id BIGINT NULL COMMENT '所属公司ID',
    budget_name VARCHAR(128) NOT NULL COMMENT '预算名称',
    budget_type VARCHAR(32) NOT NULL COMMENT '预算类型(annual/monthly/category)',
    budget_year INT NOT NULL COMMENT '预算年份',
    budget_month INT NULL COMMENT '预算月份',
    category_id BIGINT NULL COMMENT '关联分类ID',
    budget_amount DECIMAL(18,2) NOT NULL DEFAULT 0.00 COMMENT '预算金额',
    used_amount DECIMAL(18,2) NOT NULL DEFAULT 0.00 COMMENT '已使用金额',
    warning_percent DECIMAL(5,2) NOT NULL DEFAULT 80.00 COMMENT '预警百分比',
    status TINYINT(1) NOT NULL DEFAULT 1 COMMENT '状态(0禁用1启用)',
    create_user_id BIGINT NOT NULL COMMENT '创建用户ID',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='预算表';

-- 成本中心表
CREATE TABLE IF NOT EXISTS t_cost_center (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键，成本中心ID',
    company_id BIGINT NOT NULL COMMENT '所属公司ID',
    cost_center_code VARCHAR(64) NOT NULL COMMENT '成本中心编码',
    cost_center_name VARCHAR(128) NOT NULL COMMENT '成本中心名称',
    parent_id BIGINT NOT NULL DEFAULT 0 COMMENT '父级成本中心ID',
    manager_user_id BIGINT NULL COMMENT '负责人用户ID',
    budget_amount DECIMAL(18,2) NOT NULL DEFAULT 0.00 COMMENT '预算金额',
    description VARCHAR(255) NULL COMMENT '描述',
    status TINYINT(1) NOT NULL DEFAULT 1 COMMENT '状态(0禁用1启用)',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='成本中心表';

-- ===========================================
-- 6. 业务关联表
-- ===========================================

-- 维修工单表
CREATE TABLE IF NOT EXISTS t_business_repair_order (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键，工单ID',
    company_id BIGINT NOT NULL COMMENT '所属公司ID',
    order_no VARCHAR(64) NOT NULL COMMENT '工单编号',
    customer_name VARCHAR(128) NOT NULL COMMENT '客户姓名',
    customer_phone VARCHAR(32) NULL COMMENT '客户电话',
    vehicle_info VARCHAR(255) NULL COMMENT '车辆信息',
    repair_type VARCHAR(64) NOT NULL COMMENT '维修类型',
    repair_status TINYINT(1) NOT NULL DEFAULT 1 COMMENT '维修状态(1待修2进行中3完成)',
    estimated_amount DECIMAL(18,2) NOT NULL DEFAULT 0.00 COMMENT '预估金额',
    actual_amount DECIMAL(18,2) NOT NULL DEFAULT 0.00 COMMENT '实际金额',
    repair_user_id BIGINT NULL COMMENT '维修人员ID',
    create_user_id BIGINT NOT NULL COMMENT '创建用户ID',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='维修工单表';

-- 维修成本明细表
CREATE TABLE IF NOT EXISTS t_business_repair_cost (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键，成本明细ID',
    repair_order_id BIGINT NOT NULL COMMENT '维修工单ID',
    cost_type VARCHAR(32) NOT NULL COMMENT '成本类型(parts/labor)',
    cost_name VARCHAR(128) NOT NULL COMMENT '成本名称',
    cost_amount DECIMAL(18,2) NOT NULL DEFAULT 0.00 COMMENT '成本金额',
    cost_center_id BIGINT NULL COMMENT '成本中心ID',
    remark VARCHAR(255) NULL COMMENT '备注',
    create_user_id BIGINT NOT NULL COMMENT '创建用户ID',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='维修成本明细表';

-- ===========================================
-- 7. 移动端专用表
-- ===========================================

-- 用户设备表
CREATE TABLE IF NOT EXISTS t_user_device (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键，设备ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    device_token VARCHAR(255) NOT NULL COMMENT '设备推送令牌',
    device_type VARCHAR(32) NOT NULL COMMENT '设备类型(iOS/Android)',
    device_model VARCHAR(128) NULL COMMENT '设备型号',
    os_version VARCHAR(32) NULL COMMENT '操作系统版本',
    app_version VARCHAR(32) NULL COMMENT '应用版本',
    is_active TINYINT(1) NOT NULL DEFAULT 1 COMMENT '是否活跃',
    last_login_time DATETIME NULL COMMENT '最后登录时间',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户设备表';

-- 离线交易记录表
CREATE TABLE IF NOT EXISTS t_offline_transaction (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键，离线记录ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    device_id VARCHAR(64) NOT NULL COMMENT '设备唯一标识',
    transaction_data JSON NOT NULL COMMENT '交易数据JSON',
    sync_status TINYINT(1) NOT NULL DEFAULT 0 COMMENT '同步状态(0未同步1已同步2同步失败)',
    sync_time DATETIME NULL COMMENT '同步时间',
    sync_attempts INT NOT NULL DEFAULT 0 COMMENT '同步尝试次数',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='离线交易记录表';

-- 通知记录表
CREATE TABLE IF NOT EXISTS t_notification_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键，通知ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    notification_type VARCHAR(32) NOT NULL COMMENT '通知类型',
    title VARCHAR(128) NOT NULL COMMENT '通知标题',
    content TEXT NOT NULL COMMENT '通知内容',
    is_read TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否已读',
    read_time DATETIME NULL COMMENT '阅读时间',
    send_status TINYINT(1) NOT NULL DEFAULT 0 COMMENT '发送状态(0待发送1已发送2发送失败)',
    send_time DATETIME NULL COMMENT '发送时间',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='通知记录表';

-- ===========================================
-- 8. 智能识别表
-- ===========================================

-- 语音识别日志表
CREATE TABLE IF NOT EXISTS t_voice_recognition_log (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键，识别ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    audio_file_id BIGINT NULL COMMENT '音频文件ID',
    recognized_text TEXT NULL COMMENT '识别出的文本',
    parsed_amount DECIMAL(18,2) NULL COMMENT '解析出的金额',
    parsed_category VARCHAR(64) NULL COMMENT '解析出的分类',
    parsed_type TINYINT(1) NULL COMMENT '解析出的类型(1收入2支出)',
    confidence DECIMAL(5,2) NULL COMMENT '识别置信度',
    recognition_status TINYINT(1) NOT NULL DEFAULT 1 COMMENT '识别状态(0失败1成功)',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='语音识别日志表';

-- OCR识别日志表
CREATE TABLE IF NOT EXISTS t_ocr_recognition_log (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键，识别ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    image_file_id BIGINT NULL COMMENT '图片文件ID',
    recognized_text TEXT NULL COMMENT '识别出的文本',
    parsed_amount DECIMAL(18,2) NULL COMMENT '解析出的金额',
    parsed_merchant VARCHAR(128) NULL COMMENT '解析出的商家',
    parsed_items JSON NULL COMMENT '解析出的商品明细',
    confidence DECIMAL(5,2) NULL COMMENT '识别置信度',
    recognition_status TINYINT(1) NOT NULL DEFAULT 1 COMMENT '识别状态(0失败1成功)',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='OCR识别日志表';

-- ===========================================
-- 9. 索引说明
-- ===========================================
-- 注意：索引将在单独的脚本中创建 (docs/create-indexes.sql)
-- 这样可以避免重复执行时的错误

-- ===========================================
-- 10. 创建外键约束（可选，根据需要启用）
-- ===========================================

-- 注意：生产环境中可根据需要添加外键约束，但会影响性能和灵活性
-- 以下是可选的外键约束定义：

-- ALTER TABLE t_user ADD CONSTRAINT fk_user_role FOREIGN KEY (role_id) REFERENCES t_role(id);
-- ALTER TABLE t_user ADD CONSTRAINT fk_user_company FOREIGN KEY (company_id) REFERENCES t_company(id);
-- ALTER TABLE t_role_permission ADD CONSTRAINT fk_rp_role FOREIGN KEY (role_id) REFERENCES t_role(id);
-- ALTER TABLE t_role_permission ADD CONSTRAINT fk_rp_permission FOREIGN KEY (permission_id) REFERENCES t_permission(id);
-- ALTER TABLE t_account ADD CONSTRAINT fk_account_user FOREIGN KEY (user_id) REFERENCES t_user(id);
-- ALTER TABLE t_account ADD CONSTRAINT fk_account_company FOREIGN KEY (company_id) REFERENCES t_company(id);
-- ALTER TABLE t_transaction ADD CONSTRAINT fk_transaction_user FOREIGN KEY (user_id) REFERENCES t_user(id);
-- ALTER TABLE t_transaction ADD CONSTRAINT fk_transaction_company FOREIGN KEY (company_id) REFERENCES t_company(id);
-- ALTER TABLE t_transaction ADD CONSTRAINT fk_transaction_category FOREIGN KEY (category_id) REFERENCES t_category(id);
-- ALTER TABLE t_transaction ADD CONSTRAINT fk_transaction_account FOREIGN KEY (account_id) REFERENCES t_account(id);
