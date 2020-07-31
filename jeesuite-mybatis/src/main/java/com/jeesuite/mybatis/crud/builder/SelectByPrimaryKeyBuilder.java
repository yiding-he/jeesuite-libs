/**
 *
 */
package com.jeesuite.mybatis.crud.builder;

import com.jeesuite.mybatis.crud.CrudMethods;
import com.jeesuite.mybatis.crud.helper.ColumnMapper;
import com.jeesuite.mybatis.crud.helper.EntityMapper;
import com.jeesuite.mybatis.crud.helper.TableMapper;
import org.apache.ibatis.jdbc.SQL;

/**
 * 批量插入
 * @author <a href="mailto:vakinge@gmail.com">vakin</a>
 * @since 2018年11月22日
 */
public class SelectByPrimaryKeyBuilder extends AbstractSelectMethodBuilder {

    @Override
    String[] methodNames() {
        return new String[]{CrudMethods.selectByPrimaryKey.name()};
    }

    @Override
    String buildSQL(EntityMapper entityMapper, boolean selective) {
        // 从表注解里获取表名等信息
        TableMapper tableMapper = entityMapper.getTableMapper();
        ColumnMapper idColumn = entityMapper.getIdColumn();

        return new SQL()
                .SELECT("*")
                .FROM(tableMapper.getName())
                .WHERE(idColumn.getColumn() + "=#{" + idColumn.getProperty() + "}")
                .toString();
    }

}
