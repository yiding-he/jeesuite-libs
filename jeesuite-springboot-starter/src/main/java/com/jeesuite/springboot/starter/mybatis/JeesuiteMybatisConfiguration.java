package com.jeesuite.springboot.starter.mybatis;

import com.jeesuite.mybatis.MybatisConfigs;
import com.jeesuite.mybatis.datasource.MutiRouteDataSource;
import com.jeesuite.mybatis.parser.MybatisMapperParser;
import com.jeesuite.mybatis.spring.JeesuiteMybatisRegistry;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternUtils;

@Configuration
@EnableConfigurationProperties(MybatisPluginProperties.class)
@ConditionalOnClass(MutiRouteDataSource.class)
@AutoConfigureAfter(MybatisAutoConfiguration.class)
public class JeesuiteMybatisConfiguration implements InitializingBean {

    @Autowired
    private SqlSessionFactory sqlSessionFactory;

    @Autowired
    private MybatisPluginProperties properties;

    @Value("${mybatis.mapper-locations}")
    private String mapperLocations;

    @Override
    public void afterPropertiesSet() throws Exception {
        Resource[] resources = ResourcePatternUtils.getResourcePatternResolver(new DefaultResourceLoader()).getResources(mapperLocations);
        String group = "default";
        MybatisMapperParser.addMapperLocations(group, resources);
        MybatisConfigs.addProperties(group, properties.getProperties());
        JeesuiteMybatisRegistry.register(group, sqlSessionFactory.getConfiguration());
    }

}
