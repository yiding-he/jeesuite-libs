package com.jeesuite.rest.filter;

import com.jeesuite.rest.filter.annotation.ResponseFormat;
import com.jeesuite.rest.filter.annotation.ResponseFormat.FormatType;

import javax.ws.rs.container.DynamicFeature;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.FeatureContext;
import java.lang.reflect.Method;

/**
 * @author <a href="mailto:vakinge@gmail.com">vakin</a>
 * @since 2016年2月25日
 */
public class DefaultFilterDynamicFeature implements DynamicFeature {

    @Override
    public void configure(ResourceInfo resourceInfo, FeatureContext context) {
        // 获取资源方法
        Method resourceMethod = resourceInfo.getResourceMethod();

        if (resourceMethod != null) {

            // 获取FormatJson注解
            ResponseFormat formatJson = resourceMethod.getAnnotation(ResponseFormat.class);

            if (formatJson == null || formatJson.type().equals(FormatType.JSON)) {
                context.register(DefaultWebFilter.class);
            }

        }
    }
}
