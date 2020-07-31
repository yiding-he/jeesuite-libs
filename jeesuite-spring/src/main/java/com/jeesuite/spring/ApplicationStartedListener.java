package com.jeesuite.spring;

import org.springframework.context.ApplicationContext;

/**
 * 应用启动完成监听器接口
 *
 * @author <a href="mailto:vakinge@gmail.com">vakin</a>
 * @since 2017年11月20日
 */
public interface ApplicationStartedListener {

    void onApplicationStarted(ApplicationContext applicationContext);
}
