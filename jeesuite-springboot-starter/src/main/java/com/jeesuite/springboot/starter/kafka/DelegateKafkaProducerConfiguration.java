/**
 *
 */
package com.jeesuite.springboot.starter.kafka;

import com.jeesuite.kafka.spring.TopicProducerSpringProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author <a href="mailto:vakinge@gmail.com">vakin</a>
 * @since 2017年3月28日
 */
@Configuration
@EnableConfigurationProperties(KafkaProducerProperties.class)
@ConditionalOnClass(TopicProducerSpringProvider.class)
@ConditionalOnProperty(name = "kafka.bootstrap.servers")
public class DelegateKafkaProducerConfiguration {

    @Autowired
    private KafkaProducerProperties producerProperties;

    @Bean
    public TopicProducerSpringProvider producerProvider() {

        TopicProducerSpringProvider bean = new TopicProducerSpringProvider();
        bean.setConfigs(producerProperties.getConfigs());
        bean.setDefaultAsynSend(producerProperties.isDefaultAsynSend());
        bean.setDelayRetries(producerProperties.getDelayRetries());
        bean.setProducerGroup(producerProperties.getProducerGroup());
        bean.setMonitorEnabled(producerProperties.isMonitorEnabled());
        bean.setConsumerAckEnabled(producerProperties.isConsumerAckEnabled());
        return bean;
    }


}
