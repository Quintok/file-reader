package com.company.caching;

import com.company.ClassInfo;

import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.spi.CachingProvider;

public class ObjectReferenceCache {

    private static ObjectReferenceCache instance;
    private final Cache<Integer, ClassInfo> cache;

    private ObjectReferenceCache() {
        CachingProvider cachingProvider = Caching.getCachingProvider();
        CacheManager cacheManager = cachingProvider.getCacheManager();
        cache = cacheManager
                .getCache("objectreference", Integer.class, ClassInfo.class);
        instance = this;
    }

    public static ObjectReferenceCache get() {
        if (null != instance)
            return instance;
        return new ObjectReferenceCache();
    }

    public <T extends ClassInfo> T get(int objectId) {
        return (T) cache.get(objectId);
    }

    public <T extends ClassInfo> void put(int objectId, T object) {
        cache.put(objectId, object);
    }
}
