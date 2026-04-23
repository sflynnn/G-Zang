package com.gzang.app.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.gzang.app.util.TenantContextHolder;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * MyBatis Plus 自动填充处理器 用于自动填充 create_time 和 update_time 字段
 *
 * @author G-Zang Team
 */
@Component
public class MyBatisPlusMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        this.strictInsertFill(metaObject, "createTime", LocalDateTime.class, LocalDateTime.now());
        this.strictInsertFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
        this.strictInsertFill(metaObject, "createBy", Long.class, getUserIdOrNull());
        this.strictInsertFill(metaObject, "updateBy", Long.class, getUserIdOrNull());
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        this.strictUpdateFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
        this.strictUpdateFill(metaObject, "updateBy", Long.class, getUserIdOrNull());
    }

    private Long getUserIdOrNull() {
        try {
            return TenantContextHolder.getUserId();
        } catch (Exception e) {
            return null;
        }
    }
}
