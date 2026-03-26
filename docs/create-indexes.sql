-- G-Zang 数据库索引创建脚本
-- 此脚本可以安全地重复执行，不会因索引已存在而报错

USE g_zang;

-- ===========================================
-- 索引创建（使用IF NOT EXISTS避免重复创建）
-- ===========================================

-- 用户表索引
CREATE UNIQUE INDEX IF NOT EXISTS uk_username ON t_user (username);
CREATE INDEX IF NOT EXISTS idx_role_id ON t_user (role_id);
CREATE INDEX IF NOT EXISTS idx_company_id ON t_user (company_id);
CREATE INDEX IF NOT EXISTS idx_status ON t_user (status);

-- 交易记录表索引
CREATE INDEX IF NOT EXISTS idx_user_transaction_time ON t_transaction (user_id, transaction_time);
CREATE INDEX IF NOT EXISTS idx_company_transaction_time ON t_transaction (company_id, transaction_time);
CREATE INDEX IF NOT EXISTS idx_category_id ON t_transaction (category_id);
CREATE INDEX IF NOT EXISTS idx_account_id ON t_transaction (account_id);
CREATE INDEX IF NOT EXISTS idx_related_business_id ON t_transaction (related_business_id);

-- 操作日志表索引
CREATE INDEX IF NOT EXISTS idx_user_operation_time ON t_operation_log (user_id, operation_time);
CREATE INDEX IF NOT EXISTS idx_operation_type ON t_operation_log (operation_type);
CREATE INDEX IF NOT EXISTS idx_ip_address ON t_operation_log (ip_address);

-- 预算表索引
CREATE INDEX IF NOT EXISTS idx_user_budget ON t_budget (user_id, budget_year, budget_month);
CREATE INDEX IF NOT EXISTS idx_company_budget ON t_budget (company_id, budget_year, budget_month);
CREATE INDEX IF NOT EXISTS idx_category_budget ON t_budget (category_id, budget_year);

-- 其他性能优化索引
CREATE UNIQUE INDEX IF NOT EXISTS uk_user_device_token ON t_user_device (user_id, device_token);
CREATE INDEX IF NOT EXISTS idx_sync_status_user ON t_offline_transaction (sync_status, user_id);
CREATE INDEX IF NOT EXISTS idx_user_read_status ON t_notification_record (user_id, is_read, create_time);

SELECT '索引创建完成！' as message;
