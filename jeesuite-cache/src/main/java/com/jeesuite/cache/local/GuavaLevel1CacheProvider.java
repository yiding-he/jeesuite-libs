/**
 *
 */
package com.jeesuite.cache.local;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * 本地缓存服务
 * @author <a href="mailto:vakinge@gmail.com">vakin</a>
 * @since 2016年6月1日
 */
public class GuavaLevel1CacheProvider implements Level1CacheProvider {

    private static final String _NULL = "NULL";

    private static final Logger logger = LoggerFactory.getLogger(GuavaLevel1CacheProvider.class);

    private Map<String, Cache<String, Object>> caches = new ConcurrentHashMap<String, Cache<String, Object>>();

    private int maxSize = 10000;

    private int timeToLiveSeconds = 600;

    public void setMaxSize(int maxSize) {
        this.maxSize = maxSize;
    }

    public void setTimeToLiveSeconds(int timeToLiveSeconds) {
        this.timeToLiveSeconds = timeToLiveSeconds;
    }

    @Override
    public void start() {

    }

    public boolean set(String cacheName, String key, Object value) {
        if (value == null) return true;
        Cache<String, Object> cache = getCacheHolder(cacheName);
        if (cache != null) {
            cache.put(key, value);
        }
        return true;
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String cacheName, String key) {
        try {
            Cache<String, Object> cache = getCacheHolder(cacheName);
            if (cache != null) {
                Object result = cache.get(key, new Callable<Object>() {
                    @Override
                    public Object call() throws Exception {
                        return _NULL;
                    }
                });
                if (result != null && !_NULL.equals(result)) {
                    return (T) result;
                }
            }
        } catch (Exception e) {
            logger.warn("get LEVEL1 cache error", e);
        }
        return null;
    }

    public void remove(String cacheName, String key) {
        Cache<String, Object> cache = getCacheHolder(cacheName);
        if (cache != null) {
            cache.invalidateAll();
        }
    }

    private Cache<String, Object> getCacheHolder(String cacheName) {
        return getAndNotexistsCreateCache(cacheName);
    }

    private Cache<String, Object> getAndNotexistsCreateCache(String cacheName) {
        Cache<String, Object> cache = caches.get(cacheName);
        if (cache != null) return cache;
        synchronized (caches) {
            if ((cache = caches.get(cacheName)) != null) return cache;
            cache = CacheBuilder
                    .newBuilder()
                    .maximumSize(maxSize)
                    .expireAfterWrite(timeToLiveSeconds, TimeUnit.SECONDS)
                    .build();
            caches.put(cacheName, cache);
        }
        return cache;
    }

    public void remove(String cacheName) {

    }

    public void clearAll() {
        for (Cache<String, Object> cache : caches.values()) {
            cache.invalidateAll();
        }
    }


    @Override
    public void close() throws IOException {
    }


}
