/**
 *
 */
package com.jeesuite.cache.command;

import com.jeesuite.cache.local.Level1CacheSupport;
import redis.clients.util.SafeEncoder;

import java.util.Date;

import static com.jeesuite.cache.redis.JedisProviderFactory.*;

/**
 * 对象redis操作对象（通过二进制序列化缓存）
 * @author <a href="mailto:vakinge@gmail.com">vakin</a>
 * @since 2015年12月7日
 */
public class RedisObject extends RedisBase {


    /**
     * @param key
     */
    public RedisObject(String key) {
        super(key);
    }

    /**
     * 指定组名
     * @param key
     * @param groupName
     */
    public RedisObject(String key, String groupName) {
        super(key, groupName, true);
    }

    /**
     * 重置key（适合一个方法里面频繁操作不同缓存的场景）<br>
     * <font color="red">非线程安全，请不要在多线程场景使用</font>
     * @param key
     * @return
     */
    public RedisObject resetKey(String key) {
        this.key = key;
        this.keyBytes = SafeEncoder.encode(key);
        return this;
    }

    /**
     * 设置缓存，默认过期时间
     * @param value
     * @return
     */
    public boolean set(Object value) {
        return set(value, RedisBase.getDefaultExpireSeconds());
    }

    /**
     * 设置缓存指定过期时间间隔
     * @param value
     * @param seconds (过期秒数 ，小于等于0时 不设置)
     * @return
     */
    public boolean set(Object value, long seconds) {

        if (value == null)
            return false;
        try {
            boolean result = false;
            if (isCluster(groupName)) {
                result = getBinaryJedisClusterCommands(groupName).set(keyBytes, valueSerialize(value)).equals(RESP_OK);
            } else {
                result = getBinaryJedisCommands(groupName).set(keyBytes, valueSerialize(value)).equals(RESP_OK);
            }
            if (result) {
                result = setExpire(seconds);
                //set可能是更新缓存，所以统一通知各节点清除本地缓存
                Level1CacheSupport.getInstance().publishSyncEvent(key);
            }
            return result;
        } finally {
            getJedisProvider(groupName).release();
        }

    }

    /**
     * 检查给定 key 是否存在。
     *
     * @param keyBytes
     * @return
     */
    public boolean set(Object value, Date expireAt) {
        if (value == null)
            return false;
        try {
            boolean result = false;
            if (isCluster(groupName)) {
                result = getBinaryJedisClusterCommands(groupName).set(keyBytes, valueSerialize(value)).equals(RESP_OK);
                ;
            } else {
                result = getBinaryJedisCommands(groupName).set(keyBytes, valueSerialize(value)).equals(RESP_OK);
            }
            if (result) {
                result = setExpireAt(expireAt);
                //set可能是更新缓存，所以统一通知各节点清除本地缓存
                Level1CacheSupport.getInstance().publishSyncEvent(key);
            }
            return result;
        } finally {
            getJedisProvider(groupName).release();
        }
    }


    public <T> T get() {
        try {
            //本地缓存读取
            T value = Level1CacheSupport.getInstance().get(this.key);
            if (value != null) return value;

            byte[] bytes = null;
            if (isCluster(groupName)) {
                bytes = getBinaryJedisClusterCommands(groupName).get(keyBytes);
            } else {
                bytes = getBinaryJedisCommands(groupName).get(keyBytes);
            }
            value = valueDerialize(bytes);
            //local
            Level1CacheSupport.getInstance().set(this.key, value);
            return value;
        } finally {
            getJedisProvider(groupName).release();
        }

    }

    @Override
    public boolean remove() {
        boolean removed = super.remove();
        //通知清除本地缓存
        if (removed) Level1CacheSupport.getInstance().publishSyncEvent(key);
        return removed;
    }


}
