package com.gzang.app.config;

import com.baomidou.mybatisplus.core.toolkit.PluginUtils;
import com.baomidou.mybatisplus.extension.plugins.inner.InnerInterceptor;
import com.gzang.app.util.TenantContextHolder;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.update.Update;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

/**
 * 多租户拦截器 自动在 SQL 查询中添加 user_id 和 company_id 条件
 *
 * @author G-Zang Team
 */
public class TenantInterceptor implements InnerInterceptor {

    /**
     * 需要进行多租户过滤的表（需要 user_id 过滤）
     */
    private static final List<String> TENANT_TABLES = Arrays.asList(
            "t_user", "t_account", "t_transaction", "t_category",
            "t_budget", "t_business_repair_order", "t_cost_center",
            "t_operation_log", "t_file_upload", "t_offline_transaction",
            "t_notification_record"
    );

    /**
     * 公司级别的表（需要 company_id 过滤）
     */
    private static final List<String> COMPANY_TABLES = Arrays.asList(
            "t_account", "t_transaction", "t_category",
            "t_budget", "t_business_repair_order", "t_cost_center",
            "t_operation_log", "t_file_upload", "t_offline_transaction",
            "t_notification_record", "t_user_role", "t_role_data_scope",
            "t_field_permission"
    );

    /**
     * 不进行租户过滤的表（如系统表）
     */
    private static final List<String> SKIP_TABLES = Arrays.asList(
            "t_role", "t_permission", "t_role_permission",
            "t_company", "t_system_config", "t_user_device"
    );

    @Override
    public void beforeQuery(Executor executor, MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler, BoundSql boundSql) throws SQLException {
        if (isTenantTable(ms)) {
            String sql = boundSql.getSql();
            String newSql = addTenantCondition(sql, ms);
            if (!sql.equals(newSql)) {
                PluginUtils.MPBoundSql mpBoundSql = PluginUtils.mpBoundSql(boundSql);
                mpBoundSql.sql(newSql);
            }
        }
    }

    @Override
    public void beforeUpdate(Executor executor, MappedStatement ms, Object parameter) throws SQLException {
        if (isTenantTable(ms)) {
            BoundSql boundSql = ms.getBoundSql(parameter);
            String sql = boundSql.getSql();
            String newSql = addTenantCondition(sql, ms);
            if (!sql.equals(newSql)) {
                PluginUtils.MPBoundSql mpBoundSql = PluginUtils.mpBoundSql(boundSql);
                mpBoundSql.sql(newSql);
            }
        }
    }

    /**
     * 判断是否为需要多租户过滤的表
     */
    private boolean isTenantTable(MappedStatement ms) {
        String tableName = getTableName(ms);
        // 跳过不需要租户过滤的系统表
        if (SKIP_TABLES.contains(tableName)) {
            return false;
        }
        return TENANT_TABLES.contains(tableName);
    }

    /**
     * 从 MappedStatement 中获取表名
     */
    private String getTableName(MappedStatement ms) {
        String id = ms.getId();
        // 从 mapper 方法名中提取表名，例如：com.gzang.app.mapper.UserMapper.selectById -> t_user
        String className = id.substring(0, id.lastIndexOf("."));
        String mapperName = className.substring(className.lastIndexOf(".") + 1);
        return "t_" + mapperName.toLowerCase().replace("mapper", "");
    }

    /**
     * 添加多租户条件
     */
    private String addTenantCondition(String sql, MappedStatement ms) {
        try {
            Statement statement = CCJSqlParserUtil.parse(sql);

            if (statement instanceof Select select) {
                return addTenantConditionToSelect(select, ms);
            } else if (statement instanceof Update update) {
                return addTenantConditionToUpdate(update, ms);
            }
        } catch (JSQLParserException | RuntimeException e) {
            // 解析SQL失败，将返回原SQL
            return sql;
        }

        return sql;
    }

    /**
     * 为 SELECT 语句添加多租户条件
     */
    private String addTenantConditionToSelect(Select select, MappedStatement ms) {
        if (select.getSelectBody() instanceof PlainSelect plainSelect) {
            Expression where = plainSelect.getWhere();

            Expression tenantCondition = buildTenantCondition(ms);
            if (tenantCondition != null) {
                if (where == null) {
                    plainSelect.setWhere(tenantCondition);
                } else {
                    plainSelect.setWhere(new AndExpression(where, tenantCondition));
                }
            }
        }
        return select.toString();
    }

    /**
     * 为 UPDATE 语句添加多租户条件
     */
    private String addTenantConditionToUpdate(Update update, MappedStatement ms) {
        Expression where = update.getWhere();

        Expression tenantCondition = buildTenantCondition(ms);
        if (tenantCondition != null) {
            if (where == null) {
                update.setWhere(tenantCondition);
            } else {
                update.setWhere(new AndExpression(where, tenantCondition));
            }
        }
        return update.toString();
    }

    /**
     * 构建多租户条件
     */
    private Expression buildTenantCondition(MappedStatement ms) {
        String tableName = getTableName(ms);
        Long userId = TenantContextHolder.getCurrentUserId();
        Long companyId = TenantContextHolder.getCurrentCompanyId();

        Expression userCondition = null;
        Expression companyCondition = null;

        // 所有表都需要 user_id 条件
        if (userId != null) {
            userCondition = new EqualsTo(new Column("user_id"), new LongValue(userId));
        }

        // 公司级别的表需要 company_id 条件
        if (COMPANY_TABLES.contains(tableName) && companyId != null) {
            companyCondition = new EqualsTo(new Column("company_id"), new LongValue(companyId));
        }

        // 组合条件
        if (userCondition != null && companyCondition != null) {
            return new AndExpression(userCondition, companyCondition);
        } else if (userCondition != null) {
            return userCondition;
        } else if (companyCondition != null) {
            return companyCondition;
        }

        return null;
    }
}
