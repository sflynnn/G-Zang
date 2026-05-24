-- ============================================================
-- V15__add_transaction_extended_fields.sql
-- 交易表扩展字段，支持标签、转账目标账户、支付方式
-- 执行时间: 2026-05-24
-- ============================================================

USE g_zang;

-- 1. 添加目标账户ID（转账场景）
ALTER TABLE t_transaction 
ADD COLUMN target_account_id BIGINT NULL 
COMMENT '目标账户ID（转账类型交易使用）' 
AFTER account_id;

-- 2. 添加标签字段（JSON数组格式存储）
ALTER TABLE t_transaction 
ADD COLUMN tags JSON NULL 
COMMENT '标签列表，JSON数组格式如 ["餐饮", "午餐"]' 
AFTER remark;

-- 3. 添加支付方式字段
ALTER TABLE t_transaction 
ADD COLUMN payment_method VARCHAR(50) NULL 
COMMENT '支付方式：cash/card/wx/alipay/other' 
AFTER tags;

-- 4. 添加索引优化查询性能
-- 注意：如果索引已存在会报错，使用 CREATE INDEX IF NOT EXISTS 或手动检查
-- CREATE INDEX idx_transaction_book_month ON t_transaction(book_id, transaction_time);
-- CREATE INDEX idx_transaction_category ON t_transaction(category_id);
-- CREATE INDEX idx_transaction_target ON t_transaction(target_account_id);

-- 5. 验证字段添加
SELECT COLUMN_NAME, DATA_TYPE, IS_NULLABLE, COLUMN_COMMENT 
FROM INFORMATION_SCHEMA.COLUMNS 
WHERE TABLE_SCHEMA = 'g_zang' 
AND TABLE_NAME = 't_transaction' 
AND COLUMN_NAME IN ('target_account_id', 'tags', 'payment_method');

-- 输出完成信息
SELECT 'V15__add_transaction_extended_fields.sql 执行完成' AS status;
