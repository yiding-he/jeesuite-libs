package com.jeesuite.mybatis.plugin.shard;

import com.jeesuite.mybatis.core.InterceptorHandler;
import com.jeesuite.mybatis.plugin.JeesuiteMybatisInterceptor;
import org.apache.ibatis.plugin.Invocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

/**
 * 分库自动路由处理
 *
 * @author <a href="mailto:vakinge@gmail.com">vakin</a>
 * @Copyright (c) 2015, jwww
 * @since 2015年12月7日
 */
public class TableRouteHandler implements InterceptorHandler, InitializingBean {


    protected static final Logger logger = LoggerFactory.getLogger(TableRouteHandler.class);


    @Override
    public void afterPropertiesSet() throws Exception {
        // TODO Auto-generated method stub

    }

    @Override
    public Object onInterceptor(Invocation invocation) throws Throwable {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onFinished(Invocation invocation, Object result) {
        // TODO Auto-generated method stub

    }


    /* (non-Javadoc)
     * @see com.jeesuite.mybatis.core.InterceptorHandler#onStart()
     */
    @Override
    public void start(JeesuiteMybatisInterceptor context) {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see com.jeesuite.mybatis.core.InterceptorHandler#onClose()
     */
    @Override
    public void close() {
        // TODO Auto-generated method stub

    }

    @Override
    public int interceptorOrder() {
        // TODO Auto-generated method stub
        return 0;
    }
} 

