/**
 *
 */
package com.jeesuite.springboot.starter.kafka;

import com.jeesuite.common.util.ResourceUtils;
import com.jeesuite.spring.InstanceFactory;
import com.jeesuite.spring.SpringInstanceProvider;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Properties;

/**
 * @author <a href="mailto:vakinge@gmail.com">vakin</a>
 * @since 2016年12月31日
 */
@ConfigurationProperties(prefix = "jeesuite.kafka.consumer")
public class KafkaConsumerProperties implements InitializingBean, ApplicationContextAware {

    private boolean independent;

    private boolean useNewAPI = true;

    private int processThreads = 100;

    private String scanPackages;

    private Properties configs = new Properties();

    public boolean isIndependent() {
        return independent;
    }

    public void setIndependent(boolean independent) {
        this.independent = independent;
    }

    public boolean isUseNewAPI() {
        return useNewAPI;
    }

    public void setUseNewAPI(boolean useNewAPI) {
        this.useNewAPI = useNewAPI;
    }

    public int getProcessThreads() {
        return processThreads;
    }

    public void setProcessThreads(int processThreads) {
        this.processThreads = processThreads;
    }

    public String getScanPackages() {
        return scanPackages;
    }

    public void setScanPackages(String scanPackages) {
        this.scanPackages = scanPackages;
    }

    public Properties getConfigs() {
        return configs;
    }

    public void setConfigs(Properties configs) {
        this.configs = configs;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        String kafkaServers = ResourceUtils.getProperty("kafka.bootstrap.servers");
        String zkServers = ResourceUtils.getProperty("kafka.zkServers");
        configs.put("bootstrap.servers", kafkaServers);
        if (useNewAPI == false && zkServers != null) {
            configs.put("zookeeper.connect", zkServers);
        }
        Properties properties = ResourceUtils.getAllProperties("kafka.consumer.");
        Iterator<Entry<Object, Object>> iterator = properties.entrySet().iterator();
        while (iterator.hasNext()) {
            Entry<Object, Object> entry = iterator.next();
            configs.put(entry.getKey().toString().replace("kafka.consumer.", ""), entry.getValue());
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        InstanceFactory.setInstanceProvider(new SpringInstanceProvider(applicationContext));
    }

}
