package com.jeesuite.rest.filter.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ResponseFormat {

    /**
     * 默认需要事务
     */
    FormatType type();

    /**
     * JSON是否自动包装
     *
     * @author vakinge
     */
    boolean jsonWrapper() default true;

    public static enum FormatType {
        JSON, XML, NONE
    }
}
