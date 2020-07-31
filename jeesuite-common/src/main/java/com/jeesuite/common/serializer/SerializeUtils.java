package com.jeesuite.common.serializer;

import java.io.IOException;

/**
 * @author <a href="mailto:vakinge@gmail.com">vakin</a>
 * @since 2016年12月28日
 */
public class SerializeUtils {

    static KryoPoolSerializer serializer = new KryoPoolSerializer();

    /**
     * 序列化
     *
     * @param object 需要序列化的对象
     */
    public static byte[] serialize(Object object) {
        try {
            return serializer.serialize(object);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 反序列化
     *
     * @param bytes 需要被反序列化的数据
     */
    public static Object deserialize(byte[] bytes) {
        try {
            return serializer.deserialize(bytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
