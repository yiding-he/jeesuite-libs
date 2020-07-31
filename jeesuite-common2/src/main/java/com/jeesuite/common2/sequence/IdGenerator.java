/**
 *
 */
package com.jeesuite.common2.sequence;

/**
 * @author <a href="mailto:vakinge@gmail.com">vakin</a>
 * @since 2016年11月18日
 */
public interface IdGenerator {

    public long nextId();

    public void close();
}
