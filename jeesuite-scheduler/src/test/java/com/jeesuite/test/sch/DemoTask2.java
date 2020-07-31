/**
 *
 */
package com.jeesuite.test.sch;

import com.jeesuite.common.util.DateUtils;
import com.jeesuite.scheduler.AbstractJob;
import com.jeesuite.scheduler.JobContext;
import org.apache.commons.lang3.RandomUtils;

import java.util.Date;

/**
 * @author <a href="mailto:wei.jiang@lifesense.com">vakin</a>
 * @since 2016年1月28日
 * @Copyright (c) 2015, lifesense.com
 */
public class DemoTask2 extends AbstractJob {

    @Override
    public void doJob(JobContext context) throws Exception {
        System.out.println("\n=============\nDemoTask_2=====>" + context.getNodeId() + "--" + DateUtils.format(new Date()) + "\n===============\n");
        Thread.sleep(RandomUtils.nextLong(1000, 2000));
    }

    @Override
    public boolean parallelEnabled() {
        return false;
    }

}
