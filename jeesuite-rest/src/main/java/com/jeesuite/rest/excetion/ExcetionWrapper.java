/**
 *
 */
package com.jeesuite.rest.excetion;

import com.jeesuite.rest.response.WrapperResponseEntity;

/**
 * @author <a href="mailto:vakinge@gmail.com">vakin</a>
 * @since 2016年12月23日
 */
public interface ExcetionWrapper {

    WrapperResponseEntity toResponse(Exception e);
}
