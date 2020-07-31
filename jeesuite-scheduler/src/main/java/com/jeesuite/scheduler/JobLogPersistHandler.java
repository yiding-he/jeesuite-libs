/**
 *
 */
package com.jeesuite.scheduler;

import com.jeesuite.scheduler.model.JobConfig;

import java.util.Date;

/**
 * 任务运行日志持久化接口
 * @author <a href="mailto:vakinge@gmail.com">vakin</a>
 * @since 2016年12月30日
 */
public interface JobLogPersistHandler {

    public void onSucess(JobConfig conf, Date nextFireTime);

    public void onError(JobConfig conf, Date nextFireTime, Exception e);
}
