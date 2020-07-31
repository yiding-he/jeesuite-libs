package com.jeesuite.mybatis.core;

import com.jeesuite.mybatis.plugin.JeesuiteMybatisInterceptor;
import org.apache.ibatis.plugin.Invocation;

/**
 * mybatis插件拦截处理器接口
 *
 * @author <a href="mailto:vakinge@gmail.com">vakin</a>
 * @Copyright (c) 2015, jwww
 * @since 2015年12月7日
 */
public interface InterceptorHandler {

    void start(JeesuiteMybatisInterceptor context);

    void close();

    Object onInterceptor(Invocation invocation) throws Throwable;

    void onFinished(Invocation invocation, Object result);

    int interceptorOrder();


}
