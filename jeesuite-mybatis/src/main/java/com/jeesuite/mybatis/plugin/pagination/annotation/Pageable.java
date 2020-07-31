/**
 *
 */
package com.jeesuite.mybatis.plugin.pagination.annotation;

import java.lang.annotation.*;

/**
 * 分页标注
 * @author <a href="mailto:vakinge@gmail.com">vakin</a>
 * @since 2017年6月22日
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface Pageable {

}
