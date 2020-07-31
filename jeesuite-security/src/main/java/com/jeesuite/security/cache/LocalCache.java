package com.jeesuite.security.cache;

import com.google.common.cache.CacheBuilder;
import com.jeesuite.security.Cache;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class LocalCache implements Cache {

    private com.google.common.cache.Cache<String, Object> cache;

    public LocalCache(int timeToLiveSeconds) {
        cache = CacheBuilder
                .newBuilder()
                .maximumSize(10000)
                .expireAfterWrite(timeToLiveSeconds, TimeUnit.SECONDS)
                .build();
    }

    @Override
    public void setString(String key, String value) {
        cache.put(key, value);
    }

    @Override
    public String getString(String key) {
        return Objects.toString(getObject(key), null);
    }

    @Override
    public void setObject(String key, Object value) {
        cache.put(key, value);
    }

    @Override
    public <T> T getObject(String key) {
        return (T) cache.getIfPresent(key);
    }

    @Override
    public void remove(String key) {
        cache.invalidate(key);
    }

    @Override
    public boolean exists(String key) {
        return cache.getIfPresent(key) != null;
    }

    @Override
    public void removeAll() {
        cache.invalidateAll();
    }

}
