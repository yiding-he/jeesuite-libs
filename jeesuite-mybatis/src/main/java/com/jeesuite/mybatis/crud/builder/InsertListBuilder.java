/**
 *
 */
package com.jeesuite.mybatis.crud.builder;

import com.jeesuite.mybatis.crud.SqlTemplate;
import com.jeesuite.mybatis.crud.helper.ColumnMapper;
import com.jeesuite.mybatis.crud.helper.EntityMapper;
import com.jeesuite.mybatis.crud.helper.TableMapper;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.session.Configuration;

import java.util.Set;

/**
 * 批量插入
 * @author <a href="mailto:vakinge@gmail.com">vakin</a>
 * @since 2018年11月22日
 */
public class InsertListBuilder extends AbstractMethodBuilder {

    @Override
    SqlCommandType sqlCommandType() {
        return SqlCommandType.INSERT;
    }

    @Override
    String[] methodNames() {
        return new String[]{"insertList"};
    }

    @Override
    String buildSQL(EntityMapper entityMapper, boolean selective) {

        // 从表注解里获取表名等信息
        TableMapper table = entityMapper.getTableMapper();
        Set<ColumnMapper> columns = entityMapper.getColumnsMapper();

        StringBuilder fieldBuilder = new StringBuilder("(");
        StringBuilder prppertyBuilder = new StringBuilder("(");
        if (!entityMapper.autoId()) {
            fieldBuilder.append(entityMapper.getIdColumn().getColumn()).append(",");
            prppertyBuilder.append("#{item.").append(entityMapper.getIdColumn().getProperty()).append("},");
        }
        for (ColumnMapper column : columns) {
            if (column.isId() || !column.isInsertable()) {
                continue;
            }
            String fieldExpr = SqlTemplate.wrapIfTag(column.getProperty(), column.getColumn(), true);
            String propertyExpr = SqlTemplate.wrapIfTag(column.getProperty(), "#{item." + column.getProperty() + "}", true);
            fieldBuilder.append(fieldExpr);
            fieldBuilder.append(",");
            prppertyBuilder.append(propertyExpr);
            prppertyBuilder.append(",");
        }

        fieldBuilder.deleteCharAt(fieldBuilder.length() - 1);
        prppertyBuilder.deleteCharAt(prppertyBuilder.length() - 1);

        fieldBuilder.append(")");
        prppertyBuilder.append(")");
        String sql = String.format(SqlTemplate.BATCH_INSERT, table.getName(), fieldBuilder.toString(), prppertyBuilder.toString());
        return String.format(SqlTemplate.SCRIPT_TEMAPLATE, sql);
    }

    @Override
    void setResultType(Configuration configuration, MappedStatement statement, Class<?> entityClass) {
    }

}
