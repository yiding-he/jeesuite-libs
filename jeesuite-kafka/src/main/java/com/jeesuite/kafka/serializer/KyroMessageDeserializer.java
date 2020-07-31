package com.jeesuite.kafka.serializer;

import com.jeesuite.common.serializer.SerializeUtils;
import org.apache.kafka.common.serialization.Deserializer;

import java.util.Map;

/**
 * @author <a href="mailto:vakinge@gmail.com">vakin</a>
 * @since 2016年6月14日
 */
public class KyroMessageDeserializer implements Deserializer<Object> {

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
    }

    @Override
    public Object deserialize(String topic, byte[] data) {
        if (data == null)
            return null;
        else
            return SerializeUtils.deserialize(data);
    }

    @Override
    public void close() {
        // nothing to do
    }
}
