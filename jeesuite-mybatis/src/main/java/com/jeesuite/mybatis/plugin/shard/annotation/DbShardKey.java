package com.jeesuite.mybatis.plugin.shard.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * 标识用于分库的字段
 *
 * @author <a href="mailto:vakinge@gmail.com">vakin</a>
 * @Copyright (c) 2015, jwww
 * @since 2016年1月31日
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface DbShardKey {

}
