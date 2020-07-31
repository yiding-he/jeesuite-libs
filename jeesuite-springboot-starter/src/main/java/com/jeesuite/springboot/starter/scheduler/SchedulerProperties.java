/**
 *
 */
package com.jeesuite.springboot.starter.scheduler;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author <a href="mailto:vakinge@gmail.com">vakin</a>
 * @since 2016年12月31日
 */
@ConfigurationProperties(prefix = "jeesuite.task")
public class SchedulerProperties {


    private String registryType = "zookeeper";

    private String registryServers;

    private String groupName;

    private String scanPackages;

    private int threadPoolSize;

    public String getRegistryType() {
        return registryType;
    }

    public void setRegistryType(String registryType) {
        this.registryType = registryType;
    }

    public String getRegistryServers() {
        return registryServers;
    }

    public void setRegistryServers(String registryServers) {
        this.registryServers = registryServers;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getScanPackages() {
        return scanPackages;
    }

    public void setScanPackages(String scanPackages) {
        this.scanPackages = scanPackages;
    }

    public int getThreadPoolSize() {
        return threadPoolSize;
    }

    public void setThreadPoolSize(int threadPoolSize) {
        this.threadPoolSize = threadPoolSize;
    }


}
