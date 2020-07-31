package com.jeesuite.kafka.serializer;

import com.jeesuite.common.serializer.SerializeUtils;
import org.apache.kafka.common.serialization.Serializer;

import java.io.Serializable;
import java.util.Map;


/**
 * @author <a href="mailto:vakinge@gmail.com">vakin</a>
 * @since 2016年6月14日
 */
public class KyroMessageSerializer implements Serializer<Serializable> {

    /**
     * Configure this class.
     *
     * @param configs configs in key/value pairs
     * @param isKey   whether is for key or value
     */
    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {

    }

    /**
     * serialize
     *
     * @param topic topic associated with data
     * @param data  typed data
     *
     * @return serialized bytes
     */
    @Override
    public byte[] serialize(String topic, Serializable data) {
        return SerializeUtils.serialize(data);
    }

    /**
     * Close this serializer
     */
    @Override
    public void close() {

    }
}
