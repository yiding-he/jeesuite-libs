package com.jeesuite.kafka.serializer;

import com.jeesuite.common.serializer.SerializeUtils;
import kafka.serializer.Decoder;
import org.apache.kafka.common.serialization.Deserializer;


/**
 * @author <a href="mailto:vakinge@gmail.com">vakin</a>
 * @since 2016年6月14日
 */
public class MessageDecoder implements Decoder<Object> {

    private Deserializer<Object> deserializer;

    public MessageDecoder() {
    }

    public MessageDecoder(Deserializer<Object> deserializer) {
        super();
        this.deserializer = deserializer;
    }

    @Override
    public Object fromBytes(byte[] bytes) {
        if (deserializer != null) return deserializer.deserialize(null, bytes);
        return SerializeUtils.deserialize(bytes);
    }
}
