/**
 *
 */
package com.jeesuite.kafka.producer.handler;

import com.jeesuite.kafka.message.DefaultMessage;
import org.apache.kafka.clients.producer.RecordMetadata;

import java.io.Closeable;

/**
 * @author <a href="mailto:vakinge@gmail.com">vakin</a>
 * @since 2016年12月10日
 */
public interface ProducerEventHandler extends Closeable {

    public void onSuccessed(String topicName, RecordMetadata metadata);

    public void onError(String topicName, DefaultMessage message);

}
