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

import com.jeesuite.mybatis.MybatisConfigs;
import com.jeesuite.mybatis.crud.GeneralSqlGenerator;
import com.jeesuite.mybatis.parser.EntityInfo;
import com.jeesuite.mybatis.parser.MybatisMapperParser;
import com.jeesuite.mybatis.plugin.JeesuiteMybatisInterceptor;
import org.apache.ibatis.session.Configuration;

import java.lang.reflect.Method;
import java.util.List;

/**
 * @author <a href="mailto:vakinge@gmail.com">vakin</a>
 * @since 2018年11月22日
 */
public class JeesuiteMybatisRegistry {

    public static void register(String group, Configuration configuration) throws Exception {
        if ("default".equals(MybatisConfigs.getCrudDriver(group))) {
            new GeneralSqlGenerator(group, configuration).generate();
        } else if ("mapper3".equals(MybatisConfigs.getCrudDriver(group))) {
            Class<?> helperClazz = Class.forName("tk.mybatis.mapper.mapperhelper.MapperHelper");
            Object helper = helperClazz.newInstance();
            Class<?> configClazz = Class.forName("tk.mybatis.mapper.entity.Config");
            Object config = configClazz.newInstance();
            Method method = configClazz.getDeclaredMethod("setNotEmpty", boolean.class);
            method.invoke(config, false);
            method = helperClazz.getDeclaredMethod("setConfig", configClazz);
            method.invoke(helper, config);

            method = helperClazz.getDeclaredMethod("registerMapper", Class.class);
            List<EntityInfo> entityInfos = MybatisMapperParser.getEntityInfos(group);
            for (EntityInfo entityInfo : entityInfos) {
                method.invoke(helper, entityInfo.getMapperClass());
            }

            method = helperClazz.getDeclaredMethod("processConfiguration", Configuration.class);
            method.invoke(helper, configuration);
        } else {
            new GeneralSqlGenerator(group, configuration).generate();
        }
        //注册拦截器
        String[] hanlderNames = MybatisConfigs.getHanlderNames(group);
        if (hanlderNames.length > 0) {
            JeesuiteMybatisInterceptor interceptor = new JeesuiteMybatisInterceptor(group, hanlderNames);
            configuration.addInterceptor(interceptor);
            interceptor.afterRegister();
        }

    }
}
