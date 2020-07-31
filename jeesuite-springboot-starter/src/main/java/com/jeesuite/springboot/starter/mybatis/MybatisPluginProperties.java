/**
 *
 */
package com.jeesuite.springboot.starter.mybatis;

import com.jeesuite.mybatis.MybatisConfigs;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Properties;

/**
 * @author <a href="mailto:vakinge@gmail.com">vakin</a>
 * @since 2016年12月31日
 */
@ConfigurationProperties(prefix = "jeesuite.mybatis")
public class MybatisPluginProperties {

    private Properties properties = new Properties();

    public void setCacheEnabled(boolean cacheEnabled) {
        properties.setProperty(MybatisConfigs.CACHE_ENABLED, String.valueOf(cacheEnabled));
    }

    public void setRwRouteEnabled(boolean rwRouteEnabled) {
        properties.setProperty(MybatisConfigs.RW_ROUTE_ENABLED, String.valueOf(rwRouteEnabled));
    }

    public void setPaginationEnabled(boolean paginationEnabled) {
        properties.setProperty(MybatisConfigs.PAGINATION_ENABLED, String.valueOf(paginationEnabled));
    }

    public void setDbType(String dbType) {
        properties.setProperty(MybatisConfigs.DB_TYPE, dbType);
    }

    public void setCrudDriver(String crudDriver) {
        properties.setProperty(MybatisConfigs.CRUD_DRIVER, crudDriver);
    }

    public void setNullValueCache(boolean nullValueCache) {
        properties.setProperty(MybatisConfigs.CACHE_NULL_VALUE, String.valueOf(nullValueCache));
    }

    public void setCacheExpireSeconds(long cacheExpireSeconds) {
        properties.setProperty(MybatisConfigs.CACHE_EXPIRE_SECONDS, String.valueOf(cacheExpireSeconds));
    }

    public void setDynamicExpire(boolean dynamicExpire) {
        properties.setProperty(MybatisConfigs.CACHE_DYNAMIC_EXPIRE, String.valueOf(dynamicExpire));
    }

    public void setInterceptorHandlerClass(String interceptorHandlerClass) {
        properties.setProperty(MybatisConfigs.INTERCEPTOR_HANDLERCLASS, interceptorHandlerClass);
    }

    public void setTenantModeEnabled(boolean tenantModeEnabled) {
        properties.setProperty(MybatisConfigs.TENANT_MODE_ENABLED, String.valueOf(tenantModeEnabled));
    }

    public Properties getProperties() {
        return properties;
    }

}
