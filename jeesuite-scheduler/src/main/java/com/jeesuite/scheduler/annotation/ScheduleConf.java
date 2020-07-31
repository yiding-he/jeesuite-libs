/**
 *
 */
package com.jeesuite.scheduler.annotation;

import java.lang.annotation.*;

/**
 * @author <a href="mailto:vakinge@gmail.com">vakin</a>
 * @since 2015年12月10日
 * @Copyright (c) 2015, jwww
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface ScheduleConf {

    /**
     * 任务名称
     * @return
     */
    String jobName();

    /**
     * 重试次数
     * @return
     */
    int retries() default 0;

    String cronExpr();

    /**
     * 是否启动立即执行一次
     * @return
     */
    boolean executeOnStarted() default false;
}
