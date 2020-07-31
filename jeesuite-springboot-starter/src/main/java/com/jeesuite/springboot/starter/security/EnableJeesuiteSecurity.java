/**
 *
 */
package com.jeesuite.springboot.starter.security;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 启用 JeesuiteSecurity 安全组件
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(JeesuiteSecurityConfigurator.class)
public @interface EnableJeesuiteSecurity {

}
