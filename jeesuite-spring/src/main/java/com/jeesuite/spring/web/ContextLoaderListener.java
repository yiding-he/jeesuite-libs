package com.jeesuite.spring.web;

import com.jeesuite.spring.ApplicationStartedListener;
import com.jeesuite.spring.InstanceFactory;
import com.jeesuite.spring.SpringInstanceProvider;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContextEvent;
import java.util.Map;


public class ContextLoaderListener extends org.springframework.web.context.ContextLoaderListener {

    @Override
    public void contextInitialized(ServletContextEvent event) {
        String serviceName = event.getServletContext().getInitParameter("appName");
        System.setProperty("serviceName", serviceName == null ? "undefined" : serviceName);
        super.contextInitialized(event);
        WebApplicationContext applicationContext = WebApplicationContextUtils.getRequiredWebApplicationContext(event.getServletContext());
        SpringInstanceProvider provider = new SpringInstanceProvider(applicationContext);
        InstanceFactory.loadFinished(provider);

        Map<String, ApplicationStartedListener> interfaces = applicationContext.getBeansOfType(ApplicationStartedListener.class);
        if (interfaces != null) {
            for (ApplicationStartedListener listener : interfaces.values()) {
                System.out.println(">>>begin to execute listener:" + listener.getClass().getName());
                listener.onApplicationStarted(applicationContext);
                System.out.println("<<<<finish execute listener:" + listener.getClass().getName());
            }
        }
    }
}
