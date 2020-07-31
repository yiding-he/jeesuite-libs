package com.jeesuite.mybatis.crud.builder;

import com.jeesuite.mybatis.crud.helper.ColumnMapper;
import com.jeesuite.mybatis.crud.helper.EntityHelper;
import org.apache.ibatis.mapping.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.apache.ibatis.session.Configuration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * <br>
 * Class Name   : AbstractSelectMethodBuilder
 *
 * @author jiangwei
 * @version 1.0.0
 * @since 2020年5月9日
 */
public abstract class AbstractSelectMethodBuilder extends AbstractMethodBuilder {

    @Override
    SqlCommandType sqlCommandType() {
        return SqlCommandType.SELECT;
    }

    @Override
    void setResultType(Configuration configuration, MappedStatement ms, Class<?> entityClass) {
        List<ResultMap> resultMaps = new ArrayList<ResultMap>();
        resultMaps.add(getResultMap(configuration, entityClass));
        MetaObject metaObject = SystemMetaObject.forObject(ms);
        metaObject.setValue("resultMaps", Collections.unmodifiableList(resultMaps));
    }

    /**
     * 生成当前实体的resultMap对象
     */
    public static ResultMap getResultMap(Configuration configuration, Class<?> entityClass) {
        List<ResultMapping> resultMappings = new ArrayList<ResultMapping>();

        Set<ColumnMapper> entityClassColumns = EntityHelper.getEntityMapper(entityClass).getColumnsMapper();
        for (ColumnMapper entityColumn : entityClassColumns) {
            ResultMapping.Builder builder = new ResultMapping.Builder(configuration, entityColumn.getProperty(), entityColumn.getColumn(), entityColumn.getJavaType());
            if (entityColumn.getJdbcType() != null) {
                builder.jdbcType(entityColumn.getJdbcType());
            }

            List<ResultFlag> flags = new ArrayList<ResultFlag>();
            if (entityColumn.isId()) {
                flags.add(ResultFlag.ID);
            }
            builder.flags(flags);
            builder.lazy(false);
            resultMappings.add(builder.build());
        }
        ResultMap.Builder builder = new ResultMap.Builder(configuration, "BaseResultMap", entityClass, resultMappings, true);
        return builder.build();
    }

}
