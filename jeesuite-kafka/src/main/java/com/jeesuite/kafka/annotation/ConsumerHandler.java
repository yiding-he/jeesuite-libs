/**
 *
 */
package com.jeesuite.kafka.annotation;

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
public @interface ConsumerHandler {

    String topic();

}
