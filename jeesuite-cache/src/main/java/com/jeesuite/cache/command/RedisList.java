/**
 *
 */
package com.jeesuite.cache.command;

import java.util.List;

import static com.jeesuite.cache.redis.JedisProviderFactory.*;

/**
 * redis操作List
 *
 * @author <a href="mailto:vakinge@gmail.com">vakin</a>
 * @since 2015年12月7日
 */
public class RedisList extends RedisBinaryCollection {

    public RedisList(String key) {
        super(key);
    }

    /**
     * @param key
     * @param expireTime 超时时间(秒) 小于等于0 为永久缓存
     */
    public RedisList(String key, long expireTime) {
        super(key, expireTime);
    }

    /**
     * 指定组名
     *
     * @param key
     * @param groupName
     */
    public RedisList(String key, String groupName) {
        super(key, groupName);
    }

    /**
     *
     * @param key
     * @param groupName 分组名
     * @param expireTime 超时时间(秒) 小于等于0 为永久缓存
     */
    public RedisList(String key, String groupName, long expireTime) {
        super(key, groupName, expireTime);
    }

    public long lpush(Object... objects) {
        try {
            long result = 0;
            byte[][] datas = valuesSerialize(objects);
            if (isCluster(groupName)) {
                result = getBinaryJedisClusterCommands(groupName).lpush(keyBytes, datas);
            } else {
                result = getBinaryJedisCommands(groupName).lpush(keyBytes, datas);
            }
            //设置超时时间
            if (result > 0) setExpireIfNot(expireTime);
            return result;
        } finally {
            getJedisProvider(groupName).release();
        }
    }

    public long rpush(Object... objects) {
        try {
            byte[][] datas = valuesSerialize(objects);
            long result = 0;
            if (isCluster(groupName)) {
                result = getBinaryJedisClusterCommands(groupName).rpush(keyBytes, datas);
            } else {
                result = getBinaryJedisCommands(groupName).rpush(keyBytes, datas);
            }
            //设置超时时间
            if (result > 0) setExpireIfNot(expireTime);
            return result;
        } finally {
            getJedisProvider(groupName).release();
        }
    }

    public <T> T lpop() {
        byte[] datas = null;
        try {
            if (isCluster(groupName)) {
                datas = getBinaryJedisClusterCommands(groupName).lpop(keyBytes);
            } else {
                datas = getBinaryJedisCommands(groupName).lpop(keyBytes);
            }
            return valueDerialize(datas);
        } finally {
            getJedisProvider(groupName).release();
        }
    }

    public <T> T rpop() {
        try {

            byte[] datas = null;
            if (isCluster(groupName)) {
                datas = getBinaryJedisClusterCommands(groupName).rpop(keyBytes);
            } else {
                datas = getBinaryJedisCommands(groupName).rpop(keyBytes);
            }
            return valueDerialize(datas);
        } finally {
            getJedisProvider(groupName).release();
        }
    }

    /**
     * 获取全部列表
     *
     * @return
     */
    public <T> List<T> get() {
        return range(0, -1);
    }

    public <T> List<T> range(int start, int end) {
        try {
            List<byte[]> result = null;
            if (isCluster(groupName)) {
                result = getBinaryJedisClusterCommands(groupName).lrange(keyBytes, start, end);
            } else {
                result = getBinaryJedisCommands(groupName).lrange(keyBytes, start, end);
            }
            return toObjectList(result);
        } finally {
            getJedisProvider(groupName).release();
        }
    }

    /**
     * 返回长度
     *
     * @return
     */
    public long length() {
        try {
            if (isCluster(groupName)) {
                return getBinaryJedisClusterCommands(groupName).llen(keyBytes);
            } else {
                return getBinaryJedisCommands(groupName).llen(keyBytes);
            }
        } finally {
            getJedisProvider(groupName).release();
        }
    }

    /**
     * 设置指定位置的值
     *
     * @param index
     * @param newValue
     * @return
     */
    public boolean set(long index, Object newValue) {
        try {
            boolean result = false;
            if (isCluster(groupName)) {
                result = getBinaryJedisClusterCommands(groupName).lset(keyBytes, index, valueSerialize(newValue))
                        .equals(RESP_OK);
            } else {
                result = getBinaryJedisCommands(groupName).lset(keyBytes, index, valueSerialize(newValue)).equals(RESP_OK);
            }
            return result;
        } finally {
            getJedisProvider(groupName).release();
        }
    }

    /**
     * 移除(所有)指定值元素
     * @param value
     * @return
     */
    public boolean removeValue(Object value) {
        try {
            boolean result = false;
            if (isCluster(groupName)) {
                result = getBinaryJedisClusterCommands(groupName).lrem(keyBytes, 0, valueSerialize(value)) >= 1;
            } else {
                result = getBinaryJedisCommands(groupName).lrem(keyBytes, 0, valueSerialize(value)) >= 1;
            }
            return result;
        } finally {
            getJedisProvider(groupName).release();
        }
    }
}
