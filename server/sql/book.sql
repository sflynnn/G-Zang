-- =============================================
-- G-Zang 账本表
-- =============================================

CREATE TABLE IF NOT EXISTS `t_book` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_id` BIGINT NOT NULL COMMENT '所属用户ID',
    `company_id` BIGINT DEFAULT NULL COMMENT '所属公司ID（个人账本为NULL）',
    `name` VARCHAR(50) NOT NULL COMMENT '账本名称',
    `icon` VARCHAR(10) DEFAULT '📒' COMMENT '账本图标',
    `color` VARCHAR(20) DEFAULT '#0F4C5C' COMMENT '账本颜色',
    `currency` VARCHAR(10) DEFAULT 'CNY' COMMENT '货币代码',
    `currency_symbol` VARCHAR(10) DEFAULT '¥' COMMENT '货币符号',
    `type` VARCHAR(20) DEFAULT 'PERSONAL' COMMENT '账本类型: PERSONAL/FAMILY/TEAM/BUSINESS',
    `is_default` TINYINT(1) DEFAULT FALSE COMMENT '是否默认账本',
    `remark` VARCHAR(200) DEFAULT NULL COMMENT '备注',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_company_id` (`company_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='账本表';

-- =============================================
-- 给 t_transaction 添加 book_id 字段
-- =============================================
ALTER TABLE `t_transaction` ADD COLUMN `book_id` BIGINT DEFAULT NULL COMMENT '账本ID' AFTER `company_id`;
-- 使用存储过程确保索引不重复创建（MySQL 8.0.29 以下不支持 CREATE INDEX IF NOT EXISTS）
DROP PROCEDURE IF EXISTS `create_book_id_index`;
DELIMITER //
CREATE PROCEDURE `create_book_id_index`()
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM INFORMATION_SCHEMA.STATISTICS
        WHERE TABLE_SCHEMA = DATABASE()
        AND TABLE_NAME = 't_transaction'
        AND INDEX_NAME = 'idx_book_id'
    ) THEN
        CREATE INDEX `idx_book_id` ON `t_transaction`(`book_id`);
    END IF;
END //
DELIMITER ;
CALL `create_book_id_index`();
DROP PROCEDURE IF EXISTS `create_book_id_index`;

-- =============================================
-- 插入默认账本（用于测试）
-- =============================================

-- 先检查是否存在，如果不存在则插入
INSERT INTO `t_book` (`user_id`, `name`, `icon`, `color`, `currency`, `currency_symbol`, `type`, `is_default`, `remark`)
SELECT 1, '日常开销', '📒', '#0F4C5C', 'CNY', '¥', 'PERSONAL', TRUE, '默认账本'
WHERE NOT EXISTS (SELECT 1 FROM `t_book` WHERE `user_id` = 1 AND `name` = '日常开销');
