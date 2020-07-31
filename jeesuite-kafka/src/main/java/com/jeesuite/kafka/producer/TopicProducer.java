package com.jeesuite.kafka.producer;

import com.jeesuite.kafka.message.DefaultMessage;
import com.jeesuite.kafka.producer.handler.ProducerEventHandler;

/**
 * @author <a href="mailto:vakinge@gmail.com">vakin</a>
 * @since 2016年6月14日
 */
public interface TopicProducer {

    void addEventHandler(ProducerEventHandler eventHandler);

    /**
     * 发送消息（可选择发送模式）
     *
     * @param asynSend 是否异步发送
     */
    boolean publish(final String topic, final DefaultMessage message, boolean asynSend);

    void close();
}
