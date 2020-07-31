/**
 *
 */
package com.jeesuite.common2.excel;

import java.util.List;

/**
 * @author <a href="mailto:vakinge@gmail.com">vakin</a>
 * @since 2017年1月18日
 */
public interface ResultProcessor<T> {

    int batchSize();

    void process(List<T> datas);

}
