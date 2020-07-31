/**
 *
 */
package com.jeesuite.mybatis.core;

import java.io.Serializable;

/**
 * @author <a href="mailto:vakinge@gmail.com">vakin</a>
 * @since 2015年12月7日
 * @Copyright (c) 2015, jwww
 */
public abstract class BaseEntity implements Serializable {


    private static final long serialVersionUID = -607752621362896528L;

    public abstract Serializable getId();

}
