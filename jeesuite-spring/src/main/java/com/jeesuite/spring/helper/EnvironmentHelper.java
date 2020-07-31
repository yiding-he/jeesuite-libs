package com.jeesuite.spring.helper;

import com.jeesuite.spring.InstanceFactory;
import org.springframework.core.env.*;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class EnvironmentHelper {

    private static Environment environment;


    public static synchronized void setEnvironment(Environment environment) {
        if (EnvironmentHelper.environment != null) {
            EnvironmentHelper.environment = environment;
        }
    }

    public static String getProperty(String key) {
        if (environment == null) init();
        return environment == null ? null : environment.getProperty(key);
    }

    private static synchronized void init() {
        if (!InstanceFactory.isInitialized()) {
            throw new NullPointerException();
        }
        if (environment == null) {
            synchronized (EnvironmentHelper.class) {
                environment = InstanceFactory.getInstance(Environment.class);
            }
        }
    }

    public static boolean containsProperty(String key) {
        if (environment == null) init();
        return environment == null ? false : environment.containsProperty(key);
    }


    public static Map<String, Object> getAllProperties(String prefix) {
        if (environment == null) init();
        MutablePropertySources propertySources = ((ConfigurableEnvironment) environment).getPropertySources();

        Map<String, Object> properties = new LinkedHashMap<String, Object>();
        for (PropertySource<?> source : propertySources) {
            if (source.getName().startsWith("servlet") || source.getName().startsWith("system")) {
                continue;
            }
            if (source instanceof EnumerablePropertySource) {
                for (String name : ((EnumerablePropertySource<?>) source).getPropertyNames()) {
                    boolean match = StringUtils.isEmpty(prefix);
                    if (!match) {
                        match = name.startsWith(prefix);
                    }
                    if (match) {
                        Object value = source.getProperty(name);
                        if (value != null) {
                            properties.put(name, value);
                        }
                    }
                }
            }
        }
        return Collections.unmodifiableMap(properties);
    }
}
