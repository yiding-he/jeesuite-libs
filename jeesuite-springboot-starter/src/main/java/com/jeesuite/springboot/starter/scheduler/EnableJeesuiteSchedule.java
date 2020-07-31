/**
 *
 */
package com.jeesuite.springboot.starter.scheduler;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author <a href="mailto:vakinge@gmail.com">vakin</a>
 * @since 2017年3月28日
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(DelegateScheConfiguration.class)
public @interface EnableJeesuiteSchedule {

}
