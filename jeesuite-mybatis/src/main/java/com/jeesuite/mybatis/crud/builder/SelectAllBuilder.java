/**
 *
 */
package com.jeesuite.mybatis.crud.builder;

import com.jeesuite.mybatis.crud.helper.EntityMapper;
import com.jeesuite.mybatis.crud.helper.TableMapper;
import org.apache.ibatis.jdbc.SQL;

/**
 * @author <a href="mailto:vakinge@gmail.com">vakin</a>
 * @since 2015年12月2日
 * @Copyright (c) 2015, jwww
 */
public class SelectAllBuilder extends AbstractSelectMethodBuilder {

    @Override
    String[] methodNames() {
        return new String[]{"selectAll"};
    }

    @Override
    String buildSQL(EntityMapper entityMapper, boolean selective) {
        TableMapper tableMapper = entityMapper.getTableMapper();
        return new SQL() {
            {
                SELECT("*");
                FROM(tableMapper.getName());
            }
        }.toString();
    }

}
