package com.jeesuite.springboot.starter.security;

import com.jeesuite.security.SecurityConfiguration;
import com.jeesuite.security.SecurityDelegatingFilter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass(SecurityDelegatingFilter.class)
@EnableConfigurationProperties(SecurityConfiguration.class)
public class JeesuiteSecurityConfigurator {

    @Bean
    public FilterRegistrationBean<SecurityDelegatingFilter> securityFilterRegistrationBean(SecurityConfiguration conf) {
        FilterRegistrationBean<SecurityDelegatingFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new SecurityDelegatingFilter(conf));
        return registrationBean;
    }
}
