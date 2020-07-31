/**
 *
 */
package com.jeesuite.mybatis.crud;

import com.jeesuite.mybatis.crud.builder.*;
import com.jeesuite.mybatis.parser.EntityInfo;
import com.jeesuite.mybatis.parser.MybatisMapperParser;
import org.apache.ibatis.scripting.LanguageDriver;
import org.apache.ibatis.session.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author <a href="mailto:vakinge@gmail.com">vakin</a>
 * @since 2016年2月2日
 * @Copyright (c) 2015, jwww
 */
public class GeneralSqlGenerator {

    private static final Logger log = LoggerFactory.getLogger(GeneralSqlGenerator.class);

    private LanguageDriver languageDriver;

    private Configuration configuration;

    private String group;

    public GeneralSqlGenerator(String group, Configuration configuration) {
        this.group = group;
        this.configuration = configuration;
        this.languageDriver = configuration.getDefaultScriptingLanguageInstance();
    }

    public void generate() {
        if (languageDriver == null) languageDriver = configuration.getDefaultScriptingLanguageInstance();
        List<EntityInfo> entityInfos = MybatisMapperParser.getEntityInfos(group);
        for (EntityInfo entity : entityInfos) {
            new InsertBuilder().build(configuration, languageDriver, entity);
            new InsertListBuilder().build(configuration, languageDriver, entity);
            new DeleteByPrimaryKeyBuilder().build(configuration, languageDriver, entity);
            new UpdateBuilder().build(configuration, languageDriver, entity);
            new SelectAllBuilder().build(configuration, languageDriver, entity);
            new SelectByPrimaryKeyBuilder().build(configuration, languageDriver, entity);
            new SelectByPrimaryKeysBuilder().build(configuration, languageDriver, entity);
            new CountAllBuilder().build(configuration, languageDriver, entity);
            log.info(" >> generate autoCrud for:[{}] finish", entity.getEntityClass().getName());
        }
    }
}
