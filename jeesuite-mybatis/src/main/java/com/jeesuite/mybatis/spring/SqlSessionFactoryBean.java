/*
 * Copyright 2016-2018 www.jeesuite.com.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.jeesuite.mybatis.spring;

import com.jeesuite.common.util.ResourceUtils;
import com.jeesuite.mybatis.MybatisConfigs;
import com.jeesuite.mybatis.parser.MybatisMapperParser;
import com.jeesuite.spring.InstanceFactory;
import com.jeesuite.spring.SpringInstanceProvider;
import org.apache.ibatis.session.Configuration;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.io.Resource;

/**
 * @author <a href="mailto:vakinge@gmail.com">vakin</a>
 * @since 2018年11月22日
 */
public class SqlSessionFactoryBean extends org.mybatis.spring.SqlSessionFactoryBean implements ApplicationContextAware {

    private String groupName = "default";

    private Resource[] mapperLocations;

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    @Override
    public void setMapperLocations(Resource[] mapperLocations) {
        super.setMapperLocations(mapperLocations);
        this.mapperLocations = mapperLocations;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        super.afterPropertiesSet();
        MybatisMapperParser.addMapperLocations(groupName, mapperLocations);
        String prefix = "default".equals(groupName) ? "jeesuite.mybatis" : groupName + ".jeesuite.mybatis";
        MybatisConfigs.addProperties(groupName, ResourceUtils.getAllProperties(prefix));
        Configuration configuration = getObject().getConfiguration();
        JeesuiteMybatisRegistry.register(groupName, configuration);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        InstanceFactory.setInstanceProvider(new SpringInstanceProvider(applicationContext));
    }


}
